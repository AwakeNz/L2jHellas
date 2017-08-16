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

import com.l2jhellas.gameserver.datatables.xml.AdminData;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands:
 * - gm = turns gm mode on/off
 */
public class AdminGm implements IAdminCommandHandler
{
	protected static final Logger _log = Logger.getLogger(AdminGm.class.getName());
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_gm"
	};

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_gm"))
		{
			handleGm(activeChar);
		}

		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private void handleGm(L2PcInstance activeChar)
	{
		if (activeChar.isGM())
		{
			AdminData.getInstance().deleteGm(activeChar);
			activeChar.tempAc = activeChar.getAccessLevel().getLevel();
			activeChar.setAccessLevel(0);

			activeChar.sendMessage("You no longer have GM status.");
			_log.config(AdminGm.class.getName() + ": GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") turned his GM status off");
		}
		else
		{
			AdminData.getInstance().addGm(activeChar, false);
			activeChar.setAccessLevel(activeChar.tempAc);

			activeChar.sendMessage("You now have GM status.");
			_log.config(AdminGm.class.getName() + ": GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") turned his GM status on");
		}
	}
}