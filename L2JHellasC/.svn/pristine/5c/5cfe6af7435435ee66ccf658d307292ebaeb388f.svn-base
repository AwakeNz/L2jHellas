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
import com.l2jhellas.gameserver.model.L2Spawn;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2GourdInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;

public class JackpotSeed implements IItemHandler
{
	private L2GourdInstance _gourd = null;

	private static int[] _itemIds =
	{
	6389, 6390
	};

	private static int[] _npcIds =
	{
	12774, 12777
	};

	@Override
	public void useItem(L2Playable playable, L2ItemInstance item)
	{
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2NpcTemplate template1 = null;
		int itemId = item.getItemId();
		for (int i = 0; i < _itemIds.length; i++)
		{
			if (_itemIds[i] == itemId)
			{
				template1 = NpcData.getInstance().getTemplate(_npcIds[i]);
				break;
			}
		}

		if (template1 == null)
		{
			return;
		}

		try
		{
			L2Spawn spawn = new L2Spawn(template1);
			spawn.setId(IdFactory.getInstance().getNextId());
			spawn.setLocx(activeChar.getX());
			spawn.setLocy(activeChar.getY());
			spawn.setLocz(activeChar.getZ());
			_gourd = (L2GourdInstance) spawn.spawnOne();
			L2World.getInstance().storeObject(_gourd);
			_gourd.setOwner(activeChar.getName());
			activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2).addString("Created " + template1.name + " at x: " + spawn.getLocx() + " y: " + spawn.getLocy() + " z: " + spawn.getLocz()));
		}
		catch (Exception e)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2).addString("Target is not ingame."));
		}
		activeChar = null;
		template1 = null;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}

}