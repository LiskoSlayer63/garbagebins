package liznet.trashcansreborn.block;

import liznet.trashcansreborn.GuiHandler;
import liznet.trashcansreborn.TrashCansReborn;
import liznet.trashcansreborn.block.tile.TileTrashcan;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTrashcan extends BlockMod
{
	
	public BlockTrashcan() 
	{
		super();
		setUnlocalizedName("trashcan");
		setRegistryName("trashcan");
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!player.isSneaking())
		{
			player.openGui(TrashCansReborn.instance, GuiHandler.trashcanID, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileTrashcan();
	}
}
