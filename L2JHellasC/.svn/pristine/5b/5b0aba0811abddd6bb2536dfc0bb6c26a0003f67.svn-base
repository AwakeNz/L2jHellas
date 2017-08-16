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
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.PrivateStoreManageListSell;
import com.l2jhellas.util.Util;

public final class RequestPrivateStoreManageSell extends L2GameClientPacket
{
	private static final String _C__73_REQUESTPRIVATESTOREMANAGESELL = "[C] 73 RequestPrivateStoreManageSell";

	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		// Fix for privatestore exploit during login
		if (!player.isVisible())
		{
			Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " try exploit at login with privatestore!", Config.DEFAULT_PUNISH);
			_log.warning(RequestPrivateStoreManageSell.class.getName() + ": Player " + player.getName() + " try exploit at login with privatestore!");
			return;
		}

		// If player is in store mode /offline_shop like L2OFF
		if (player.isStored())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
		if (player.isAlikeDead())
		{
			player.sendMessage("You are dead. you can't use it right now!");
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (player.isInOlympiadMode())
		{
			player.sendMessage("You are in olympiade mode. you can't use it right now!");
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if(player.getActiveTradeList()!=null || player.getActiveWarehouse()!=null || player.getActiveEnchantItem()!=null)
		{
			player.sendMessage("Trade-wh-enchant not allowded while managing private store.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// You can't open store when the task is lunched
		if (player.isSittingTaskLunched())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		// Like L2OFF - You can't open buy/sell when you are sitting
		if (player.isSitting() && player.getPrivateStoreType() == 0)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (player.isSitting() && player.getPrivateStoreType() != 0)
		{
			player.standUp();
		}

		if (player.getMountType() != 0)
			return;

		/** @formatter:off */
		if ((player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_SELL)
				|| (player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_SELL + 1)
				|| (player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_PACKAGE_SELL))
			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
		/** @formatter:on */
		if (player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_NONE)
		{
			if (player.isSitting())
				player.standUp();
			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_SELL + 1);
			player.sendPacket(new PrivateStoreManageListSell(player));
		}
	}

	@Override
	public String getType()
	{
		return _C__73_REQUESTPRIVATESTOREMANAGESELL;
	}
}