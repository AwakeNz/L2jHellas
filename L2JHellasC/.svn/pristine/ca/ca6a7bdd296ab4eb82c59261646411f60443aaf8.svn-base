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

import com.l2jhellas.gameserver.datatables.sql.ClanTable;
import com.l2jhellas.gameserver.model.L2Clan;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;

public class AllyInfo extends L2GameServerPacket
{
	private static final String _S__7A_FRIENDLIST = "[S] 7a AllyInfo";
	private static L2PcInstance _cha;

	public AllyInfo(L2PcInstance cha)
	{
		_cha = cha;
	}

	@Override
	protected final void writeImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		if (activeChar.getAllyId() == 0)
		{
			_cha.sendPacket(SystemMessageId.NO_CURRENT_ALLIANCES);
			return;
		}

		// ======<AllyInfo>======
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_INFO_HEAD);
		_cha.sendPacket(sm);
		// ======<Ally Name>======
		sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_NAME_S1);
		sm.addString(_cha.getClan().getAllyName());
		_cha.sendPacket(sm);
		int online = 0;
		int count = 0;
		int clancount = 0;
		for (L2Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == _cha.getAllyId())
			{
				clancount++;
				online += clan.getOnlineMembers("").length;
				count += clan.getMembers().length;
			}
		}
		// Connection
		sm = SystemMessage.getSystemMessage(SystemMessageId.CONNECTION_S1_TOTAL_S2);
		sm.addString("" + online);
		sm.addString("" + count);
		_cha.sendPacket(sm);
		L2Clan leaderclan = ClanTable.getInstance().getClan(_cha.getAllyId());
		sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_LEADER_S2_OF_S1);
		sm.addString(leaderclan.getName());
		sm.addString(leaderclan.getLeaderName());
		_cha.sendPacket(sm);
		// clan count
		sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_CLAN_TOTAL_S1);
		sm.addString("" + clancount);
		_cha.sendPacket(sm);
		// clan information
		sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_INFO_HEAD);
		_cha.sendPacket(sm);
		for (L2Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == _cha.getAllyId())
			{
				// clan name
				sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_INFO_NAME_S1);
				sm.addString(clan.getName());
				_cha.sendPacket(sm);
				// clan leader name
				sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_INFO_LEADER_S1);
				sm.addString(clan.getLeaderName());
				_cha.sendPacket(sm);
				// clan level
				sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_INFO_LEVEL_S1);
				sm.addNumber(clan.getLevel());
				_cha.sendPacket(sm);
				// ---------
				sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_INFO_SEPARATOR);
				_cha.sendPacket(sm);
			}
		}
		sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_INFO_FOOT);
		_cha.sendPacket(sm);
	}

	@Override
	public String getType()
	{
		return _S__7A_FRIENDLIST;
	}
}