import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;

/**
 * Generates the two textures of the TNT arrow (SPEC 3): the vanilla arrow, item
 * and entity, with its grey head recoloured in the reds of tnt_side.png and
 * crossed by a white band, like the label of a block of TNT. Then reads both
 * files back and checks their size and content.
 *
 * Usage, from the project root:
 *   java tools/GenerateTextures.java C:/gradle-home/caches/fabric-loom/26.3/minecraft-merged.jar
 */
public class GenerateTextures {
	static final Path TEXTURES = Path.of("src/main/resources/assets/tntarrow/textures");
	static final String VANILLA = "assets/minecraft/textures/";

	// From tnt_side.png: its four reds, lightest first, and the whites of its label.
	static final int[] REDS = {0xFFEA4318, 0xFFDB2F1A, 0xFFB11527, 0xFF912D11};
	static final int[] WHITES = {0xFFFFFFFF, 0xFFDDD9D9, 0xFFCECECE};

	/** Where the head is, and which of its pixels form the white band. */
	record Head(int x0, int y0, int x1, int y1, BiPredicate<Integer, Integer> band) {
		boolean contains(int x, int y) {
			return x >= x0 && x <= x1 && y >= y0 && y <= y1;
		}
	}

	// Item, 16x16: the head is at the top right, pointing up and right. The band
	// runs across it, perpendicular to the shaft: the diagonal x - y = 8.
	static final Head ITEM_HEAD = new Head(10, 2, 15, 6, (x, y) -> x - y == 8);
	// Entity, 32x32: rows 0-4 are the arrow seen from the side, drawn twice
	// mirrored (x 0-15 and 16-31), the head in the middle at x 12-19. The band is
	// one column on each copy.
	static final Head ENTITY_HEAD = new Head(12, 0, 19, 4, (x, y) -> x == 14 || x == 17);

	public static void main(String[] args) throws IOException {
		Path item = TEXTURES.resolve("item/tnt_arrow.png");
		Path entity = TEXTURES.resolve("entity/projectiles/tnt_arrow.png");
		BufferedImage itemImage;
		BufferedImage entityImage;
		try (ZipFile jar = new ZipFile(args[0])) {
			itemImage = recolour(read(jar, VANILLA + "item/arrow.png"), ITEM_HEAD);
			entityImage = recolour(read(jar, VANILLA + "entity/projectiles/arrow.png"), ENTITY_HEAD);
		}
		write(item, itemImage);
		write(entity, entityImage);
		check(item, itemImage, 16);
		check(entity, entityImage, 32);
		System.out.println("2 textures written and read back");
	}

	static BufferedImage recolour(BufferedImage source, Head head) {
		BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int changed = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int argb = source.getRGB(x, y);
				if (head.contains(x, y) && isGrey(argb)) {
					int light = argb & 0xFF;
					argb = head.band().test(x, y) ? shade(WHITES, light) : shade(REDS, light);
					changed++;
				}
				image.setRGB(x, y, argb);
			}
		}
		if (changed == 0) {
			throw new IllegalStateException("No grey head pixel found: has the vanilla texture changed?");
		}
		return image;
	}

	/** An opaque grey: the metal of the head. The shaft is brown, the fletching outside the head box. */
	static boolean isGrey(int argb) {
		int r = argb >> 16 & 0xFF;
		int g = argb >> 8 & 0xFF;
		int b = argb & 0xFF;
		return argb >>> 24 == 0xFF && r == g && g == b;
	}

	/** The palette colour matching a grey's lightness, the palette going from light to dark. */
	static int shade(int[] palette, int light) {
		int index = (255 - light) * palette.length / 256;
		return palette[index];
	}

	static BufferedImage read(ZipFile jar, String name) throws IOException {
		try (InputStream in = jar.getInputStream(jar.getEntry(name))) {
			return ImageIO.read(in);
		}
	}

	static void write(Path path, BufferedImage image) throws IOException {
		Files.createDirectories(path.getParent());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);
		Files.write(path, out.toByteArray());
	}

	static void check(Path path, BufferedImage expected, int size) throws IOException {
		BufferedImage written = ImageIO.read(path.toFile());
		if (written == null || written.getWidth() != size || written.getHeight() != size) {
			throw new IllegalStateException(path + ": not a " + size + "x" + size + " image");
		}
		int[] want = expected.getRGB(0, 0, size, size, null, 0, size);
		int[] got = written.getRGB(0, 0, size, size, null, 0, size);
		if (!Arrays.equals(want, got)) {
			throw new IllegalStateException(path + ": pixels differ from what was written");
		}
	}
}
