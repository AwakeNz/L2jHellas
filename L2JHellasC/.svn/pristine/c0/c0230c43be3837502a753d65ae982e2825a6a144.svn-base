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

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.ItemRequest;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.TradeList;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.util.Util;

public final class RequestPrivateStoreSell extends L2GameClientPacket
{
	// private static final String _C__96_SENDPRIVATESTOREBUYBUYLIST = "[C] 96 SendPrivateStoreBuyBuyList";
	private static final String _C__96_REQUESTPRIVATESTORESELL = "[C] 96 RequestPrivateStoreSell";

	private int _storePlayerId;
	private int _count;
	private int _price;
	private ItemRequest[] _items;

	@Override
	protected void readImpl()
	{
		_storePlayerId = readD();
		_count = readD();
		// count*20 is the size of a for iteration of each item
		if (_count < 0 || _count * 20 > _buf.remaining() || _count > Config.MAX_ITEM_IN_PACKET)
			_count = 0;
		_items = new ItemRequest[_count];

		long priceTotal = 0;
		for (int i = 0; i < _count; i++)
		{
			int objectId = readD();
			int itemId = readD();
			readH(); // TODO analyse this
			readH(); // TODO analyse this
			long count = readD();
			int price = readD();

			if (count > Integer.MAX_VALUE || count < 0)
			{
				String msgErr = "[RequestPrivateStoreSell] player " + getClient().getActiveChar().getName() + " tried an overflow exploit, ban this player!";
				Util.handleIllegalPlayerAction(getClient().getActiveChar(), msgErr, Config.DEFAULT_PUNISH);
				_count = 0;
				_items = null;
				return;
			}
			_items[i] = new ItemRequest(objectId, itemId, (int) count, price);
			priceTotal += price * count;
		}

		if (priceTotal < 0 || priceTotal > Integer.MAX_VALUE)
		{
			String msgErr = "[RequestPrivateStoreSell] player " + getClient().getActiveChar().getName() + " tried an overflow exploit, ban this player!";
			Util.handleIllegalPlayerAction(getClient().getActiveChar(), msgErr, Config.DEFAULT_PUNISH);
			_count = 0;
			_items = null;
			return;
		}

		_price = (int) priceTotal;
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (!player.getAntiFlood().getTransaction().tryPerformAction("privatestoresell"))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendMessage("You selling items too fast");
			return;
		}
		L2Object object = L2World.getInstance().findObject(_storePlayerId);
		if (object == null || !(object instanceof L2PcInstance))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		L2PcInstance storePlayer = (L2PcInstance) object;
		if (storePlayer.getPrivateStoreType() != L2PcInstance.STORE_PRIVATE_BUY)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		TradeList storeList = storePlayer.getBuyList();
		if (storeList == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
       
		// Check if player didn't choose any items
        if (_items == null || _items.length == 0)
        {
        	player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

		if(player.getActiveTradeList()!=null || player.getActiveEnchantItem()!=null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendMessage("You can't use this action right now!");
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendMessage("Transactions are disable for your Access Level.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (storePlayer.getAdena() < _price)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			storePlayer.sendMessage("You have not enough adena, canceling PrivateBuy.");
			storePlayer.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
			storePlayer.broadcastUserInfo();
			return;
		}

		if (!storeList.PrivateStoreSell(player, _items, _price))
		{
			 player.sendPacket(ActionFailed.STATIC_PACKET);
			_log.warning(RequestPrivateStoreSell.class.getName() + ": PrivateStore sell has failed due to invalid list or request. Player: " + player.getName() + ", Private store of: " + storePlayer.getName());
			return;
		}

		if (storeList.getItemCount() == 0)
		{
			storePlayer.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
			storePlayer.broadcastUserInfo();
		}
	}

	@Override
	public String getType()
	{
		return _C__96_REQUESTPRIVATESTORESELL;
	}
}