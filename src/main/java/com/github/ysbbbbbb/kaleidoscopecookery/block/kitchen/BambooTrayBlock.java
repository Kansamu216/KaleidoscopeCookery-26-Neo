package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IBambooTray;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRegistrationProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Bamboo tray: a 2-pixel-thick tray with four slots. Right-clicking the top face puts an item into
 * (or takes it out of) the slot the player clicked. When a second tray is placed underneath, the
 * {@code stand} property flips so the renderer can show the raised "stand" model.
 *
 * <p>Ported from 1.20.1: {@code use(...)} becomes {@code useItemOn(...)}, block properties come from
 * {@link ModRegistrationProperties}, and the 26.x {@link LevelReader}/{@link ScheduledTickAccess}
 * update-shape signature is used.</p>
 */
public class BambooTrayBlock extends Block implements EntityBlock {
   public static final BooleanProperty STAND = BooleanProperty.create("stand");
   private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);

   public BambooTrayBlock() {
      super(ModRegistrationProperties.blockProperties()
            .mapColor(MapColor.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .strength(0.5F)
            .sound(SoundType.BAMBOO)
            .noOcclusion()
            .pushReaction(PushReaction.POPPED)
            .ignitedByLava());
      this.registerDefaultState(this.stateDefinition.any().setValue(STAND, false));
   }

   @Override
   public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                      InteractionHand hand, BlockHitResult hitResult) {
      if (hand != InteractionHand.MAIN_HAND || hitResult.getDirection() != Direction.UP) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      }
      if (!(level.getBlockEntity(pos) instanceof IBambooTray tray)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      }
      // Never place the tray onto itself as an item
      if (player.getMainHandItem().is(this.asItem())) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      }

      double localX = hitResult.getLocation().x - pos.getX();
      double localZ = hitResult.getLocation().z - pos.getZ();
      int slot = (localZ >= 0.5 ? 2 : 0) + (localX >= 0.5 ? 1 : 0);

      ItemStack held = player.getMainHandItem();
      if (held.isEmpty()) {
         tray.onTakeOut(level, player, slot, player.isShiftKeyDown());
      } else {
         tray.onPutItem(level, player, held, slot);
      }
      return InteractionResult.SUCCESS;
   }

   @Override
   public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                 Direction direction, BlockPos neighborPos, BlockState neighborState,
                                 net.minecraft.util.RandomSource random) {
      if (direction == Direction.DOWN) {
         return state.setValue(STAND, neighborState.is(this));
      }
      return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(STAND);
   }

   @Override
   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos below = context.getClickedPos().below();
      boolean sameBlock = context.getLevel().getBlockState(below).is(this);
      return super.defaultBlockState().setValue(STAND, sameBlock);
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @Override
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BambooTrayBlockEntity(pos, state);
   }

   @Override
   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      if (level.isClientSide()) {
         return null;
      }
      return (lvl, pos, blockState, blockEntity) -> {
         if (blockEntity instanceof BambooTrayBlockEntity tray) {
            BambooTrayBlockEntity.serverTick(lvl, pos, blockState, tray);
         }
      };
   }

   @Override
   public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
      List<ItemStack> drops = super.getDrops(state, builder);
      BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (blockEntity instanceof BambooTrayBlockEntity tray) {
         for (ItemStack stack : tray.getItems()) {
            if (!stack.isEmpty()) {
               drops.add(stack.copy());
            }
         }
      }
      return drops;
   }
}
