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

package net.malisis.core.packet;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.malisis.core.inventory.MalisisInventoryContainer;
import net.malisis.core.inventory.MalisisInventoryContainer.ActionType;
import net.malisis.core.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InventoryActionMessage implements IMessageHandler<InventoryActionMessage.Packet, IMessage>
{
	/**
	 * Handles the packet received from the client. Pass the action to the container, and send the changes back to the client.
	 */
	@Override
	public IMessage onMessage(Packet message, MessageContext ctx)
	{
		if (ctx.side != Side.SERVER)
			return null;

		EntityPlayerMP player = EntityUtils.findPlayerFromUUID(message.uuid);
		Container c = player.openContainer;
		if (message.windowId != c.windowId || !(c instanceof MalisisInventoryContainer))
			return null;

		MalisisInventoryContainer container = (MalisisInventoryContainer) c;
		container.handleAction(message.action, message.slotNumber, message.code, message.playerInventory);
		container.detectAndSendChanges();

		return null;
	}

	/**
	 * Sends GUI action to the server MalisisInventoryContainer.
	 * 
	 * @param action
	 * @param slotNumber
	 * @param code
	 * @param playerInventory
	 */
	@SideOnly(Side.CLIENT)
	public static void sendAction(ActionType action, int slotNumber, int code, boolean playerInventory)
	{
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		int windowId = player.openContainer.windowId;
		Packet packet = new Packet(action, slotNumber, code, player.getUniqueID(), playerInventory, windowId);
		MalisisPacket.network.sendToServer(packet);
	}

	public static class Packet implements IMessage
	{
		private ActionType action;
		private int slotNumber;
		private int code;
		private UUID uuid;
		private boolean playerInventory;
		private int windowId;

		public Packet()
		{}

		public Packet(ActionType action, int slotNumber, int code, UUID uuid, boolean playerInventory, int windowId)
		{
			this.action = action;
			this.slotNumber = slotNumber;
			this.code = code;
			this.uuid = uuid;
			this.playerInventory = playerInventory;
			this.windowId = windowId;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			action = ActionType.values()[buf.readByte()];
			slotNumber = buf.readInt();
			code = buf.readInt();
			uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
			playerInventory = buf.readBoolean();
			windowId = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeByte(action.ordinal());
			buf.writeInt(slotNumber);
			buf.writeInt(code);
			ByteBufUtils.writeUTF8String(buf, uuid.toString());
			buf.writeBoolean(playerInventory);
			buf.writeInt(windowId);
		}
	}

}
