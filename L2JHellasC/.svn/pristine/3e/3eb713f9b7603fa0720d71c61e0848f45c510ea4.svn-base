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
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.logs.GMAudit;

/**
 * This class handles following admin commands:
 * - invul = turns invulnerability on/off
 */
public class AdminInvul implements IAdminCommandHandler
{
	private static Logger _log = Logger.getLogger(AdminInvul.class.getName());
	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_invul",
		"admin_setinvul"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");

		if (command.equals("admin_invul"))
		{
			handleInvul(activeChar);
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		if (command.equals("admin_setinvul"))
		{
			L2Object target = activeChar.getTarget();
			if (target instanceof L2PcInstance)
			{
				handleInvul((L2PcInstance) target);
			}
		}
		return true;
	}

	private void handleInvul(L2PcInstance activeChar)
	{
		String text;
		if (activeChar.isInvul())
		{
			activeChar.setIsInvul(false);
			text = activeChar.getName() + " is now mortal";
			if (Config.DEBUG)
				_log.config(AdminInvul.class.getName() + ": Gm removed invul mode from character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
		}
		else
		{
			activeChar.setIsInvul(true);
			text = activeChar.getName() + " is now invulnerable";
			if (Config.DEBUG)
				_log.config(AdminInvul.class.getName() + ": Gm activated invul mode for character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
		}
		activeChar.sendMessage(text);
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}