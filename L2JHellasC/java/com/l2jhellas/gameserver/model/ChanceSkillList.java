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
package com.l2jhellas.gameserver.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jhellas.gameserver.handler.ISkillHandler;
import com.l2jhellas.gameserver.handler.SkillHandler;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.network.serverpackets.MagicSkillLaunched;
import com.l2jhellas.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jhellas.gameserver.skills.SkillTable;

/**
 * @author kombat
 */
public class ChanceSkillList extends ConcurrentHashMap<L2Skill, ChanceCondition>
{
	private static final long serialVersionUID = 1L;

	private L2Character _owner;

	public ChanceSkillList(L2Character owner)
	{
		super();
		_owner = owner;
	}

	public L2Character getOwner()
	{
		return _owner;
	}

	public void setOwner(L2Character owner)
	{
		_owner = owner;
	}

	public void onHit(L2Character target, boolean ownerWasHit, boolean wasCrit)
	{
		int event;
		if (ownerWasHit)
		{
			event = ChanceCondition.EVT_ATTACKED | ChanceCondition.EVT_ATTACKED_HIT;
			if (wasCrit)
				event |= ChanceCondition.EVT_ATTACKED_CRIT;
		}
		else
		{
			event = ChanceCondition.EVT_HIT;
			if (wasCrit)
				event |= ChanceCondition.EVT_CRIT;
		}

		onEvent(event, target);
	}

	public void onEvadedHit(L2Character attacker)
	{
		onEvent(ChanceCondition.EVT_EVADED_HIT, attacker);
	}

	public void onSkillHit(L2Character target, boolean ownerWasHit, boolean wasMagic, boolean wasOffensive)
	{
		int event;
		if (ownerWasHit)
		{
			event = ChanceCondition.EVT_HIT_BY_SKILL;
			if (wasOffensive)
			{
				event |= ChanceCondition.EVT_HIT_BY_OFFENSIVE_SKILL;
				event |= ChanceCondition.EVT_ATTACKED;
			}
			else
			{
				event |= ChanceCondition.EVT_HIT_BY_GOOD_MAGIC;
			}
		}
		else
		{
			event = ChanceCondition.EVT_CAST;
			event |= wasMagic ? ChanceCondition.EVT_MAGIC : ChanceCondition.EVT_PHYSICAL;
			event |= wasOffensive ? ChanceCondition.EVT_MAGIC_OFFENSIVE : ChanceCondition.EVT_MAGIC_GOOD;
		}

		onEvent(event, target);
	}

	public void onEvent(int event, L2Character target)
	{
		if (_owner.isDead())
			return;
		
		for (Map.Entry<L2Skill, ChanceCondition> entry : entrySet())
		{
			L2Skill sk = entry.getKey();
			ChanceCondition val = entry.getValue();
			
			if (val != null && val.trigger(event))
			{
				makeCast(sk, target);
			}
		}
	}

	private void makeCast(L2Skill skill, L2Character target)
	{
		try
		{
			if (skill.getWeaponDependancy(_owner))
			{
				if (skill.triggerAnotherSkill()) //should we use this skill or this skill is just referring to another one ...
				{
					skill = SkillTable.getInstance().getInfo(skill.getTriggeredId(), skill.getTriggeredLevel());
					if (skill == null)
						return;
				}

				if (_owner.isSkillDisabled(skill.getId()))
					return;
				
				if (skill.getReuseDelay() > 0)
					_owner.disableSkill(skill.getId());
				
				L2Object[] targets = skill.getTargetList(_owner, false);

				if (targets.length == 0)
					return;
				
				ISkillHandler handler = SkillHandler.getInstance().getHandler(skill.getSkillType());

				_owner.broadcastPacket(new MagicSkillLaunched(_owner, skill.getDisplayId(), skill.getLevel(), targets));
				_owner.broadcastPacket(new MagicSkillUse(_owner, (L2Character) targets[0], skill.getDisplayId(), skill.getLevel(), 0, 0));

				// Launch the magic skill and calculate its effects

				if (handler != null)
					handler.useSkill(_owner, skill, targets);
				else
					skill.useSkill(_owner, targets);
			}
		}
		catch (Exception e)
		{
		}
	}
}