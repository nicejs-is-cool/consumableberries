package nl.codersquack.nicejs.ccb.mixin;

import com.cobblemon.mod.common.block.BerryBlock;
import com.cobblemon.mod.common.item.berry.BerryItem;
import com.cobblemon.mod.common.item.berry.StatusCuringBerryItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nl.codersquack.nicejs.ccb.ConsumableBerries;
import nl.codersquack.nicejs.ccb.PSIHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StatusCuringBerryItem.class, remap = false)
public abstract class StatusCuringBerryItemMixin extends BerryItem {
    public StatusCuringBerryItemMixin(@NotNull BerryBlock berryBlock) {
        super(berryBlock);
    }
    // The function below was mostly ported from Cobblemon with some tweaks.
    @Inject(method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", cancellable = true, at = @At(value = "HEAD"))
    public void useRedirect(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        //ConsumableBerries.LOGGER.info("Overriding StatusCuringBerryItem::use");
        if (user instanceof ServerPlayer splayer) {
             InteractionResultHolder<ItemStack> inter = ((StatusCuringBerryItem)(Object) this).use(splayer, user.getItemInHand(hand));

            if (inter == null) { // ts so ass
                //ConsumableBerries.LOGGER.info("caught the null, redirecting to super.use");
                cir.setReturnValue(super.use(world, user, hand));
                cir.cancel();
                return;
            }
            cir.setReturnValue(inter);
            cir.cancel();

            return;
        }
        if (PSIHelper.INSTANCE.willInteractWithPokemon(user, user.getItemInHand(hand), ((StatusCuringBerryItem)(Object) this).getBagItem())) {
            cir.setReturnValue(InteractionResultHolder.success(user.getItemInHand(hand)));
            cir.cancel();
            return;
        }
        cir.setReturnValue(super.use(world, user, hand));
        cir.cancel();
    }
}
