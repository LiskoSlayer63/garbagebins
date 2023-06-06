package lizcraft.garbagebins.common.block.entity.interfaces;

import lizcraft.garbagebins.common.SettingsState;

public interface IFilterState 
{
	SettingsState getFilterState();
	void setFilterState(SettingsState state);
}