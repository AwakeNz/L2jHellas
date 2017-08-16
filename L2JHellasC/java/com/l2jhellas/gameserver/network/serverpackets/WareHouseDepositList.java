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

import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.templates.L2Item;

public class WareHouseDepositList extends L2GameServerPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 2;
	public static final int CASTLE = 3; //not sure
	public static final int FREIGHT = 4; //not sure
	private static final String _S__53_WAREHOUSEDEPOSITLIST = "[S] 41 WareHouseDepositList";
	private final L2PcInstance _activeChar;
	private final int _playerAdena;
	private final ArrayList<L2ItemInstance> _items;
	private final int _whType;

	public WareHouseDepositList(L2PcInstance player, int type)
	{
		_activeChar = player;
		_whType = type;
		_playerAdena = _activeChar.getAdena();
		_items = new ArrayList<L2ItemInstance>();

		if(_activeChar.getActiveTradeList()!=null || _activeChar.getActiveEnchantItem() != null)
		{
			_activeChar.sendMessage("You can't use wh while active trade or enchant.");
			_activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		for (L2ItemInstance temp : _activeChar.getInventory().getAvailableItems(true))
			_items.add(temp);

		// augmented and shadow items can be stored in private wh
		if (_whType == PRIVATE)
		{
			for (L2ItemInstance temp : player.getInventory().getItems())
			{
				if (temp != null && !temp.isEquipped() && (temp.isShadowItem() || temp.isAugmented()))
					_items.add(temp);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
	writeC(0x41);
	writeH(_whType);
	writeD(_playerAdena);
	writeH(_items.size());
	
	for (L2ItemInstance temp : _items)
	{
		if (temp == null || temp.getItem() == null)
			continue;
		
		final L2Item item = temp.getItem();
		
		writeH(item.getType1());
		writeD(temp.getObjectId());
		writeD(temp.getItemId());
		writeD(temp.getCount());
		writeH(item.getType2());
		writeH(temp.getCustomType1());
		writeD(item.getBodyPart());
		writeH(temp.getEnchantLevel());
		writeH(temp.getCustomType2());
		writeH(0x00);
		writeD(temp.getObjectId());
		if (temp.isAugmented())
		{
			writeD(0x0000FFFF & temp.getAugmentation().getAugmentationId());
			writeD(temp.getAugmentation().getAugmentationId() >> 16);
		}
		else
			writeQ(0x00);
	}
	_items.clear();
	}

	@Override
	public String getType()
	{
		return _S__53_WAREHOUSEDEPOSITLIST;
	}
}