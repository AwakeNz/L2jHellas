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

import com.l2jhellas.gameserver.model.TradeList.TradeItem;

/**
 * @author AbsolutePower
 */
public class TradeUpdateItems extends L2GameServerPacket
{
	private TradeItem _item;
	private int _count;
	
	public TradeUpdateItems(TradeItem item,int count)
	{
		_item = item;
		_count = count;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x74);
		writeH(1);

		writeH((_count > 0 && _item.getItem().isStackable()) ? 3 : 2);
		writeH(_item.getItem().getType1());
		writeD(_item.getObjectId());
		writeD(_item.getItem().getItemId());
		writeD(_count);
		writeH(_item.getItem().getType2());
		writeD(_item.getItem().getBodyPart());
		writeH(_item.getEnchant());
	}
}