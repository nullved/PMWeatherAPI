package net.nullved.pmweatherapi.client.storage.radar;

import dev.protomanly.pmweather.block.RadarBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.nullved.pmweatherapi.client.storage.IClientStorage;
import net.nullved.pmweatherapi.client.storage.PMWClientStorages;
import net.nullved.pmweatherapi.storage.radar.RadarStorage;
import net.nullved.pmweatherapi.storage.radar.RadarStorageData;

/**
 * A {@link IClientStorage} implementation for {@link RadarBlock}s
 * <br><br>
 * You should not create a {@link RadarClientStorage}, instead, use {@link PMWClientStorages#radars()}
 * @since 0.15.3.3
 */
public class RadarClientStorage extends RadarStorage implements IClientStorage<RadarStorageData> {
    /**
     * <strong>DO NOT CALL THIS CONSTRUCTOR!!!</strong>
     * <br>
     * Get a radar storage from {@link PMWClientStorages#radars()}
     * @param clientLevel The {@link ClientLevel} to create this storage for
     * @since 0.15.3.3
     */
    public RadarClientStorage(ClientLevel clientLevel) {
        super(clientLevel.dimension());
    }

    /**
     * Gets the level associated with this {@link RadarClientStorage}
     * @return The {@link Minecraft} {@link ClientLevel}
     * @since 0.15.3.3
     */
    @Override
    public ClientLevel getLevel() {
        return Minecraft.getInstance().level;
    }
}