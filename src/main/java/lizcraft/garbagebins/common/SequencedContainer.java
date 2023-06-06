package lizcraft.garbagebins.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;

/*
 *   Buffered inventory
 * 
 * - Uses first slot (0) for input and then handles other slots as a chronological history
 * - First slot return always ItemStack.EMPTY
 * - You shouldn't put items directly to the buffer using setInventorySlotContents() and instead always use addToBuffer (except when autofilling)
 * - If buffer somehow got out of sync or there's empty slots between stacks, you can use arrangeBuffer()
 */
public class SequencedContainer implements Container
{
	private final NonNullList<ItemStack> bufferContents;
	
	public SequencedContainer(int size)
	{
		this.bufferContents = NonNullList.withSize(size - 1, ItemStack.EMPTY);
	}
	
	public void load(CompoundTag nbt) //read
	{
		ContainerHelper.loadAllItems(nbt, this.bufferContents);
	}
	
	public CompoundTag save(CompoundTag compound) //write
	{
		this.arrangeBuffer();
		ContainerHelper.saveAllItems(compound, this.bufferContents);
		return compound;
	}

	@Override
	public void clearContent() 
	{
		for (int i = 0; i < this.getContainerSize(); i++)
			this.setItem(i, ItemStack.EMPTY);
	}

	@Override
	public int getContainerSize() 
	{
		return this.bufferContents.size() + 1;
	}

	@Override
	public boolean isEmpty() 
	{
		for (ItemStack stack : this.bufferContents)
			if (!stack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getItem(int index) 
	{
		if (index > 0 && index <= this.bufferContents.size())
			return this.bufferContents.get(index - 1).copy();
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count) 
	{
		if (index > 0 && index <= this.bufferContents.size())
		{
			ItemStack stack = this.bufferContents.get(index - 1);
			
			if (stack.getCount() == count)
				return removeItemNoUpdate(index);
			else 
			{
				ItemStack itemstack = ContainerHelper.removeItem(this.bufferContents, index - 1, count);
				if (!itemstack.isEmpty()) {
					this.setChanged();
				}

				return itemstack;
			}
		}
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) 
	{
		if (index > 0 && index <= this.bufferContents.size())
			return this.takeFromBuffer(index - 1);

		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int index, ItemStack stack) 
	{
		if (index == 0)
			this.addToBuffer(stack);
		else if (stack.isEmpty())
			this.takeFromBuffer(index - 1);
		else
			this.bufferContents.set(index - 1, stack);
		
		this.setChanged();
	}

	/*
	 *  Check if there's items on the right side of an empty slot and rearrange
	 */
	public void arrangeBuffer()
	{
		for (int i = 0; i < this.bufferContents.size() - 1; i++)
			if (this.bufferContents.get(i).isEmpty() && !this.bufferContents.get(i + 1).isEmpty())
				takeFromBuffer(i);
	}
	
	/*
	 *  Move every item to the right and add stack as first item
	 */
	public void addToBuffer(ItemStack stack)
	{
		if (stack.isEmpty())
			return;
		
		for (int index = this.bufferContents.size() - 1; index > 0; index--)
			this.bufferContents.set(index, this.bufferContents.get(index - 1));
		
		this.bufferContents.set(0, stack);
	}

	/*
	 * Take stack from given slot and move every item after that to the left
	 */
	private ItemStack takeFromBuffer(int slot)
	{
		ItemStack stack = ItemStack.EMPTY;
		
		if (slot < this.bufferContents.size())
		{
			stack = this.bufferContents.get(slot);
			
			for (int index = slot; index < this.bufferContents.size() - 1; index++)
				this.bufferContents.set(index, this.bufferContents.get(index + 1));
					
			this.bufferContents.set(this.bufferContents.size() - 1, ItemStack.EMPTY);
		}
			
		return stack;
	}

	@Override
	public void setChanged() 
	{
		
	}

	@Override
	public boolean stillValid(Player p_18946_) 
	{
		return true; 
	}
}
