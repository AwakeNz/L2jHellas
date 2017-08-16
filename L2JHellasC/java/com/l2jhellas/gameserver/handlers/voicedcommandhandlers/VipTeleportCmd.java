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

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.handler.IVoicedCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

public class VipTeleportCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"areavip"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equalsIgnoreCase(VOICED_COMMANDS[0]))
		{
			if (activeChar.atEvent)
			{
				activeChar.sendMessage("You cant teleport right now.");
				return false;
			}
			else if (activeChar.isInDuel())
			{
				activeChar.sendMessage("You cant teleport while you are in duel.");
				return false;
			}
			else if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("You cant teleport while you are in Olympiad.");
				return false;
			}
			else if (activeChar.isInCombat())
			{
				activeChar.sendMessage("You cant teleport while you're fighting.");
				return false;
			}
			else if (activeChar.getParty().isInDimensionalRift())
			{
				activeChar.sendMessage("You cant teleport while you are in Dimensional Rift.");
				return false;
			}
			else if (activeChar.isFestivalParticipant())
			{
				activeChar.sendMessage("You cant teleport while you're in festival.");
				return false;
			}
			else if (activeChar.isInJail())
			{
				activeChar.sendMessage("You are in Jail..");
				return false;
			}
			else if (activeChar.inObserverMode())
			{
				activeChar.sendMessage("You cant teleport while in Observer Mode.");
				return false;
			}
			else if (activeChar.isDead())
			{
				activeChar.sendMessage("You are dead! You cant teleport.");
				return false;
			}
			else if (activeChar.isFakeDeath())
			{
				activeChar.sendMessage("You are dead? get up first!");
				return false;
			}
			else if (activeChar._donator = false)
			{
				activeChar.sendMessage("You must be donator to use this service.");
				return false;
			}

			activeChar.teleToLocation(Config.VIP_X, Config.VIP_Y, Config.VIP_Z);
			activeChar.sendMessage("You have been teleported in Vip Area!");
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}