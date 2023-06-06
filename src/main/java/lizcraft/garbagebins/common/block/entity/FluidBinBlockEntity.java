package lizcraft.garbagebins.common.block.entity;

import org.jetbrains.annotations.NotNull;

import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.block.entity.interfaces.IQuickUseState;
import lizcraft.garbagebins.common.gui.FluidBinMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidBinBlockEntity extends BaseBinBlockEntity implements IQuickUseState
{
	protected boolean quickUseState = true;
	
	private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(this::createHandler);
	
	public FluidBinBlockEntity(BlockPos pos, BlockState state) 
	{
		super(CommonContent.FLUIDBIN_BLOCKENTITY_TYPE.get(), pos, state);
	}
	
	@Override
	public boolean getQuickUseState() 
	{
		return this.quickUseState;
	}

	@Override
	public void setQuickUseState(boolean state) 
	{
		this.quickUseState = state;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) 
	{
		return new FluidBinMenu(id, playerInventory, this);
	}

	@Override
	public Component getDisplayName() 
	{
		return Component.translatable("block.garbagebins.fluidbin");
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) 
	{
		if (!this.remove && cap == ForgeCapabilities.FLUID_HANDLER) 
		{
			if (this.fluidHandler == null)
				this.fluidHandler = LazyOptional.of(this::createHandler);
			
			return this.fluidHandler.cast();
	    }
		
		return super.getCapability(cap, side);
	}
	
	@Override
	public void invalidateCaps() 
	{
		super.invalidateCaps();
		if (fluidHandler != null) 
		{
			fluidHandler.invalidate();
			fluidHandler = null;
		}
	}
	
	@Override
	public void reviveCaps() 
	{
		super.reviveCaps();
		fluidHandler = LazyOptional.of(this::createHandler);
	}
	
	@Override
	public void load(CompoundTag tag) 
	{
		super.load(tag);
		
		if (tag.contains("quickUseState"))
			this.quickUseState = tag.getBoolean("quickUseState");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) 
	{
		super.saveAdditional(tag);
		
		tag.putBoolean("quickUseState", quickUseState);
	}

	@Override
	public CompoundTag getUpdateTag() 
	{
		CompoundTag tag = super.getUpdateTag();

		tag.putBoolean("quickUseState", quickUseState);
		
		return tag;
	}
	
	private IFluidHandler createHandler() 
	{
		return new IFluidHandler()
		{
			@Override
			public int getTanks() 
			{
				return 1;
			}

			@Override
			public @NotNull FluidStack getFluidInTank(int tank) 
			{
				return FluidStack.EMPTY;
			}

			@Override
			public int getTankCapacity(int tank) 
			{
				return Integer.MAX_VALUE;
			}

			@Override
			public boolean isFluidValid(int tank, @NotNull FluidStack stack) 
			{
				return true;
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) 
			{
				return resource.getAmount();
			}

			@Override
			public @NotNull FluidStack drain(FluidStack resource, FluidAction action) 
			{
				return FluidStack.EMPTY;
			}

			@Override
			public @NotNull FluidStack drain(int maxDrain, FluidAction action) 
			{
				return FluidStack.EMPTY;
			}
		};
	}
}
