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

import com.l2jhellas.gameserver.handler.IVoicedCommandHandler;
import com.l2jhellas.gameserver.instancemanager.CastleManager;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

public class ClCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"cl"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{

		if (command.equalsIgnoreCase(VOICED_COMMANDS[0]))
		{

			if (activeChar.getClan() == null)
				return false;

			L2PcInstance leader = null;
			leader = (L2PcInstance) L2World.getInstance().findObject(activeChar.getClan().getLeaderId());

			if (leader == null)
			{
				activeChar.sendMessage("Your partner is not online.");
				return false;
			}
			else if (leader.isInJail())
			{
				activeChar.sendMessage("Your leader is in Jail.");
				return false;
			}
			else if (leader.isInOlympiadMode())
			{
				activeChar.sendMessage("Your leader is in the Olympiad now.");
				return false;
			}
			else if (leader.atEvent)
			{
				activeChar.sendMessage("Your leader is in an event.");
				return false;
			}
			else if (leader.isInDuel())
			{
				activeChar.sendMessage("Your leader is in a duel.");
				return false;
			}
			else if (leader.isFestivalParticipant())
			{
				activeChar.sendMessage("Your leader is in a festival.");
				return false;
			}
			else if (leader.isInParty() && leader.getParty().isInDimensionalRift())
			{
				activeChar.sendMessage("Your leader is in dimensional rift.");
				return false;
			}
			else if (leader.inObserverMode())
			{
				activeChar.sendMessage("Your leader is in the observation.");
			}
			else if (leader.getClan() != null && CastleManager.getInstance().getCastleByOwner(leader.getClan()) != null && CastleManager.getInstance().getCastleByOwner(leader.getClan()).getSiege().getIsInProgress())
			{
				activeChar.sendMessage("Your leader is in siege, you can't go to your leader.");
				return false;
			}
			else if (activeChar.isInJail())
			{
				activeChar.sendMessage("You are in Jail!");
				return false;
			}
			else if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("You are in the Olympiad now.");
				return false;
			}
			else if (activeChar.atEvent)
			{
				activeChar.sendMessage("You are in an event.");
				return false;
			}
			else if (activeChar.isInDuel())
			{
				activeChar.sendMessage("You are in a duel!");
				return false;
			}
			else if (activeChar.inObserverMode())
			{
				activeChar.sendMessage("You are in the observation.");
			}
			else if (activeChar.getClan() != null && CastleManager.getInstance().getCastleByOwner(activeChar.getClan()) != null && CastleManager.getInstance().getCastleByOwner(activeChar.getClan()).getSiege().getIsInProgress())
			{
				activeChar.sendMessage("You are in siege, you can't go to your leader.");
				return false;
			}
			else if (activeChar.isFestivalParticipant())
			{
				activeChar.sendMessage("You are in a festival.");
				return false;
			}
			else if (activeChar.isInParty() && activeChar.getParty().isInDimensionalRift())
			{
				activeChar.sendMessage("You are in the dimensional rift.");
				return false;
			}
			else if (activeChar == leader)
			{
				activeChar.sendMessage("You cannot teleport to yourself.");
				return false;
			}
			if (activeChar.getInventory().getItemByItemId(3470) == null)
			{
				activeChar.sendMessage("You need one or more Gold Bars to use the cl-teleport system.");
				return false;
			}
			int leaderx = 0;
			int leadery = 0;
			int leaderz = 0;

			leaderx = leader.getX();
			leadery = leader.getY();
			leaderz = leader.getZ();

			activeChar.teleToLocation(leaderx, leadery, leaderz);
			activeChar.sendMessage("You have been teleported to your leader!");
			activeChar.getInventory().destroyItemByItemId("RessSystem", 3470, 1, activeChar, activeChar.getTarget());
			activeChar.sendMessage("One GoldBar has dissapeared! Thank you!");
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}