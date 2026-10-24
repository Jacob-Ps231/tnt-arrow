package re.jerome.tntarrow.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import re.jerome.tntarrow.TntArrowMod;

public class TntArrowClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// EntityRenderers keeps its register private; Fabric's registry is
		// deprecated without a replacement, and still the only way in.
		EntityRendererRegistry.register(TntArrowMod.TNT_ARROW_ENTITY, TntArrowRenderer::new);
	}
}
