package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRegistrationProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EightImmortalsTableBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    private static final VoxelShape SELECTION_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape COLLISION_SHAPE = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);

    public EightImmortalsTableBlock() {
        super(ModRegistrationProperties.blockProperties()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.BAMBOO_WOOD)
                .noOcclusion()
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, Part.RIGHT_BOTTOM));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockState state = this.defaultBlockState().setValue(FACING, facing);
        BlockPos anchor = context.getClickedPos();
        Level level = context.getLevel();

        // Conservative placement check: every occupied part must be replaceable.
        for (Part part : Part.values()) {
            BlockPos partPos = getPartPos(anchor, facing, part);
            // Keep the anchor within the build height range.
            if (level.isOutsideBuildHeight(partPos) || !level.getBlockState(partPos).canBeReplaced(context)) {
                return null;
            }
        }
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Direction facing = state.getValue(FACING);
        for (Part part : Part.values()) {
            if (part != Part.RIGHT_BOTTOM) {
                level.setBlock(getPartPos(pos, facing, part), state.setValue(PART, part), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.player.Player player) {
        // Clean up the other three parts when any single part is broken.
        if (!level.isClientSide()) {
            Direction facing = state.getValue(FACING);
            BlockPos anchor = getAnchorPos(pos, facing, state.getValue(PART));
            for (Part part : Part.values()) {
                BlockPos partPos = getPartPos(anchor, facing, part);
                if (partPos.equals(pos)) {
                    continue;
                }
                BlockState partState = level.getBlockState(partPos);
                if (partState.is(this)
                        && partState.getValue(FACING) == facing
                        && partState.getValue(PART) == part) {
                    level.setBlock(partPos, Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SELECTION_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    private static BlockPos getPartPos(BlockPos anchor, Direction facing, Part part) {
        Direction left = facing.getCounterClockWise();
        return switch (part) {
            case RIGHT_BOTTOM -> anchor;
            case LEFT_BOTTOM -> anchor.relative(left);
            case RIGHT_TOP -> anchor.relative(facing);
            case LEFT_TOP -> anchor.relative(left).relative(facing);
        };
    }

    private static BlockPos getAnchorPos(BlockPos pos, Direction facing, Part part) {
        Direction right = facing.getClockWise();
        Direction bottom = facing.getOpposite();
        return switch (part) {
            case RIGHT_BOTTOM -> pos;
            case LEFT_BOTTOM -> pos.relative(right);
            case RIGHT_TOP -> pos.relative(bottom);
            case LEFT_TOP -> pos.relative(right).relative(bottom);
        };
    }

    public enum Part implements StringRepresentable {
        RIGHT_BOTTOM("right_bottom"),
        LEFT_BOTTOM("left_bottom"),
        RIGHT_TOP("right_top"),
        LEFT_TOP("left_top");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
