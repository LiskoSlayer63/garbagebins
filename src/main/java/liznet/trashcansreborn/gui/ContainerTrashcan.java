package liznet.trashcansreborn.gui;

import liznet.trashcansreborn.block.tile.TileTrashcan;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
//import reborncore.common.container.RebornContainer;

public class ContainerTrashcan extends Container
{
	public TileTrashcan tile;
	public EntityPlayer player;
	
	public ContainerTrashcan(TileTrashcan tile, EntityPlayer player) 
	{
		super();
		this.tile = tile;
		this.player = player;

		this.addSlotToContainer(new Slot(tile.inv, 0, 84, 44));

		int i;

		for (i = 0; i < 3; ++i) 
		{
			for (int j = 0; j < 9; ++j) 
			{
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 12 + j * 18, 102 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) 
		{
			this.addSlotToContainer(new Slot(player.inventory, i, 12 + i * 18, 160));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) 
	{
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int id)
	{
		ItemStack stack = this.getSlot(id).getStack();
		if(this.mergeItemStack(stack, 0, 1, false))
			return stack;
		return ItemStack.EMPTY;
	}
}
