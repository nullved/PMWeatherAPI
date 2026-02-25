package net.nullved.pmweatherapi.storage.radar;

import dev.protomanly.pmweather.block.RadarBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.nullved.pmweatherapi.client.storage.IClientStorage;
import net.nullved.pmweatherapi.client.storage.radar.RadarClientStorage;
import net.nullved.pmweatherapi.data.PMWStorages;
import net.nullved.pmweatherapi.network.S2CRadarPacket;
import net.nullved.pmweatherapi.network.S2CStoragePacket;
import net.nullved.pmweatherapi.storage.IServerStorage;
import net.nullved.pmweatherapi.storage.ISyncServerStorage;

/**
 * {@link IServerStorage} for {@link RadarBlock}s
 * <br><br>
 * You should not create a {@link RadarServerStorage}, instead, use {@link PMWStorages#radars()}
 *
 * @since 0.15.3.3
 * @see RadarStorage
 * @see RadarClientStorage
 */
public class RadarServerStorage extends RadarStorage implements ISyncServerStorage<RadarStorageData> {
    private final ServerLevel level;

    /**
     * <strong>DO NOT CALL THIS CONSTRUCTOR!!!</strong>
     * <br>
     * Get a radar storage from {@link PMWStorages#radars()}
     * @param level The level to create this storage for
     * @since 0.15.3.3
     */
    public RadarServerStorage(ServerLevel level) {
        super(level.dimension());
        this.level = level;
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public S2CStoragePacket<? extends IClientStorage<RadarStorageData>> packet(CompoundTag tag) {
        return new S2CRadarPacket(tag);
    }
}
