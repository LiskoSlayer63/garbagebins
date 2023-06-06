package lizcraft.garbagebins.client.gui;

import lizcraft.garbagebins.common.gui.GarbageBinMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

public class GarbageBinContainerScreen extends BaseContainerScreen<GarbageBinMenu> 
{
	public GarbageBinContainerScreen(GarbageBinMenu container, Inventory inv, Component titleIn) 
	{
		super(container, inv, titleIn);
	}
}
