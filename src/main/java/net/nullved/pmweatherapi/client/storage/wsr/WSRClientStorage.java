package net.nullved.pmweatherapi.client.storage.wsr;

import dev.protomanly.pmweather.multiblock.wsr88d.WSR88DCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.nullved.pmweatherapi.client.storage.IClientStorage;
import net.nullved.pmweatherapi.client.storage.PMWClientStorages;
import net.nullved.pmweatherapi.storage.wsr.WSRStorage;
import net.nullved.pmweatherapi.storage.wsr.WSRStorageData;

/**
 * A {@link IClientStorage} implementation for {@link WSR88DCore}s
 * <br><br>
 * You should not create a {@link WSRClientStorage}, instead, use {@link PMWClientStorages#wsrs()}
 * @since 0.15.3.3
 */
public class WSRClientStorage extends WSRStorage implements IClientStorage<WSRStorageData> {
    /**
     * <strong>DO NOT CALL THIS CONSTRUCTOR!!!</strong>
     * <br>
     * Get a radar storage from {@link PMWClientStorages#wsrs()}
     * @param clientLevel The {@link ClientLevel} to create this storage for
     * @since 0.15.3.3
     */
    public WSRClientStorage(ClientLevel clientLevel) {
        super(clientLevel.dimension());
    }

    /**
     * Gets the level associated with this {@link WSRClientStorage}
     * @return The {@link Minecraft} {@link ClientLevel}
     * @since 0.15.3.3
     */
    @Override
    public ClientLevel getLevel() {
        return Minecraft.getInstance().level;
    }
}