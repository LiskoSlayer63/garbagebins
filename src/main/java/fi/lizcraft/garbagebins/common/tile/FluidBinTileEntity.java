package fi.lizcraft.garbagebins.common.tile;

import fi.lizcraft.garbagebins.common.CommonContent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidBinTileEntity extends TileEntity implements ICapabilityProvider
{
    private final LazyOptional<IFluidHandler> handler = LazyOptional.of(() -> new FluidBinHandler(this));
	
	public FluidBinTileEntity(TileEntityType<?> tileEntityTypeIn) 
	{
		super(tileEntityTypeIn);
	}

	public FluidBinTileEntity() 
	{
		super(CommonContent.FLUIDBIN_TILEENTITYTYPE);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) 
	{
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) 
			return this.handler.cast();
		return super.getCapability(capability, facing);
	}
	
	
	/*
	 * FLUID HANDLER FOR AUTOMATION
	 */
	
	public static class FluidBinHandler implements IFluidHandler
	{
		FluidBinTileEntity tileEntity;
		
		public FluidBinHandler(FluidBinTileEntity tileEntity)
		{
			this.tileEntity = tileEntity;
		}

		@Override
		public int getTanks() 
		{
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int tank) 
		{
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) 
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack stack) 
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) 
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) 
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) 
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
