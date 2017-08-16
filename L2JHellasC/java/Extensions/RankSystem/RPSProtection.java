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
package Extensions.RankSystem;

import java.util.Map;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.entity.engines.CTF;
import com.l2jhellas.gameserver.model.entity.engines.DM;
import com.l2jhellas.gameserver.model.entity.engines.TvT;
import com.l2jhellas.gameserver.model.zone.ZoneId;

/**
 * Class contains some check methods.
 * Each L2j pack use difference variables or method names, so this class is dedicated for each pack.
 * 
 * @author Masterio
 */
public class RPSProtection
{
	/**
	 * No farming detected if return TRUE.<BR>
	 * Checking: Party, Clan/Ally, IP, self-kill.
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	public static final boolean antiFarmCheck(L2PcInstance player1, L2PcInstance player2)
	{

		if (player1 == null || player2 == null)
			return true;

		if (player1.equals(player2))
			return false;

		//Anti FARM Clan - Ally
		if (Config.ANTI_FARM_CLAN_ALLY_ENABLED && checkClan(player1, player2) && checkAlly(player1, player2))
		{
			player1.sendMessage("PvP Farm is not allowed!");
			return false;
		}

		//Anti FARM Party   
		if (Config.ANTI_FARM_PARTY_ENABLED && checkParty(player1, player2))
		{
			player1.sendMessage("PvP Farm is not allowed!");
			return false;
		}

		//Anti FARM same IP
		if (Config.ANTI_FARM_IP_ENABLED && checkIP(player1, player2))
		{
			player1.sendMessage("PvP Farm is not allowed!");
			return false;
		}

		return true;
	}

	/**
	 * If player on the event or on olympiad, return TRUE.
	 * This method can be difference for any l2jServer revisions.
	 * 
	 * @param player
	 * @return
	 */
	public static final boolean checkEvent(L2PcInstance player)
	{
		if (player.isInOlympiadMode() || player.isOlympiadStart())
			return true;
		if ((TvT._started && player._inEventTvT) || player.isinZodiac || (DM._started && player._inEventDM) || (CTF._started && player._inEventCTF))
			return true;

		return false;
	}

	/**
	 * If player1 and player2 are in the same clan, return TRUE.
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	public final static boolean checkClan(L2PcInstance player1, L2PcInstance player2)
	{
		if (player1.getClanId() > 0 && player2.getClanId() > 0 && player1.getClanId() == player2.getClanId())
			return true;

		return false;
	}

	/**
	 * If player1 and player2 in the same ally, return TRUE.
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	public final static boolean checkAlly(L2PcInstance player1, L2PcInstance player2)
	{
		if (player1.getAllyId() > 0 && player2.getAllyId() > 0 && player1.getAllyId() == player2.getAllyId())
			return true;

		return false;
	}

	/**
	 * If player1 and player2 clans have a war, return TRUE.
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	public final static boolean checkWar(L2PcInstance player1, L2PcInstance player2)
	{
		if (player1.getClanId() > 0 && player2.getClanId() > 0 && player1.getClan() != null && player2.getClan() != null && player1.getClan().isAtWarWith(player2.getClan().getClanId()))
			return true;

		return false;
	}

	/**
	 * If player1 and player2 are in party return TRUE.
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	public final static boolean checkParty(L2PcInstance player1, L2PcInstance player2)
	{
		if (player1.getParty() != null && player2.getParty() != null && player1.getParty().equals(player2.getParty()))
			return true;

		return false;
	}

	/**
	 * If killer and victim have the same IP address return TRUE.
	 * 
	 * @param killer
	 * @param victim
	 * @return
	 */
	public final static boolean checkIP(L2PcInstance killer, L2PcInstance victim)
	{
		if (killer.getClient() != null && victim.getClient() != null)
		{
			String ip1 = killer.getClient().getConnection().getInetAddress().getHostAddress();
			String ip2 = victim.getClient().getConnection().getInetAddress().getHostAddress();

			if (ip1.equals(ip2))
				return true;
		}

		return false;
	}

	/**
	 * Returns true if character is in allowed zone.
	 * 
	 * @param player
	 * @return
	 */
	public static final boolean isInPvpAllowedZone(L2PcInstance player)
	{
		if (Config.ALLOWED_ZONES_IDS.size() == 0)
			return true;

		for (int value : Config.ALLOWED_ZONES_IDS)
		{
			ZoneId zone = getZoneId(value);

			if (player.isInsideZone(zone))
				return true;
		}

		return false;
	}

	/**
	 * Returns true if character is in restricted zone.
	 * 
	 * @param player
	 * @return
	 */
	public static final boolean isInPvpRestrictedZone(L2PcInstance player)
	{
		for (int value : Config.RESTRICTED_ZONES_IDS)
		{
			ZoneId zone = getZoneId(value);

			if (player.isInsideZone(zone))
				return true;
		}

		return false;
	}

	/**
	 * Returns true if character is in restricted zone for death manager.
	 * 
	 * @param player
	 * @return
	 */
	public static final boolean isInDMRestrictedZone(L2PcInstance player)
	{
		for (int value : Config.DEATH_MANAGER_RESTRICTED_ZONES_IDS)
		{
			ZoneId zone = getZoneId(value);

			if (player.isInsideZone(zone))
				return true;
		}

		return false;
	}

	/**
	 * Returns 1.0 if character is not in Bonus Ratio zone, otherwise returns ratio from configuration file.
	 * 
	 * @param player
	 * @return
	 */
	public static final double getZoneBonusRatio(L2PcInstance player)
	{
		for (Map.Entry<Integer, Double> e : Config.RANK_POINTS_BONUS_ZONES_IDS.entrySet())
		{
			ZoneId zone = getZoneId(e.getKey());

			if (player.isInsideZone(zone))
				return e.getValue();
		}

		return 1.0;
	}
	/**
	 * Returns the ZoneId.<br>
	 * ZoneId not exists in lower revisions of l2jServer (H5), then this method can be removed.<br>
	 * <b>IMPORTANT:</b> L2jServer have difference zone id's for each L2 chronicle.
	 * 
	 * @param zoneId
	 * @return
	 */
	private static final ZoneId getZoneId(int zoneId)
	{
		ZoneId zone = null;
		
		switch (zoneId)
		{
			case 1:
				zone = ZoneId.PVP;
			case 2:
				zone = ZoneId.PEACE;
			case 4:
				zone = ZoneId.SIEGE;
			case 8:
				zone = ZoneId.MOTHER_TREE;
			case 12:
				zone = ZoneId.NO_SUMMON_FRIEND;
			case 16:
				zone = ZoneId.CLAN_HALL;
			case 64:
				zone = ZoneId.NO_LANDING;
			case 128:
				zone = ZoneId.WATER;
			case 256:
				zone = ZoneId.JAIL;
			case 512:
				zone = ZoneId.MONSTER_TRACK;
		}

		return zone;
	}
}
