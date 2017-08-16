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

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.datatables.sql.ClanTable;
import com.l2jhellas.gameserver.model.L2Clan;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;

public final class AllyDismiss extends L2GameClientPacket
{
	private static final String _C__85_ALLYDISMISS = "[C] 85 AllyDismiss";

	private String _clanName;

	@Override
	protected void readImpl()
	{
		_clanName = readS();
	}

	@Override
	protected void runImpl()
	{
		if (_clanName == null)
			return;
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		if (player.getClan() == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
			return;
		}
		L2Clan leaderClan = player.getClan();
		if (leaderClan.getAllyId() == 0)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(SystemMessageId.NO_CURRENT_ALLIANCES);
			return;
		}
		if (!player.isClanLeader() || leaderClan.getClanId() != leaderClan.getAllyId())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER);
			return;
		}
		L2Clan clan = ClanTable.getInstance().getClanByName(_clanName);
		if (clan == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(SystemMessageId.CLAN_DOESNT_EXISTS);
			return;
		}
		if (clan.getClanId() == leaderClan.getClanId())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(SystemMessageId.ALLIANCE_LEADER_CANT_WITHDRAW);
			return;
		}
		if (clan.getAllyId() != leaderClan.getAllyId())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(SystemMessageId.DIFFERENT_ALLIANCE);
			return;
		}

		long currentTime = System.currentTimeMillis();
		leaderClan.setAllyPenaltyExpiryTime(
				/** @formatter:off */
        		currentTime + Config.ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED * 86400000L,
        		L2Clan.PENALTY_TYPE_DISMISS_CLAN); //24*60*60*1000 = 86400000
        		/** @formatter:on */
		leaderClan.updateClanInDB();

		clan.setAllyId(0);
		clan.setAllyName(null);
		clan.setAllyPenaltyExpiryTime(
				/** @formatter:off */
        		currentTime + Config.ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED * 86400000L,
        		L2Clan.PENALTY_TYPE_CLAN_DISMISSED); //24*60*60*1000 = 86400000
        		/** @formatter:on */
		clan.updateClanInDB();
		player.sendPacket(SystemMessageId.YOU_HAVE_EXPELED_A_CLAN);
	}

	@Override
	public String getType()
	{
		return _C__85_ALLYDISMISS;
	}
}