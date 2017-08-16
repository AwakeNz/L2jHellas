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
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.TradeList;
import com.l2jhellas.gameserver.model.TradeList.TradeItem;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.TradeOtherAdd;
import com.l2jhellas.gameserver.network.serverpackets.TradeOwnAdd;
import com.l2jhellas.gameserver.network.serverpackets.TradeUpdateItems;

public final class AddTradeItem extends L2GameClientPacket
{
	private static final String _C__16_ADDTRADEITEM = "[C] 16 AddTradeItem";

	protected int _tradeId;
	private int _objectId;
	private int _count;
	
	public AddTradeItem()
	{
	}

	@Override
	protected void readImpl()
	{
		_tradeId = readD();
		_objectId = readD();
		_count = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		TradeList trade = player.getActiveTradeList();
		
		if (trade == null)
			return;
	
		if ((trade.getPartner() == null) || (L2World.getInstance().findObject(trade.getPartner().getObjectId()) == null))
		{
			player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			player.cancelActiveTrade();
			return;
		}

		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			player.cancelActiveTrade();
			return;
		}

		if (trade.isConfirmed() || trade.getPartner().getActiveTradeList().isConfirmed())
		{
			player.sendPacket(SystemMessageId.CANNOT_ADJUST_ITEMS_AFTER_TRADE_CONFIRMED);
			return;
		}
		
		if (!player.validateItemManipulation(_objectId, "trade"))
		{
			player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}

		final L2ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		
		if (item == null || _count <= 0)
		{
			player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
			return;
		}
		
		if (item.getLocation() != L2ItemInstance.ItemLocation.INVENTORY)
		{
			player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}
		
        if (_count > item.getCount())
            _count = item.getCount();
        

		final TradeItem tradeitem = trade.addItem(item.getObjectId(), _count);
			
		if (tradeitem != null)
		{	       
			player.sendPacket(new TradeOwnAdd(tradeitem));
			player.sendPacket(new TradeUpdateItems(tradeitem,item.getCount()-tradeitem.getCount()));
			trade.getPartner().sendPacket(new TradeOtherAdd(tradeitem));
		}
	}

	@Override
	public String getType()
	{
		return _C__16_ADDTRADEITEM;
	}
}