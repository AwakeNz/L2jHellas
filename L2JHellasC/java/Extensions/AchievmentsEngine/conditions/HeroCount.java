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

import Extensions.AchievmentsEngine.base.Condition;

import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.entity.Hero;
import com.l2jhellas.gameserver.model.entity.olympiad.Olympiad;
import com.l2jhellas.gameserver.templates.StatsSet;

public class HeroCount extends Condition
{
	public HeroCount(Object value)
	{
		super(value);
		setName("Hero Count");
	}

	@Override
	public boolean meetConditionRequirements(L2PcInstance player)
	{
		if (getValue() == null)
			return false;

		int val = Integer.parseInt(getValue().toString());
		for (int hero : Hero.getInstance().getHeroes().keySet())
		{
			if (hero == player.getObjectId())
			{
				StatsSet sts = Hero.getInstance().getHeroes().get(hero);
				if (sts.getString(Olympiad.CHAR_NAME).equals(player.getName()))
				{
					if (sts.getInteger(Hero.COUNT) >= val)
						return true;
				}
			}
		}
		return false;
	}
}