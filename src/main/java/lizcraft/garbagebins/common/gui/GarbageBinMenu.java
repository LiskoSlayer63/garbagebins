package lizcraft.garbagebins.common.gui;

import lizcraft.garbagebins.common.SequencedContainer;
import lizcraft.garbagebins.common.SettingsState;
import lizcraft.garbagebins.common.block.entity.GarbageBinBlockEntity;
import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.FilterHandler;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;

public class GarbageBinMenu extends BaseBinMenu<GarbageBinBlockEntity>
{
	public static final int INPUT_SLOT_LEFT = 15;
	public static final int INPUT_SLOT_TOP = 24;
	
	public static final int BUFFER_SLOT_LEFT = 73;
	public static final int BUFFER_SLOT_TOP = 24;
	
	public static final int FILTER_SLOT_TOP = 67;
	
	private final Container container;
	private final IItemHandler filter;
	
	public GarbageBinMenu(int id, Inventory playerInventory, FriendlyByteBuf packet) 
	{
		this(id, playerInventory, playerInventory.player.level.getBlockEntity(packet.readBlockPos()) instanceof GarbageBinBlockEntity garbageBin ? garbageBin : null);
	}
	
	public GarbageBinMenu(int id, Inventory playerInventory, GarbageBinBlockEntity parent)
	{
		super(CommonContent.GARBAGEBIN_MENU.get(), id, parent);
		this.container = this.parent != null ? this.parent.getContainer() : new SequencedContainer(6);
		this.filter = this.parent != null ? this.parent.getFilter() : new FilterHandler(9);
		checkContainerSize(container, 6);
		this.container.startOpen(playerInventory.player);
		
		// Add input slot
		this.addSlot(new InputSlot(this.container, 0, INPUT_SLOT_LEFT, INPUT_SLOT_TOP));
		
		// Build sequenced inventory
		for(int slot = 0; slot < 5; slot++) 
		{
			int xPos = BUFFER_SLOT_LEFT + slot * SLOT_SPACING;
			
			this.addSlot(new BufferSlot(this.container, slot + 1, xPos, BUFFER_SLOT_TOP));
	    }

		// Build filter inventory
		for(int slot = 0; slot < 9; slot++) 
		{
			int xPos = SLOT_LEFT + slot * SLOT_SPACING;
			
			this.addSlot(new FilterSlot(this.filter, slot, xPos, FILTER_SLOT_TOP));
	    }
		
		this.playerInventorySlots(playerInventory);
	}

	@Override
	public boolean stillValid(Player playerIn) 
	{
		return this.container.stillValid(playerIn);
	}
	
	@Override
	public void removed(Player playerIn) 
	{
	    super.removed(playerIn);
	    this.container.stopOpen(playerIn);
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

	/*
	 * Vanilla code stolen from the Hopper
	 */
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) 
	{
		ItemStack copy = ItemStack.EMPTY;
	    Slot slot = this.slots.get(index);
	    
	    if (slot != null && slot.hasItem()) 
	    {
	    	ItemStack stack = slot.getItem();
	        copy = stack.copy();
	        
	        if (index < this.container.getContainerSize()) 
	        {
	        	if (!this.moveItemStackTo(stack, this.container.getContainerSize(), this.slots.size(), false)) // Put the item to the inventory
	        		return ItemStack.EMPTY;
	        } 
	        else if (!this.moveItemStackTo(stack, 0, 1, false)) // Put the item to the input slot
	        {
	        	return ItemStack.EMPTY;
	        }

	        if (stack.isEmpty()) 
	        {
	        	slot.set(ItemStack.EMPTY);
	        	return ItemStack.EMPTY; // <-- Fix looping all the contents when buffer shifts
	        } 
	        else 
	        {
	        	slot.setChanged();
	        }
	    }
	    
	    return copy;
	}
	
	public Container getContainer()
	{
		return this.container;
	}
	
	public GarbageBinBlockEntity getParent()
	{
		return this.parent;
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
	 * Slot type for input
	 */
	public class InputSlot extends Slot 
	{
		public InputSlot(Container container, int index, int xPosition, int yPosition) 
		{
			super(container, index, xPosition, yPosition);
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			if (GarbageBinMenu.this.parent != null)
				return GarbageBinMenu.this.parent.isItemValid(stack);
			return true;
		}

		@Override
		public boolean mayPickup(Player player)
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
			if (GarbageBinMenu.this.parent != null)
				GarbageBinMenu.this.parent.setChanged();
		}

		@Override
		public boolean isActive()
		{
			if (GarbageBinMenu.this.parent != null)
				return GarbageBinMenu.this.parent.getFilterState() != SettingsState.DISABLED;
			
			return false;
		}
	}
}
