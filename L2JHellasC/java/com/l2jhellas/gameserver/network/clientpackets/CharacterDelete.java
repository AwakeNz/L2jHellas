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

import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.CharDeleteFail;
import com.l2jhellas.gameserver.network.serverpackets.CharDeleteOk;
import com.l2jhellas.gameserver.network.serverpackets.CharSelectInfo;

public final class CharacterDelete extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(CharacterDelete.class.getName());
	private static final String _C__0C_CHARACTERDELETE = "[C] 0C CharacterDelete";

	// cd
	private int _charSlot;

	@Override
	protected void readImpl()
	{
		_charSlot = readD();
	}

	@Override
	protected void runImpl()
	{
		if (Config.DEBUG)
			_log.fine("deleting slot:" + _charSlot);

		L2PcInstance character = null;
		try
		{
			if (Config.DELETE_DAYS == 0)
				character = getClient().deleteChar(_charSlot);
			else
				character = getClient().markToDeleteChar(_charSlot);
		}
		catch (Exception e)
		{
			_log.warning(CharacterDelete.class.getName() + ": could not delete character slot:" + _charSlot);
			if (Config.DEVELOPER)
				e.printStackTrace();
		}

		if (character == null)
		{
			sendPacket(new CharDeleteOk());
		}
		else
		{
			if (character.isClanLeader())
			{
				sendPacket(new CharDeleteFail(CharDeleteFail.REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED));
			}
			else
			{
				sendPacket(new CharDeleteFail(CharDeleteFail.REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER));
			}
		}

		CharSelectInfo cl = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1, 0);
		sendPacket(cl);
		getClient().setCharSelection(cl.getCharInfo());
	}

	@Override
	public String getType()
	{
		return _C__0C_CHARACTERDELETE;
	}
}