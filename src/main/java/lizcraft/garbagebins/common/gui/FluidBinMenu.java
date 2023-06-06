package lizcraft.garbagebins.common.gui;

import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.SettingsState;
import lizcraft.garbagebins.common.block.entity.FluidBinBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class FluidBinMenu extends BaseBinMenu<FluidBinBlockEntity>
{
	public static final int INPUT_SLOT_LEFT = 15;
	public static final int INPUT_SLOT_TOP = 24;
	
	public static final int BUFFER_SLOT_LEFT = 73;
	public static final int BUFFER_SLOT_TOP = 24;
	
	public static final int FILTER_SLOT_TOP = 67;
	
	
	public FluidBinMenu(int id, Inventory playerInventory, FriendlyByteBuf packet) 
	{
		this(id, playerInventory, playerInventory.player.level.getBlockEntity(packet.readBlockPos()) instanceof FluidBinBlockEntity fluidBin ? fluidBin : null);
	}

	public FluidBinMenu(int id, Inventory playerInventory, FluidBinBlockEntity parent) 
	{
		super(CommonContent.FLUIDBIN_MENU.get(), id, parent);
		
		

		this.playerInventorySlots(playerInventory);
	}

	@Override
	public boolean stillValid(Player playerIn) 
	{
		return true;
	}
	
	@Override
	public void removed(Player playerIn) 
	{
	    super.removed(playerIn);
	    //this.container.stopOpen(playerIn);
	}
	
	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) 
	{
		if (slotId >= 0 && slotId < this.slots.size() && this.slots.get(slotId) instanceof FilterSlot slot)
		{
			if (this.getCarried().isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.set(new ItemStack(this.getCarried().getItem()));
		}
		else
			super.clicked(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) 
	{
		return ItemStack.EMPTY;
	}

	/* 
	 * Slot type for buffer
	 */
	public class BufferSlot extends Slot
	{
		public BufferSlot(Container container, int index, int xPosition, int yPosition) 
		{
			super(container, index, xPosition, yPosition);
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return false;
		}
	}
	
	
	/* 
	 * Slot type for filter
	 */
	public class FilterSlot extends SlotItemHandler
	{
		public FilterSlot(IItemHandler container, int index, int xPosition, int yPosition) 
		{
			super(container, index, xPosition, yPosition);
		}

		@Override
		public boolean mayPickup(Player player)
		{
			return false;
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return false;
		}

		@Override
		public void setChanged() 
		{
			if (FluidBinMenu.this.parent != null)
				FluidBinMenu.this.parent.setChanged();
		}

		@Override
		public boolean isActive()
		{
			if (FluidBinMenu.this.parent != null)
				return FluidBinMenu.this.parent.getFilterState() != SettingsState.DISABLED;
			
			return false;
		}
	}
}
