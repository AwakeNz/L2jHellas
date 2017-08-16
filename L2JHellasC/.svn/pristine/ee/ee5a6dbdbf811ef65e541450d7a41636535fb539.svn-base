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

import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.templates.StatsSet;
import com.l2jhellas.util.Rnd;

/**
 * @author kombat
 */
public final class ChanceCondition
{
	protected static final Logger _log = Logger.getLogger(ChanceCondition.class.getName());

	public static final int EVT_HIT = 1;
	public static final int EVT_CRIT = 2;
	public static final int EVT_CAST = 4;
	public static final int EVT_PHYSICAL = 8;
	public static final int EVT_MAGIC = 16;
	public static final int EVT_MAGIC_GOOD = 32;
	public static final int EVT_MAGIC_OFFENSIVE = 64;
	public static final int EVT_ATTACKED = 128;
	public static final int EVT_ATTACKED_HIT = 256;
	public static final int EVT_ATTACKED_CRIT = 512;
	public static final int EVT_HIT_BY_SKILL = 1024;
	public static final int EVT_HIT_BY_OFFENSIVE_SKILL = 2048;
	public static final int EVT_HIT_BY_GOOD_MAGIC = 4096;
	public static final int EVT_EVADED_HIT = 8192;

	public static enum TriggerType
	{
		// You hit an enemy
		ON_HIT(1),
		// You hit an enemy - was crit
		ON_CRIT(2),
		// You cast a skill
		ON_CAST(4),
		// You cast a skill - it was a physical one
		ON_PHYSICAL(8),
		// You cast a skill - it was a magic one
		ON_MAGIC(16),
		// You cast a skill - it was a magic one - good magic
		ON_MAGIC_GOOD(32),
		// You cast a skill - it was a magic one - offensive magic
		ON_MAGIC_OFFENSIVE(64),
		// You are attacked by enemy
		ON_ATTACKED(128),
		// You are attacked by enemy - by hit
		ON_ATTACKED_HIT(256),
		// You are attacked by enemy - by hit - was crit
		ON_ATTACKED_CRIT(512),
		// A skill was casted on you
		ON_HIT_BY_SKILL(1024),
		// An evil skill was casted on you
		ON_HIT_BY_OFFENSIVE_SKILL(2048),
		// A good skill was casted on you
		ON_HIT_BY_GOOD_MAGIC(4096),
		// Evading melee attack
		ON_EVADED_HIT(8192);

		private int _mask;

		private TriggerType(int mask)
		{
			_mask = mask;
		}

		public boolean check(int event)
		{
			return (_mask & event) != 0; // Trigger (sub-)type contains event (sub-)type
		}
	}

	private final TriggerType _triggerType;

	private final int _chance;

	private ChanceCondition(TriggerType trigger, int chance)
	{
		_triggerType = trigger;
		_chance = chance;
	}

	public static ChanceCondition parse(StatsSet set)
	{
		try
		{
			TriggerType trigger = set.getEnum("chanceType", TriggerType.class, null);
			int chance = set.getInteger("activationChance", 0);
			if (trigger != null && chance > 0)
				return new ChanceCondition(trigger, chance);
		}
		catch (Exception e)
		{
			_log.warning(ChanceCondition.class.getSimpleName() + ": ChanceCondition: Condition parse error.");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
		return null;
	}

	public boolean trigger(int event)
	{
		return _triggerType.check(event) && Rnd.get(100) < _chance;
	}

	@Override
	public String toString()
	{
		return "Trigger[" + _chance + ";" + _triggerType.toString() + "]";
	}
}