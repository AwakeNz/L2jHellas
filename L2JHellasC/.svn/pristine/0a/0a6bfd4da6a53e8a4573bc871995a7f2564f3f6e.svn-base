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


/**
 * @formatter:off
 *                sample<BR>
 * <BR>
 *                a3<BR>
 *                05000000<BR>
 *                03000000 03000000 06000000 3c000000 00000000 power strike<BR>
 *                10000000 02000000 06000000 3c000000 00000000 mortal blow<BR>
 *                38000000 04000000 06000000 36010000 00000000 power shot<BR>
 *                4d000000 01000000 01000000 98030000 01000000 ATTACK aura 920sp<BR>
 *                8e000000 03000000 03000000 cc010000 00000000 Armor Mastery<BR>
 * <BR>
 *                format d (ddddd)<BR>
 *                skillid, level, maxlevel?,<BR>
 * <BR>
 *                C4 format changes:<BR>
 *                0000: [8a] [00 00 00 00] [35 00 00 00] 92 00 00 00 01 00 00 .....5..........<BR>
 *                ^^^^^^^^^^^^^<BR>
 *                0010: 00 2d 00 00 00 04 01 00 00 00 00 00 00 a4 00 00 .-..............<BR>
 *                0020: 00 01 00 00 00 03 00 00 00 e4 0c 00 00 00 00 00 ................<BR>
 *                0030: 00 d4 00 00 00 01 00 00 00 06 00 00 00 08 52 00 ..............R.
 * @formatter:on
 */
public class AquireSkillList extends L2GameServerPacket
{
	public enum skillType
	{
		Usual, Fishing, Clan
	}

	private static final String _S__A3_AQUIRESKILLLIST = "[S] 8a AquireSkillList";

	private final List<Skill> _skills;
	private final skillType _fishingSkills;

	private class Skill
	{
		public int id;
		public int nextLevel;
		public int maxLevel;
		public int spCost;
		public int requirements;

		public Skill(int pId, int pNextLevel, int pMaxLevel, int pSpCost, int pRequirements)
		{
			id = pId;
			nextLevel = pNextLevel;
			maxLevel = pMaxLevel;
			spCost = pSpCost;
			requirements = pRequirements;
		}
	}

	public AquireSkillList(skillType type)
	{
		_skills = new ArrayList<Skill>();
		_fishingSkills = type;
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int spCost, int requirements)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, spCost, requirements));
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x8a);
		writeD(_fishingSkills.ordinal()); // c4 : C5 : 0: usuall 1: fishing 2: clans
		writeD(_skills.size());

		for (Skill temp : _skills)
		{
			writeD(temp.id);
			writeD(temp.nextLevel);
			writeD(temp.maxLevel);
			writeD(temp.spCost);
			writeD(temp.requirements);
		}
	}

	@Override
	public String getType()
	{
		return _S__A3_AQUIRESKILLLIST;
	}
}