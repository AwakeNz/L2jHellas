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
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.templates.L2Item;

public class RequestUnEquipItem extends L2GameClientPacket
{
	private static final String _C__11_REQUESTUNEQUIPITEM = "[C] 11 RequestUnequipItem";

	// cd
	private int _slot;

	/**
	 * packet type id 0x11
	 * format: cd
	 * 
	 * @param decrypt
	 */
	@Override
	protected void readImpl()
	{
		_slot = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null)
			return;

		if (activeChar._haveFlagCTF)
		{
			activeChar.sendMessage("You can't unequip a CTF flag.");
			return;
		}

		L2ItemInstance item = activeChar.getInventory().getPaperdollItemByL2ItemId(_slot);
		if ((item != null) && item.isWear())
		{
			// Wear-items are not to be unequipped
			return;
		}
		// Prevent of unequiping a cursed weapon
		if (_slot == L2Item.SLOT_LR_HAND && activeChar.isCursedWeaponEquiped())
		{
			// Message ?
			return;
		}

		// Prevent player from unequipping items in special conditions
		if (activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAlikeDead())
		{
			activeChar.sendMessage("Your status does not allow you to do that.");
			return;
		}
		if (activeChar.isAttackingNow() || activeChar.isCastingNow())
			return;

		// Remove augmentation boni
		if ((item != null) && item.isAugmented())
		{
			item.getAugmentation().removeBoni(activeChar);
		}

		L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInBodySlotAndRecord(item);

		// show the update in the inventory
		InventoryUpdate iu = new InventoryUpdate();

		for (int i = 0; i < unequiped.length; i++)
		{
			activeChar.checkSSMatch(null, unequiped[i]);

			iu.addModifiedItem(unequiped[i]);
		}

		activeChar.sendPacket(iu);

		activeChar.abortAttack();
		activeChar.broadcastUserInfo();

		// this can be 0 if the user pressed the right mouse button twice very fast
		if (unequiped.length > 0)
		{

			SystemMessage sm = null;
			if (unequiped[0].getEnchantLevel() > 0)
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
				sm.addNumber(unequiped[0].getEnchantLevel());
				sm.addItemName(unequiped[0].getItemId());
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED);
				sm.addItemName(unequiped[0].getItemId());
			}
			activeChar.sendPacket(sm);
			sm = null;
		}
	}

	@Override
	public String getType()
	{
		return _C__11_REQUESTUNEQUIPITEM;
	}
}