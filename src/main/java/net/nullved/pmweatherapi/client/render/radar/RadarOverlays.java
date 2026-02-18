package net.nullved.pmweatherapi.client.render.radar;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.render.RadarRenderData;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A class to manage radar overlays.
 * <br>
 * To register an overlay, use {@link #registerOverlay(IRadarOverlay)}
 * @since 0.14.15.0
 */
public class RadarOverlays {
    private static final HashMap<ResourceLocation, OverlayBinding> OVERLAYS = new HashMap<>();

    private record OverlayBinding(IRadarOverlay overlay, Supplier<? extends Object[]> args) {}

    /**
     * @return The {@link Set} of all overlay instances to render to
     */
    public static Set<IRadarOverlay> getOverlays() {
        return OVERLAYS.values()
            .stream()
            .map(OverlayBinding::overlay)
            .collect(Collectors.toSet());
    }

    /**
     * Renders all overlays
     * @param canRender {@code true} if either the server doesn't require WSR-88D or a WSR-88D is complete within 4 chunks of the radar
     * @param radarRenderData The data used to call {@link BlockEntityRenderer#render(BlockEntity, float, PoseStack, MultiBufferSource, int, int)}
     * @since 0.14.15.0
     */
    public static void renderOverlays(RadarRenderData radarRenderData, boolean canRender) {
        OVERLAYS.forEach((rl, binding) -> {
            radarRenderData.poseStack().pushPose();
            binding.overlay.render(canRender, radarRenderData, binding.args);
            radarRenderData.poseStack().popPose();
        });
    }

    /**
     * Registers an overlay to be rendered.
     * @param overlay A {@link Supplier} returning an instance of an {@link IRadarOverlay}
     * @param argsSupplier A supplier that returns an array of arguments
     * @since 0.14.16.1
     */
    public static void registerOverlay(IRadarOverlay overlay, Supplier<? extends Object[]> argsSupplier) {
        PMWeatherAPI.LOGGER.info("Registering overlay {}", overlay.getID());
        OVERLAYS.put(overlay.getID(), new OverlayBinding(overlay, argsSupplier));
    }

    /**
     * Registers an overlay to be rendered.
     * @param overlay A {@link Supplier} returning an instance of an {@link IRadarOverlay}
     * @since 0.14.15.2
     */
    public static void registerOverlay(IRadarOverlay overlay) {
        registerOverlay(overlay, () -> new Object[0]);
    }

    /**
     * Unregisters an overlay
     * @param overlay The {@link IRadarOverlay} to be unregistered
     * @since 0.16.1.0-rc2
     */
    public static void unregisterOverlay(IRadarOverlay overlay) {
        unregisterOverlay(overlay.getID());
    }

    /**
     * Unregisters an overlay
     * @param overlayId The {@link ResourceLocation} of the {@link IRadarOverlay} to be unregistered
     * @since 0.16.1.0-rc2
     */
    public static void unregisterOverlay(ResourceLocation overlayId) {
        PMWeatherAPI.LOGGER.info("Unregistering overlay {}", overlayId);
        OVERLAYS.remove(overlayId);
    }
}
