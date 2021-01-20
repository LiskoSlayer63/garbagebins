package lizcraft.garbagebins.common.block;

import lizcraft.garbagebins.common.tile.GarbageBinTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class GarbageBinBlock extends BaseBinBlock
{
	public GarbageBinBlock(Properties properties) 
	{
		super(properties);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) 
	{
		if (worldIn.isRemote) 
			return ActionResultType.SUCCESS;
		
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof GarbageBinTileEntity && !ChestBlock.isBlocked(worldIn, pos))
			player.openContainer((GarbageBinTileEntity)tileentity);
	  
		return ActionResultType.CONSUME;
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader worldIn) 
	{
		return new GarbageBinTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) 
	{
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
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
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) 
	{
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
}
