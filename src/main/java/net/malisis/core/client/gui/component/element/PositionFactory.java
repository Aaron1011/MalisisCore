/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Ordinastie
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

package net.malisis.core.client.gui.component.element;

import java.util.function.ToIntFunction;

import net.malisis.core.client.gui.component.UIComponent;
import net.malisis.core.client.gui.component.element.Position.DynamicPosition;
import net.malisis.core.client.gui.component.element.Position.IPosition;

/**
 * @author Ordinastie
 *
 */
public class PositionFactory
{
	private ToIntFunction<UIComponent<?>> xFunction;
	private ToIntFunction<UIComponent<?>> yFunction;

	public PositionFactory(ToIntFunction<UIComponent<?>> xFunction)
	{
		this.xFunction = xFunction;
	}

	//absolute position
	public IPosition y(int y)
	{
		yFunction = owner -> {
			return Padding.of(owner.getParent()).top() + y;
		};;
		return build();
	}

	//relative postion
	public IPosition above(UIComponent<?> component)
	{
		return above(component, 0);
	}

	public IPosition above(UIComponent<?> component, int spacing)
	{
		yFunction = owner -> {
			return component.position().y() - owner.size().height() - spacing;
		};
		return build();
	}

	public IPosition below(UIComponent<?> component)
	{
		return below(component, 0);
	}

	public IPosition below(UIComponent<?> component, int spacing)
	{
		yFunction = owner -> {
			return component.position().y() + component.size().height() + spacing;
		};
		return build();
	}

	public IPosition topAligned()
	{
		return topAligned(0);
	}

	public IPosition topAligned(int spacing)
	{
		return y(0);
	}

	public IPosition bottomAligned()
	{
		return bottomAligned(0);
	}

	public IPosition bottomAligned(int spacing)
	{
		yFunction = owner -> {
			UIComponent<?> parent = owner.getParent();
			if (owner.getParent() == null)
				return 0;
			return parent.size().height() - owner.size().height() - Padding.of(parent).bottom() - spacing;
		};
		return build();
	}

	public IPosition middleAligned()
	{
		return middleAligned(0);
	}

	public IPosition middleAligned(int offset)
	{
		yFunction = owner -> {
			UIComponent<?> parent = owner.getParent();
			if (owner.getParent() == null)
				return 0;
			return (parent.size().height() - Padding.of(parent).vertical() - owner.size().height()) / 2 + offset;
		};
		return build();
	}

	//aligned relative to another component
	public IPosition topAlignedTo(UIComponent<?> other)
	{
		return topAlignedTo(other, 0);
	}

	public IPosition topAlignedTo(UIComponent<?> other, int offset)
	{
		yFunction = owner -> {
			return other.position().y() + offset;
		};
		return build();
	}

	public IPosition bottomAlignedTo(UIComponent<?> other)
	{
		return bottomAlignedTo(other, 0);
	}

	public IPosition bottomAlignedTo(UIComponent<?> other, int offset)
	{
		yFunction = owner -> {
			return other.position().y() + other.size().height() - owner.size().height() + offset;
		};
		return build();
	}

	public IPosition middleAlignedTo(UIComponent<?> other)
	{
		return middleAlignedTo(other, 0);
	}

	public IPosition middleAlignedTo(UIComponent<?> other, int offset)
	{
		yFunction = owner -> {
			return other.position().y() + (other.size().height() - owner.size().height()) / 2 + offset;
		};
		return build();
	}

	public IPosition build()
	{
		return new DynamicPosition(xFunction, yFunction);
	}

}
