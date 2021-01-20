package lizcraft.garbagebins.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

/*
 *   Buffered inventory
 * 
 * - Uses first slot (0) for input and then handles other slots as a chronological history
 * - First slot return always ItemStack.EMPTY
 * - You shouldn't put items directly to the buffer using setInventorySlotContents() and instead always use addToBuffer (except when autofilling)
 * - If buffer somehow got out of sync or there's empty slots between stacks, you can use arrangeBuffer()
 */
public class BufferInventory implements IInventory
{
	private final NonNullList<ItemStack> bufferContents;
	private final TileEntity tileEntity;
	
	public BufferInventory(int size)
	{
		this.bufferContents = NonNullList.withSize(size - 1, ItemStack.EMPTY);
		this.tileEntity = null;
	}
	
	public BufferInventory(TileEntity master, int size)
	{
		this.bufferContents = NonNullList.withSize(size - 1, ItemStack.EMPTY);
		this.tileEntity = master;
	}
	
	public void read(CompoundNBT nbt)
	{
		ItemStackHelper.loadAllItems(nbt, this.bufferContents);
	}
	
	public CompoundNBT write(CompoundNBT compound) 
	{
		this.arrangeBuffer();
		ItemStackHelper.saveAllItems(compound, this.bufferContents);
		return compound;
	}

	@Override
	public void clear() 
	{
		for (ItemStack stack : this.bufferContents)
			if (!stack.isEmpty())
				stack = ItemStack.EMPTY;
	}

	@Override
	public int getSizeInventory() 
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
	public ItemStack getStackInSlot(int index) 
	{
		if (index > 0 && index <= this.bufferContents.size())
			return this.bufferContents.get(index - 1);
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) 
	{
		if (index > 0 && index <= this.bufferContents.size())
		{
			ItemStack stack = this.bufferContents.get(index - 1);
			
			if (stack.getCount() == count)
				return removeStackFromSlot(index);
			else
				return ItemStackHelper.getAndSplit(this.bufferContents, index - 1, count);
		}
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) 
	{
		if (index > 0 && index <= this.bufferContents.size())
			return this.takeFromBuffer(index - 1);

		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{
		if (index == 0)
			this.addToBuffer(stack);
		else if (stack.isEmpty())
			this.takeFromBuffer(index - 1);
		else
			this.bufferContents.set(index - 1, stack);
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) 
	{
		return true;
	}
	
	@Override
	public void markDirty() 
	{
		if (this.tileEntity != null)
			this.tileEntity.markDirty();
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
}
