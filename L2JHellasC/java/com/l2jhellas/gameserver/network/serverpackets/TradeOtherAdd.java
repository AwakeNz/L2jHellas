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
 * @author Yme
 */
public class TradeOtherAdd extends L2GameServerPacket
{
	private static final String _S__31_TRADEOTHERADD = "[S] 21 TradeOtherAdd";
	private final TradeItem _item;

	public TradeOtherAdd(TradeItem item)
	{
		_item = item;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x21);
		
		writeH(1); // item count
		
		writeH(_item.getItem().getType1()); // item type1
		writeD(_item.getObjectId());
		writeD(_item.getItem().getItemId());
		writeD(_item.getCount());
		writeH(_item.getItem().getType2()); // item type2
		writeH(0x00); 
		
		writeD(_item.getItem().getBodyPart()); //slot
		writeH(_item.getEnchant()); // enchant level
		writeH(0x00);
		writeH(0x00);
	}

	@Override
	public String getType()
	{
		return _S__31_TRADEOTHERADD;
	}
}