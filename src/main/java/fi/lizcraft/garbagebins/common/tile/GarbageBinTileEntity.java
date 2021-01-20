package fi.lizcraft.garbagebins.common.tile;

import fi.lizcraft.garbagebins.common.BufferInventory;
import fi.lizcraft.garbagebins.common.CommonContent;
import fi.lizcraft.garbagebins.common.block.BaseBinBlock;
import fi.lizcraft.garbagebins.common.block.GarbageBinBlock;
import fi.lizcraft.garbagebins.common.gui.GarbageBinContainer;
import fi.lizcraft.garbagebins.utils.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@OnlyIn(
value = Dist.CLIENT,
_interface = IChestLid.class
)
public class GarbageBinTileEntity extends TileEntity implements IChestLid, ITickableTileEntity, ICapabilityProvider, INamedContainerProvider, IInventory
{
	private final BufferInventory inventory = new BufferInventory(6);
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> new GarbageBinHandler(this));
	
	private float lidAngle;
	private float prevLidAngle;
	
	private int numPlayersUsing;
	private int ticksSinceSync;
	   
	public GarbageBinTileEntity(TileEntityType<?> tileEntityType) 
	{
		super(tileEntityType);
	}
	
	public GarbageBinTileEntity() 
	{
		super(CommonContent.GARBAGEBIN_TILEENTITYTYPE);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) 
	{
		super.read(state, nbt);
		inventory.read(nbt);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) 
	{
		super.write(compound);
		inventory.write(compound);
		return compound;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) 
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) 
			return this.handler.cast();
		return super.getCapability(capability, facing);
	}

	@Override
	public void tick() 
	{
		this.ticksSinceSync++;
	    this.numPlayersUsing = calculatePlayersUsingSync(this.world, this, this.ticksSinceSync, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.numPlayersUsing);
		this.prevLidAngle = this.lidAngle;

	    if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) 
	    {
	    	if (this.numPlayersUsing > 0)
	    		this.lidAngle += 0.1F;
	    	else
	    		this.lidAngle -= 0.1F;

	        if (this.lidAngle > 1.0F)
	            this.lidAngle = 1.0F;
	        
	        if (this.lidAngle < 0.0F)
	        	this.lidAngle = 0.0F;
	    }
	    
	    if (this.ticksSinceSync % 200 == 0)
	    {
	    	boolean powered = this.world.isBlockPowered(this.pos);
	    	boolean enabled = this.getBlockState().get(BaseBinBlock.ENABLED);
	    	boolean remote = this.world.isRemote;
	    	
	    	Logger.info("TICK: GarbageBin Powered: " + powered + ", Enabled: " + enabled + ", Side: " + (remote ? "CLIENT" : "SERVER"));
	    }
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public float getLidAngle(float partialTicks) 
	{
		return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
	}

	@Override
	public boolean receiveClientEvent(int id, int type) 
	{
		Logger.info("Received client event!");
		if (id == 1) 
		{
			this.numPlayersUsing = type;
			return true;
		}
        return super.receiveClientEvent(id, type);
	}
	
	@Override
	public void openInventory(PlayerEntity player) 
	{
		if (!player.isSpectator())
		{
			if (this.numPlayersUsing < 0) 
				this.numPlayersUsing = 0;

			++this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}

	@Override
	public void closeInventory(PlayerEntity player) 
	{
		if (!player.isSpectator()) 
		{
			--this.numPlayersUsing;
	        this.onOpenOrClose();
	    }
	}

	
	/*
	 * CONTAINERPROVIDER OVERRIDES
	 */

	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) 
	{
		return new GarbageBinContainer(id, playerInventory, this);
	}

	@Override
	public ITextComponent getDisplayName() 
	{
		return new TranslationTextComponent("block.garbagebins.garbagebin_item");
	}
	
	
	/*
	 * INVENTORY OVERRIDES
	 */
	
	@Override
	public int getSizeInventory() 
	{
		return this.inventory.getSizeInventory();
	}

	@Override
	public boolean isEmpty() 
	{
		return this.inventory.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) 
	{
		return this.inventory.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) 
	{
		return this.inventory.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) 
	{
		return this.inventory.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{
		this.inventory.setInventorySlotContents(index, stack);
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) 
	{
		if (this.world.getTileEntity(this.pos) != this) 
			return false;
		else 
			return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clear() 
	{
		inventory.clear();
	}
	
	protected void onOpenOrClose() 
	{
		Block block = this.getBlockState().getBlock();
	    if (block instanceof GarbageBinBlock) 
	    {
	    	this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
	    	this.world.notifyNeighborsOfStateChange(this.pos, block);
	    }
	}
	
	private static int calculatePlayersUsingSync(World worldIn, IInventory inventory, int ticksSince, int posX, int posY, int posZ, int playersUsing) 
	{
		if (!worldIn.isRemote && playersUsing != 0 && (ticksSince + posX + posY + posZ) % 200 == 0)
			playersUsing = calculatePlayersUsing(worldIn, inventory, posX, posY, posZ);

	    return playersUsing;
	}

	public static int calculatePlayersUsing(World worldIn, IInventory inventory, int posX, int posY, int posZ) 
	{
		int count = 0;

	    for(PlayerEntity playerentity : worldIn.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(posX - 5.0D, posY - 5.0D, posZ - 5.0D, posX + 1 + 5.0D, posY + 1 + 5.0D, posZ + 1 + 5.0D))) 
	    {
	    	if (playerentity.openContainer instanceof GarbageBinContainer) 
	    	{
	    		IInventory iinventory = ((GarbageBinContainer)playerentity.openContainer).getIInventory();
	            if (iinventory == inventory)
	            	count++;
	        }
	    }

	    return count;
	}
	
	
	/*
	 * ITEM HANDLER FOR AUTOMATION
	 */
	
	public static class GarbageBinHandler implements IItemHandler
	{
		GarbageBinTileEntity tileEntity;
		
		public GarbageBinHandler(GarbageBinTileEntity tileEntity)
		{
			this.tileEntity = tileEntity;
		}
		
		@Override
		public int getSlots() 
		{
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) 
		{
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) 
		{
	    	boolean powered = this.tileEntity.world.isBlockPowered(this.tileEntity.pos);
	    	boolean enabled = this.tileEntity.getBlockState().get(BaseBinBlock.ENABLED);
	    	boolean remote = this.tileEntity.world.isRemote;
	    	
	    	Logger.info("INSERT: GarbageBin Powered: " + powered + ", Enabled: " + enabled + ", Side: " + (remote ? "CLIENT" : "SERVER"));
			
			if (!enabled)
				return stack;
			
			if (!simulate)
			{
				this.tileEntity.setInventorySlotContents(0, stack);
				this.tileEntity.markDirty();
			}
			
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) 
		{
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot)
		{
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) 
		{
			return this.tileEntity.getBlockState().get(BaseBinBlock.ENABLED);
		}
	}
}
