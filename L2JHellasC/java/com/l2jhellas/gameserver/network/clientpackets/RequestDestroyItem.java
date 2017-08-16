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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.datatables.xml.PetData;
import com.l2jhellas.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jhellas.gameserver.network.serverpackets.ItemList;
import com.l2jhellas.gameserver.network.serverpackets.StatusUpdate;
import com.l2jhellas.util.Util;
import com.l2jhellas.util.database.L2DatabaseFactory;

public final class RequestDestroyItem extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestDestroyItem.class.getName());
	private static final String _C__59_REQUESTDESTROYITEM = "[C] 59 RequestDestroyItem";

	private int _objectId;
	private int _count;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null)
			return;

		if (_count <= 0)
		{
			if (_count < 0)
				Util.handleIllegalPlayerAction(activeChar, "[RequestDestroyItem] count < 0! ban! oId: " + _objectId + " owner: " + activeChar.getName(), Config.DEFAULT_PUNISH);
			return;
		}

		int count = _count;

		if (!activeChar.getAntiFlood().getTransaction().tryPerformAction("destroy"))
		{
			activeChar.sendMessage("You destroying items too fast.");
			return;
		}
		if (activeChar.getActiveEnchantItem() != null || activeChar.getActiveWarehouse() != null || activeChar.getActiveTradeList()!= null)
		{
			activeChar.sendMessage("You can't destroy items when you are enchanting, got active warehouse or active trade.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		if (activeChar.getPrivateStoreType() != 0)
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}

		L2ItemInstance itemToRemove = activeChar.getInventory().getItemByObjectId(_objectId);
		// if we can't find requested item, its actually a cheat!
		if (itemToRemove == null)
			return;

		// Cannot discard item that the skill is consuming
		if (activeChar.isCastingNow())
		{
			if ((activeChar.getCurrentSkill() != null) && activeChar.getCurrentSkill().getSkill().getItemConsumeId() == itemToRemove.getItemId())
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
				return;
			}
		}

		int itemId = itemToRemove.getItemId();
		if (itemToRemove == null || itemToRemove.isWear() || !itemToRemove.isDestroyable() || CursedWeaponsManager.getInstance().isCursed(itemId))
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
			return;
		}

		if (!itemToRemove.isStackable() && count > 1)
		{
			Util.handleIllegalPlayerAction(activeChar, "[RequestDestroyItem] count > 1 but item is not stackable! oid: " + _objectId + " owner: " + activeChar.getName(), Config.DEFAULT_PUNISH);
			return;
		}

		if (_count > itemToRemove.getCount())
			count = itemToRemove.getCount();

		if (itemToRemove.isEquipped())
		{
			L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getEquipSlot());
			InventoryUpdate iu = new InventoryUpdate();
			for (int i = 0; i < unequiped.length; i++)
			{
				activeChar.checkSSMatch(null, unequiped[i]);

				iu.addModifiedItem(unequiped[i]);
			}
			activeChar.sendPacket(iu);
			activeChar.broadcastUserInfo();
		}

		if (PetData.isPetItem(itemId))
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				if (activeChar.getPet() != null && activeChar.getPet().getControlItemId() == _objectId)
				{
					activeChar.getPet().unSummon(activeChar);
				}

				// if it's a pet control item, delete the pet
				PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
				statement.setInt(1, _objectId);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warning(RequestDestroyItem.class.getSimpleName() + ": could not delete pet objectid: ");
			}
		}

		L2ItemInstance removedItem = activeChar.getInventory().destroyItem("Destroy", _objectId, count, activeChar, null);

		if (removedItem == null)
			return;

		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			InventoryUpdate iu = new InventoryUpdate();
			if (removedItem.getCount() == 0)
				iu.addRemovedItem(removedItem);
			else
				iu.addModifiedItem(removedItem);

			// client.getConnection().sendPacket(iu);
			activeChar.sendPacket(iu);
		}
		else
			sendPacket(new ItemList(activeChar, true));

		StatusUpdate su = new StatusUpdate(activeChar.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
		activeChar.sendPacket(su);

		//L2World world = L2World.getInstance();
		//world.removeObject(removedItem);
	}

	@Override
	public String getType()
	{
		return _C__59_REQUESTDESTROYITEM;
	}
}