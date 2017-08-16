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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.model.L2Effect;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.actor.L2Attackable;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.skills.Env;
import com.l2jhellas.util.Rnd;

/**
 * @author littlecrow
 *         Implementation of the Confusion Effect
 */
public final class EffectConfuseMob extends L2Effect
{

	public EffectConfuseMob(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.CONFUSE_MOB_ONLY;
	}

	/** Notify started */
	@Override
	public boolean onStart()
	{
		getEffected().startConfused();
		onActionTime();
		return true;
	}

	/** Notify exited */
	@Override
	public void onExit()
	{
		getEffected().stopConfused(this);
	}

	@Override
	public boolean onActionTime()
	{
		List<L2Character> targetList = new ArrayList<L2Character>();

		// Getting the possible targets

		Collection<L2Object> objs = getEffected().getKnownList().getKnownObjects().values();
		for (L2Object obj : objs)
		{
			if ((obj instanceof L2Attackable) && (obj != getEffected()))
				targetList.add((L2Character) obj);
		}

		// if there is no target, exit function
		if (targetList.isEmpty())
		{
			return true;
		}

		// Choosing randomly a new target
		int nextTargetIdx = Rnd.nextInt(targetList.size());
		L2Object target = targetList.get(nextTargetIdx);

		// Attacking the target
		// getEffected().setTarget(target);
		getEffected().setTarget(target);
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		((L2Attackable) getEffected()).addDamageHate((L2Character) target, 0, (4 + Rnd.get(4)) * getEffector().getLevel());
		return true;
	}
}