package lizcraft.garbagebins.common;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class FilterHandler implements IItemHandlerModifiable
{
	private final NonNullList<ItemStack> filterContents;

	public FilterHandler(int size)
	{
		this.filterContents = NonNullList.withSize(size, ItemStack.EMPTY);
	}
	
	public void load(CompoundTag nbt) //read
	{
		ContainerHelper.loadAllItems(nbt, this.filterContents);
	}
	
	public CompoundTag save(CompoundTag compound) //write
	{
		ContainerHelper.saveAllItems(compound, this.filterContents);
		return compound;
	}
	
	public boolean contains(ItemStack stack)
	{
		for (int i = 0; i < this.filterContents.size(); i++)
			if (this.filterContents.get(i).getItem() == stack.getItem())
				return true;
		return false;
	}
	
	public void clear()
	{
		for (int i = 0; i < this.filterContents.size(); i++)
			if (!this.filterContents.get(i).isEmpty())
				this.filterContents.set(i, ItemStack.EMPTY);
	}

	@Override
	public int getSlots() 
	{
		return this.filterContents.size();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) 
	{
		return this.filterContents.get(slot);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) 
	{
		this.filterContents.set(slot, new ItemStack(stack.getItem()));
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) 
	{
		if(!simulate)
			this.setStackInSlot(slot, stack);
		return stack;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) 
	{
		if(!simulate)
			this.setStackInSlot(slot, ItemStack.EMPTY);
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) 
	{
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) 
	{
		return true;
	}
}
