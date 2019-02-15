package liznet.trashcansreborn.block;

import liznet.trashcansreborn.block.tile.TileTrashcanFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class BlockTrashcanFluid extends BlockMod
{	
	public BlockTrashcanFluid() 
	{
		super();
		setRegistryName("trashcanfluid");
		setTranslationKey("trashcanfluid");
	}
	
	@Override
	public Class<? extends TileEntity> getTileEntityClass(){
		return TileTrashcanFluid.class;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(player.getHeldItemMainhand() != null)
		{
			ItemStack itemStack = player.getHeldItemMainhand();
			FluidStack fluidStack = FluidUtil.getFluidContained(itemStack);
			if(fluidStack != null)
			{
				FluidUtil.getFluidHandler(itemStack).drain(fluidStack.amount, true);
				player.setHeldItem(hand, FluidUtil.getFluidHandler(itemStack).getContainer());
			}
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileTrashcanFluid();
	}
}
