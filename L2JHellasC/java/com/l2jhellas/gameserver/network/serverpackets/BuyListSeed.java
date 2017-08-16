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
package com.l2jhellas.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2TradeList;

/**
 * Format: c ddh[hdddhhd]<BR>
 * c - id (0xE8)<BR>
 * <BR>
 * d - money<BR>
 * d - manor id<BR>
 * h - size<BR>
 * [<BR>
 * h - item type 1<BR>
 * d - object id<BR>
 * d - item id<BR>
 * d - count<BR>
 * h - item type 2<BR>
 * h<BR>
 * d - price<BR>
 * ]<BR>
 * 
 * @author l3x
 */
public final class BuyListSeed extends L2GameServerPacket
{
	private static final String _S__E8_BUYLISTSEED = "[S] E8 BuyListSeed";

	private final int _manorId;
	private List<L2ItemInstance> _list = new ArrayList<L2ItemInstance>();
	private final int _money;

	public BuyListSeed(L2TradeList list, int manorId, int currentMoney)
	{
		_money = currentMoney;
		_manorId = manorId;
		_list = list.getItems();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xE8);

		writeD(_money);                   // current money
		writeD(_manorId);                 // manor id

		writeH(_list.size());             // list length

		for (L2ItemInstance item : _list)
		{
			writeH(0x04);                 // item->type1
			writeD(0x00);                 // objectId
			writeD(item.getItemId());     // item id
			writeD(item.getCount());      // item count
			writeH(0x04);                 // item->type2
			writeH(0x00);                 // unknown :)
			writeD(item.getPriceToSell());// price
		}
	}

	@Override
	public String getType()
	{
		return _S__E8_BUYLISTSEED;
	}
}