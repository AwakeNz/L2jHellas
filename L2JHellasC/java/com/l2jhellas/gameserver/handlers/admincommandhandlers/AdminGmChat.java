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

import com.l2jhellas.gameserver.datatables.xml.AdminData;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.CreatureSay;

/**
 * This class handles following admin commands:
 * - gmchat text = sends text to all online GM's
 * - gmchat_menu text = same as gmchat, displays the admin panel after chat
 */
public class AdminGmChat implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_gmchat",
		"admin_snoop",
		"admin_gmchat_menu"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_gmchat"))
		{
			handleGmChat(command, activeChar);
		}
		else if (command.startsWith("admin_snoop"))
		{
			snoop(command, activeChar);
		}
		if (command.startsWith("admin_gmchat_menu"))
		{
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		return true;
	}

	/**
	 * @param command
	 * @param activeChar
	 */
	private void snoop(String command, L2PcInstance activeChar)
	{
		L2Object target = activeChar.getTarget();
		if (target == null)
		{
			activeChar.sendPacket(SystemMessageId.SELECT_TARGET);
			return;
		}
		if (!(target instanceof L2PcInstance))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		L2PcInstance player = (L2PcInstance) target;
		player.addSnooper(activeChar);
		activeChar.addSnooped(player);
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	/**
	 * @param command
	 * @param activeChar
	 */
	private void handleGmChat(String command, L2PcInstance activeChar)
	{
		try
		{
			int offset = 0;
			String text;
			if (command.contains("menu"))
			{
				offset = 17;
			}
			else
			{
				offset = 13;
			}
			text = command.substring(offset);
			CreatureSay cs = new CreatureSay(0, 9, activeChar.getName(), text);
			AdminData.getInstance().broadcastToGMs(cs);
		}
		catch (StringIndexOutOfBoundsException e)
		{
			// empty message.. ignore
		}
	}
}