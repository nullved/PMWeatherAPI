package net.nullved.pmweatherapi.client.render.radar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.nullved.pmweatherapi.client.render.RadarRenderData;
import net.nullved.pmweatherapi.data.PMWExtras;
import net.nullved.pmweatherapi.radar.RadarMode;

/**
 * An interface defining a radar overlay
 * To implement this, you must override {@link #render(boolean, RadarRenderData, Object...)} and {@link #getModID()}
 * @since 0.14.15.2
 */
public interface IRadarOverlay {
    /**
     * Renders objects on top of the radar
     * @param canRender {@code true} if either the server doesn't require WSR-88D or a WSR-88D is complete within 4 chunks of the radar
     * @param radarRenderData The data used to call {@link BlockEntityRenderer#render(BlockEntity, float, PoseStack, MultiBufferSource, int, int)}
     * @param args The arguments to pass to the Radar Overlay
     * @since 0.14.15.2
     */
    void render(boolean canRender, RadarRenderData radarRenderData, Object... args);

    /**
     * Get the {@link RadarMode} of the radar
     * @param radarRenderData The {@link RadarRenderData}
     * @return The radar's {@link RadarMode}
     * @since 0.14.16.2
     */
    default RadarMode getRadarMode(RadarRenderData radarRenderData) {
        return radarRenderData.blockEntity().getBlockState().getValue(PMWExtras.RADAR_MODE);
    }

    default void renderQuad(RadarRenderData radarRenderData, Vec3 q, float scale, PoseStack pose, int color, int cli) {
        renderQuad(radarRenderData, (float) q.x, (float) q.y, (float) q.z, scale, pose, color, cli);
    }

    default void renderQuad(RadarRenderData radarRenderData, float qx, float qy, float qz, float scale, PoseStack poseStack, int color, int cli) {
        VertexConsumer vc = radarRenderData.multiBufferSource().getBuffer(RadarRenderTypes.RADAR);

        qx *= scale;
        qz *= scale;

        PoseStack.Pose pose = poseStack.last();
        vc.addVertex(pose.pose(), -scale + qx, qy, -scale + qz)
            .setColor(color)
            .setLight(cli);
        vc.addVertex(pose.pose(), -scale + qx, qy, scale + qz)
            .setColor(color)
            .setLight(cli);
        vc.addVertex(pose.pose(), scale + qx, qy, scale + qz)
            .setColor(color)
            .setLight(cli);
        vc.addVertex(pose.pose(), scale + qx, qy, -scale + qz)
            .setColor(color)
            .setLight(cli);
    }


    default void texVertex(VertexConsumer buffer, PoseStack.Pose pose, int color, float x, float y, float z, float u, float v, int overlay) {
        buffer.addVertex(pose, x, y, z)
            .setColor(color)
            .setUv(u, v)
            .setLight(0xF000F0)
            .setOverlay(overlay)
            .setNormal(pose, 0.0f, 1.0f, 0.0f);
    }

    /**
     * Render a texture at the given {@link ResourceLocation}
     * @param texture The {@link ResourceLocation} of the texture
     * @param radarRenderData The {@link RadarRenderData}
     * @param poseStack The {@link PoseStack} to render with
     * @param color The color
     * @since 0.15.3.3
     */
    default void renderTexture(ResourceLocation texture, RadarRenderData radarRenderData, PoseStack poseStack, int color) {
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = radarRenderData.multiBufferSource().getBuffer(RadarRenderTypes.doubleSided(texture));

        texVertex(consumer, pose, color, -0.5f, -0.5f, 0, 0, 0, radarRenderData.combinedOverlayIn());
        texVertex(consumer, pose, color, 0.5f, -0.5f, 0, 1, 0, radarRenderData.combinedOverlayIn());
        texVertex(consumer, pose, color, 0.5f, 0.5f, 0, 1, 1, radarRenderData.combinedOverlayIn());
        texVertex(consumer, pose, color, -0.5f, 0.5f, 0, 0, 1, radarRenderData.combinedOverlayIn());
    }

    /**
     * Renders a texture from the given {@link ResourceLocation} facing upwards.
     * Not guaranteed to work when other rotation transformations are present!
     *
     * @param texture The {@link ResourceLocation} of the texture
     * @param radarRenderData The {@link RadarRenderData}
     * @param poseStack The {@link PoseStack} to render with
     * @param color The color
     * @since 0.16.1.0-rc2
     */
    default void renderTextureUpwards(ResourceLocation texture, RadarRenderData radarRenderData, PoseStack poseStack, int color) {
        orientUpwards(poseStack);
        renderTexture(texture, radarRenderData, poseStack, color);
        unorientUpwards(poseStack);
    }

    /**
     * Render a texture at the given {@link ResourceLocation}
     * @param texture The {@link ResourceLocation} of the texture
     * @param radarRenderData The {@link RadarRenderData}
     * @param poseStack The {@link PoseStack} to render with
     * @since 0.15.3.3
     */
    default void renderTexture(ResourceLocation texture, RadarRenderData radarRenderData, PoseStack poseStack) {
        renderTexture(texture, radarRenderData, poseStack, 0xFFFFFFFF);
    }


    /**
     * Renders a texture from the given {@link ResourceLocation} facing upwards.
     * Not guaranteed to work when other rotation transformations are present!
     *
     * @param texture The {@link ResourceLocation} of the texture
     * @param radarRenderData The {@link RadarRenderData}
     * @param poseStack The {@link PoseStack} to render with
     * @since 0.16.1.0-rc2
     */
    default void renderTextureUpwards(ResourceLocation texture, RadarRenderData radarRenderData, PoseStack poseStack) {
        orientUpwards(poseStack);
        renderTexture(texture, radarRenderData, poseStack);
        unorientUpwards(poseStack);
    }

    /**
     * Render a texture at the given {@link ResourceLocation}
     * @param texture The {@link ResourceLocation} of the texture
     * @param radarRenderData The {@link RadarRenderData}
     * @param color The color
     * @since 0.15.3.3
     */
    default void renderTexture(ResourceLocation texture, RadarRenderData radarRenderData, int color) {
        renderTexture(texture, radarRenderData, radarRenderData.poseStack(), color);
    }


    /**
     * Renders a texture from the given {@link ResourceLocation} facing upwards.
     * Not guaranteed to work when other rotation transformations are present!
     *
     * @param texture The {@link ResourceLocation} of the texture
     * @param radarRenderData The {@link RadarRenderData}
     * @param color The color
     * @since 0.16.1.0-rc2
     */
    default void renderTextureUpwards(ResourceLocation texture, RadarRenderData radarRenderData, int color) {
        orientUpwards(radarRenderData.poseStack());
        renderTexture(texture, radarRenderData, color);
        unorientUpwards(radarRenderData.poseStack());
    }

    /**
     * Render a texture at the given {@link ResourceLocation}
     * @param texture The {@link ResourceLocation} of the texture
     * @param radarRenderData The {@link RadarRenderData}
     * @since 0.15.3.3
     */
    default void renderTexture(ResourceLocation texture, RadarRenderData radarRenderData) {
        renderTexture(texture, radarRenderData, radarRenderData.poseStack(), 0xFFFFFFFF);
    }


    /**
     * Renders a texture from the given {@link ResourceLocation} facing upwards.
     * Not guaranteed to work when other rotation transformations are present!
     *
     * @param texture The {@link ResourceLocation} of the texture
     * @param radarRenderData The {@link RadarRenderData}
     * @since 0.16.1.0-rc2
     */
    default void renderTextureUpwards(ResourceLocation texture, RadarRenderData radarRenderData) {
        orientUpwards(radarRenderData.poseStack());
        renderTexture(texture, radarRenderData);
        unorientUpwards(radarRenderData.poseStack());
    }

    /**
     * Render the text given in the given {@link Component} with a background color
     * @param component The {@link Component} to render
     * @param radarRenderData The {@link RadarRenderData}
     * @param poseStack The {@link PoseStack} to render with
     * @param backgroundColor The background color to render
     * @since 0.16.0.0
     */
    default void renderText(Component component, RadarRenderData radarRenderData, PoseStack poseStack, int backgroundColor) {
        Font font = Minecraft.getInstance().font;
        font.drawInBatch(component, 0, 0, 0xFFFFFF, false, poseStack.last().pose(), radarRenderData.multiBufferSource(), Font.DisplayMode.NORMAL, backgroundColor, 0xF000F0);
    }

    /**
     * Render the text given in the given {@link Component}
     * @param component The {@link Component} to render
     * @param radarRenderData The {@link RadarRenderData}
     * @param poseStack The {@link PoseStack} to render with
     * @since 0.14.16.2
     */
    default void renderText(Component component, RadarRenderData radarRenderData, PoseStack poseStack) {
        this.renderText(component, radarRenderData, poseStack, 0);
    }

    /**
     * Render the text given in the given {@link Component}
     * @param component The {@link Component} to render
     * @param radarRenderData The {@link RadarRenderData}
     * @since 0.14.16.2
     */
    default void renderText(Component component, RadarRenderData radarRenderData) {
        this.renderText(component, radarRenderData, radarRenderData.poseStack());
    }

    /**
     * @return The Mod ID of the mod that registered this overlay
     * @since 0.14.15.2
     */
    String getModID();

    /**
     * If not overriden, this method returns the class name of the implementor converted to snake case
     * @return The path to use for this overlay's ID
     * @since 0.14.15.2
     */
    default String getIDPath() {
        return this.getClass().getSimpleName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    /**
     * The ID of this overlay, defined by {@link #getModID()} and {@link #getIDPath()}
     * @return The {@link ResourceLocation} of this overlay
     * @since 0.14.15.2
     */
    default ResourceLocation getID() {
        // by default, returns
        return ResourceLocation.fromNamespaceAndPath(getModID(), getIDPath());
    }

    /* === UTILITY METHODS FOR POSES === */

    /**
     * Scales a pose by a uniform scale factor
     *
     * @param pose THe {@link PoseStack} to scale
     * @param scaleFactor The scale factor
     * @since 0.16.1.0-rc2
     */
    default void scale(PoseStack pose, float scaleFactor) {
        pose.scale(scaleFactor, scaleFactor, scaleFactor);
    }

    /**
     * Orients a {@link PoseStack} to face upwards.
     * May not work if other rotations are present!
     *
     * @param poseStack The {@link PoseStack} to orient upwards
     * @since 0.16.1.0-rc2
     */
    default void orientUpwards(PoseStack poseStack) {
        poseStack.mulPose(Axis.XN.rotationDegrees(90));
    }

    /**
     * Unorients a {@link PoseStack} that is facing upwards.
     * May not work if other rotations are present!
     *
     * @param poseStack The {@link PoseStack} to unorient
     * @since 0.16.1.0-rc2
     */
    default void unorientUpwards(PoseStack poseStack) {
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
    }

    /**
     * Flips a {@link PoseStack} on the specified {@link Axis}.
     *
     * @param poseStack The {@link PoseStack} to flip
     * @param axis The {@link Axis} to flip around
     * @since 0.16.1.0-rc2
     */
    default void flipPose(PoseStack poseStack, Axis axis) {
        poseStack.mulPose(axis.rotationDegrees(180));
    }

    /**
     * Goes to the given {@link Vec3} relative to the radar's position.
     * Uses {@link #poseToRadarSpace(PoseStack, RadarRenderData)} and {@link #poseToWorldSpace(PoseStack, RadarRenderData)}
     *
     * @param location The {@link Vec3} of the absolute location
     * @param poseStack The {@link PoseStack}
     * @param rrd The {@link RadarRenderData}
     * @since 0.16.1.0-rc2
     */
    default void placeOnRadar(Vec3 location, PoseStack poseStack, RadarRenderData rrd) {
        double dx = location.x - (rrd.radarX() + 0.5D);
        double dz = location.z - (rrd.radarZ() + 0.5D);

        float ratio = (rrd.sizeRenderDiameter() / 2.0F) / rrd.simSize();
        poseStack.translate(dx * ratio, 0.01f, dz * ratio);
//
//        poseToRadarSpace(poseStack, rrd);
//        poseStack.translate(location.x, 0, location.z);
//        poseToWorldSpace(poseStack, rrd);
    }

    /**
     * Transforms and scales a pose so that 1 block in world space == 1 pixel in radar space.
     *
     * @param poseStack The {@link PoseStack} to transform
     * @param rrd The {@link RadarRenderData}
     * @since 0.16.1.0-rc2
     */
    default void poseToRadarSpace(PoseStack poseStack, RadarRenderData rrd) {
        float wtrRatio = (rrd.sizeRenderDiameter() / 2.0F) / rrd.simSize();

        poseStack.translate(0, 0.055f, 0);
        poseStack.scale(wtrRatio, 1, wtrRatio);
        poseStack.translate(-rrd.radarX() - 0.5f, 0f, -rrd.radarZ() - 0.5f);
    }

    /**
     * Translates a {@link Vec3} of absolute coordinates to a {@link Vec3} of radar coordinates
     *
     * @param pos The {@link Vec3} of absolute coordinates
     * @param rrd The {@link RadarRenderData}
     * @return The {@link Vec3} of radar coordinates
     * @since 0.16.1.0-rc2
     */
    default Vec3 worldToRadarCoords(Vec3 pos, RadarRenderData rrd) {
        return worldToRadarCoords(pos, rrd.blockEntity().getBlockPos(), rrd.sizeRenderDiameter(), rrd.simSize());
    }

    /**
     * Translates a {@link Vec3} of absolute coordinates to a {@link Vec3} of radar coordinates
     *
     * @param pos The {@link Vec3} of absolute coordinates
     * @param radarPos The {@link BlockPos} of the radar
     * @param sizeRenderDiameter The diameter of the radar size. Is 3 for small radars and 6 for big (unless 3x3 option is on)
     * @param simSize The simulation size of the radar. Is 2048 for small radars and 8192 for big radars
     * @return The {@link Vec3} of radar coordinates
     * @since 0.16.1.0-rc2
     */
    default Vec3 worldToRadarCoords(Vec3 pos, BlockPos radarPos, float sizeRenderDiameter, float simSize) {
        return worldToRadarCoords(pos.x, pos.z, radarPos, sizeRenderDiameter, simSize);
    }

    /**
     * Translates absolute coordinates to a {@link Vec3} of radar coordinates
     *
     * @param x The x-coordinate of the absolute position
     * @param z The z-coordinate of the absolute position
     * @param rrd The {@link RadarRenderData}
     * @return The {@link Vec3} of radar coordinates
     * @since 0.16.1.0-rc2
     */
    default Vec3 worldToRadarCoords(double x, double z, RadarRenderData rrd) {
        return worldToRadarCoords(x, z, rrd.blockEntity().getBlockPos(), rrd.sizeRenderDiameter(), rrd.simSize());
    }

    /**
     * Translates a {@link Vec3} of absolute coordinates to a {@link Vec3} of radar coordinates
     *
     * @param x The x-coordinate of the absolute position
     * @param z The z-coordinate of the absolute position
     * @param sizeRenderDiameter The diameter of the radar size. Is 3 for small radars and 6 for big (unless 3x3 option is on)
     * @param simSize The simulation size of the radar. Is 2048 for small radars and 8192 for big radars
     * @return The {@link Vec3} of radar coordinates
     * @since 0.16.1.0-rc2
     */
    default Vec3 worldToRadarCoords(double x, double z, BlockPos radarPos, float sizeRenderDiameter, float simSize) {
        double wtrRatio = (sizeRenderDiameter / 2.0F) / simSize;

        double centerX = radarPos.getX() + 0.5D;
        double centerZ = radarPos.getZ() + 0.5D;

        double dx = x - centerX;
        double dz = z - centerZ;

        return new Vec3(dx * wtrRatio, 0, dz * wtrRatio);
    }

    /**
     * Untransforms and scales a pose where 1 block in world space == 1 pixel in radar space.
     *
     * @param poseStack The {@link PoseStack} to transform
     * @param rrd The {@link RadarRenderData}
     * @since 0.16.1.0-rc2
     */
    default void poseToWorldSpace(PoseStack poseStack, RadarRenderData rrd) {
        float rtwRatio =  rrd.simSize() / (rrd.sizeRenderDiameter() / 2.0F);

        poseStack.translate(rrd.radarX() + 0.5f, 0.0f, rrd.radarZ() + 0.5f);
        poseStack.scale(rtwRatio, 1, rtwRatio);
        poseStack.translate(0, -0.05f, 0);
    }

    /**
     * Translates a {@link Vec3} of radar coordinates to a {@link Vec3} of absolute coordinates
     *
     * @param pos The {@link Vec3} of radar coordinates
     * @param rrd The {@link RadarRenderData}
     * @return The {@link Vec3} of radar coordinates
     * @since 0.16.1.0-rc2
     */
    default Vec3 radarToWorldCoords(Vec3 pos, RadarRenderData rrd) {
        return radarToWorldCoords(pos, rrd.blockEntity().getBlockPos(), rrd.sizeRenderDiameter(), rrd.simSize());
    }

    /**
     * Translates a {@link Vec3} of radar coordinates to a {@link Vec3} of absolute coordinates
     *
     * @param pos The {@link Vec3} of radar coordinates
     * @param sizeRenderDiameter The diameter of the radar size. Is 3 for small radars and 6 for big (unless 3x3 option is on)
     * @param simSize The simulation size of the radar. Is 2048 for small radars and 8192 for big radars
     * @return The {@link Vec3} of radar coordinates
     * @since 0.16.1.0-rc2
     */
    default Vec3 radarToWorldCoords(Vec3 pos, BlockPos radarPos, float sizeRenderDiameter, float simSize) {
        return radarToWorldCoords(pos.x, pos.z, radarPos, sizeRenderDiameter, simSize);
    }

    /**
     * Translates a {@link Vec3} of radar coordinates to a {@link Vec3} of absolute coordinates
     *
     * @param x The x-coordinate of the radar position
     * @param z The z-coordinate of the radar position
     * @param rrd The {@link RadarRenderData}
     * @return The {@link Vec3} of radar coordinates
     * @since 0.16.1.0-rc2
     */
    default Vec3 radarToWorldCoords(double x, double z, RadarRenderData rrd) {
        return radarToWorldCoords(x, z, rrd.blockEntity().getBlockPos(), rrd.sizeRenderDiameter(), rrd.simSize());
    }

    /**
     * Translates a {@link Vec3} of radar coordinates to a {@link Vec3} of absolute coordinates
     *
     * @param x The x-coordinate of the radar position
     * @param z The z-coordinate of the radar position
     * @param sizeRenderDiameter The diameter of the radar size. Is 3 for small radars and 6 for big (unless 3x3 option is on)
     * @param simSize The simulation size of the radar. Is 2048 for small radars and 8192 for big radars
     * @return The {@link Vec3} of radar coordinates
     * @since 0.16.1.0-rc2
     */
    default Vec3 radarToWorldCoords(double x, double z, BlockPos radarPos, float sizeRenderDiameter, float simSize) {
        double rtwRatio = simSize / (sizeRenderDiameter / 2.0);

        double centerX = radarPos.getX();
        double centerZ = radarPos.getZ();

        double worldX = centerX + (x * rtwRatio);
        double worldZ = centerZ + (z * rtwRatio);

        return new Vec3(worldX, radarPos.getY(), worldZ);
    }
}