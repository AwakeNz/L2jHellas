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
package com.l2jhellas.gameserver.handlers.admincommandhandlers;

import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.Inventory;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.CharInfo;
import com.l2jhellas.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jhellas.gameserver.network.serverpackets.UserInfo;
import com.l2jhellas.logs.GMAudit;
import com.l2jhellas.util.IllegalPlayerAction;
import com.l2jhellas.util.Util;

/**
 * This class handles following admin commands:
 * - enchant_armor
 */
public class AdminEnchant implements IAdminCommandHandler
{
	protected static final Logger _log = Logger.getLogger(AdminEnchant.class.getName());

	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_seteh",//6
		"admin_setec",//10
		"admin_seteg",//9
		"admin_setel",//11
		"admin_seteb",//12
		"admin_setew",//7
		"admin_setes",//8
		"admin_setle",//1
		"admin_setre",//2
		"admin_setlf",//4
		"admin_setrf",//5
		"admin_seten",//3
		"admin_setun",//0
		"admin_setba",//13
		"admin_enchant"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_enchant"))
		{
			showMainPage(activeChar);
		}
		else
		{
			int armorType = -1;

			if (command.startsWith("admin_seteh"))
			{
				armorType = Inventory.PAPERDOLL_HEAD;
			}
			else if (command.startsWith("admin_setec"))
			{
				armorType = Inventory.PAPERDOLL_CHEST;
			}
			else if (command.startsWith("admin_seteg"))
			{
				armorType = Inventory.PAPERDOLL_GLOVES;
			}
			else if (command.startsWith("admin_seteb"))
			{
				armorType = Inventory.PAPERDOLL_FEET;
			}
			else if (command.startsWith("admin_setel"))
			{
				armorType = Inventory.PAPERDOLL_LEGS;
			}
			else if (command.startsWith("admin_setew"))
			{
				armorType = Inventory.PAPERDOLL_RHAND;
			}
			else if (command.startsWith("admin_setes"))
			{
				armorType = Inventory.PAPERDOLL_LHAND;
			}
			else if (command.startsWith("admin_setle"))
			{
				armorType = Inventory.PAPERDOLL_LEAR;
			}
			else if (command.startsWith("admin_setre"))
			{
				armorType = Inventory.PAPERDOLL_REAR;
			}
			else if (command.startsWith("admin_setlf"))
			{
				armorType = Inventory.PAPERDOLL_LFINGER;
			}
			else if (command.startsWith("admin_setrf"))
			{
				armorType = Inventory.PAPERDOLL_RFINGER;
			}
			else if (command.startsWith("admin_seten"))
			{
				armorType = Inventory.PAPERDOLL_NECK;
			}
			else if (command.startsWith("admin_setun"))
			{
				armorType = Inventory.PAPERDOLL_UNDER;
			}
			else if (command.startsWith("admin_setba"))
			{
				armorType = Inventory.PAPERDOLL_BACK;
			}

			if (armorType == Inventory.PAPERDOLL_NULL)
			{
				activeChar.sendMessage("Your target has no item equipted in your selected slot.");
				armorType = -1;
			}

			if ((armorType != -1))
			{
				try
				{
					int ench = Integer.parseInt(command.substring(12));

					// check value
					if (ench < 0 || ench > 65535)
					{
						activeChar.sendMessage("You must set the enchant level to be between 0-65535.");
					}
					else
					{
						L2Object target = activeChar.getTarget();
						L2PcInstance player = (L2PcInstance) target;
						if ((ench > Config.GM_OVER_ENCHANT) && (Config.GM_OVER_ENCHANT != 0) && (player != null) && !player.isGM())
						{
							player.sendMessage("A GM tried to overenchant you. You will both be banned.");
							Util.handleIllegalPlayerAction(player, "The player " + player.getName() + " has been edited. BAN!", IllegalPlayerAction.PUNISH_KICKBAN);
							activeChar.sendMessage("You tried to overenchant somebody. You will both be banned.");
							Util.handleIllegalPlayerAction(activeChar, "The GM " + activeChar.getName() + " has overenchanted the player " + player.getName() + ". BAN!", IllegalPlayerAction.PUNISH_KICKBAN);;
						}
						else
						{
							setEnchant(activeChar, ench, armorType);
						}
					}
				}
				catch (StringIndexOutOfBoundsException e)
				{
					_log.warning(AdminEnchant.class.getName() + ": Set enchant error: ");
					if (Config.DEVELOPER)
						e.printStackTrace();
					activeChar.sendMessage("Please specify a new enchant value.");
				}
				catch (NumberFormatException e)
				{
					_log.warning(AdminEnchant.class.getName() + ": Set enchant error: ");
					if (Config.DEVELOPER)
						e.printStackTrace();
					activeChar.sendMessage("Please specify a valid new enchant value.");
				}
			}
			// show the enchant menu after an action
			showMainPage(activeChar);
		}
		return true;
	}

	private void setEnchant(L2PcInstance activeChar, int ench, int armorType)
	{
		// get the target
		L2Object target = activeChar.getTarget();
		if (target == null)
		{
			target = activeChar;
		}
		L2PcInstance player = null;

		if (target instanceof L2PcInstance)
		{
			player = (L2PcInstance) target;
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}

		// now we need to find the equipped weapon of the targeted character...
		int curEnchant = 0; // display purposes only
		L2ItemInstance itemInstance = null;

		// only attempt to enchant if there is a weapon equipped
		L2ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
		if (parmorInstance != null && parmorInstance.getEquipSlot() == armorType)
		{
			itemInstance = parmorInstance;
		}
		else
		{
			// for bows and double handed weapons
			parmorInstance = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LRHAND);
			if (parmorInstance != null && parmorInstance.getEquipSlot() == Inventory.PAPERDOLL_LRHAND)
			{
				itemInstance = parmorInstance;
			}
		}

		// set enchant value
		if (itemInstance == null)
		{
			activeChar.sendMessage("Selected slot is empty in your targets inventory.");
			return;
		}
		player.getInventory().unEquipItemInSlotAndRecord(armorType);
		curEnchant = itemInstance.getEnchantLevel();
		itemInstance.setEnchantLevel(ench);
		player.getInventory().equipItemAndRecord(itemInstance);

		// send packets
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(itemInstance);
		player.sendPacket(iu);
		player.broadcastPacket(new CharInfo(player));
		player.sendPacket(new UserInfo(player));

		// informations
		activeChar.sendMessage("Changed enchantment of " + player.getName() + "'s " + itemInstance.getItem().getItemName() + " from " + curEnchant + " to " + ench + ".");
		player.sendMessage("Admin has changed the enchantment of your " + itemInstance.getItem().getItemName() + " from " + curEnchant + " to " + ench + ".");

		// log
		GMAudit.auditGMAction(activeChar.getName(), "enchant", player.getName(), itemInstance.getItem().getItemName() + " from " + curEnchant + " to " + ench);
	}

	private void showMainPage(L2PcInstance activeChar)
	{
		AdminHelpPage.showHelpPage(activeChar, "enchant.htm");
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}