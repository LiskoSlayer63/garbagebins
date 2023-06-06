package lizcraft.garbagebins.common.block.entity.interfaces;

import lizcraft.garbagebins.common.SettingsState;

public interface IRedstoneState 
{
	SettingsState getRedstoneState();
	void setRedstoneState(SettingsState state);
}
