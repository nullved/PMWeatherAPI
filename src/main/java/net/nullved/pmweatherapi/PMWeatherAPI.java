package net.nullved.pmweatherapi;

import com.mojang.logging.LogUtils;
import dev.protomanly.pmweather.addons.AddonHelper;
import dev.protomanly.pmweather.addons.AddonInfo;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.nullved.pmweatherapi.client.data.PMWClientStorages;
import net.nullved.pmweatherapi.client.metar.MetarClientStorage;
import net.nullved.pmweatherapi.client.radar.RadarClientStorage;
import net.nullved.pmweatherapi.client.radar.WSRClientStorage;
import net.nullved.pmweatherapi.client.render.IDOverlay;
import net.nullved.pmweatherapi.client.render.radar.RadarOverlays;
import net.nullved.pmweatherapi.config.PMWClientConfig;
import net.nullved.pmweatherapi.data.PMWStorages;
import net.nullved.pmweatherapi.metar.MetarServerStorage;
import net.nullved.pmweatherapi.metar.MetarStorage;
import net.nullved.pmweatherapi.metar.MetarStorageData;
import net.nullved.pmweatherapi.network.PMWNetworking;
import net.nullved.pmweatherapi.radar.storage.*;
import net.nullved.pmweatherapi.storage.data.BlockPosData;
import net.nullved.pmweatherapi.storage.data.StorageDataManager;
import org.slf4j.Logger;

import java.util.List;

@Mod(PMWeatherAPI.MODID)
public class PMWeatherAPI {
    public static final String MODID = "pmweatherapi";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PMWeatherAPI(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing PMWAPI...");

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerPayloads);

        AddonHelper.registerAddon(new AddonInfo(modContainer, List.of("0.16.4", "0.16.5")));

        LOGGER.info("Initialized PMWAPI");

        LOGGER.info("Registering PMWAPI Config");
        if (FMLEnvironment.dist.isClient()) {
            modContainer.registerConfig(ModConfig.Type.CLIENT, PMWClientConfig.SPEC);
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Registering PMWAPI Storage Data...");
        StorageDataManager.register(BlockPosData.ID, BlockPosData::deserializeFromNBT);
        StorageDataManager.register(RadarStorageData.ID, RadarStorageData::deserializeFromNBT);
        StorageDataManager.register(MetarStorageData.ID, MetarStorageData::deserializeFromNBT);
        StorageDataManager.register(WSRStorageData.ID, WSRStorageData::deserializeFromNBT);

        LOGGER.info("Registering PMWAPI Storages [Common]...");
        PMWStorages.registerStorage(RadarStorage.ID, RadarServerStorage.class, RadarServerStorage::new);
        PMWStorages.registerStorage(MetarStorage.ID, MetarServerStorage.class, MetarServerStorage::new);
        PMWStorages.registerStorage(WSRStorage.ID, WSRServerStorage.class, WSRServerStorage::new);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        LOGGER.info("Registering PMWAPI Network Payloads...");

        PMWNetworking.register(event.registrar("1"));
    }

    private void clientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Registering PMWAPI Storages [Client]...");
        PMWClientStorages.registerStorage(RadarStorage.ID, RadarClientStorage.class, RadarClientStorage::new);
        PMWClientStorages.registerStorage(MetarStorage.ID, MetarClientStorage.class, MetarClientStorage::new);
        PMWClientStorages.registerStorage(WSRStorage.ID, WSRClientStorage.class, WSRClientStorage::new);

        LOGGER.info("Registering PMWAPI Radar Overlays...");
        RadarOverlays.registerOverlay(IDOverlay.INSTANCE);
//        RadarOverlays.registerOverlay(ExampleOverlay.INSTANCE);
    }
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
