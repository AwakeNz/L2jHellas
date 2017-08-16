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
package com.l2jhellas.gameserver.network.clientpackets;

import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ExAskJoinPartyRoom;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;


public class RequestAskJoinPartyRoom extends L2GameClientPacket
{
	private static final String _C__D0_14_REQUESTASKJOINPARTYROOM = "[C] D0:14 RequestAskJoinPartyRoom";
	private String _playername ;

	@Override
	protected void readImpl()
	{
		_playername = readS();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		    return;
				
		final L2PcInstance target = L2World.getInstance().getPlayer(_playername);
		
		if (target != null)
		{
			if (!target.isProcessingRequest())
			{
				activeChar.onTransactionRequest(target);
				target.sendPacket(new ExAskJoinPartyRoom(activeChar.getName()));
			}
			else
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addPcName(target));
		}
		else
		 activeChar.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
				
	}

	@Override
	public String getType()
	{
		return _C__D0_14_REQUESTASKJOINPARTYROOM;
	}
}