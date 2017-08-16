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
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.skills.Env;

public class EffectGrow extends L2Effect
{
	public EffectGrow(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}

	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof L2Npc)
		{
			L2Npc npc = (L2Npc) getEffected();
			npc.setCollisionHeight((int) (npc.getCollisionHeight() * 1.24));
			npc.setCollisionRadius((int) (npc.getCollisionRadius() * 1.19));

			getEffected().startAbnormalEffect(L2Character.ABNORMAL_EFFECT_GROW);
			return true;
		}
		return false;
	}

	@Override
	public boolean onActionTime()
	{
		if (getEffected() instanceof L2Npc)
		{
			L2Npc npc = (L2Npc) getEffected();
			npc.setCollisionHeight(npc.getTemplate().collisionHeight);
			npc.setCollisionRadius(npc.getTemplate().collisionRadius);

			getEffected().stopAbnormalEffect(L2Character.ABNORMAL_EFFECT_GROW);
		}
		return false;
	}

	@Override
	public void onExit()
	{
		if (getEffected() instanceof L2Npc)
		{
			L2Npc npc = (L2Npc) getEffected();
			npc.setCollisionHeight(npc.getTemplate().collisionHeight);
			npc.setCollisionRadius(npc.getTemplate().collisionRadius);

			getEffected().stopAbnormalEffect(L2Character.ABNORMAL_EFFECT_GROW);
		}
	}
}