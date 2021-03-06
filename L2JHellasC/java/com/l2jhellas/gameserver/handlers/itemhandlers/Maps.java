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
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.RadarControl;
import com.l2jhellas.gameserver.network.serverpackets.ShowMiniMap;

/**
 * This class provides handling for items that should display a map
 * when double clicked.
 */
public class Maps implements IItemHandler
{
	// all the items ids that this handler knowns
	private static final int[] ITEM_IDS =
	{
	1665, 1863, 7063
	};

	@Override
	public void useItem(L2Playable playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		final L2PcInstance activeChar = (L2PcInstance) playable;
		final int itemId = item.getItemId();
		if (itemId == 7063)
		{
			activeChar.sendPacket(new ShowMiniMap(1665));
			activeChar.sendPacket(new RadarControl(0, 1, 51995, -51265, -3104));
		}
		else
			activeChar.sendPacket(new ShowMiniMap(itemId));
		return;
	}

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}