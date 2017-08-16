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
package com.l2jhellas.gameserver.handlers.skillhandlers;

import com.l2jhellas.gameserver.handler.ISkillHandler;
import com.l2jhellas.gameserver.model.L2Effect;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.L2SkillType;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.L2Summon;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.skills.Formulas;
import com.l2jhellas.logs.LogRecorder;

public class Mdam implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
	L2SkillType.MDAM, L2SkillType.DEATHLINK
	};

	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (activeChar.isAlikeDead())
			return;

		boolean ss = false;
		boolean bss = false;

		L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();

		/*
		 * if (activeChar instanceof L2PcInstance)
		 * {
		 * if (weaponInst == null)
		 * {
		 * SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessage.S1_S2);
		 * sm2.addString("You must equip a weapon before casting a spell.");
		 * activeChar.sendPacket(sm2);
		 * return;
		 * }
		 * }
		 */

		if (weaponInst != null)
		{
			if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
			{
				bss = true;
				weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
			}
			else if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_SPIRITSHOT)
			{
				ss = true;
				weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
			}
		}
		// If there is no weapon equipped, check for an active summon.
		else if (activeChar instanceof L2Summon)
		{
			L2Summon activeSummon = (L2Summon) activeChar;

			if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
			{
				bss = true;
				activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
			}
			else if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_SPIRITSHOT)
			{
				ss = true;
				activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
			}
		}

		for (int index = 0; index < targets.length; index++)
		{
			L2Character target = (L2Character) targets[index];

			if (activeChar instanceof L2PcInstance && target instanceof L2PcInstance && target.isAlikeDead() && target.isFakeDeath())
			{
				target.stopFakeDeath(null);
			}
			else if (target.isAlikeDead())
			{
				continue;
			}
			// if (skill != null)
			// if (skill.isOffensive())
			// {

			// boolean acted;
			// if (skill.getSkillType() == L2Skill.SkillType.DOT ||
			// skill.getSkillType() == L2Skill.SkillType.MDOT)
			// acted = Formulas.getInstance().calcSkillSuccess(
			// activeChar, target, skill);
			// else
			// acted = Formulas.getInstance().calcMagicAffected(
			// activeChar, target, skill);
			// if (!acted) {
			// activeChar.sendPacket(new
			// SystemMessage(SystemMessage.MISSED_TARGET));
			// continue;
			// }
			//
			// }

			boolean mcrit = Formulas.getInstance().calcMCrit(activeChar.getMCriticalHit(target, skill));

			int damage = (int) Formulas.getInstance().calcMagicDam(activeChar, target, skill, ss, bss, mcrit);

			if (damage > 5000 && activeChar instanceof L2PcInstance)
			{
				String name = "";
				if (target instanceof L2RaidBossInstance)
					name = "RaidBoss ";
				if (target instanceof L2Npc)
					name += target.getName() + "(" + ((L2Npc) target).getTemplate().npcId + ")";
				if (target instanceof L2PcInstance)
					name = target.getName() + "(" + target.getObjectId() + ") ";
				name += target.getLevel() + " lvl";
				LogRecorder.add(activeChar.getName() + "(" + activeChar.getObjectId() + ") " + activeChar.getLevel() + " lvl did damage " + damage + " with skill " + skill.getName() + "(" + skill.getId() + ") to " + name, "damage_mdam");
			}

			// Why are we trying to reduce the current target HP here?
			// Why not inside the below "if" condition, after the effects
			// processing as it should be?
			// It doesn't seem to make sense for me. I'm moving this line inside
			// the "if" condition, right after the effects processing...
			// [changed by nexus - 2006-08-15]
			// target.reduceCurrentHp(damage, activeChar);

			if (damage > 0)
			{
				// Manage attack or cast break of the target (calculating rate,
				// sending message...)
				if (!target.isRaid() && !target.isBoss() && Formulas.getInstance().calcAtkBreak(target, damage))
				{
					target.breakAttack();
					target.breakCast();
				}

				activeChar.sendDamageMessage(target, damage, mcrit, false, false);

				if (skill.hasEffects())
				{
					if (target.reflectSkill(skill))
					{
						activeChar.stopSkillEffects(skill.getId());
						skill.getEffects(null, activeChar);
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
						sm.addSkillName(skill.getId());
						activeChar.sendPacket(sm);
					}
					else
					{
						// activate attacked effects, if any
						target.stopSkillEffects(skill.getId());
						Formulas.getInstance();
						if (Formulas.calcSkillSuccess(activeChar, target, skill, false, ss, bss))
							skill.getEffects(activeChar, target);
						else
						{
							SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2);
							sm.addString(target.getName());
							sm.addSkillName(skill.getDisplayId());
							activeChar.sendPacket(sm);
						}
					}
				}

				target.reduceCurrentHp(damage, activeChar);
			}
		}
		// self Effect :]
		L2Effect effect = activeChar.getFirstEffect(skill.getId());
		if (effect != null && effect.isSelfEffect())
		{
			// Replace old effect with new one.
			effect.exit();
		}
		skill.getEffectsSelf(activeChar);

		if (skill.isSuicideAttack())
		{
			activeChar.doDie(null);
			activeChar.setCurrentHp(0);
		}
	}

	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}