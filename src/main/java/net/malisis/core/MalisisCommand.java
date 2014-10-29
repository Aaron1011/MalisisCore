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

package net.malisis.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class MalisisCommand extends CommandBase
{
	Set<String> parameters = new HashSet<>();

	public MalisisCommand()
	{
		parameters.add("config");
		parameters.add("version");
	}

	@Override
	public String getCommandName()
	{
		return "malisis";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "malisiscore.commands.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		if (params.length == 0)
			throw new WrongUsageException("malisiscore.commands.usage", new Object[0]);

		if (!parameters.contains(params[0]))
			throw new WrongUsageException("malisiscore.commands.usage", new Object[0]);

		switch (params[0])
		{
			case "config":
				configCommand(sender, params);
				break;

			case "version":
				IMalisisMod mod = null;
				if (params.length == 1)
					mod = MalisisCore.instance;
				else
				{
					mod = MalisisCore.getMod(params[1]);
					if (mod == null)
						MalisisCore.message("malisiscore.commands.modnotfound", params[1]);
				}
				if (mod != null)
					MalisisCore.message("malisiscore.commands.modversion", mod.getName(), mod.getVersion());
				break;
			case "gui":
				// Minecraft.getMinecraft().displayGuiScreen(new UIWindow(100, 100).createScreenProxy());
				break;

			default:
				MalisisCore.message("Not yet implemented");
				break;
		}

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender)
	{
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] params)
	{
		if (params.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(params, parameters);
		else if (params.length == 2)
			return getListOfStringsFromIterableMatchingLastWord(params, MalisisCore.listModId());
		else
			return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return false;
	}

	public void configCommand(ICommandSender sender, String[] params)
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			MalisisCore.log.warn("Can't open configuration GUI on a dedicated server.");
			return;
		}

		IMalisisMod mod = null;
		if (params.length == 1)
			mod = MalisisCore.instance;
		else
		{
			mod = MalisisCore.getMod(params[1]);
			if (mod == null)
				MalisisCore.message("malisiscore.commands.modnotfound", params[1]);
		}
		if (mod != null)
		{
			if (!MalisisCore.openConfigurationGui(mod, true))
				MalisisCore.message("malisiscore.commands.noconfiguration", mod.getName());
		}
	}

}
