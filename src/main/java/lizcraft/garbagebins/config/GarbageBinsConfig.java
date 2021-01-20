package lizcraft.garbagebins.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class GarbageBinsConfig 
{
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    
    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;
    
    static {
        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        // currently unused, but general settings config goes in here
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
