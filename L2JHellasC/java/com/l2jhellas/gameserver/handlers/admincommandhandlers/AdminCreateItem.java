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

import java.util.StringTokenizer;

import com.l2jhellas.gameserver.datatables.sql.ItemTable;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.ItemList;
import com.l2jhellas.gameserver.templates.L2Item;
import com.l2jhellas.logs.GMAudit;

/**
 * This class handles following admin commands:
 * - itemcreate = show menu
 * - create_item <id> [num] = creates num items with respective id, if num is
 * not specified, assumes 1.
 */
public class AdminCreateItem implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_itemcreate",
		"admin_create_item"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");

		if (command.equals("admin_itemcreate"))
		{
			AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_create_item"))
		{
			try
			{
				String val = command.substring(17);
				StringTokenizer st = new StringTokenizer(val);
				L2PcInstance target;
				if ((L2PcInstance) activeChar.getTarget() instanceof L2PcInstance && (L2PcInstance) activeChar.getTarget() != null)
				{
					target = (L2PcInstance) activeChar.getTarget();
				}
				else
					target = activeChar;
				
				if (st.countTokens() == 2)
				{
					String id = st.nextToken();
					int idval = Integer.parseInt(id);
					String num = st.nextToken();
					int numval = Integer.parseInt(num);
					createItem(target, idval, numval);
				}
				else if (st.countTokens() == 1)
				{
					String id = st.nextToken();
					int idval = Integer.parseInt(id);
					createItem(target, idval, 1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //itemcreate <itemId> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("Specify a valid number.");
			}
			AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private void createItem(L2PcInstance activeChar, int id, int num)
	{
		if (num > 20)
		{
			L2Item template = ItemTable.getInstance().getTemplate(id);
			if (!template.isStackable())
			{
				activeChar.sendMessage("This item does not stack - Creation aborted.");
				return;
			}
		}

		activeChar.getInventory().addItem("Admin", id, num, activeChar, null);

		ItemList il = new ItemList(activeChar, true);
		activeChar.sendPacket(il);

		activeChar.sendMessage("You have spawned " + num + " item(s) number " + id + " in your inventory.");
	}
}