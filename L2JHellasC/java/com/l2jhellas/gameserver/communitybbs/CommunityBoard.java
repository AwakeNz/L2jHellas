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
package com.l2jhellas.gameserver.communitybbs;

import java.util.StringTokenizer;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.communitybbs.Manager.ClanBBSManager;
import com.l2jhellas.gameserver.communitybbs.Manager.ClassBBSManager;
import com.l2jhellas.gameserver.communitybbs.Manager.PostBBSManager;
import com.l2jhellas.gameserver.communitybbs.Manager.RPSBBSManager;
import com.l2jhellas.gameserver.communitybbs.Manager.RegionBBSManager;
import com.l2jhellas.gameserver.communitybbs.Manager.ShopBBSManager;
import com.l2jhellas.gameserver.communitybbs.Manager.TopBBSManager;
import com.l2jhellas.gameserver.communitybbs.Manager.TopicBBSManager;
import com.l2jhellas.gameserver.datatables.xml.MultisellData;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.L2GameClient;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;

public class CommunityBoard
{
	private static CommunityBoard _instance;

	private CommunityBoard()
	{
	}

	public static CommunityBoard getInstance()
	{
		if (_instance == null)
		{
			_instance = new CommunityBoard();
		}

		return _instance;
	}

	// Protections
	public boolean checkPlayerConditions(L2PcInstance player)
	{
		if (player.isInOlympiadMode())
		{
			player.sendMessage("CommunityBoard use is prohibited at the Olympiad");
			return false;
		}
		if (player.isFlying() || player.isMounted())
		{
			player.sendMessage("CommunityBoard use is prohibited at while flying or mounted!");
			return false;
		}
		if (player.inObserverMode())
		{
			player.sendMessage("CommunityBoard use is prohibited in ObserveMode!");
			return false;
		}
		if (player.isAlikeDead() || player.isDead())
		{
			player.sendMessage("CommunityBoard use is prohibited While Dead");
			return false;
		}
		if (player.getKarma() > 0)
		{
			player.sendMessage("CommunityBoard use is prohibited with negative Karma!");
			return false;
		}
		if (player.isInCombat())
		{
			player.sendMessage("CommunityBoard use is prohibited in Combat!");
			return false;
		}
		if (player.isCastingNow())
		{
			player.sendMessage("CommunityBoard use is prohibited while Casting!");
			return false;
		}
		if (player.isAttackingNow())
		{
			player.sendMessage("CommunityBoard use is prohibited while Attacking!");
			return false;
		}
		if (player.isInDuel())
		{
			player.sendMessage("CommunityBoard use is prohibited while Playing Duel!");
			return false;
		}
		if (player.isFishing())
		{
			player.sendMessage("CommunityBoard use is prohibited while Fishing!");
			return false;
		}
		if (player.isInStoreMode())
		{
			player.sendMessage("CommunityBoard use is prohibited in StoreMode!");
			return false;
		}
		if (player.isInFunEvent() || player._inEventCTF || player._inEventDM || player._inEventTvT || player._inEventVIP)
		{
			player.sendMessage("CommunityBoard use is prohibited while you are in an event!");
			return false;
		}
		if (player.isInJail() || player.isCursedWeaponEquiped() || player.isFlying() || player.isInBoat() || player.isProcessingTransaction() || player.isStunned())
		{
			player.sendMessage("CommunityBoard use is prohibited right now!");
			return false;
		}
		return true;
	}

	public void handleCommands(L2GameClient client, String command)
	{
		L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
			return;

		if (!checkPlayerConditions(activeChar))
		{
			return;
		}

		if (Config.COMMUNITY_TYPE.equals("full"))
		{
			if (command.startsWith("_bbsclan"))
			{
				ClanBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else if (command.startsWith("_bbsmemo"))
			{
				TopicBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else if (command.startsWith("_bbstopics"))
			{
				TopicBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else if (command.startsWith("_bbsposts"))
			{
				PostBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else if (command.startsWith("_bbstop"))
			{
				TopBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else if (command.startsWith("_bbshome"))
			{
				TopBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else if (command.startsWith("_bbsloc"))
			{
				RegionBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else if (command.startsWith("_bbsclass"))
			{
				ClassBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else if (command.startsWith("_bbshop"))
			{
				ShopBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else if (command.startsWith("_bbsmultisell;"))
			{
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				ShopBBSManager.getInstance().parsecmd("_bbsshop;" + st.nextToken(), activeChar);
				MultisellData.getInstance().SeparateAndSend(Integer.parseInt(st.nextToken()), activeChar, false, 0);
			}
			else if(command.startsWith("_bbsrps"))
			{ 
				   RPSBBSManager.getInstance().parsecmd(command, activeChar);
			}
		}
		else if (Config.COMMUNITY_TYPE.equals("old"))
		{
			RegionBBSManager.getInstance().parsecmd(command, activeChar);
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.CB_OFFLINE);
		}
		
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}

	/**
	 * @param client
	 * @param url
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @param arg5
	 */
	public void handleWriteCommands(L2GameClient client, String url, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
			return;

		if (Config.COMMUNITY_TYPE.equals("Full"))
		{
			if (url.equals("Topic"))
			{
				TopicBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
			}
			else if (url.equals("Post"))
			{
				PostBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
			}
			else if (url.equals("Region"))
			{
				RegionBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
			}
			else if (url.equals("Notice"))
			{
				ClanBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
			}
		}
		else if (Config.COMMUNITY_TYPE.equals("old"))
		{
			RegionBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
		}
	}
}