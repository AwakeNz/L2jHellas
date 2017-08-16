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
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.ExManagePartyRoomMember;
import com.l2jhellas.gameserver.network.serverpackets.JoinParty;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;

/**
 * sample<BR>
 * 2a<BR>
 * 01 00 00 00<BR>
 * format cdd
 */
public final class RequestAnswerJoinParty extends L2GameClientPacket
{
	private static final String _C__2A_REQUESTANSWERPARTY = "[C] 2A RequestAnswerJoinParty";

	private int _response;

	@Override
	protected void readImpl()
	{
		_response = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player != null)
		{
			L2PcInstance requestor = player.getActiveRequester();
			if (requestor == null)
				return;

			JoinParty join = new JoinParty(_response);
			requestor.sendPacket(join);

			if (_response == 1)
			{
				if (requestor.isInParty())
				{
					if (requestor.getParty().getMemberCount() >= 9)
					{
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PARTY_FULL);
						player.sendPacket(sm);
						requestor.sendPacket(sm);
						return;
					}
				}
				player.joinParty(requestor.getParty());
				
				if (requestor.isInPartyMatchRoom() && player.isInPartyMatchRoom())
				{
					final PartyMatchRoomList list = PartyMatchRoomList.getInstance();
					if (list != null && (list.getPlayerRoomId(requestor) == list.getPlayerRoomId(player)))
					{
						final PartyMatchRoom room = list.getPlayerRoom(requestor);
						if (room != null)
						{
							final ExManagePartyRoomMember packet = new ExManagePartyRoomMember(player, room, 1);
							for (L2PcInstance member : room.getPartyMembers())
							{
								if (member != null)
									member.sendPacket(packet);
							}
						}
					}
				}
				else if (requestor.isInPartyMatchRoom() && !player.isInPartyMatchRoom())
				{
					final PartyMatchRoomList list = PartyMatchRoomList.getInstance();
					if (list != null)
					{
						final PartyMatchRoom room = list.getPlayerRoom(requestor);
						if (room != null)
						{
							room.addMember(player);
							ExManagePartyRoomMember packet = new ExManagePartyRoomMember(player, room, 1);
							for (L2PcInstance member : room.getPartyMembers())
							{
								if (member != null)
									member.sendPacket(packet);
							}
							player.setPartyRoom(room.getId());
							player.broadcastUserInfo();
						}
					}
				}
			}
			else
			{
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.PLAYER_DECLINED);
				requestor.sendPacket(msg);
				msg = null;

				// activate garbage collection if there are no other members in party (happens when we were creating new one)
				if (requestor.getParty() != null && requestor.getParty().getMemberCount() == 1)
					requestor.setParty(null);
			}
			if (requestor.getParty() != null)
				requestor.getParty().setPendingInvitation(false); // if party is null, there is no need of decreasing

			player.setActiveRequester(null);
			requestor.onTransactionResponse();
			requestor.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	@Override
	public String getType()
	{
		return _C__2A_REQUESTANSWERPARTY;
	}
}