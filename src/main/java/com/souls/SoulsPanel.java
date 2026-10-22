package com.souls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class SoulsPanel extends PluginPanel
{
	private final SoulsPlugin plugin;
	private final JLabel totalLabel = new JLabel("0", SwingConstants.CENTER);
	private final JTextField amountField = new JTextField("1000");
	private final JTextField setField = new JTextField();
	private final JTextField calcField = new JTextField();
	private final JLabel calcResult = new JLabel("= ", SwingConstants.CENTER);
	private final JLabel message = new JLabel(" ", SwingConstants.CENTER);

	public SoulsPanel(SoulsPlugin plugin, BufferedImage icon)
	{
		super();
		this.plugin = plugin;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		totalLabel.setIcon(new ImageIcon(icon));
		totalLabel.setFont(totalLabel.getFont().deriveFont(22f));
		totalLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(totalLabel);

		// --- Quick adjust with arrows ---
		JButton up = new JButton("\u25B2");
		JButton down = new JButton("\u25BC");
		up.setToolTipText("Add souls");
		down.setToolTipText("Spend souls");
		up.addActionListener(e -> apply(parseField(amountField), 1));
		down.addActionListener(e -> apply(parseField(amountField), -1));
		JPanel arrows = new JPanel(new GridLayout(1, 2, 4, 0));
		arrows.add(down);
		arrows.add(up);
		JPanel adjustRow = new JPanel(new BorderLayout(6, 0));
		adjustRow.add(amountField, BorderLayout.CENTER);
		adjustRow.add(arrows, BorderLayout.EAST);
		add(section("Adjust by (k / m / b allowed)", adjustRow));

		// --- Set exact total ---
		JButton set = new JButton("Set");
		set.addActionListener(e ->
		{
			Long v = parseField(setField);
			if (v != null)
			{
				plugin.setTotal(v);
				setField.setText("");
				info(" ");
			}
		});
		JPanel setRow = new JPanel(new BorderLayout(6, 0));
		setRow.add(setField, BorderLayout.CENTER);
		setRow.add(set, BorderLayout.EAST);
		add(section("Set total", setRow));

		// --- Calculator ---
		JButton sub = new JButton("\u2212 Spend");
		JButton addBtn = new JButton("+ Add");
		sub.addActionListener(e -> apply(parseField(calcField), -1));
		addBtn.addActionListener(e -> apply(parseField(calcField), 1));
		JPanel calcButtons = new JPanel(new GridLayout(1, 2, 4, 0));
		calcButtons.add(sub);
		calcButtons.add(addBtn);
		JPanel calc = new JPanel();
		calc.setLayout(new BoxLayout(calc, BoxLayout.Y_AXIS));
		calc.add(calcField);
		calc.add(Box.createVerticalStrut(4));
		calc.add(calcResult);
		calc.add(Box.createVerticalStrut(4));
		calc.add(calcButtons);
		calcField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e) { updateCalc(); }
			public void removeUpdate(DocumentEvent e) { updateCalc(); }
			public void changedUpdate(DocumentEvent e) { updateCalc(); }
		});
		add(section("Calculator, e.g. 2.5m*3 - 400k", calc));

		message.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
		add(message);
	}

	public void refresh(long total)
	{
		totalLabel.setText(NumberFormat.getIntegerInstance().format(total));
	}

	private void apply(Long amount, int sign)
	{
		if (amount == null)
		{
			return;
		}
		info(" ");
		plugin.addSouls(sign * amount);
	}

	private Long parseField(JTextField field)
	{
		try
		{
			double v = Calc.eval(field.getText());
			if (Double.isNaN(v) || Double.isInfinite(v))
			{
				throw new IllegalArgumentException();
			}
			return Math.round(v);
		}
		catch (RuntimeException ex)
		{
			info("Couldn't read that number");
			return null;
		}
	}

	private void updateCalc()
	{
		String t = calcField.getText().trim();
		if (t.isEmpty())
		{
			calcResult.setText("= ");
			return;
		}
		try
		{
			calcResult.setText("= " + NumberFormat.getIntegerInstance().format(Math.round(Calc.eval(t))));
		}
		catch (RuntimeException ex)
		{
			calcResult.setText("= ?");
		}
	}

	private void info(String s)
	{
		message.setText(s);
	}

	private static JPanel section(String title, JPanel content)
	{
		JPanel p = new JPanel(new BorderLayout(0, 4));
		p.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
		JLabel l = new JLabel(title);
		l.setForeground(Color.LIGHT_GRAY);
		p.add(l, BorderLayout.NORTH);
		p.add(content, BorderLayout.CENTER);
		return p;
	}
}
