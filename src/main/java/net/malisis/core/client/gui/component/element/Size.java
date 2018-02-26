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

import static com.google.common.base.Preconditions.*;

import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

import javax.annotation.Nonnull;

import net.malisis.core.client.gui.component.UIComponent;

/**
 * @author Ordinastie
 *
 */
public interface Size
{
	public interface ISize
	{
		public default void setOwner(UIComponent<?> owner)
		{}

		public int width();

		public int height();
	}

	public interface WidthFunction extends ToIntFunction<UIComponent<?>>
	{
	}

	public interface HeightFunction extends ToIntFunction<UIComponent<?>>
	{
	}

	public static class DynamicSize implements ISize
	{
		private final WidthFunction width;
		private final HeightFunction height;
		private UIComponent<?> owner;

		public DynamicSize(@Nonnull WidthFunction width, @Nonnull HeightFunction height)
		{
			this.width = checkNotNull(width);
			this.height = checkNotNull(height);
		}

		@Override
		public void setOwner(@Nonnull UIComponent<?> owner)
		{
			this.owner = checkNotNull(owner);
		}

		@Override
		public int width()
		{
			return width.applyAsInt(owner);
		}

		@Override
		public int height()
		{
			return height.applyAsInt(owner);
		}
	}

	public static ISize of(int width, int height)
	{
		return width(width).height(height);
	}

	public static ISize inherited()
	{
		return relativeWidth(1.0F).relativeHeight(1.0F);
	}

	public static ISize contentSize()
	{
		return new DynamicSize(Sizes.contentWidth(0), Sizes.contentHeight(0));
	}

	public static ISize contentSize(int widthOffset, int heightOffset)
	{
		return new DynamicSize(Sizes.contentWidth(widthOffset), Sizes.contentHeight(heightOffset));
	}

	public static ISize matches(@Nonnull UIComponent<?> other)
	{
		checkNotNull(other);
		return widthRelativeTo(1.0F, other).heightRelativeTo(1.0F, other);
	}

	public static SizeFactory width(WidthFunction widthFunction)
	{
		return new SizeFactory(widthFunction);
	}

	public static SizeFactory width(int width)
	{
		return width(owner -> width);
	}

	public static SizeFactory width(IntSupplier supplier)
	{
		return width(owner -> supplier.getAsInt());
	}

	public static SizeFactory relativeWidth(float width)
	{
		return relativeWidth(width, 0);
	}

	public static SizeFactory relativeWidth(float width, int offset)
	{
		return width(Sizes.relativeWidth(width, offset));
	}

	public static SizeFactory widthRelativeTo(float width, @Nonnull UIComponent<?> other)
	{
		return widthRelativeTo(width, other, 0);
	}

	public static SizeFactory widthRelativeTo(float width, @Nonnull UIComponent<?> other, int offset)
	{

		return width(Sizes.widthRelativeTo(width, other, offset));
	}

	public class SizeFactory
	{
		private WidthFunction widthFunction;
		private HeightFunction heightFunction;

		public SizeFactory(WidthFunction widthFunction)
		{
			this.widthFunction = widthFunction;
		}

		public ISize height(HeightFunction heightFunction)
		{
			this.heightFunction = heightFunction;
			return build();
		}

		public ISize height(int height)
		{
			return height(owner -> height);
		}

		public ISize height(IntSupplier supplier)
		{
			return height(owner -> supplier.getAsInt());
		}

		public ISize relativeHeight(float height)
		{
			return relativeHeight(height, 0);
		}

		public ISize relativeHeight(float height, int offset)
		{
			return height(Sizes.relativeHeight(height, offset));
		}

		public ISize heightRelativeTo(float height, UIComponent<?> other)
		{
			return heightRelativeTo(height, other, 0);
		}

		public ISize heightRelativeTo(float height, UIComponent<?> other, int offset)
		{
			return height(Sizes.heightRelativeTo(height, other, offset));
		}

		private ISize build()
		{
			return new DynamicSize(widthFunction, heightFunction);
		}
	}
}
