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
package com.l2jhellas.gameserver.model.entity.olympiad;

import java.util.logging.Logger;

import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2Party;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.Location;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Summon;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PetInstance;
import com.l2jhellas.gameserver.model.zone.type.L2OlympiadStadiumZone;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.ExOlympiadMode;
import com.l2jhellas.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jhellas.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.skills.HeroSkillTable;
import com.l2jhellas.gameserver.skills.SkillTable;

public abstract class AbstractOlympiadGame
{
	protected static final Logger _log = Logger.getLogger(AbstractOlympiadGame.class.getName());
	
	protected static final String POINTS = "olympiad_points";
	protected static final String COMP_DONE = "competitions_done";
	protected static final String COMP_WON = "competitions_won";
	protected static final String COMP_LOST = "competitions_lost";
	protected static final String COMP_DRAWN = "competitions_drawn";
	private static int[] _playerLocation;
	
	protected long _startTime = 0;
	protected boolean _aborted = false;
	protected final int _stadiumID;
	
	protected AbstractOlympiadGame(int id)
	{
		_stadiumID = id;
	}
	
	public final boolean isAborted()
	{
		return _aborted;
	}
	
	public final int getStadiumId()
	{
		return _stadiumID;
	}
	
	protected boolean makeCompetitionStart()
	{
		_startTime = System.currentTimeMillis();
		return !_aborted;
	}
	
	protected final void addPointsToParticipant(Participant par, int points)
	{
		par.updateStat(POINTS, points);
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_GAINED_S2_OLYMPIAD_POINTS);
		sm.addString(par.name);
		sm.addNumber(points);
		broadcastPacket(sm);
	}
	
	protected final void removePointsFromParticipant(Participant par, int points)
	{
		par.updateStat(POINTS, -points);
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_LOST_S2_OLYMPIAD_POINTS);
		sm.addString(par.name);
		sm.addNumber(points);
		broadcastPacket(sm);
	}
	
	/**
	 * Return null if player passed all checks or broadcast the reason to opponent.
	 * @param player to check.
	 * @return null or reason.
	 */
	protected static SystemMessage checkDefaulted(L2PcInstance player)
	{
		if (player == null || player.isOnline()==0)
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME);
		
		if (player.getClient() == null || player.getClient().isDetached())
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME);
		
		// safety precautions
		if (player.inObserverMode())
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		
		if (player.isDead())
		{
			player.sendPacket(SystemMessageId.CANNOT_PARTICIPATE_OLYMPIAD_WHILE_DEAD);
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.isSubClassActive())
		{
			player.sendPacket(SystemMessageId.SINCE_YOU_HAVE_CHANGED_YOUR_CLASS_INTO_A_SUB_JOB_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.isCursedWeaponEquiped())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_JOIN_OLYMPIAD_POSSESSING_S1).addItemName(player.getCursedWeaponEquipedId()));
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}

		
		return null;
	}
	
	protected static final boolean portPlayerToArena(Participant par, Location loc, int id)
	{
		final L2PcInstance player = par.player;
		if (player == null || player.isOnline()==0)
			return false;
		
		_playerLocation = new int[3];
		
		try
		{

			if(player.isDead())
				player.doRevive();
			
			if(player.isInvul())
				player.setIsInvul(false);
			
			if(player.getAppearance().getInvisible())
				player.getAppearance().setVisible();
			
			_playerLocation[0] = player.getX();
			_playerLocation[1] = player.getY();
			_playerLocation[2] = player.getZ();
			
			player.setXYZ(player.getX(), player.getY(), player.getZ());
			
			player.standUp();
			player.setTarget(null);
			
			player.setOlympiadGameId(id);
			player.setIsInOlympiadMode(true);
			player.setIsOlympiadStart(false);
			player.setOlympiadSide(par.side);
			player.teleToLocation(loc, false);
			player.sendPacket(new ExOlympiadMode(2));
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		catch (Exception e)
		{
			_log.warning(AbstractOlympiadGame.class.getName() + ": error");
			return false;
		}
		return true;
	}
	
	protected static final void removals(L2PcInstance player, boolean removeParty)
	{
		try
		{
			if (player == null)
				return;
			
			// Remove Buffs
			player.stopAllEffects();
			
			// Remove Clan Skills
			if (player.getClan() != null)
			{
				for (L2Skill skill : player.getClan().getAllSkills())
					player.removeSkill(skill, false);
			}
			
			// Abort casting if player casting
			player.abortAttack();
			player.abortCast();
			
			// Force the character to be visible
			player.getAppearance().setVisible();
			
			// Remove Hero Skills
			if (player.isHero())
			{
				for (L2Skill skill : HeroSkillTable.GetHeroSkills())
					player.removeSkill(skill, false);
			}
			
			// Heal Player fully
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			
			// Remove Summon's Buffs
			final L2Summon summon = player.getPet();
			if (summon != null)
			{
				summon.stopAllEffects();
				summon.abortAttack();
				summon.abortCast();
				
				if (summon instanceof L2PetInstance)
					summon.unSummon(player);
			}
			
			// stop any cubic that has been given by other player.
			player.getCubics().clear();
			
			// Remove player from his party
			if (removeParty)
			{
				final L2Party party = player.getParty();
				if (party != null)
					party.removePartyMember(player);
			}
			
			player.checkItemRestriction();
			
			// Remove shot automation
			player.disableAutoShotsAll();
			
			// Discharge any active shots
			L2ItemInstance item = player.getActiveWeaponInstance();
			if (item != null)
				item.setChargedNoNe();
			
			player.sendSkillList();
		}
		catch (Exception e)
		{
			_log.warning(AbstractOlympiadGame.class.getName() + ": error");
		}
	}
	
	/**
	 * Buff and heal the player. WW2 for fighter/mage + haste 1 if fighter.
	 * @param player : the happy benefactor.
	 */
	protected static final void buffAndHealPlayer(L2PcInstance player)
	{
		L2Skill skill = SkillTable.getInstance().getInfo(1204, 2); // Windwalk 2
		if (skill != null)
		{
			skill.getEffects(player, player);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(1204));
		}
		
		if (!player.isMageClass())
		{
			skill = SkillTable.getInstance().getInfo(1086, 1); // Haste 1
			if (skill != null)
			{
				skill.getEffects(player, player);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(1086));
			}
		}
		
		// Heal Player fully
		player.setCurrentCp(player.getMaxCp());
		player.setCurrentHp(player.getMaxHp());
		player.setCurrentMp(player.getMaxMp());
	}
	
	protected static final void cleanEffects(L2PcInstance player)
	{
		try
		{
			// prevent players kill each other
			player.setIsOlympiadStart(false);
			player.setTarget(null);
			player.abortAttack();
			player.abortCast();
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			
			if (player.isDead())
				player.doRevive();
			
			final L2Summon summon = player.getPet();
			if (summon != null && !summon.isDead())
			{
				summon.setTarget(null);
				summon.abortAttack();
				summon.abortCast();
				summon.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}
			
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			player.getStatus().startHpMpRegeneration();
		}
		catch (Exception e)
		{
			_log.warning(AbstractOlympiadGame.class.getName() + ": error");
		}
	}
	
	protected static final void playerStatusBack(L2PcInstance player)
	{
		try
		{
			player.standUp();
			
			player.setIsInOlympiadMode(false);
			player.setIsOlympiadStart(false);
			player.setOlympiadSide(-1);
			player.setOlympiadGameId(-1);
			player.sendPacket(new ExOlympiadMode(0));
			
			player.stopAllEffects();
			
			final L2Summon summon = player.getPet();
			if (summon != null && !summon.isDead())
				summon.stopAllEffects();
			
			// Add Clan Skills
			if (player.getClan() != null)
			{
				player.getClan().addSkillEffects(player);
				
				// heal again after adding clan skills
				player.setCurrentCp(player.getMaxCp());
				player.setCurrentHp(player.getMaxHp());
				player.setCurrentMp(player.getMaxMp());
			}
			
			// Add Hero Skills
			if (player.isHero())
			{
				for (L2Skill skill : HeroSkillTable.GetHeroSkills())
					player.addSkill(skill, false);				
			}
			player.sendSkillList();
		}
		catch (Exception e)
		{
			_log.warning(AbstractOlympiadGame.class.getName() + ": error");
		}
	}
	
	protected static final void portPlayerBack(L2PcInstance player)
	{
		if (player == null)
			return;
		
		if (player.getX() == 0 && player.getY() == 0)
			return;
		
		player.teleToLocation(_playerLocation[0], _playerLocation[1], _playerLocation[2], true);
	}
	
	public static final void rewardParticipant(L2PcInstance player, int[][] reward)
	{
		if (player == null || player.isOnline()==0 || reward == null)
			return;
		
		try
		{
			SystemMessage sm;
			L2ItemInstance item;
			final InventoryUpdate iu = new InventoryUpdate();
			for (int[] it : reward)
			{
				if (it == null || it.length != 2)
					continue;
				
				item = player.getInventory().addItem("Olympiad", it[0], it[1], player, null);
				if (item == null)
					continue;
				
				iu.addModifiedItem(item);
				sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
				sm.addItemName(it[0]);
				sm.addNumber(it[1]);
				player.sendPacket(sm);
			}
			player.sendPacket(iu);
		}
		catch (Exception e)
		{
			_log.warning(AbstractOlympiadGame.class.getName() + ": error");
		}
	}
	
	public abstract CompetitionType getType();
	
	public abstract String[] getPlayerNames();
	
	public abstract boolean containsParticipant(int playerId);
	
	public abstract void sendOlympiadInfo(L2Character player);
	
	public abstract void broadcastOlympiadInfo(L2OlympiadStadiumZone stadium);
	
	protected abstract void broadcastPacket(L2GameServerPacket packet);
	
	protected abstract boolean checkDefaulted();
	
	protected abstract void hpsShow();
	
	protected abstract void removals();
	
	protected abstract void buffAndHealPlayers();
	
	protected abstract boolean portPlayersToArena(Location location);
	
	protected abstract void cleanEffects();
	
	protected abstract void portPlayersBack();
	
	protected abstract void playersStatusBack();
	
	protected abstract void clearPlayers();
	
	protected abstract void handleDisconnect(L2PcInstance player);
	
	protected abstract void resetDamage();
	
	protected abstract void addDamage(L2PcInstance player, int damage);
	
	protected abstract boolean checkBattleStatus();
	
	protected abstract boolean haveWinner();
	
	protected abstract void validateWinner(L2OlympiadStadiumZone stadium);
	
	protected abstract int getDivider();
	
	protected abstract int[][] getReward();
}