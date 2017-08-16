/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jhellas.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import Extensions.Balancer.Balancer;
import Extensions.Balancer.BalancerEdit;
import Extensions.RankSystem.RPSBypass;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.communitybbs.CommunityBoard;
import com.l2jhellas.gameserver.datatables.xml.AdminData;
import com.l2jhellas.gameserver.handler.AdminCommandHandler;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.L2CharPosition;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.instance.L2AccountManagerInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2ClassMasterInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2OlympiadManagerInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.entity.Hero;
import com.l2jhellas.gameserver.model.entity.L2Event;
import com.l2jhellas.gameserver.model.entity.engines.CTF;
import com.l2jhellas.gameserver.model.entity.engines.DM;
import com.l2jhellas.gameserver.model.entity.engines.TvT;
import com.l2jhellas.gameserver.model.entity.engines.ZodiacMain;
import com.l2jhellas.gameserver.model.entity.olympiad.OlympiadGameManager;
import com.l2jhellas.gameserver.model.entity.olympiad.OlympiadGameTask;
import com.l2jhellas.gameserver.model.entity.olympiad.OlympiadManager;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jhellas.logs.GMAudit;
import com.l2jhellas.util.database.L2DatabaseFactory;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestBypassToServer.class.getName());
	private static final String _C__21_REQUESTBYPASSTOSERVER = "[C] 21 RequestBypassToServer";

	private String _command;

	/**
	 * @param decrypt
	 */
	@Override
	protected void readImpl()
	{
		_command = readS();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null || _command.isEmpty())
			return;

		if (!activeChar.getAntiFlood().getServerBypass().tryPerformAction(_command) && !activeChar.isGM())
			return;

		try
		{
			if (_command.startsWith("admin_") && activeChar.isGM())
			{
				String command = _command.split(" ")[0];

				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(command);

				if (ach == null)
				{
					if (activeChar.isGM())
					{
						activeChar.sendMessage("The command " + command.substring(6) + " doesn't exist.");
					}

					_log.warning(RequestBypassToServer.class.getName() + ": No handler registered for admin command '" + command + "'");
					return;
				}

				if (!AdminData.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access rights to use this command.");
					_log.warning(RequestBypassToServer.class.getName() + ": "+activeChar.getName() + " tried to use admin command " + command + " without proper Access Level.");
					return;
				}

				if (Config.GMAUDIT)
				{
					GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", _command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"));
				}

				ach.useAdminCommand(_command, activeChar);
			}
			else if (_command.equals("come_here") && activeChar.isGM())
			{
				comeHere(activeChar);
			}
			else if (_command.startsWith("SecondAnswer"))
			{
				activeChar.sendMessage("You passed our vertification system");
				activeChar.PassedProt = true;
			}
			else if (_command.startsWith("FirstAnswer"))
			{
				activeChar.sendMessage("You didn't pass our vertification system you will be kicked within 2 minutes!");
				activeChar.PassedProt = false;
			}
			else if (_command.startsWith("player_help "))
			{
				playerHelp(activeChar, _command.substring(12));
			}
			else if (ZodiacMain.voting && _command.startsWith("PeloponnesianWar") && !ZodiacMain.HasVoted(activeChar))
			{
				activeChar.sendMessage("You have voted for PeloponnesianWar!");
				ZodiacMain.count[0]++;
				ZodiacMain.showFinalWindow(activeChar);
				ZodiacMain.AddVotedPlayer(activeChar);
			}
			else if (ZodiacMain.voting && _command.startsWith("CaptureThem") && !ZodiacMain.HasVoted(activeChar))
			{
				activeChar.sendMessage("You have voted for CaptureThem!");
				ZodiacMain.count[1]++;
				ZodiacMain.showFinalWindow(activeChar);
				ZodiacMain.AddVotedPlayer(activeChar);
			}
			else if (ZodiacMain.voting && _command.startsWith("CastleWars") && !ZodiacMain.HasVoted(activeChar))
			{
				activeChar.sendMessage("You have voted for CastleWars!");
				ZodiacMain.count[2]++;
				ZodiacMain.showFinalWindow(activeChar);
				ZodiacMain.AddVotedPlayer(activeChar);
			}
			else if(ZodiacMain.voting && _command.startsWith("ProtectTheLdr") && !ZodiacMain.HasVoted(activeChar))
			{
				activeChar.sendMessage("You have voted for ProtectTheLeader!");
				ZodiacMain.count[3]++;
				ZodiacMain.showFinalWindow(activeChar);
				ZodiacMain.AddVotedPlayer(activeChar);
			}
			else if (ZodiacMain.voting && _command.startsWith("TreasureChest") && !ZodiacMain.HasVoted(activeChar))
			{
				activeChar.sendMessage("You have voted for TreasureChest!");
				ZodiacMain.count[4]++;
				ZodiacMain.showFinalWindow(activeChar);
				ZodiacMain.AddVotedPlayer(activeChar);
			}
			else if (ZodiacMain.voting && _command.startsWith("ChaosEvent") && !ZodiacMain.HasVoted(activeChar))
			{
				activeChar.sendMessage("You have voted for Chaos Event!");
				ZodiacMain.count[5]++;
				ZodiacMain.showFinalWindow(activeChar);
				ZodiacMain.AddVotedPlayer(activeChar);
			}
			else if (ZodiacMain.voting && _command.startsWith("Challenge") && !ZodiacMain.HasVoted(activeChar))
			{
				activeChar.sendMessage("You have voted for Challenge Event!");
				ZodiacMain.count[6]++;
				ZodiacMain.showFinalWindow(activeChar);
				ZodiacMain.AddVotedPlayer(activeChar);
			}	
			else if (_command.startsWith("sendMsg"))
			{ // Message System By Pauler

				StringTokenizer st = new StringTokenizer(_command);

				st.nextToken();

				String to;
				String title;
				String message = "";;

				if (st.hasMoreTokens())
				{
					to = st.nextToken();
				}
				else
					return;

				if (st.hasMoreTokens())
				{
					title = st.nextToken();
				}
				else
					return;

				while (st.hasMoreTokens())
				{
					message = message + st.nextToken() + " ";
				}

				if (to.equalsIgnoreCase(activeChar.getName()))
				{
					activeChar.sendMessage("You cannot send a message to yourself.");
					return;
				}

				if (to.equalsIgnoreCase("") || message.equalsIgnoreCase("") || to == null)
				{
					activeChar.sendMessage("You have to fill all the fields.");
					return;
				}

				if (title.equalsIgnoreCase("") || title == null)
				{
					title = "(No Subject)";
				}

				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement statement = con.prepareStatement("INSERT INTO mails VALUES ('0',?,?,?,?)");
					statement.setString(1, activeChar.getName().toLowerCase());
					statement.setString(2, to);
					statement.setString(3, title);
					statement.setString(4, message);
					statement.execute();
					statement.close();

					activeChar.sendMessage("Your message has been sent.");
				}
				catch (Exception e)
				{
					_log.warning(RequestBypassToServer.class.getName() + ": could not send message");
					if (Config.DEVELOPER)
						e.printStackTrace();
				}
			}
			else if (_command.startsWith("delMsg"))
			{
				StringTokenizer st = new StringTokenizer(_command);
				st.nextToken();

				int messageId = Integer.parseInt(st.nextToken());
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement statement = con.prepareStatement("DELETE FROM mails WHERE id=?");
					statement.setInt(1, messageId);
					statement.execute();
					statement.close();
					activeChar.sendMessage("The message has been deleted.");
				}
				catch (Exception e)
				{
					_log.warning(RequestBypassToServer.class.getName() + ": could not delete message");
					if (Config.DEVELOPER)
						e.printStackTrace();
				}

			}
			else if (_command.startsWith("submitemail"))
			{
				try
				{
					String value = _command.substring(11);
					StringTokenizer s = new StringTokenizer(value, " ");
					String email1 = null;

					try
					{
						email1 = s.nextToken();

						try
						{
							try (Connection con = L2DatabaseFactory.getInstance().getConnection())
							{
								PreparedStatement statement = con.prepareStatement("UPDATE characters SET email=? WHERE obj_Id=?");
								statement.setString(1, email1);
								statement.setInt(2, activeChar.getObjectId());
								statement.execute();
								statement.close();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}

							activeChar.sendMessage("We successfully added your email " + email1 + " to our database");
							L2AccountManagerInstance.setHasSubEmail(activeChar);

						}
						catch (Exception e)
						{
							e.printStackTrace();
						}

					}
					catch (Exception e)
					{
						activeChar.sendMessage("Something went wrong.");
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Something went wrong.");
				}
			}
			else if (_command.startsWith("npc_"))
			{
				if (!activeChar.validateBypass(_command))
				{
					return;
				}
				int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
					id = _command.substring(4, endOfId);
				else
					id = _command.substring(4);
				
				try
				{
					L2Object object = L2World.getInstance().findObject(Integer.parseInt(id));

					if(object != null && object instanceof L2Npc && endOfId > 0 && activeChar.isInsideRadius(object, L2Npc.INTERACTION_DISTANCE, false, false) || ((Config.ALLOW_REMOTE_CLASS_MASTER) && (object instanceof L2ClassMasterInstance)))
					{
						((L2Npc) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					}
					
					if (_command.substring(endOfId + 1).startsWith("event_participate"))
					{
						L2Event.inscribePlayer(activeChar);
					}
					else if (_command.substring(endOfId + 1).startsWith("tvt_player_join "))
					{
						String teamName = _command.substring(endOfId + 1).substring(16);

						if (TvT._joining)
						{
							TvT.addPlayer(activeChar, teamName);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not join now!");
						}
					}
					else if (_command.substring(endOfId + 1).startsWith("tvt_player_leave"))
					{
						if (TvT._joining)
						{
							TvT.removePlayer(activeChar);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not leave now!");
						}
					}
					else if (_command.substring(endOfId + 1).startsWith("dmevent_player_join"))
					{
						if (DM._joining)
						{
							DM.addPlayer(activeChar);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not join now!");
						}
					}
					else if (_command.substring(endOfId + 1).startsWith("dmevent_player_leave"))
					{
						if (DM._joining)
						{
							DM.removePlayer(activeChar);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not leave now!");
						}
					}
					else if (_command.substring(endOfId + 1).startsWith("ctf_player_join "))
					{
						String teamName = _command.substring(endOfId + 1).substring(16);

						if (CTF._joining)
						{
							CTF.addPlayer(activeChar, teamName);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not join now!");
						}
					}
					else if (_command.substring(endOfId + 1).startsWith("ctf_player_leave"))
					{
						if (CTF._joining)
						{
							CTF.removePlayer(activeChar);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not leave now!");
						}
					}
				
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
				}
			}
			// Draw a Symbol
			else if (_command.equals("menu_select?ask=-16&reply=1"))
			{
				L2Object object = activeChar.getTarget();
				if (object instanceof L2Npc)
				{
					((L2Npc) object).onBypassFeedback(activeChar, _command);
				}
			}
			else if (_command.equals("menu_select?ask=-16&reply=2"))
			{
				L2Object object = activeChar.getTarget();
				if (object instanceof L2Npc)
				{
					((L2Npc) object).onBypassFeedback(activeChar, _command);
				}
			}
			// Navigate through Manor windows
			else if (_command.startsWith("manor_menu_select?"))
			{
				L2Object object = activeChar.getTarget();
				if (object instanceof L2Npc)
				{
					((L2Npc) object).onBypassFeedback(activeChar, _command);
				}
			}
			else if (_command.startsWith("bbs_"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("_bbs"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("Quest "))
			{
				if (!activeChar.validateBypass(_command))
					return;
				
				String[] str = _command.substring(6).trim().split(" ", 2);
				if (str.length == 1)
					activeChar.processQuestEvent(str[0], "");
				else
					activeChar.processQuestEvent(str[0], str[1]);
			}
			// Balancer: ->
			// -------------------------------------------------------------------------------
			else if (_command.startsWith("bp_balance") && activeChar.isGM())
			{
				String bp = _command.substring(11);
				StringTokenizer st = new StringTokenizer(bp);
				
				if (st.countTokens() != 1)
				{
					return;
				}
				
				int classId = Integer.parseInt(st.nextToken());
				
				Balancer.sendBalanceWindow(classId, activeChar);
			}
			
			else if (_command.startsWith("bp_add") && activeChar.isGM())
			{
				String bp = _command.substring(7);
				StringTokenizer st = new StringTokenizer(bp);
				
				if (st.countTokens() != 3)
				{
					return;
				}
				
				String stat = st.nextToken();
				int classId = Integer.parseInt(st.nextToken()),
					value = Integer.parseInt(st.nextToken());
				
				BalancerEdit.editStat(stat, classId, value, true);
				
				Balancer.sendBalanceWindow(classId, activeChar);
			}
			
			else if (_command.startsWith("bp_rem") && activeChar.isGM())
			{
				String bp = _command.substring(7);
				StringTokenizer st = new StringTokenizer(bp);
				
				if (st.countTokens() != 3)
				{
					return;
				}
				
				String stat = st.nextToken();
				int classId = Integer.parseInt(st.nextToken()),
					value = Integer.parseInt(st.nextToken());
				
				BalancerEdit.editStat(stat, classId, value, false);
				
				Balancer.sendBalanceWindow(classId, activeChar);
			}
			// Rank PvP System by Masterio --------------------------------------------
			else if(_command.startsWith("RPS."))
			{ 
				RPSBypass.executeCommand(activeChar, _command);
			}
			// ------------------------------------------------------------------------
			else if (_command.startsWith("_match"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroFights(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("_diary"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("arenachange")) // change
			{
				 
				final boolean isManager = activeChar.getTarget() instanceof L2OlympiadManagerInstance;
				if (!isManager)
				{
					// Without npc, command can be used only in observer mode on arena
					if (!activeChar.inObserverMode() || activeChar.isInOlympiadMode() || activeChar.getOlympiadGameId() < 0)
						return;
				}
				
				if (OlympiadManager.getInstance().isRegisteredInComp(activeChar))
				{
					activeChar.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
					return;
				}
                			
				final int arenaId = Integer.parseInt(_command.substring(12).trim());
				final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(arenaId);
				if (nextArena != null)
				{
                     nextArena.getZone().addSpectator(arenaId, activeChar);
					 return;
				}
			}

			// -------------------------------------------------------------------------------
		}
		catch (Exception e)
		{
			_log.warning(RequestBypassToServer.class.getSimpleName() + ": Bad RequestBypassToServer: ");
		}
	}

	/**
	 * @param client
	 */
	private void comeHere(L2PcInstance activeChar)
	{
		L2Object obj = activeChar.getTarget();
		if (obj == null)
			return;
		if (obj instanceof L2Npc)
		{
			L2Npc temp = (L2Npc) obj;
			temp.setTarget(activeChar);
			temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 0));
			// temp.moveTo(player.getX(),player.getY(), player.getZ(), 0 );
		}
	}
	private void playerHelp(L2PcInstance activeChar, String path)
	{
		if (path.indexOf("..") != -1)
			return;

		String filename = "data/html/help/" + path;
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile(filename);
		activeChar.sendPacket(html);
	}
	
	@Override
	public String getType()
	{
		return _C__21_REQUESTBYPASSTOSERVER;
	}
}