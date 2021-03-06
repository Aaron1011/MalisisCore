/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 PaleoCrafter, Ordinastie
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

package net.malisis.core.client.gui.component.decoration;

import java.util.Arrays;
import java.util.List;

import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.UIComponent;
import net.malisis.core.client.gui.component.element.Size;
import net.malisis.core.client.gui.element.XYResizableGuiShape;
import net.malisis.core.renderer.animation.Animation;
import net.malisis.core.renderer.animation.transformation.ITransformable;
import net.malisis.core.renderer.font.FontOptions;
import net.malisis.core.renderer.font.MalisisFont;
import net.malisis.core.renderer.icon.provider.GuiIconProvider;

/**
 * UITooltip
 *
 * @author PaleoCrafter
 */
public class UITooltip extends UIComponent<UITooltip>
{
	/** The {@link MalisisFont} to use for this {@link UITooltip}. */
	protected MalisisFont font = MalisisFont.minecraftFont;
	/** The {@link FontOptions} to use for this {@link UITooltip}. */
	protected FontOptions fontOptions = FontOptions.builder().color(0xFFFFFF).shadow().build();

	protected List<String> lines;
	protected int padding = 4;
	protected int delay = 0;
	protected Animation<ITransformable.Alpha> animation;

	public UITooltip(MalisisGui gui)
	{
		super(gui);
		zIndex = 300;

		shape = new XYResizableGuiShape();
		iconProvider = new GuiIconProvider(gui.getGuiTexture().getXYResizableIcon(227, 31, 15, 15, 5));

		//animation = new Animation<>(this, new AlphaTransform(0, 255).forTicks(2));
	}

	public UITooltip(MalisisGui gui, String text)
	{
		this(gui);
		setText(text);
	}

	public UITooltip(MalisisGui gui, int delay)
	{
		this(gui);
		setDelay(delay);
	}

	public UITooltip(MalisisGui gui, String text, int delay)
	{
		this(gui);
		setText(text);
		setDelay(delay);
	}

	//#region Getters/Setters
	public UITooltip setText(String text)
	{
		lines = Arrays.asList(text.split("\\n"));
		calculateSize();
		return this;

	}

	public UITooltip setText(List<String> lines)
	{
		this.lines = lines;
		calculateSize();
		return this;
	}

	public UITooltip setDelay(int delay)
	{
		this.delay = delay;
		return this;
	}

	public int getDelay()
	{
		return delay;
	}

	protected int getOffsetX()
	{
		return 8;
	}

	protected int getOffsetY()
	{
		return -16;
	}

	//#end Getters/Setters

	protected void calculateSize()
	{
		setSize(Size.of((int) font.getMaxStringWidth(lines, fontOptions) + padding * 2,
						(int) (lines.size() > 1 ? font.getStringHeight(fontOptions) * lines.size() : 8) + padding * 2));
	}

	public void animate()
	{
		if (delay == 0)
			return;

		setAlpha(0);
		//getGui().animate(animation, delay);
	}

	@Override
	public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		shape.setPosition(mouseX + getOffsetX(), mouseY + getOffsetY());
		renderer.drawShape(shape, rp);
	}

	@Override
	public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		int x = mouseX + getOffsetX() + padding;
		int y = mouseY + getOffsetY() + padding;
		int i = 0;
		for (String str : lines)
		{
			int sy = y;
			if (i > 0)
				sy += 2;
			renderer.drawText(font, str, x, sy + font.getStringHeight(fontOptions) * i, zIndex + 1, fontOptions, false);
			i++;
		}
	}
}
