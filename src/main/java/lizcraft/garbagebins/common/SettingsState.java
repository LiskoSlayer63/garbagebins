package lizcraft.garbagebins.common;

public enum SettingsState
{
	NORMAL(0),
	INVERTED(1),
	DISABLED(2);
	
	private int state;
	
	private SettingsState(int state)
	{
		this.state = state;
	}
	
	public SettingsState next()
	{
		return load(this.state + 1);
	}
	
	public static SettingsState load(int raw)
	{
		if (raw == 1)
			return INVERTED;
		
		if (raw == 2)
			return DISABLED;
		
		return NORMAL;
	}
	
	public byte save()
	{
		return (byte)this.state;
	}
}