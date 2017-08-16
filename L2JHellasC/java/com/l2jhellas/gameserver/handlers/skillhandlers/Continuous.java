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

import com.l2jhellas.gameserver.ai.CtrlEvent;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.handler.ISkillHandler;
import com.l2jhellas.gameserver.instancemanager.DuelManager;
import com.l2jhellas.gameserver.model.L2Effect;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.L2SkillType;
import com.l2jhellas.gameserver.model.actor.L2Attackable;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.L2Summon;
import com.l2jhellas.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.skills.Formulas;

public class Continuous implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{/** @formatter:off */
	L2SkillType.BUFF,
	L2SkillType.DEBUFF,
	L2SkillType.DOT,
	L2SkillType.MDOT,
	L2SkillType.POISON,
	L2SkillType.BLEED,
	L2SkillType.HOT,
	L2SkillType.CPHOT,
	L2SkillType.MPHOT,
	L2SkillType.FEAR,
	L2SkillType.CONT,
	L2SkillType.WEAKNESS,
	L2SkillType.REFLECT,
	L2SkillType.UNDEAD_DEFENSE,
	L2SkillType.AGGDEBUFF,
	L2SkillType.FORCE_BUFF
	};/** @formatter:on */

	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		L2Character target = null;

		L2PcInstance player = null;
		if (activeChar instanceof L2PcInstance)
			player = (L2PcInstance) activeChar;

		for (int index = 0; index < targets.length; index++)
		{
			target = (L2Character) targets[index];

			if (skill.getSkillType() != L2SkillType.BUFF && skill.getSkillType() != L2SkillType.HOT && skill.getSkillType() != L2SkillType.CPHOT && skill.getSkillType() != L2SkillType.MPHOT && skill.getSkillType() != L2SkillType.UNDEAD_DEFENSE && skill.getSkillType() != L2SkillType.AGGDEBUFF && skill.getSkillType() != L2SkillType.CONT)
			{
				if (target.reflectSkill(skill))
					target = activeChar;
			}

			// Walls and Door should not be buffed
			if (target instanceof L2DoorInstance && (skill.getSkillType() == L2SkillType.BUFF || skill.getSkillType() == L2SkillType.HOT))
				continue;

			// Player holding a cursed weapon can't be buffed and can't buff
			if (skill.getSkillType() == L2SkillType.BUFF)
			{
				if (target != activeChar)
				{
					if (target instanceof L2PcInstance && ((L2PcInstance) target).isCursedWeaponEquiped())
						continue;
					else if (player != null && player.isCursedWeaponEquiped())
						continue;
				}
			}

			if (skill.isOffensive())
			{

				boolean ss = false;
				boolean sps = false;
				boolean bss = false;
				if (player != null)
				{
					L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
					if (weaponInst != null)
					{
						if (skill.isMagic())
						{
							if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
							{
								bss = true;
								if (skill.getId() != 1020) // vitalize
									weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
							}
							else if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_SPIRITSHOT)
							{
								sps = true;
								if (skill.getId() != 1020) // vitalize
									weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
							}
						}
						else if (weaponInst.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT)
						{
							ss = true;
							if (skill.getId() != 1020) // vitalize
								weaponInst.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
						}
					}
				}
				else if (activeChar instanceof L2Summon)
				{
					L2Summon activeSummon = (L2Summon) activeChar;
					if (skill.isMagic())
					{
						if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
						{
							bss = true;
							activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
						}
						else if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_SPIRITSHOT)
						{
							sps = true;
							activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
						}
					}
					else if (activeSummon.getChargedSoulShot() == L2ItemInstance.CHARGED_SOULSHOT)
					{
						ss = true;
						activeSummon.setChargedSoulShot(L2ItemInstance.CHARGED_NONE);
					}
				}

				Formulas.getInstance();
				boolean acted = Formulas.calcSkillSuccess(activeChar, target, skill, ss, sps, bss);
				if (!acted)
				{
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
					continue;
				}

			}
			boolean stopped = false;
			L2Effect[] effects = target.getAllEffects();
			if (effects != null)
			{
				for (L2Effect e : effects)
				{
					if (e != null && skill != null)
						if (e.getSkill().getId() == skill.getId())
						{
							e.exit();
							stopped = true;
						}
				}
			}
			if (skill.isToggle() && stopped)
				return;

			// if this is a debuff let the duel manager know about it
			// so the debuff can be removed after the duel
			// (player & target must be in the same duel)
			if (target instanceof L2PcInstance && ((L2PcInstance) target).isInDuel() && (skill.getSkillType() == L2SkillType.DEBUFF || skill.getSkillType() == L2SkillType.BUFF) && player.getDuelId() == ((L2PcInstance) target).getDuelId())
			{
				DuelManager dm = DuelManager.getInstance();
				for (L2Effect buff : skill.getEffects(activeChar, target))
					if (buff != null)
						dm.onBuff(((L2PcInstance) target), buff);
			}
			else
				skill.getEffects(activeChar, target);

			if (skill.getSkillType() == L2SkillType.AGGDEBUFF)
			{
				if (target instanceof L2Attackable)
					target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, (int) skill.getPower());
				else if (target instanceof L2Playable)
				{
					if (target.getTarget() == activeChar)
						target.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, activeChar);
					else
						target.setTarget(activeChar);
				}
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
	}

	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}