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

import com.l2jhellas.gameserver.cache.HtmCache;
import com.l2jhellas.gameserver.handler.IItemHandler;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jhellas.gameserver.network.serverpackets.RadarControl;
import com.l2jhellas.gameserver.network.serverpackets.ShowMiniMap;

public class Book implements IItemHandler
{
	private static final int[] ITEM_IDS =
	{/** @formatter:off */
	5588, 6317, 7561, 7064, 7082,
	7083, 7084, 7085, 7086, 7087,
	7088, 7089, 7090, 7091, 7092,
	7093, 7094, 7095, 7096, 7097,
	7098, 7099, 7100, 7101, 7102,
	7103, 7104, 7105, 7106, 7107,
	7108, 7109, 7110, 7111, 7112
	};/** @formatter:on */

	@Override
	public void useItem(L2Playable playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		L2PcInstance activeChar = (L2PcInstance) playable;
		final int itemId = item.getItemId();

		String filename = "data/html/help/" + itemId + ".htm";
		String content = HtmCache.getInstance().getHtm(filename);

		// Quest item: Lidia's diary
		if (itemId == 7064)
		{
			activeChar.sendPacket(new ShowMiniMap(1665));
			activeChar.sendPacket(new RadarControl(0, 1, 51995, -51265, -3104));
		}

		NpcHtmlMessage itemReply = new NpcHtmlMessage(5);
		itemReply.setHtml(content);
		activeChar.sendPacket(itemReply);

		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}