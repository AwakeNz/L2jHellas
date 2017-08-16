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

import com.l2jhellas.gameserver.handler.IUserCommandHandler;
import com.l2jhellas.gameserver.handler.UserCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;

public class RequestUserCommand extends L2GameClientPacket
{
	private static final String _C__AA_REQUESTUSERCOMMAND = "[C] aa RequestUserCommand";

	private int _command;

	@Override
	protected void readImpl()
	{
		_command = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		IUserCommandHandler handler = UserCommandHandler.getInstance().getHandler(_command);

		if (handler == null)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
			sm.addString("user commandID " + _command + " not implemented yet");
			player.sendPacket(sm);
			sm = null;
		}
		else
		{
			handler.useUserCommand(_command, getClient().getActiveChar());
		}
	}

	@Override
	public String getType()
	{
		return _C__AA_REQUESTUSERCOMMAND;
	}
}