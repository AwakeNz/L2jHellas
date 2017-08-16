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

import com.l2jhellas.gameserver.communitybbs.Manager.RegionBBSManager;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.LeaveWorld;
import com.l2jhellas.logs.GMAudit;

public class AdminKick implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_kick",
		"admin_kick_non_gm"
	};/** @formatter:off */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		String target = (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target");
		GMAudit.auditGMAction(activeChar.getName(), command, target, "");

		if (command.startsWith("admin_kick"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String player = st.nextToken();
				L2PcInstance plyr = L2World.getInstance().getPlayer(player);
				if (plyr != null)
				{
					plyr.sendPacket(new LeaveWorld());
					plyr.closeNetConnection(false);
					activeChar.sendMessage("You kicked " + plyr.getName() + " from the game.");
					RegionBBSManager.getInstance().changeCommunityBoard();
				}
			}
		}
		if (command.startsWith("admin_kick_non_gm"))
		{
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
			{
				if (!player.isGM())
				{
					counter++;
					player.sendPacket(new LeaveWorld());
					player.closeNetConnection(false);
					RegionBBSManager.getInstance().changeCommunityBoard();
				}
			}
			activeChar.sendMessage("Kicked " + counter + " players");
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}