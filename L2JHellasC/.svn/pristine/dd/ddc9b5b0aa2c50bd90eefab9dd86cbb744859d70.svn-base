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
package Extensions.AchievmentsEngine.conditions;

import java.util.Map;

import Extensions.AchievmentsEngine.base.Condition;

import com.l2jhellas.gameserver.instancemanager.RaidBossPointsManager;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

public class RaidKill extends Condition
{
	public RaidKill(Object value)
	{
		super(value);
		setName("Raid Kill");
	}

	@Override
	public boolean meetConditionRequirements(L2PcInstance player)
	{
		if (getValue() == null)
			return false;

		int val = Integer.parseInt(getValue().toString());

		Map<Integer, Integer> list = RaidBossPointsManager.getInstance().getList(player);
		if (list != null)
		{
			for (int bid : list.keySet())
			{
				if (bid == val)
				{
					if (RaidBossPointsManager.getInstance().getList(player).get(bid) > 0)
						return true;
				}
			}
		}
		return false;
	}
}