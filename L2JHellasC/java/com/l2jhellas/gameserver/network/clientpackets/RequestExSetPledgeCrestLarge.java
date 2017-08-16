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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.cache.CrestCache;
import com.l2jhellas.gameserver.cache.CrestCache.CrestType;
import com.l2jhellas.gameserver.idfactory.IdFactory;
import com.l2jhellas.gameserver.model.L2Clan;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.util.database.L2DatabaseFactory;

/**
 * Format : chdb<BR>
 * c (id) 0xD0<BR>
 * h (subid) 0x11<BR>
 * d data size<BR>
 * b raw data (picture i think ;) )
 * 
 * @author -Wooden-
 */
public final class RequestExSetPledgeCrestLarge extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestExSetPledgeCrestLarge.class.getName());
	private static final String _C__D0_11_REQUESTEXSETPLEDGECRESTLARGE = "[C] D0:11 RequestExSetPledgeCrestLarge";
	private int _length;
	private byte[] _data;

	@Override
	protected void readImpl()
	{
		_length = readD();
		_data = new byte[_length];
		readB(_data);
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		L2Clan clan = activeChar.getClan();
		if (clan == null)
			return;

		if (_length < 0)
		{
			activeChar.sendMessage("File transfer error.");
			return;
		}

		if (_length > 2176)
		{
			activeChar.sendMessage("The insignia file size is greater than 2176 bytes.");
			return;
		}

		@SuppressWarnings("unused")
		boolean updated = false;
		int crestLargeId = -1;
		if ((activeChar.getClanPrivileges() & L2Clan.CP_CL_REGISTER_CREST) == L2Clan.CP_CL_REGISTER_CREST)
		{
			if (clan.hasCastle() == 0 && clan.hasHideout() == 0)
			{
				if (clan.getCrestLargeId() == 0)
					return;
				
				crestLargeId = 0;
				activeChar.sendMessage("The insignia has been removed.");
				updated = true;
				for (L2PcInstance member : clan.getOnlineMembers(""))
					if (member != null)
						member.broadcastUserInfo();
			}
			else
			{
				if (clan.hasCastle() == 0 && clan.hasHideout() == 0)
				{
					activeChar.sendMessage("Only a clan that owns a clan hall or a castle can get their emblem displayed on clan related items.");
					return;
				}
				crestLargeId = IdFactory.getInstance().getNextId();
				if (!CrestCache.saveCrest(CrestType.PLEDGE_LARGE, crestLargeId, _data))
				{
					_log.info(RequestExSetPledgeCrestLarge.class.getName() + "Error saving large crest for clan " + clan.getName() + " [" + clan.getClanId() + "]");
					return;
				}
				
				int newId = IdFactory.getInstance().getNextId();
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest_large_id=? WHERE clan_id=?");
					statement.setInt(1, newId);
					statement.setInt(2, clan.getClanId());
					statement.executeUpdate();
					statement.close();
				}
				catch (SQLException e)
				{
					_log.warning(RequestExSetPledgeCrestLarge.class.getName() + " could not update the database with large crest id:" + e);
					if (Config.DEVELOPER)
						e.printStackTrace();
				}
				clan.setCrestLargeId(newId);
				clan.setHasCrestLarge(true);
				
				activeChar.sendPacket(SystemMessageId.CLAN_EMBLEM_WAS_SUCCESSFULLY_REGISTERED);
				updated = true;
				
				for (L2PcInstance member : clan.getOnlineMembers(""))
					if (member != null)
						member.broadcastUserInfo();
			}
		}
	}

	@Override
	public String getType()
	{
		return _C__D0_11_REQUESTEXSETPLEDGECRESTLARGE;
	}
}