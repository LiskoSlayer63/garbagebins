package lizcraft.garbagebins.common.block.entity;

import lizcraft.garbagebins.common.SettingsState;
import lizcraft.garbagebins.common.block.entity.interfaces.IFilterState;
import lizcraft.garbagebins.common.block.entity.interfaces.IRedstoneState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseBinBlockEntity extends BlockEntity implements MenuProvider, IFilterState, IRedstoneState
{
	protected SettingsState filterState = SettingsState.DISABLED;
	protected SettingsState redstoneState = SettingsState.NORMAL;

	public BaseBinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) 
	{
		super(type, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		this.level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
	}
	
	@Override
	public SettingsState getRedstoneState()
	{
		return this.redstoneState;
	}

	@Override
	public SettingsState getFilterState()
	{
		return this.filterState;
	}

	@Override
	public void setRedstoneState(SettingsState state)
	{
		this.redstoneState = state;
	}

	@Override
	public void setFilterState(SettingsState state)
	{
		this.filterState = state;
	}

	@Override
	public void load(CompoundTag tag) 
	{
		super.load(tag);
		
		if (tag.contains("filterState"))
			this.filterState = SettingsState.load(tag.getByte("filterState"));
		
		if (tag.contains("redstoneState"))
			this.redstoneState = SettingsState.load(tag.getByte("redstoneState"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) 
	{
		super.saveAdditional(tag);
		
		tag.putByte("filterState", this.filterState.save());
		tag.putByte("redstoneState", this.redstoneState.save());
	}

	@Override
	public CompoundTag getUpdateTag() 
	{
		CompoundTag tag = super.getUpdateTag();

		tag.putByte("filterState", this.filterState.save());
		tag.putByte("redstoneState", this.redstoneState.save());
		
		return tag;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() 
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
