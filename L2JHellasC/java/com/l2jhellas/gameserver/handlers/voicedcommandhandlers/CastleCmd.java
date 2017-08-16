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
import com.l2jhellas.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.entity.Castle;
import com.l2jhellas.gameserver.network.serverpackets.Ride;

public class CastleCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
	"open doors", "close doors", "ride wyvern"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith(VOICED_COMMANDS[0]) && target.equals("castle") && (activeChar.isClanLeader()))
		{
			L2DoorInstance door = (L2DoorInstance) activeChar.getTarget();
			Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().hasCastle());
			if (door == null || castle == null)
				return false;
			if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
			{
				door.openMe();
			}

		}
		else if (command.startsWith(VOICED_COMMANDS[1]) && target.equals("castle") && (activeChar.isClanLeader()))
		{
			L2DoorInstance door = (L2DoorInstance) activeChar.getTarget();
			Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().hasCastle());
			if (door == null || castle == null)
				return false;
			if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
			{
				door.closeMe();
			}

		}
		else if (command.startsWith(VOICED_COMMANDS[2]) && target.equals("castle"))
		{
			if (activeChar.getClan().hasCastle() > 0 && activeChar.isClanLeader())
			{
				
				if (!activeChar.disarmWeapons())
					return false;
				
				if(activeChar.getActiveTradeList() !=null)
					activeChar.cancelActiveTrade();
				
				Ride mount = new Ride(activeChar.getObjectId(), Ride.ACTION_MOUNT, 12621);
				activeChar.sendPacket(mount);
				activeChar.broadcastPacket(mount);
				activeChar.setMountType(mount.getMountType());
			}
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}