package net.nullved.pmweatherapi.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.render.radar.IRadarOverlay;
import net.nullved.pmweatherapi.radar.NearbyRadars;

public class DebugOverlay implements IRadarOverlay {
    public static final IRadarOverlay INSTANCE = new DebugOverlay();

    @Override
    public void render(boolean canRender, RadarRenderData radarRenderData, Object... args) {
        NearbyRadars.client().forRadarNearBlock(radarRenderData.radarPos(), radarRenderData.simSize(), bp -> {
            PoseStack pose = radarRenderData.poseStack();
            pose.pushPose();
            placeOnRadar(bp.getCenter(), pose, radarRenderData);
            scale(pose, 0.05f);
            renderTextureUpwards(PMWeatherAPI.rl("textures/radar/test1.png"), radarRenderData, pose);

            pose.translate(0, 1, 0);
            renderTexture(PMWeatherAPI.rl("textures/radar/test2.png"), radarRenderData, pose);
            pose.mulPose(Axis.YP.rotationDegrees(90));
            renderTexture(PMWeatherAPI.rl("textures/radar/test2.png"), radarRenderData, pose);
            pose.popPose();
        });
    }

    @Override
    public String getModID() {
        return PMWeatherAPI.MODID;
    }

    @Override
    public String getIDPath() {
        return "debug";
    }
}
