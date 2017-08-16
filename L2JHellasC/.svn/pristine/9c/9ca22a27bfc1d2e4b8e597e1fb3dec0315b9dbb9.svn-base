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
package com.l2jhellas.gameserver.ai;

import static com.l2jhellas.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static com.l2jhellas.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static com.l2jhellas.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;

import java.util.Collection;
import java.util.concurrent.Future;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.Territory;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.controllers.GameTimeController;
import com.l2jhellas.gameserver.datatables.xml.NpcData;
import com.l2jhellas.gameserver.geodata.GeoEngine;
import com.l2jhellas.gameserver.instancemanager.DimensionalRiftManager;
import com.l2jhellas.gameserver.model.L2CharPosition;
import com.l2jhellas.gameserver.model.L2Effect;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.L2SkillTargetType;
import com.l2jhellas.gameserver.model.L2SkillType;
import com.l2jhellas.gameserver.model.Location;
import com.l2jhellas.gameserver.model.actor.L2Attackable;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.L2Summon;
import com.l2jhellas.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2FestivalMonsterInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2FriendlyMobInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2GuardInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2MinionInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2RiftInvaderInstance;
import com.l2jhellas.gameserver.model.quest.Quest;
import com.l2jhellas.gameserver.model.quest.QuestEventType;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;
import com.l2jhellas.util.Rnd;
import com.l2jhellas.util.Util;


/**
 * This class manages AI of L2Attackable.<BR><BR>
 *
 */
public class L2AttackableAI extends L2CharacterAI implements Runnable
{
	private static final int RANDOM_WALK_RATE = 30; // confirmed
	// private static final int MAX_DRIFT_RANGE = 300;
	private static final int MAX_ATTACK_TIMEOUT = 100; // int ticks, i.e. 30 seconds, here 10 secs
	
	/** The L2Attackable AI task executed every 1s (call onEvtThink method)*/
	private Future<?> _aiTask;
	
	/** The delay after which the attacked is stopped */
	private int _attackTimeout;
	
	/** The L2Attackable aggro counter */
	private int _globalAggro;
	
	/** The flag used to indicate that a thinking action is in progress */
	private boolean _thinking; // to prevent recursive thinking
	
	private int timepass = 0;
	private int chaostime = 0;
	private final L2NpcTemplate _skillrender;
	int lastBuffTick;
	
	/**
	 * Constructor of L2AttackableAI.<BR><BR>
	 *
	 * @param accessor The AI accessor of the L2Character
	 *
	 */
	public L2AttackableAI(L2Character.AIAccessor accessor)
	{
		super(accessor);
		_skillrender = NpcData.getInstance().getTemplate(((L2Npc) _actor).getTemplate().npcId);
		//_selfAnalysis.init();
		_attackTimeout = Integer.MAX_VALUE;
		_globalAggro = -10; // 10 seconds timeout of ATTACK after respawn
	}
	
	@Override
	public void run()
	{
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	/**
	 * Return True if the target is autoattackable (depends on the actor type).<BR><BR>
	 *
	 * <B><U> Actor is a L2GuardInstance</U> :</B><BR><BR>
	 * <li>The target isn't a Folk or a Door</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>The L2Player target has karma (=PK)</li>
	 * <li>The L2MonsterInstance target is aggressive</li><BR><BR>
	 *
	 * <B><U> Actor is a L2SiegeGuardInstance</U> :</B><BR><BR>
	 * <li>The target isn't a Folk or a Door</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>A siege is in progress</li>
	 * <li>The L2Player target isn't a Defender</li><BR><BR>
	 *
	 * <B><U> Actor is a L2FriendlyMobInstance</U> :</B><BR><BR>
	 * <li>The target isn't a Folk, a Door or another L2Npc</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>The L2Player target has karma (=PK)</li><BR><BR>
	 *
	 * <B><U> Actor is a L2MonsterInstance</U> :</B><BR><BR>
	 * <li>The target isn't a Folk, a Door or another L2Npc</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>The actor is Aggressive</li><BR><BR>
	 *
	 * @param target The targeted L2Object
	 *
	 */
	private boolean autoAttackCondition(L2Character target)
	{
		if (target == null || _actor == null)
			return false;
		
		L2Attackable me = (L2Attackable) _actor;
		
		// Check if the target isn't invulnerable
		if (target.isInvul())
		{
			// However EffectInvincible requires to check GMs specially
			if (target instanceof L2PcInstance && ((L2PcInstance) target).isGM())
				return false;
			if (target instanceof L2Summon && ((L2Summon) target).getOwner().isGM())
				return false;
		}
		
		// Check if the target isn't a Folk or a Door
		if (target instanceof L2DoorInstance)
			return false;
		
		// Check if the target isn't dead, is in the Aggro range and is at the same height
		if (target.isAlikeDead() || (target instanceof L2Playable && !me.isInsideRadius(target, me.getAggroRange(), true, false)))
			return false;
		
		// Check if the target is a L2PlayableInstance
		if (target instanceof L2PcInstance)
		{
			// Check if the AI isn't a Raid Boss, can See Silent Moving players and the target isn't in silent move mode
			if (!(me.isRaid()) && ((L2PcInstance) target).isSilentMoving())
				return false;
		}
		
		// Check if the target is a L2Player
		if (target instanceof L2PcInstance)
		{
			// Don't take the aggro if the GM has the access level below or equal to GM_DONT_TAKE_AGGRO
			if (((L2PcInstance) target).isGM() && !((L2PcInstance) target).getAccessLevel().canTakeAggro())
				return false;
			
			// Check if player is an ally (comparing mem addr)
			if ("varka".equals(me.getFactionId()) && ((L2PcInstance) target).isAlliedWithVarka())
				return false;
			if ("ketra".equals(me.getFactionId()) && ((L2PcInstance) target).isAlliedWithKetra())
				return false;
			// check if the target is within the grace period for JUST getting up from fake death
			if (((L2PcInstance) target).isRecentFakeDeath())
				return false;
			
			if (target.isInParty() && target.getParty().isInDimensionalRift())
			{
				byte riftType = target.getParty().getDimensionalRift().getType();
				byte riftRoom = target.getParty().getDimensionalRift().getCurrentRoom();
				
				if (me instanceof L2RiftInvaderInstance && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(me.getX(), me.getY(), me.getZ()))
					return false;
			}
		}
		
		// Check if the target is a L2Summon
		if (target instanceof L2Summon)
		{
			L2PcInstance owner = ((L2Summon) target).getOwner();
			if (owner != null)
			{
				// Don't take the aggro if the GM has the access level below or equal to GM_DONT_TAKE_AGGRO
				if (owner.isGM() && (owner.isInvul() || !owner.getAccessLevel().canTakeAggro()))
					return false;
				// Check if player is an ally (comparing mem addr)
				if ("varka".equals(me.getFactionId()) && owner.isAlliedWithVarka())
					return false;
				if ("ketra".equals(me.getFactionId()) && owner.isAlliedWithKetra())
					return false;
			}
		}
		// Check if the actor is a L2GuardInstance
		if (_actor instanceof L2GuardInstance)
		{
			
			// Check if the L2Player target has karma (=PK)
			if (target instanceof L2PcInstance && ((L2PcInstance) target).getKarma() > 0)
				// Los Check
				return GeoEngine.canSeeTarget(me, target,false);
			
			//if (target instanceof L2Summon)
			//	return ((L2Summon)target).getKarma() > 0;
			
			boolean guardsAttackAggroMobs = false;
			// Check if the L2MonsterInstance target is aggressive
			if (target instanceof L2MonsterInstance && guardsAttackAggroMobs)
				return (((L2MonsterInstance) target).isAggressive() && GeoEngine.canSeeTarget(me, target,false));
			
			return false;
		}
		else if (_actor instanceof L2FriendlyMobInstance)
		{ // the actor is a L2FriendlyMobInstance
			
			// Check if the target isn't another L2Npc
			if (target instanceof L2Npc)
				return false;
			
			// Check if the L2Player target has karma (=PK)
			if (target instanceof L2PcInstance && ((L2PcInstance) target).getKarma() > 0)
				return GeoEngine.canSeeTarget(me, target,false); // Los Check
			else
				return false;
		}
		else
		{
			if (target instanceof L2Attackable)
			{
				if (((L2Attackable) _actor).getEnemyClan() == null || ((L2Attackable) target).getClan() == null)
					return false;
				
				if (!target.isAutoAttackable(_actor))
					return false;
				
				if (((L2Attackable) _actor).getEnemyClan().equals(((L2Attackable) target).getClan()))
				{
					if (_actor.isInsideRadius(target, ((L2Attackable) _actor).getEnemyRange(), false, false))
					{
						return GeoEngine.canSeeTarget(_actor, target,false);
					}
					else
						return false;
				}
				if (((L2Attackable) _actor).getIsChaos() > 0 && me.isInsideRadius(target, ((L2Attackable) _actor).getIsChaos(), false, false))
				{
					if (((L2Attackable) _actor).getFactionId() != null && ((L2Attackable) _actor).getFactionId().equals(((L2Attackable) target).getFactionId()))
					{
						return false;
					}
					// Los Check
					return GeoEngine.canSeeTarget(me, target,false);
				}
			}
			
			if (target instanceof L2Attackable || target instanceof L2Npc)
				return false;
			
			// depending on config, do not allow mobs to attack _new_ players in peacezones,
			// unless they are already following those players from outside the peacezone.
			if (!Config.ALT_MOB_AGRO_IN_PEACEZONE && target.isInsidePeaceZone(_actor.getActingPlayer()))
				return false;
			
			// Check if the actor is Aggressive
			return (me.isAggressive() && GeoEngine.canSeeTarget(me, target,false));
		}
	}
	
	public void startAITask()
	{
		// If not idle - create an AI task (schedule onEvtThink repeatedly)
		if (_aiTask == null)
			_aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, AI_INITIAL_TASK, AI_PERIOD_TASK);
	}
	
	@Override
	public void stopAITask()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(false);
			_aiTask = null;
		}
	}
	
	@Override
	protected void onEvtDead()
	{
		stopAITask();
		super.onEvtDead();
	}
	
	/**
	 * Set the Intention of this L2CharacterAI and create an  AI Task executed every 1s (call onEvtThink method) for this L2Attackable.<BR><BR>
	 *
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : If actor _knowPlayer isn't EMPTY, AI_INTENTION_IDLE will be change in AI_INTENTION_ACTIVE</B></FONT><BR><BR>
	 *
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention
	 * @param arg1 The second parameter of the Intention
	 *
	 */
	@Override
	synchronized void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		if (intention == AI_INTENTION_IDLE || intention == AI_INTENTION_ACTIVE)
		{
			// Check if actor is not dead
			if (!_actor.isAlikeDead())
			{
				L2Attackable npc = (L2Attackable) _actor;
				
				// If its _knownPlayer isn't empty set the Intention to AI_INTENTION_ACTIVE
				if (!npc.getKnownList().getKnownPlayers().isEmpty())
					intention = AI_INTENTION_ACTIVE;
				else
				{
					if (npc.getSpawn() != null)
					{
						final int range = Config.MAX_DRIFT_RANGE;
						if (!npc.isInsideRadius(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), range + range, true, false))
							intention = AI_INTENTION_ACTIVE;
					}
				}
			}
			
			if (intention == AI_INTENTION_IDLE)
			{
				// Set the Intention of this L2AttackableAI to AI_INTENTION_IDLE
				super.changeIntention(AI_INTENTION_IDLE, null, null);
				
				// Stop AI task and detach AI from NPC
				if (_aiTask != null)
				{
					_aiTask.cancel(true);
					_aiTask = null;
				}
				
				// Cancel the AI
				_accessor.detachAI();
				
				return;
			}
		}
		
		// Set the Intention of this L2AttackableAI to intention
		super.changeIntention(intention, arg0, arg1);
		
		// If not idle - create an AI task (schedule onEvtThink repeatedly)
		startAITask();
	}
	
	/**
	 * Manage the Attack Intention : Stop current Attack (if necessary), Calculate attack timeout, Start a new Attack and Launch Think Event.<BR><BR>
	 *
	 * @param target The L2Character to attack
	 *
	 */
	@Override
	protected void onIntentionAttack(L2Character target)
	{
		// Calculate the attack timeout
		_attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getInstance().getGameTicks();
		
		// self and buffs
		
		if (lastBuffTick + 30 < GameTimeController.getInstance().getGameTicks())
		{
			if (_skillrender.hasBuffSkill())
				for (L2Skill sk : _skillrender._buffskills)
					if (Cast(sk))
						break;
			
			lastBuffTick = GameTimeController.getInstance().getGameTicks();
		}
		
		// Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event
		super.onIntentionAttack(target);
	}
	
	/**
	 * Manage AI standard thinks of a L2Attackable (called by onEvtThink).<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Update every 1s the _globalAggro counter to come close to 0</li>
	 * <li>If the actor is Aggressive and can attack, add all autoAttackable L2Character in its Aggro Range to its _aggroList, chose a target and order to attack it</li>
	 * <li>If the actor is a L2GuardInstance that can't attack, order to it to return to its home location</li>
	 * <li>If the actor is a L2MonsterInstance that can't attack, order to it to random walk (1/100)</li><BR><BR>
	 *
	 */
	private void thinkActive()
	{
		L2Attackable npc = (L2Attackable) _actor;
		
		// If this is a flag, then it remains in the same location.
		if(_actor.getObjectId() ==36006)
			return;
		
		// Update every 1s the _globalAggro counter to come close to 0
		if (_globalAggro != 0)
		{
			if (_globalAggro < 0)
				_globalAggro++;
			else
				_globalAggro--;
		}
		
		// Add all autoAttackable L2Character in L2Attackable Aggro Range to its _aggroList with 0 damage and 1 hate
		// A L2Attackable isn't aggressive during 10s after its spawn because _globalAggro is set to -10
		if (_globalAggro >= 0)
		{
			// Get all visible objects inside its Aggro Range
			final Collection<L2Object> objs = _actor.getKnownList().getKnownObjects().values();
			//synchronized (npc.getKnownList().getKnownObjects())
			{
				for (L2Object obj : objs)
				{
					if (!(obj instanceof L2Character))
						continue;
					L2Character target = (L2Character) obj;
					
					/*
					 * Check to see if this is a festival mob spawn.
					 * If it is, then check to see if the aggro trigger
					 * is a festival participant...if so, move to attack it.
					 */
					if ((_actor instanceof L2FestivalMonsterInstance) && obj instanceof L2PcInstance)
					{
						L2PcInstance targetPlayer = (L2PcInstance) obj;
						
						if (!(targetPlayer.isFestivalParticipant()))
							continue;
					}
					
					if (autoAttackCondition(target)) // check aggression
					{
						// Get the hate level of the L2Attackable against this L2Character target contained in _aggroList
						int hating = npc.getHating(target);
						
						// Add the attacker to the L2Attackable _aggroList with 0 damage and 1 hate
						if (hating == 0)
							npc.addDamageHate(target, 0, 1);
					}
				}
			}
			
			// Chose a target from its aggroList
			L2Character hated;
			if (_actor.isConfused())
				hated = getAttackTarget(); // effect handles selection
			else
				hated = npc.getMostHated();
			
			// Order to the L2Attackable to attack the target
			if (hated != null)
			{
				// Get the hate level of the L2Attackable against this L2Character target contained in _aggroList
				int aggro = npc.getHating(hated);
				
				if (aggro + _globalAggro > 0)
				{
					// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2Player
					if (!_actor.isRunning())
						_actor.setRunning();
					
					// Set the AI Intention to AI_INTENTION_ATTACK
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, hated);
				}
				
				return;
			}
			
		}
		
		// Check if the actor is a L2GuardInstance
		if (_actor instanceof L2GuardInstance)
		{
			// Order to the L2GuardInstance to return to its home location because there's no target to attack
			((L2GuardInstance) _actor).returnHome();
		}
		
		// If this is a festival monster, then it remains in the same location.
		if (_actor instanceof L2FestivalMonsterInstance)
			return;
		
		// Check if the mob should not return to spawn point
		if (!npc.canReturnToSpawnPoint())
			return;
		
		// Minions following leader
		if (_actor instanceof L2MinionInstance && ((L2MinionInstance) _actor).getLeader() != null)
		{
			final int offset = (int) (100 + npc.getCollisionRadius() + ((L2MinionInstance) _actor).getLeader().getTemplate().getCollisionRadius());
			final int minRadius = (int) (_actor.getTemplate().getCollisionRadius() + 30);
			
			if (((L2MinionInstance) _actor).getLeader().isRunning())
				_actor.setRunning();
			else
				_actor.setWalking();
			
			if (_actor.getPlanDistanceSq(((L2MinionInstance) _actor).getLeader()) > offset * offset)
			{
				int x1 = Rnd.get(minRadius * 2, offset * 2); //x
				int y1 = Rnd.get(x1, offset * 2); //dist
				
				y1 = (int) Math.sqrt(y1 * y1 - x1 * x1); //y
				
				if (x1 > offset + minRadius)
					x1 = _actor.getX() + x1 - offset;
				else
					x1 = _actor.getX() - x1 + minRadius;
				
				if (y1 > offset + minRadius)
					y1 = _actor.getY() + y1 - offset;
				else
					y1 = _actor.getY() - y1 + minRadius;
				
				// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
				moveTo(x1, y1, _actor.getZ());
				return;
			}
			else if (Rnd.nextInt(RANDOM_WALK_RATE) == 0)
			{
				if (_skillrender.hasBuffSkill())
					for (L2Skill sk : _skillrender._buffskills)
						if (Cast(sk))
							return;
			}
		}
		// Order to the L2MonsterInstance to random walk (1/100)
		else if (npc.getSpawn() != null && Rnd.nextInt(RANDOM_WALK_RATE) == 0)
		{
			int x1, y1, z1;
			final int range = Config.MAX_DRIFT_RANGE;
			
			if (_skillrender.hasBuffSkill())
				for (L2Skill sk : _skillrender._buffskills)
					if (Cast(sk))
						return;
			
			// If NPC with random coord in territory
			if (npc.getSpawn().getLocx() == 0 && npc.getSpawn().getLocy() == 0)
			{
				// Calculate a destination point in the spawn area
				int p[] = Territory.getInstance().getRandomPoint(npc.getSpawn().getLocation());
				x1 = p[0];
				y1 = p[1];
				z1 = p[2];
				
				// Calculate the distance between the current position of the L2Character and the target (x,y)
				double distance2 = _actor.getPlanDistanceSq(x1, y1);
				
				if (distance2 > (range + range) * (range + range))
				{
					npc.setIsReturningToSpawnPoint(true);
					float delay = (float) Math.sqrt(distance2) / range;
					x1 = _actor.getX() + (int) ((x1 - _actor.getX()) / delay);
					y1 = _actor.getY() + (int) ((y1 - _actor.getY()) / delay);
				}
				
				// If NPC with random fixed coord, don't move (unless needs to return to spawnpoint)
				if (Territory.getInstance().getProcMax(npc.getSpawn().getLocation()) > 0 && !npc.isReturningToSpawnPoint())
					return;
			}
			else
			{
				// If NPC with fixed coord
				x1 = npc.getSpawn().getLocx();
				y1 = npc.getSpawn().getLocy();
				z1 = npc.getSpawn().getLocz();
				
				if (!_actor.isInsideRadius(x1, y1, z1, range + range, true, false))
					npc.setIsReturningToSpawnPoint(true);
				else
				{
					x1 += Rnd.nextInt(range * 2) - range;
					y1 += Rnd.nextInt(range * 2) - range;
					z1 = npc.getZ();
				}
			}
			
			// Orfen After teleporting should not respawn to the spawn point
		    if (npc.getNpcId() == 29014 && npc instanceof L2GrandBossInstance && ((L2GrandBossInstance) npc).getTeleported())
			return;
		    
			//_log.info("Curent pos ("+getX()+", "+getY()+"), moving to ("+x1+", "+y1+").");
			// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
			moveTo(x1, y1, z1);
		}
	}
	
	/**
	 * Manage AI attack thinks of a L2Attackable (called by onEvtThink).<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Update the attack timeout if actor is running</li>
	 * <li>If target is dead or timeout is expired, stop this attack and set the Intention to AI_INTENTION_ACTIVE</li>
	 * <li>Call all L2Object of its Faction inside the Faction Range</li>
	 * <li>Chose a target and order to attack it with magic skill or physical attack</li><BR><BR>
	 *
	 * TODO: Manage casting rules to healer mobs (like Ant Nurses)
	 *
	 */
	private void thinkAttack()
	{
		if (_actor.isCastingNow())
			return;
		
		 if (_attackTimeout < GameTimeController.getInstance().getGameTicks())
	        {
	            // Check if the actor is running
	            if (_actor.isRunning())
	            {
	                // Set the actor movement type to walk and send Server->Client packet ChangeMoveType to all others L2Player
	                _actor.setWalking();

	                // Calculate a new attack timeout
	                _attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getInstance().getGameTicks();
	            }
	        }

	        L2Character originalAttackTarget = getAttackTarget();

	        // Check if target is dead or if timeout is expired to stop this attack
	        if (originalAttackTarget == null || originalAttackTarget.isAlikeDead() || _attackTimeout < GameTimeController.getInstance().getGameTicks())
	        {
	            // Stop hating this target after the attack timeout or if target is dead
	            if (originalAttackTarget != null)
	                ((L2Attackable) _actor).stopHating(originalAttackTarget);

	            // Set the AI Intention to AI_INTENTION_ACTIVE
	            setIntention(AI_INTENTION_ACTIVE);

	            _actor.setWalking();
	            return;
	        }
	        

		// Handle all L2Object of its Faction inside the Faction Range
		
		if (((L2Npc) _actor).getFactionId() != null)
		{
			String faction_id = ((L2Npc) _actor).getFactionId();
			
			// Go through all L2Object that belong to its faction
			Collection<L2Object> objs = _actor.getKnownList().getKnownObjects().values();
			//synchronized (_actor.getKnownList().getKnownObjects())
			try
			{
				for (L2Object obj : objs)
				{
					if (obj instanceof L2Npc)
					{
						L2Npc npc = (L2Npc) obj;
						
						if (getAttackTarget() == null || faction_id != npc.getFactionId())
							continue;
						
						// Check if the L2Object is inside the Faction Range of the actor
						if (_actor.isInsideRadius(npc, npc.getFactionRange(), true, false) && GeoEngine.canSeeTarget(_actor, npc,false) && Math.abs(getAttackTarget().getZ() - npc.getZ()) < 600 && npc.getAI() != null && _actor.getAttackByList().contains(getAttackTarget()) && (npc.getAI()._intention == CtrlIntention.AI_INTENTION_IDLE || npc.getAI()._intention == CtrlIntention.AI_INTENTION_ACTIVE))
						{
							if ((originalAttackTarget instanceof L2PcInstance) || (originalAttackTarget instanceof L2Summon))
							{
								if (npc.getTemplate().getEventQuests(QuestEventType.ON_FACTION_CALL) != null)
								{
									L2PcInstance player = (originalAttackTarget instanceof L2PcInstance) ? (L2PcInstance) originalAttackTarget : ((L2Summon) originalAttackTarget).getOwner();
									for (Quest quest : npc.getTemplate().getEventQuests(QuestEventType.ON_FACTION_CALL))
										quest.notifyFactionCall(npc, (L2Npc) _actor, player, (originalAttackTarget instanceof L2Summon));
								}
							}
							if (getAttackTarget() instanceof L2PcInstance && getAttackTarget().isInParty() && getAttackTarget().getParty().isInDimensionalRift())
							{
								byte riftType = getAttackTarget().getParty().getDimensionalRift().getType();
								byte riftRoom = getAttackTarget().getParty().getDimensionalRift().getCurrentRoom();
								
								if (_actor instanceof L2RiftInvaderInstance && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(npc.getX(), npc.getY(), npc.getZ()))
									continue;
							}
							
							// Notify the L2Object AI with EVT_AGGRESSION
							npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 1);
						}
					}
				}
			}
			catch (NullPointerException e)
			{
				_log.info("L2AttackableAI: thinkAttack() faction call failed.");
				if (Config.DEBUG)
					e.printStackTrace();
			}
		}
		
		
		L2Character MostHate = ((L2Attackable) _actor).getMostHated();

		if (MostHate == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}

		 setAttackTarget(MostHate);
		 _actor.setTarget(MostHate);
		 
		/*
		if(_actor.getTarget() == null || this.getAttackTarget() == null || this.getAttackTarget().isDead() || ctarget == _actor)
			AggroReconsider();
		 */
		
		//----------------------------------------------------------------
		
		//------------------------------------------------------------------------------'
		//Initialize data
		final int actorCollision = (int) _actor.getTemplate().getCollisionRadius();
		final int combinedCollision = (int) (actorCollision + originalAttackTarget.getTemplate().getCollisionRadius());
		final double dista = Math.sqrt(_actor.getPlanDistanceSq(originalAttackTarget.getX(), originalAttackTarget.getY()));
		int dist2 = (int) dista - _actor.getTemplate().collisionRadius;

		int range = combinedCollision;
		if (originalAttackTarget.isMoving())
			range += 15;
			
		if (_actor.isMoving())
			range += 15;
			
		//------------------------------------------------------
		// In case many mobs are trying to hit from same place, move a bit,
		// circling around the target
		if (!_actor.isMovementDisabled() && Rnd.nextInt(100) <= 33) // check it once per 3 seconds
		{

			for (L2Object nearby : _actor.getKnownList().getKnownMonstersInRadius(actorCollision))
			{
				if(nearby==null)
					continue;
				
				 if (nearby != MostHate)
				{
					int newX = combinedCollision + Rnd.get(45);
					
					if (Rnd.nextBoolean())
						newX = MostHate.getX() + newX;
					else
						newX = MostHate.getX() - newX;
					
					int newY = combinedCollision + Rnd.get(45);
					
					if (Rnd.nextBoolean())
						newY = MostHate.getY() + newY;
					else
						newY = MostHate.getY() - newY;
					
						int newZ = _actor.getZ() + 30;

						if (!_actor.isInsideRadius(newX, newY, 0, actorCollision, false, false))
						{
							Location mov = GeoEngine.moveCheckForAI(new Location(_actor.getX(), _actor.getY(), _actor.getZ()),new Location(newX,newY,newZ));
							setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(mov.getX(), mov.getY(), mov.getZ(), 0));
						}
						
					return;
				}
			}
		}
		
		//Dodge if its needed
		if (!_actor.isMovementDisabled() && ((L2Attackable) _actor).getCanDodge() > 0)
			if (Rnd.get(100) <= ((L2Attackable) _actor).getCanDodge())
			{
				// Micht: kepping this one otherwise we should do 2 sqrt
				double distance2 = _actor.getPlanDistanceSq(getAttackTarget().getX(), getAttackTarget().getY());
				if (Math.sqrt(distance2) <= (60 + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius))
				{
					//Diasable the RND for increasing the performance
					//int chance = 60;
					//if (chance >= Rnd.get(100))
					//{
					int posX = _actor.getX();
					int posY = _actor.getY();
					int posZ = _actor.getZ();
					/*
					if (Rnd.get(1)>0)
					posX=((L2Attackable)_actor).getSpawn().getLocx()+Rnd.get(100);
					else
					posX=((L2Attackable)_actor).getSpawn().getLocx()-Rnd.get(100);

					if (Rnd.get(1)>0)
					posY=((L2Attackable)_actor).getSpawn().getLocy() + Rnd.get(100);
					else
					posY=((L2Attackable)_actor).getSpawn().getLocy()-Rnd.get(100);

					setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(posX, posY, posZ, 0));
					 */
					if (Rnd.get(1) > 0)
						posX = posX + Rnd.get(100);
					else
						posX = posX - Rnd.get(100);
					
					if (Rnd.get(1) > 0)
						posY = posY + Rnd.get(100);
					else
						posY = posY - Rnd.get(100);
					
					Location mov = GeoEngine.moveCheckForAI(new Location(_actor.getX(), _actor.getY(), _actor.getZ()),new Location(posX,posY,posZ));				
					setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(mov.getX(), mov.getY(), mov.getZ(), 0));
					return;
					//}
				}
			}
		
		//------------------------------------------------------------------------------
		// BOSS/Raid Minion Target Reconsider
		if (_actor.isRaid() || _actor.isRaidMinion())
		{
			chaostime++;
			if (_actor instanceof L2RaidBossInstance && !((L2MonsterInstance) _actor).hasMinions())
			{
				if (chaostime > 10)
					if (Rnd.get(100) <= 100 - (_actor.getCurrentHp() * 100 / _actor.getMaxHp()))
					{
						AggroReconsider();
						chaostime = 0;
					}
			}
			else if (_actor instanceof L2RaidBossInstance && ((L2MonsterInstance) _actor).hasMinions())
			{
				if (chaostime > 10)
					if (Rnd.get(100) <= 100 - (_actor.getCurrentHp() * 200 / _actor.getMaxHp()))
					{
						AggroReconsider();
						chaostime = 0;
					}
			}
			else if (_actor instanceof L2GrandBossInstance)
			{
				if (chaostime > 10)
				{
					double chaosRate = 100 - (_actor.getCurrentHp() * 300 / _actor.getMaxHp());
					if ((chaosRate <= 10 && Rnd.get(100) <= 10) || (chaosRate > 10 && Rnd.get(100) <= chaosRate))
					{
						AggroReconsider();
						chaostime = 0;
					}
				}
			}
			else
			{
				if (chaostime > 10)
					if (Rnd.get(100) <= 100 - (_actor.getCurrentHp() * 200 / _actor.getMaxHp()))
					{
						AggroReconsider();
						chaostime = 0;
					}
			}
		}
		
		if (_skillrender.hasSkill())
		{
			//-------------------------------------------------------------------------------
			//Heal Condition
			if (_skillrender.hasHealSkill() && _skillrender._healskills != null)
			{
				double percentage = _actor.getCurrentHp() / _actor.getMaxHp() * 100;
				if (_actor instanceof L2MinionInstance)
				{
					L2Character leader = ((L2MinionInstance) _actor).getLeader();
					if (leader != null && !leader.isDead() && Rnd.get(100) > (leader.getCurrentHp() / leader.getMaxHp() * 100))
						for (L2Skill sk : _skillrender._healskills)
						{
							if (sk.getTargetType() == L2SkillTargetType.TARGET_SELF)
								continue;
							if ((sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.isMagic() && _actor.isMuted()) || (!sk.isMagic() && _actor.isPsychicalMuted())))
							{
								continue;
							}
							if (!Util.checkIfInRange((sk.getCastRange() + _actor.getTemplate().collisionRadius + leader.getTemplate().collisionRadius), _actor, leader, false) && !isParty(sk) && !_actor.isMovementDisabled())
							{
								moveToPawn(leader, sk.getCastRange() + _actor.getTemplate().collisionRadius + leader.getTemplate().collisionRadius);
							}
							if (GeoEngine.canSeeTarget(_actor, leader,false))
							{
								clientStopMoving(null);
								_actor.setTarget(leader);
								clientStopMoving(null);
								_actor.doCast(sk);
								return;
							}
						}
				}
				if (Rnd.get(100) < (100 - percentage) / 3)
					for (L2Skill sk : _skillrender._healskills)
					{
						if ((sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.isMagic() && _actor.isMuted())) || (!sk.isMagic() && _actor.isPsychicalMuted()))
						{
							continue;
						}
						clientStopMoving(null);
						_actor.setTarget(_actor);
						_actor.doCast(sk);
						return;
					}
				for (L2Skill sk : _skillrender._healskills)
				{
					if ((sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.isMagic() && _actor.isMuted())) || (!sk.isMagic() && _actor.isPsychicalMuted()))
					{
						continue;
					}
					if (sk.getTargetType() == L2SkillTargetType.TARGET_ONE)
						for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(sk.getCastRange() + _actor.getTemplate().collisionRadius))
						{
							if (!(obj instanceof L2Attackable) || obj.isDead())
								continue;
							
							L2Attackable targets = ((L2Attackable) obj);
							if (((L2Attackable) _actor).getFactionId() != targets.getFactionId() && ((L2Attackable) _actor).getFactionId() != null)
								continue;
							percentage = targets.getCurrentHp() / targets.getMaxHp() * 100;
							if (Rnd.get(100) < (100 - percentage) / 10)
							{
								if (GeoEngine.canSeeTarget(_actor, targets,false))
								{
									clientStopMoving(null);
									_actor.setTarget(obj);
									_actor.doCast(sk);
									return;
								}
							}
						}
					if (isParty(sk))
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return;
					}
				}
			}
			//-------------------------------------------------------------------------------
			//Res Skill Condition
			if (_skillrender.hasResSkill())
			{
				if (_actor instanceof L2MinionInstance)
				{
					L2Character leader = ((L2MinionInstance) _actor).getLeader();
					if (leader != null && leader.isDead())
						for (L2Skill sk : _skillrender._resskills)
						{
							if (sk.getTargetType() == L2SkillTargetType.TARGET_SELF)
								continue;
							if ((sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.isMagic() && _actor.isMuted()) || (!sk.isMagic() && _actor.isPsychicalMuted())))
							{
								continue;
							}
							if (!Util.checkIfInRange((sk.getCastRange() + _actor.getTemplate().collisionRadius + leader.getTemplate().collisionRadius), _actor, leader, false) && !isParty(sk) && !_actor.isMovementDisabled())
							{
								moveToPawn(leader, sk.getCastRange() + _actor.getTemplate().collisionRadius + leader.getTemplate().collisionRadius);
							}
							if (GeoEngine.canSeeTarget(_actor, leader,false))
							{
								clientStopMoving(null);
								_actor.setTarget(((L2MinionInstance) _actor).getLeader());
								_actor.doCast(sk);
								return;
							}
						}
				}
				for (L2Skill sk : _skillrender._resskills)
				{
					if ((sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.isMagic() && _actor.isMuted())) || (!sk.isMagic() && _actor.isPsychicalMuted()))
					{
						continue;
					}
					if (sk.getTargetType() == L2SkillTargetType.TARGET_ONE)
						for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(sk.getCastRange() + _actor.getTemplate().collisionRadius))
						{
							if (!(obj instanceof L2Attackable) || !obj.isDead())
								continue;
							
							L2Attackable targets = ((L2Attackable) obj);
							if (((L2Attackable) _actor).getFactionId() != targets.getFactionId() && ((L2Attackable) _actor).getFactionId() != null)
								continue;
							if (Rnd.get(100) < 10)
							{
								if (GeoEngine.canSeeTarget(_actor, targets,false))
								{
									clientStopMoving(null);
									_actor.setTarget(obj);
									_actor.doCast(sk);
									return;
								}
							}
						}
					if (isParty(sk))
					{
						clientStopMoving(null);
						L2Object target = getAttackTarget();
						_actor.setTarget(_actor);
						_actor.doCast(sk);
						_actor.setTarget(target);
						return;
					}
				}
			}
		}
		
		//-------------------------------------------------------------------------------
		//Immobilize Condition
		if ((_actor.isMovementDisabled() && (dista > range || getAttackTarget().isMoving())) || (dista > range && getAttackTarget().isMoving()))
		{
			MovementDisable();
			return;
		}
		setTimepass(0);
		//--------------------------------------------------------------------------------
		//Skill Use
		if (_skillrender.hasSkill())
		{
			if (Rnd.get(100) <= ((L2Npc) _actor).getSkillChance())
			{
				L2Skill skills = _skillrender._generalskills.get(Rnd.nextInt(_skillrender._generalskills.size()));
				if (Cast(skills))
					return;
				for (L2Skill sk : _skillrender._generalskills)
					if (Cast(sk))
						return;
			}
			
			//--------------------------------------------------------------------------------
			//Long/Short Range skill Usage
			if (((L2Npc) _actor).hasLSkill() || ((L2Npc) _actor).hasSSkill())
			{
				if (((L2Npc) _actor).hasSSkill() && dist2 <= 150 && Rnd.get(100) <= ((L2Npc) _actor).getSSkillChance())
				{
					SSkillRender();
					if (_skillrender._Srangeskills != null)
					{
						L2Skill skills = _skillrender._Srangeskills.get(Rnd.nextInt(_skillrender._Srangeskills.size()));
						if (Cast(skills))
							return;
						for (L2Skill sk : _skillrender._Srangeskills)
							if (Cast(sk))
								return;
					}
				}
				if (((L2Npc) _actor).hasLSkill() && dist2 > 150 && Rnd.get(100) <= ((L2Npc) _actor).getSSkillChance())
				{
					LSkillRender();
					if (_skillrender._Lrangeskills != null)
					{
						L2Skill skills = _skillrender._Lrangeskills.get(Rnd.nextInt(_skillrender._Lrangeskills.size()));
						if (Cast(skills))
							return;
						for (L2Skill sk : _skillrender._Lrangeskills)
							if (Cast(sk))
								return;
					}
				}
			}
		}
		
		range += (_actor.getPhysicalAttackRange());
		
		//--------------------------------------------------------------------------------
		// Starts Melee or Primary Skill
		if (dist2 > range || !GeoEngine.canSeeTarget(_actor, getAttackTarget(),false))
		{
			if (_actor.isMovementDisabled())
			{
				TargetReconsider();
				return;
			}
			else
			{
				if (getAttackTarget().isMoving())
					range -= 30;

				moveTo(getAttackTarget().getX(), getAttackTarget().getY(), getAttackTarget().getZ());
											
				return;
			}
		}
		else
		{
		    Melee(((L2Npc) _actor).getPrimaryAttack());
		}
	}
	
	private void Melee(int type)
	{
		if (type != 0)
		{
			switch (type)
			{
				case -1:
				{
					if (_skillrender._generalskills != null)
						for (L2Skill sk : _skillrender._generalskills)
							if (Cast(sk))
								return;
					break;
				}
				case 1:
				{
					if (_skillrender.hasAtkSkill())
						for (L2Skill sk : _skillrender._atkskills)
							if (Cast(sk))
								return;
					break;
				}
				default:
				{
					if (_skillrender._generalskills != null)
						for (L2Skill sk : _skillrender._generalskills)
							if (sk.getId() == ((L2Npc) _actor).getPrimaryAttack())
								if (Cast(sk))
									return;
				}
				break;
			}
		}
		
		_accessor.doAttack(getAttackTarget());
	}
	
	private boolean Cast(L2Skill sk)
	{
		if (sk == null)
			return false;
		if (sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.isMagic() && _actor.isMuted()) || (!sk.isMagic() && _actor.isPsychicalMuted()))
			return false;
		if (getAttackTarget() == null)
			if (((L2Attackable) _actor).getMostHated() != null)
				setAttackTarget(((L2Attackable) _actor).getMostHated());
		L2Character attackTarget = getAttackTarget();
		if (attackTarget == null)
			return false;
		double dist = Math.sqrt(_actor.getPlanDistanceSq(attackTarget.getX(), attackTarget.getY()));
		double dist2 = dist - attackTarget.getTemplate().collisionRadius;
		double range = _actor.getPhysicalAttackRange() + _actor.getTemplate().collisionRadius + attackTarget.getTemplate().collisionRadius;
		double srange = sk.getCastRange() + _actor.getTemplate().collisionRadius;
		if (attackTarget.isMoving())
			dist2 = dist2 - 30;
		
		switch (sk.getSkillType())
		{
			
			case BUFF:
			case REFLECT:
			{
				if (_actor.getFirstEffect(sk) == null)
				{
					clientStopMoving(null);
					//L2Object target = attackTarget;
					_actor.setTarget(_actor);
					_actor.doCast(sk);
					//_actor.setTarget(target);
					return true;
				}
				//----------------------------------------
				//If actor already have buff, start looking at others same faction mob to cast
				if (sk.getTargetType() == L2SkillTargetType.TARGET_SELF)
					return false;
				if (sk.getTargetType() == L2SkillTargetType.TARGET_ONE)
				{
					L2Character target = EffectTargetReconsider(sk, true);
					if (target != null)
					{
						clientStopMoving(null);
						L2Object targets = attackTarget;
						_actor.setTarget(target);
						_actor.doCast(sk);
						_actor.setTarget(targets);
						return true;
					}
				}
				if (canParty(sk))
				{
					clientStopMoving(null);
					L2Object targets = attackTarget;
					_actor.setTarget(_actor);
					_actor.doCast(sk);
					_actor.setTarget(targets);
					return true;
				}
				break;
			}
			case HEAL:
			case HOT:
			case HEAL_PERCENT:
			case HEAL_STATIC:
			case BALANCE_LIFE:
			{
				double percentage = _actor.getCurrentHp() / _actor.getMaxHp() * 100;
				if (_actor instanceof L2MinionInstance && sk.getTargetType() != L2SkillTargetType.TARGET_SELF)
				{
					L2Character leader = ((L2MinionInstance) _actor).getLeader();
					if (leader != null && !leader.isDead() && Rnd.get(100) > (leader.getCurrentHp() / leader.getMaxHp() * 100))
					{
						if (!Util.checkIfInRange((sk.getCastRange() + _actor.getTemplate().collisionRadius + leader.getTemplate().collisionRadius), _actor, leader, false) && !isParty(sk) && !_actor.isMovementDisabled())
						{
							moveToPawn(leader, sk.getCastRange() + _actor.getTemplate().collisionRadius + leader.getTemplate().collisionRadius);
						}
						if (GeoEngine.canSeeTarget(_actor, leader,false))
						{
							clientStopMoving(null);
							_actor.setTarget(leader);
							_actor.doCast(sk);
							return true;
						}
					}
				}
				if (Rnd.get(100) < (100 - percentage) / 3)
				{
					clientStopMoving(null);
					_actor.setTarget(_actor);
					_actor.doCast(sk);
					return true;
				}
				
				if (sk.getTargetType() == L2SkillTargetType.TARGET_ONE)
					for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(sk.getCastRange() + _actor.getTemplate().collisionRadius))
					{
						if (!(obj instanceof L2Attackable) || obj.isDead())
							continue;
						
						L2Attackable targets = ((L2Attackable) obj);
						if (((L2Attackable) _actor).getFactionId() != targets.getFactionId() && ((L2Attackable) _actor).getFactionId() != null)
							continue;
						percentage = targets.getCurrentHp() / targets.getMaxHp() * 100;
						if (Rnd.get(100) < (100 - percentage) / 10)
						{
							if (GeoEngine.canSeeTarget(_actor, targets,false))
							{
								clientStopMoving(null);
								_actor.setTarget(obj);
								_actor.doCast(sk);
								return true;
							}
						}
					}
				if (isParty(sk))
				{
					for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(sk.getSkillRadius() + _actor.getTemplate().collisionRadius))
					{
						if (!(obj instanceof L2Attackable))
						{
							continue;
						}
						L2Npc targets = ((L2Npc) obj);
						L2Npc actors = ((L2Npc) _actor);
						if (actors.getFactionId() != null && targets.getFactionId().equals(actors.getFactionId()))
						{
							if (obj.getCurrentHp() < obj.getMaxHp() && Rnd.get(100) <= 20)
							{
								clientStopMoving(null);
								_actor.setTarget(_actor);
								_actor.doCast(sk);
								return true;
							}
						}
					}
				}
				break;
			}
			case RESURRECT:
			{
				if (!isParty(sk))
				{
					if (_actor instanceof L2MinionInstance && sk.getTargetType() != L2SkillTargetType.TARGET_SELF)
					{
						L2Character leader = ((L2MinionInstance) _actor).getLeader();
						if (leader != null && leader.isDead())
							if (!Util.checkIfInRange((sk.getCastRange() + _actor.getTemplate().collisionRadius + leader.getTemplate().collisionRadius), _actor, leader, false) && !isParty(sk) && !_actor.isMovementDisabled())
							{
								moveToPawn(leader, sk.getCastRange() + _actor.getTemplate().collisionRadius + leader.getTemplate().collisionRadius);
							}
						if (GeoEngine.canSeeTarget(_actor, leader,false))
						{
							clientStopMoving(null);
							_actor.setTarget(((L2MinionInstance) _actor).getLeader());
							_actor.doCast(sk);
							return true;
						}
					}
					
					for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(sk.getCastRange() + _actor.getTemplate().collisionRadius))
					{
						if (!(obj instanceof L2Attackable) || !obj.isDead())
							continue;
						
						L2Attackable targets = ((L2Attackable) obj);
						if (((L2Attackable) _actor).getFactionId() != targets.getFactionId() && ((L2Attackable) _actor).getFactionId() != null)
							continue;
						if (Rnd.get(100) < 10)
						{
							if (GeoEngine.canSeeTarget(_actor, targets,false))
							{
								clientStopMoving(null);
								_actor.setTarget(obj);
								_actor.doCast(sk);
								return true;
							}
						}
					}
				}
				else if (isParty(sk))
				{
					for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(sk.getSkillRadius() + _actor.getTemplate().collisionRadius))
					{
						if (!(obj instanceof L2Attackable))
						{
							continue;
						}
						L2Npc targets = ((L2Npc) obj);
						L2Npc actors = ((L2Npc) _actor);
						if (actors.getFactionId() != null && actors.getFactionId().equals(targets.getFactionId()))
						{
							if (obj.getCurrentHp() < obj.getMaxHp() && Rnd.get(100) <= 20)
							{
								clientStopMoving(null);
								_actor.setTarget(_actor);
								_actor.doCast(sk);
								return true;
							}
						}
					}
				}
				break;
			}
			case DEBUFF:
			case WEAKNESS:
			case POISON:
			case DOT:
			case MDOT:
			case BLEED:
			{
				if (GeoEngine.canSeeTarget(_actor, attackTarget,false) && !canAOE(sk) && !attackTarget.isDead() && dist2 <= srange)
				{
					if (attackTarget.getFirstEffect(sk) == null)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					if (sk.getTargetType() == L2SkillTargetType.TARGET_AURA)
					{
						clientStopMoving(null);
						//L2Object target = attackTarget;
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return true;
					}
					if ((sk.getTargetType() == L2SkillTargetType.TARGET_AREA || sk.getTargetType() == L2SkillTargetType.TARGET_MULTIFACE) && GeoEngine.canSeeTarget(_actor, attackTarget,false) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				else if (sk.getTargetType() == L2SkillTargetType.TARGET_ONE)
				{
					L2Character target = EffectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				break;
			}
			case SLEEP:
			{
				if (sk.getTargetType() == L2SkillTargetType.TARGET_ONE)
				{
					
					if (!attackTarget.isDead() && dist2 <= srange)
					{
						
						if (dist2 > range || attackTarget.isMoving())
						{
							if (attackTarget.getFirstEffect(sk) == null)
							{
								clientStopMoving(null);
								//_actor.setTarget(attackTarget);
								_actor.doCast(sk);
								return true;
							}
						}
					}
					
					L2Character target = EffectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					if (sk.getTargetType() == L2SkillTargetType.TARGET_AURA)
					{
						clientStopMoving(null);
						//L2Object target = attackTarget;
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return true;
					}
					if ((sk.getTargetType() == L2SkillTargetType.TARGET_AREA || sk.getTargetType() == L2SkillTargetType.TARGET_MULTIFACE) && GeoEngine.canSeeTarget(_actor, attackTarget,false) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				break;
			}
			case ROOT:
			case STUN:
			case PARALYZE:
			{
				if (GeoEngine.canSeeTarget(_actor, attackTarget,false) && !canAOE(sk) && dist2 <= srange)
				{
					if (attackTarget.getFirstEffect(sk) == null)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					if (sk.getTargetType() == L2SkillTargetType.TARGET_AURA)
					{
						clientStopMoving(null);
						//L2Object target = attackTarget;
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return true;
					}
					else if ((sk.getTargetType() == L2SkillTargetType.TARGET_AREA || sk.getTargetType() == L2SkillTargetType.TARGET_MULTIFACE) && GeoEngine.canSeeTarget(_actor, attackTarget,false) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				else if (sk.getTargetType() == L2SkillTargetType.TARGET_ONE)
				{
					L2Character target = EffectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				break;
			}
			case MUTE:
			case FEAR:
			{
				if (GeoEngine.canSeeTarget(_actor, attackTarget,false) && !canAOE(sk) && dist2 <= srange)
				{
					if (attackTarget.getFirstEffect(sk) == null)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					if (sk.getTargetType() == L2SkillTargetType.TARGET_AURA)
					{
						clientStopMoving(null);
						//L2Object target = attackTarget;
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return true;
					}
					if ((sk.getTargetType() == L2SkillTargetType.TARGET_AREA || sk.getTargetType() == L2SkillTargetType.TARGET_MULTIFACE) && GeoEngine.canSeeTarget(_actor, attackTarget,false) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				else if (sk.getTargetType() == L2SkillTargetType.TARGET_ONE)
				{
					L2Character target = EffectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				break;
			}
			case CANCEL:
			case NEGATE:
			{
				if (sk.getTargetType() == L2SkillTargetType.TARGET_ONE)
				{
					if (attackTarget.getFirstEffect(L2Effect.EffectType.BUFF) != null && GeoEngine.canSeeTarget(_actor, attackTarget,false) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						//L2Object target = attackTarget;
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return true;
					}
					L2Character target = EffectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						L2Object targets = attackTarget;
						_actor.setTarget(target);
						_actor.doCast(sk);
						_actor.setTarget(targets);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					if ((sk.getTargetType() == L2SkillTargetType.TARGET_AURA  && GeoEngine.canSeeTarget(_actor, attackTarget,false)))
						
					{
						clientStopMoving(null);
						//L2Object target = attackTarget;
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return true;
					}
					else if ((sk.getTargetType() == L2SkillTargetType.TARGET_AREA || sk.getTargetType() == L2SkillTargetType.TARGET_MULTIFACE) && GeoEngine.canSeeTarget(_actor, attackTarget,false) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
				}
				break;
			}
			case PDAM:
			case MDAM:
			case BLOW:
			case DRAIN:
			case CHARGEDAM:
			case DEATHLINK:
			case CPDAM:
			case MANADAM:
			{
				if (!canAura(sk))
				{
					if (GeoEngine.canSeeTarget(_actor, attackTarget,false) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
					else
					{
						L2Character target = SkillTargetReconsider(sk);
						if (target != null)
						{
							clientStopMoving(null);
							L2Object targets = attackTarget;
							_actor.setTarget(target);
							_actor.doCast(sk);
							_actor.setTarget(targets);
							return true;
						}
					}
				}
				else
				{
					clientStopMoving(null);
					_actor.doCast(sk);
					return true;
				}
				break;
			}
			default:
			{
				if (!canAura(sk))
				{
					
					if (GeoEngine.canSeeTarget(_actor, attackTarget,false) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						_actor.doCast(sk);
						return true;
					}
					else
					{
						L2Character target = SkillTargetReconsider(sk);
						if (target != null)
						{
							clientStopMoving(null);
							L2Object targets = attackTarget;
							_actor.setTarget(target);
							_actor.doCast(sk);
							_actor.setTarget(targets);
							return true;
						}
					}
				}
				else
				{
					clientStopMoving(null);
					//L2Object targets = attackTarget;
					//_actor.setTarget(_actor);
					_actor.doCast(sk);
					//_actor.setTarget(targets);
					return true;
				}
				
			}
			break;
		}
		
		return false;
	}
	
	/**
	 * This AI task will start when ACTOR cannot move and attack range larger than distance
	 */
	private void MovementDisable()
	{
		
		double dist = 0;
		double dist2 = 0;
		int range = 0;
		try
		{
			if (_actor.getTarget() == null)
				_actor.setTarget(getAttackTarget());
			dist = Math.sqrt(_actor.getPlanDistanceSq(getAttackTarget().getX(), getAttackTarget().getY()));
			dist2 = dist - _actor.getTemplate().collisionRadius;
			range = _actor.getPhysicalAttackRange() + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius;
			if (getAttackTarget().isMoving())
			{
				dist = dist - 30;
				if (_actor.isMoving())
					dist = dist - 50;
			}
		}
		catch (NullPointerException e)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}
		
		//Check if activeChar has any skill
		if (_skillrender.hasSkill())
		{
			//-------------------------------------------------------------
			//Try to stop the target or disable the target as priority
			int random = Rnd.get(100);
			if (_skillrender.hasImmobiliseSkill() && !getAttackTarget().isImmobilized() && random < 2)
			{
				for (L2Skill sk : _skillrender._immobiliseskills)
				{
					if (sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.getCastRange() + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius <= dist2 && !canAura(sk)) || (sk.isMagic() && _actor.isMuted()) || (!sk.isMagic() && _actor.isPsychicalMuted()))
					{
						continue;
					}
					if (!GeoEngine.canSeeTarget(_actor, getAttackTarget(),false))
						continue;
					if (getAttackTarget().getFirstEffect(sk) == null)
					{
						clientStopMoving(null);
						//L2Object target = getAttackTarget();
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return;
					}
				}
			}
			//-------------------------------------------------------------
			//Same as Above, but with Mute/FEAR etc....
			if (_skillrender.hasCOTSkill() && random < 5)
			{
				for (L2Skill sk : _skillrender._cotskills)
				{
					if (sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.getCastRange() + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius <= dist2 && !canAura(sk)) || (sk.isMagic() && _actor.isMuted()) || (!sk.isMagic() && _actor.isPsychicalMuted()))
					{
						continue;
					}
					if (!GeoEngine.canSeeTarget(_actor, getAttackTarget(),false))
						continue;
					if (getAttackTarget().getFirstEffect(sk) == null)
					{
						clientStopMoving(null);
						//L2Object target = getAttackTarget();
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return;
					}
				}
			}
			//-------------------------------------------------------------
			if (_skillrender.hasDebuffSkill() && random < 8)
			{
				for (L2Skill sk : _skillrender._debuffskills)
				{
					if (sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.getCastRange() + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius <= dist2 && !canAura(sk)) || (sk.isMagic() && _actor.isMuted()) || (!sk.isMagic() && _actor.isPsychicalMuted()))
					{
						continue;
					}
					if (!GeoEngine.canSeeTarget(_actor, getAttackTarget(),false))
						continue;
					if (getAttackTarget().getFirstEffect(sk) == null)
					{
						clientStopMoving(null);
						//L2Object target = getAttackTarget();
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return;
					}
				}
			}
			//-------------------------------------------------------------
			//Some side effect skill like CANCEL or NEGATE
			if (_skillrender.hasNegativeSkill() && random < 9)
			{
				for (L2Skill sk : _skillrender._negativeskills)
				{
					if (sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.getCastRange() + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius <= dist2 && !canAura(sk)) || (sk.isMagic() && _actor.isMuted()) || (!sk.isMagic() && _actor.isPsychicalMuted()))
					{
						continue;
					}
					if (!GeoEngine.canSeeTarget(_actor, getAttackTarget(),false))
						continue;
					if (getAttackTarget().getFirstEffect(L2Effect.EffectType.BUFF) != null)
					{
						clientStopMoving(null);
						//L2Object target = getAttackTarget();
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return;
					}
				}
			}
			//-------------------------------------------------------------
			//Start ATK SKILL when nothing can be done
			if (_skillrender.hasAtkSkill())
			{
				for (L2Skill sk : _skillrender._atkskills)
				{
					if (sk.getMpConsume() >= _actor.getCurrentMp() || _actor.isSkillDisabled(sk.getId()) || (sk.getCastRange() + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius <= dist2 && !canAura(sk)) || (sk.isMagic() && _actor.isMuted()) || (!sk.isMagic() && _actor.isPsychicalMuted()))
					{
						continue;
					}
					if (!GeoEngine.canSeeTarget(_actor, getAttackTarget(),false))
						continue;
					clientStopMoving(null);
					//L2Object target = getAttackTarget();
					//_actor.setTarget(_actor);
					_actor.doCast(sk);
					//_actor.setTarget(target);
					return;
				}
			}

		}
		//timepass = timepass + 1;
		if (_actor.isMovementDisabled())
		{
			//timepass = 0;
			TargetReconsider();
			
			return;
		}
		//else if(timepass>=5)
		//{
		//	timepass = 0;
		//	AggroReconsider();
		//	return;
		//}
		
		if (dist > range || !GeoEngine.canSeeTarget(_actor, getAttackTarget(),false))
		{
			if (getAttackTarget().isMoving())
				range -= 30;
			
			if (!GeoEngine.canMoveToCoord(_actor.getX(), _actor.getY(), _actor.getZ(), getAttackTarget().getX(), getAttackTarget().getY(), getAttackTarget().getZ()))
				moveTo(getAttackTarget().getX(), getAttackTarget().getY(), getAttackTarget().getZ());
			else
			    moveToPawn(getAttackTarget(), Math.max(range, 5));

			return;
			
		}
		
		Melee(((L2Npc) _actor).getPrimaryAttack());
		return;
	}
	
	private L2Character EffectTargetReconsider(L2Skill sk, boolean positive)
	{
		if (sk == null)
			return null;
		if (sk.getSkillType() != L2SkillType.NEGATE || sk.getSkillType() != L2SkillType.CANCEL)
		{
			if (!positive)
			{
				double dist = 0;
				double dist2 = 0;
				int range = 0;
				L2Attackable actor = (L2Attackable) _actor;
				
				if (actor.getAttackByList() != null)
					for (L2Character obj : actor.getAttackByList())
					{
						if (obj == null || obj.isDead() || !GeoEngine.canSeeTarget(_actor, obj,false) || obj == getAttackTarget())
							continue;
						try
						{
							_actor.setTarget(getAttackTarget());
							dist = Math.sqrt(_actor.getPlanDistanceSq(obj.getX(), obj.getY()));
							dist2 = dist - _actor.getTemplate().collisionRadius;
							range = sk.getCastRange() + _actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
							if (obj.isMoving())
								dist2 = dist2 - 70;
						}
						catch (NullPointerException e)
						{
							continue;
						}
						if (dist2 <= range)
						{
							if (getAttackTarget().getFirstEffect(sk) == null)
								return obj;
						}
					}
				
				//----------------------------------------------------------------------
				//If there is nearby Target with aggro, start going on random target that is attackable
				for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(range))
				{
					if (obj.isDead() || !GeoEngine.canSeeTarget(_actor, obj,false))
						continue;
					try
					{
						_actor.setTarget(getAttackTarget());
						dist = Math.sqrt(_actor.getPlanDistanceSq(obj.getX(), obj.getY()));
						dist2 = dist;
						range = sk.getCastRange() + _actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
						if (obj.isMoving())
							dist2 = dist2 - 70;
					}
					catch (NullPointerException e)
					{
						continue;
					}
					if (obj instanceof L2Attackable)
					{
						if (((L2Attackable) _actor).getEnemyClan() != null && ((L2Attackable) _actor).getEnemyClan().equals(((L2Attackable) obj).getClan()))
						{
							if (dist2 <= range)
							{
								if (getAttackTarget().getFirstEffect(sk) == null)
									return obj;
							}
						}
					}
					if (obj instanceof L2PcInstance || obj instanceof L2Summon)
					{
						if (dist2 <= range)
						{
							if (getAttackTarget().getFirstEffect(sk) == null)
								return obj;
						}
					}
				}
			}
			else if (positive)
			{
				double dist = 0;
				double dist2 = 0;
				int range = 0;
				for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(range))
				{
					if (!(obj instanceof L2Attackable) || obj.isDead() || !GeoEngine.canSeeTarget(_actor, obj,false))
						continue;
					
					L2Attackable targets = ((L2Attackable) obj);
					if (((L2Attackable) _actor).getFactionId() != targets.getFactionId() && ((L2Attackable) _actor).getFactionId() != null)
						continue;
					
					try
					{
						_actor.setTarget(getAttackTarget());
						dist = Math.sqrt(_actor.getPlanDistanceSq(obj.getX(), obj.getY()));
						dist2 = dist - _actor.getTemplate().collisionRadius;
						range = sk.getCastRange() + _actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
						if (obj.isMoving())
							dist2 = dist2 - 70;
					}
					catch (NullPointerException e)
					{
						continue;
					}
					if (dist2 <= range)
					{
						if (obj.getFirstEffect(sk) == null)
							return obj;
					}
				}
			}
			return null;
		}
		else
		{
			double dist = 0;
			double dist2 = 0;
			int range = 0;
			range = sk.getCastRange() + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius;
			for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(range))
			{
				if (obj == null || obj.isDead() || !GeoEngine.canSeeTarget(_actor, obj,false))
					continue;
				try
				{
					_actor.setTarget(getAttackTarget());
					dist = Math.sqrt(_actor.getPlanDistanceSq(obj.getX(), obj.getY()));
					dist2 = dist - _actor.getTemplate().collisionRadius;
					range = sk.getCastRange() + _actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
					if (obj.isMoving())
						dist2 = dist2 - 70;
				}
				catch (NullPointerException e)
				{
					continue;
				}
				if (obj instanceof L2Attackable)
				{
					if (((L2Attackable) _actor).getEnemyClan() != null && ((L2Attackable) _actor).getEnemyClan().equals(((L2Attackable) obj).getClan()))
					{
						if (dist2 <= range)
						{
							if (getAttackTarget().getFirstEffect(L2Effect.EffectType.BUFF) != null)
								return obj;
						}
					}
				}
				if (obj instanceof L2PcInstance || obj instanceof L2Summon)
				{
					
					if (dist2 <= range)
					{
						if (getAttackTarget().getFirstEffect(L2Effect.EffectType.BUFF) != null)
							return obj;
					}
				}
			}
			return null;
		}
	}
	
	private L2Character SkillTargetReconsider(L2Skill sk)
	{
		double dist = 0;
		double dist2 = 0;
		int range = 0;
		L2Attackable actor = (L2Attackable) _actor;
		if (actor.getHateList() != null)
			for (L2Character obj : actor.getHateList())
			{
				if (obj == null || !GeoEngine.canSeeTarget(_actor, obj,false) || obj.isDead())
					continue;
				try
				{
					_actor.setTarget(getAttackTarget());
					dist = Math.sqrt(_actor.getPlanDistanceSq(obj.getX(), obj.getY()));
					dist2 = dist - _actor.getTemplate().collisionRadius;
					range = sk.getCastRange() + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius;
					//if(obj.isMoving())
					//	dist2 = dist2 - 40;
				}
				catch (NullPointerException e)
				{
					continue;
				}
				if (dist2 <= range)
				{
					return obj;
				}
			}
		
		if (!(_actor instanceof L2GuardInstance))
		{
			Collection<L2Object> objs = _actor.getKnownList().getKnownObjects().values();
			for (L2Object target : objs)
			{
				try
				{
					_actor.setTarget(getAttackTarget());
					dist = Math.sqrt(_actor.getPlanDistanceSq(target.getX(), target.getY()));
					dist2 = dist;
					range = sk.getCastRange() + _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius;
					//if(obj.isMoving())
					//	dist2 = dist2 - 40;
				}
				catch (NullPointerException e)
				{
					continue;
				}
				L2Character obj = null;
				if (target instanceof L2Character)
					obj = (L2Character) target;
				if (obj == null || !GeoEngine.canSeeTarget(_actor, obj,false) || dist2 > range)
					continue;
				if (obj instanceof L2PcInstance)
				{
					return obj;
					
				}
				if (obj instanceof L2Attackable)
				{
					if (((L2Attackable) _actor).getEnemyClan() != null && ((L2Attackable) _actor).getEnemyClan().equals(((L2Attackable) obj).getClan()))
					{
						return obj;
					}
					if (((L2Attackable) _actor).getIsChaos() != 0)
					{
						if (((L2Attackable) obj).getFactionId() != null && ((L2Attackable) obj).getFactionId().equals(((L2Attackable) _actor).getFactionId()))
							continue;
						else
							return obj;
					}
				}
				if (obj instanceof L2Summon)
				{
					return obj;
				}
				
			}
		}
		return null;
	}
	
	private void TargetReconsider()
	{
		double dist = 0;
		double dist2 = 0;
		int range = 0;
		L2Attackable actor = (L2Attackable) _actor;
		L2Character MostHate = ((L2Attackable) _actor).getMostHated();
		if (actor.getHateList() != null)
			for (L2Character obj : actor.getHateList())
			{
				if (obj == null || !GeoEngine.canSeeTarget(_actor, obj,false) || obj.isDead() || obj != MostHate || obj == _actor)
					continue;
				try
				{
					dist = Math.sqrt(_actor.getPlanDistanceSq(obj.getX(), obj.getY()));
					dist2 = dist - _actor.getTemplate().collisionRadius;
					range = _actor.getPhysicalAttackRange() + _actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
					if (obj.isMoving())
						dist2 = dist2 - 70;
				}
				catch (NullPointerException e)
				{
					continue;
				}
				
				if (dist2 <= range)
				{
					if (MostHate != null)
						actor.addDamageHate(obj, actor.getHating(MostHate), actor.getHating(MostHate));
					else
						actor.addDamageHate(obj, 2000, 2000);
					_actor.setTarget(obj);
					setAttackTarget(obj);
					return;
				}
			}
		if (!(_actor instanceof L2GuardInstance))
		{
			Collection<L2Object> objs = _actor.getKnownList().getKnownObjects().values();
			for (L2Object target : objs)
			{
				L2Character obj = null;
				if (target instanceof L2Character)
					obj = (L2Character) target;
				
				if (obj == null || !GeoEngine.canSeeTarget(_actor, obj,false) || obj.isDead() || obj != MostHate || obj == _actor || obj == getAttackTarget())
					continue;
				if (obj instanceof L2PcInstance)
				{
					if (MostHate != null)
						actor.addDamageHate(obj, actor.getHating(MostHate), actor.getHating(MostHate));
					else
						actor.addDamageHate(obj, 2000, 2000);
					_actor.setTarget(obj);
					setAttackTarget(obj);
					
				}
				else if (obj instanceof L2Attackable)
				{
					if (((L2Attackable) _actor).getEnemyClan() != null && ((L2Attackable) _actor).getEnemyClan().equals(((L2Attackable) obj).getClan()))
					{
						actor.addDamageHate(obj, 0, actor.getHating(MostHate));
						_actor.setTarget(obj);
					}
					if (((L2Attackable) _actor).getIsChaos() != 0)
					{
						if (((L2Attackable) obj).getFactionId() != null && ((L2Attackable) obj).getFactionId().equals(((L2Attackable) _actor).getFactionId()))
							continue;
						else
						{
							if (MostHate != null)
								actor.addDamageHate(obj, actor.getHating(MostHate), actor.getHating(MostHate));
							else
								actor.addDamageHate(obj, 2000, 2000);
							_actor.setTarget(obj);
							setAttackTarget(obj);
						}
					}
				}
				else if (obj instanceof L2Summon)
				{
					if (MostHate != null)
						actor.addDamageHate(obj, actor.getHating(MostHate), actor.getHating(MostHate));
					else
						actor.addDamageHate(obj, 2000, 2000);
					_actor.setTarget(obj);
					setAttackTarget(obj);
				}
			}
		}
	}
	
	
	private void AggroReconsider()
	{
		
		L2Attackable actor = (L2Attackable) _actor;
		if (actor.getHateList() != null)
		{
			
			int rand = Rnd.get(actor.getHateList().size());
			int count = 0;
			for (L2Character obj : actor.getHateList())
			{
				if (count < rand)
				{
					count++;
					continue;
				}
				
				if (obj == null || !GeoEngine.canSeeTarget(_actor, obj,false) || obj.isDead() || obj == getAttackTarget() || obj == actor)
					continue;
				
				try
				{
					_actor.setTarget(getAttackTarget());
					
				}
				catch (NullPointerException e)
				{
					continue;
				}
				if (((L2Attackable) _actor).getMostHated() != null)
					actor.addDamageHate(obj, actor.getHating(((L2Attackable) _actor).getMostHated()), actor.getHating(((L2Attackable) _actor).getMostHated()));
				else
					actor.addDamageHate(obj, 2000, 2000);
				_actor.setTarget(obj);
				setAttackTarget(obj);
				return;
				
			}
		}
		
		if (!(_actor instanceof L2GuardInstance))
		{
			Collection<L2Object> objs = _actor.getKnownList().getKnownObjects().values();
			for (L2Object target : objs)
			{
				L2Character obj = null;
				if (target instanceof L2Character)
					obj = (L2Character) target;
				else
					continue;
				if (obj == null || !GeoEngine.canSeeTarget(_actor, obj, false) || obj.isDead() || obj != ((L2Attackable) _actor).getMostHated() || obj == _actor)
					continue;
				if (obj instanceof L2PcInstance)
				{
					if (((L2Attackable) _actor).getMostHated() != null || !((L2Attackable) _actor).getMostHated().isDead())
						actor.addDamageHate(obj, actor.getHating(((L2Attackable) _actor).getMostHated()), actor.getHating(((L2Attackable) _actor).getMostHated()));
					else
						actor.addDamageHate(obj, 2000, 2000);
					_actor.setTarget(obj);
					setAttackTarget(obj);
					
				}
				else if (obj instanceof L2Attackable)
				{
					if (((L2Attackable) _actor).getEnemyClan() != null && (((L2Attackable) _actor).getEnemyClan().equals(((L2Attackable) obj).getClan())))
					{
						if (((L2Attackable) _actor).getMostHated() != null)
							actor.addDamageHate(obj, actor.getHating(((L2Attackable) _actor).getMostHated()), actor.getHating(((L2Attackable) _actor).getMostHated()));
						else
							actor.addDamageHate(obj, 2000, 2000);
						_actor.setTarget(obj);
					}
					if (((L2Attackable) _actor).getIsChaos() != 0)
					{
						if (((L2Attackable) obj).getFactionId() != null && ((L2Attackable) obj).getFactionId().equals(((L2Attackable) _actor).getFactionId()))
							continue;
						else
						{
							if (((L2Attackable) _actor).getMostHated() != null)
								actor.addDamageHate(obj, actor.getHating(((L2Attackable) _actor).getMostHated()), actor.getHating(((L2Attackable) _actor).getMostHated()));
							else
								actor.addDamageHate(obj, 2000, 2000);
							_actor.setTarget(obj);
							setAttackTarget(obj);
						}
					}
				}
				else if (obj instanceof L2Summon)
				{
					if (((L2Attackable) _actor).getMostHated() != null)
						actor.addDamageHate(obj, actor.getHating(((L2Attackable) _actor).getMostHated()), actor.getHating(((L2Attackable) _actor).getMostHated()));
					else
						actor.addDamageHate(obj, 2000, 2000);
					_actor.setTarget(obj);
					setAttackTarget(obj);
				}
			}
		}
	}
	
	private void LSkillRender()
	{
		if (_skillrender._Lrangeskills == null)
			_skillrender._Lrangeskills = ((L2Npc) _actor).getLrangeSkill();
	}
	
	private void SSkillRender()
	{
		if (_skillrender._Srangeskills == null)
			_skillrender._Srangeskills = ((L2Npc) _actor).getSrangeSkill();
	}
	
	/**
	 * Manage AI thinking actions of a L2Attackable.<BR><BR>
	 */
	@Override
	protected void onEvtThink()
	{
		// Check if the actor can't use skills and if a thinking action isn't already in progress
		if (_thinking || _actor.isAllSkillsDisabled())
			return;
		
		// Start thinking action
		_thinking = true;
		
		try
		{
			// Manage AI thinks of a L2Attackable
			if (getIntention() == AI_INTENTION_ACTIVE)
				thinkActive();
			else if (getIntention() == AI_INTENTION_ATTACK)
				thinkAttack();
		}
		finally
		{
			// Stop thinking action
			_thinking = false;
		}
	}
	
	/**
	 * Launch actions corresponding to the Event Attacked.<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Init the attack : Calculate the attack timeout, Set the _globalAggro to 0, Add the attacker to the actor _aggroList</li>
	 * <li>Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2Player</li>
	 * <li>Set the Intention to AI_INTENTION_ATTACK</li><BR><BR>
	 *
	 * @param attacker The L2Character that attacks the actor
	 *
	 */
	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		//if (_actor instanceof L2ChestInstance && !((L2ChestInstance)_actor).isInteracted())
		//{
		//((L2ChestInstance)_actor).deleteMe();
		//((L2ChestInstance)_actor).getSpawn().startRespawn();
		//return;
		//}
		
		// Calculate the attack timeout
		_attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getInstance().getGameTicks();
		
		// Set the _globalAggro to 0 to permit attack even just after spawn
		if (_globalAggro < 0)
			_globalAggro = 0;
		
		// Add the attacker to the _aggroList of the actor
		((L2Attackable) _actor).addDamageHate(attacker, 0, 1);
		
		// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2Player
		if (!_actor.isRunning())
			_actor.setRunning();
		
		// Set the Intention to AI_INTENTION_ATTACK
		if (getIntention() != AI_INTENTION_ATTACK)
		{
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
		else if (((L2Attackable) _actor).getMostHated() != getAttackTarget())
		{
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
		
		super.onEvtAttacked(attacker);
	}
	
	/**
	 * Launch actions corresponding to the Event Aggression.<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Add the target to the actor _aggroList or update hate if already present </li>
	 * <li>Set the actor Intention to AI_INTENTION_ATTACK (if actor is L2GuardInstance check if it isn't too far from its home location)</li><BR><BR>
	 *
	 * @param attacker The L2Character that attacks
	 * @param aggro The value of hate to add to the actor against the target
	 *
	 */
	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		L2Attackable me = (L2Attackable) _actor;
		
		if (target != null)
		{
			// Add the target to the actor _aggroList or update hate if already present
			me.addDamageHate(target, 0, aggro);
			
			// Set the actor AI Intention to AI_INTENTION_ATTACK
			if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
			{
				// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2Player
				if (!_actor.isRunning())
					_actor.setRunning();
				
				setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			}
		}
	}
	
	@Override
	protected void onIntentionActive()
	{
		// Cancel attack timeout
		_attackTimeout = Integer.MAX_VALUE;
		super.onIntentionActive();
	}
	
	@Override
	public void setGlobalAggro(int value)
	{
		_globalAggro = value;
	}
	
	/**
	 * @param timepass The timepass to set.
	 */
	public void setTimepass(int TP)
	{
		timepass = TP;
	}
	
	/**
	 * @return Returns the timepass.
	 */
	public int getTimepass()
	{
		return timepass;
	}
}