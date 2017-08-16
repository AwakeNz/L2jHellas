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

import com.l2jhellas.gameserver.communitybbs.Manager.RegionBBSManager;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.logs.GMAudit;

/**
 * This class handles following admin commands:
 * - character_disconnect = disconnects target player
 */
public class AdminDisconnect implements IAdminCommandHandler
{
	protected static final Logger _log = Logger.getLogger(AdminDisconnect.class.getName());

	private static final String[] ADMIN_COMMANDS =
	{
		"admin_character_disconnect"
	};

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_character_disconnect"))
		{
			disconnectCharacter(activeChar);
		}

		String target = (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target");
		GMAudit.auditGMAction(activeChar.getName(), command, target, "");
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private void disconnectCharacter(L2PcInstance activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
		{
			player = (L2PcInstance) target;
		}
		else
		{
			return;
		}

		if (player.getObjectId() == activeChar.getObjectId())
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
			sm.addString("You cannot logout your character.");
			activeChar.sendPacket(sm);
		}
		else
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
			sm.addString("Character " + player.getName() + " disconnected from server.");
			activeChar.sendPacket(sm);

			_log.warning(AdminDisconnect.class.getSimpleName() + ": " + player.getName() + " kicked from server.");

			RegionBBSManager.getInstance().changeCommunityBoard();

			player.closeNetConnection(true);
		}
	}
}