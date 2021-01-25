package lizcraft.garbagebins.common.tile;

import lizcraft.garbagebins.common.BufferInventory;
import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.block.BaseBinBlock;
import lizcraft.garbagebins.common.block.GarbageBinBlock;
import lizcraft.garbagebins.common.gui.GarbageBinContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
public class GarbageBinTileEntity extends TileEntity implements IChestLid, ITickableTileEntity, ICapabilityProvider, INamedContainerProvider
{
	private final BufferInventory inventory = new BufferInventory(this, 6);
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
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.getBlockState().get(BaseBinBlock.ENABLED)) 
			return this.handler.cast();
		return super.getCapability(capability, facing);
	}

	@Override
	public void tick() 
	{
		this.ticksSinceSync++;
	    this.numPlayersUsing = calculatePlayersUsingSync(this.world, this, this.ticksSinceSync, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.numPlayersUsing);
		this.prevLidAngle = this.lidAngle;
		
		if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F)
			this.playSound(CommonContent.GARBAGEBIN_OPEN);

	    if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) 
	    {
	    	float oldAngle = this.lidAngle;
	    	
	    	if (this.numPlayersUsing > 0)
	    		this.lidAngle += 0.1F;
	    	else
	    		this.lidAngle -= 0.1F;

	        if (this.lidAngle > 1.0F)
	            this.lidAngle = 1.0F;
	        
	        if (this.lidAngle < 0.5F && oldAngle >= 0.5F)
	            this.playSound(CommonContent.GARBAGEBIN_CLOSE);
	        
	        if (this.lidAngle < 0.0F)
	        	this.lidAngle = 0.0F;
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
		if (id == 1) 
		{
			this.numPlayersUsing = type;
			return true;
		}
        return super.receiveClientEvent(id, type);
	}
	
	@Override
	public void remove() 
	{
		this.updateContainingBlockInfo();
		super.remove();
	}
	
	/*
	 * CONTAINERPROVIDER OVERRIDES
	 */

	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) 
	{
		return new GarbageBinContainer(id, playerInventory, this.inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() 
	{
		return new TranslationTextComponent("block.garbagebins.garbagebin");
	}
	
	public void addToGarbageBin(ItemStack stack)
	{
		this.inventory.addToBuffer(stack);
	}
	
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

	public void closeInventory(PlayerEntity player) 
	{
		if (!player.isSpectator()) 
		{
			--this.numPlayersUsing;
	        this.onOpenOrClose();
	    }
	}

	public boolean isUsableByPlayer(PlayerEntity player) 
	{
		if (this.world.getTileEntity(this.pos) != this) 
			return false;
		else 
			return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
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
	
	private static int calculatePlayersUsingSync(World worldIn, TileEntity tileEntity, int ticksSince, int posX, int posY, int posZ, int playersUsing) 
	{
		if (!worldIn.isRemote && playersUsing != 0 && (ticksSince + posX + posY + posZ) % 200 == 0)
			playersUsing = calculatePlayersUsing(worldIn, tileEntity, posX, posY, posZ);

	    return playersUsing;
	}

	public static int calculatePlayersUsing(World worldIn, TileEntity tileEntity, int posX, int posY, int posZ) 
	{
		int count = 0;

	    for(PlayerEntity playerentity : worldIn.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(posX - 5.0D, posY - 5.0D, posZ - 5.0D, posX + 1 + 5.0D, posY + 1 + 5.0D, posZ + 1 + 5.0D))) 
	    {
	    	if (playerentity.openContainer instanceof GarbageBinContainer) 
	    	{
	    		TileEntity containerEntity = ((GarbageBinContainer)playerentity.openContainer).getTileEntity();
	            if (containerEntity == tileEntity)
	            	count++;
	        }
	    }

	    return count;
	}
	
	private void playSound(SoundEvent soundIn) 
	{
		this.world.playSound((PlayerEntity)null, this.pos, soundIn, SoundCategory.BLOCKS, 0.7F, this.world.rand.nextFloat() * 0.1F + 0.9F);
	}
	
	
	/*
	 * ITEM HANDLER FOR AUTOMATION
	 */
	
	public static class GarbageBinHandler implements IItemHandler
	{
		private final GarbageBinTileEntity tileEntity;
		
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
			if (!simulate)
			{
				this.tileEntity.addToGarbageBin(stack);
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
			return true;
		}
	}
}
