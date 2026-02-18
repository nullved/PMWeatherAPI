package net.nullved.pmweatherapi.example;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.render.RadarRenderData;
import net.nullved.pmweatherapi.client.render.radar.IRadarOverlay;
import net.nullved.pmweatherapi.config.PMWClientConfig;
import net.nullved.pmweatherapi.radar.NearbyRadars;
import net.nullved.pmweatherapi.radar.RadarMode;
import net.nullved.pmweatherapi.storm.NearbyStorms;

/**
 * This is an example overlay that draws a dot at every lightning strike and fades out
 * @see IRadarOverlay
 */
@OnlyIn(Dist.CLIENT)
public class ExampleOverlay implements IRadarOverlay {
    public static final ExampleOverlay INSTANCE = new ExampleOverlay();

    @Override
    public void render(boolean canRender, RadarRenderData radarRenderData, Object... args) {
        if (!canRender) return;
        BlockEntity blockEntity = radarRenderData.blockEntity();
        BlockPos pos = blockEntity.getBlockPos();
        RadarMode mode = getRadarMode(radarRenderData);

        if (mode == RadarMode.REFLECTIVITY) {
            NearbyRadars.client().forRadarNearBlock(pos, radarRenderData.simSize(), r -> renderMarker(radarRenderData, r.getCenter(), 0xFF00FF00, 0xFFFF00FF));
        } else if (mode == RadarMode.VELOCITY) {
            NearbyStorms.client().forStormNearBlock(pos, radarRenderData.simSize(), s -> renderMarker(radarRenderData, s.position, 0xFF0000FF, 0xFFFFFF00));
        }
    }

    @Override
    public String getModID() {
        return "example";
    }

    private void renderMarker(RadarRenderData radarRenderData, Vec3 marker, int color, int c2) {
        PoseStack pose = radarRenderData.poseStack();
        pose.pushPose();
        placeOnRadar(marker, pose, radarRenderData);
        scale(pose, 0.1f);

        renderTextureUpwards(PMWeatherAPI.rl("textures/radar/test1.png"), radarRenderData, pose, color);

        if (PMWClientConfig.debug) {
            pose.pushPose();
            pose.translate(0, 1, 0);
            renderTexture(PMWeatherAPI.rl("textures/radar/test2.png"), radarRenderData, pose, c2);
            pose.popPose();
        }

        pose.popPose();
    }

//        Vec3 radarPos = worldToRadarCoords(markerLocation, radarRenderData);
//        pose.translate(radarPos.x, 0.05f, radarPos.z);
//        pose.mulPose(Axis.XN.rotationDegrees(90));
//
//        placeOnRadar(markerLocation, pose, radarRenderData);
//        orientUpwards(pose);
//        pose.scale(0.1F, 0.1F, 0.1F);
}
