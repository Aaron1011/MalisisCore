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

package net.malisis.core.client.gui.component.interaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.malisis.core.client.gui.ClipArea;
import net.malisis.core.client.gui.GuiIcon;
import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.component.UIComponent;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.event.ComponentEvent;
import net.malisis.core.client.gui.event.KeyboardEvent;
import net.malisis.core.client.gui.event.MouseEvent;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.preset.ShapePreset;
import net.malisis.core.util.MouseButton;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

/**
 * @author Ordinastie
 * 
 */
public class UISelect extends UIComponent<UISelect>
{
	//@formatter:off
	public static GuiIcon[] iconsSelect = new GuiIcon[] { 		new GuiIcon(200,	30, 	3, 		12),
																new GuiIcon(203,	30, 	3, 		12),
																new GuiIcon(206, 	30, 	3, 		12)};
	public static GuiIcon[] iconsSelectDisabled = new GuiIcon[] { 	iconsSelect[0].offsetCopy(0,  12),
																	iconsSelect[1].offsetCopy(0,  12),
																	iconsSelect[2].offsetCopy(0,  12) };
	public GuiIcon[] iconsExpanded = new GuiIcon[] { 			new GuiIcon(200,	30, 	3, 		3),
																new GuiIcon(203,	30, 	3, 		3),
																new GuiIcon(206, 	30, 	3, 		3),
																new GuiIcon(200, 	33, 	3, 		3),
																new GuiIcon(203, 	33, 	3, 		3),
																new GuiIcon(206, 	33, 	3, 		3),
																new GuiIcon(200, 	39, 	3, 		3),
																new GuiIcon(203, 	39, 	3, 		3),
																new GuiIcon(206, 	39, 	3, 		3)};
	//@formatter:on
	public static GuiIcon arrowIcon = new GuiIcon(209, 48, 7, 4);

	protected OptionsContainer optionsContainer;

	protected int selectedOption = -1;
	protected int maxExpandedWidth = -1;
	protected int maxDisplayedOptions = -1;
	protected boolean expanded = false;

	public UISelect(int width, HashMap<Integer, Option> options)
	{
		this.width = width;
		this.height = 12;
		this.optionsContainer = new OptionsContainer();
		setOptions(options);
	}

	public UISelect(int width)
	{
		this(width, null);
	}

	@Override
	public void setParent(UIContainer parent)
	{
		super.setParent(parent);
		this.optionsContainer.setParent(parent);
	}

	@Override
	public void setFocused(boolean focused)
	{
		super.setFocused(focused);
		if (!focused && expanded)
			expanded = false;
	}

	public UISelect maxExpandedWidth(int width)
	{
		maxExpandedWidth = width;
		optionsContainer.calcExpandedSize();
		return this;
	}

	public UISelect maxDisplayedOptions(int nb)
	{
		maxDisplayedOptions = nb;
		optionsContainer.calcExpandedSize();
		return this;
	}

	public UISelect setOptions(HashMap<Integer, Option> options)
	{
		optionsContainer.setOptions(options);
		return this;
	}

	public void setSelectedOption(int index)
	{
		selectedOption = index;
	}

	public Option select(int index)
	{
		Option oldValue = getOption(selectedOption);
		Option newValue = getOption(index);

		selectedOption = newValue != null ? newValue.index : -1;

		if (!fireEvent(new ComponentEvent.ValueChanged(this, oldValue, newValue)))
		{
			selectedOption = oldValue.index;
			return oldValue;
		}
		return newValue;
	}

	public Option getOption(int index)
	{
		return optionsContainer.getOption(index);
	}

	public Option getSelectedOption()
	{
		return optionsContainer.getOption(selectedOption);
	}

	@Override
	public boolean isInsideBounds(int x, int y)
	{
		return super.isInsideBounds(x, y) || optionsContainer.isInsideBounds(x, y);
	}

	@Override
	public UIComponent getComponentAt(int x, int y)
	{
		if (super.isInsideBounds(x, y))
			return this;
		else if (optionsContainer.isInsideBounds(x, y))
			return optionsContainer.getComponentAt(x, y);
		return null;
	}

	@Override
	public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		Shape shape = ShapePreset.GuiXResizable(width, 12, 3);
		renderer.drawShape(shape, isDisabled() ? iconsSelectDisabled : iconsSelect);
	}

	@Override
	public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		Shape shape = ShapePreset.GuiElement(7, 4);
		shape.translate(width - 9, 4, 0);
		RenderParameters rp = new RenderParameters();
		if (isHovered() || expanded)
			rp.colorMultiplier.set(0xBEC8FF);
		renderer.drawShape(shape, rp, arrowIcon);

		if (selectedOption != -1)
		{
			String text = getOption(selectedOption).label;
			if (text != null && text.length() != 0)
				renderer.drawText(renderer.clipString(text, width - 15), 2, 2, 0xFFFFFF, true);
		}
		// MalisisCore.message(this.optionsContainer.options.get(0).isHovered());

		renderer.next();

		ClipArea area = optionsContainer.getClipArea();
		renderer.startClipping(area);

		optionsContainer.draw(renderer, mouseX, mouseY, partialTick);

		renderer.endClipping(area);
	}

	@Subscribe
	public void onClick(MouseEvent.Release event)
	{
		if (event.getButton() != MouseButton.LEFT)
			return;

		if (super.isInsideBounds(event.getX(), event.getY()))
		{
			if (!expanded)
				optionsContainer.setFocused(true);
			expanded = !expanded;
		}
		else if (expanded)
		{
			int selected = ((optionsContainer.componentY(event.getY()) - 1) / 10);
			select(selected);
			expanded = false;
			setFocused(true);
		}

	}

	@Subscribe
	public void onScrollWheel(MouseEvent.ScrollWheel event)
	{
		if (isFocused() && isHovered())
		{
			int selected = selectedOption + event.getDelta() * -1;
			selected = Math.max(0, Math.min(optionsContainer.optionsLabel.size() - 1, selected));
			select(selected);
		}
	}

	@Subscribe
	public void onKeyTyped(KeyboardEvent event)
	{
		if (!isFocused())
			return;

		int keyCode = event.getKeyCode();
		switch (keyCode)
		{
			case Keyboard.KEY_UP:
				select(selectedOption - 1);
				break;
			case Keyboard.KEY_DOWN:
				select(selectedOption + 1);
				break;
			case Keyboard.KEY_HOME:
				select(0);
				break;
			case Keyboard.KEY_END:
				select(optionsContainer.optionsLabel.size() - 1);
				break;
		}
	}

	private class OptionsContainer extends UIContainer
	{
		HashMap<Integer, Option> options;
		ArrayList<UILabel> optionsLabel = new ArrayList<>();

		public OptionsContainer()
		{
			this.zIndex = 101;
		}

		public void setOptions(HashMap<Integer, Option> options)
		{
			this.options = options;
			for (UILabel label : this.optionsLabel)
				unregister(label);
			this.optionsLabel.clear();
			int i = 0;

			for (Entry<Integer, UISelect.Option> entry : options.entrySet())
			{
				UILabel label = new UILabel(entry.getValue().label).setPosition(2, 1 + 10 * i++).setZIndex(zIndex + 1).setDrawShadow(true)
						.register(UISelect.this);
				this.optionsLabel.add(label);
				this.add(label);
			}

			calcExpandedSize();
		}

		private void calcExpandedSize()
		{
			width = UISelect.this.width;
			height = 10 * (maxDisplayedOptions == -1 ? optionsLabel.size() : maxDisplayedOptions) + 1;
			if (maxDisplayedOptions != -1 && maxDisplayedOptions < optionsLabel.size())

				for (UILabel label : optionsLabel)
					width = Math.max(width, label.getWidth() + 4);

			if (maxExpandedWidth > 0)
				width = Math.min(maxExpandedWidth, width);

			for (UILabel label : optionsLabel)
				label.setSize(width - 2);
		}

		public Option getOption(int index)
		{
			if (index < 0 || index >= options.size())
				return null;
			return options.get(index);
		}

		@Override
		public void setFocused(boolean focused)
		{
			if (focused)
				UISelect.this.setFocused(focused);
		}

		@Override
		public boolean isVisible()
		{
			return expanded;
		}

		@Override
		public int screenX()
		{
			return UISelect.this.screenX();
		}

		@Override
		public int screenY()
		{
			return UISelect.this.screenY() + 12;
		}

		@Override
		public ClipArea getClipArea()
		{
			return new ClipArea(this, 0, false);
		}

		@Override
		public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
		{
			Shape shape = ShapePreset.GuiXYResizable(width, height, 3, 3);
			renderer.drawShape(shape, iconsExpanded);

			for (UILabel label : optionsLabel)
			{
				if (label.isHovered())
				{
					renderer.next();

					GL11.glDisable(GL11.GL_TEXTURE_2D);

					shape = ShapePreset.GuiXResizable(label.getWidth(), label.getHeight());
					shape.translate(componentX(label) - 1, componentY(label), 0);

					RenderParameters rp = new RenderParameters();
					rp.colorMultiplier.set(0x5E789F);
					rp.useTexture.set(false);
					renderer.drawShape(shape, rp);

					renderer.next();

					GL11.glEnable(GL11.GL_TEXTURE_2D);

				}
			}

		}

		@Override
		public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
		{
			int i = 0;
			for (UILabel label : optionsLabel)
			{
				int color = label.isHovered() ? (i == selectedOption ? 0xDED89F : 0xFFFFFF) : (i == selectedOption ? 0x9EA8DF : 0xFFFFFF);
				label.setColor(color);
				label.draw(renderer, mouseX, mouseY, partialTick);
				i++;
			}
		}

	}

	public static class Option<T>
	{
		private int index;
		private T key;
		private String label;

		public Option(int index, T key, String value)
		{
			this.index = index;
			this.key = key;
			this.label = value;
		}

		public int getIndex()
		{
			return index;
		}

		public T getKey()
		{
			return key;
		}

		public String getLabel()
		{
			return label;
		}

		public static <T> HashMap<Integer, Option> fromList(List<T> list)
		{
			HashMap<Integer, Option> options = new HashMap<>();
			int index = 0;
			for (T opt : list)
			{
				Option<T> option = new Option(index, opt, opt.toString());
				options.put(index, option);
				index++;
			}

			return options;
		}

		public static <T> HashMap<Integer, Option> fromList(HashMap<T, String> list)
		{
			HashMap<Integer, Option> options = new HashMap<>();
			int index = 0;

			for (Entry<T, String> entry : list.entrySet())
			{
				Option<T> option = new Option(index, entry.getKey(), entry.getValue());
				options.put(index, option);
				index++;
			}

			return options;
		}
	}

}
