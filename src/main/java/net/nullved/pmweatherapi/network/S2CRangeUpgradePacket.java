package net.nullved.pmweatherapi.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.storage.PMWClientStorages;
import net.nullved.pmweatherapi.client.storage.radar.RangeUpgradeClientStorage;

/**
 * The packet that syncs range upgrade modules from the server to the client, using the Storages system
 * @since 0.16.4.0
 */
public class S2CRangeUpgradePacket extends S2CStoragePacket<RangeUpgradeClientStorage> {
    public static final CustomPacketPayload.Type<S2CRangeUpgradePacket> TYPE = new Type<>(PMWeatherAPI.rl("s2c_range_upgrades"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CRangeUpgradePacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, S2CRangeUpgradePacket::tag, S2CRangeUpgradePacket::new);

    /**
     * Creates a new {@link S2CRangeUpgradePacket}
     * @param tag The {@link CompoundTag} to send with the packet
     * @since 0.16.4.0
     */
    public S2CRangeUpgradePacket(CompoundTag tag) {
        super(tag);
    }

    @Override
    public RangeUpgradeClientStorage getStorage() {
        return PMWClientStorages.rangeUpgrades().get();
    }

    @Override
    public Type<S2CRangeUpgradePacket> type() {
        return TYPE;
    }
}
