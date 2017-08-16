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

import com.l2jhellas.gameserver.model.L2Clan;
import com.l2jhellas.gameserver.model.L2ClanMember;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

/**
 * format SdSS dddddddd d (Sddddd)
 */
public class GMViewPledgeInfo extends L2GameServerPacket
{
	private static final String _S__A9_GMVIEWPLEDGEINFO = "[S] 90 GMViewPledgeInfo";
	private final L2Clan _clan;
	private final L2PcInstance _activeChar;

	public GMViewPledgeInfo(L2Clan clan, L2PcInstance activeChar)
	{
		_clan = clan;
		_activeChar = activeChar;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x90);
		writeS(_activeChar.getName());
		writeD(_clan.getClanId());
		writeD(0x00);
		writeS(_clan.getName());
		writeS(_clan.getLeaderName());
		writeD(_clan.getCrestId()); // -> no, it's no longer used (nuocnam) fix by game
		writeD(_clan.getLevel());
		writeD(_clan.hasCastle());
		writeD(_clan.hasHideout());
		writeD(_clan.getRank());
		writeD(_clan.getReputationScore());
		writeD(0);
		writeD(0);

		writeD(_clan.getAllyId()); // c2
		writeS(_clan.getAllyName()); // c2
		writeD(_clan.getAllyCrestId()); // c2
		writeD(_clan.isAtWar() ? 1 : 0); // c3

		L2ClanMember[] members = _clan.getMembers();
		writeD(members.length);

		for (int i = 0; i < members.length; i++)
		{
			writeS(members[i].getName());
			writeD(members[i].getLevel());
			writeD(members[i].getClassId());
			writeD(0);
			writeD(1);
			writeD(members[i].isOnline() ? members[i].getObjectId() : 0);
			writeD(0);
		}
	}

	@Override
	public String getType()
	{
		return _S__A9_GMVIEWPLEDGEINFO;
	}
}