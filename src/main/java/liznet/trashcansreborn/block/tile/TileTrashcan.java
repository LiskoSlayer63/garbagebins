package liznet.trashcansreborn.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileTrashcan extends TileEntity implements IInventory, ITickable
{
	public InventoryBasic inv = new InventoryBasic("trashcan", false, 1);
	private ItemHandler handler = new ItemHandler();
	
	@Override
	public void update() 
	{
		if(!isEmpty())
			clear();
	}
	
	@Override
	public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)handler;
		return super.getCapability(capability, facing);
	}
	
	@Override
	public String getName() 
	{
		return inv.getName();
	}

	@Override
	public boolean hasCustomName() 
	{
		return inv.hasCustomName();
	}

	@Override
	public ITextComponent getDisplayName() 
	{
		return inv.getDisplayName();
	}

	@Override
	public int getSizeInventory() 
	{
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int index) 
	{
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) 
	{
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) 
	{
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return inv.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) 
	{
		return true;
	}
	
	@Override
	public boolean isEmpty() 
	{
		return inv.isEmpty();
	}

	@Override
	public void openInventory(EntityPlayer player) 
	{
		inv.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) 
	{
		inv.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) 
	{
		return inv.isItemValidForSlot(index, stack);
	}

	@Override
	public int getField(int id) 
	{
		return inv.getField(id);
	}

	@Override
	public void setField(int id, int value) 
	{
		inv.setField(id, value);
	}

	@Override
	public int getFieldCount() 
	{
		return inv.getFieldCount();
	}

	@Override
	public void clear() 
	{
		inv.clear();
	}
	
	public class ItemHandler implements IItemHandler {
		
		@Override
		public int getSlots() {
			return inv.getSizeInventory();
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			inv.setInventorySlotContents(slot, stack);
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return inv.getSizeInventory();
		}
		
	}
}
