/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jhellas.gameserver.network;

import java.nio.ByteBuffer;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.LoginServerThread;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.datatables.xml.AdminData;
import com.l2jhellas.gameserver.network.L2GameClient.GameClientState;
import com.l2jhellas.mmocore.network.IClientFactory;
import com.l2jhellas.mmocore.network.IMMOExecutor;
import com.l2jhellas.mmocore.network.IPacketHandler;
import com.l2jhellas.mmocore.network.MMOConnection;
import com.l2jhellas.mmocore.network.ReceivablePacket;

/**
 * Stateful Packet Handler
 * The Stateful approach prevents the server from handling inconsistent packets, examples:
 * Clients sends a MoveToLocation packet without having a character attached. (Potential errors handling the packet).
 * Clients sends a RequestAuthLogin being already authed. (Potential exploit).
 * Note: If for a given exception a packet needs to be handled on more then one state, then it should be added to all these states.
 * 
 * @author KenM
 */
public final class L2GamePacketHandler implements IPacketHandler<L2GameClient>, IClientFactory<L2GameClient>, IMMOExecutor<L2GameClient>
{
	private static final Logger _log = Logger.getLogger(L2GamePacketHandler.class.getName());
	
	@Override
	public ReceivablePacket<L2GameClient> handlePacket(ByteBuffer buf, L2GameClient client)
	{
		int opcode = buf.get() & 0xFF;
		
		ReceivablePacket<L2GameClient> packet = null;
		
		if(opcode  == 0xd0)
		{
			int id2 = -1;
			
			if (buf.remaining() >= 2)
				id2 = buf.getShort() & 0xffff;

			if(DoubleOpcodePackets.PACKET_ARRAY[id2].isInRightState(client.getState()))
				packet = DoubleOpcodePackets.PACKET_ARRAY[id2].getPacket();
			else
				packet = Packets.DummyPacket.getPacket();
				
			
		}
		else
		{	
			if(opcode== 157 || opcode == 202)
				packet = Packets.GameGuardReply.getPacket();
			else
			{
				if(Packets.PACKET_ARRAY[opcode].isInRightState(client.getState()))
					packet = Packets.PACKET_ARRAY[opcode].getPacket();
				else
					packet = Packets.DummyPacket.getPacket();
			}
		}
		
        return packet;	
	}


	@SuppressWarnings("unused")
	private void punish(L2GameClient client)
	{
		switch (Config.UNKNOWN_PACKETS_PUNISHMENT)
		{
			case (1):
				if (client.getActiveChar() != null)
				{
					AdminData.getInstance().broadcastMessageToGMs("Player " + client.getActiveChar().toString() + " flooding unknown packets.");
				}
			break;
			case (2):
				_log.warning(L2GamePacketHandler.class.getName() + ": PacketProtection: " + client.toString() + " got kicked due flooding of unknown packets.");
				if (client.getActiveChar() != null)
				{
					AdminData.getInstance().broadcastMessageToGMs("Player " + client.getActiveChar().toString() + " flooding unknown packets and got kicked.");
					client.getActiveChar().sendMessage("You are will be kicked for unknown packet flooding, GM informed.");
					client.getActiveChar().closeNetConnection(false);
				}
			break;
			case (3):
				_log.warning(L2GamePacketHandler.class.getName() + ": PacketProtection: " + client.toString() + " got banned due flooding of unknown packets.");
				LoginServerThread.getInstance().sendAccessLevel(client.getAccountName(), -99);
				if (client.getActiveChar() != null)
				{
					AdminData.getInstance().broadcastMessageToGMs("Player " + client.getActiveChar().toString() + " flooding unknown packets and got banned.");
					client.getActiveChar().sendMessage("You are banned for unknown packet flooding, GM informed.");
					client.getActiveChar().closeNetConnection(false);
				}
			break;
		}
	}

	// impl
	@Override
	public L2GameClient create(MMOConnection<L2GameClient> con)
	{
		return new L2GameClient(con);
	}

	@Override
	public void execute(ReceivablePacket<L2GameClient> rp)
	{
		try
		{
			if (rp.getClient().getState() == GameClientState.IN_GAME)
			{
				ThreadPoolManager.getInstance().executePacket(rp);
			}
			else
			{
				ThreadPoolManager.getInstance().executeIOPacket(rp);
			}
		}
		catch (RejectedExecutionException e)
		{
			// if the server is shutdown we ignore
			if (!ThreadPoolManager.getInstance().isShutdown())
			{
				_log.severe("Failed executing: " + rp.getClass().getSimpleName() + " for Client: " + rp.getClient().toString());
			}
		}
	}
}