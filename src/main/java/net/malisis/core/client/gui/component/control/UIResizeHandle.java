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

package net.malisis.core.client.gui.component.control;

import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.UIComponent;
import net.malisis.core.client.gui.component.element.Padding;
import net.malisis.core.client.gui.component.element.Position;
import net.malisis.core.client.gui.component.element.Size;
import net.malisis.core.renderer.icon.provider.GuiIconProvider;
import net.malisis.core.util.MouseButton;

/**
 * @author Ordinastie
 *
 */
public class UIResizeHandle extends UIComponent<UIResizeHandle> implements IControlComponent
{
	public enum Type
	{
		BOTH,
		HORIZONTAL,
		VERTICAL
	}

	private Type type;

	public UIResizeHandle(MalisisGui gui, UIComponent<?> parent, Type type)
	{
		super(gui);
		this.type = type != null ? type : Type.BOTH;

		Padding padding = Padding.of(parent);
		setPosition(Position.builder().rightAligned(-padding.right()).bottomAligned(-padding.bottom()).build());
		setSize(Size.of(5, 5));
		register(this);

		parent.addControlComponent(this);

		iconProvider = new GuiIconProvider(gui.getGuiTexture().getIcon(268, 0, 15, 15));
	}

	public UIResizeHandle(MalisisGui gui, UIComponent<?> parent)
	{
		this(gui, parent, Type.BOTH);
	}

	@Override
	public boolean onDrag(int lastX, int lastY, int x, int y, MouseButton button)
	{
		if (button != MouseButton.LEFT)
			return super.onDrag(lastX, lastY, x, y, button);

		int w = getParent().size().width();
		int h = getParent().size().height();
		if (type == Type.BOTH || type == Type.HORIZONTAL)
			w += x - lastX;
		if (type == Type.BOTH || type == Type.VERTICAL)
			h += y - lastY;
		if (w < 10)
			w = 10;
		if (h < 10)
			h = 10;

		getParent().setSize(Size.of(w, h));

		return true;
	}

	@Override
	public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{}

	@Override
	public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		renderer.drawShape(shape, rp);
	}
}
