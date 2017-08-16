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

import com.l2jhellas.gameserver.model.L2ClanMember;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

public class PledgeShowMemberListAdd extends L2GameServerPacket
{
	private static final String _S__55_PLEDGESHOWMEMBERLISTADD = "[S] 55 PledgeShowMemberListAdd";
	private String _name;
	private int _lvl;
	private int _classId;
	private int _isOnline;
	private int _pledgeType;

	public PledgeShowMemberListAdd(L2PcInstance player)
	{
		_name = player.getName();
		_lvl = player.getLevel();
		_classId = player.getClassId().getId();
		_isOnline = (player.isOnline() == 1 ? player.getObjectId() : 0);
		_pledgeType = player.getPledgeType();
	}

	public PledgeShowMemberListAdd(L2ClanMember cm)
	{
		try
		{
			_name = cm.getName();
			_lvl = cm.getLevel();
			_classId = cm.getClassId();
			_isOnline = (cm.isOnline() ? cm.getObjectId() : 0);
			_pledgeType = cm.getPledgeType();
		}
		catch (Exception e)
		{
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x55);
		writeS(_name);
		writeD(_lvl);
		writeD(_classId);
		writeD(0);
		writeD(1);
		writeD(_isOnline); // 1=online 0=offline
		writeD(_pledgeType);
	}

	@Override
	public String getType()
	{
		return _S__55_PLEDGESHOWMEMBERLISTADD;
	}
}