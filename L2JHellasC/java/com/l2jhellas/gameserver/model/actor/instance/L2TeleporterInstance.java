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
package com.l2jhellas.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.datatables.xml.MapRegionTable;
import com.l2jhellas.gameserver.datatables.xml.TeleportLocationData;
import com.l2jhellas.gameserver.instancemanager.CastleManager;
import com.l2jhellas.gameserver.instancemanager.SiegeManager;
import com.l2jhellas.gameserver.model.L2TeleportLocation;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;

/**
 * @author NightMarez
 */
public final class L2TeleporterInstance extends L2NpcInstance
{
	private static final int COND_ALL_FALSE = 0;
	private static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	private static final int COND_OWNER = 2;
	private static final int COND_REGULAR = 3;

	/**
	 * @param template
	 */
	public L2TeleporterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);

		int condition = validateCondition(player);

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		if (actualCommand.equalsIgnoreCase("goto"))
		{
			int npcId = getTemplate().npcId;

			switch (npcId)
			{
				case 31095: //
				case 31096: //
				case 31097: //
				case 31098: // Enter Necropolises
				case 31099: //
				case 31100: //
				case 31101: //
				case 31102: //

				case 31114: //
				case 31115: //
				case 31116: // Enter Catacombs
				case 31117: //
				case 31118: //
				case 31119: //
					player.setIsIn7sDungeon(true);
				break;
				case 31103: //
				case 31104: //
				case 31105: //
				case 31106: // Exit Necropolises
				case 31107: //
				case 31108: //
				case 31109: //
				case 31110: //

				case 31120: //
				case 31121: //
				case 31122: // Exit Catacombs
				case 31123: //
				case 31124: //
				case 31125: //
					player.setIsIn7sDungeon(false);
				break;
			}

			if (st.countTokens() <= 0)
			{
				return;
			}
			int whereTo = Integer.parseInt(st.nextToken());
			if (condition == COND_REGULAR)
			{
				doTeleport(player, whereTo);
				return;
			}
			else if (condition == COND_OWNER)
			{
				int minPrivilegeLevel = 0; // NOTE: Replace 0 with highest level when privilege level is implemented
				if (st.countTokens() >= 1)
				{
					minPrivilegeLevel = Integer.parseInt(st.nextToken());
				}
				if (10 >= minPrivilegeLevel) // NOTE: Replace 10 with privilege level of player
					doTeleport(player, whereTo);
				else
					player.sendMessage("You don't have the sufficient access level to teleport there.");
				return;
			}
		}

		super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}

		return "data/html/teleporter/" + pom + ".htm";
	}

	@Override
	public void showChatWindow(L2PcInstance player)
	{
		String filename = "data/html/teleporter/castleteleporter-no.htm";

		int condition = validateCondition(player);
		if (condition == COND_REGULAR)
		{
			super.showChatWindow(player);
			return;
		}
		else if (condition > COND_ALL_FALSE)
		{
			if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
				filename = "data/html/teleporter/castleteleporter-busy.htm"; // Busy because of siege
			else if (condition == COND_OWNER) // Clan owns castle
				filename = getHtmlPath(getNpcId(), 0); // Owner message window
		}

		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	private void doTeleport(L2PcInstance player, int val)
	{
		L2TeleportLocation list = TeleportLocationData.getInstance().getTemplate(val);
		if (list != null)
		{
			int payType = 0;
			if (player.getLevel() < 41)
				payType = 1;
			
			if (player.isinZodiac)
			{
				player.sendMessage("You cannot teleport while in Zodiac Event.");
				return;
			}
			else if (player.isAlikeDead() || player.isDead())
				return;
			//you cannot teleport to village that is in siege
			if (SiegeManager.getInstance().getSiege(list.getLocX(), list.getLocY(), list.getLocZ()) != null)
			{
				player.sendPacket(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE);
				return;
			}
			else if (MapRegionTable.townHasCastleInSiege(list.getLocX(), list.getLocY()))
			{
				player.sendPacket(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE);
				return;
			}
			else if (!Config.ALT_GAME_FLAGGED_PLAYER_CAN_USE_GK && player.getPvpFlag() > 0)
			{
				player.sendMessage("You Are Not Able To Run Away From PvP!");
				return;
			}
			else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && player.getKarma() > 0) //karma
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
				sm.addString("Go away, you're not welcome here.");
				player.sendPacket(sm);
				return;
			}
			else if (list.getIsForNoble() && !player.isNoble())
			{
				String filename = "data/html/teleporter/nobleteleporter-no.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (player.getLevel() < list.getMinLevel())
			{
				String filename = "data/html/teleporter/" + getNpcId() + "-min-level.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", getName());
				html.replace("%playername%", player.getName());
				html.replace("%minLevel%", String.valueOf(list.getMinLevel()));
				player.sendPacket(html);
				return;
			}
			else if (player.getLevel() > list.getMaxLevel())
			{
				String filename = "data/html/teleporter/" + getNpcId() + "-max-level.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", getName());
				html.replace("%playername%", player.getName());
				html.replace("%maxLevel%", String.valueOf(list.getMaxLevel()));
				player.sendPacket(html);
				return;
			}
			else if (!player.isGM() && list.getIsForGM())
			{
				String filename = "data/html/teleporter/" + getNpcId() + "-gmonly.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", getName());
				html.replace("%playername%", player.getName());
				player.sendPacket(html);
			}
			else if (!list.getIsForNoble() && (Config.ALT_GAME_FREE_TELEPORT || player.destroyItemsByList("Teleport "+(list.getIsForNoble() ? " nobless" : ""), list.getItemsList(), this, true, payType)))
			{
				if (Config.DEBUG)
					_log.fine("Teleporting player " + player.getName() + " to new location: " + list.getLocX() + ":" + list.getLocY() + ":" + list.getLocZ());
				player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ(), true);
			}
			else if (list.getIsForNoble() && (Config.ALT_GAME_FREE_TELEPORT || player.destroyItemsByList("Teleport "+(list.getIsForNoble() ? " nobless" : ""), list.getItemsList(), this, true, payType)))
			{
				if (Config.DEBUG)
					_log.fine("Teleporting player " + player.getName() + " to new location: " + list.getLocX() + ":" + list.getLocY() + ":" + list.getLocZ());
				player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ(), true);
			}
		}
		else
		{
			_log.warning(L2TeleporterInstance.class.getName() + ":No teleport destination with id:" + val);
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	private int validateCondition(L2PcInstance player)
	{
		if (CastleManager.getInstance().getCastleIndex(this) < 0) // Teleporter isn't on castle ground
			return COND_REGULAR; // Regular access
		else if (getCastle().getSiege().getIsInProgress()) // Teleporter is on castle ground and siege is in progress
			return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
		else if (player.getClan() != null) // Teleporter is on castle ground and player is in a clan
		{
			if (getCastle().getOwnerId() == player.getClanId()) // Clan owns castle
				return COND_OWNER; // Owner
		}
		return COND_ALL_FALSE;
	}
}