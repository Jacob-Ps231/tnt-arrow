package re.jerome.tntarrow.client;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import re.jerome.tntarrow.TntArrow;
import re.jerome.tntarrow.TntArrowMod;

// SpectralArrowRenderer with another texture.
public class TntArrowRenderer extends ArrowRenderer<TntArrow, ArrowRenderState> {
	private static final Identifier TEXTURE = TntArrowMod.id("textures/entity/projectiles/tnt_arrow.png");

	public TntArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected Identifier getTextureLocation(ArrowRenderState state) {
		return TEXTURE;
	}

	@Override
	public ArrowRenderState createRenderState() {
		return new ArrowRenderState();
	}
}
