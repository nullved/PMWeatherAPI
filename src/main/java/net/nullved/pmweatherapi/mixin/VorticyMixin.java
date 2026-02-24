package net.nullved.pmweatherapi.mixin;

import dev.protomanly.pmweather.weather.Storm;
import dev.protomanly.pmweather.weather.Vorticy;
import net.neoforged.neoforge.common.NeoForge;
import net.nullved.pmweatherapi.event.VorticyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Vorticy.class)
public class VorticyMixin {

    @Shadow
    public boolean dead;

    @Shadow
    private Storm storm;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void newVorticy(Storm storm, float maxWindspeedMult, float widthPerc, float distancePerc, int lifetime, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new VorticyEvent.New((Vorticy) (Object) this, storm.weatherHandler));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (dead) {
            NeoForge.EVENT_BUS.post(new VorticyEvent.Dead((Vorticy) (Object) this, this.storm.weatherHandler));
        }
    }
}
