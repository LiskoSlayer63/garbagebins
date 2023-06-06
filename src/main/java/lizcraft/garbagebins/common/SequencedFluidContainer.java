package lizcraft.garbagebins.common;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SequencedFluidContainer implements IFluidHandler
{
	private final NonNullList<FluidStack> fluids;
	
	public SequencedFluidContainer(int size)
	{
		this.fluids = NonNullList.withSize(size - 1, FluidStack.EMPTY);
	}

	@Override
	public int getTanks()
	{
		return this.fluids.size();
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) 
	{
		if (tank > 0 && tank <= this.fluids.size())
			return this.fluids.get(tank - 1).copy();
		
		return FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int tank) 
	{
		return tank == 0 ? Integer.MAX_VALUE : this.fluids.get(tank - 1).getAmount();
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
		return null;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		// TODO Auto-generated method stub
		return null;
	}
}
