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

package net.malisis.core.util;

import net.malisis.core.client.gui.MalisisGui;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Ordinastie
 *
 */
public class TileEntityUtils
{
	@SideOnly(Side.CLIENT)
	private static TileEntity currentTileEntity;
	@SideOnly(Side.CLIENT)
	private static MalisisGui currenGui;

	public static <T> T getTileEntity(Class<T> type, IBlockAccess world, int x, int y, int z)
	{
		if (world == null)
			return null;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
			return null;

		try
		{
			return type.cast(te);
		}
		catch (ClassCastException e)
		{
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void linkTileEntityToGui(TileEntity te, MalisisGui gui)
	{
		currentTileEntity = te;
		currenGui = gui;
		currenGui.updateGui();
	}

	@SideOnly(Side.CLIENT)
	public static void updateGui(TileEntity te)
	{
		if (te != currentTileEntity)
			return;
		currenGui.updateGui();
	}

}
