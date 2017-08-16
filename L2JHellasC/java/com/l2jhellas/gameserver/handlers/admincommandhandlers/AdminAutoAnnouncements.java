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

import com.l2jhellas.gameserver.handler.AutoAnnouncementHandler;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands:
 * - admin_list_autoannouncements = lists all announcments
 * - admin_add_autoannouncement = adds text to startup announcements
 * - admin_del_autoannouncement = deletes announcement with respective id
 * - admin_autoannounce = announce all stored announcements to all players
 */
public class AdminAutoAnnouncements implements IAdminCommandHandler
{
	private static String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_list_autoannouncements",
		"admin_add_autoannouncement",
		"admin_del_autoannouncement",
		"admin_autoannounce"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (command.equals("admin_list_autoannouncements"))
		{
			AutoAnnouncementHandler.getInstance().listAutoAnnouncements(admin);
		}
		else if (command.startsWith("admin_add_autoannouncement"))
		{
			// FIXME the player can send only 16 chars (if you try to send more
			// it sends null), remove this function or not?
			if (!command.equals("admin_add_autoannouncement"))
			{
				try
				{
					StringTokenizer st = new StringTokenizer(command.substring(27));
					
					if(st!=null)
					{
					int delay = Integer.parseInt(st.nextToken().trim());
					String autoAnnounce = st.nextToken();

					if (delay > 30)
					{
						while (st.hasMoreTokens())
						{
							autoAnnounce = autoAnnounce + " " + st.nextToken();
						};

						AutoAnnouncementHandler.getInstance().registerAnnouncment(autoAnnounce, delay);
						AutoAnnouncementHandler.getInstance().listAutoAnnouncements(admin);
					}
					else
						admin.sendMessage("Delay must be > 30");
				    }
				}
				catch (StringIndexOutOfBoundsException e)
				{
				}
			}
		}
		else if (command.startsWith("admin_del_autoannouncement"))
		{
			try
			{
				int val = new Integer(command.substring(27)).intValue();
				AutoAnnouncementHandler.getInstance().removeAnnouncement(val);
				AutoAnnouncementHandler.getInstance().listAutoAnnouncements(admin);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_autoannounce"))
		{
			AutoAnnouncementHandler.getInstance().listAutoAnnouncements(admin);
		}

		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}