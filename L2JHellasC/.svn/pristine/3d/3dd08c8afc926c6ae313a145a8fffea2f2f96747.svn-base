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
package com.l2jhellas.gameserver.handlers.voicedcommandhandlers;

import Extensions.RankSystem.RPSCookie;
import Extensions.RankSystem.RPSHtmlPvpStatus;

import com.l2jhellas.gameserver.handler.IVoicedCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Masterio
 */
public class PvpInfoCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"pvpinfo"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith(VOICED_COMMANDS[0]))
		{
			if (activeChar == null)
				return false;

			if (activeChar.getRPSCookie() == null)
				return false;

			RPSCookie pc = activeChar.getRPSCookie();

			// reset death status:
			if (!activeChar.isDead())
			{
				pc.setDeathStatus(null);
			}

			// save target of active player when command executed:
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof L2PcInstance)
			{
				pc.setTarget((L2PcInstance) activeChar.getTarget());
			}
			else
			{
				pc.setTarget(activeChar);
				activeChar.sendMessage("PvP Status executed on self!");
			}

			RPSHtmlPvpStatus.sendPage(activeChar, pc.getTarget());
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}

}