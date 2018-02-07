package liznet.trashcansreborn;

import liznet.trashcansreborn.block.tile.TileTrashcan;
import liznet.trashcansreborn.gui.ContainerTrashcan;
import liznet.trashcansreborn.gui.GuiTrashcan;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	public static int trashcanID = 0;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		if(ID == trashcanID)
		{
			return new ContainerTrashcan((TileTrashcan) world.getTileEntity(new BlockPos(x,y,z)), player);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		if(ID == trashcanID)
		{
			return new GuiTrashcan(player, (TileTrashcan) world.getTileEntity(new BlockPos(x,y,z)));
		}
		return null;
	}
}
