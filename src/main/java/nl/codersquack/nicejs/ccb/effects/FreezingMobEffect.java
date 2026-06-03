package nl.codersquack.nicejs.ccb.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FreezingMobEffect extends MobEffect {
    public FreezingMobEffect(MobEffectCategory category, int color) {
        super(category, color);

    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.setIsInPowderSnow(true);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        return true;
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        super.onEffectAdded(livingEntity, amplifier);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        super.onEffectStarted(livingEntity, amplifier);
    }
}
