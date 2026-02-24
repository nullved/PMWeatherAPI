package net.nullved.pmweatherapi.client.render.debug;

import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.data.PMWClientStorages;
import net.nullved.pmweatherapi.client.render.RadarRenderData;
import net.nullved.pmweatherapi.client.render.radar.IRadarOverlay;
import net.nullved.pmweatherapi.radar.NearbyRadars;

public class DebugWSRSOverlay extends DebugOverlay {
    public static final IRadarOverlay INSTANCE = new DebugWSRSOverlay();

    @Override
    public void render(boolean canRender, RadarRenderData radarRenderData, Object... args) {
        PMWClientStorages.wsrs().get().forAllWithinRange(radarRenderData.radarPos(), radarRenderData.simSize(),
            wsd -> renderMarker(radarRenderData, wsd.getPos().getCenter(), 0xFF00FF00));
    }

    @Override
    public String getModID() {
        return PMWeatherAPI.MODID;
    }

    @Override
    public String getIDPath() {
        return "debug_wsrs";
    }
}
