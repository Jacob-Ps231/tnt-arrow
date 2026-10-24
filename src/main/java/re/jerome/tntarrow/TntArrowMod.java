package re.jerome.tntarrow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TntArrowMod implements ModInitializer {
	public static final String MOD_ID = "tntarrow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final ResourceKey<Item> ITEM_KEY = ResourceKey.create(Registries.ITEM, id("tnt_arrow"));
	public static final Item TNT_ARROW = Registry.register(BuiltInRegistries.ITEM, ITEM_KEY,
			new TntArrowItem(new Item.Properties().setId(ITEM_KEY)));

	private static final ResourceKey<EntityType<?>> ENTITY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, id("tnt_arrow"));
	// The builder chain of EntityTypes.ARROW (SPEC 7.4).
	public static final EntityType<TntArrow> TNT_ARROW_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE, ENTITY_KEY,
			EntityType.Builder.<TntArrow>of(TntArrow::new, MobCategory.MISC)
					.noLootTable()
					.sized(0.5F, 0.5F)
					.eyeHeight(0.13F)
					.clientTrackingRange(4)
					.updateInterval(20)
					.build(ENTITY_KEY));

	// Private in the CreativeModeTabs of the jar (Fabric API widens it at build
	// time): rebuilt from its id, which does not depend on that.
	private static final ResourceKey<CreativeModeTab> COMBAT =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("combat"));

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		TntArrowConfig.load();
		// Vanilla registers each dispensable arrow by hand, none through the tag.
		DispenserBlock.registerProjectileBehavior(TNT_ARROW);
		CreativeModeTabEvents.modifyOutputEvent(COMBAT).register(output -> output.insertAfter(Items.SPECTRAL_ARROW, TNT_ARROW));
		LOGGER.info("TNT Arrow loaded");
	}
}
