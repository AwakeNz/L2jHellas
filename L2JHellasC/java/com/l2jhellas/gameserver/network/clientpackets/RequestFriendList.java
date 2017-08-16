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

import com.l2jhellas.gameserver.datatables.sql.CharNameTable;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;

public final class RequestFriendList extends L2GameClientPacket
{
	private static final String _C__60_REQUESTFRIENDLIST = "[C] 60 RequestFriendList";

	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
			return;
		
		activeChar.sendPacket(SystemMessageId.FRIEND_LIST_HEADER);
		
		for (int id : activeChar.getFriendList())
		{
			final String friendName = CharNameTable.getInstance().getNameById(id);
			
			if (friendName == null)
				continue;
			
			final L2PcInstance friend = L2World.getInstance().getPlayer(id);
			
			activeChar.sendPacket(SystemMessage.getSystemMessage((friend == null || friend.isOnline()==0) ? SystemMessageId.S1_OFFLINE : SystemMessageId.S1_ONLINE).addString(friendName));
		}
		
		activeChar.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
	}

	@Override
	public String getType()
	{
		return _C__60_REQUESTFRIENDLIST;
	}
}