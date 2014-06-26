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

package net.malisis.core.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.UIComponent;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.container.UIPanel;
import net.malisis.core.client.gui.component.container.UIWindow;
import net.malisis.core.client.gui.component.decoration.UIMultiLineLabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.event.ComponentEvent;
import net.malisis.core.configuration.setting.Setting;

import com.google.common.eventbus.Subscribe;

/**
 * @author Ordinastie
 * 
 */
public class ConfigurationGui extends MalisisGui
{
	private Settings settings;
	protected ArrayList<UIPanel> pannels = new ArrayList<>();
	protected HashMap<UIComponent, Setting> componentSettings = new HashMap<>();

	protected int windowWidth = 400;
	protected int windowHeight = 120;

	protected UIMultiLineLabel comment;
	protected UIButton btnCancel;
	protected UIButton btnSave;

	public ConfigurationGui(Settings settings)
	{
		this.settings = settings;

		Set<String> categories = settings.getCategories();

		if (categories.size() > 1)
		{
			//TODO: build tabs
		}

		UIWindow window = new UIWindow("config.title", windowWidth, windowHeight);

		for (String category : categories)
		{
			window.add(createSettingContainer(category));
		}

		comment = new UIMultiLineLabel(136, windowHeight - 41);
		comment.setColor(0xFFFFFF);
		comment.setDrawShadow(true);
		UIPanel panelComment = new UIPanel(140, windowHeight - 35).setPosition(0, 0, Anchor.RIGHT);
		panelComment.setColor(0xCCCCCC);
		panelComment.add(comment);

		btnCancel = new UIButton("gui.cancel").setPosition(-32, 0, Anchor.BOTTOM | Anchor.CENTER).register(this);
		btnSave = new UIButton("gui.done").setPosition(32, 0, Anchor.BOTTOM | Anchor.CENTER).register(this);

		window.add(panelComment);
		window.add(btnCancel);
		window.add(btnSave);

		addToScreen(window);
	}

	private UIContainer createSettingContainer(String category)
	{
		List<Setting> categorySettings = settings.getSettings(category);
		UIContainer container = (UIContainer) new UIContainer(windowWidth - 105, windowHeight - 35).setPosition(5, 12);

		int y = 0;
		for (Setting setting : categorySettings)
		{
			UIComponent component = setting.getComponent();
			component.setPosition(0, y);
			component.register(this);
			container.add(component);
			componentSettings.put(component, setting);

			y += component.getHeight() + 2;
		}

		return container;
	}

	@Subscribe
	public void onMouseOver(ComponentEvent.HoveredStateChanged event)
	{
		if (event.getState() == true)
		{
			Setting setting = componentSettings.get(event.getComponent());
			if (setting != null)
				comment.setText(setting.getComments());
		}
		else
			comment.setText("");
	}

	@Subscribe
	public void onButtonClick(UIButton.ClickedEvent event)
	{
		if (event.getComponent() == btnCancel)
			close();
		else
		{
			Set<String> categories = settings.getCategories();
			for (String category : categories)
			{
				List<Setting> categorySettings = settings.getSettings(category);
				for (Setting setting : categorySettings)
				{
					setting.set(setting.getValueFromComponent());
				}
			}

			settings.save();
			close();
		}
	}
}
