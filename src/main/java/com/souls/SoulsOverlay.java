package com.souls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import net.runelite.api.Point;
import java.text.NumberFormat;
import javax.inject.Inject;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class SoulsOverlay extends Overlay
{
	private final SoulsPlugin plugin;
	private final SoulsConfig config;

	@Inject
	private SoulsOverlay(SoulsPlugin plugin, SoulsConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		if (!config.showOverlay() || plugin.getIcon() == null)
		{
			return null;
		}
		String text = NumberFormat.getIntegerInstance().format(plugin.getTotal());
		g.setFont(FontManager.getRunescapeBoldFont());
		FontMetrics fm = g.getFontMetrics();
		int size = 20;
		int gap = 4;

		g.drawImage(plugin.getIcon(), 0, 0, null);
		int textY = (size + fm.getAscent() - fm.getDescent()) / 2;
		OverlayUtil.renderTextLocation(g, new Point(size + gap, textY), text, Color.WHITE);

		return new Dimension(size + gap + fm.stringWidth(text), size);
	}
}
