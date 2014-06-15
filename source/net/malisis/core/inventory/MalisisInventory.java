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

package net.malisis.core.inventory;

import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.packet.OpenIventoryMessage;
import net.malisis.core.util.ItemUtils;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MalisisInventory implements IInventory
{
	/**
	 * Object containing this <code>MalisisInventory</code>.
	 */
	protected IInventoryProvider inventoryProvider;
	/**
	 * Slots for this <code>MalisisInventory</code>.
	 */
	protected MalisisSlot[] slots;
	/**
	 * Size of this <code>MalisisInventory</code>.
	 */
	protected int size;
	/**
	 * Maximum stack size for the slots
	 */
	protected int slotMaxStackSize = 64;

	protected EventBus bus = new EventBus();

	public MalisisInventory(IInventoryProvider provider, int size)
	{
		this.inventoryProvider = provider;
		this.size = size;
		MalisisSlot[] slots = new MalisisSlot[size];
		for (int i = 0; i < size; i++)
			slots[i] = new MalisisSlot(this, i);

		setSlots(slots);
	}

	public void setSlots(MalisisSlot[] slots)
	{
		this.size = slots.length;
		this.slots = slots;
	}

	public void overrideSlot(MalisisSlot slot, int slotNumber)
	{
		if (slotNumber < 0 || slotNumber >= getSizeInventory())
			return;

		slots[slotNumber] = slot;
	}

	public void register(Object object)
	{
		bus.register(object);
	}

	// #region getters/setters
	/**
	 * Gets the slot at position slotNumber.
	 * 
	 * @param slotNumber
	 * @return
	 */
	public MalisisSlot getSlot(int slotNumber)
	{
		if (slotNumber < 0 || slotNumber >= getSizeInventory())
			return null;

		return slots[slotNumber];
	}

	/**
	 * @return all slots from this <code>MalisisInventory</code>.
	 */
	public MalisisSlot[] getSlots()
	{
		return slots;
	}

	/**
	 * Gets the itemStack from the slot at position slotNumber.
	 * 
	 * @param slotNumber
	 * @return
	 */
	public ItemStack getItemStack(int slotNumber)
	{
		MalisisSlot slot = getSlot(slotNumber);
		return slot != null ? slot.getItemStack() : null;
	}

	/**
	 * Sets the itemStack for the slot at position slotNumber.
	 * 
	 * @param slotNumber
	 * @param itemStack
	 */
	public void setItemStack(int slotNumber, ItemStack itemStack)
	{
		MalisisSlot slot = getSlot(slotNumber);
		if (slot == null)
			return;

		if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit())
			itemStack.stackSize = this.getInventoryStackLimit();
		slot.setItemStack(itemStack);
		slot.onSlotChanged();
	}

	/**
	 * Checks whether itemStack can be contained by slot.
	 * 
	 * @param slot
	 * @param itemStack
	 * @return
	 */
	public boolean itemValidForSlot(MalisisSlot slot, ItemStack itemStack)
	{
		return true;
	}

	/**
	 * Checks whether itemStack can be contained by the slot at position slotNumber.
	 */
	@Override
	public boolean isItemValidForSlot(int slotNumber, ItemStack itemStack)
	{
		MalisisSlot slot = getSlot(slotNumber);
		if (slot == null)
			return false;
		return slot.isItemValid(itemStack);
	}

	/**
	 * @return size of this <code>MalisisInventory</code>.
	 */
	@Override
	public int getSizeInventory()
	{
		return this.size;
	}

	/**
	 * @return stack size limit for the slots
	 */
	@Override
	public int getInventoryStackLimit()
	{
		return slotMaxStackSize;
	}

	public void setInventoryStackLimit(int limit)
	{
		slotMaxStackSize = limit;
	}

	// #end getters/setters

	/**
	 * Called when itemStack change in slot
	 * 
	 * @param malisisSlot
	 */
	public void onSlotChanged(MalisisSlot slot)
	{
		bus.post(new InventoryEvent.SlotChanged(this, slot));
	}

	/**
	 * Called when this <code>MalisisInventory</code> is opened.
	 */
	@Override
	public void openInventory()
	{}

	/**
	 * Transfer itemStack inside this <code>MalisisInventory</code>.
	 * 
	 * @param itemStack that could not fit inside this <code>MalisisInventory</code>
	 * @return
	 */
	public ItemStack transferInto(ItemStack itemStack)
	{
		return transferInto(itemStack, false);
	}

	/**
	 * Transfer itemStack inside this <code>MalisisInventory</code>.
	 * 
	 * @param itemStack that could not fit inside this <code>MalisisInventory</code>
	 * @param reversed start filling slots from the last slot
	 * @return
	 */
	public ItemStack transferInto(ItemStack itemStack, boolean reversed)
	{
		int start = reversed ? size - 1 : 0;
		int end = reversed ? 0 : size - 1;

		itemStack = transferInto(itemStack, false, start, end);
		if (itemStack != null)
			itemStack = transferInto(itemStack, true, start, end);

		return itemStack;
	}

	/**
	 * Transfer itemStack inside this <code>MalisisInventory</code> into slots at position from start to end. If start > end, the slots will
	 * be filled backwards.
	 * 
	 * @param itemStack
	 * @param emptySlot
	 * @param start
	 * @param end
	 * @return
	 */
	protected ItemStack transferInto(ItemStack itemStack, boolean emptySlot, int start, int end)
	{
		MalisisSlot slot;
		int current = start, step = 1;
		if (start > end)
		{
			step = -1;
			start = end;
			end = current;
		}

		while (itemStack != null && current >= start && current <= end)
		{
			slot = getSlot(current);
			if (slot.isItemValid(itemStack) && (emptySlot || slot.getItemStack() != null))
			{
				ItemUtils.ItemStacksMerger ism = new ItemUtils.ItemStacksMerger(itemStack, slot.getItemStack());
				if (ism.merge(ItemUtils.FULL_STACK, slot.getSlotStackLimit()))
				{
					itemStack = ism.merge;
					slot.setItemStack(ism.into);
					slot.onSlotChanged();
				}
			}
			current += step;
		}

		return itemStack;
	}

	/**
	 * Read this <code>MalisisInventory</code> data from tagCompound
	 * 
	 * @param tagCompound
	 */
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		NBTTagList nbttaglist = tagCompound.getTagList("Items", NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound stackTag = nbttaglist.getCompoundTagAt(i);
			int slotNumber = stackTag.getByte("Slot") & 255;
			MalisisSlot slot = getSlot(slotNumber);
			if (slot != null)
				slot.setItemStack(ItemStack.loadItemStackFromNBT(stackTag));
		}
	}

	/**
	 * Writes this <code>MalisisInventory</code> data inside tagCompound
	 * 
	 * @param tagCompound
	 */
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < slots.length; i++)
		{
			ItemStack stack = getItemStack(i);
			if (stack != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Items", itemList);
	}

	/**
	 * Open this <code>MalisisInventory</code>. Called server-side only
	 * 
	 * @param player
	 * @return
	 * 
	 */
	public MalisisInventoryContainer open(EntityPlayerMP player)
	{
		if (inventoryProvider == null)
			return null;

		MalisisInventoryContainer c = new MalisisInventoryContainer(this, player, 0);
		OpenIventoryMessage.send(inventoryProvider, player, c.windowId);
		c.sendInventoryContent();

		openInventory();
		bus.post(new InventoryEvent.Open(this));

		return c;
	}

	/**
	 * Open this <code>MalisisInventory</code>. Called client-side only.
	 * 
	 * @param player
	 * @param windowId
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public MalisisInventoryContainer open(EntityClientPlayerMP player, int windowId)
	{
		if (inventoryProvider == null)
			return null;

		MalisisInventoryContainer c = new MalisisInventoryContainer(this, player, windowId);
		if (FMLCommonHandler.instance().getSide().isClient())
		{
			MalisisGui gui = inventoryProvider.getGui(c, player);
			if (gui != null)
				gui.display();
		}

		openInventory();
		bus.post(new InventoryEvent.Open(this));

		return c;
	}

	// #region Unused
	/**
	 * Unused
	 */
	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	/**
	 * Unused
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return true;
	}

	/**
	 * Unused
	 */
	@Override
	public void closeInventory()
	{}

	/**
	 * Unused
	 */
	@Override
	public void markDirty()
	{}

	/**
	 * Unused : always returns null
	 */
	@Override
	public ItemStack decrStackSize(int slot, int count)
	{
		return null;
	}

	/**
	 * Unused : always returns null
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return null;
	}

	@Override
	public String getInventoryName()
	{
		return null;
	}

	/**
	 * Use MalisisInventory.getItemStack(int slotNumber);
	 */
	@Override
	@Deprecated
	public ItemStack getStackInSlot(int slotNumber)
	{
		return getItemStack(slotNumber);
	}

	/**
	 * Use MalisisInventory.setItemStack(int slotNumber, ItemStack itemStack)
	 */
	@Override
	@Deprecated
	public void setInventorySlotContents(int slotNumber, ItemStack itemStack)
	{
		setItemStack(slotNumber, itemStack);
	}
	// #end Unused
}