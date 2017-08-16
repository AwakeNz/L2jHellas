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
import com.l2jhellas.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.ExRegenHp;
import com.l2jhellas.gameserver.network.serverpackets.StatusUpdate;
import com.l2jhellas.gameserver.skills.Env;

public final class EffectHealOverTime extends L2Effect
{
	public EffectHealOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.HEAL_OVER_TIME;
	}

	@Override
	public boolean onActionTime()
	{	
		if (getEffected().isDead() || getEffected() instanceof L2DoorInstance)
 			return false;
		
		final double maxHp = getEffected().getMaxHp();
		double newHp = getEffected().getCurrentHp() + calc();
		if (newHp > maxHp)
			newHp = maxHp;
				
		// Set hp amount.
		getEffected().setCurrentHp(newHp);
				
		// Send status update.
		final StatusUpdate su = new StatusUpdate(getEffected().getObjectId());
		su.addAttribute(StatusUpdate.CUR_HP, (int) newHp);
		getEffected().sendPacket(su);
		return true;
	}
	
 	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof L2PcInstance && getTotalCount() > 0 && getPeriod() > 0)
			getEffected().sendPacket(new ExRegenHp(getTotalCount() * getPeriod(), getPeriod(), calc()));
		
		return true;
	}
}