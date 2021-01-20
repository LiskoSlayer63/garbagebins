package fi.lizcraft.garbagebins.utils;

public class Logger 
{
	private static org.apache.logging.log4j.Logger logger;
	private static boolean debug = false;
	private static boolean init = false;
	
	public static void init(org.apache.logging.log4j.Logger logger)
	{
		if(logger == null) 
			return;
		
		Logger.logger = logger;
		init = true;
	}
	
	public static void info(String msg)
	{
		if(init)
			logger.info(msg);
	}
	
	public static void info(Object msg)
	{
		if(init)
			logger.info(msg);
	}
	
	public static void debug(String msg)
	{
		if(init && debug)
			logger.info("[DEBUG]: " + msg);
	}
	
	public static void debug(Object msg)
	{
		if(init && debug)
			logger.info("[DEBUG]: " + msg);
	}
	
	public static void warn(String msg)
	{
		if(init)
			logger.warn(msg);
	}
	
	public static void warn(Object msg)
	{
		if(init)
			logger.warn(msg);
	}
	
	public static void error(String msg)
	{
		if(init)
			logger.error(msg);
	}
	
	public static void error(Exception e)
	{
		if(init)
			logger.error(e);
	}
	
	public static void error(Object e)
	{
		if(init)
			logger.error(e);
	}
	
	public static boolean isDebugEnabled()
	{
		return debug;
	}
	
	public static void enableDebug(boolean debug)
	{
		Logger.debug = debug;
		
		if(debug)
			Logger.info("DEBUG MODE ENABLED!");
	}
	
	public static boolean isInit()
	{
		return init;
	}
}
