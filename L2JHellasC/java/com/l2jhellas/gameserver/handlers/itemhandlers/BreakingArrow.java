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
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.SocialAction;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;

public class BreakingArrow implements IItemHandler
{
	private static final int[] ITEM_IDS =
	{
		8192
	};

	@Override
	public void useItem(L2Playable playable, L2ItemInstance item)
	{
		int itemId = item.getItemId();

		if (!(playable instanceof L2PcInstance))
		{
			return;
		}

		L2PcInstance activeChar = (L2PcInstance) playable;
		L2Object target = activeChar.getTarget();
		if (!(target instanceof L2GrandBossInstance) || target == null)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INCORRECT_TARGET));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		L2GrandBossInstance Frintezza = (L2GrandBossInstance) target;
		if (!activeChar.isInsideRadius(Frintezza, 500, false, false))
		{
			activeChar.sendMessage("The purpose is inaccessible");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (itemId == 8192 && Frintezza.getObjectId() == 29045)
		{
			Frintezza.broadcastPacket(new SocialAction(Frintezza.getObjectId(), 2),2000);
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
		}
	}

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}