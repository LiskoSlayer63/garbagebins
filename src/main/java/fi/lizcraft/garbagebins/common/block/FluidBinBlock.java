package fi.lizcraft.garbagebins.common.block;

import java.util.Optional;

import fi.lizcraft.garbagebins.common.tile.FluidBinTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidBinBlock extends BaseBinBlock 
{
	private static final VoxelShape DRAIN_SHAPE = Block.makeCuboidShape(5.0D, 14.0D, 5.0D, 11.0D, 16.0D, 11.0D);
	protected static final VoxelShape BASE_SHAPE = VoxelShapes.combineAndSimplify(BaseBinBlock.BASE_SHAPE, DRAIN_SHAPE, IBooleanFunction.ONLY_FIRST);
	
	public FluidBinBlock(Properties properties) 
	{
		super(properties);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) 
	{
		if (worldIn.isRemote)
			return ActionResultType.SUCCESS;
		
		ItemStack itemStack = player.getHeldItem(handIn);
		Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(itemStack);
		
		if (fluidStack.isPresent() && !fluidStack.get().isEmpty())
		{
			FluidUtil.getFluidHandler(itemStack).map(fluidHandler -> fluidHandler.drain(Integer.MAX_VALUE, FluidAction.EXECUTE));
			
			Optional<ItemStack> container = FluidUtil.getFluidHandler(itemStack).map(IFluidHandlerItem::getContainer);
			player.setHeldItem(handIn, container.get());
			
			return ActionResultType.CONSUME;
		}
		
		return ActionResultType.FAIL;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
	{
		return BASE_SHAPE;
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) 
	{
		return new FluidBinTileEntity();
	}
}
