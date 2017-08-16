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
import com.l2jhellas.gameserver.datatables.xml.AdminData;
import com.l2jhellas.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jhellas.gameserver.network.serverpackets.ItemList;
import com.l2jhellas.gameserver.templates.L2EtcItemType;
import com.l2jhellas.gameserver.templates.L2Item;
import com.l2jhellas.logs.GMAudit;
import com.l2jhellas.util.IllegalPlayerAction;
import com.l2jhellas.util.Util;

public final class RequestDropItem extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestDropItem.class.getName());
	private static final String _C__12_REQUESTDROPITEM = "[C] 12 RequestDropItem";

	private int _objectId;
	private int _count;
	private int _x;
	private int _y;
	private int _z;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readD();
		_x = readD();
		_y = readD();
		_z = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if ((activeChar == null) || activeChar.isDead())
			return;

		// Flood protect drop to avoid packet lag
		if (!activeChar.getAntiFlood().getDropItem().tryPerformAction("drop item"))
			return;

		L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);

		if ((item == null) || (_count == 0) || !activeChar.validateItemManipulation(_objectId, "drop") || (!Config.ALLOW_DISCARDITEM && !activeChar.isGM()) || !item.isDropable())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		if (item.getItemType() == L2EtcItemType.QUEST)
			return;
		int itemId = item.getItemId();

		// Cursed Weapons cannot be dropped
		if (CursedWeaponsManager.getInstance().isCursed(itemId))
			return;

		if (_count > item.getCount())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
			return;
		}

		if (activeChar.getActiveEnchantItem() != null || activeChar.getActiveWarehouse() != null || activeChar.getActiveTradeList()!= null)
		{
			activeChar.sendMessage("You can't drop items when you are enchanting, got active warehouse or active trade.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (_count <= 0)
		{
			Util.handleIllegalPlayerAction(activeChar, "[RequestDropItem] count <= 0! ban! oid: " + _objectId + " owner: " + activeChar.getName(), IllegalPlayerAction.PUNISH_KICK);
			return;
		}

		if (!item.isStackable() && (_count > 1))
		{
			Util.handleIllegalPlayerAction(activeChar, "[RequestDropItem] count > 1 but item is not stackable! ban! oid: " + _objectId + " owner: " + activeChar.getName(), IllegalPlayerAction.PUNISH_KICK);
			return;
		}

		if (!activeChar.getAccessLevel().allowTransaction())
		{
			activeChar.sendMessage("Transactions are disabled for your Access Level.");
			activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}

		if (activeChar.isProcessingTransaction() || (activeChar.getPrivateStoreType() != 0))
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}
		if (activeChar.isFishing())
		{
			// You can't mount, dismount, break and drop items while fishing
			activeChar.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_2);
			return;
		}
		
		if (activeChar.isFlying())
		{
			return;
		}

		// Cannot discard item that the skill is consumming
		if (activeChar.isCastingNow())
		{
			if (activeChar.getCurrentSkill() != null && activeChar.getCurrentSkill().getSkill().getItemConsumeId() == item.getItemId())
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
				return;
			}
		}

		if ((L2Item.TYPE2_QUEST == item.getItem().getType2()) && !activeChar.isGM())
		{
			if (Config.DEBUG)
			{
				_log.finest(activeChar.getObjectId() + ":player tried to drop quest item");
			}
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_EXCHANGE_ITEM);
			return;
		}

		if (!activeChar.isInsideRadius(_x, _y, 150, false) || (Math.abs(_z - activeChar.getZ()) > 50))
		{
			if (Config.DEBUG)
			{
				_log.finest(activeChar.getObjectId() + ": trying to drop too far away");
			}
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_DISTANCE_TOO_FAR);
			return;
		}

		if (Config.DEBUG)
		{
			_log.fine("requested drop item " + _objectId + "(" + item.getCount() + ") at " + _x + "/" + _y + "/" + _z);
		}

		if (item.isEquipped())
		{
			L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(item.getLocationSlot());
			InventoryUpdate iu = new InventoryUpdate();
			for (L2ItemInstance itm : unequiped)
			{
				activeChar.checkSSMatch(null, itm);
				iu.addModifiedItem(itm);
			}
			
			activeChar.sendPacket(iu);
			activeChar.broadcastUserInfo();

			ItemList il = new ItemList(activeChar, true);
			activeChar.sendPacket(il);			
		}

		L2ItemInstance dropedItem = activeChar.dropItem("Drop", _objectId, _count, _x, _y, _z, null, false);

		if (Config.DEBUG)
		{
			_log.fine("dropping " + _objectId + " item(" + _count + ") at: " + _x + " " + _y + " " + _z);
		}

		// activeChar.broadcastUserInfo();

		if (activeChar.isGM())
		{
			String target = (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target");
			GMAudit.auditGMAction(activeChar.getName(), "drop", target, dropedItem.getItemId() + " - " + dropedItem.getItemName() + " - " + dropedItem.getObjectId());
		}

		if ((dropedItem != null) && (dropedItem.getItemId() == 57) && (dropedItem.getCount() >= 1000000))
		{
			String msg = "Character (" + activeChar.getName() + ") has dropped (" + dropedItem.getCount() + ")adena at (" + _x + "," + _y + "," + _z + ")";
			_log.warning(RequestDropItem.class.getName() + ": "+msg);
			AdminData.getInstance().broadcastMessageToGMs(msg);
		}
	}

	@Override
	public String getType()
	{
		return _C__12_REQUESTDROPITEM;
	}
}