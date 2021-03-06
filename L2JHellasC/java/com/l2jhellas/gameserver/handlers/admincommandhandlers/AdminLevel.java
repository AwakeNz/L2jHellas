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
import com.l2jhellas.gameserver.datatables.xml.ExperienceData;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.logs.GMAudit;

public class AdminLevel implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_add_level",
		"admin_set_level"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		L2Object targetChar = activeChar.getTarget();
		String target = (targetChar != null ? targetChar.getName() : "no-target");
		GMAudit.auditGMAction(activeChar.getName(), command, target, "");

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		String val = "";
		if (st.countTokens() >= 1)
		{
			val = st.nextToken();
		}

		if (actualCommand.equalsIgnoreCase("admin_add_level"))
		{
			try
			{
				if (targetChar instanceof L2Playable)
				{
					((L2Playable) targetChar).getStat().addLevel(Byte.parseByte(val));
				}
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Wrong Number Format.");
			}
		}
		else if (actualCommand.equalsIgnoreCase("admin_set_level"))
		{
			try
			{
				if (targetChar == null || !(targetChar instanceof L2Playable))
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					return false;
				}

				final L2Playable targetPlayer = (L2Playable) targetChar;

				final byte lvl = Byte.parseByte(val);
				int max_level = ExperienceData.getInstance().getMaxLevel();

				if (targetChar instanceof L2PcInstance && ((L2PcInstance) targetPlayer).isSubClassActive())
				{
					max_level = Config.MAX_SUBCLASS_LEVEL;
				}

				if (lvl >= 1 && lvl <= max_level)
				{
					final long pXp = targetPlayer.getStat().getExp();
					final long tXp = ExperienceData.getInstance().getExpForLevel(lvl);

					if (pXp > tXp)
					{
						targetPlayer.getStat().removeExpAndSp(pXp - tXp, 0);
					}
					else if (pXp < tXp)
					{
						targetPlayer.getStat().addExpAndSp(tXp - pXp, 0);
					}
				}
				else
				{
					activeChar.sendMessage("You must specify level between 1 and " + ExperienceData.getInstance().getMaxLevel() + ".");
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("You must specify level between 1 and " + ExperienceData.getInstance().getMaxLevel() + ".");
				return false;
			}
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}