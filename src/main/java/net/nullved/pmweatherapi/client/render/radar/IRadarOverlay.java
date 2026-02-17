package net.nullved.pmweatherapi.client.render.radar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
        //RenderType rt = RadarRenderTypes.createForTexture(texture);
        VertexConsumer consumer = radarRenderData.multiBufferSource().getBuffer(RenderType.entityCutout(texture));

        vertex(consumer, pose, color, -0.5f, -0.5f, 0, 0, 0, radarRenderData.combinedOverlayIn());
        vertex(consumer, pose, color, 0.5f, -0.5f, 0, 1, 0, radarRenderData.combinedOverlayIn());
        vertex(consumer, pose, color, 0.5f, 0.5f, 0, 1, 1, radarRenderData.combinedOverlayIn());
        vertex(consumer, pose, color, -0.5f, 0.5f, 0, 0, 1, radarRenderData.combinedOverlayIn());

//        consumer.addVertex(pose, -0.5f, -0.5f, 0.0f)
//                .setColor(color)
//                .setUv(0.0F, 0.0F)
//                .setOverlay(renderData.combinedOverlayIn())
//                .setLight(renderData.combinedLightIn())
//                .setNormal(pose, 0.0f, 1.0f, 0.0f);
//
//        consumer.addVertex(pose, 0.5f, -0.5f, 0.0f)
//                .setColor(color)
//                .setUv(1.0F, 0.0F)
//                .setOverlay(renderData.combinedOverlayIn())
//                .setLight(renderData.combinedLightIn())
//                .setNormal(pose, 0.0f, 1.0f, 0.0f);
//
//        consumer.addVertex(pose, 0.5f, 0.5f, 0.0f)
//                .setColor(color)
//                .setUv(1.0F, 1.0F)
//                .setOverlay(renderData.combinedOverlayIn())
//                .setLight(renderData.combinedLightIn())
//                .setNormal(pose, 0.0f, 1.0f, 0.0f);
//
//        consumer.addVertex(pose, -0.5f, 0.5f, 0.0f)
//                .setColor(color)
//                .setUv(0.0F, 1.0F)
//                .setOverlay(renderData.combinedOverlayIn())
//                .setLight(renderData.combinedLightIn())
//                .setNormal(pose, 0.0f, 1.0f, 0.0f);
    }

    default void vertex(VertexConsumer buffer, PoseStack.Pose pose, int color, float x, float y, float z, float u, float v, int overlay) {
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
     * @since 0.15.3.3
     */
    default void renderTexture(ResourceLocation texture, RadarRenderData radarRenderData, PoseStack poseStack) {
        renderTexture(texture, radarRenderData, poseStack, 0xFFFFFFFF);
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
     * Render a texture at the given {@link ResourceLocation}
     * @param texture The {@link ResourceLocation} of the texture
     * @param radarRenderData The {@link RadarRenderData}
     * @since 0.15.3.3
     */
    default void renderTexture(ResourceLocation texture, RadarRenderData radarRenderData) {
        renderTexture(texture, radarRenderData, radarRenderData.poseStack(), 0xFFFFFFFF);
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



    default Vec3 worldToRadarCoords(Vec3 pos, BlockPos radarPos, int resolution, float simSize) {
        return worldToRadarCoords(pos.x, pos.y, pos.z, radarPos, resolution, simSize);
    }

    default Vec3 worldToRadarCoords(double x, double y, double z, BlockPos radarPos, int resolution, float simSize) {
//        NearbyStorms.client().forStormNearBlock(pos, 2048, s -> renderMarker(renderData, s.position.add(-pos.getX(), -pos.getY(), -pos.getZ()), 0xFF008800));
//        Vector3f radarPos = relative.add(0.5, 0.5, 0.5).toVector3f().mul(3 / (2 * resolution)).div(2048, 0, 2048).div(1.0F / resolution, 0.0F, 1.0F / resolution);

//        Vector3f radarPos = relative.add(0.5, 0.5, 0.5).toVector3f().mul(3 / (2 * resolution)).div(2048, 0, 2048).div(1.0F / resolution, 0.0F, 1.0F / resolution);
//        Vector3f topLeft = (new Vector3f(-1.0F, 0.0F, -1.0F)).mul(0.015F).add(radarPos.x, 0.005F, radarPos.z);
//        Vector3f bottomLeft = (new Vector3f(-1.0F, 0.0F, 1.0F)).mul(0.015F).add(radarPos.x, 0.005F, radarPos.z);
//        Vector3f bottomRight = (new Vector3f(1.0F, 0.0F, 1.0F)).mul(0.015F).add(radarPos.x, 0.005F, radarPos.z);
//        Vector3f topRight = (new Vector3f(1.0F, 0.0F, -1.0F)).mul(0.015F).add(radarPos.x, 0.005F, radarPos.z);

        double resolutionScaleFactor = 3D / (2 * resolution);
        double invSize = 1D / simSize;

        double dx = x - radarPos.getX();
        double dy = y - radarPos.getY();
        double dz = z - radarPos.getZ();

        Vec3 radarCoords = new Vec3(dx, dy, dz);
        radarCoords.add(0.5, 0.5, 0.5)
                .multiply(resolutionScaleFactor, resolutionScaleFactor, resolutionScaleFactor)
                .multiply(invSize, invSize, invSize);

        return radarCoords;
    }

    default Vec3 radarToWorldCoords(Vec3 pos, BlockPos radarPos, int resolution, float simSize) {
        return radarToWorldCoords(pos.x, pos.y, pos.z, radarPos, resolution, simSize);
    }

    default Vec3 radarToWorldCoords(double x, double y, double z, BlockPos radarPos, int resolution, float simSize) {
        double resolutionScaleFactor = (2 * resolution) / 3D;
        double invResolution = 1D / resolution;

        double dx = x + radarPos.getX();
        double dy = y + radarPos.getY();
        double dz = z + radarPos.getZ();

        Vec3 worldCoords = new Vec3(dx, dy, dz);
        worldCoords.add(-0.5, -0.5, -0.5)
                .multiply(resolutionScaleFactor, resolutionScaleFactor, resolutionScaleFactor)
                .multiply(simSize, simSize, simSize);

        return worldCoords;
    }

    default Vec3 worldToRadarCoordsExp(Vec3 pos, BlockPos radarPos, float sizeRenderDiameter, float simSize) {
        double wtrRatio = (sizeRenderDiameter / 2.0f) / simSize;

        double centerX = radarPos.getX() + 0.5D;
        double centerZ = radarPos.getZ() + 0.5D;

        double dx = pos.x - centerX;
        double dz = pos.z - centerZ;

        return new Vec3(dx * wtrRatio, 0, dz * wtrRatio);
    }

    default Vec3 radarToWorldCoordsExp(Vec3 pos, BlockPos radarPos, float sizeRenderDiameter, float simSize) {
        double rtwRatio = simSize / (sizeRenderDiameter / 2.0);

        double centerX = radarPos.getX() + 0.5D;
        double centerZ = radarPos.getZ() + 0.5D;

        double worldX = centerX + (pos.x * rtwRatio);
        double worldZ = centerZ + (pos.z * rtwRatio);

        return new Vec3(worldX, radarPos.getY(), worldZ);
    }
}