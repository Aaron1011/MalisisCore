/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.core.renderer.font;

import net.minecraft.util.text.TextFormatting;

/**
 * @author Ordinastie
 *
 */
public class StringWalker
{
	private MalisisFont font;
	private FontOptions fontOptions;
	private String str;
	private boolean litteral;
	private boolean skipChars = true;
	private boolean applyStyles;
	private boolean isText;

	//	private int prevColor;
	//	private boolean prevUnderline;

	private int index;
	private int endIndex;
	private char c;
	private TextFormatting format;
	private Link link;
	private float width;

	public StringWalker(String str, MalisisFont font, FontOptions options)
	{
		this.str = str;
		this.font = font;
		this.fontOptions = options;
		this.index = 0;
		this.endIndex = str.length();
		this.litteral = options != null && options.isFormattingDisabled();
	}

	//#region Getters/Setters

	public void setLitteral(boolean litteral)
	{
		this.litteral = litteral;
	}

	public void skipChars(boolean skip)
	{
		this.skipChars = skip;
	}

	public void applyStyles(boolean apply)
	{
		this.applyStyles = apply;
	}

	public boolean isApplyStyles()
	{
		return applyStyles;
	}

	public int getIndex()
	{
		return index;
	}

	public char getChar()
	{
		return c;
	}

	public TextFormatting getFormatting()
	{
		return format;
	}

	public boolean isFormatted()
	{
		return format != null;
	}

	public Link getLink()
	{
		return link;
	}

	public boolean isLink()
	{
		return link != null;
	}

	public float getWidth()
	{
		return width;
	}

	public void startIndex(int index)
	{
		this.index = index;
	}

	public void endIndex(int index)
	{
		if (index == 0)
			index = str.length();
		this.endIndex = index;
	}

	private void setLinkStyle(FontOptions options)
	{
		if (options == null || litteral || !applyStyles)
			return;

		//prevColor = fro.color;
		//prevUnderline = fro.underline;
		//		fro.saveDefault();
		//fro.color = 0x6666FF;
		//fro.underline = true;
	}

	private void resetLinkStyle(FontOptions options)
	{
		if (options == null || litteral || !applyStyles)
			return;

		//fro.color = prevColor;
		//fro.underline = prevUnderline;
	}

	//#end Getters/Setters

	private void checkFormatting()
	{
		format = FontOptions.getFormatting(str, index);
		if (format == null)
			format = FontOptions.getFormatting(str, index - 1);

		if (format == null)
			return;

		if (applyStyles && fontOptions != null && !isLink())
			fontOptions.apply(format);

		if (skipChars && !litteral)
		{
			index += 2;
			checkFormatting();
		}
	}

	public void checkLink()
	{
		if (link != null)
		{
			isText = link.isText(getIndex());

			if (str.charAt(index) == ']')
			{
				resetLinkStyle(fontOptions);
				if (skipChars && !litteral)
					index++;
			}
		}
		else
		{
			link = FontOptions.getLink(str, index);
			if (isLink())
			{
				if (skipChars && !litteral)
					index += link.indexAdvance();
				setLinkStyle(fontOptions);
			}
		}

	}

	public int walkTo(float x)
	{
		float width = 0;
		while (walk())
		{
			width += getWidth();
			if (width > x)
				return getIndex() - 1;
		}

		return getIndex();
	}

	public boolean walk()
	{
		if (index >= endIndex)
			return false;

		checkFormatting();
		//checkLink();

		if (index >= endIndex)
			return false;

		c = str.charAt(index);
		width = font.getCharWidth(c, fontOptions);
		if (fontOptions != null && fontOptions.isBold())
			width += fontOptions.getFontScale();

		if (!litteral && !skipChars && (format != null || (link != null && !isText)))
			width = 0;

		index++;
		return true;
	}
}
