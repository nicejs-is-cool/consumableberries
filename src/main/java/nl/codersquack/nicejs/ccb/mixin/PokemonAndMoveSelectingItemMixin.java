package nl.codersquack.nicejs.ccb.mixin;

import com.cobblemon.mod.common.api.item.PokemonAndMoveSelectingItem;
import com.cobblemon.mod.common.item.berry.BerryItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = com.cobblemon.mod.common.api.item.PokemonAndMoveSelectingItem.class, remap = false)
public interface PokemonAndMoveSelectingItemMixin extends PokemonAndMoveSelectingItem {
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/item/PokemonAndMoveSelectingItem;interactGeneral(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/InteractionResultHolder;"), cancellable = true)
    public default void useFix(ServerPlayer player, ItemStack stack, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (stack.getItem() instanceof BerryItem) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}
