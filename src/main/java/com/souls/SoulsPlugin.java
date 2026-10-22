package com.souls;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(
	name = "Souls",
	description = "Track souls earned from kills and spent like gold",
	tags = {"souls", "challenge", "counter"}
)
public class SoulsPlugin extends Plugin
{
	private static final String TOTAL_KEY = "total";

	@Inject
	private Client client;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private SoulsOverlay overlay;
	@Inject
	private ConfigManager configManager;
	@Inject
	private SoulsConfig config;

	private SoulsPanel panel;
	private NavigationButton navButton;
	private BufferedImage icon;
	private long total;
	private final Set<NPC> engaged = new HashSet<>();

	@Override
	protected void startUp()
	{
		Long saved = configManager.getConfiguration(SoulsConfig.GROUP, TOTAL_KEY, Long.class);
		total = saved == null ? 0 : saved;

		icon = ImageUtil.resizeImage(ImageUtil.loadImageResource(SoulsPlugin.class, "soul_icon.png"), 20, 20);

		panel = new SoulsPanel(this, icon);
		panel.refresh(total);

		navButton = NavigationButton.builder()
			.tooltip("Souls")
			.icon(ImageUtil.resizeImage(icon, 16, 16))
			.priority(7)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navButton);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		overlayManager.remove(overlay);
		engaged.clear();
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied e)
	{
		if (e.getActor() instanceof NPC && e.getHitsplat().isMine())
		{
			engaged.add((NPC) e.getActor());
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath e)
	{
		if (!(e.getActor() instanceof NPC))
		{
			return;
		}
		NPC npc = (NPC) e.getActor();
		if (engaged.remove(npc))
		{
			int level = npc.getCombatLevel();
			if (level > 0)
			{
				addSouls((long) level * config.multiplier());
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned e)
	{
		engaged.remove(e.getNpc());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		if (e.getGameState() == GameState.LOADING)
		{
			engaged.clear();
		}
	}

	public void addSouls(long delta)
	{
		setTotal(total + delta);
	}

	public void setTotal(long value)
	{
		total = Math.max(0, value);
		configManager.setConfiguration(SoulsConfig.GROUP, TOTAL_KEY, total);
		if (panel != null)
		{
			SwingUtilities.invokeLater(() -> panel.refresh(total));
		}
	}

	public long getTotal()
	{
		return total;
	}

	public BufferedImage getIcon()
	{
		return icon;
	}

	@Provides
	SoulsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SoulsConfig.class);
	}
}
