package com.souls;

/** Tiny expression evaluator: + - * / ( ) and k/m/b suffixes, e.g. "2.5m*3 - 400k". */
final class Calc
{
	private final String s;
	private int pos;

	private Calc(String s)
	{
		this.s = s;
	}

	static double eval(String input)
	{
		Calc c = new Calc(input.toLowerCase().replace(",", "").replace(" ", "").replace('x', '*'));
		double v = c.expr();
		if (c.pos != c.s.length())
		{
			throw new IllegalArgumentException("Unexpected: " + c.s.charAt(c.pos));
		}
		return v;
	}

	private double expr()
	{
		double v = term();
		while (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-'))
		{
			char op = s.charAt(pos++);
			double r = term();
			v = op == '+' ? v + r : v - r;
		}
		return v;
	}

	private double term()
	{
		double v = factor();
		while (pos < s.length() && (s.charAt(pos) == '*' || s.charAt(pos) == '/'))
		{
			char op = s.charAt(pos++);
			double r = factor();
			v = op == '*' ? v * r : v / r;
		}
		return v;
	}

	private double factor()
	{
		if (pos >= s.length())
		{
			throw new IllegalArgumentException("Unexpected end");
		}
		char c = s.charAt(pos);
		if (c == '-')
		{
			pos++;
			return -factor();
		}
		if (c == '(')
		{
			pos++;
			double v = expr();
			if (pos >= s.length() || s.charAt(pos) != ')')
			{
				throw new IllegalArgumentException("Missing )");
			}
			pos++;
			return v;
		}
		int start = pos;
		while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.'))
		{
			pos++;
		}
		if (start == pos)
		{
			throw new IllegalArgumentException("Number expected");
		}
		double v = Double.parseDouble(s.substring(start, pos));
		if (pos < s.length())
		{
			char suf = s.charAt(pos);
			if (suf == 'k') { v *= 1_000; pos++; }
			else if (suf == 'm') { v *= 1_000_000; pos++; }
			else if (suf == 'b') { v *= 1_000_000_000; pos++; }
		}
		return v;
	}
}
