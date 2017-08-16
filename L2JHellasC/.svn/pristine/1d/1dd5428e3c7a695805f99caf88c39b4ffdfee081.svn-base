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
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.SocialAction;

public class AdminMassHero implements IAdminCommandHandler
{
	private static String[] ADMIN_COMMANDS =
	{
		"admin_masshero"
	};

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (activeChar == null)
			return false;

		if (command.startsWith(ADMIN_COMMANDS[0])) // admin_masshero
		{
			for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
			{
				if (player instanceof L2PcInstance)
				{
					/* Check to see if the player already is Hero and if aren't in Olympiad Mode */
					if (!player.isHero() || !player.isInOlympiadMode())
					{
						player.setHero(true);
						player.sendMessage("Admin is rewarding all online players with Hero Status.");
						player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
						player.broadcastUserInfo();
					}
					player = null;
				}
			}
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}