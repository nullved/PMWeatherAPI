package net.nullved.pmweatherapi.client.render.radar;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class RadarRenderTypes {
    private static final HashMap<ResourceLocation, RenderType> OVERLAY_CACHE = new HashMap<>();
    public static final RenderType RADAR = RenderType.create(
            "radar",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );

    public static RenderType createForTexture(ResourceLocation resourceLocation) {
        return OVERLAY_CACHE.computeIfAbsent(resourceLocation, r-> RenderType.create(
                "radar_overlay_texture",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256,
                true,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                        .setTransparencyState(NO_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                        .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(RenderStateShard.NO_OVERLAY)
                        .setLayeringState(RenderStateShard.NO_LAYERING)
                        .createCompositeState(true)
            )
        );
    }

    public static RenderType doubleSided(ResourceLocation resourceLocation) {
        return OVERLAY_CACHE.computeIfAbsent(resourceLocation, r -> RenderType.create(
            "radar_overlay_texture_ds",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            true,
            true,
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setCullState(NO_CULL)
                .createCompositeState(true)
        ));
    }

    public static void flushOverlayCache(MultiBufferSource.BufferSource bufferSource) {
        OVERLAY_CACHE.values().forEach(bufferSource::endBatch);
        OVERLAY_CACHE.clear();
    }
}
