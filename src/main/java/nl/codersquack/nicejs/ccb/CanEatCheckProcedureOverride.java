package nl.codersquack.nicejs.ccb;

import com.cobblemon.mod.common.api.item.PokemonAndMoveSelectingItem;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.item.berry.BerryItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import sol_valheim_reforged.procedures.CanEatCheckProcedure;

@EventBusSubscriber
public class CanEatCheckProcedureOverride {
    @SubscribeEvent
    public static void onStartUseItem(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player playerEntity) {
            ItemStack stack = event.getItem();
            ConsumableBerries.LOGGER.info("CECP override");
            if (stack.getItem() instanceof PokemonAndMoveSelectingItem berryItem) {
                ConsumableBerries.LOGGER.info("PAMSI override return");
                if (PSIHelper.INSTANCE.willInteractWithPokemon(playerEntity, stack, berryItem.getBagItem())) return;
            }
            if (stack.getItem() instanceof PokemonSelectingItem berryItem) {
                ConsumableBerries.LOGGER.info("PSI override return");
                if (PSIHelper.INSTANCE.willInteractWithPokemon(playerEntity, stack, berryItem.getBagItem())) return;
            }
        }
        ConsumableBerries.LOGGER.info("CECP start");
        CanEatCheckProcedure.onUseItemStart(event);
    }
}
