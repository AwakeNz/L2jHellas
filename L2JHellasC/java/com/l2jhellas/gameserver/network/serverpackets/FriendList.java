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
package com.l2jhellas.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jhellas.gameserver.datatables.sql.CharNameTable;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

/**
 * Support for "Chat with Friends" dialog.<BR>
 * <BR>
 * Format: ch (hdSdh)<BR>
 * h: Total Friend Count<BR>
 * <BR>
 * h: Unknown<BR>
 * d: Player Object ID<BR>
 * S: Friend Name<BR>
 * d: Online/Offline<BR>
 * h: Unknown<BR>
 * <BR>
 * 
 * @author Tempy
 */
public class FriendList extends L2GameServerPacket
{
	private static final String _S__FA_FRIENDLIST = "[S] FA FriendList";
	private final List<FriendInfo> _info;
	
	private static class FriendInfo
	{
		int _objId;
		String _name;
		boolean _online;
		
		public FriendInfo(int objId, String name, boolean online)
		{
			_objId = objId;
			_name = name;
			_online = online;
		}
	}
	
	public FriendList(L2PcInstance player)
	{
		_info = new ArrayList<>(player.getFriendList().size());
		
		for (int objId : player.getFriendList())
		{
			final String name = CharNameTable.getInstance().getNameById(objId);
			final L2PcInstance player1 = L2World.getInstance().getPlayer(objId);
			
			_info.add(new FriendInfo(objId, name, (player1 != null && player1.isOnline()==1)));
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xfa);
		writeD(_info.size());
		for (FriendInfo info : _info)
		{
			writeD(info._objId);
			writeS(info._name);
			writeD(info._online ? 0x01 : 0x00);
			writeD(info._online ? info._objId : 0x00);
		}
	}

	@Override
	public String getType()
	{
		return _S__FA_FRIENDLIST;
	}
}