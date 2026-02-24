package net.nullved.pmweatherapi.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.render.RadarRenderData;
import net.nullved.pmweatherapi.client.render.radar.IRadarOverlay;
import net.nullved.pmweatherapi.radar.NearbyRadars;

public abstract class DebugOverlay implements IRadarOverlay {
    protected void renderMarker(RadarRenderData radarRenderData, Vec3 pos, int color) {
        PoseStack pose = radarRenderData.poseStack();
        pose.pushPose();
        placeOnRadar(pos, pose, radarRenderData);
        scale(pose, 0.05f);
        renderTextureUpwards(PMWeatherAPI.rl("textures/radar/test1.png"), radarRenderData, pose, color);

        pose.translate(0, 1, 0);
        renderTexture(PMWeatherAPI.rl("textures/radar/test2.png"), radarRenderData, pose, color);
        pose.mulPose(Axis.YP.rotationDegrees(90));
        renderTexture(PMWeatherAPI.rl("textures/radar/test2.png"), radarRenderData, pose, color);
        pose.popPose();
    }
}
