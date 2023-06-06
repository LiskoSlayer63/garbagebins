package lizcraft.garbagebins.common.block.entity;

import org.jetbrains.annotations.NotNull;

import lizcraft.garbagebins.common.SequencedContainer;
import lizcraft.garbagebins.common.SettingsState;
import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.FilterHandler;
import lizcraft.garbagebins.common.block.BaseBinBlock;
import lizcraft.garbagebins.common.gui.GarbageBinMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class GarbageBinBlockEntity extends BaseBinBlockEntity implements LidBlockEntity
{
	private final FilterHandler filter = new FilterHandler(9);
	private final SequencedContainer inventory = new SequencedContainer(6) 
	{
		private final GarbageBinBlockEntity parent = GarbageBinBlockEntity.this;
		
		@Override
		public void startOpen(Player player) 
		{
			if (!parent.remove && !player.isSpectator()) 
				parent.openersCounter.incrementOpeners(player, parent.getLevel(), parent.getBlockPos(), parent.getBlockState());
		}
		
		@Override
		public void stopOpen(Player player) 
		{
			if (!parent.remove && !player.isSpectator()) 
				parent.openersCounter.decrementOpeners(player, parent.getLevel(), parent.getBlockPos(), parent.getBlockState());
		}

		@Override
		public boolean stillValid(Player player) 
		{
			if (parent.getLevel().getBlockEntity(parent.getBlockPos()) != parent) 
				return false;
			
			return !(player.distanceToSqr((double)parent.getBlockPos().getX() + 0.5D, (double)parent.getBlockPos().getY() + 0.5D, (double)parent.getBlockPos().getZ() + 0.5D) > 64.0D);
		}
		
		@Override
		public void setChanged() 
		{
			parent.setChanged();
		}
	};
	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() 
	{
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) 
		{
			GarbageBinBlockEntity.playSound(level, pos, CommonContent.GARBAGEBIN_OPEN.get());
		}
		
		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) 
		{
			GarbageBinBlockEntity.playSound(level, pos, CommonContent.GARBAGEBIN_CLOSE.get());
		}
		
		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int p_155364_, int openerCount) 
		{
			GarbageBinBlockEntity.this.signalOpenCount(level, pos, state, p_155364_, openerCount);
		}
		
		@Override
		protected boolean isOwnContainer(Player player) 
		{
			if (!(player.containerMenu instanceof GarbageBinMenu)) 
				return false;
			
	        return ((GarbageBinMenu)player.containerMenu).getContainer() == GarbageBinBlockEntity.this.inventory;
		}
	};
	private final ChestLidController lidController = new ChestLidController();

	private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(this::createHandler);

	public GarbageBinBlockEntity(BlockPos pos, BlockState state) 
	{
		super(CommonContent.GARBAGEBIN_BLOCKENTITY_TYPE.get(), pos, state);
	}
	
	public Container getContainer()
	{
		return this.inventory;
	}
	
	public IItemHandler getFilter()
	{
		return this.filter;
	}
	
	public boolean isItemValid(ItemStack stack)
	{
		if (this.filterState == SettingsState.NORMAL)
			return this.filter.contains(stack);
		
		if (this.filterState == SettingsState.INVERTED)
			return !this.filter.contains(stack);
		
		return this.filterState == SettingsState.DISABLED;
	}
	
	public boolean isAutomationValid()
	{
		if (this.redstoneState == SettingsState.NORMAL)
			return !this.getBlockState().getValue(BaseBinBlock.POWERED);

		if (this.redstoneState == SettingsState.INVERTED)
			return this.getBlockState().getValue(BaseBinBlock.POWERED);
		
		return this.redstoneState == SettingsState.DISABLED;
	}
	
	@Override
	public void setFilterState(SettingsState state)
	{
		super.setFilterState(state);
		
		if (state == SettingsState.DISABLED)
			this.filter.clear();
	}

	@Override
	public float getOpenNess(float partialTicks) 
	{
		return this.lidController.getOpenness(partialTicks);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) 
	{
		return new GarbageBinMenu(id, playerInventory, this);
	}

	@Override
	public Component getDisplayName() 
	{
		return Component.translatable("block.garbagebins.garbagebin");
	}
	
	@Override
	public void load(CompoundTag tag) 
	{
		super.load(tag);
		
		if (tag.contains("lastItems"))
			this.inventory.load(tag.getCompound("lastItems"));
		
		if (tag.contains("filterItems"))
			this.filter.load(tag.getCompound("filterItems"));
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) 
	{
		super.saveAdditional(tag);
		
		tag.put("lastItems", this.inventory.save(new CompoundTag()));
		tag.put("filterItems", this.filter.save(new CompoundTag()));
	}
	
	@Override
	public boolean triggerEvent(int id, int openerCount) 
	{
		if (id == 1) 
		{
			this.lidController.shouldBeOpen(openerCount > 0);
			return true;
		}
		
		return super.triggerEvent(id, openerCount);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) 
	{
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) 
		{
			if (this.itemHandler == null)
				this.itemHandler = LazyOptional.of(this::createHandler);
			
			return this.itemHandler.cast();
	    }
		
		return super.getCapability(cap, side);
	}
	
	@Override
	public void invalidateCaps() 
	{
		super.invalidateCaps();
		if (itemHandler != null) 
		{
			itemHandler.invalidate();
			itemHandler = null;
		}
	}
	
	@Override
	public void reviveCaps() 
	{
		super.reviveCaps();
		itemHandler = LazyOptional.of(this::createHandler);
	}
	
	public void recheckOpen() 
	{
		if (!this.remove)
			this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
	}
	
	public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, GarbageBinBlockEntity blockEntity) 
	{
		blockEntity.lidController.tickLid();
	}
	
	protected static void playSound(Level level, BlockPos pos, SoundEvent sound)
	{
		level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sound, SoundSource.BLOCKS, 0.7F, level.random.nextFloat() * 0.1F + 0.9F);
	}
	
	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int p_155336_, int openerCount) 
	{
		Block block = state.getBlock();
		level.blockEvent(pos, block, 1, openerCount);
	}
	
	private IItemHandler createHandler() 
	{
		return new IItemHandler()
		{
			@Override
			public int getSlots() 
			{
				return 1;
			}

			@Override
			public @NotNull ItemStack getStackInSlot(int slot) 
			{
				return ItemStack.EMPTY;
			}

			@Override
			public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) 
			{
				if (!GarbageBinBlockEntity.this.isAutomationValid() || !GarbageBinBlockEntity.this.isItemValid(stack))
					return stack;
				
				if (!simulate)
					GarbageBinBlockEntity.this.inventory.addToBuffer(stack);
				
				return ItemStack.EMPTY;
			}

			@Override
			public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) 
			{
				return ItemStack.EMPTY;
			}

			@Override
			public int getSlotLimit(int slot) 
			{
				return Integer.MAX_VALUE;
			}

			@Override
			public boolean isItemValid(int slot, @NotNull ItemStack stack) 
			{
				return slot == 0 && GarbageBinBlockEntity.this.isItemValid(stack);
			}
		};
	}
}
