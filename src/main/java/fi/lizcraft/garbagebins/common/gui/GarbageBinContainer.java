package fi.lizcraft.garbagebins.common.gui;

import fi.lizcraft.garbagebins.common.BufferInventory;
import fi.lizcraft.garbagebins.common.CommonContent;
import fi.lizcraft.garbagebins.utils.Logger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class GarbageBinContainer extends Container
{
	private static final int HOTBAR_SLOT_COUNT = 9;
	private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
	private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;

	public static final int INPUT_SLOTS_COUNT = 1;
	public static final int BUFFER_SLOTS_COUNT = 5;
	public static final int GARBAGEBIN_SLOTS_COUNT = INPUT_SLOTS_COUNT + BUFFER_SLOTS_COUNT;
	
	public static final int PLAYER_INVENTORY_XPOS = 8;
	public static final int PLAYER_INVENTORY_YPOS = 51;

	public static final int INPUT_SLOT_XPOS = 15;
	public static final int INPUT_SLOT_YPOS = 20;
	
	public static final int BUFFER_SLOT_XPOS = 73;
	public static final int BUFFER_SLOT_YPOS = 20;
	
	private static final int SLOT_X_SPACING = 18;
	private static final int SLOT_Y_SPACING = 18;
	private static final int HOTBAR_XPOS = 8;
	private static final int HOTBAR_YPOS = 109;
	
	private final IInventory inventory;
	
	public GarbageBinContainer(int id, PlayerInventory playerInventory, PacketBuffer extraData) 
	{
		this(id, playerInventory, new BufferInventory(6));
	}
	
	public GarbageBinContainer(int id, PlayerInventory playerInventory, IInventory inventory)
	{
		super(CommonContent.GARBAGEBIN_CONTAINER, id);
		this.inventory = inventory;
		assertInventorySize(inventory, 6);
		inventory.openInventory(playerInventory.player);
		
		// Add input slot
		this.addSlot(new InputSlot(inventory, 0, INPUT_SLOT_XPOS, INPUT_SLOT_YPOS));
		
		// Build buffer inventory
		for(int col = 0; col < BUFFER_SLOTS_COUNT; col++) 
		{
			int slot = col + INPUT_SLOTS_COUNT;
			int xPos = BUFFER_SLOT_XPOS + col * SLOT_X_SPACING;
			
			this.addSlot(new BufferSlot(inventory, slot, xPos, BUFFER_SLOT_YPOS));
	    }

		// Build player inventory
	    for(int row = 0; row < PLAYER_INVENTORY_ROW_COUNT; row++) 
	    {
	    	for(int col = 0; col < PLAYER_INVENTORY_COLUMN_COUNT; col++) 
	    	{
	    		int slot = HOTBAR_SLOT_COUNT + row * PLAYER_INVENTORY_COLUMN_COUNT + col;
	    		int xPos = PLAYER_INVENTORY_XPOS + col * SLOT_X_SPACING;
	    		int yPos = PLAYER_INVENTORY_YPOS + row * SLOT_Y_SPACING;
	    		
	    		this.addSlot(new Slot(playerInventory, slot, xPos, yPos));
	        }
	    }

	    // Build player hotbar
	    for(int slot = 0; slot < HOTBAR_SLOT_COUNT; slot++) 
	    {
	    	int xPos = HOTBAR_XPOS + slot * SLOT_X_SPACING;
	    	
	    	this.addSlot(new Slot(playerInventory, slot, xPos, HOTBAR_YPOS));
	    }
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) 
	{
		return this.inventory.isUsableByPlayer(playerIn);
	}

	/*
	 * Vanilla code stolen from the Hopper
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) 
	{
		ItemStack copy = ItemStack.EMPTY;
	    Slot slot = this.inventorySlots.get(index);
	    
	    Logger.info("Transferring stack!");
	    
	    if (slot != null && slot.getHasStack()) 
	    {
	    	ItemStack stack = slot.getStack();
	        copy = stack.copy();
	        
	        if (index < this.inventory.getSizeInventory()) 
	        {
	        	if (!this.mergeItemStack(stack, this.inventory.getSizeInventory(), this.inventorySlots.size(), false)) // Put the item to the inventory
	        		return ItemStack.EMPTY;
	        } 
	        else if (!this.mergeItemStack(stack, 0, 1, false)) // Put the item to the input slot
	        {
	        	return ItemStack.EMPTY;
	        }

	        if (stack.isEmpty()) 
	        {
	        	slot.putStack(ItemStack.EMPTY);
	        	return ItemStack.EMPTY; // <-- Fix looping all the contents when buffer shifts
	        } 
	        else 
	        {
	        	slot.onSlotChanged();
	        }
	    }
	    
	    return copy;
	}
	
	@Override
	public void onContainerClosed(PlayerEntity playerIn) 
	{
		super.onContainerClosed(playerIn);
		this.inventory.closeInventory(playerIn);
	}
	
	
	/* 
	 * This is only for checking how many are using the Container
	 */
	public IInventory getIInventory()
	{
		return this.inventory;
	}
	
	/* 
	 * Slot type for buffer
	 */
	public static class BufferSlot extends Slot 
	{
		public BufferSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) 
		{
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return false;
		}
	}

	
	/* 
	 * Slot type for input
	 */
	public static class InputSlot extends Slot 
	{
		public InputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) 
		{
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean canTakeStack(PlayerEntity player)
		{
			return false;
		}
	}
}
