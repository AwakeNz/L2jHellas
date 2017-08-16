/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jhellas.gameserver.model.actor.stat;

import Extensions.Balancer.BalanceLoad;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.skills.Calculator;
import com.l2jhellas.gameserver.skills.Env;
import com.l2jhellas.gameserver.skills.Stats;

public class CharStat
{
	private final L2Character _activeChar;
	private long _exp = 0;
	private int _sp = 0;
	private byte _level = 1;

	private int _fire = 0;
	private int _water = 0;
	private int _wind = 0;
	private int _earth = 0;
	private int _holy = 0;
	private int _dark = 0;

	public CharStat(L2Character activeChar)
	{
		_activeChar = activeChar;
	}

	/**
	 * Calculate the new value of the state with modifiers that will be applied
	 * on the targeted L2Character.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * A L2Character owns a table of Calculators called <B>_calculators</B>.
	 * Each Calculator (a calculator per state) own a table of Func object. A
	 * Func object is a mathematic function that permit to calculate the
	 * modifier of a state (ex : REGENERATE_HP_RATE...) : <BR>
	 * <BR>
	 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR>
	 * <BR>
	 * When the calc method of a calculator is launched, each mathematic
	 * function is called according to its priority <B>_order</B>. Indeed, Func
	 * with lowest priority order is executed firsta and Funcs with the same
	 * order are executed in unspecified order. The result of the calculation is
	 * stored in the value property of an Env class instance.<BR>
	 * <BR>
	 * 
	 * @param stat
	 *        The stat to calculate the new value with modifiers
	 * @param init
	 *        The initial value of the stat before applying modifiers
	 * @param target
	 *        The L2Charcater whose properties will be used in the
	 *        calculation (ex : CON, INT...)
	 * @param skill
	 *        The L2Skill whose properties will be used in the calculation
	 *        (ex : Level...)
	 */
	public final double calcStat(Stats stat, double init, L2Character target, L2Skill skill)
	{
		if (_activeChar == null)
			return init;

		int id = stat.ordinal();

		Calculator c = _activeChar.getCalculators()[id];

		// If no Func object found, no modifier is applied
		if (c == null || c.size() == 0)
			return init;

		// Create and init an Env object to pass parameters to the Calculator
		Env env = new Env();
		env.player = _activeChar;
		env.target = target;
		env.skill = skill;
		env.value = init;

		// Launch the calculation
		c.calc(env);
		// avoid some troubles with negative stats (some stats should never be
		// negative)
		if (env.value <= 0 && ((stat == Stats.MAX_HP) || (stat == Stats.MAX_MP) || (stat == Stats.MAX_CP) || (stat == Stats.MAGIC_DEFENCE) || (stat == Stats.POWER_DEFENCE) || (stat == Stats.POWER_ATTACK) || (stat == Stats.MAGIC_ATTACK) || (stat == Stats.POWER_ATTACK_SPEED) || (stat == Stats.MAGIC_ATTACK_SPEED) || (stat == Stats.SHIELD_DEFENCE) || (stat == Stats.STAT_CON) || (stat == Stats.STAT_DEX) || (stat == Stats.STAT_INT) || (stat == Stats.STAT_MEN) || (stat == Stats.STAT_STR) || (stat == Stats.STAT_WIT)))
		{
			env.value = 1;
		}

		return env.value;
	}
	
	/**
	 * Return the STR of the L2Character (base+modifier).
	 */
	public final int getSTR()
	{
		int str = (int) calcStat(Stats.STAT_STR, _activeChar.getTemplate().baseSTR, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			str += BalanceLoad.STR[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return str;
	}
	
	/**
	 * Return the DEX of the L2Character (base+modifier).
	 */
	public final int getDEX()
	{
		int DEX = (int) calcStat(Stats.STAT_DEX, _activeChar.getTemplate().baseDEX, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			DEX += BalanceLoad.DEX[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return DEX;
	}
	
	/**
	 * Return the CON of the L2Character (base+modifier).
	 */
	public final int getCON()
	{
		int CON = (int) calcStat(Stats.STAT_CON, _activeChar.getTemplate().baseCON, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			CON += BalanceLoad.CON[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return CON;
	}

	/**
	 * Return the INT of the L2Character (base+modifier).
	 */
	public int getINT()
	{
		int INT = (int) calcStat(Stats.STAT_INT, _activeChar.getTemplate().baseINT, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			INT += BalanceLoad.INT[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return INT;
	}
	
	/**
	 * Return the MEN of the L2Character (base+modifier).
	 */
	public final int getMEN()
	{
		int MEN = (int) calcStat(Stats.STAT_MEN, _activeChar.getTemplate().baseMEN, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			MEN += BalanceLoad.MEN[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return MEN;
	}

	/**
	 * Return the WIT of the L2Character (base+modifier).
	 */
	public final int getWIT()
	{
		int WIT = (int) calcStat(Stats.STAT_WIT, _activeChar.getTemplate().baseWIT, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			WIT += BalanceLoad.WIT[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return WIT;
	}
	
	/**
	 *  Return the Critical Hit rate (base+modifier) of the L2Character.
	 */
	public int getCriticalHit(L2Character target, L2Skill skill)
	{
		int criticalHit = (int) (calcStat(Stats.CRITICAL_RATE, _activeChar.getTemplate().baseCritRate, target, skill) * 10.0 + 0.5);
		criticalHit /= 10;
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			criticalHit += BalanceLoad.Critical[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		// Set a cap of Critical Hit at 500
		if (criticalHit > Config.MAX_PCRIT_RATE)
			criticalHit = Config.MAX_PCRIT_RATE;
		
		return criticalHit;
	}
	
	/**
	 *  Return the Magic Critical Hit rate (base+modifier) of the L2Character. 
	 */
	public final int getMCriticalHit(L2Character target, L2Skill skill)
	{
		int mrate = (int) calcStat(Stats.MCRITICAL_RATE, _activeChar.getTemplate().baseMCritRate, target, skill);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			mrate += BalanceLoad.MagicCritical[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		if (mrate > Config.MAX_MCRIT_RATE)
			mrate = Config.MAX_MCRIT_RATE;
		return mrate;
	}
	
	/**
	 * Return the Attack Evasion rate (base+modifier) of the L2Character.
	 */
	public int getEvasionRate(L2Character target)
	{
		int val = (int) (calcStat(Stats.EVASION_RATE, 0, target, null) / _activeChar.getArmourExpertisePenalty());
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.Evasion[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return (int) val;
	}
	
	/**
	 * Return the Accuracy (base+modifier) of the L2Character in function of the
	 * Weapon Expertise Penalty.
	 */
	public int getAccuracy()
	{
		int val = (int) calcStat(Stats.ACCURACY_COMBAT, 0, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.Accuracy[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	public int getMaxHp()
	{
		int val = (int) calcStat(Stats.MAX_HP, _activeChar.getTemplate().baseHpMax, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.HP[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	public int getMaxCp()
	{
		int val = (int) calcStat(Stats.MAX_CP, _activeChar.getTemplate().baseCpMax, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.CP[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	public int getMaxMp()
	{
		int val = (int) calcStat(Stats.MAX_MP, _activeChar.getTemplate().baseMpMax, null, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.MP[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	/**
	 * Return the MAtk (base+modifier) of the L2Character for a skill used in
	 * function of abnormal effects in progress.<BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Calculate Magic damage</li> <BR>
	 * <BR>
	 * 
	 * @param target
	 *        The L2Character targeted by the skill
	 * @param skill
	 *        The L2Skill used against the target
	 */
	
	@SuppressWarnings("incomplete-switch")
	public int getMAtk(L2Character target, L2Skill skill)
	{
		if (_activeChar == null)
			return 1;
		float bonusAtk = 1;
		if (_activeChar.isChampion())
			bonusAtk = Config.CHAMPION_ATK;
		double attack = _activeChar.getTemplate().baseMAtk * bonusAtk;
		// Get the skill type to calculate its effect in function of base stats
		// of the L2Character target
		Stats stat = skill == null ? null : skill.getStat();

		if (stat != null)
		{
			switch (stat)
			{
				case AGGRESSION:
					attack += _activeChar.getTemplate().baseAggression;
				break;
				case BLEED:
					attack += _activeChar.getTemplate().baseBleed;
				break;
				case POISON:
					attack += _activeChar.getTemplate().basePoison;
				break;
				case STUN:
					attack += _activeChar.getTemplate().baseStun;
				break;
				case ROOT:
					attack += _activeChar.getTemplate().baseRoot;
				break;
				case MOVEMENT:
					attack += _activeChar.getTemplate().baseMovement;
				break;
				case CONFUSION:
					attack += _activeChar.getTemplate().baseConfusion;
				break;
				case SLEEP:
					attack += _activeChar.getTemplate().baseSleep;
				break;
				case FIRE:
					attack += _activeChar.getTemplate().baseFire;
				break;
				case WIND:
					attack += _activeChar.getTemplate().baseWind;
				break;
				case WATER:
					attack += _activeChar.getTemplate().baseWater;
				break;
				case EARTH:
					attack += _activeChar.getTemplate().baseEarth;
				break;
				case HOLY:
					attack += _activeChar.getTemplate().baseHoly;
				break;
				case DARK:
					attack += _activeChar.getTemplate().baseDark;
				break;
			}
		}

		// Add the power of the skill to the attack effect
		if (skill != null)
			attack += skill.getPower();
		
		// Calculate modifiers Magic Attack
		int val = (int) calcStat(Stats.MAGIC_ATTACK, attack, target, skill);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.MAtk[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	/**
	 * Return the MAtk Speed (base+modifier) of the L2Character in function of the Armor Expertise Penalty.
	 */
	public int getMAtkSpd()
	{
		float bonusSpdAtk = 1;
		if (_activeChar.isChampion())
			bonusSpdAtk = Config.CHAMPION_SPD_ATK;
		
		int val = (int) calcStat(Stats.MAGIC_ATTACK_SPEED, _activeChar.getTemplate().baseMAtkSpd * bonusSpdAtk, null, null);
		val /= _activeChar.getArmourExpertisePenalty();
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.MAtkSpd[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	/**
	 * Return the MDef (base+modifier) of the L2Character against a skill in
	 * function of abnormal effects in progress.<BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Calculate Magic damage</li> <BR>
	 * 
	 * @param target
	 *        The L2Character targeted by the skill
	 * @param skill
	 *        The L2Skill used against the target
	 */
	public int getMDef(L2Character target, L2Skill skill)
	{
		// Get the base MDef of the L2Character
		double defence = _activeChar.getTemplate().baseMDef;

		// Calculate modifier for Raid Bosses
		if (_activeChar.isRaid() || _activeChar.isBoss())
			defence *= Config.RAID_M_DEFENCE_MULTIPLIER;

		// Calculate modifiers Magic Attack
		int val = (int) calcStat(Stats.MAGIC_DEFENCE, defence, target, skill);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.MDef[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	/**
	 * Return the PAtk (base+modifier) of the L2Character.
	 */
	public int getPAtk(L2Character target)
	{
		float bonusAtk = 1;
		if (_activeChar.isChampion())
			bonusAtk = Config.CHAMPION_ATK;
		int val = (int) calcStat(Stats.POWER_ATTACK, _activeChar.getTemplate().basePAtk * bonusAtk, target, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.PAtk[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	/**
	 * Return the PAtk Speed (base+modifier) of the L2Character in function of the Armor Expertise Penalty.
	 */
	public int getPAtkSpd()
	{
		float bonusAtk = 1;
		if (_activeChar.isChampion())
			bonusAtk = Config.CHAMPION_SPD_ATK;
		
		int val = (int) (calcStat(Stats.POWER_ATTACK_SPEED, _activeChar.getTemplate().basePAtkSpd * bonusAtk, null, null) / _activeChar.getArmourExpertisePenalty());
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.PAtkSpd[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	/** 
	 * Return the PDef (base+modifier) of the L2Character.
	 */
	public int getPDef(L2Character target)
	{
		// Get the base PDef of the L2Character
		double defence = _activeChar.getTemplate().basePDef;

		// Calculate modifier for Raid Bosses
		if (_activeChar.isRaid() || _activeChar.isBoss())
			defence *= Config.RAID_P_DEFENCE_MULTIPLIER;

		// Calculate modifiers Magic Attack
		int val = (int) calcStat(Stats.POWER_DEFENCE, defence, target, null);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.PDef[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return val;
	}
	
	/** Return the PAtk Modifier against animals. */
	public final double getPAtkAnimals(L2Character target)
	{
		return calcStat(Stats.PATK_ANIMALS, 1, target, null);
	}
	/** Return the PAtk Modifier against dragons. */
	public final double getPAtkDragons(L2Character target)
	{
		return calcStat(Stats.PATK_DRAGONS, 1, target, null);
	}
	/** Return the PAtk Modifier against insects. */
	public final double getPAtkInsects(L2Character target)
	{
		return calcStat(Stats.PATK_INSECTS, 1, target, null);
	}
	/** Return the PAtk Modifier against monsters. */
	public final double getPAtkMonsters(L2Character target)
	{
		return calcStat(Stats.PATK_BEAST, 1, target, null);
	}
	/** Return the PAtk Modifier against plants. */
	public final double getPAtkPlants(L2Character target)
	{
		return calcStat(Stats.PATK_PLANTS, 1, target, null);
	}
	public final double getPAtkGiants(L2Character target)
	{
		return calcStat(Stats.PATK_PLANTS, 1, target, null);
	}
	/** Return the PAtk Modifier against undead. */
	public final double getPAtkUndead(L2Character target)
	{
		return calcStat(Stats.PATK_UNDEAD, 1, target, null);
	}
	public final double getPDefUndead(L2Character target)
	{
		return calcStat(Stats.PDEF_UNDEAD, 1, target, null);
	}
	
	/**
	 * Return the Physical Attack range (base+modifier) of the L2Character.
	 */
	public final int getPhysicalAttackRange()
	{
		return (int) calcStat(Stats.POWER_ATTACK_RANGE, _activeChar.getTemplate().baseAtkRange, null, null);
	}
	
	/**
	 * Return the weapon reuse modifier
	 */
	public final double getWeaponReuseModifier(L2Character target)
	{
		return calcStat(Stats.ATK_REUSE, 1, target, null);
	}
	
	/**
	 * Return the WalkSpeed (base+modifier) of the L2Character.
	 */
	public int getWalkSpeed()
	{
		if (_activeChar instanceof L2PcInstance)
			return (getRunSpeed() * 70) / 100;
		else
			return (int) calcStat(Stats.WALK_SPEED, _activeChar.getTemplate().baseWalkSpd, null, null);
	}
	
	/**
	 * Return the RunSpeed (base+modifier) of the L2Character in function of the Armor Expertise Penalty.
	 */
	public int getRunSpeed()
	{
		// err we should be adding TO the persons run speed not making it a constant
		int val = (int) (calcStat(Stats.RUN_SPEED, _activeChar.getTemplate().baseRunSpd, null, null));
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			val += BalanceLoad.Speed[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		
		if (_activeChar.isFlying())
			return val += Config.WYVERN_SPEED;
		if (_activeChar.isRiding())
			return val += Config.STRIDER_SPEED;
		val /= _activeChar.getArmourExpertisePenalty();
		// Apply max run speed cap.
		if (val > Config.MAX_RUN_SPEED)
			val = Config.MAX_RUN_SPEED;
		return val;
	}
	
	/**
	 * Return the ShieldDef rate (base+modifier) of the L2Character.
	 */
	public final int getShldDef()
	{
		return (int) calcStat(Stats.SHIELD_DEFENCE, 0, null, null);
	}
	
	/**
	 * Return the mpConsume.
	 */
	public final int getMpConsume(L2Skill skill)
	{
		if (skill == null)
			return 1;
		double mpconsume = skill.getMpConsume();
		if (skill.isDance())
			if (_activeChar != null && _activeChar.getDanceCount() > 0)
				mpconsume += _activeChar.getDanceCount() * skill.getNextDanceMpCost();
		return (int) calcStat(Stats.MP_CONSUME, mpconsume, null, skill);
	}
	
	/**
	 * Return the mpInitialConsume.
	 */
	public final int getMpInitialConsume(L2Skill skill)
	{
		if (skill == null)
			return 1;

		return (int) calcStat(Stats.MP_CONSUME, skill.getMpInitialConsume(), null, skill);
	}
	
	public int getAttackElementValue(int attackAttribute)
	{
		switch (attackAttribute)
		{
			case -2:
				return 0;
			case 0:
				return _fire = (int) calcStat(Stats.FIRE_POWER, 0, null, null);
			case 1:
				return _water = (int) calcStat(Stats.WATER_POWER, 0, null, null);
			case 2:
				return _wind = (int) calcStat(Stats.WIND_POWER, 0, null, null);
			case 3:
				return _earth = (int) calcStat(Stats.EARTH_POWER, 0, null, null);
			case 4:
				return _holy = (int) calcStat(Stats.HOLY_POWER, 0, null, null);
			case 5:
				return _dark = (int) calcStat(Stats.DARK_POWER, 0, null, null);
		}
		return 0;
	}
	
	public int getAttackElement()
	{
		double tempVal = 0, stats[] =
		{
		_fire, _water, _wind, _earth, _holy, _dark
		};

		int returnVal = -2;
		_earth = (int) calcStat(Stats.EARTH_POWER, 0, null, null);
		_fire = (int) calcStat(Stats.FIRE_POWER, 0, null, null);
		_water = (int) calcStat(Stats.WATER_POWER, 0, null, null);
		_wind = (int) calcStat(Stats.WIND_POWER, 0, null, null);
		_holy = (int) calcStat(Stats.HOLY_POWER, 0, null, null);
		_dark = (int) calcStat(Stats.DARK_POWER, 0, null, null);

		for (int x = 0; x < stats.length; x++)
		{
			if (stats[x] > tempVal)
			{
				returnVal = x;
				tempVal = stats[x];
			}
		}

		return returnVal;
	}
	
	public float getMovementSpeedMultiplier()
	{
		return getRunSpeed() / (float) _activeChar.getTemplate().baseRunSpd;
	}
	
	/**
	 * Return the Attack Speed multiplier (base+modifier) of the L2Character to get proper animations.
	 */
	public final float getAttackSpeedMultiplier()
	{
		return (float) ((1.1) * getPAtkSpd() / _activeChar.getTemplate().basePAtkSpd);
	}
	
	/**
	 * Return the RunSpeed (base+modifier) or WalkSpeed (base+modifier) of the
	 * L2Character in function of the movement type.
	 */
	public float getMoveSpeed()
	{
		return (_activeChar.isRunning()) ? getRunSpeed() : getWalkSpeed();
	}

	public long getExp()
	{
		return _exp;
	}
	
	public void setExp(long value)
	{
		_exp = value;
	}
	
	public int getSp()
	{
		return _sp;
	}

	public void setSp(int value)
	{
		_sp = value;
	}

	public byte getLevel()
	{
		return _level;
	}

	public void setLevel(byte value)
	{
		_level = value;
	}
	
	public L2Character getActiveChar()
	{
		return _activeChar;
	}
	
	/**
	 * Return the Critical Damage rate (base+modifier) of the L2Character.
	 */
	public final double getCriticalDmg(L2Character target, double init)
	{
		return calcStat(Stats.CRITICAL_DAMAGE, init, target, null);
	}

	/**
	 * Return the Magical Attack range (base+modifier) of the L2Character.
	 */
	public final int getMagicalAttackRange(L2Skill skill)
	{
		if (skill != null)
		{
			int MAtkRange = (int) calcStat(Stats.MAGIC_ATTACK_RANGE, skill.getCastRange(), null, skill);
			if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
				MAtkRange += BalanceLoad.MAtkRange[((L2PcInstance) _activeChar).getClassId().getId() - 88];
			return MAtkRange;
		}
		return _activeChar.getTemplate().baseAtkRange;
	}

	/** Return the MReuse rate (base+modifier) of the L2Character. */
	public final double getMReuseRate(L2Skill skill)
	{
		double MReuseRate = calcStat(Stats.MAGIC_REUSE_RATE, _activeChar.getTemplate().baseMReuseRate, null, skill);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			MReuseRate += BalanceLoad.MReuseRate[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return (double) MReuseRate;
	}

	/**
	 *  Return the PReuse rate (base+modifier) of the L2Character.
	 */
	public final double getPReuseRate(L2Skill skill)
	{
		double PReuseRate = calcStat(Stats.P_REUSE, _activeChar.getTemplate().baseMReuseRate, null, skill);
		if (_activeChar instanceof L2PcInstance && ((L2PcInstance) _activeChar).getClassId().getId() >= 88)
			PReuseRate += BalanceLoad.PReuseRate[((L2PcInstance) _activeChar).getClassId().getId() - 88];
		return (double) PReuseRate;
	}
}