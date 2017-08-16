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
package com.l2jhellas.gameserver.network.clientpackets;

import java.util.logging.Logger;

import Extensions.RaidEvent.L2RaidEvent;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.datatables.xml.MapRegionTable;
import com.l2jhellas.gameserver.instancemanager.CastleManager;
import com.l2jhellas.gameserver.instancemanager.ClanHallManager;
import com.l2jhellas.gameserver.model.L2SiegeClan;
import com.l2jhellas.gameserver.model.Location;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.entity.Castle;
import com.l2jhellas.gameserver.model.entity.ClanHall;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.Revive;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.util.IllegalPlayerAction;
import com.l2jhellas.util.Util;

public final class RequestRestartPoint extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestRestartPoint.class.getName());
	private static final String _C__6d_REQUESTRESTARTPOINT = "[C] 6d RequestRestartPoint";
	
	protected int _requestedPointType;
	protected boolean _continuation;
	
	@Override
	protected void readImpl()
	{
		_requestedPointType = readD();
	}
	
	class DeathTask implements Runnable
	{
		L2PcInstance activeChar;
		
		DeathTask(L2PcInstance _activeChar)
		{
			activeChar = _activeChar;
		}
		
		@Override
		public void run()
		{
			try
			{
				Location loc = null;
				Castle castle = null;
				
				if (activeChar.isInJail())
					_requestedPointType = 27;
				else if (activeChar.isFestivalParticipant())
					_requestedPointType = 4;
				
				if (activeChar.getKarma() > 0)
					loc = new Location(17836, 170178, -3507); //Should teleport to floran vilage
				else
				{
					switch (_requestedPointType)
					{
						case 1: // to clanhall
							if (activeChar.getClan().hasHideout() == 0)
							{
								// cheater
								activeChar.sendMessage("You may not use this respawn point!");
								Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
								return;
							}
							loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.ClanHall);
							
							if (ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()) != null && ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP) != null)
							{
								activeChar.restoreExp(ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP).getLvl());
							}
						break;
						
						case 2: // to castle
							Boolean isInDefense = false;
							castle = CastleManager.getInstance().getCastle(activeChar);
							if (castle != null && castle.getSiege().getIsInProgress())
							{
								// siege in progress
								if (castle.getSiege().checkIsDefender(activeChar.getClan()))
									isInDefense = true;
							}
							if (activeChar.getClan().hasCastle() == 0 && !isInDefense)
							{
								// cheater
								activeChar.sendMessage("You may not use this respawn point!");
								Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
								return;
							}
							loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.Castle);
						break;
						
						case 3: // to siege HQ
							L2SiegeClan siegeClan = null;
							castle = CastleManager.getInstance().getCastle(activeChar);
							
							if (castle != null && castle.getSiege().getIsInProgress())
								siegeClan = castle.getSiege().getAttackerClan(activeChar.getClan());
							
							if (siegeClan == null || siegeClan.getFlag().size() == 0)
							{
								// cheater
								activeChar.sendMessage("You may not use this respawn point!");
								Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
								return;
							}
							loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.SiegeFlag);
						break;
						
						case 4: // Fixed or Player is a festival participant
							if (!activeChar.isGM() && !activeChar.isFestivalParticipant())
							{
								// cheater
								activeChar.sendMessage("You may not use this respawn point!");
								Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
								return;
							}
							loc = new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ()); // spawn them where they died
						break;
						
						case 27: // to jail
							if (!activeChar.isInJail())
								return;
							loc = new Location(-114356, -249645, -2984);
						break;
						
						default:
							if (Config.ALT_RESPAWN_POINT)
								loc = new Location(Config.ALT_RESPAWN_POINT_X, Config.ALT_RESPAWN_POINT_Y, Config.ALT_RESPAWN_POINT_Z);
							else
								loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.Town);
						break;
					}
				}

				activeChar.setIsIn7sDungeon(false);					
				
				if (activeChar.isDead())
					activeChar.doRevive();	
				
				activeChar.teleToLocation(loc, true);
			}
			catch (Throwable e)
			{
				_log.severe(RequestRestartPoint.class.getName() + ": error");
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
			return;
		
		// SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessage.S1_S2);
		// sm2.addString("type:"+requestedPointType);
		// activeChar.sendPacket(sm2);
		
		if (activeChar.isFakeDeath())
		{
			activeChar.stopFakeDeath(null);
			activeChar.broadcastPacket(new Revive(activeChar));
			return;
		}
		else if (!activeChar.isAlikeDead())
		{
			_log.warning(RequestRestartPoint.class.getName() + ": Living player [" + activeChar.getName() + "] called RestartPointPacket! Ban this player!");
			return;
		}
		
		if (activeChar.inClanEvent || activeChar.inPartyEvent || activeChar.inSoloEvent)
		{
			activeChar.inClanEvent = false;
			activeChar.inPartyEvent = false;
			activeChar.inSoloEvent = false;
			if (L2RaidEvent._eventType == 2)
			{
				if (L2RaidEvent._participatingPlayers.contains(activeChar))
					// Clear player from Event.
					L2RaidEvent._participatingPlayers.remove(activeChar);
			}
			if (L2RaidEvent._eventType == 3)
			{
				if (activeChar.getParty() != null)
					activeChar.leaveParty();
				activeChar.sendMessage("You have been kicked from the party.");
			}
			activeChar.sendMessage("You've been erased from the event!");
			int num = L2RaidEvent._participatingPlayers.size();
			if (num > 0 && num != 1)
				num -= 1;
			else
				L2RaidEvent.hardFinish();
		}
		
		Castle castle = CastleManager.getInstance().getCastle(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		if (castle != null && castle.getSiege().getIsInProgress())
		{
			// DeathFinalizer df = new DeathFinalizer(10000);
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
			if (activeChar.getClan() != null && castle.getSiege().checkIsAttacker(activeChar.getClan()))
			{
				// Schedule respawn delay for attacker
				ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), castle.getSiege().getAttackerRespawnDelay());
				sm.addString("You will be re-spawned in " + castle.getSiege().getAttackerRespawnDelay() / 1000 + " seconds.");
				activeChar.sendPacket(sm);
			}
			else
			{
				// Schedule respawn delay for defender with penalty for CT lose
				ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), castle.getSiege().getDefenderRespawnDelay());
				sm.addString("You will be re-spawned in " + castle.getSiege().getDefenderRespawnDelay() / 1000 + " seconds.");
				activeChar.sendPacket(sm);
			}
			sm = null;
			return;
		}
		
		new DeathTask(activeChar).run();
	}
	
	@Override
	public String getType()
	{
		return _C__6d_REQUESTRESTARTPOINT;
	}
}