package com.souls;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(SoulsConfig.GROUP)
public interface SoulsConfig extends Config
{
	String GROUP = "souls";

	@ConfigItem(keyName = "showOverlay", name = "Show on-screen total", description = "Show the soul total on screen", position = 1)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(keyName = "multiplier", name = "Souls per combat level", description = "Souls gained per level of the monster killed", position = 2)
	default int multiplier()
	{
		return 10;
	}
}
