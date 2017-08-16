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
package com.l2jhellas.gameserver.network.clientpackets;

import com.l2jhellas.gameserver.model.PartyMatchRoom;
import com.l2jhellas.gameserver.model.PartyMatchRoomList;
import com.l2jhellas.gameserver.model.PartyMatchWaitingList;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.ExPartyRoomMember;
import com.l2jhellas.gameserver.network.serverpackets.PartyMatchDetail;
import com.l2jhellas.gameserver.network.serverpackets.PartyMatchList;

public final class RequestPartyMatchConfig extends L2GameClientPacket
{
	private static final String _C__6F_REQUESTPARTYMATCHCONFIG = "[C] 6F RequestPartyMatchConfig";

	private int _auto, _loc, _lvl;
	
	@Override
	protected void readImpl()
	{
		_auto = readD();
		_loc = readD();
		_lvl = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (!activeChar.isInPartyMatchRoom() && activeChar.getParty() != null && activeChar.getParty().getLeader() != activeChar)
		{
			activeChar.sendPacket(SystemMessageId.CANT_VIEW_PARTY_ROOMS);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isInPartyMatchRoom())
		{
			// If Player is in Room show him room, not list
			PartyMatchRoomList list = PartyMatchRoomList.getInstance();
			if (list == null)
				return;
			
			PartyMatchRoom room = list.getPlayerRoom(activeChar);
			if (room == null)
				return;
			
			activeChar.sendPacket(new PartyMatchDetail(room));
			activeChar.sendPacket(new ExPartyRoomMember(room, 2));
			
			activeChar.setPartyRoom(room.getId());
			activeChar.broadcastUserInfo();
		}
		else
		{
			// Add to waiting list
			PartyMatchWaitingList.getInstance().addPlayer(activeChar);
			
			// Send Room list
			activeChar.sendPacket(new PartyMatchList(activeChar, _auto, _loc, _lvl));
		}
	}

	@Override
	public String getType()
	{
		return _C__6F_REQUESTPARTYMATCHCONFIG;
	}
}