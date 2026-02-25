package net.nullved.pmweatherapi.storage.radar;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.storage.radar.RangeUpgradeClientStorage;
import net.nullved.pmweatherapi.storage.PMWStorage;
import net.nullved.pmweatherapi.storage.data.BlockPosData;

/**
 * {@link PMWStorage} for Radar Upgrade Modules
 *
 * @since 0.16.4.0
 * @see PMWStorage
 * @see RangeUpgradeServerStorage
 * @see RangeUpgradeClientStorage
 */
public abstract class RangeUpgradeStorage extends PMWStorage<BlockPosData> {
    public static final ResourceLocation ID = PMWeatherAPI.rl("range_upgrades");

    public RangeUpgradeStorage(ResourceKey<Level> dimension) {
        super(dimension);
    }

    @Override
    public ResourceLocation getExpectedDataType() {
        return BlockPosData.ID;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int version() {
        return 1;
    }
}
