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

import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

public class AdminDeport implements IAdminCommandHandler
{
	private static String[] ADMIN_COMMANDS =
	{
		"admin_deport"
	};

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{

		if (activeChar.getTarget() instanceof L2PcInstance)
		{
			if (command.startsWith(ADMIN_COMMANDS[0])) // admin_deport
			{
				((L2PcInstance) activeChar.getTarget()).teleToLocation(82698, 148638, -3473); // Location Giran
			}
		}
		return false;

	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}