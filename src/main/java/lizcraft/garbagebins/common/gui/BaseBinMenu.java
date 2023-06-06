package lizcraft.garbagebins.common.gui;

import lizcraft.garbagebins.common.SettingsState;
import lizcraft.garbagebins.common.block.entity.BaseBinBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class BaseBinMenu<T extends BaseBinBlockEntity> extends AbstractContainerMenu
{
	public static final int SLOT_SPACING = 18;
	public static final int SLOT_LEFT = 8;
	
	public static final int HOTBAR_TOP = 162;
	public static final int INVENTORY_TOP = 104;
	
	public static final int FILTER_DIFF = 37;
	
	protected final T parent;
	
	public BaseBinMenu(MenuType<?> menuType, int id, T parent)
	{
		super(menuType, id);
		this.parent = parent;
	}
	
	public T getParent() 
	{
		return this.parent;
	}
	
	public void playerInventorySlots(Inventory playerInventory)
	{
		buildPlayerInventory(playerInventory);
		buildPlayerInventoryFilter(playerInventory);
	}
	
	protected void buildPlayerInventory(Inventory playerInventory) 
	{
	    // Build player hotbar
	    for(int slot = 0; slot < 9; slot++) 
	    {
	    	int xPos = SLOT_LEFT + slot * SLOT_SPACING;
	    	
	    	this.addSlot(new SlotWithoutFilter(playerInventory, slot, xPos, HOTBAR_TOP - FILTER_DIFF));
	    }
	    
		// Build player inventory
	    for(int row = 0; row < 3; row++) 
	    {
	    	for(int col = 0; col < 9; col++) 
	    	{
	    		int slot = 9 + row * 9 + col;
	    		int xPos = SLOT_LEFT + col * SLOT_SPACING;
	    		int yPos = INVENTORY_TOP + row * SLOT_SPACING;
	    		
	    		this.addSlot(new SlotWithoutFilter(playerInventory, slot, xPos, yPos - FILTER_DIFF));
	        }
	    }
	}
	
	protected void buildPlayerInventoryFilter(Inventory playerInventory) 
	{
	    // Build player hotbar
	    for(int slot = 0; slot < 9; slot++) 
	    {
	    	int xPos = SLOT_LEFT + slot * SLOT_SPACING;
	    	
	    	this.addSlot(new SlotWithFilter(playerInventory, slot, xPos, HOTBAR_TOP));
	    }
	    
		// Build player inventory
	    for(int row = 0; row < 3; row++) 
	    {
	    	for(int col = 0; col < 9; col++) 
	    	{
	    		int slot = 9 + row * 9 + col;
	    		int xPos = SLOT_LEFT + col * SLOT_SPACING;
	    		int yPos = INVENTORY_TOP + row * SLOT_SPACING;
	    		
	    		this.addSlot(new SlotWithFilter(playerInventory, slot, xPos, yPos));
	        }
	    }
	}
	
	protected class SlotWithFilter extends Slot
	{
		public SlotWithFilter(Container container, int index, int xPosition, int yPosition) 
		{
			super(container, index, xPosition, yPosition);
		}

		@Override
		public boolean isActive()
		{
			if (BaseBinMenu.this.parent != null)
				return BaseBinMenu.this.parent.getFilterState() != SettingsState.DISABLED;
			
			return false;
		}
		
	}
	
	protected class SlotWithoutFilter extends Slot
	{
		public SlotWithoutFilter(Container container, int index, int xPosition, int yPosition) 
		{
			super(container, index, xPosition, yPosition);
		}

		@Override
		public boolean isActive()
		{
			if (BaseBinMenu.this.parent != null)
				return BaseBinMenu.this.parent.getFilterState() == SettingsState.DISABLED;
			
			return true;
		}
		
	}
}
