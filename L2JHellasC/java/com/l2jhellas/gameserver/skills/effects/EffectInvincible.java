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
package com.l2jhellas.gameserver.skills.effects;

import com.l2jhellas.gameserver.model.L2Effect;
import com.l2jhellas.gameserver.skills.Env;

public class EffectInvincible extends L2Effect
{
	public EffectInvincible(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return L2Effect.EffectType.INVINCIBLE;
	}

	@Override
	public boolean onStart()
	{
		getEffected().setIsInvul(true);
		return true;
	}

	@Override
	public boolean onActionTime()
	{
		// Simply stop the effect
		getEffected().setIsInvul(false);
		return false;
	}

	@Override
	public void onExit()
	{
		getEffected().setIsInvul(false);
	}
}