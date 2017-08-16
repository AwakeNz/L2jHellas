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
package com.l2jhellas.gameserver.handlers.itemhandlers;

import com.l2jhellas.gameserver.handler.IItemHandler;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jhellas.gameserver.skills.SkillTable;

public class Firework implements IItemHandler
{
	private static final int[] ITEM_IDS =
	{
	6403, 6406, 6407
	};

	@Override
	public void useItem(L2Playable playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return; // prevent Class cast exception
		L2PcInstance activeChar = (L2PcInstance) playable;
		int itemId = item.getItemId();
		/** @formatter:off */
		if (activeChar.isInOlympiadMode()
				|| activeChar.inObserverMode()
				|| activeChar.inObserverMode()
				|| activeChar.isSitting()				
				|| activeChar.isConfused()
				|| activeChar.isStunned()
				|| activeChar.isDead()
				|| activeChar.isAlikeDead())
		{/** @formatter:on */
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		/**
		 * Elven Firecracker
		 */
		if (itemId == 6403) // elven_firecracker, xml: 2023
		{
			MagicSkillUse MSU = new MagicSkillUse(playable, activeChar, 2023, 1, 1, 0);
			activeChar.sendPacket(MSU);
			activeChar.broadcastPacket(MSU,800);
			useFw(activeChar, 2023, 1);
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
		}
		/**
		 * Firework
		 */
		else if (itemId == 6406) // firework, xml: 2024
		{
			MagicSkillUse MSU = new MagicSkillUse(playable, activeChar, 2024, 1, 1, 0);
			activeChar.sendPacket(MSU);
			activeChar.broadcastPacket(MSU,800);
			useFw(activeChar, 2024, 1);
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
		}
		/**
		 * Lage Firework
		 */
		else if (itemId == 6407) // large_firework, xml: 2025
		{
			MagicSkillUse MSU = new MagicSkillUse(playable, activeChar, 2025, 1, 1, 0);
			activeChar.sendPacket(MSU);
			activeChar.broadcastPacket(MSU,800);
			useFw(activeChar, 2025, 1);
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
		}
	}

	public void useFw(L2PcInstance activeChar, int magicId, int level)
	{
		L2Skill skill = SkillTable.getInstance().getInfo(magicId, level);
		if (skill != null)
		{
			activeChar.useMagic(skill, false, false);
		}
	}

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}