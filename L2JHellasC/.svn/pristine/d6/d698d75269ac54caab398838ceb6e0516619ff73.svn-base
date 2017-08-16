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

import com.l2jhellas.gameserver.datatables.xml.NpcData;
import com.l2jhellas.gameserver.handler.IItemHandler;
import com.l2jhellas.gameserver.idfactory.IdFactory;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2Spawn;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;

public class ChristmasTree implements IItemHandler
{
	private static final int[] ITEM_IDS =
	{
	5560, /* x-mas tree */
	5561
	/* Special x-mas tree */
	};

	private static final int[] NPC_IDS =
	{
	13006, /* Christmas tree w. flashing lights and snow */
	13007
	};

	@Override
	public void useItem(L2Playable playable, L2ItemInstance item)
	{
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2NpcTemplate template1 = null;

		int itemId = item.getItemId();
		for (int i = 0; i < ITEM_IDS.length; i++)
		{
			if (ITEM_IDS[i] == itemId)
			{
				template1 = NpcData.getInstance().getTemplate(NPC_IDS[i]);
				break;
			}
		}

		if (template1 == null)
			return;

		L2Object target = activeChar.getTarget();
		if (target == null)
			target = activeChar;

		try
		{
			L2Spawn spawn = new L2Spawn(template1);
			spawn.setId(IdFactory.getInstance().getNextId());
			spawn.setLocx(target.getX());
			spawn.setLocy(target.getY());
			spawn.setLocz(target.getZ());
			L2World.getInstance().storeObject(spawn.spawnOne());

			activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);

			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
			sm.addString("Created " + template1.name + " at x: " + spawn.getLocx() + " y: " + spawn.getLocy() + " z: " + spawn.getLocz());
			activeChar.sendPacket(sm);
		}
		catch (Exception e)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
			sm.addString("Target is not ingame.");
			activeChar.sendPacket(sm);
		}
	}

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}