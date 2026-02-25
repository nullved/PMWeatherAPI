package net.nullved.pmweatherapi.client.storage.radar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.nullved.pmweatherapi.client.storage.IClientStorage;
import net.nullved.pmweatherapi.client.storage.PMWClientStorages;
import net.nullved.pmweatherapi.storage.data.BlockPosData;
import net.nullved.pmweatherapi.storage.radar.RangeUpgradeStorage;

/**
 * A {@link IClientStorage} implementation for Radar Upgrade Modules
 * <br><br>
 * You should not create a {@link RangeUpgradeClientStorage}, instead, use {@link PMWClientStorages#rangeUpgrades()}
 * @since 0.16.4.0
 */
public class RangeUpgradeClientStorage extends RangeUpgradeStorage implements IClientStorage<BlockPosData> {
    /**
     * <strong>DO NOT CALL THIS CONSTRUCTOR!!!</strong>
     * <br>
     * Get a radar storage from {@link PMWClientStorages#rangeUpgrades()}
     * @param clientLevel The {@link ClientLevel} to create this storage for
     * @since 0.16.4.0
     */
    public RangeUpgradeClientStorage(ClientLevel clientLevel) {
        super(clientLevel.dimension());
    }

    @Override
    public ClientLevel getLevel() {
        return Minecraft.getInstance().level;
    }
}
