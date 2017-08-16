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
package com.l2jhellas.gameserver.skills;

import java.util.HashMap;
import java.util.Map;

public class NpcBufferSkills
{
	private int _npcId = 0;
	private final Map<Integer, Integer> _skillLevels = new HashMap<Integer, Integer>();
	private final Map<Integer, Integer> _skillFeeIds = new HashMap<Integer, Integer>();
	private final Map<Integer, Integer> _skillFeeAmounts = new HashMap<Integer, Integer>();

	public NpcBufferSkills(int npcId)
	{
		_npcId = npcId;
	}

	public void addSkill(int skillId, int skillLevel, int skillFeeId, int skillFeeAmount)
	{
		_skillLevels.put(skillId, skillLevel);
		_skillFeeIds.put(skillId, skillFeeId);
		_skillFeeAmounts.put(skillId, skillFeeAmount);
	}

	public int[] getSkillInfo(int skillId)
	{
		Integer skillLevel = _skillLevels.get(skillId);
		Integer skillFeeId = _skillFeeIds.get(skillId);
		Integer skillFeeAmount = _skillFeeAmounts.get(skillId);

		if ((skillLevel == null) || (skillFeeId == null) || (skillFeeAmount == null))
			return null;

		return new int[]
		{
		skillLevel, skillFeeId, skillFeeAmount
		};
	}

	public int getNpcId()
	{
		return _npcId;
	}
}