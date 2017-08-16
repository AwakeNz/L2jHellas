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
package com.l2jhellas.gameserver.handlers.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jhellas.gameserver.datatables.xml.MapRegionTable;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.Location;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.zone.ZoneId;

public class AdminZone implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_zone_check"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		// String val = "";
		// if (st.countTokens() >= 1) {val = st.nextToken();}

		if (actualCommand.equalsIgnoreCase("admin_zone_check"))
		{
			if (activeChar.isInsideZone(ZoneId.PVP))
			{
				activeChar.sendMessage("This is a PvP zone.");
			}
			else
			{
				activeChar.sendMessage("This is NOT a PvP zone.");
			}

			if (activeChar.isInsideZone(ZoneId.NO_LANDING))
			{
				activeChar.sendMessage("This is a no landing zone.");
			}
			else
			{
				activeChar.sendMessage("This is NOT a no landing zone.");
			}

			activeChar.sendMessage("MapRegion: x:" + MapRegionTable.getMapRegionX(activeChar.getX()) + " y:" + MapRegionTable.getMapRegionX(activeChar.getY()));

			activeChar.sendMessage("Closest Town: " + MapRegionTable.getInstance().getClosestTownName(activeChar.getX(),activeChar.getY()));

			Location loc;

			loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.Castle);
			activeChar.sendMessage("TeleToLocation (Castle): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());

			loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.ClanHall);
			activeChar.sendMessage("TeleToLocation (ClanHall): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());

			loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.SiegeFlag);
			activeChar.sendMessage("TeleToLocation (SiegeFlag): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());

			loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.Town);
			activeChar.sendMessage("TeleToLocation (Town): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());
		
			AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}