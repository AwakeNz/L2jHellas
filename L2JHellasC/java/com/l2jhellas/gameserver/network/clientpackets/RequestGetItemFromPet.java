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

import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PetInstance;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.util.IllegalPlayerAction;
import com.l2jhellas.util.Util;

public final class RequestGetItemFromPet extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestGetItemFromPet.class.getName());
	private static final String REQUESTGETITEMFROMPET__C__8C = "[C] 8C RequestGetItemFromPet";

	private int _objectId;
	private int _amount;
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_amount = readD();
		_unknown = readD();// = 0 for most trades
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if ((player == null) || (player.getPet() == null) || !(player.getPet() instanceof L2PetInstance))
			return;
		final L2PetInstance pet = (L2PetInstance) player.getPet();

		if (player.getActiveEnchantItem() != null)
		{
			Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " Tried To Use Enchant Exploit , And Got Banned!", IllegalPlayerAction.PUNISH_KICKBAN);
			return;
		}
		
		if (player.getActiveWarehouse() != null || player.getActiveTradeList() != null)
		{
			player.sendMessage("You can't get items when you got active warehouse or active trade.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (_amount <= 0)
		{
			Util.handleIllegalPlayerAction(player, "[RequestGetItemFromPet] count < 0! ban! oid: " + _objectId + " owner: " + player.getName(), Config.DEFAULT_PUNISH);
			return;
		}

		if (!player.getAntiFlood().getTransaction().tryPerformAction("getfrompet"))
		{
			player.sendMessage("You get items from pet too fast.");
			return;
		}

		if (pet.transferItem("Transfer", _objectId, _amount, player.getInventory(), player, pet) == null)
		{
			_log.warning(RequestGetItemFromPet.class.getName() + ": Invalid item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
		}
	}

	@Override
	public String getType()
	{
		return REQUESTGETITEMFROMPET__C__8C;
	}
}