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
package com.l2jhellas.gameserver.skills.conditions;

import com.l2jhellas.gameserver.skills.Env;

public class ConditionPlayerHpPercentage extends Condition
{
	private final double _p;

	public ConditionPlayerHpPercentage(double p)
	{
		_p = p;
	}

	@Override
	public boolean testImpl(Env env)
	{
		return env.player.getCurrentHp() <= env.player.getMaxHp() * _p;
	}
}