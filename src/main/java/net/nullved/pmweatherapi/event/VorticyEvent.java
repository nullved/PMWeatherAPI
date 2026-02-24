package net.nullved.pmweatherapi.event;

import dev.protomanly.pmweather.weather.Vorticy;
import dev.protomanly.pmweather.weather.WeatherHandler;
import dev.protomanly.pmweather.weather.WeatherHandlerClient;
import net.neoforged.bus.api.Event;

/**
 * Base Vorticy Event
 * @see New
 * @see Dead
 * @since 0.14.15.4
 */
public abstract class VorticyEvent extends Event {
    private final Vorticy vorticy;
    private final WeatherHandler weatherHandler;

    public VorticyEvent(Vorticy vorticy, WeatherHandler weatherHandler) {
        this.vorticy = vorticy;
        this.weatherHandler = weatherHandler;
    }

    public Vorticy getVorticy() {
        return vorticy;
    }

    public WeatherHandler getWeatherHandler() {
        return weatherHandler;
    }

    public boolean isClientSide() {
        return weatherHandler instanceof WeatherHandlerClient;
    }

    /**
     * Called when a new {@link Vorticy} is created
     * @since 0.14.15.4
     */
    public static class New extends VorticyEvent {
        public New(Vorticy vorticy, WeatherHandler weatherHandler) {
            super(vorticy, weatherHandler);
        }
    }

    /**
     * Called when a {@link Vorticy} dies
     * @since 0.14.15.4
     */
    public static class Dead extends VorticyEvent {
        public Dead(Vorticy vorticy, WeatherHandler weatherHandler) {
            super(vorticy, weatherHandler);
        }
    }
}
