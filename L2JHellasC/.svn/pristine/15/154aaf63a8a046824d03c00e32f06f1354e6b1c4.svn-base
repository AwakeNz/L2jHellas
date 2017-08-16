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

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.communitybbs.Manager.RegionBBSManager;
import com.l2jhellas.gameserver.datatables.xml.AdminData;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.entity.olympiad.Olympiad;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.logs.GMAudit;

/**
 * This class handles following admin commands:
 * - admin|admin1/admin2/admin3/admin4/admin5 = slots for the 5 starting admin
 * menus
 * - gmliston/gmlistoff = includes/excludes active character from /gmlist
 * results
 * - silence = toggles private messages acceptance mode
 * - diet = toggles weight penalty mode
 * - tradeoff = toggles trade acceptance mode
 * - reload = reloads specified component from
 * multisell|skill|npc|htm|item|instancemanager
 * - set/set_menu/set_mod = alters specified server setting
 * - saveolymp = saves olympiad state manually
 * - manualhero = cycles olympiad and calculate new heroes.
 */
public class AdminAdmin implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_admin",
		"admin_admin1",
		"admin_admin2",
		"admin_admin3",
		"admin_admin4",
		"admin_admin5",
		"admin_gmliston",
		"admin_gmlistoff",
		"admin_silence",
		"admin_invis",
		"admin_vis",
		"admin_inv_menu",
		"admin_diet",
		"admin_tradeoff",
		"admin_set",
		"admin_set_menu",
		"admin_set_mod",
		"admin_saveolymp",
		"admin_manualhero"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");

		if (command.startsWith("admin_admin"))
		{
			showMainPage(activeChar, command);
		}
		else if (command.startsWith("admin_gmliston"))
		{
			AdminData.getInstance().showGm(activeChar);
			activeChar.sendMessage("Registerd into gm list");
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		else if (command.startsWith("admin_gmlistoff"))
		{
			AdminData.getInstance().hideGm(activeChar);
			activeChar.sendMessage("Removed from gm list");
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		else if (command.equals("admin_inv_menu"))
		{
			if (!activeChar.getAppearance().getInvisible())
			{
				activeChar.getAppearance().setInvisible();
				activeChar.broadcastUserInfo();
				activeChar.decayMe();
				activeChar.spawnMe();
			}
			else
			{
				activeChar.getAppearance().setVisible();
				activeChar.broadcastUserInfo();
			}
			RegionBBSManager.getInstance().changeCommunityBoard();
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		else if (command.startsWith("admin_invis"))
		{
			activeChar.getAppearance().setInvisible();
			activeChar.broadcastUserInfo();
			activeChar.decayMe();
			activeChar.spawnMe();
			RegionBBSManager.getInstance().changeCommunityBoard();
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		else if (command.startsWith("admin_vis"))
		{
			activeChar.getAppearance().setVisible();
			activeChar.broadcastUserInfo();
			RegionBBSManager.getInstance().changeCommunityBoard();
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		else if (command.startsWith("admin_silence"))
		{
			if (activeChar.getMessageRefusal()) // already in message refusal mode
			{
				activeChar.setMessageRefusal(false);
				activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
			}
			else
			{
				activeChar.setMessageRefusal(true);
				activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
			}
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		else if (command.startsWith("admin_saveolymp"))
		{
			try
			{
				Olympiad.getInstance().saveOlympiadStatus();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Typed wrong command!");
			}
			activeChar.sendMessage("Olympiad data is saved!");
			AdminHelpPage.showHelpPage(activeChar, "game_menu.htm");
		}
		else if (command.startsWith("admin_manualhero"))
		{
			try
			{
				Olympiad.getInstance().manualSelectHeroes();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Typed wrong command!");
			}
			activeChar.sendMessage("Heroes are formed");
			AdminHelpPage.showHelpPage(activeChar, "game_menu.htm");
		}
		else if (command.startsWith("admin_diet"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				if (st.nextToken().equalsIgnoreCase("on"))
				{
					activeChar.setDietMode(true);
					activeChar.sendMessage("Diet mode ON");
				}
				else if (st.nextToken().equalsIgnoreCase("off"))
				{
					activeChar.setDietMode(false);
					activeChar.sendMessage("Diet mode OFF");
				}
			}
			catch (Exception ex)
			{
				if (activeChar.getDietMode())
				{
					activeChar.setDietMode(false);
					activeChar.sendMessage("Diet mode OFF");
				}
				else
				{
					activeChar.setDietMode(true);
					activeChar.sendMessage("Diet mode ON");
				}
			}
			finally
			{
				activeChar.refreshOverloaded();
			}
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		else if (command.startsWith("admin_tradeoff"))
		{
			try
			{
				String mode = command.substring(15);
				if (mode.equalsIgnoreCase("on"))
				{
					activeChar.setTradeRefusal(true);
					activeChar.sendMessage("Trade refusal is enabled");
				}
				else if (mode.equalsIgnoreCase("off"))
				{
					activeChar.setTradeRefusal(false);
					activeChar.sendMessage("Trade refusal is disabled");
				}
			}
			catch (Exception ex)
			{
				if (activeChar.getTradeRefusal())
				{
					activeChar.setTradeRefusal(false);
					activeChar.sendMessage("Trade refusal is disabled");
				}
				else
				{
					activeChar.setTradeRefusal(true);
					activeChar.sendMessage("Trade refusal is enabled");
				}
			}
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		}
		else if (command.startsWith("admin_set"))
		{
			StringTokenizer st = new StringTokenizer(command);
			String[] cmd = st.nextToken().split("_");
			try
			{
				String[] parameter = st.nextToken().split("=");
				String pName = parameter[0].trim();
				String pValue = parameter[1].trim();
				if (Config.setParameterValue(pName, pValue))
				{
					activeChar.sendMessage("parameter " + pName + " succesfully set to " + pValue);
				}
				else
				{
					activeChar.sendMessage("Invalid parameter!");
				}
			}
			catch (Exception e)
			{
				if (cmd.length == 2)
				{
					activeChar.sendMessage("Usage: //set parameter=vaue");
				}
			}
			finally
			{
				if (cmd.length == 3)
				{
					if (cmd[2].equalsIgnoreCase("menu"))
					{
						AdminHelpPage.showHelpPage(activeChar, "settings.htm");
					}
					else if (cmd[2].equalsIgnoreCase("mod"))
					{
						AdminHelpPage.showHelpPage(activeChar, "mods_menu.htm");
					}
				}
			}
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private void showMainPage(L2PcInstance activeChar, String command)
	{
		int mode = 0;
		String filename = null;
		try
		{
			mode = Integer.parseInt(command.substring(11));
		}
		catch (Exception e)
		{
		}
		switch (mode)
		{
			case 1:
				filename = "main";
			break;
			case 2:
				filename = "game";
			break;
			case 3:
				filename = "effects";
			break;
			case 4:
				filename = "server";
			break;
			case 5:
				filename = "mods";
			break;
			default:
				filename = "main";
			break;
		}
		AdminHelpPage.showHelpPage(activeChar, filename + "_menu.htm");
	}
}