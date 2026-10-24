package re.jerome.tntarrow;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TntArrowMod implements ModInitializer {
	public static final String MOD_ID = "tntarrow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		TntArrowConfig.load();
		LOGGER.info("TNT Arrow loaded");
	}
}
