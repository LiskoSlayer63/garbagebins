package lizcraft.garbagebins.common.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseBinBlock extends Block implements EntityBlock, SimpleWaterloggedBlock
{
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	private static final VoxelShape LID_SHAPE = Block.box(1.0D, 14.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	private static final VoxelShape BODY_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);
	protected static final VoxelShape BASE_SHAPE = Shapes.or(BODY_SHAPE, LID_SHAPE);
	
	public BaseBinBlock(Properties properties) 
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(true)).setValue(WATERLOGGED, Boolean.valueOf(false)));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, POWERED, WATERLOGGED);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) 
	{
		return BASE_SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) 
	{
		Direction direction = context.getHorizontalDirection().getOpposite();
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		
		return this.defaultBlockState().setValue(FACING, direction).setValue(POWERED, Boolean.valueOf(true)).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) 
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState state, Direction p_51556_, BlockState p_51557_, LevelAccessor level, BlockPos p_51559_, BlockPos p_51560_) 
	{
		if (state.getValue(WATERLOGGED))
			level.scheduleTick(p_51559_, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		
		return super.updateShape(state, p_51556_, p_51557_, level, p_51559_, p_51560_);
	}
	
	@Override
	public BlockState rotate(BlockState p_54094_, Rotation p_54095_) 
	{
		return p_54094_.setValue(FACING, p_54095_.rotate(p_54094_.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState p_54091_, Mirror p_54092_) 
	{
		return p_54091_.rotate(p_54092_.getRotation(p_54091_.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState p_54110_, Level p_54111_, BlockPos p_54112_, BlockState p_54113_, boolean p_54114_) 
	{
		if (!p_54113_.is(p_54110_.getBlock()))
			this.checkPoweredState(p_54111_, p_54112_, p_54110_);
		
		super.onPlace(p_54110_, p_54111_, p_54112_, p_54113_, p_54114_);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState p_54078_, Level p_54079_, BlockPos p_54080_, Block p_54081_, BlockPos p_54082_, boolean p_54083_) 
	{
		this.checkPoweredState(p_54079_, p_54080_, p_54078_);
		
		super.neighborChanged(p_54078_, p_54079_, p_54080_, p_54081_, p_54082_, p_54083_);
	}
	
	@Override
	public boolean isPathfindable(BlockState p_51522_, BlockGetter p_51523_, BlockPos p_51524_, PathComputationType p_51525_) 
	{
		return false;
	}
	
	protected void checkPoweredState(Level level, BlockPos pos, BlockState state) 
	{
		boolean flag = level.hasNeighborSignal(pos);
		if (flag != state.getValue(POWERED))
			level.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(flag)), Block.UPDATE_INVISIBLE);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int value) 
	{
		super.triggerEvent(state, level, pos, id, value);
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity == null ? false : blockentity.triggerEvent(id, value);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker) 
	{
		return typeA == typeB ? (BlockEntityTicker<A>)ticker : null;
	}
}
