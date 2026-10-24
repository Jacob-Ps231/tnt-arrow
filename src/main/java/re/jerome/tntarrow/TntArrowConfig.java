package re.jerome.tntarrow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.StrictJsonParser;

// Gameplay values live here rather than scattered as constants (SPEC 6).
//
// Reading and writing use two codecs built from the same field list. The reading
// one uses optionalFieldOf so a partial file still loads; the writing one uses
// fieldOf, because optionalFieldOf omits a field equal to its default and would
// generate a file with nothing in it to edit.
//
// The file is written only when it is missing. A file the player edited is never
// rewritten, even when it is invalid: the mod then runs on the defaults and says
// so in the log, and the player's file stays as they left it.
public record TntArrowConfig(Explosion explosion) {
	public static final TntArrowConfig DEFAULTS = new TntArrowConfig(Explosion.DEFAULTS);

	private static final Codec<TntArrowConfig> READ_CODEC = codec(false);
	private static final Codec<TntArrowConfig> WRITE_CODEC = codec(true);

	private static Codec<TntArrowConfig> codec(boolean everyField) {
		return RecordCodecBuilder.create(
				i -> i.group(
								field(Explosion.codec(everyField), "explosion", Explosion.DEFAULTS, everyField)
										.forGetter(TntArrowConfig::explosion))
						.apply(i, TntArrowConfig::new));
	}

	private static <T> MapCodec<T> field(Codec<T> codec, String name, T fallback, boolean everyField) {
		return everyField ? codec.fieldOf(name) : codec.optionalFieldOf(name, fallback);
	}

	// JSON has no comments, so the file carries its own explanation as a key the
	// reading codec ignores.
	private static final String[] COMMENT = {
		"TNT Arrow -- configuration. Read at startup: restart the game to apply a change.",
		"Only the server's copy matters: it is the server that makes the arrow explode.",
		"Delete this file to regenerate it with the default values.",
		"If a value is invalid, the whole file is ignored, the defaults are used and",
		"the log says why. This file is never overwritten.",
		"",
		"explosion.power: the blast power of the arrow. 4.0 is a block of TNT.",
		"A decimal number between 0.5 and 10.",
	};

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private static TntArrowConfig current = DEFAULTS;

	public static TntArrowConfig get() {
		return current;
	}

	/** Reads the file, or writes the defaults to it when it is missing. */
	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve(TntArrowMod.MOD_ID + ".json");

		if (Files.exists(path)) {
			current = read(path);
		} else {
			current = DEFAULTS;
			write(path);
		}

		TntArrowMod.LOGGER.info("Config {}: explosion power {}", path, current.explosion.power);
	}

	private static TntArrowConfig read(Path path) {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			DataResult<TntArrowConfig> result = READ_CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(reader));
			// result() rather than resultOrPartial(): a partial result would mix the
			// player's values with defaults the log does not name.
			result.error().ifPresent(error ->
					TntArrowMod.LOGGER.warn("Invalid config {}, using defaults: {}", path, error.message()));
			return result.result().orElse(DEFAULTS);
		} catch (IOException | JsonParseException e) {
			TntArrowMod.LOGGER.warn("Unreadable config {}, using defaults", path, e);
			return DEFAULTS;
		}
	}

	private static void write(Path path) {
		WRITE_CODEC.encodeStart(JsonOps.INSTANCE, DEFAULTS)
				.resultOrPartial(error -> TntArrowMod.LOGGER.warn("Could not encode config: {}", error))
				.ifPresent(encoded -> {
					JsonObject out = new JsonObject();
					out.add("_comment", GSON.toJsonTree(COMMENT));
					encoded.getAsJsonObject().entrySet().forEach(entry -> out.add(entry.getKey(), entry.getValue()));

					try {
						Files.createDirectories(path.getParent());
						Files.writeString(path, GSON.toJson(out));
					} catch (IOException e) {
						TntArrowMod.LOGGER.warn("Could not write config {}", path, e);
					}
				});
	}

	/** Blast power of the arrow, 4.0 being a block of TNT (SPEC 2.3). */
	public record Explosion(float power) {
		public static final Explosion DEFAULTS = new Explosion(4.0F);

		static Codec<Explosion> codec(boolean everyField) {
			return RecordCodecBuilder.create(
					i -> i.group(
									field(Codec.floatRange(0.5F, 10.0F), "power", DEFAULTS.power, everyField)
											.forGetter(Explosion::power))
							.apply(i, Explosion::new));
		}
	}
}
