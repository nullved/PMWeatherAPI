package net.nullved.pmweatherapi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.protomanly.pmweather.PMWeather;
import dev.protomanly.pmweather.block.RadarBlock;
import dev.protomanly.pmweather.block.entity.RadarBlockEntity;
import dev.protomanly.pmweather.config.ClientConfig;
import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.event.GameBusClientEvents;
import dev.protomanly.pmweather.multiblock.wsr88d.WSR88DCore;
import dev.protomanly.pmweather.render.RadarRenderer;
import dev.protomanly.pmweather.util.ColorTables;
import dev.protomanly.pmweather.weather.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.nullved.pmweatherapi.client.data.PMWClientStorages;
import net.nullved.pmweatherapi.client.render.PixelRenderData;
import net.nullved.pmweatherapi.client.render.RadarRenderData;
import net.nullved.pmweatherapi.client.render.radar.RadarOverlays;
import net.nullved.pmweatherapi.client.render.radar.RadarRenderTypes;
import net.nullved.pmweatherapi.config.PMWClientConfig;
import net.nullved.pmweatherapi.data.PMWExtras;
import net.nullved.pmweatherapi.radar.RadarMode;
import net.nullved.pmweatherapi.util.ColorMap;
import net.nullved.pmweatherapi.util.ColorMaps;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mixin(RadarRenderer.class)
public class RadarRendererMixin {
    @Shadow public static int RenderedRadars = 0;
    @Unique private static double pmwapi$lastFrameId = 0;
    @Unique private static double pmwapi$lastNanoTime = 0;

    @WrapMethod(method = "render")
    private void render(BlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn, Operation<Void> original) {
        if (PMWClientConfig.useOriginalPMWeatherRendering) {
            original.call(blockEntity, partialTicks, poseStack, multiBufferSource, combinedLightIn, combinedOverlayIn);
            return;
        }

        pmwapi$renderRadarFrameCheck(blockEntity, partialTicks);

        if (!(blockEntity instanceof RadarBlockEntity radarBlockEntity)) return;
        if (!radarBlockEntity.getBlockState().getValue(RadarBlock.ON)) return;
        if (Minecraft.getInstance().player.position().distanceTo(blockEntity.getBlockPos().getCenter()) > (double) 20.0F || RenderedRadars > 2) return;

        ++RenderedRadars;
        boolean canRender = true;
        BlockPos pos = radarBlockEntity.getBlockPos();
        float sizeRenderDiameter = 3.0F;
        float simSize = 2048.0F;

        if (radarBlockEntity.hasRangeUpgrade) {
            simSize *= 4.0F;
            if (!ClientConfig._3X3Radar) {
                sizeRenderDiameter = 6.0F;
            }
        }

        int resolution = ClientConfig.radarResolution;
//        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
//        matrix4fStack.pushMatrix();
//        matrix4fStack.mul(poseStack.last().pose());
//        matrix4fStack.translate(0.5F, 1.05F, 0.5F);

//        RenderSystem.applyModelViewMatrix();
//        RenderSystem.enableBlend();
//        RenderSystem.depthMask(true);
//        RenderSystem.enableDepthTest();
//        RenderSystem.setShader(GameRenderer::getPositionColorShader);
//        RenderSystem.defaultBlendFunc();

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.05F, 0.5F);

        // PMWeatherAPI: Switch to using a VertexConsumer over a BufferBuilder
        VertexConsumer vc = multiBufferSource.getBuffer(RadarRenderTypes.RADAR);
        List<Storm> storms = new ArrayList<>(radarBlockEntity.storms);
        boolean update = false;

        ClientConfig.RadarMode clientRadarMode = ClientConfig.radarMode;
        if (radarBlockEntity.lastUpdate < radarBlockEntity.tickCount) {
            radarBlockEntity.lastUpdate = radarBlockEntity.tickCount + 60;
            update = true;
        }

        if (ServerConfig.requireWSR88D && update) {
            canRender = pmwapi$testForWSR(blockEntity);
        }

        float invResolution = 1.0F / resolution;
        float size = sizeRenderDiameter * invResolution;

        RadarRenderData radarRenderData = new RadarRenderData(radarBlockEntity, sizeRenderDiameter, simSize, partialTicks, poseStack, multiBufferSource, combinedLightIn, combinedOverlayIn);
        RadarMode radarMode = blockEntity.getBlockState().getValue(PMWExtras.RADAR_MODE);
        if (!PMWClientStorages.RADAR_MODE_COLORS.computeIfAbsent(radarBlockEntity, bp -> new HashMap<>()).containsKey(radarMode)) update = true;

        // PMWeatherAPI: Flatten double for loop into single pixel idx (pidx) variable
        int maxPixelIdx = 4 * resolution * resolution + 4 * resolution + 1;
        for (int pidx = 0; pidx < maxPixelIdx; pidx++) {
            int x = pmwapi$getX(pidx, resolution);
            int z = pmwapi$getZ(pidx, resolution);

            long longID = (long) (x + resolution + 1) + (long) (z + resolution + 1) * ((long) resolution * 2L + 1L);

            float dbz = radarBlockEntity.reflectivityMap.getOrDefault(longID, 0.0F);
            float temp = radarBlockEntity.temperatureMap.getOrDefault(longID, 15.0F);
            float vel = radarBlockEntity.velocityMap.getOrDefault(longID, 0.0F);
            int color = PMWClientStorages.RADAR_MODE_COLORS.computeIfAbsent(radarBlockEntity, bp -> new HashMap<>()).computeIfAbsent(radarMode, rm -> new HashMap<>()).getOrDefault(longID, 0xFFFF00FF);

            float px = x * invResolution * (sizeRenderDiameter / 2);
            float pz = z * invResolution * (sizeRenderDiameter / 2);
            double wx = (float) (x * invResolution * simSize + pos.getCenter().x);
            double wz = (float) (z * invResolution * simSize + pos.getCenter().z);
            Vec3 worldpos = new Vec3(wx, 0, wz);

            if (update) {
                float clouds = Clouds.getCloudDensity(GameBusClientEvents.weatherHandler, new Vector2f((float) wx, (float) wz), 0.0F);

                dbz = 0.0F;
                temp = 0.0F;
                Vec2 f = (new Vec2((float)x, (float)z)).normalized();
                Vec3 wind = WindEngine.getWind(new Vec3(wx, blockEntity.getLevel().getMaxBuildHeight() + 1, wz), blockEntity.getLevel(), false, false, false);
                Vec2 w = new Vec2((float)wind.x, (float)wind.z);
                vel = f.dot(w);

                for(Storm storm : storms) {
                    dbz = pmwapi$getDBZForStorm(radarBlockEntity, storm, worldpos, dbz);
                }

                float v = Math.max(clouds - 0.15F, 0.0F) * 4.0F;
                if (v > 0.3F) {
                    float dif = (v - 0.4F) / 1.5F;
                    v -= dif;
                }

                dbz = Math.max(dbz, v);
                dbz += (PMWeather.RANDOM.nextFloat() - 0.5F) * 5.0F / 60.0F;
                vel += (PMWeather.RANDOM.nextFloat() - 0.5F) * 3.0F;
                if (dbz > 1.0F) {
                    dbz = (dbz - 1.0F) / 3.0F + 1.0F;
                }

                if (!canRender) {
                    dbz = PMWeather.RANDOM.nextFloat() * 1.2F;
                    vel = (PMWeather.RANDOM.nextFloat() - 0.5F) * 300.0F;
                    temp = 15.0F;
                } else {
                    temp = ThermodynamicEngine.samplePoint(GameBusClientEvents.weatherHandler, worldpos, blockEntity.getLevel(), radarBlockEntity, 0).temperature();
                }

                radarBlockEntity.reflectivityMap.put(longID, dbz);
                radarBlockEntity.temperatureMap.put(longID, temp);
                radarBlockEntity.velocityMap.put(longID, vel);

                // PMWeatherAPI: Support custom radar modes
                if (!PMWClientConfig.disableCustomRadarModeRendering) {
                    PixelRenderData pixelRenderData = new PixelRenderData(canRender, dbz * 60.0F, vel, temp, x, z, resolution, wx, wz, radarRenderData);
                    color = radarMode.getColorForPixel(pixelRenderData);
                    PMWClientStorages.RADAR_MODE_COLORS.computeIfAbsent(radarBlockEntity, bp -> new HashMap<>()).get(radarMode).put(longID, color);
                }
            }

            float rdbz = dbz * 60.0F;
            int startColor = radarBlockEntity.terrainMap.getOrDefault(longID, Color.BLACK).getRGB();
            if (radarBlockEntity.init && update) {
                Holder<Biome> biome = radarBlockEntity.getNearestBiome(new BlockPos((int) wx, pos.getY(), (int) wz));
                String rn = biome.getRegisteredName().toLowerCase();
                if (rn.contains("ocean") || rn.contains("river")) startColor = 0xFF000000 | biome.value().getWaterColor();
                else if (rn.contains("beach") || rn.contains("desert")) startColor = 0xFFE3C696;
                else if (rn.contains("badlands")) startColor = 0xFFD66F2A;
                else startColor = 0xFF000000 | biome.value().getGrassColor(wx, wz);

                if (PMWClientConfig.darkenBiomesOnRadar) startColor = 0xFF000000 | ColorMap.lerp(0.5F, startColor, 0xFF000000);
                if (PMWClientConfig.transparentBackground) startColor = 0x00000000;
                radarBlockEntity.terrainMap.put(longID, new Color(startColor));
            }

            if (PMWClientConfig.disableCustomRadarModeRendering || PMWClientConfig.useOriginalPMWeatherColors) {
                color = pmwapi$getCTPixelColor(radarBlockEntity, rdbz, startColor, temp, radarMode, vel);
            }

            if (ClientConfig.radarDebugging && update) {
                Color dbg = radarBlockEntity.debugMap.getOrDefault(longID, Color.BLACK);
                dbg = pmwapi$getClientDebugColor(radarBlockEntity, clientRadarMode, wx, pos.getY(), wz, dbg, x, z, (float) resolution, pos, storms);
                color = dbg.getRGB();
                radarBlockEntity.debugMap.put(longID, new Color(color));
            }

            if (!RadarMode.isBaseRenderingDisabled()) {
                int a = (int) (FastColor.ARGB32.alpha(color) * 0.75F + 0.25F);
                color = (a << 24) | color & 0xFFFFFF;

                pmwapi$renderQuad(vc, px, 0.0F, pz, size / 4.0F, poseStack, color, combinedLightIn);
            }
        }

        int color = radarMode.getDotColor();
        pmwapi$renderQuad(vc, 0.0F, 0.01F, 0.0F, 0.015F, poseStack, color, combinedLightIn);

//        if (multiBufferSource instanceof MultiBufferSource.BufferSource bs) {
//            bs.endBatch(RadarRenderTypes.RADAR);
//        }

        // PMWeatherAPI: RadarOverlays callback
        // Overlays start rendering here
        if (!ClientConfig.radarDebugging || !PMWClientConfig.disableOverlaysWhenDebugging) {
            RadarOverlays.renderOverlays(radarRenderData, canRender);
        }

        poseStack.popPose();
//
//        if (multiBufferSource instanceof MultiBufferSource.BufferSource bs) {
//            RadarRenderTypes.flushOverlayCache(bs);
//        }

//        matrix4fStack.mul(poseStack.last().pose().invert());
//        matrix4fStack.translate(-0.5F, -1.05F, -0.5F);
//        matrix4fStack.popMatrix();

//        RenderSystem.applyModelViewMatrix();
    }

    @Unique
    private static void pmwapi$renderRadarFrameCheck(BlockEntity blockEntity, float partialTicks) {
        boolean paused = Minecraft.getInstance().isPaused();
        double currentFrameId = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() + partialTicks : 0;
        long now = System.nanoTime();
        boolean shouldReset = false;

        if (paused) {
            if (now - pmwapi$lastNanoTime > 2_000_000) shouldReset = true;
        } else {
            if (Math.abs(currentFrameId - pmwapi$lastFrameId) > 0.001) shouldReset = true;
        }

        if (shouldReset) {
            RenderedRadars = 0;
            pmwapi$lastFrameId = currentFrameId;
            pmwapi$lastNanoTime = now;
        }
    }

    @Unique
    private void pmwapi$renderQuad(VertexConsumer vc, float qx, float qy, float qz, float scale, PoseStack poseStack, int color, int cli) {
        PoseStack.Pose pose = poseStack.last();
        vc.addVertex(pose.pose(), -scale + qx, qy, -scale + qz)
                .setColor(color)
                .setLight(cli)
                .setNormal(pose, 0.0f, 1.0f, 0.0f);
        vc.addVertex(pose.pose(), -scale + qx, qy, scale + qz)
                .setColor(color)
                .setLight(cli)
                .setNormal(pose, 0.0f, 1.0f, 0.0f);
        vc.addVertex(pose.pose(), scale + qx, qy, scale + qz)
                .setColor(color)
                .setLight(cli)
                .setNormal(pose, 0.0f, 1.0f, 0.0f);
        vc.addVertex(pose.pose(), scale + qx, qy, -scale + qz)
                .setColor(color)
                .setLight(cli)
                .setNormal(pose, 0.0f, 1.0f, 0.0f);
    }

    @Unique
    private int pmwapi$getX(int idx, int resolution) {
        return idx / (2 * resolution + 1) - resolution;
    }

    @Unique
    private int pmwapi$getZ(int idx, int resolution) {
        return idx % (2 * resolution + 1) - resolution;
    }

    @Unique
    private boolean pmwapi$testForWSR(BlockEntity blockEntity) {
        boolean canRender = false;
        int searchrange = 64;
        boolean shouldSearch = false;
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        // PMWeatherAPI: Minor optimization, Create Radar -> WSR-88D lookup to not do up to 64^3 level#getBlockState calls EVERY 3 SECONDS
        if (PMWExtras.RADAR_WSR_88D_LOOKUP.containsKey(pos)) {
            BlockEntity wsr88D = level.getBlockEntity(PMWExtras.RADAR_WSR_88D_LOOKUP.get(pos));
            if (wsr88D != null && wsr88D.getBlockState().getBlock() instanceof WSR88DCore wsr88DCore) {
                if (wsr88DCore.isComplete(wsr88D.getBlockState())) {
                    return true;
                } else {
                    PMWExtras.RADAR_WSR_88D_LOOKUP.remove(pos);
                    shouldSearch = true;
                }
            } else {
                PMWExtras.RADAR_WSR_88D_LOOKUP.remove(pos);
                shouldSearch = true;
            }
        } else shouldSearch = true;

        if (shouldSearch) {
            for (int x = -searchrange; x <= searchrange && !canRender; ++x) {
                for (int y = -searchrange; y <= searchrange && !canRender; ++y) {
                    for (int z = -searchrange * 2; z <= searchrange * 2; ++z) {
                        BlockState state = level.getBlockState(pos.offset(x, y, z));
                        Block var26 = state.getBlock();
                        if (var26 instanceof WSR88DCore core) {
                            if (core.isComplete(state)) {
                                canRender = true;
                                PMWExtras.RADAR_WSR_88D_LOOKUP.put(pos, pos.offset(x, y, z));
                                break;
                            }
                        }
                    }
                }
            }
        }

        return canRender;
    }

    @Unique
    private static float pmwapi$getDBZForStorm(RadarBlockEntity radarBlockEntity, Storm storm, Vec3 worldpos, float dbz) {
        if (!storm.visualOnly && storm.hasRadarRepresentation()) {
            double renderRange = storm.getRadarRenderRange();
            double dist = storm.position.multiply(1.0F, 0.0F, 1.0F).distanceTo(worldpos);

            if (dist < renderRange) return Math.max(dbz, storm.getRadarReflectivityReturn(radarBlockEntity, worldpos));
        }

        return dbz;
    }

    @Unique
    private int pmwapi$getCTPixelColor(RadarBlockEntity radarBlockEntity, float rdbz, int terrainCol, float temp, RadarMode radarMode, float vel) {
        Color color;
        color = ColorTables.getReflectivity(rdbz, new Color(terrainCol));

        if (rdbz > 5.0F && !radarBlockEntity.hasRangeUpgrade) {
            if (temp < 3.0F && temp > -1.0F) {
                color = ColorTables.getMixedReflectivity(rdbz);
            } else if (temp <= -1.0F) {
                color = ColorTables.getSnowReflectivity(rdbz);
            }
        }

        if (radarMode == RadarMode.VELOCITY) {
            color = new Color(0, 0, 0);
            vel /= 1.75F;
            color = ColorTables.lerp(Mth.clamp(Math.max(rdbz, (Mth.abs(vel) - 18.0F) / 0.65F) / 12.0F, 0.0F, 1.0F), color, ColorTables.getVelocity(vel));
        }

        if (radarMode == RadarMode.IR) {
            float ir = rdbz * 10.0F;
            if (rdbz > 10.0F) {
                ir = 100.0F + (rdbz - 10.0F) * 2.5F;
            }

            if (rdbz > 50.0F) {
                ir += (rdbz - 50.0F) * 5.0F;
            }

            color = ColorTables.getIR(ir);
        }

        return color.getRGB();
    }

    @Unique
    private Color pmwapi$getClientDebugColor(RadarBlockEntity radarBlockEntity, ClientConfig.RadarMode clientRadarMode, double wx, double wy, double wz, Color def, int x, int z, float resolution, BlockPos pos, List<Storm> storms) {
        if (clientRadarMode == ClientConfig.RadarMode.TEMPERATURE) {
            float t = ThermodynamicEngine.samplePoint(GameBusClientEvents.weatherHandler, new Vec3(wx, 0.0F, wz), radarBlockEntity.getLevel(), radarBlockEntity, 0).temperature();
            if (t <= 0.0F) {
                return ColorTables.lerp(Math.clamp(t / -40.0F, 0.0F, 1.0F), new Color(153, 226, 251, 255), new Color(29, 53, 221, 255));
            } else if (t < 15.0F) {
                return ColorTables.lerp(Math.clamp(t / 15.0F, 0.0F, 1.0F), new Color(255, 255, 255, 255), new Color(225, 174, 46, 255));
            } else {
                return ColorTables.lerp(Math.clamp((t - 15.0F) / 25.0F, 0.0F, 1.0F), new Color(225, 174, 46, 255), new Color(232, 53, 14, 255));
            }
        }

        if (clientRadarMode == ClientConfig.RadarMode.SST) {
            Float t = ThermodynamicEngine.GetSST(GameBusClientEvents.weatherHandler, new Vec3(wx, 0.0F, wz), radarBlockEntity.getLevel(), radarBlockEntity, 0);
            if (t == null) {
                return Color.BLACK;
            } else {
                return PMWClientConfig.useOriginalPMWeatherRendering
                    ? ColorTables.getSST(t)
                    : new Color(ColorMaps.SST.get(t));
            }
        }

        if (clientRadarMode == ClientConfig.RadarMode.WINDFIELDS && GameBusClientEvents.weatherHandler != null) {
            Vec3 wP = (new Vec3(x, 0.0F, z)).multiply(1.0F / resolution, 0.0F, 1.0F / resolution).multiply(256.0F, 0.0F, 256.0F).add(pos.getCenter());
            float wind = 0.0F;

            for(Storm storm : storms) {
                wind += storm.getTornadicWind(wP);
            }

            return PMWClientConfig.useOriginalPMWeatherRendering
                ? ColorTables.getWindspeed(wind)
                : new Color(ColorMaps.WINDSPEED.get(wind));
        }

        if (clientRadarMode == ClientConfig.RadarMode.GLOBALWINDS && GameBusClientEvents.weatherHandler != null) {
            int height = GameBusClientEvents.weatherHandler.getWorld().getHeight(Heightmap.Types.MOTION_BLOCKING, (int) wx, (int) wz);
            float wind = (float)WindEngine.getWind(new Vec3(wx, height, wz), GameBusClientEvents.weatherHandler.getWorld(), false, false, false, true).length();

            return PMWClientConfig.useOriginalPMWeatherRendering
                ? ColorTables.getHurricaneWindspeed(wind)
                : new Color(ColorMaps.HURRICANE_WINDSPEED.get(wind));
        }

        Vec3 worldPos = new Vec3(wx, wy, wz);
        if (clientRadarMode == ClientConfig.RadarMode.CAPE) {
            Sounding sounding = new Sounding(GameBusClientEvents.weatherHandler, worldPos, radarBlockEntity.getLevel(), 500, 12000, radarBlockEntity);
            Sounding.CAPE CAPE = sounding.getCAPE(sounding.getSBParcel());
            return ColorTables.lerp(Mth.clamp(CAPE.CAPE() / 6000.0F, 0.0F, 1.0F), new Color(0, 0, 0), new Color(255, 0, 0));
        }

        if (clientRadarMode == ClientConfig.RadarMode.CAPE3KM) {
            Sounding sounding = new Sounding(GameBusClientEvents.weatherHandler, worldPos, radarBlockEntity.getLevel(), 250, 4000, radarBlockEntity);
            Sounding.CAPE CAPE = sounding.getCAPE(sounding.getSBParcel());
            return ColorTables.lerp(Mth.clamp(CAPE.CAPE3() / 1000.0F, 0.0F, 1.0F), new Color(0, 0, 0), new Color(255, 0, 0));
        }

        if (clientRadarMode == ClientConfig.RadarMode.CINH) {
            Sounding sounding = new Sounding(GameBusClientEvents.weatherHandler, worldPos, radarBlockEntity.getLevel(), 500, 12000, radarBlockEntity);
            Sounding.CAPE CAPE = sounding.getCAPE(sounding.getSBParcel());
            return ColorTables.lerp(Mth.clamp(CAPE.CINH() / -250.0F, 0.0F, 1.0F), new Color(0, 0, 0), new Color(0, 0, 255));
        }

        if (clientRadarMode == ClientConfig.RadarMode.LAPSERATE03) {
            Sounding sounding = new Sounding(GameBusClientEvents.weatherHandler, worldPos, radarBlockEntity.getLevel(), 250, 4000, radarBlockEntity);
            float lapse = (float)Math.floor(sounding.getLapseRate(0, 3000) * 2.0F) / 2.0F;
            if (lapse > 5.0F) {
                return ColorTables.lerp(Mth.clamp((lapse - 5.0F) / 5.0F, 0.0F, 1.0F), new Color(255, 255, 0), new Color(255, 0, 0));
            } else {
                return ColorTables.lerp(Mth.clamp(lapse / 5.0F, 0.0F, 1.0F), new Color(0, 255, 0), new Color(255, 255, 0));
            }
        }

        if (clientRadarMode == ClientConfig.RadarMode.LAPSERATE36) {
            Sounding sounding = new Sounding(GameBusClientEvents.weatherHandler, worldPos, radarBlockEntity.getLevel(), 250, 7000, radarBlockEntity);
            float lapse = (float)Math.floor(sounding.getLapseRate(3000, 6000) * 2.0F) / 2.0F;
            if (lapse > 5.0F) {
                return ColorTables.lerp(Mth.clamp((lapse - 5.0F) / 5.0F, 0.0F, 1.0F), new Color(255, 255, 0), new Color(255, 0, 0));
            } else {
                return ColorTables.lerp(Mth.clamp(lapse / 5.0F, 0.0F, 1.0F), new Color(0, 255, 0), new Color(255, 255, 0));
            }
        }

        return def;
    }
}