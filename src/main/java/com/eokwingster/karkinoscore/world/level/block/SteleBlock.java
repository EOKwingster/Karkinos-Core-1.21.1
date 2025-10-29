package com.eokwingster.karkinoscore.world.level.block;

import com.eokwingster.karkinoscore.client.gui.screen.SteleBlockScreen;
import com.eokwingster.karkinoscore.util.KCUtils;
import com.eokwingster.karkinoscore.world.level.block.entity.SteleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class SteleBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<StelePart> PART;
    public static final EnumProperty<SteleType> TYPE;
    public static final VoxelShape SHAPE_BOTTOM_NS;
    public static final VoxelShape SHAPE_BOTTOM_EW;
    public static final VoxelShape SHAPE_TOP_NS;
    public static final VoxelShape SHAPE_TOP_EW;
    public static final VoxelShape SHAPE_SIDE_BOTTOM_NORTH;
    public static final VoxelShape SHAPE_SIDE_BOTTOM_SOUTH;
    public static final VoxelShape SHAPE_SIDE_BOTTOM_EAST;
    public static final VoxelShape SHAPE_SIDE_BOTTOM_WEST;
    public static final VoxelShape SHAPE_SIDE_TOP_NORTH;
    public static final VoxelShape SHAPE_SIDE_TOP_SOUTH;
    public static final VoxelShape SHAPE_SIDE_TOP_EAST;
    public static final VoxelShape SHAPE_SIDE_TOP_WEST;

    protected SteleBlock(Properties properties, SteleType type) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PART, StelePart.BOTTOM));
        this.registerDefaultState(this.getStateDefinition().any().setValue(TYPE, type));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            String messageKey = ((SteleBlockEntity) level.getBlockEntity(getBottomPos(state, pos))).getMessageKey();
            Minecraft.getInstance().setScreen(new SteleBlockScreen(messageKey, state.getValue(TYPE)));
        }
        return InteractionResult.SUCCESS;
    }

    private BlockPos getBottomPos(BlockState state, BlockPos pos) {
        return switch (state.getValue(PART)) {
            case BOTTOM -> pos;
            case TOP -> pos.below();
            case SIDE_BOTTOM -> pos.relative(state.getValue(FACING), 1);
            case SIDE_TOP -> pos.relative(state.getValue(FACING), 1).below();
        };
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative() && state.getValue(PART) != StelePart.BOTTOM) {
            BlockPos bottomPos = getBottomPos(state, pos);
            BlockState bottomState = level.getBlockState(bottomPos);
            if (bottomState.is(this)) {
                level.setBlock(bottomPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, bottomPos, Block.getId(bottomState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        StelePart part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        if (!shouldCheckPositions(part, direction, facing)) {
            return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }
        if (neighborState.is(this)) {
            StelePart neighborPart = neighborState.getValue(PART);
            Direction neighborFacing = neighborState.getValue(FACING);
            if (checkNeighbor(part, neighborPart, direction, facing, neighborFacing)) {
                return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    private boolean shouldCheckPositions(StelePart part, Direction direction, Direction facing) {
        return switch (part) {
            case BOTTOM -> direction.getAxis() != facing.getAxis() && direction != Direction.DOWN;
            case TOP -> direction.getAxis() != facing.getAxis() && direction != Direction.UP;
            case SIDE_BOTTOM -> direction == facing || direction == Direction.UP;
            case SIDE_TOP -> direction == facing || direction == Direction.DOWN;
        };
    }

    private boolean checkNeighbor(StelePart part, StelePart neighborPart, Direction direction, Direction facing, Direction neighborFacing) {
        if (direction.getAxis() == Direction.Axis.Y) {
            Direction directionCheck = switch (part) {
                case BOTTOM, SIDE_BOTTOM -> Direction.UP;
                case TOP, SIDE_TOP -> Direction.DOWN;
            };
            StelePart partCheck = switch (part) {
                case BOTTOM -> StelePart.TOP;
                case TOP -> StelePart.BOTTOM;
                case SIDE_BOTTOM -> StelePart.SIDE_TOP;
                case SIDE_TOP -> StelePart.SIDE_BOTTOM;
            };
            return facing == neighborFacing && direction == directionCheck && neighborPart == partCheck;
        } else {
            if (part == StelePart.BOTTOM || part == StelePart.TOP) {
                if (direction.getAxis() == facing.getClockWise().getAxis() && neighborFacing == direction.getOpposite()) {
                    return neighborPart == (part == StelePart.BOTTOM ? StelePart.SIDE_BOTTOM : StelePart.SIDE_TOP);
                }
            } else {
                if (direction.getAxis() == neighborFacing.getClockWise().getAxis() && facing == direction) {
                    return neighborPart == (part == StelePart.SIDE_BOTTOM ? StelePart.BOTTOM : StelePart.TOP);
                }
            }
            return false;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction bottomFacing = context.getHorizontalDirection().getOpposite();
        BlockPos bottomPos = context.getClickedPos();
        BlockState bottomState = this.defaultBlockState().setValue(FACING, bottomFacing);
        List<BlockPos> positions = getPartsPositionsFromBottomInOrder(bottomState, bottomPos);
        for (BlockPos pos : positions) {
            if (!context.getLevel().getBlockState(pos).canBeReplaced(context)) {
                return null;
            }
        }
        return bottomState;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        StelePart part = state.getValue(PART);
        return switch (part) {
            case BOTTOM -> facing.getAxis() == Direction.Axis.X ? SHAPE_BOTTOM_EW : SHAPE_BOTTOM_NS;
            case TOP -> facing.getAxis() == Direction.Axis.X ? SHAPE_TOP_EW : SHAPE_TOP_NS;
            case SIDE_BOTTOM -> switch (facing) {
                case NORTH -> SHAPE_SIDE_BOTTOM_NORTH;
                case SOUTH -> SHAPE_SIDE_BOTTOM_SOUTH;
                case EAST -> SHAPE_SIDE_BOTTOM_EAST;
                case WEST -> SHAPE_SIDE_BOTTOM_WEST;
                default -> Shapes.empty();
            };
            case SIDE_TOP -> switch (facing) {
                case NORTH -> SHAPE_SIDE_TOP_NORTH;
                case SOUTH -> SHAPE_SIDE_TOP_SOUTH;
                case EAST -> SHAPE_SIDE_TOP_EAST;
                case WEST -> SHAPE_SIDE_TOP_WEST;
                default -> Shapes.empty();
            };
        };
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, TYPE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        StelePart part = blockState.getValue(PART);
        return part == StelePart.BOTTOM || part == StelePart.TOP ? new SteleBlockEntity(blockPos, blockState): null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            List<BlockPos> positions = getPartsPositionsFromBottomInOrder(state, pos);
            List<BlockState> states = getPartsStatesFromBottomInOrder(state);
            for (int i = 1; i < 6; i++) {
                level.setBlock(positions.get(i), states.get(i), 3);
            }
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    // order facing the north: bottom, top, right side bottom, right side top, left side bottom, left side top
    private List<BlockPos> getPartsPositionsFromBottomInOrder(BlockState bottomState, BlockPos bottomPos) {
        Direction bottomFacing = bottomState.getValue(FACING);
        return List.of(
                bottomPos,
                bottomPos.above(),
                bottomPos.relative(bottomFacing.getClockWise()),
                bottomPos.relative(bottomFacing.getClockWise()).above(),
                bottomPos.relative(bottomFacing.getCounterClockWise()),
                bottomPos.relative(bottomFacing.getCounterClockWise()).above()
        );
    }

    private List<BlockState> getPartsStatesFromBottomInOrder(BlockState bottomState) {
        Direction bottomFacing = bottomState.getValue(FACING);
        return List.of(
                bottomState.setValue(PART, StelePart.BOTTOM).setValue(FACING, bottomFacing),
                bottomState.setValue(PART, StelePart.TOP).setValue(FACING, bottomFacing),
                bottomState.setValue(PART, StelePart.SIDE_BOTTOM).setValue(FACING, bottomFacing.getCounterClockWise()),
                bottomState.setValue(PART, StelePart.SIDE_TOP).setValue(FACING, bottomFacing.getCounterClockWise()),
                bottomState.setValue(PART, StelePart.SIDE_BOTTOM).setValue(FACING, bottomFacing.getClockWise()),
                bottomState.setValue(PART, StelePart.SIDE_TOP).setValue(FACING, bottomFacing.getClockWise())
        );
    }

    @Override
    protected long getSeed(BlockState state, BlockPos pos) {
        return Mth.getSeed(getBottomPos(state, pos));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    static {
        PART = EnumProperty.create("part", StelePart.class);
        TYPE = EnumProperty.create("type", SteleType.class);
        SHAPE_BOTTOM_NS = Shapes.box(0, 0, 0.1875, 1, 1, 0.8125);
        SHAPE_BOTTOM_EW = KCUtils.rotateShape(SHAPE_BOTTOM_NS, Rotation.CLOCKWISE_90);
        SHAPE_TOP_NS = Shapes.or(
                Shapes.box(0, 0, 0.1875, 1, 0.875, 0.8125),
                Shapes.box(0, 0.875, 0.3125, 1, 1, 0.6875)
        );
        SHAPE_TOP_EW = KCUtils.rotateShape(SHAPE_TOP_NS, Rotation.CLOCKWISE_90);
        SHAPE_SIDE_BOTTOM_NORTH = Shapes.box(0.3125, 0, 0, 0.6875, 1, 0.125);
        SHAPE_SIDE_BOTTOM_EAST = KCUtils.rotateShape(SHAPE_SIDE_BOTTOM_NORTH, Rotation.CLOCKWISE_90);
        SHAPE_SIDE_BOTTOM_SOUTH = KCUtils.rotateShape(SHAPE_SIDE_BOTTOM_NORTH, Rotation.CLOCKWISE_180);
        SHAPE_SIDE_BOTTOM_WEST = KCUtils.rotateShape(SHAPE_SIDE_BOTTOM_NORTH, Rotation.COUNTERCLOCKWISE_90);
        SHAPE_SIDE_TOP_NORTH = Shapes.box(0.3125, 0, 0, 0.6875, 0.875, 0.125);
        SHAPE_SIDE_TOP_EAST = KCUtils.rotateShape(SHAPE_SIDE_TOP_NORTH, Rotation.CLOCKWISE_90);
        SHAPE_SIDE_TOP_SOUTH = KCUtils.rotateShape(SHAPE_SIDE_TOP_NORTH, Rotation.CLOCKWISE_180);
        SHAPE_SIDE_TOP_WEST = KCUtils.rotateShape(SHAPE_SIDE_TOP_NORTH, Rotation.COUNTERCLOCKWISE_90);
    }

    public enum StelePart implements StringRepresentable {
        BOTTOM("bottom"),
        TOP("top"),
        SIDE_BOTTOM("side_bottom"),
        SIDE_TOP("side_top");

        private final String name;

        StelePart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum SteleType implements StringRepresentable {
        BLOOD("blood"),
        MARBLE("marble");

        private final String name;

        SteleType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
