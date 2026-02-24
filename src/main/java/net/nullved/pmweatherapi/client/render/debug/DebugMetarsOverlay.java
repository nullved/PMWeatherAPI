package net.nullved.pmweatherapi.client.render.debug;

import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.data.PMWClientStorages;
import net.nullved.pmweatherapi.client.render.RadarRenderData;
import net.nullved.pmweatherapi.client.render.radar.IRadarOverlay;

public class DebugMetarsOverlay extends DebugOverlay {
    public static final IRadarOverlay INSTANCE = new DebugMetarsOverlay();

    @Override
    public void render(boolean canRender, RadarRenderData radarRenderData, Object... args) {
        PMWClientStorages.metars().get().forAllWithinRange(radarRenderData.radarPos(), radarRenderData.simSize(),
            msd -> renderMarker(radarRenderData, msd.getPos().getCenter(), 0xFF0000FF));
    }

    @Override
    public String getModID() {
        return PMWeatherAPI.MODID;
    }

    @Override
    public String getIDPath() {
        return "debug_metars";
    }
}
