package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TeaBannerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRegistrationProperties;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * The tea banner block. The block exposes no blockstate properties (the
 * blockstate JSON only has an empty variant); the facing, color and pattern are
 * kept in the block entity. Client rendering is not ported yet - it will read
 * the data from the block entity.
 */
public class TeaBannerBlock extends Block implements EntityBlock {
   /** Hanging orientation: the banner either stands on the floor or is mounted on a wall. */
   public static final net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.world.level.block.state.properties.AttachFace> FACE
      = net.minecraft.world.level.block.state.properties.BlockStateProperties.ATTACH_FACE;
   public static final net.minecraft.world.level.block.state.properties.EnumProperty<Direction> FACING
      = net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
   private static final VoxelShape FLOOR_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
   private static final VoxelShape NORTH_SHAPE = Block.box(0.0, 7.0, 4.0, 16.0, 15.0, 12.0);
   private static final VoxelShape SOUTH_SHAPE = Block.box(0.0, 7.0, 4.0, 16.0, 15.0, 12.0);
   private static final VoxelShape WEST_SHAPE = Block.box(4.0, 7.0, 0.0, 12.0, 15.0, 16.0);
   private static final VoxelShape EAST_SHAPE = Block.box(4.0, 7.0, 0.0, 12.0, 15.0, 16.0);

   public TeaBannerBlock() {
      super(
         ModRegistrationProperties.blockProperties()
            .mapColor(MapColor.COLOR_RED)
            .strength(1.0F)
            .noCollision()
            .noOcclusion()
            .sound(SoundType.WOOD)
      );
      this.registerDefaultState(this.stateDefinition.any()
         .setValue(FACING, Direction.NORTH)
         .setValue(FACE, net.minecraft.world.level.block.state.properties.AttachFace.FLOOR));
   }

   public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
      ItemStack heldStack = stack;
      if (!(level.getBlockEntity(pos) instanceof TeaBannerBlockEntity teaBanner)) {
         return InteractionResult.PASS;
      }

      // Apply dye to recolor the banner.
      if (heldStack.getItem() instanceof DyeItem dyeItem) {
         if (teaBanner.getColor() == heldStack.get(net.minecraft.core.component.DataComponents.DYE)) {
            return InteractionResult.PASS;
         }
         if (!level.isClientSide()) {
            teaBanner.setColor(heldStack.get(net.minecraft.core.component.DataComponents.DYE));
            if (!player.hasInfiniteMaterials()) {
               heldStack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
         }
         return InteractionResult.SUCCESS;
      }

      // Apply a supported banner pattern item.
      if (TeaBannerBlockEntity.isSupportedPattern(heldStack)) {
         if (ItemStack.isSameItemSameComponents(heldStack, teaBanner.getPatternItem())) {
            return InteractionResult.PASS;
         }
         if (!level.isClientSide()) {
            ItemStack previousPattern = teaBanner.setPatternItem(heldStack.copyWithCount(1));
            if (!player.hasInfiniteMaterials()) {
               heldStack.shrink(1);
            }
            if (!previousPattern.isEmpty()) {
               popResource(level, pos, previousPattern);
            }
            level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
         }
         return InteractionResult.SUCCESS;
      }

      // Shears strip the pattern.
      if (heldStack.is(Items.SHEARS) && teaBanner.hasPattern()) {
         if (!level.isClientSide()) {
            popResource(level, pos, teaBanner.removePatternItem());
            heldStack.hurtAndBreak(1, player, hand);
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
         }
         return InteractionResult.SUCCESS;
      }

      return InteractionResult.PASS;
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TeaBannerBlockEntity teaBanner) {
         Direction facing = placer == null ? Direction.NORTH : placer.getDirection().getOpposite();
         if (facing.getAxis().isHorizontal()) {
            teaBanner.setFacing(facing);
         }
      }
   }


   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
      ItemStack cloneItemStack = super.getCloneItemStack(level, pos, state, includeData, player);
      level.getBlockEntity(pos, com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks.TEA_BANNER_BE.get()).ifPresent(be -> {
         TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
         be.saveCustomOnly(output);
         net.minecraft.world.item.BlockItem.setBlockEntityData(cloneItemStack, com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks.TEA_BANNER_BE.get(), output);
      });
      return cloneItemStack;
   }

   public List<ItemStack> getDrops(BlockState state, LootParams.Builder lootParamsBuilder) {
      List<ItemStack> drops = super.getDrops(state, lootParamsBuilder);
      BlockEntity parameter = (BlockEntity)lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (parameter instanceof TeaBannerBlockEntity teaBanner) {
         // Attach the stored color/pattern to the existing dropped banner stack
         // (do not add a new drop, the loot table already provides the item).
         ItemStack withData = teaBanner.createItemStack();
         drops.stream()
            .filter(stack -> stack.is((Item)ModItems.TEA_BANNER.get()))
            .findFirst()
            .ifPresent(stack -> stack.applyComponents(withData.getComponents()));
      }
      return drops;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(FACING, FACE);
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
      if (state.getValue(FACE) == net.minecraft.world.level.block.state.properties.AttachFace.FLOOR) {
         return FLOOR_SHAPE;
      }
      return switch (state.getValue(FACING)) {
         case SOUTH -> SOUTH_SHAPE;
         case WEST -> WEST_SHAPE;
         case EAST -> EAST_SHAPE;
         default -> NORTH_SHAPE;
      };
   }

   /** Matches upstream: floor (standing) or wall mounted; ceiling mounting is not allowed. */
   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      for (Direction direction : context.getNearestLookingDirections()) {
         BlockState candidate;
         if (direction.getAxis() == Direction.Axis.Y) {
            if (direction == Direction.UP) {
               continue; // ceiling mounted banners do not exist upstream
            }
            candidate = this.defaultBlockState()
               .setValue(FACE, net.minecraft.world.level.block.state.properties.AttachFace.FLOOR)
               .setValue(FACING, context.getHorizontalDirection());
         } else {
            candidate = this.defaultBlockState()
               .setValue(FACE, net.minecraft.world.level.block.state.properties.AttachFace.WALL)
               .setValue(FACING, direction.getOpposite());
         }
         if (this.canSurvive(candidate, context.getLevel(), context.getClickedPos())) {
            return candidate;
         }
      }
      return null;
   }

   private static Direction connectedDirection(BlockState state) {
      return switch (state.getValue(FACE)) {
         case FLOOR -> Direction.DOWN;
         case CEILING -> Direction.UP;
         case WALL -> state.getValue(FACING);
      };
   }

   @Override
   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction supportSide = connectedDirection(state).getOpposite();
      BlockPos supportPos = pos.relative(supportSide);
      return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, supportSide.getOpposite());
   }

   @Override
   protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                    Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
      return this.canSurvive(state, level, pos) ? super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighbourState, random)
         : Blocks.AIR.defaultBlockState();
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TeaBannerBlockEntity(pos, state);
   }
}
