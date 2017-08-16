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
package com.l2jhellas.gameserver.handlers.usercommandhandlers;

import com.l2jhellas.gameserver.handler.IUserCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.Ride;
import com.l2jhellas.gameserver.skills.SkillTable;
import com.l2jhellas.util.Broadcast;

/**
 * Support for /dismount command.
 * 
 * @author Micht
 */
public class DisMount implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		62
	};

	@Override
	public synchronized boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
			return false;

		if (activeChar.isRentedPet())
		{
			activeChar.stopRentPet();
		}
		else if (activeChar.isMounted())
		{
			if (activeChar.setMountType(0))
			{
				if(activeChar.getActiveTradeList() !=null)
					activeChar.cancelActiveTrade();
				
				if (activeChar.isFlying())
					activeChar.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
				Ride dismount = new Ride(activeChar.getObjectId(), Ride.ACTION_DISMOUNT, 0);
				Broadcast.toSelfAndKnownPlayersInRadius(activeChar, dismount, 810000/* 900 */);
				activeChar.setMountObjectID(0);
			}
		}
		return true;
	}

	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}