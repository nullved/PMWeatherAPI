package net.nullved.pmweatherapi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.protomanly.pmweather.weather.Storm;
import dev.protomanly.pmweather.weather.Vorticy;
import net.neoforged.neoforge.common.NeoForge;
import net.nullved.pmweatherapi.event.VorticyEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Vorticy.class)
public class VorticyMixin {
//    @Shadow
//    public boolean dead;
//
//    @Inject(method = "<init>", at = @At("TAIL"))
//    public void newVorticy(Storm storm, float maxWindspeedMult, float widthPerc, float distancePerc, int lifetime, CallbackInfo ci) {
//        NeoForge.EVENT_BUS.post(new VorticyEvent.New(storm.vorticies.get(storm.vorticies.indexOf(this)));
//    }
//
//    @Inject(method = "tick", at = @At("TAIL"))
//    public void tick(CallbackInfo ci) {
//        if (dead) {
//            NeoForge.EVENT_BUS.post(new VorticyEvent.Dead(this));
//        }
//    }
//
//    @WrapOperation(method = "tick", at = @At(value = "FIELD", target = "Ldev/protomanly/pmweather/weather/Vorticy;dead:Z", opcode = Opcodes.GETFIELD))
//    public boolean deadVorticy(Vorticy vorticy, Operation<Boolean> original) {
//        if (vorticy.dead) {
//            NeoForge.EVENT_BUS.post(new VorticyEvent.Dead(vorticy));
//        }
//        return original.call(vorticy);
//    }
}
