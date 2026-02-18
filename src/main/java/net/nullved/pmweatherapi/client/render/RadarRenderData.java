package net.nullved.pmweatherapi.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.protomanly.pmweather.block.entity.RadarBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

/**
 * A wrapper class to be passed to {@link RadarRenderData}
 * @param blockEntity The {@link RadarBlockEntity} associated with the render call
 * @param sizeRenderDiameter The size in blocks of the radar
 * @param simSize The size of the radar
 * @param partialTicks The time, in partial ticks, since last full tick
 * @param poseStack The {@link PoseStack}
 * @param multiBufferSource The {@link MultiBufferSource}
 * @param combinedLightIn The current light value on the block entity
 * @param combinedOverlayIn The current overlay of the block entity
 * @since 0.14.15.2
 */
public record RadarRenderData(RadarBlockEntity blockEntity, float sizeRenderDiameter, float simSize, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
    /**
     * Helper method to get the {@link BlockPos} of the {@link RadarBlockEntity}
     * @return The radar's {@link BlockPos}
     * @since 0.16.1.0-rc2
     */
    public BlockPos radarPos() {
        return this.blockEntity.getBlockPos();
    }

    /**
     * Helper method to get the x-position of the {@link RadarBlockEntity}
     * @return The radar's x-position
     * @since 0.16.1.0-rc2
     */
    public int radarX() {
        return radarPos().getX();
    }

    /**
     * Helper method to get the z-position of the {@link RadarBlockEntity}
     * @return The radar's z-position
     * @since 0.16.1.0-rc2
     */
    public int radarZ() {
        return radarPos().getZ();
    }
}