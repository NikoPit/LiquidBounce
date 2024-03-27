package net.ccbluex.liquidbounce.features.module.modules.world.scaffold

import net.ccbluex.liquidbounce.features.module.modules.player.invcleaner.ModuleInventoryCleaner
import net.ccbluex.liquidbounce.utils.client.player
import net.ccbluex.liquidbounce.utils.item.DISALLOWED_BLOCKS_TO_PLACE
import net.ccbluex.liquidbounce.utils.item.UNFAVORABLE_BLOCKS_TO_PLACE
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.FallingBlock
import net.minecraft.block.SideShapeType
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

object ScaffoldBlockItemSelection {

    fun isValidBlock(stack: ItemStack?): Boolean {
        if (stack == null) return false

        val item = stack.item

        if (item !is BlockItem) {
            return false
        }

        val block = item.block
        val defaultState = block.defaultState

        if (!defaultState.isSolidSurface(ModuleScaffold.world, BlockPos.ORIGIN, player, Direction.UP)) {
            return false
        }

        // We don't want to suicide...
        if (block is FallingBlock) {
            return false
        }

        return !DISALLOWED_BLOCKS_TO_PLACE.contains(block)
    }

    /**
     * Special handling for unfavourable blocks (like crafting tables, slabs, etc.):
     * - [ModuleScaffold]: Unfavourable blocks are only used when there is no other option left
     * - [ModuleInventoryCleaner]: Unfavourable blocks are not used as blocks by inv-cleaner.
     */
    fun isBlockUnfavourable(stack: ItemStack): Boolean {
        val item = stack.item

        if (item !is BlockItem)
            return true

        val block = item.block

        return when {
            // We dislike slippery blocks...
            block.slipperiness > 0.6F -> true
            // We dislike soul sand and slime...
            block.velocityMultiplier < 1.0F -> true
            // We hate honey...
            block.jumpVelocityMultiplier < 1.0F -> true
            // We don't want to place bee hives, chests, spawners, etc.
            block is BlockWithEntity -> true
            // We don't like slabs etc.
            !block.defaultState.isFullCube(ModuleScaffold.mc.world!!, BlockPos.ORIGIN) -> true
            // Is there a hard coded answer?
            else -> block in UNFAVORABLE_BLOCKS_TO_PLACE
        }
    }
}
