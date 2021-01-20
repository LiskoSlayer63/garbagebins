package fi.lizcraft.garbagebins.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BaseBinBlock extends ContainerBlock 
{
	public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	private static final VoxelShape LID_SHAPE = Block.makeCuboidShape(1.0D, 14.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	private static final VoxelShape BODY_SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);
	protected static final VoxelShape BASE_SHAPE = VoxelShapes.or(BODY_SHAPE, LID_SHAPE);
	
	public BaseBinBlock(Properties properties) 
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(ENABLED, Boolean.valueOf(true)));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) 
	{
		builder.add(FACING, ENABLED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
	{
		return BASE_SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) 
	{
		Direction direction = context.getPlacementHorizontalFacing().getOpposite();
		return this.getDefaultState().with(FACING, direction).with(ENABLED, Boolean.valueOf(true));
	}
	
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) 
	{
		if (!oldState.isIn(state.getBlock()))
			this.updateState(worldIn, pos, state);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) 
	{
		this.updateState(worldIn, pos, state);
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return null;
	}

	@Override
    public BlockRenderType getRenderType(BlockState state) 
    {
    	return BlockRenderType.MODEL;
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) 
	{
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) 
	{
		if (!state.isIn(newState.getBlock())) 
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof IInventory) 
			{
				InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}
	        super.onReplaced(state, worldIn, pos, newState, isMoving);
		} 
		else 
		{
			worldIn.getTileEntity(pos).updateContainingBlockInfo();
		}
	}

	private void updateState(World worldIn, BlockPos pos, BlockState state) 
	{
		boolean flag = !worldIn.isBlockPowered(pos);
		if (flag != state.get(ENABLED)) 
			worldIn.setBlockState(pos, state.with(ENABLED, Boolean.valueOf(flag)), 4);
	}
}
