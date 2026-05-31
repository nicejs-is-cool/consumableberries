package nl.codersquack.nicejs.ccb.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nl.codersquack.nicejs.ccb.ConsumableBerries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
    @Inject(method = "updateUsingItem", at = @At(value = "HEAD"))
    protected void updateUsingItem(ItemStack usingItem, CallbackInfo ci) {
        /*ConsumableBerries.LOGGER.info("this.useItemRemaining={}, serverSide={}, !useOnRelease={}", ((LivingEntity) (Object) this).getUseItemRemainingTicks(), !this.level().isClientSide, !usingItem.useOnRelease());
        if (!this.level().isClientSide) {
            ConsumableBerries.LOGGER.info("Stacktrace: {}", Arrays.toString(Thread.currentThread().getStackTrace()));
        }*/
    }
}
