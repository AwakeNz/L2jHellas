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

import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.model.L2CharPosition;
import com.l2jhellas.gameserver.model.L2Effect;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2SiegeGuardInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2SiegeSummonInstance;
import com.l2jhellas.gameserver.skills.Env;
import com.l2jhellas.gameserver.skills.Formulas;
import com.l2jhellas.util.Rnd;

/**
 * @author littlecrow
 *         Implementation of the Fear Effect
 */
public final class EffectFear extends L2Effect
{
	public static final int FEAR_RANGE = 500;

	public EffectFear(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.FEAR;
	}

	/** Notify started */
	@Override
	public boolean onStart()
	{
		if (!getEffected().isAfraid())
		{
			// Fear skills cannot be used l2pcinstance to l2pcinstance. Heroic Dread, Curse: Fear, Fear and Horror are the exceptions.
			if (getEffected() instanceof L2PcInstance && getEffector() instanceof L2PcInstance && getSkill().getId() != 1376 && getSkill().getId() != 1169 && getSkill().getId() != 65 && getSkill().getId() != 1092)
				return false;
			if (getEffected() instanceof L2NpcInstance)
				return false;
			if (getEffected() instanceof L2SiegeGuardInstance)
				return false;
			// Fear skills cannot be used on Headquarters Flag.
			if (getEffected() instanceof L2SiegeFlagInstance)
				return false;

			if (getEffected() instanceof L2SiegeSummonInstance)
				return false;

			getEffected().startFear();

			int posX = getEffected().getX();
			int posY = getEffected().getY();
			int posZ = getEffected().getZ();

			int signx = -1;
			int signy = -1;
			if (getEffected().getX() > getEffector().getX())
				signx = 1;
			if (getEffected().getY() > getEffector().getY())
				signy = 1;
			posX += signx * FEAR_RANGE;
			posY += signy * FEAR_RANGE;

			if (!getEffected().isRaid() && !(getEffected() instanceof L2DoorInstance) && !(getEffected() instanceof L2Npc && ((L2Npc) getEffected()).getNpcId() == 35062))
			{
				int chance = Rnd.get(100);
				if (getSkill().getLethalChance2() > 0 && chance < Formulas.getInstance().calcLethal(getEffector(), getEffected(), getSkill().getLethalChance2()))
				{
					if (getEffected() instanceof L2Npc)
					{
						getEffected().reduceCurrentHp(getEffected().getCurrentHp() - 1, getEffector());
					}
				}
			}

			getEffected().setRunning();
			getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(posX, posY, posZ, 0));
			return true;
		}
		return false;
	}

	@Override
	public boolean onActionTime()
	{
		getEffected().stopFear(this);
		return true;
	}
}