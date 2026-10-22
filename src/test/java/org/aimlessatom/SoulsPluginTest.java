package org.aimlessatom;

import com.souls.SoulsPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SoulsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SoulsPlugin.class);
		RuneLite.main(args);
	}
}