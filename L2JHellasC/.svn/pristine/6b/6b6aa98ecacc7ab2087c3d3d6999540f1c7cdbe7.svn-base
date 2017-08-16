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
import com.l2jhellas.gameserver.handler.SkillHandler;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.L2SkillType;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.StatusUpdate;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;

/**
 * @author earendil
 */
public class BalanceLife implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.BALANCE_LIFE
	};

	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		// L2Character activeChar = activeChar;
		// check for other effects
		try
		{
			ISkillHandler handler = SkillHandler.getInstance().getHandler(L2SkillType.BUFF);

			if (handler != null)
				handler.useSkill(activeChar, skill, targets);
		}
		catch (Exception e)
		{
		}

		L2Character target = null;

		L2PcInstance player = null;
		if (activeChar instanceof L2PcInstance)
			player = (L2PcInstance) activeChar;

		double fullHP = 0;
		double currentHPs = 0;

		for (int index = 0; index < targets.length; index++)
		{
			target = (L2Character) targets[index];

			// We should not heal if char is dead
			if (target == null || target.isDead())
				continue;

			// Player holding a cursed weapon can't be healed and can't heal
			if (target != activeChar)
			{
				if (target instanceof L2PcInstance && ((L2PcInstance) target).isCursedWeaponEquiped())
					continue;
				else if (player != null && player.isCursedWeaponEquiped())
					continue;
			}

			fullHP += target.getMaxHp();
			currentHPs += target.getCurrentHp();
		}

		double percentHP = currentHPs / fullHP;

		for (int index = 0; index < targets.length; index++)
		{
			target = (L2Character) targets[index];

			double newHP = target.getMaxHp() * percentHP;
			double totalHeal = newHP - target.getCurrentHp();

			target.setCurrentHp(newHP);

			if (totalHeal > 0)
				target.setLastHealAmount((int) totalHeal);

			StatusUpdate su = new StatusUpdate(target.getObjectId());
			su.addAttribute(StatusUpdate.CUR_HP, (int) target.getCurrentHp());
			target.sendPacket(su);

			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
			sm.addString("HP of the party has been balanced.");
			target.sendPacket(sm);

		}
	}

	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}