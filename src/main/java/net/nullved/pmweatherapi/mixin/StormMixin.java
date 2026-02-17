package net.nullved.pmweatherapi.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.protomanly.pmweather.weather.Storm;
import dev.protomanly.pmweather.weather.Vorticy;
import net.neoforged.neoforge.common.NeoForge;
import net.nullved.pmweatherapi.event.StormEvent;
import net.nullved.pmweatherapi.event.VorticyEvent;
import org.checkerframework.checker.units.qual.A;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Storm.class)
public class StormMixin {
    @WrapOperation(method = "doGrowth", at = {
        @At(value = "FIELD", target = "Ldev/protomanly/pmweather/weather/Storm;stage:I", opcode = Opcodes.PUTFIELD, ordinal = 0),
        @At(value = "FIELD", target = "Ldev/protomanly/pmweather/weather/Storm;stage:I", opcode = Opcodes.PUTFIELD, ordinal = 1),
        @At(value = "FIELD", target = "Ldev/protomanly/pmweather/weather/Storm;stage:I", opcode = Opcodes.PUTFIELD, ordinal = 2),
    })
    public void stageChanged(Storm storm, int value, Operation<Void> original) {
        NeoForge.EVENT_BUS.post(new StormEvent.StageChanged(storm, value));
        original.call(storm, value);
    }

    @WrapOperation(method = "tick", at = {
        @At(value = "FIELD", target = "Ldev/protomanly/pmweather/weather/Storm;ticksSinceDying:I", opcode = Opcodes.PUTFIELD),
    })
    public void ticksSinceDyingChanged(Storm storm, int value, Operation<Void> original) {
        NeoForge.EVENT_BUS.post(new StormEvent.Dying(storm));
        original.call(storm, value);
    }

    @WrapOperation(method = "initFirstTime", at = {
        @At(value = "FIELD", target = "Ldev/protomanly/pmweather/weather/Storm;ID:J", opcode = Opcodes.PUTFIELD)
    })
    public void stormInitFirstTime(Storm storm, long value, Operation<Void> original) {
        NeoForge.EVENT_BUS.post(new StormEvent.New(storm));
        original.call(storm, value);
    }

    @WrapOperation(method = "remove", at = @At(value = "FIELD", target = "Ldev/protomanly/pmweather/weather/Storm;dead:Z", opcode = Opcodes.PUTFIELD))
    public void stormDead(Storm storm, boolean value, Operation<Void> original) {
        NeoForge.EVENT_BUS.post(new StormEvent.Dead(storm));
        original.call(storm, value);
    }
}
