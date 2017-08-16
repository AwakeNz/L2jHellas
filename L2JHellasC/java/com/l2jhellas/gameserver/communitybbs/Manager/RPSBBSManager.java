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
package com.l2jhellas.gameserver.communitybbs.Manager;

import java.util.logging.Level;
import java.util.logging.Logger;

import Extensions.RankSystem.RPSHtmlCommunityBoard;

import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Masterio
 */
public class RPSBBSManager extends BaseBBSManager
{
	public static final Logger log = Logger.getLogger(RPSBBSManager.class.getSimpleName());

	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
			if (command.startsWith("_bbsrps:"))
			{
				int page = 0;
				try
				{
					page = Integer.parseInt(command.split(":", 2)[1].trim());
				}
				catch (Exception e)
				{
					log.log(Level.WARNING, e.getMessage());
					page = 0;
				}

				separateAndSend(RPSHtmlCommunityBoard.getPage(activeChar, page), activeChar);
			}
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		// 
	}

	private static RPSBBSManager _instance = new RPSBBSManager();

	public static RPSBBSManager getInstance()
	{
		return _instance;
	}
}
