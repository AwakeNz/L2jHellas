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
import com.l2jhellas.gameserver.model.L2Skill;

/**
 * Format: (ch) d [dd]
 * 
 * @author -Wooden-
 */
public class PledgeSkillList extends L2GameServerPacket
{
	private static final String _S__FE_39_PLEDGESKILLLIST = "[S] FE:39 PledgeSkillList";
	private final L2Clan _clan;

	public PledgeSkillList(L2Clan clan)
	{
		_clan = clan;
	}

	@Override
	protected void writeImpl()
	{
		L2Skill[] skills = _clan.getAllSkills();

		writeC(0xfe);
		writeH(0x39);
		writeD(skills.length);
		for (L2Skill sk : skills)
		{
			writeD(sk.getId());
			writeD(sk.getLevel());
		}
	}

	@Override
	public String getType()
	{
		return _S__FE_39_PLEDGESKILLLIST;
	}
}