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
package com.l2jhellas.shield.antibot;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Boorinio for l2jhellas
 */
public class AntiAfk
{
	public static void getInstance()
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
		{
			@Override
			public void run()
			{
				antiafksystem();
			}
		}, 60 * 1000 * 10);
	}

	static void antiafksystem()
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
		{
			player.botx = player.getX();
			player.boty = player.getY();
			player.botz = player.getZ();
		}
		waitSecs(60 * Config.MINUTES_AFK_PLAYERS);
		for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
		{
			if (player.botx == player.getX() && player.boty == player.getY() && player.botz == player.getZ() && !player.isGM())
			{
				player.sendMessage("BB bot or afk player!");
				player.closeNetConnection(true);
			}
		}
		waitSecs(60 * Config.MINUTES_AFK_PLAYERS);
		antiafksystem();
	}

	public static void waitSecs(int i)
	{
		try
		{
			Thread.sleep(i * 1000);
		}
		catch (InterruptedException ie)
		{
			ie.printStackTrace();
		}
	}
}