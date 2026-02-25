package net.nullved.pmweatherapi.storage.radar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.nullved.pmweatherapi.client.storage.IClientStorage;
import net.nullved.pmweatherapi.client.storage.radar.RangeUpgradeClientStorage;
import net.nullved.pmweatherapi.data.PMWStorages;
import net.nullved.pmweatherapi.network.S2CRadarPacket;
import net.nullved.pmweatherapi.network.S2CRangeUpgradePacket;
import net.nullved.pmweatherapi.network.S2CStoragePacket;
import net.nullved.pmweatherapi.storage.IServerStorage;
import net.nullved.pmweatherapi.storage.ISyncServerStorage;
import net.nullved.pmweatherapi.storage.data.BlockPosData;

/**
 * {@link IServerStorage} for Range Upgrade Modules
 * <br><br>
 * You should not create a {@link RangeUpgradeServerStorage}, instead, use {@link PMWStorages#rangeUpgrades()}
 *
 * @since 0.16.4.0
 * @see RangeUpgradeStorage
 * @see RangeUpgradeClientStorage
 */
public class RangeUpgradeServerStorage extends RangeUpgradeStorage implements ISyncServerStorage<BlockPosData> {
    private final ServerLevel level;

    /**
     * <strong>DO NOT CALL THIS CONSTRUCTOR!!!</strong>
     * <br>
     * Get a range upgrade storage from {@link PMWStorages#rangeUpgrades()}
     * @param level The level to create this storage for
     * @since 0.16.4.0
     */
    public RangeUpgradeServerStorage(ServerLevel level) {
        super(level.dimension());
        this.level = level;
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public S2CStoragePacket<? extends IClientStorage<BlockPosData>> packet(CompoundTag tag) {
        return new S2CRangeUpgradePacket(tag);
    }
}
