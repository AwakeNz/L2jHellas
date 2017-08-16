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
package com.l2jhellas.gameserver.network.clientpackets;

import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.PackageSendableList;

/**
 * Format: (c)d<BR>
 * d: char object id (?)
 * 
 * @author -Wooden-
 */
public final class RequestPackageSendableItemList extends L2GameClientPacket
{
	private static final String _C_9E_REQUESTPACKAGESENDABLEITEMLIST = "[C] 9E RequestPackageSendableItemList";
	private int _objectID;

	@Override
	protected void readImpl()
	{
		_objectID = readD();
	}

	@Override
	public void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();

		if(player == null)
			return;

		if (!player.getAntiFlood().getTransaction().tryPerformAction("deposit"))
		{
			player.sendMessage("You depositing items too fast.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if(player.getObjectId() == _objectID)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
	
		L2ItemInstance[] items = getClient().getActiveChar().getInventory().getAvailableItems(true);
		// build list...
		sendPacket(new PackageSendableList(items, _objectID));
	}

	@Override
	public String getType()
	{
		return _C_9E_REQUESTPACKAGESENDABLEITEMLIST;
	}
}