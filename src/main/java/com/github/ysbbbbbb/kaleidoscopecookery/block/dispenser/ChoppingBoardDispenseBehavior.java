package com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IChoppingBoard;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Dispenser behavior for the chopping board.
 * When a knife is dispensed it cuts the item currently on the board; when a
 * choppable food item is dispensed it is placed on the board.
 * Mirrors the official 1.20.1 logic, adapted to the 26.1.2 dispenser API.
 */
public class ChoppingBoardDispenseBehavior extends OptionalDispenseItemBehavior {
    private final boolean knife;

    public ChoppingBoardDispenseBehavior(boolean knife) {
        this.knife = knife;
    }

    @Override
    protected @NotNull ItemStack execute(BlockSource source, ItemStack stack) {
        this.setSuccess(false);
        Direction facing = source.state().getValue(DispenserBlock.FACING);
        BlockPos boardPos = source.pos().relative(facing);
        BlockEntity blockEntity = source.level().getBlockEntity(boardPos);
        if (blockEntity instanceof IChoppingBoard board) {
            boolean success = this.knife
                    ? board.onCutItem(source.level(), null, stack)
                    : board.onPutItem(source.level(), null, stack);
            if (success) {
                this.setSuccess(true);
                if (!this.knife) {
                    stack.shrink(1);
                }
            }
        }
        return stack;
    }

    /** Registers the knife and choppable-food dispenser behaviors, mirroring upstream 1.7.0. */
    public static void register() {
        DispenserBlock.registerBehavior((net.minecraft.world.level.ItemLike)ModItems.IRON_KITCHEN_KNIFE.get(), new ChoppingBoardDispenseBehavior(true));
        DispenserBlock.registerBehavior((net.minecraft.world.level.ItemLike)ModItems.GOLD_KITCHEN_KNIFE.get(), new ChoppingBoardDispenseBehavior(true));
        DispenserBlock.registerBehavior((net.minecraft.world.level.ItemLike)ModItems.DIAMOND_KITCHEN_KNIFE.get(), new ChoppingBoardDispenseBehavior(true));
        DispenserBlock.registerBehavior((net.minecraft.world.level.ItemLike)ModItems.NETHERITE_KITCHEN_KNIFE.get(), new ChoppingBoardDispenseBehavior(true));

        // Vanilla dispenser lookup is per item, so choppable food has to be enumerated.
        ChoppingBoardDispenseBehavior foodBehavior = new ChoppingBoardDispenseBehavior(false);
        Item[] choppingIngredients = {
            Items.TROPICAL_FISH,
            Items.COD, Items.COOKED_COD,
            Items.SALMON, Items.COOKED_SALMON,
            Items.MUTTON, Items.COOKED_MUTTON,
            Items.BEEF, Items.COOKED_BEEF,
            Items.PORKCHOP, Items.COOKED_PORKCHOP,
            Items.CHICKEN, Items.COOKED_CHICKEN,
            Items.RABBIT, Items.COOKED_RABBIT,
            ModItems.RAW_DOUGH.get()
        };
        for (Item item : choppingIngredients) {
            DispenserBlock.registerBehavior(item, foodBehavior);
        }
    }
}
