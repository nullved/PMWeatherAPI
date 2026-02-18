package net.nullved.pmweatherapi.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.render.radar.IRadarOverlay;
import net.nullved.pmweatherapi.client.render.radar.RadarOverlays;
import net.nullved.pmweatherapi.config.PMWClientConfig;
import net.nullved.pmweatherapi.radar.RadarMode;

import java.util.function.Supplier;

/**
 * The overlay for {@link RadarMode} IDs.
 * <br>
 * To enable, you must enable {@code Show Radar Mode IDs} in PMWeatherAPI's Client Config
 * @since 0.14.16.2
 */
@OnlyIn(Dist.CLIENT)
public class IDOverlay implements IRadarOverlay {
    public static final IRadarOverlay INSTANCE = new IDOverlay();

    @Override
    public void render(boolean canRender, RadarRenderData radarRenderData, Object... args) {
        if (!PMWClientConfig.showRadarModeId) return;
        if (!Minecraft.getInstance().player.isCrouching()) return;

        RadarMode mode = getRadarMode(radarRenderData);
        PoseStack poseStack = radarRenderData.poseStack();
        PMWClientConfig.RadarModeIDSide side = PMWClientConfig.radarModeIDSide;

        float scale = radarRenderData.sizeRenderDiameter() / 3.0F;

        poseStack.pushPose();
        poseStack.translate((side.x * scale) - 0.5F * (scale - 1) - 0.5F, 0.005f, (side.z * scale) - 0.5F * (scale - 1) - 0.5F);
        poseStack.mulPose(Axis.YN.rotationDegrees(side.rotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.scale(0.01F, 0.01F, 0.01F);
        poseStack.scale(scale, scale, scale);

        renderText(Component.literal(mode.getId().toString()), radarRenderData, poseStack);

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));


        float lineHeight = 8.0f;
        float offset = lineHeight;
        for (IRadarOverlay overlay: RadarOverlays.getOverlays()) {
            poseStack.pushPose();
            poseStack.translate(0, 0, offset);
            poseStack.scale(0.6f, 0.6f, 0.6f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));

            renderText(Component.literal(overlay.getID().toString()).withColor(0xBBBBBB), radarRenderData, poseStack);

            poseStack.popPose();
            offset += lineHeight * 0.6f;
        }

        poseStack.popPose();
    }

    @Override
    public String getModID() {
        return PMWeatherAPI.MODID;
    }

    @Override
    public String getIDPath() {
        return "id";
    }
}
