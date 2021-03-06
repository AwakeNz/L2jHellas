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

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.geodata.GeoEngine;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.logs.GMAudit;

/**
 * @author -Nemesiss-
 */
public class AdminGeodata implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_geo_z",
		"admin_geo_type",
		"admin_geo_nswe",
		"admin_geo_los",
		"admin_geo_position",
		"admin_geo_bug",
		"admin_geo_load",
		"admin_geo_unload"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		String target = (activeChar.getTarget() != null) ? activeChar.getTarget().getName() : "no-target";
		GMAudit.auditGMAction(activeChar.getName(), command, target, "");

		if (!Config.GEODATA)
		{
			activeChar.sendMessage("Geo Engine is Turned Off!");
			AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
			return true;
		}

		if (command.equals("admin_geo_z"))
		{
			activeChar.sendMessage("GeoEngine: Geo_Z = " + GeoEngine.getHeight(activeChar.getX(), activeChar.getY(), activeChar.getZ()) + " Loc_Z = " + activeChar.getZ());
			AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
		}
		else if (command.equals("admin_geo_type"))
		{
			short type = GeoEngine.getType(activeChar.getX(), activeChar.getY());
			activeChar.sendMessage("GeoEngine: Geo_Type = " + type);
			int height = GeoEngine.getHeight(activeChar.getX(), activeChar.getY(), activeChar.getZ());
			activeChar.sendMessage("GeoEngine: height = " + height);
			AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
		}
		else if (command.equals("admin_geo_nswe"))
		{
			String result = "";
			short nswe = GeoEngine.getNSWE(activeChar.getX(), activeChar.getY(), activeChar.getZ());
			if ((nswe & 8) == 0)
				result += " N";
			if ((nswe & 4) == 0)
				result += " S";
			if ((nswe & 2) == 0)
				result += " W";
			if ((nswe & 1) == 0)
				result += " E";
			activeChar.sendMessage("GeoEngine: Geo_NSWE -> " + nswe + "->" + result);
			AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
		}
		else if (command.equals("admin_geo_los"))
		{
			if (activeChar.getTarget() != null)
			{
				if (GeoEngine.canSeeTarget(activeChar, activeChar.getTarget(), false))
					activeChar.sendMessage("GeoEngine: Can See Target");
				else
					activeChar.sendMessage("GeoEngine: Can't See Target");

			}
			else
				activeChar.sendMessage("None Target!");
			AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
		}
		else if (command.equals("admin_geo_position"))
		{
			activeChar.sendMessage("GeoEngine: Your current position: ");
			activeChar.sendMessage(".... world coords: x: " + activeChar.getX() + " y: " + activeChar.getY() + " z: " + activeChar.getZ());
			AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
		}
		else if (command.startsWith("admin_geo_load"))
		{
			String[] v = command.substring(15).split(" ");
			if (v.length != 2)
				activeChar.sendMessage("Usage: //admin_geo_load <regionX> <regionY>");
			else
			{
				try
				{
					byte rx = Byte.parseByte(v[0]);
					byte ry = Byte.parseByte(v[1]);

					boolean result = GeoEngine.LoadGeodataFile(rx, ry);

					if (result)
						activeChar.sendMessage("GeoEngine: File for region [" + rx + "," + ry + "] loaded succesfuly");
					else
						activeChar.sendMessage("GeoEngine: File for region [" + rx + "," + ry + "] couldn't be loaded");
				}
				catch (Exception e)
				{
					activeChar.sendMessage("You have to write numbers of regions <regionX> <regionY>");
				}
			}
			AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
		}
		/*else if (command.startsWith("admin_geo_unload"))
		{
			String[] v = command.substring(17).split(" ");
			if (v.length != 2)
				activeChar.sendMessage("Usage: //admin_geo_unload <regionX> <regionY>");
			else
			{
				try
				{
					byte rx = Byte.parseByte(v[0]);
					byte ry = Byte.parseByte(v[1]);

					//GeoEngine.unloadGeodata(rx, ry);
					//activeChar.sendMessage("GeoEngine: File for region [" + rx + "," + ry + "] unloaded.");
				}
				catch (Exception e)
				{
					activeChar.sendMessage("You have to write numbers of regions <regionX> <regionY>");
				}
			}
			AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
		}*/
		/*else if (command.startsWith("admin_geo_bug"))
		{
			try
			{
				String comment = command.substring(14);
				GeoEngine.addGeoDataBug(activeChar, comment);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //admin_geo_bug you coments here");
				AdminHelpPage.showHelpPage(activeChar, "server_menu.htm");
			}
		}*/
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}