package nl.codersquack.nicejs.ccb

import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.battle.BagItem
import com.cobblemon.mod.common.util.*
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
// heavily simplified function from cobblemon so it can run on the client side
object PSIHelper {
    fun willInteractWithPokemon(player: Player, stack: ItemStack, bagItem: BagItem?): Boolean {
        val range = player.entityInteractionRange()
        val entity = player.level()
            .getEntities(player, AABB.ofSize(player.position(), range, range, range))
            .filter { player.isLookingAt(it, stepDistance = 0.1F) }
            .minByOrNull { it.distanceTo(player) } as? PokemonEntity?
        //ConsumableBerries.LOGGER.info("Is there a battle: {}", CobblemonClient.battle != null);
        CobblemonClient.battle?.let {
            return bagItem != null
        } ?: run {
            if (!player.isShiftKeyDown) {
                return entity != null && entity.ownerUUID == player.uuid
            }
        }
        /*player.getBattleState()?.let { (_, actor) ->
            return bagItem != null
        } ?: run {
            if (!player.isShiftKeyDown) {
                return entity != null && entity.ownerUUID == player.uuid
            }
        }*/

        return true
    }
}