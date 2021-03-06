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
package com.l2jhellas.gameserver.model.actor;

import static com.l2jhellas.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.SevenSigns;
import com.l2jhellas.gameserver.SevenSignsFestival;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.cache.HtmCache;
import com.l2jhellas.gameserver.datatables.sql.BuffTemplateTable;
import com.l2jhellas.gameserver.datatables.sql.ClanTable;
import com.l2jhellas.gameserver.datatables.sql.ItemTable;
import com.l2jhellas.gameserver.datatables.sql.PolymporphTable;
import com.l2jhellas.gameserver.datatables.sql.SpawnTable;
import com.l2jhellas.gameserver.datatables.xml.CharTemplateData;
import com.l2jhellas.gameserver.datatables.xml.HelperBuffData;
import com.l2jhellas.gameserver.datatables.xml.MapRegionTable;
import com.l2jhellas.gameserver.datatables.xml.MultisellData;
import com.l2jhellas.gameserver.idfactory.IdFactory;
import com.l2jhellas.gameserver.instancemanager.CastleManager;
import com.l2jhellas.gameserver.instancemanager.DimensionalRiftManager;
import com.l2jhellas.gameserver.instancemanager.QuestManager;
import com.l2jhellas.gameserver.instancemanager.games.Lottery;
import com.l2jhellas.gameserver.model.L2Clan;
import com.l2jhellas.gameserver.model.L2DropCategory;
import com.l2jhellas.gameserver.model.L2DropData;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2MaxPolyModel;
import com.l2jhellas.gameserver.model.L2NpcAIData;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.L2SkillTargetType;
import com.l2jhellas.gameserver.model.L2SkillType;
import com.l2jhellas.gameserver.model.L2Spawn;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.L2WorldRegion;
import com.l2jhellas.gameserver.model.MobGroupTable;
import com.l2jhellas.gameserver.model.actor.instance.L2ControlTowerInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2ControllableMobInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2EventBufferInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2FestivalGuideInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2FishermanInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2TeleporterInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2WarehouseInstance;
import com.l2jhellas.gameserver.model.actor.knownlist.NpcKnownList;
import com.l2jhellas.gameserver.model.actor.stat.NpcStat;
import com.l2jhellas.gameserver.model.actor.status.NpcStatus;
import com.l2jhellas.gameserver.model.entity.Castle;
import com.l2jhellas.gameserver.model.entity.L2Event;
import com.l2jhellas.gameserver.model.entity.engines.CTF;
import com.l2jhellas.gameserver.model.entity.engines.DM;
import com.l2jhellas.gameserver.model.entity.engines.TvT;
import com.l2jhellas.gameserver.model.entity.engines.ZodiacMain;
import com.l2jhellas.gameserver.model.entity.olympiad.Olympiad;
import com.l2jhellas.gameserver.model.quest.Quest;
import com.l2jhellas.gameserver.model.quest.QuestEventType;
import com.l2jhellas.gameserver.model.quest.QuestState;
import com.l2jhellas.gameserver.model.zone.type.L2TownZone;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.clientpackets.Say2;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import com.l2jhellas.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import com.l2jhellas.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jhellas.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jhellas.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jhellas.gameserver.network.serverpackets.NpcInfo;
import com.l2jhellas.gameserver.network.serverpackets.NpcSay;
import com.l2jhellas.gameserver.network.serverpackets.RadarControl;
import com.l2jhellas.gameserver.network.serverpackets.SocialAction;
import com.l2jhellas.gameserver.network.serverpackets.StatusUpdate;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.network.serverpackets.ValidateLocation;
import com.l2jhellas.gameserver.skills.SkillTable;
import com.l2jhellas.gameserver.skills.Stats;
import com.l2jhellas.gameserver.taskmanager.DecayTaskManager;
import com.l2jhellas.gameserver.templates.L2HelperBuff;
import com.l2jhellas.gameserver.templates.L2Item;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;
import com.l2jhellas.gameserver.templates.L2Weapon;
import com.l2jhellas.util.Rnd;

/**
 * This class represents a Non-Player-Character in the world. It can be a monster or a friendly character.
 * It also uses a template to fetch some static values. The templates are hardcoded in the client, so we can rely on them.<BR>
 * <BR>
 * L2Character:<BR>
 * L2Attackable<BR>
 * L2BoxInstance<BR>
 * L2FolkInstance<BR>
 */
public class L2Npc extends L2Character
{
    public int pathfindCount = 0;
    public int pathfindTime = 0;
    
	/** The interaction distance of the L2NpcInstance(is used as offset in MovetoLocation method) */
	public static final int INTERACTION_DISTANCE = 150;

	/** The L2Spawn object that manage this L2NpcInstance */
	private L2Spawn _spawn;

	/** The flag to specify if this L2NpcInstance is busy */
	private boolean _isBusy = false;

	/** The busy message for this L2NpcInstance */
	private String _busyMessage = "";

	/** True if endDecayTask has already been called */
	volatile boolean _isDecayed = false;

	/** True if a Dwarf has used Spoil on this L2NpcInstance */
	private boolean _isSpoil = false;

	/** The castle index in the array of L2Castle this L2NpcInstance belongs to */
	private int _castleIndex = -2;

	/** Engine Parameters */
	public boolean isEventMob = false, _isEventMobCTF = false, _isCTF_throneSpawn = false, _isCTF_Flag = false;
	public boolean _isEventMobTvT = false;
	public boolean _isEventMobDM = false;
	public boolean _isEventMobVIP = false;
	public String _CTF_FlagTeamName;
	public boolean _isEventVIPNPC = false, _isEventVIPNPCEnd = false;
	public boolean isPrivateEventMob = false;

	private boolean _isInTown = false;

	private int _isSpoiledBy = 0;

	private final L2MaxPolyModel _mxcModel;

	protected RandomAnimationTask _rAniTask = null;
	private int _currentLHandId;  // normally this shouldn't change from the template, but there exist exceptions
	private int _currentRHandId;  // normally this shouldn't change from the template, but there exist exceptions
	private int _currentCollisionHeight; // used for npc grow effect skills
	private int _currentCollisionRadius; // used for npc grow effect skills
	private int _scriptValue = 0;
	
	/** Task launching the function onRandomAnimation() */
	protected class RandomAnimationTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				if (this != _rAniTask)
					return; // Shouldn't happen, but who knows... just to make sure every active npc has only one timer.
				if (isMob())
				{
					// Cancel further animation timers until intention is changed to ACTIVE again.
					if (getAI().getIntention() != AI_INTENTION_ACTIVE)
						return;
				}
				else
				{
					if (!isInActiveRegion()) // NPCs in inactive region don't run this task
						return;
					// update knownlist to remove playable which aren't in range any more
					getKnownList().forgetObjects();
				}

				if (!(isDead() || isStunned() || isSleeping() || isParalyzed()))
					onRandomAnimation();

				startRandomAnimationTimer();
			}
			catch (Throwable t)
			{
			}
		}
	}

	/**
	 * Send a packet SocialAction to all L2PcInstance in the _KnownPlayers of the L2NpcInstance and create a new RandomAnimation Task.<BR>
	 * <BR>
	 */
	public void onRandomAnimation()
	{
		// Send a packet SocialAction to all L2PcInstance in the _KnownPlayers of the L2NpcInstance
		broadcastPacket(new SocialAction(getObjectId(), Rnd.get(2, 3)),800);
	}

	/**
	 * Create a RandomAnimation Task that will be launched after the calculated delay.<BR>
	 * <BR>
	 */
	public void startRandomAnimationTimer()
	{
		if (!hasRandomAnimation())
			return;

		int minWait = isMob() ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION;
		int maxWait = isMob() ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION;

		// Calculate the delay before the next animation
		int interval = Rnd.get(minWait, maxWait) * 1000;

		// Create a RandomAnimation Task that will be launched after the calculated delay
		_rAniTask = new RandomAnimationTask();
		ThreadPoolManager.getInstance().scheduleGeneral(_rAniTask, interval);
	}

	/**
	 * Check if the server allows Random Animation.<BR>
	 * <BR>
	 */
	public boolean hasRandomAnimation()
	{
		return (Config.MAX_NPC_ANIMATION > 0);
	}

	public class destroyTemporalNPC implements Runnable
	{
		private final L2Spawn _oldSpawn;

		public destroyTemporalNPC(L2Spawn spawn)
		{
			_oldSpawn = spawn;
		}

		@Override
		public void run()
		{
			try
			{
				_oldSpawn.getLastSpawn().deleteMe();
				_oldSpawn.stopRespawn();
				SpawnTable.getInstance().deleteSpawn(_oldSpawn, false);
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}
	}

	public class destroyTemporalSummon implements Runnable
	{
		L2Summon _summon;
		L2PcInstance _player;

		public destroyTemporalSummon(L2Summon summon, L2PcInstance player)
		{
			_summon = summon;
			_player = player;
		}

		@Override
		public void run()
		{
			_summon.unSummon(_player);
		}
	}

	/**
	 * Constructor of L2NpcInstance (use L2Character constructor).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Call the L2Character constructor to set the _template of the L2Character (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li> <li>Set
	 * the name of the L2Character</li> <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li><BR>
	 * <BR>
	 * 
	 * @param objectId
	 *        Identifier of the object to initialized
	 * @param template
	 *        The L2NpcTemplate to apply to the NPC
	 */
	public L2Npc(int objectId, L2NpcTemplate template)
	{
		// Call the L2Character constructor to set the _template of the L2Character, copy skills from template to object
		// and link _calculators to NPC_STD_CALCULATOR
		super(objectId, template);

		getKnownList(); // init knownlist
		getStat(); // init stats
		getStatus(); // init status
		initCharStatusUpdateValues();

		// initialize the "current" equipment
		_currentLHandId = getTemplate().lhand;
		_currentRHandId = getTemplate().rhand;
		// initialize the "current" collisions
		_currentCollisionHeight = getTemplate().collisionHeight;
		_currentCollisionRadius = getTemplate().collisionRadius;
		// Velvet - MxC
		_mxcModel = PolymporphTable.getInstance().getModelForID(template.npcId);

		if (_mxcModel != null) // Lil Config xD
		{
			_currentCollisionHeight = CharTemplateData.getInstance().getTemplate(_mxcModel.getClassId()).collisionHeight;
			_currentCollisionRadius = CharTemplateData.getInstance().getTemplate(_mxcModel.getClassId()).collisionRadius;

			if (_mxcModel.getWeaponIdRH() <= 0 && _mxcModel.getWeaponIdLH() <= 0)
			{
				_mxcModel.setWeaponIdRH(template.rhand);
				_mxcModel.setWeaponIdLH(template.lhand);
			}
		}

		// Set the name of the L2Character
		setName(template.name);
	}

	@Override
	public NpcKnownList getKnownList()
	{
		if ((super.getKnownList() == null) || !(super.getKnownList() instanceof NpcKnownList))
			setKnownList(new NpcKnownList(this));
		return (NpcKnownList) super.getKnownList();
	}

	@Override
	public NpcStat getStat()
	{
		if ((super.getStat() == null) || !(super.getStat() instanceof NpcStat))
			setStat(new NpcStat(this));
		return (NpcStat) super.getStat();
	}

	@Override
	public NpcStatus getStatus()
	{
		if ((super.getStatus() == null) || !(super.getStatus() instanceof NpcStatus))
			setStatus(new NpcStatus(this));
		return (NpcStatus) super.getStatus();
	}

	/** Return the L2NpcTemplate of the L2NpcInstance. */
	@Override
	public final L2NpcTemplate getTemplate()
	{
		return (L2NpcTemplate) super.getTemplate();
	}

	/**
	 * Return the generic Identifier of this L2Npc contained in the L2NpcTemplate.<BR><BR>
	 */
	public int getNpcId()
	{
		return getTemplate().npcId;
	}
	/**
	 * Return the faction Identifier of this L2Npc contained in the L2NpcTemplate.<BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * If a NPC belows to a Faction, other NPC of the faction inside the Faction range will help it if it's attacked<BR><BR>
	 *
	 */
	public final String getFactionId()
	{
		return getTemplate().factionId;
	}

	/**
	 * Return the Level of this L2NpcInstance contained in the L2NpcTemplate.<BR>
	 * <BR>
	 */
	@Override
	public final int getLevel()
	{
		return getTemplate().level;
	}

	/**
	 * Return True if the L2NpcInstance is aggressive (ex : L2MonsterInstance in function of aggroRange).<BR>
	 * <BR>
	 */
	public boolean isAggressive()
	{
		return false;
	}

	/**
	 * Return the Aggro Range of this L2NpcInstance contained in the L2NpcTemplate.<BR>
	 * <BR>
	 */
	public int getAggroRange()
	{
		return getTemplate().aggroRange;
	}

	/**
	 * Return the Faction Range of this L2NpcInstance contained in the L2NpcTemplate.<BR>
	 * <BR>
	 */
	public int getFactionRange()
	{
		return getTemplate().factionRange;
	}

	/**
	 * Return True if this L2NpcInstance is undead in function of the L2NpcTemplate.<BR>
	 * <BR>
	 */
	@Override
	public boolean isUndead()
	{
		return getTemplate().isUndead;
	}

	/**
	 * Send a packet NpcInfo with state of abnormal effect to all L2PcInstance in the _KnownPlayers of the L2NpcInstance.<BR>
	 * <BR>
	 */
	@Override
	public void updateAbnormalEffect()
	{
		//NpcInfo info = new NpcInfo(this);
		//broadcastPacket(info);

		// Send a Server->Client packet NpcInfo with state of abnormal effect to all L2PcInstance in the _KnownPlayers of the L2NpcInstance
		for (L2PcInstance player : getKnownList().getKnownPlayers().values())
			if (player != null)
				player.sendPacket(new NpcInfo(this, player));
	}

	/**
	 * Return the distance under which the object must be add to _knownObject in function of the object type.<BR>
	 * <BR>
	 * <B><U> Values </U> :</B><BR>
	 * <BR>
	 * <li>object is a L2FolkInstance : 0 (don't remember it)</li> <li>object is a L2Character : 0 (don't remember it)</li> <li>object is a L2PlayableInstance : 1500</li> <li>
	 * others : 500</li><BR>
	 * <BR>
	 * <B><U> Overridden in </U> :</B><BR>
	 * <BR>
	 * <li>L2Attackable</li><BR>
	 * <BR>
	 * 
	 * @param object
	 *        The Object to add to _knownObject
	 */
	public int getDistanceToWatchObject(L2Object object)
	{
		if (object instanceof L2FestivalGuideInstance)
			return 10000;

		if (object instanceof L2NpcInstance || !(object instanceof L2Character))
			return 0;

		if (object instanceof L2Playable)
			return 1500;

		return 500;
	}

	/**
	 * Return the distance after which the object must be remove from _knownObject in function of the object type.<BR>
	 * <BR>
	 * <B><U> Values </U> :</B><BR>
	 * <BR>
	 * <li>object is not a L2Character : 0 (don't remember it)</li> <li>object is a L2FolkInstance : 0 (don't remember it)</li> <li>object is a L2PlayableInstance : 3000</li> <li>
	 * others : 1000</li><BR>
	 * <BR>
	 * <B><U> Overridden in </U> :</B><BR>
	 * <BR>
	 * <li>L2Attackable</li><BR>
	 * <BR>
	 * 
	 * @param object
	 *        The Object to remove from _knownObject
	 */
	public int getDistanceToForgetObject(L2Object object)
	{
		return 2 * getDistanceToWatchObject(object);
	}

	/**
	 * Return False.<BR>
	 * <BR>
	 * <B><U> Overridden in </U> :</B><BR>
	 * <BR>
	 * <li>L2MonsterInstance : Check if the attacker is not another L2MonsterInstance</li> <li>L2PcInstance</li><BR>
	 * <BR>
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}

	/**
	 * Return the Identifier of the item in the left hand of this L2NpcInstance contained in the L2NpcTemplate.<BR>
	 * <BR>
	 */
	public int getLeftHandItem()
	{
		return _currentLHandId;
	}

	/**
	 * Return the Identifier of the item in the right hand of this L2NpcInstance contained in the L2NpcTemplate.<BR>
	 * <BR>
	 */
	public int getRightHandItem()
	{
		return _currentRHandId;
	}

	/**
	 * Return True if this L2NpcInstance has drops that can be sweeped.<BR>
	 * <BR>
	 */
	public boolean isSpoil()
	{
		return _isSpoil;
	}

	/**
	 * Set the spoil state of this L2NpcInstance.<BR>
	 * <BR>
	 */
	public void setSpoil(boolean isSpoil)
	{
		_isSpoil = isSpoil;
	}

	public final int getIsSpoiledBy()
	{
		return _isSpoiledBy;
	}

	public final void setIsSpoiledBy(int value)
	{
		_isSpoiledBy = value;
	}

	/**
	 * Return the busy status of this L2NpcInstance.<BR>
	 * <BR>
	 */
	public final boolean isBusy()
	{
		return _isBusy;
	}

	/**
	 * Set the busy status of this L2NpcInstance.<BR>
	 * <BR>
	 */
	public void setBusy(boolean isBusy)
	{
		_isBusy = isBusy;
	}

	/**
	 * Return the busy message of this L2NpcInstance.<BR>
	 * <BR>
	 */
	public final String getBusyMessage()
	{
		return _busyMessage;
	}

	/**
	 * Set the busy message of this L2NpcInstance.<BR>
	 * <BR>
	 */
	public void setBusyMessage(String message)
	{
		_busyMessage = message;
	}

	protected boolean canTarget(L2PcInstance player)
	{
		if (player.isOutOfControl())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		// TODO: More checks...

		return true;
	}

	protected boolean canInteract(L2PcInstance player)
	{
		// TODO: NPC busy check etc...

		//if (!canTarget(player))
		//    return false;

		if (!isInsideRadius(player, INTERACTION_DISTANCE, false, false))
			return false;

		return true;
	}

	/**
	 * Manage actions when a player click on the L2NpcInstance.<BR>
	 * <BR>
	 * <B><U> Actions on first click on the L2NpcInstance (Select it)</U> :</B><BR>
	 * <BR>
	 * <li>Set the L2NpcInstance as target of the L2PcInstance player (if necessary)</li> <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the
	 * select window)</li> <li>If L2NpcInstance is autoAttackable, send a Server->Client packet StatusUpdate to the L2PcInstance in order to update L2NpcInstance HP bar</li> <li>
	 * Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client</li><BR>
	 * <BR>
	 * <B><U> Actions on second click on the L2NpcInstance (Attack it/Interact with it)</U> :</B><BR>
	 * <BR>
	 * <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the select window)</li> <li>If L2NpcInstance is autoAttackable, notify the L2PcInstance
	 * AI with AI_INTENTION_ATTACK (after a height verification)</li> <li>If L2NpcInstance is NOT autoAttackable, notify the L2PcInstance AI with AI_INTENTION_INTERACT (after a
	 * distance verification) and show message</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Each group of Server->Client packet must be terminated by a ActionFailed packet in order to avoid
	 * that client wait an other packet</B></FONT><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Client packet : Action, AttackRequest</li><BR>
	 * <BR>
	 * <B><U> Overridden in </U> :</B><BR>
	 * <BR>
	 * <li>L2ArtefactInstance : Manage only first click to select Artefact</li><BR>
	 * <BR>
	 * <li>L2GuardInstance :</li><BR>
	 * <BR>
	 * 
	 * @param player
	 *        The L2PcInstance that start an action on the L2NpcInstance
	 */
	@Override
	public void onAction(L2PcInstance player)
	{
		if (!canTarget(player))
			return;

		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			if (Config.DEBUG)
				_log.fine("new target selected:" + getObjectId());

			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Check if the player is attackable (without a forced attack)
			if (isAutoAttackable(player))
			{
				// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
				// The player.getLevel() - getLevel() permit to display the correct color in the select window
				MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
				player.sendPacket(my);

				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				StatusUpdate su = new StatusUpdate(getObjectId());
				su.addAttribute(StatusUpdate.CUR_HP, (int) getCurrentHp());
				su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
				player.sendPacket(su);
			}
			else
			{
				// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
				MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
				player.sendPacket(my);
			}

			// Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			player.sendPacket(new ValidateLocation(this));
			// Check if the player is attackable (without a forced attack) and isn't dead
			if (isAutoAttackable(player) && !isAlikeDead())
			{
				// Check the height difference
				if (Math.abs(player.getZ() - getZ()) < 400) // this max heigth difference might need some tweaking
				{
					// Set the L2PcInstance Intention to AI_INTENTION_ATTACK
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
					// player.startAttack(this);
				}
				else
				{
					// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
					player.sendPacket(ActionFailed.STATIC_PACKET);
				}
			}
			else if (!isAutoAttackable(player))
			{
				// Calculate the distance between the L2PcInstance and the L2NpcInstance
				if (!canInteract(player))
				{
					// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
				}
				else
				{
					// Send a Server->Client packet SocialAction to the all L2PcInstance on the _knownPlayer of the L2NpcInstance
					// to display a social action of the L2NpcInstance on their client
					SocialAction sa = new SocialAction(getObjectId(), Rnd.get(8));
					broadcastPacket(sa,1200);

					/// Open a chat window on client with the text of the L2NpcInstance
					if (isEventMob)
						L2Event.showEventHtml(player, String.valueOf(getObjectId()));
					else if (_isEventMobTvT)
						TvT.showEventHtml(player, String.valueOf(getObjectId()));
					else if (_isEventMobDM)
						DM.showEventHtml(player, String.valueOf(getObjectId()));
					else if (_isEventMobCTF)
						CTF.showEventHtml(player, String.valueOf(getObjectId()));
					else if (_isCTF_Flag && player._inEventCTF)
						CTF.showFlagHtml(player, String.valueOf(getObjectId()), _CTF_FlagTeamName);
					else if (_isCTF_throneSpawn)
						CTF.CheckRestoreFlags();
					else
					{
						List<Quest> qlst = getTemplate().getEventQuests(QuestEventType.ON_FIRST_TALK);
						if (qlst != null && qlst.size() == 1)
							qlst.get(0).notifyFirstTalk(this, player);

						else
							showChatWindow(player, 0);
					}
				}
			}
			else
				player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	/**
	 * Manage and Display the GM console to modify the L2NpcInstance (GM only).<BR>
	 * <BR>
	 * <B><U> Actions (If the L2PcInstance is a GM only)</U> :</B><BR>
	 * <BR>
	 * <li>Set the L2NpcInstance as target of the L2PcInstance player (if necessary)</li> <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the
	 * select window)</li> <li>If L2NpcInstance is autoAttackable, send a Server->Client packet StatusUpdate to the L2PcInstance in order to update L2NpcInstance HP bar</li> <li>
	 * Send a Server->Client NpcHtmlMessage() containing the GM console about this L2NpcInstance</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Each group of Server->Client packet must be terminated by a ActionFailed packet in order to avoid
	 * that client wait an other packet</B></FONT><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Client packet : Action</li><BR>
	 * <BR>
	 */
	@Override
	public void onActionShift(L2PcInstance player)
	{
		if (player == null)
            return;

		// Check if the L2PcInstance is a GM
		if (!Config.ALT_GAME_VIEWNPC && player.getAccessLevel().isGm())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			// The player.getLevel() - getLevel() permit to display the correct color in the select window
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);

			// Check if the player is attackable (without a forced attack)
			if (isAutoAttackable(player))
			{
				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				StatusUpdate su = new StatusUpdate(getObjectId());
				su.addAttribute(StatusUpdate.CUR_HP, (int) getCurrentHp());
				su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
				player.sendPacket(su);
			}

			// Send a Server->Client NpcHtmlMessage() containing the GM console about this L2NpcInstance
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			StringBuilder html1 = new StringBuilder("<html><body><center><font color=\"LEVEL\">NPC Information</font></center>");
			String className = getClass().getName().substring(43);
			html1.append("<br>");

			html1.append("Instance Type: " + className + "<br1>Faction: " + getFactionId() + "<br1>Location ID: " + (getSpawn() != null ? getSpawn().getLocation() : 0) + "<br1>");

			if (this instanceof L2ControllableMobInstance)
				html1.append("Mob Group: " + MobGroupTable.getInstance().getGroupForMob((L2ControllableMobInstance) this).getGroupId() + "<br>");
			else
				html1.append("Respawn Time: " + (getSpawn() != null ? (getSpawn().getRespawnDelay() / 1000) + "  Seconds<br>" : "?  Seconds<br>"));

			html1.append("<table border=\"0\" width=\"100%\">");
			html1.append("<tr><td>Object ID</td><td>" + getObjectId() + "</td><td>NPC ID</td><td>" + getTemplate().npcId + "</td></tr>");
			html1.append("<tr><td>Castle</td><td>" + getCastle().getCastleId() + "</td><td>Coords</td><td>" + getX() + "," + getY() + "," + getZ() + "</td></tr>");
			html1.append("<tr><td>Level</td><td>" + getLevel() + "</td><td>Aggro</td><td>" + ((this instanceof L2Attackable) ? ((L2Attackable) this).getAggroRange() : 0) + "</td></tr>");
			html1.append("</table><br>");

			html1.append("<font color=\"LEVEL\">Combat</font>");
			html1.append("<table border=\"0\" width=\"100%\">");
			html1.append("<tr><td>Current HP</td><td>" + getCurrentHp() + "</td><td>Current MP</td><td>" + getCurrentMp() + "</td></tr>");
			html1.append("<tr><td>Max.HP</td><td>" + (int) (getMaxHp() / getStat().calcStat(Stats.MAX_HP, 1, this, null)) + "*" + getStat().calcStat(Stats.MAX_HP, 1, this, null) + "</td><td>Max.MP</td><td>" + getMaxMp() + "</td></tr>");
			html1.append("<tr><td>P.Atk.</td><td>" + getPAtk(null) + "</td><td>M.Atk.</td><td>" + getMAtk(null, null) + "</td></tr>");
			html1.append("<tr><td>P.Def.</td><td>" + getPDef(null) + "</td><td>M.Def.</td><td>" + getMDef(null, null) + "</td></tr>");
			html1.append("<tr><td>Accuracy</td><td>" + getAccuracy() + "</td><td>Evasion</td><td>" + getEvasionRate(null) + "</td></tr>");
			html1.append("<tr><td>Critical</td><td>" + getCriticalHit(null, null) + "</td><td>Speed</td><td>" + getRunSpeed() + "</td></tr>");
			html1.append("<tr><td>Atk.Speed</td><td>" + getPAtkSpd() + "</td><td>Cast.Speed</td><td>" + getMAtkSpd() + "</td></tr>");
			html1.append("</table><br>");

			html1.append("<font color=\"LEVEL\">Basic Stats</font>");
			html1.append("<table border=\"0\" width=\"100%\">");
			html1.append("<tr><td>STR</td><td>" + getSTR() + "</td><td>DEX</td><td>" + getDEX() + "</td><td>CON</td><td>" + getCON() + "</td></tr>");
			html1.append("<tr><td>INT</td><td>" + getINT() + "</td><td>WIT</td><td>" + getWIT() + "</td><td>MEN</td><td>" + getMEN() + "</td></tr>");
			html1.append("</table>");

			html1.append("<br><center><table>");
			html1.append("<tr><td><button value=\"Edit NPC\" action=\"bypass -h admin_edit_npc " + getTemplate().npcId + "\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			html1.append("<tr><td><button value=\"Show DropList\" action=\"bypass -h admin_show_droplist " + getTemplate().npcId + "\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			html1.append("<tr><td><button value=\"Show Skillist\" action=\"bypass -h admin_show_skilllist_npc " + getTemplate().npcId + "\" width=100 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			html1.append("<tr><td align=center><button value=\"Kill\" action=\"bypass -h admin_kill\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			html1.append("<tr><td align=center><button value=\"Delete\" action=\"bypass -h admin_delete\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			html1.append("</table></center><br>");
			html1.append("</body></html>");

			html.setHtml(html1.toString());
			player.sendPacket(html);
		}
		else if (Config.ALT_GAME_VIEWNPC && player.getAccessLevel().isGm())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			// The player.getLevel() - getLevel() permit to display the correct color in the select window
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);

			// Check if the player is attackable (without a forced attack)
			if (isAutoAttackable(player))
			{
				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				StatusUpdate su = new StatusUpdate(getObjectId());
				su.addAttribute(StatusUpdate.CUR_HP, (int) getCurrentHp());
				su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
				player.sendPacket(su);
			}

			NpcHtmlMessage html = new NpcHtmlMessage(0);
			StringBuilder html1 = new StringBuilder("<html><body>");

			html1.append("<br><center><font color=\"LEVEL\">[Combat Stats]</font></center>");
			html1.append("<table border=0 width=\"100%\">");
			html1.append("<tr><td>Max.HP</td><td>" + (int) (getMaxHp() / getStat().calcStat(Stats.MAX_HP, 1, this, null)) + "*" + (int) getStat().calcStat(Stats.MAX_HP, 1, this, null) + "</td><td>Max.MP</td><td>" + getMaxMp() + "</td></tr>");
			html1.append("<tr><td>P.Atk.</td><td>" + getPAtk(null) + "</td><td>M.Atk.</td><td>" + getMAtk(null, null) + "</td></tr>");
			html1.append("<tr><td>P.Def.</td><td>" + getPDef(null) + "</td><td>M.Def.</td><td>" + getMDef(null, null) + "</td></tr>");
			html1.append("<tr><td>Accuracy</td><td>" + getAccuracy() + "</td><td>Evasion</td><td>" + getEvasionRate(null) + "</td></tr>");
			html1.append("<tr><td>Critical</td><td>" + getCriticalHit(null, null) + "</td><td>Speed</td><td>" + getRunSpeed() + "</td></tr>");
			html1.append("<tr><td>Atk.Speed</td><td>" + getPAtkSpd() + "</td><td>Cast.Speed</td><td>" + getMAtkSpd() + "</td></tr>");
			html1.append("<tr><td>Race</td><td>" + getTemplate().race + "</td><td></td><td></td></tr>");
			html1.append("</table>");

			html1.append("<br><center><font color=\"LEVEL\">[Basic Stats]</font></center>");
			html1.append("<table border=0 width=\"100%\">");
			html1.append("<tr><td>STR</td><td>" + getSTR() + "</td><td>DEX</td><td>" + getDEX() + "</td><td>CON</td><td>" + getCON() + "</td></tr>");
			html1.append("<tr><td>INT</td><td>" + getINT() + "</td><td>WIT</td><td>" + getWIT() + "</td><td>MEN</td><td>" + getMEN() + "</td></tr>");
			html1.append("</table>");

			html1.append("<tr><td><button value=\"Edit NPC\" action=\"bypass -h admin_edit_npc " + getTemplate().npcId + "\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			html1.append("<tr><td><button value=\"Show DropList\" action=\"bypass -h admin_show_droplist " + getTemplate().npcId + "\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			html1.append("<tr><td><button value=\"Show Skillist\" action=\"bypass -h admin_show_skilllist_npc " + getTemplate().npcId + "\" width=100 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			html1.append("<tr><td align=center><button value=\"Kill\" action=\"bypass -h admin_kill\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			html1.append("<tr><td align=center><button value=\"Delete\" action=\"bypass -h admin_delete\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");

			html1.append("<center><font color=\"LEVEL\">[Drop Info]</font></center>");
			html1.append("<table border=1 width=\"100%\">");
			html1.append("<tr><td><center>Item Name</center></td><td width=\"55\" valign=middle align=center><center>Category</center></td><td width=\"50\" valign=middle align=center><center>Chance</center></td></tr>");

			if (getTemplate().getDropData() != null)
				for (L2DropCategory cat : getTemplate().getDropData())
					for (L2DropData drop : cat.getAllDrops())
					{
						String name = ItemTable.getInstance().getTemplate(drop.getItemId()).getItemName();
						html1.append("<tr><td><font color=\"33EEEE\">" + name + "</font></td><td width=\"55\" valign=middle align=center>" + (drop.isQuestDrop() ? "<font color=\"FF6600\">Quest</font>" : (cat.isSweep() ? "<font color=\"LEVEL\">Sweep</font>" : "<font color=\"33FF77\">Drop</font>")) + "</td><td width=\"50\" valign=middle align=center>" + (drop.getChance() >= 10000 ? (double)drop.getChance() / 10000 : drop.getChance() < 10000 ? (double)drop.getChance() / 10000 : "N/A") + "%</td></tr>");
					}

			html1.append("</table>");
			html1.append("</body></html>");

			html.setHtml(html1.toString());
			player.sendPacket(html);
		}

		if (Config.PLAYER_ALT_GAME_VIEWNPC && !player.getAccessLevel().isGm())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			// The player.getLevel() - getLevel() permit to display the correct color in the select window
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);

			// Check if the player is attackable (without a forced attack)
			if (isAutoAttackable(player))
			{
				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				StatusUpdate su = new StatusUpdate(getObjectId());
				su.addAttribute(StatusUpdate.CUR_HP, (int) getCurrentHp());
				su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
				player.sendPacket(su);
			}

			NpcHtmlMessage html = new NpcHtmlMessage(0);
			StringBuilder html1 = new StringBuilder("<html><body>");
			html1.append("<center><font color=\"LEVEL\">[Drop Info]</font></center>");
			html1.append("<table border=1 width=\"100%\">");
			html1.append("<tr><td><center>Item Name</center></td><td width=\"55\" valign=middle align=center><center>Category</center></td><td width=\"50\" valign=middle align=center><center>Chance</center></td></tr>");

			if (getTemplate().getDropData() != null)
				for (L2DropCategory cat : getTemplate().getDropData())
					for (L2DropData drop : cat.getAllDrops())
					{
						String name = ItemTable.getInstance().getTemplate(drop.getItemId()).getItemName();
						html1.append("<tr><td><font color=\"33EEEE\">" + name + "</font></td><td width=\"55\" valign=middle align=center>" + (drop.isQuestDrop() ? "<font color=\"FF6600\">Quest</font>" : (cat.isSweep() ? "<font color=\"LEVEL\">Sweep</font>" : "<font color=\"33FF77\">Drop</font>")) + "</td><td width=\"50\" valign=middle align=center>" + (drop.getChance() >= 10000 ? (double)drop.getChance() / 10000 : drop.getChance() < 10000 ? (double)drop.getChance() / 10000 : "N/A") + "%</td></tr>");
					}

			html1.append("</table>");
			html1.append("</body></html>");

			html.setHtml(html1.toString());
			player.sendPacket(html);
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	/** Return the L2Castle this L2NpcInstance belongs to. */
	public final Castle getCastle()
	{
		// Get castle this NPC belongs to (excluding L2Attackable)
				if (_castleIndex < 0)
				{
					L2TownZone town = MapRegionTable.getTown(getX(), getY(), getZ());
					
					if (town != null)
						_castleIndex = CastleManager.getInstance().getCastleIndex(town.getTaxById());
					
					if (_castleIndex < 0)
						_castleIndex = CastleManager.getInstance().findNearestCastleIndex(this);
					else
						_isInTown = true; // Npc was spawned in town
				}
				
				if (_castleIndex < 0)
					return null;
				
				return CastleManager.getInstance().getCastles().get(_castleIndex);
	}

	public final boolean getIsInTown()
	{
		if (_castleIndex < 0)
			getCastle();
		return _isInTown;
	}

	/**
	 * Open a quest or chat window on client with the text of the L2NpcInstance in function of the command.<BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Client packet : RequestBypassToServer</li><BR>
	 * <BR>
	 * 
	 * @param command
	 *        The command string received from client
	 */
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		//if (canInteract(player))
		{
			if (isBusy() && getBusyMessage().length() > 0)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);

				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile("data/html/npcbusy.htm");
				html.replace("%busymessage%", getBusyMessage());
				html.replace("%npcname%", getName());
				html.replace("%playername%", player.getName());
				player.sendPacket(html);
			}
			else if (command.equalsIgnoreCase("TerritoryStatus"))
			{
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				{
					if (getCastle().getOwnerId() > 0)
					{
						html.setFile("data/html/territorystatus.htm");
						L2Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());
						html.replace("%clanname%", clan.getName());
						html.replace("%clanleadername%", clan.getLeaderName());
					}
					else
					{
						html.setFile("data/html/territorynoclan.htm");
					}
				}
				html.replace("%castlename%", getCastle().getName());
				html.replace("%taxpercent%", "" + getCastle().getTaxPercent());
				html.replace("%objectId%", String.valueOf(getObjectId()));
				{
					if (getCastle().getCastleId() > 6)
					{
						html.replace("%territory%", "The Kingdom of Elmore");
					}
					else
					{
						html.replace("%territory%", "The Kingdom of Aden");
					}
				}
				player.sendPacket(html);
			}
			else if (command.startsWith("Quest"))
			{
				String quest = "";
				try
				{
					quest = command.substring(5).trim();
				}
				catch (IndexOutOfBoundsException ioobe)
				{
				}
				if (quest.isEmpty())
					showQuestWindow(player);
				else
					showQuestWindow(player, quest);
			}
			else if (command.startsWith("Chat"))
			{
				int val = 0;
				try
				{
					val = Integer.parseInt(command.substring(5));
				}
				catch (IndexOutOfBoundsException ioobe)
				{
				}
				catch (NumberFormatException nfe)
				{
				}
				showChatWindow(player, val);
			}
			else if (command.startsWith("Link"))
			{
				String path = command.substring(5).trim();
				if (path.indexOf("..") != -1)
					return;
				String filename = "data/html/" + path;
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
			}
			else if (command.startsWith("NobleTeleport"))
			{
				if (!player.isNoble())
				{
					String filename = "data/html/teleporter/nobleteleporter-no.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(filename);
					html.replace("%objectId%", String.valueOf(getObjectId()));
					html.replace("%npcname%", getName());
					player.sendPacket(html);
					return;
				}
				int val = 0;
				try
				{
					val = Integer.parseInt(command.substring(5));
				}
				catch (IndexOutOfBoundsException ioobe)
				{
				}
				catch (NumberFormatException nfe)
				{
				}
				showChatWindow(player, val);
			}
			else if (command.startsWith("Loto"))
			{
				int val = 0;
				try
				{
					val = Integer.parseInt(command.substring(5));
				}
				catch (IndexOutOfBoundsException ioobe)
				{
				}
				catch (NumberFormatException nfe)
				{
				}
				if (val == 0)
				{
					// new loto ticket
					for (int i = 0; i < 5; i++)
						player.setLoto(i, 0);
				}
				showLotoWindow(player, val);
			}
			else if (command.startsWith("CPRecovery"))
			{
				makeCPRecovery(player);
			}
			else if (command.startsWith("SupportMagic"))
			{
				makeSupportMagic(player);
			}
			else if (command.startsWith("GiveBlessing"))
			{
				giveBlessingSupport(player);
			}
			else if (command.startsWith("multisell"))
			{
				MultisellData.getInstance().SeparateAndSend(Integer.parseInt(command.substring(9).trim()), player, false, getCastle().getTaxRate());
			}
			else if (command.startsWith("exc_multisell"))
			{
				MultisellData.getInstance().SeparateAndSend(Integer.parseInt(command.substring(13).trim()), player, true, getCastle().getTaxRate());
			}
			else if (command.startsWith("Augment"))
			{
				int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
				switch (cmdChoice)
				{
					case 1:
						player.sendPacket(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED);
						player.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
					break;
					case 2:
						player.sendPacket(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
						player.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
					break;
				}
			}
			else if (command.startsWith("MakeBuffs"))
			{
				makeBuffs(player, command.substring(9).trim());
			}
			else if (command.startsWith("npcfind_byid"))
			{
				try
				{
					L2Spawn spawn = SpawnTable.getInstance().getTemplate(Integer.parseInt(command.substring(12).trim()));

					if (spawn != null)
						player.sendPacket(new RadarControl(0, 1, spawn.getLocx(), spawn.getLocy(), spawn.getLocz()));
				}
				catch (NumberFormatException nfe)
				{
					player.sendMessage("Wrong command parameters");
				}
			}
			else if (command.startsWith("EnterRift"))
			{
				try
				{
					Byte b1 = Byte.parseByte(command.substring(10)); // Selected Area: Recruit, Soldier etc
					DimensionalRiftManager.getInstance().start(player, b1, this);
				}
				catch (Exception e)
				{
				}
			}
			else if (command.startsWith("ChangeRiftRoom"))
			{
				if (player.isInParty() && player.getParty().isInDimensionalRift())
				{
					player.getParty().getDimensionalRift().manualTeleport(player, this);
				}
				else
				{
					DimensionalRiftManager.getInstance().handleCheat(player, this);
				}
			}
			else if (command.startsWith("ExitRift"))
			{
				if (player.isInParty() && player.getParty().isInDimensionalRift())
				{
					player.getParty().getDimensionalRift().manualExitRift(player, this);
				}
				else
				{
					DimensionalRiftManager.getInstance().handleCheat(player, this);
				}
			}
		}
	}

	/**
	 * Return null (regular NPCs don't have weapons instances).<BR>
	 * <BR>
	 */
	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		// regular NPCs don't have weapons instances
		return null;
	}

	/**
	 * Return the weapon item equipped in the right hand of the L2NpcInstance or null.<BR>
	 * <BR>
	 */
	@Override
	public L2Weapon getActiveWeaponItem()
	{
		// Get the weapon identifier equipped in the right hand of the L2NpcInstance
		int weaponId = getTemplate().rhand;

		if (weaponId < 1)
			return null;

		// Get the weapon item equipped in the right hand of the L2NpcInstance
		L2Item item = ItemTable.getInstance().getTemplate(getTemplate().rhand);

		if (!(item instanceof L2Weapon))
			return null;

		return (L2Weapon) item;
	}

	public void giveBlessingSupport(L2PcInstance player)
	{
		if (player == null)
			return;

		// Blessing of protection - author kerberos_20. Used codes from Rayan - L2Emu project.
		// Prevent a cursed weapon wielder of being buffed - I think no need of that because karma check > 0
		if (player.isCursedWeaponEquiped())
			return;

		int player_level = player.getLevel();
		// Select the player
		setTarget(player);
		// If the player is too high level, display a message and return
		if (player_level > 39 || player.getClassId().level() >= 2)
		{
			String content = "<html><body>Newbie Guide:<br>I'm sorry, but you are not eligible to receive the protection blessing.<br1>It can only be bestowed on <font color=\"LEVEL\">characters below level 39 who have not made a seccond transfer.</font></body></html>";
			insertObjectIdAndShowChatWindow(player, content);
			return;
		}
		L2Skill skill = SkillTable.getInstance().getInfo(5182, 1);
		doCast(skill);
	}

	/**
	 * Return null (regular NPCs don't have weapons instances).<BR>
	 * <BR>
	 */
	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		// regular NPCs don't have weapons instances
		return null;
	}

	/**
	 * Return the weapon item equipped in the left hand of the L2NpcInstance or null.<BR>
	 * <BR>
	 */
	@Override
	public L2Weapon getSecondaryWeaponItem()
	{
		// Get the weapon identifier equipped in the right hand of the L2NpcInstance
		int weaponId = getTemplate().lhand;

		if (weaponId < 1)
			return null;

		// Get the weapon item equipped in the right hand of the L2NpcInstance
		L2Item item = ItemTable.getInstance().getTemplate(getTemplate().lhand);

		if (!(item instanceof L2Weapon))
			return null;

		return (L2Weapon) item;
	}

	/**
	 * Throws an action command to L2BufferInstance.<br>
	 * 
	 * @param player
	 *        --> Target player
	 * @param buffTemplate
	 *        --> Name of the Buff Template to Add
	 */
	public void makeBuffs(L2PcInstance player, String buffTemplate)
	{
		int _templateId = 0;

		try
		{
			_templateId = Integer.parseInt(buffTemplate);
		}
		catch (NumberFormatException e)
		{
			_templateId = BuffTemplateTable.getInstance().getTemplateIdByName(buffTemplate);
		}
		if (_templateId > 0)
		{
			L2EventBufferInstance.makeBuffs(player, _templateId, this, true);
		}
	}

	/**
	 * Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order
	 * to display the message of the L2NpcInstance.<BR>
	 * <BR>
	 * 
	 * @param player
	 *        The L2PcInstance who talks with the L2NpcInstance
	 * @param content
	 *        The text of the L2NpcMessage
	 */
	public void insertObjectIdAndShowChatWindow(L2PcInstance player, String content)
	{
		// Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order to display the message of the L2NpcInstance
		content = content.replaceAll("%objectId%", String.valueOf(getObjectId()));
		NpcHtmlMessage npcReply = new NpcHtmlMessage(getObjectId());
		npcReply.setHtml(content);
		player.sendPacket(npcReply);
	}

	/**
	 * Return the pathfile of the selected HTML file in function of the npcId and of the page number.<BR>
	 * <BR>
	 * <B><U> Format of the pathfile </U> :</B><BR>
	 * <BR>
	 * <li>if the file exists on the server (page number = 0) : <B>data/html/default/12006.htm</B> (npcId-page number)</li> <li>if the file exists on the server (page number > 0) :
	 * <B>data/html/default/12006-1.htm</B> (npcId-page number)</li> <li>if the file doesn't exist on the server : <B>data/html/npcdefault.htm</B> (message :
	 * "I have nothing to say to you")</li><BR>
	 * <BR>
	 * <B><U> Overridden in </U> :</B><BR>
	 * <BR>
	 * <li>L2GuardInstance : Set the pathfile to data/html/guard/12006-1.htm (npcId-page number)</li><BR>
	 * <BR>
	 * 
	 * @param npcId
	 *        The Identifier of the L2NpcInstance whose text must be display
	 * @param val
	 *        The number of the page to display
	 */
	public String getHtmlPath(int npcId, int val)
	{
		String filename;
		
		if (val == 0)
			filename = "data/html/default/" + npcId + ".htm";
		else
			filename = "data/html/default/" + npcId + "-" + val + ".htm";
		
		if (HtmCache.getInstance().isLoadable(filename))
			return filename;
		
		return "data/html/npcdefault.htm";
	}

	/**
	 * Open a choose quest window on client with all quests available of the L2NpcInstance.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance</li><BR>
	 * <BR>
	 * 
	 * @param player
	 *        The L2PcInstance that talk with the L2NpcInstance
	 * @param quests
	 *        The table containing quests of the L2NpcInstance
	 */
	public void showQuestChooseWindow(L2PcInstance player, Quest[] quests)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<html><body><title>Talk about:</title><br>");

		for (Quest q : quests)
		{
			sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(q.getName()).append("\">").append(q.getDescr()).append("</a><br>");
		}

		sb.append("</body></html>");

		// Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order to display the message of the L2NpcInstance
		insertObjectIdAndShowChatWindow(player, sb.toString());
	}

	/**
	 * Open a quest window on client with the text of the L2NpcInstance.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the text of the quest state in the folder data/scripts/quests/questId/stateId.htm</li> <li>Send a Server->Client NpcHtmlMessage containing the text of the
	 * L2NpcInstance to the L2PcInstance</li> <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet</li><BR>
	 * <BR>
	 * 
	 * @param player
	 *        The L2PcInstance that talk with the L2NpcInstance
	 * @param questId
	 *        The Identifier of the quest to display the message
	 */
	public void showQuestWindow(L2PcInstance player, String questId)
	{
		String content = null;

		Quest q = QuestManager.getInstance().getQuest(questId);

		// Get the state of the selected quest
		QuestState qs = player.getQuestState(questId);

		if (q == null)
		{
			// no quests found
			content = "<html><body>You are asd either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
		}
		else
		{
			if ((q.getQuestId() >= 1 && q.getQuestId() < 1000) && (player.getWeightPenalty() >= 3 || player.GetInventoryLimit() * 0.8 <= player.getInventory().getSize()))
			{
				player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
				return;
			}

			if (qs == null)
			{
				if (q.getQuestId() >= 1 && q.getQuestId() < 1000)
				{
					Quest[] questList = player.getAllActiveQuests();
					if (questList.length >= 25)
					{
						return;
					}
				}
				// check for start point
				List<Quest> qlst = getTemplate().getEventQuests(QuestEventType.QUEST_START);

				if (qlst != null && qlst.size() > 0)
				{
					for (int i = 0; i < qlst.size(); i++)
					{
						if (qlst.get(i).equals(q))
						{
							qs = q.newQuestState(player);
							break;
						}
					}
				}
			}
		}
		if (qs != null)
		{
			// If the quest is already started, no need to show a window
			if (!qs.getQuest().notifyTalk(this, qs.getPlayer()))
				return;

			questId = qs.getQuest().getName();
			String stateId = QuestState.getStateName(qs.getState());
			String path  = "data/scripts/quests/" + questId + "/" + stateId + ".htm";
		    content = HtmCache.getInstance().getHtm(path); // TODO path for quests html

			if (Config.DEBUG)
			{
				if (content != null)
				{
					_log.fine("Showing quest window for quest " + questId + " html path: " + path);
				}
				else
				{
					_log.fine("File not exists for quest " + questId + " html path: " + path);
				}
			}
			else
			{
				_log.fine("File not exists for quest " + questId + " html path: " + path);
			}
		}

		// Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in
		// order to display the message of the L2NpcInstance
		if (content != null)
			insertObjectIdAndShowChatWindow(player, content);

		// Send a Server->Client ActionFailed to the L2PcInstance in order to
		// avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	/**
	 * Collect awaiting quests/start points and display a QuestChooseWindow (if several available) or QuestWindow.<BR>
	 * <BR>
	 * 
	 * @param player
	 *        The L2PcInstance that talk with the L2NpcInstance
	 */
	public void showQuestWindow(L2PcInstance player)
	{
         List<Quest> quests = new ArrayList<Quest>();
		
		List<Quest> awaits = getTemplate().getEventQuests(QuestEventType.ON_TALK);
		if (awaits != null)
		{
			for (Quest quest : awaits)
			{
				if (quest == null || !quest.isRealQuest() || quests.contains(quest))
					continue;
				
				QuestState qs = player.getQuestState(quest.getName());
				if (qs == null || qs.isCreated())
					continue;
				
				quests.add(quest);
			}
		}
		
		List<Quest> starts = getTemplate().getEventQuests(QuestEventType.QUEST_START);
		if (starts != null)
		{
			for (Quest quest : starts)
			{
				if (quest == null || !quest.isRealQuest() || quests.contains(quest))
					continue;
				
				quests.add(quest);
			}
		}
		
		if (quests.isEmpty())
			showQuestWindow(player, "");
		else if (quests.size() == 1)
			showQuestWindow(player, quests.get(0).getName());
		else
			showQuestChooseWindow(player, quests.toArray(new Quest[quests.size()]));
	}

	/**
	 * Open a Loto window on client with the text of the L2NpcInstance.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the text of the selected HTML file in function of the npcId and of the page number</li> <li>Send a Server->Client NpcHtmlMessage containing the text of the
	 * L2NpcInstance to the L2PcInstance</li> <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet</li><BR>
	 * 
	 * @param player
	 *        The L2PcInstance that talk with the L2NpcInstance
	 * @param val
	 *        The number of the page of the L2NpcInstance to display
	 */
	// 0 - first buy lottery ticket window
	// 1-20 - buttons
	// 21 - second buy lottery ticket window
	// 22 - selected ticket with 5 numbers
	// 23 - current lottery jackpot
	// 24 - Previous winning numbers/Prize claim
	// >24 - check lottery ticket by item object id
	public void showLotoWindow(L2PcInstance player, int val)
	{
		int npcId = getTemplate().npcId;
		String filename;
		SystemMessage sm;
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

		if (val == 0) // 0 - first buy lottery ticket window
		{
			filename = (getHtmlPath(npcId, 1));
			html.setFile(filename);
		}
		else if (val >= 1 && val <= 21) // 1-20 - buttons, 21 - second buy lottery ticket window
		{
			if (!Lottery.getInstance().isStarted())
			{
				//tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			if (!Lottery.getInstance().isSellableTickets())
			{
				//tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}

			filename = (getHtmlPath(npcId, 5));
			html.setFile(filename);

			int count = 0;
			int found = 0;
			// counting buttons and unsetting button if found
			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == val)
				{
					//unsetting button
					player.setLoto(i, 0);
					found = 1;
				}
				else if (player.getLoto(i) > 0)
				{
					count++;
				}
			}

			//if not reached limit 5 and not unseted value
			if (count < 5 && found == 0 && val <= 20)
				for (int i = 0; i < 5; i++)
					if (player.getLoto(i) == 0)
					{
						player.setLoto(i, val);
						break;
					}

			//setting pushed buttons
			count = 0;
			for (int i = 0; i < 5; i++)
				if (player.getLoto(i) > 0)
				{
					count++;
					String button = String.valueOf(player.getLoto(i));
					if (player.getLoto(i) < 10)
						button = "0" + button;
					String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
					String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
					html.replace(search, replace);
				}

			if (count == 5)
			{
				String search = "0\">Return";
				String replace = "22\">The winner selected the numbers above.";
				html.replace(search, replace);
			}
		}
		else if (val == 22) //22 - selected ticket with 5 numbers
		{
			if (!Lottery.getInstance().isStarted())
			{
				//tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			if (!Lottery.getInstance().isSellableTickets())
			{
				//tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}

			int price = Config.ALT_LOTTERY_TICKET_PRICE;
			int lotonumber = Lottery.getInstance().getId();
			int enchant = 0;
			int type2 = 0;

			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == 0)
					return;

				if (player.getLoto(i) < 17)
					enchant += Math.pow(2, player.getLoto(i) - 1);
				else
					type2 += Math.pow(2, player.getLoto(i) - 17);
			}
			if (player.getAdena() < price)
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
				player.sendPacket(sm);
				return;
			}
			if (!player.reduceAdena("Loto", price, this, true))
				return;
			Lottery.getInstance().increasePrize(price);

			sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_S2);
			sm.addNumber(lotonumber);
			sm.addItemName(4442);
			player.sendPacket(sm);

			L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), 4442);
			item.setCount(1);
			item.setCustomType1(lotonumber);
			item.setEnchantLevel(enchant);
			item.setCustomType2(type2);
			player.getInventory().addItem("Loto", item, player, this);

			InventoryUpdate iu = new InventoryUpdate();
			iu.addItem(item);
			L2ItemInstance adenaupdate = player.getInventory().getItemByItemId(57);
			iu.addModifiedItem(adenaupdate);
			player.sendPacket(iu);

			filename = (getHtmlPath(npcId, 3));
			html.setFile(filename);
		}
		else if (val == 23) //23 - current lottery jackpot
		{
			filename = (getHtmlPath(npcId, 3));
			html.setFile(filename);
		}
		else if (val == 24) // 24 - Previous winning numbers/Prize claim
		{
			filename = (getHtmlPath(npcId, 4));
			html.setFile(filename);

			int lotonumber = Lottery.getInstance().getId();
			String message = "";
			for (L2ItemInstance item : player.getInventory().getItems())
			{
				if (item == null)
					continue;
				if (item.getItemId() == 4442 && item.getCustomType1() < lotonumber)
				{
					message = message + "<a action=\"bypass -h npc_%objectId%_Loto " + item.getObjectId() + "\">" + item.getCustomType1() + " Event Number ";
					int[] numbers = Lottery.getInstance().decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
					for (int i = 0; i < 5; i++)
					{
						message += numbers[i] + " ";
					}
					int[] check = Lottery.getInstance().checkTicket(item);
					if (check[0] > 0)
					{
						switch (check[0])
						{
							case 1:
								message += "- 1st Prize";
							break;
							case 2:
								message += "- 2nd Prize";
							break;
							case 3:
								message += "- 3th Prize";
							break;
							case 4:
								message += "- 4th Prize";
							break;
						}
						message += " " + check[1] + "a.";
					}
					message += "</a><br>";
				}
			}
			if (message == "")
			{
				message += "There is no winning lottery ticket...<br>";
			}
			html.replace("%result%", message);
		}
		else if (val > 24) // >24 - check lottery ticket by item object id
		{
			int lotonumber = Lottery.getInstance().getId();
			L2ItemInstance item = player.getInventory().getItemByObjectId(val);
			if (item == null || item.getItemId() != 4442 || item.getCustomType1() >= lotonumber)
				return;
			int[] check = Lottery.getInstance().checkTicket(item);

			sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED);
			sm.addItemName(4442);
			player.sendPacket(sm);

			int adena = check[1];
			if (adena > 0)
				player.addAdena("Loto", adena, this, true);
			player.destroyItem("Loto", item, this, false);
			return;
		}
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%race%", "" + Lottery.getInstance().getId());
		html.replace("%adena%", "" + Lottery.getInstance().getPrize());
		html.replace("%ticket_price%", "" + Config.ALT_LOTTERY_TICKET_PRICE);
		html.replace("%prize5%", "" + (Config.ALT_LOTTERY_5_NUMBER_RATE * 100));
		html.replace("%prize4%", "" + (Config.ALT_LOTTERY_4_NUMBER_RATE * 100));
		html.replace("%prize3%", "" + (Config.ALT_LOTTERY_3_NUMBER_RATE * 100));
		html.replace("%prize2%", "" + Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE);
		html.replace("%enddate%", "" + DateFormat.getDateInstance().format(Lottery.getInstance().getEndDate()));
		player.sendPacket(html);

		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	public void makeCPRecovery(L2PcInstance player)
	{
		if (getNpcId() != 31225 && getNpcId() != 31226)
			return;
		if (player.isCursedWeaponEquiped())
		{
			player.sendMessage("Go away, you're not welcome here.");
			return;
		}

		int neededmoney = 100;
		SystemMessage sm;
		if (!player.reduceAdena("RestoreCP", neededmoney, player.getLastFolkNPC(), true))
			return;
		player.setCurrentCp(player.getMaxCp());
		//cp restored
		sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED);
		sm.addString(player.getName());
		player.sendPacket(sm);
	}

	/**
	 * Add Newbie helper buffs to L2Player according to its level.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the range level in which player must be to obtain buff</li> <li>If player level is out of range, display a message and return</li> <li>According to player level cast
	 * buff</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> Newbie Helper Buff list is define in sql table helper_buff_list</B></FONT><BR>
	 * <BR>
	 * 
	 * @param player
	 *        The L2PcInstance that talk with the L2NpcInstance
	 */
	public void makeSupportMagic(L2PcInstance player)
	{
		if (player == null)
			return;

		// Prevent a cursed weapon wielder of being buffed
		if (player.isCursedWeaponEquiped())
			return;

		int player_level = player.getLevel();
		int lowestLevel = 0;
		int highestLevel = 0;

		// Select the player
		setTarget(player);

		// Calculate the min and max level between witch the player must be to obtain buff
		if (player.isMageClass())
		{
			lowestLevel = HelperBuffData.getInstance().getMagicClassLowestLevel();
			highestLevel = HelperBuffData.getInstance().getMagicClassHighestLevel();
		}
		else
		{
			lowestLevel = HelperBuffData.getInstance().getPhysicClassLowestLevel();
			highestLevel = HelperBuffData.getInstance().getPhysicClassHighestLevel();
		}

		// If the player is too high level, display a message and return
		if (player_level > highestLevel)
		{
			String content = "<html><body>Newbie Guide:<br>Only a <font color=\"LEVEL\">novice character of level " + highestLevel + " or less</font> can receive my support magic.<br>Your novice character is the first one that you created and raised in this world.</body></html>";
			insertObjectIdAndShowChatWindow(player, content);
			return;
		}

		// If the player is too low level, display a message and return
		if (player_level < lowestLevel)
		{
			String content = "<html><body>Come back here when you have reached level " + lowestLevel + ". I will give you support magic then.</body></html>";
			insertObjectIdAndShowChatWindow(player, content);
			return;
		}

		L2Skill skill = null;
		// Go through the Helper Buff list define in sql table helper_buff_list and cast skill

		for (L2HelperBuff helperBuffItem : HelperBuffData.getInstance().getHelperBuffTable())
		{
			if (helperBuffItem.isMagicClassBuff() == player.isMageClass())
			{
				if (player_level >= helperBuffItem.getLowerLevel() && player_level <= helperBuffItem.getUpperLevel())
				{
					skill = SkillTable.getInstance().getInfo(helperBuffItem.getSkillID(), helperBuffItem.getSkillLevel());
					if (skill.getSkillType() == L2SkillType.SUMMON)
						player.doCast(skill);
					else
						skill.getEffects(this, player);
				}
			}
		}
	}

	public void showChatWindow(L2PcInstance player)
	{
		showChatWindow(player, 0);
	}

	/**
	 * Returns true if html exists
	 * 
	 * @param player
	 * @param type
	 * @return boolean
	 */
	private boolean showPkDenyChatWindow(L2PcInstance player, String type)
	{
		String html = HtmCache.getInstance().getHtm("data/html/" + type + "/" + getNpcId() + "-pk.htm");

		if (html != null)
		{
			NpcHtmlMessage pkDenyMsg = new NpcHtmlMessage(getObjectId());
			pkDenyMsg.setHtml(html);
			player.sendPacket(pkDenyMsg);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}

		return false;
	}

	/**
	 * Open a chat window on client with the text of the L2NpcInstance.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the text of the selected HTML file in function of the npcId and of the page number</li> <li>Send a Server->Client NpcHtmlMessage containing the text of the
	 * L2NpcInstance to the L2PcInstance</li> <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet</li><BR>
	 * 
	 * @param player
	 *        The L2PcInstance that talk with the L2NpcInstance
	 * @param val
	 *        The number of the page of the L2NpcInstance to display
	 */
	public void showChatWindow(L2PcInstance player, int val)
	{
		if (player.getKarma() > 0)
		{
			if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && this instanceof L2MerchantInstance)
			{
				if (showPkDenyChatWindow(player, "merchant"))
					return;
			}
			else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && this instanceof L2TeleporterInstance)
			{
				if (showPkDenyChatWindow(player, "teleporter"))
					return;
			}
			else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && this instanceof L2WarehouseInstance)
			{
				if (showPkDenyChatWindow(player, "warehouse"))
					return;
			}
			else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && this instanceof L2FishermanInstance)
			{
				if (showPkDenyChatWindow(player, "fisherman"))
					return;
			}
		}

		if (getTemplate().type == "L2Auctioneer" && val == 0)
			return;

		int npcId = getTemplate().npcId;

		/* For use with Seven Signs implementation */
		String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
		int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
		int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS);
		int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
		boolean isSealValidationPeriod = SevenSigns.getInstance().isSealValidationPeriod();
		int compWinner = SevenSigns.getInstance().getCabalHighestScore();

		switch (npcId)
		{
			case 31078:
			case 31079:
			case 31080:
			case 31081:
			case 31082: // Dawn Priests
			case 31083:
			case 31084:
			case 31168:
			case 31692:
			case 31694:
			case 31997:
				switch (playerCabal)
				{
					case SevenSigns.CABAL_DAWN:
						if (isSealValidationPeriod)
							if (compWinner == SevenSigns.CABAL_DAWN)
								if (compWinner != sealGnosisOwner)
									filename += "dawn_priest_2c.htm";
								else
									filename += "dawn_priest_2a.htm";
							else
								filename += "dawn_priest_2b.htm";
						else
							filename += "dawn_priest_1b.htm";
					break;
					case SevenSigns.CABAL_DUSK:
						if (isSealValidationPeriod)
							filename += "dawn_priest_3b.htm";
						else
							filename += "dawn_priest_3a.htm";
					break;
					default:
						if (isSealValidationPeriod)
							if (compWinner == SevenSigns.CABAL_DAWN)
								filename += "dawn_priest_4.htm";
							else
								filename += "dawn_priest_2b.htm";
						else
							filename += "dawn_priest_1a.htm";
					break;
				}
			break;
			case 31085:
			case 31086:
			case 31087:
			case 31088: // Dusk Priest
			case 31089:
			case 31090:
			case 31091:
			case 31169:
			case 31693:
			case 31695:
			case 31998:
				switch (playerCabal)
				{
					case SevenSigns.CABAL_DUSK:
						if (isSealValidationPeriod)
							if (compWinner == SevenSigns.CABAL_DUSK)
								if (compWinner != sealGnosisOwner)
									filename += "dusk_priest_2c.htm";
								else
									filename += "dusk_priest_2a.htm";
							else
								filename += "dusk_priest_2b.htm";
						else
							filename += "dusk_priest_1b.htm";
					break;
					case SevenSigns.CABAL_DAWN:
						if (isSealValidationPeriod)
							filename += "dusk_priest_3b.htm";
						else
							filename += "dusk_priest_3a.htm";
					break;
					default:
						if (isSealValidationPeriod)
							if (compWinner == SevenSigns.CABAL_DUSK)
								filename += "dusk_priest_4.htm";
							else
								filename += "dusk_priest_2b.htm";
						else
							filename += "dusk_priest_1a.htm";
					break;
				}
			break;
			case 31095: //
			case 31096: //
			case 31097: //
			case 31098: // Enter Necropolises
			case 31099: //
			case 31100: //
			case 31101: //
			case 31102: //
				if (isSealValidationPeriod)
				{
					if (playerCabal != compWinner || sealAvariceOwner != compWinner)
					{
						switch (compWinner)
						{
							case SevenSigns.CABAL_DAWN:
								player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
								filename += "necro_no.htm";
							break;
							case SevenSigns.CABAL_DUSK:
								player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
								filename += "necro_no.htm";
							break;
							case SevenSigns.CABAL_NULL:
								filename = (getHtmlPath(npcId, val)); // do the default!
							break;
						}
					}
					else
						filename = (getHtmlPath(npcId, val)); // do the default!
				}
				else
				{
					if (playerCabal == SevenSigns.CABAL_NULL)
						filename += "necro_no.htm";
					else
						filename = (getHtmlPath(npcId, val)); // do the default!
				}
			break;
			case 31114: //
			case 31115: //
			case 31116: // Enter Catacombs
			case 31117: //
			case 31118: //
			case 31119: //
				if (isSealValidationPeriod)
				{
					if (playerCabal != compWinner || sealGnosisOwner != compWinner)
					{
						switch (compWinner)
						{
							case SevenSigns.CABAL_DAWN:
								player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
								filename += "cata_no.htm";
							break;
							case SevenSigns.CABAL_DUSK:
								player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
								filename += "cata_no.htm";
							break;
							case SevenSigns.CABAL_NULL:
								filename = (getHtmlPath(npcId, val)); // do the default!
							break;
						}
					}
					else
						filename = (getHtmlPath(npcId, val)); // do the default!
				}
				else
				{
					if (playerCabal == SevenSigns.CABAL_NULL)
						filename += "cata_no.htm";
					else
						filename = (getHtmlPath(npcId, val)); // do the default!
				}
			break;
			case 31111: // Gatekeeper Spirit (Disciples)
				if (playerCabal == sealAvariceOwner && playerCabal == compWinner)
				{
					switch (sealAvariceOwner)
					{
						case SevenSigns.CABAL_DAWN:
							filename += "spirit_dawn.htm";
						break;
						case SevenSigns.CABAL_DUSK:
							filename += "spirit_dusk.htm";
						break;
						case SevenSigns.CABAL_NULL:
							filename += "spirit_null.htm";
						break;
					}
				}
				else
				{
					filename += "spirit_null.htm";
				}
			break;
			case 31112: // Gatekeeper Spirit (Disciples)
				filename += "spirit_exit.htm";
			break;
			case 31127: //
			case 31128: //
			case 31129: // Dawn Festival Guides
			case 31130: //
			case 31131: //
				filename += "festival/dawn_guide.htm";
			break;
			case 31137: //
			case 31138: //
			case 31139: // Dusk Festival Guides
			case 31140: //
			case 31141: //
				filename += "festival/dusk_guide.htm";
			break;
			case 31092: // Black Marketeer of Mammon
				filename += "blkmrkt_1.htm";
			break;
			case 31113: // Merchant of Mammon
				switch (compWinner)
				{
					case SevenSigns.CABAL_DAWN:
						if (playerCabal != compWinner || playerCabal != sealAvariceOwner)
						{
							player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
							player.sendPacket(ActionFailed.STATIC_PACKET);
							return;
						}
					break;
					case SevenSigns.CABAL_DUSK:
						if (playerCabal != compWinner || playerCabal != sealAvariceOwner)
						{
							player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
							player.sendPacket(ActionFailed.STATIC_PACKET);
							return;
						}
					break;
				}
				filename += "mammmerch_1.htm";
			break;
			case 31126: // Blacksmith of Mammon
				switch (compWinner)
				{
					case SevenSigns.CABAL_DAWN:
						if (playerCabal != compWinner || playerCabal != sealGnosisOwner)
						{
							player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
							player.sendPacket(ActionFailed.STATIC_PACKET);
							return;
						}
					break;
					case SevenSigns.CABAL_DUSK:
						if (playerCabal != compWinner || playerCabal != sealGnosisOwner)
						{
							player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
							player.sendPacket(ActionFailed.STATIC_PACKET);
							return;
						}
					break;
				}
				filename += "mammblack_1.htm";
			break;
			case 31132:
			case 31133:
			case 31134:
			case 31135:
			case 31136:  // Festival Witches
			case 31142:
			case 31143:
			case 31144:
			case 31145:
			case 31146:
				filename += "festival/festival_witch.htm";
			break;
			case 31688:
				if (player.isNoble())
					filename = Olympiad.OLYMPIAD_HTML_PATH + "noble_main.htm";
				else
					filename = (getHtmlPath(npcId, val));
			break;
			case 31690:
			case 31769:
			case 31770:
			case 31771:
			case 31772:
				if (player.isHero())
					filename = Olympiad.OLYMPIAD_HTML_PATH + "hero_main.htm";
				else
					filename = (getHtmlPath(npcId, val));
			break;
			default:
				if (npcId >= 31865 && npcId <= 31918)
				{
					filename += "rift/GuardianOfBorder.htm";
					break;
				}
				if ((npcId >= 31093 && npcId <= 31094) || (npcId >= 31172 && npcId <= 31201) || (npcId >= 31239 && npcId <= 31254))
					return;
				// Get the text of the selected HTML file in function of the npcId and of the page number
				filename = (getHtmlPath(npcId, val));
			break;
		}

		// Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);

		//String word = "npc-"+npcId+(val>0 ? "-"+val : "" )+"-dialog-append";

		if (this instanceof L2MerchantInstance)
			if (Config.LIST_PET_RENT_NPC.contains(npcId))
				html.replace("_Quest", "_RentPet\">Rent Pet</a><br><a action=\"bypass -h npc_%objectId%_Quest");

		html.replace("%name%", getName());
		html.replace("%player_name%", player.getName());
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%festivalMins%", SevenSignsFestival.getInstance().getTimeToNextFestivalStr());
		player.sendPacket(html);

		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	/**
	 * Open a chat window on client with the text specified by the given file name and path,<BR>
	 * relative to the datapack root. <BR>
	 * <BR>
	 * Added by Tempy
	 * 
	 * @param player
	 *        The L2PcInstance that talk with the L2NpcInstance
	 * @param filename
	 *        The filename that contains the text to send
	 */
	public void showChatWindow(L2PcInstance player, String filename)
	{
		// Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);

		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	private int Savvato()
	{
	   return Calendar.SATURDAY;
	}
	private int dayofweek()
	{
	   return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * Return the Exp Reward of this L2Npc contained in the L2NpcTemplate (modified by RATE_XP).<BR><BR>
	 */
	public int getExpReward()
	{
		final double rateXp = getStat().calcStat(Stats.MAX_HP, 1, this, null);
		int exp=0;
		
		 if(Config.ALLOW_SATURDAY_RATE_XP_SP && dayofweek() == Savvato())		 
		  exp = (int) (getTemplate().rewardExp * rateXp * Config.SATURDAY_RATE_XP);
		 else
		  exp = (int) (getTemplate().rewardExp * rateXp * Config.RATE_XP);
		 
		 return exp;
	}
	
	/**
	 * Return the SP Reward of this L2Npc contained in the L2NpcTemplate (modified by RATE_SP).<BR><BR>
	 */
	public int getSpReward()
	{
		double rateSp = getStat().calcStat(Stats.MAX_HP, 1, this, null);
		int sp=0;
		
		 if(Config.ALLOW_SATURDAY_RATE_XP_SP && dayofweek() == Savvato())		 
		     sp = (int) (getTemplate().rewardSp * rateSp * Config.SATURDAY_RATE_SP);
		 else
			 sp = (int) (getTemplate().rewardSp * rateSp * Config.RATE_SP);
		 
		 return sp;
	}
	
	/**
	 * Return the Exp Reward of this L2NpcInstance contained in the L2NpcTemplate (modified by RATE_XP).<BR>
	 * <BR>
	 */
	public int getExpReward(int isPremium)
	{
		double rateXp = getStat().calcStat(Stats.MAX_HP, 1, this, null);
		if (isPremium == 1)
			return (int) (getTemplate().rewardExp * rateXp * Config.PREMIUM_RATE_XP);
		else
			return (int) (getTemplate().rewardExp * rateXp * Config.RATE_XP);
	}

	/**
	 * Return the SP Reward of this L2NpcInstance contained in the L2NpcTemplate (modified by RATE_SP).<BR>
	 * <BR>
	 */
	public int getSpReward(int isPremium)
	{
		double rateSp = getStat().calcStat(Stats.MAX_HP, 1, this, null);
		if (isPremium == 1)
			return (int) (getTemplate().rewardSp * rateSp * Config.PREMIUM_RATE_SP);
		else
			return (int) (getTemplate().rewardSp * rateSp * Config.RATE_SP);
	}

	/**
	 * Kill the L2NpcInstance (the corpse disappeared after 7 seconds).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Create a DecayTask to remove the corpse of the L2NpcInstance after 7 seconds</li> <li>Set target to null and cancel Attack or Cast</li> <li>Stop movement</li> <li>Stop
	 * HP/MP/CP Regeneration task</li> <li>Stop all active skills effects in progress on the L2Character</li> <li>Send the Server->Client packet StatusUpdate with current HP and MP
	 * to all other L2PcInstance to inform</li> <li>Notify L2Character AI</li><BR>
	 * <BR>
	 * <B><U> Overridden in </U> :</B><BR>
	 * <BR>
	 * <li>L2Attackable</li><BR>
	 * <BR>
	 * 
	 * @param killer
	 *        The L2Character who killed it
	 */
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		ZodiacMain.OnKillNpc(this,killer);
		// normally this wouldn't really be needed, but for those few exceptions,
		// we do need to reset the weapons back to the initial templated weapon.
		_currentLHandId = getTemplate().lhand;
		_currentRHandId = getTemplate().rhand;
		_currentCollisionHeight = getTemplate().collisionHeight;
		_currentCollisionRadius = getTemplate().collisionRadius;
		DecayTaskManager.getInstance().addDecayTask(this);
		return true;
	}

	/**
	 * Set the spawn of the L2NpcInstance.<BR>
	 * <BR>
	 * 
	 * @param spawn
	 *        The L2Spawn that manage the L2NpcInstance
	 */
	public void setSpawn(L2Spawn spawn)
	{
		_spawn = spawn;
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

	}

	/**
	 * Remove the L2NpcInstance from the world and update its spawn object (for a complete removal use the deleteMe method).
	 * Actions:
	 * Remove the L2NpcInstance from the world when the decay task is launched
	 * Decrease its spawn counter
	 * Manage Siege task (killFlag, killCT)
	 * Caution: This method DOESN'T REMOVE the object from _allObjects of L2World
	 * Caution: This method DOESN'T SEND Server->Client packets to players
	 */
	@Override
	public void onDecay()
	{
		if (isDecayed())
			return;
		setDecayed(true);

		// reset champion status if the thing is a mob
		setChampion(false);

		// Manage Life Control Tower
		if (this instanceof L2ControlTowerInstance)
			((L2ControlTowerInstance) this).onDeath();

		// Remove the L2NpcInstance from the world when the decay task is launched
		super.onDecay();

		// Decrease its spawn counter
		if (_spawn != null)
			_spawn.decreaseCount(this);
	}

	/**
	 * Remove PROPERLY the L2NpcInstance from the world.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Remove the L2NpcInstance from the world and update its spawn object</li> <li>Remove all L2Object from _knownObjects and _knownPlayer of the L2NpcInstance then cancel
	 * Attak or Cast and notify AI</li> <li>Remove L2Object object from _allObjects of L2World</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR>
	 * <BR>
	 */
	public void deleteMe()
	{
		final L2WorldRegion region = getWorldRegion();
		
		if (region != null)
		{
			region.removeFromZones(this);	
		}
		
		decayMe();
		// Remove all L2Object from _knownObjects and _knownPlayer of the L2Character then cancel Attak or Cast and notify AI
		getKnownList().removeAllKnownObjects();
		// Remove L2Object object from _allObjects of L2World
		L2World.getInstance().removeObject(this);
		
		
		
		super.deleteMe();				
	}

	/**
	 * Return the L2Spawn object that manage this L2NpcInstance.<BR>
	 * <BR>
	 */
	public L2Spawn getSpawn()
	{
		return _spawn;
	}

	@Override
	public String toString()
	{
		return getTemplate().name;
	}

	public boolean isDecayed()
	{
		return _isDecayed;
	}

	public void setDecayed(boolean decayed)
	{
		_isDecayed = decayed;
	}

	public void endDecayTask()
	{
		if (!isDecayed())
		{
			DecayTaskManager.getInstance().cancelDecayTask(this);
			onDecay();
		}
	}

	public boolean isMob() // rather delete this check
	{
		return false; // This means we use MAX_NPC_ANIMATION instead of MAX_MONSTER_ANIMATION
	}

	// Two functions to change the appearance of the equipped weapons on the NPC
	// This is only useful for a few NPCs and is most likely going to be called from AI
	public void setLHandId(int newWeaponId)
	{
		_currentLHandId = newWeaponId;
	}

	public void setRHandId(int newWeaponId)
	{
		_currentRHandId = newWeaponId;
	}

	public void setCollisionHeight(int height)
	{
		_currentCollisionHeight = height;
	}

	public void setCollisionRadius(int radius)
	{
		_currentCollisionRadius = radius;
	}

	public int getCollisionHeight()
	{
		return _currentCollisionHeight;
	}

	public int getCollisionRadius()
	{
		return _currentCollisionRadius;
	}

	public L2MaxPolyModel getMxcPoly()
	{
		return _mxcModel;
	}

	public L2Npc scheduleDespawn(long delay)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(this.new DespawnTask(this), delay);
		return this;
	}
	
	public String getClan()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getClan();
	}
	public int getEnemyRange()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getEnemyRange();
	}
	
	public String getEnemyClan()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getEnemyClan();
	}
	
	public int getClanRange()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getClanRange();
	}
	
	// GET THE PRIMARY ATTACK
	public int getPrimaryAttack()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getPrimaryAttack();
		
	}
	
	public int getSkillChance()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getSkillChance();
		
	}
	
	public int getCanMove()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getCanMove();
	}
	
	public int getIsChaos()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getIsChaos();
		
	}
	
	public int getCanDodge()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getDodge();
		
	}
	
	public int getSSkillChance()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getShortRangeChance();
		
	}
	
	public int getLSkillChance()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getLongRangeChance();
		
	}
	
	public int getSwitchRangeChance()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		return AI.getSwitchRangeChance();
		
	}
	
	public boolean hasLSkill()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		
		if (AI.getLongRangeSkill() == 0)
			return false;
		else
			return true;
		
	}
	
	public boolean hasSSkill()
	{
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		
		if (AI.getShortRangeSkill() == 0)
			return false;
		else
			return true;
		
	}
	
	public ArrayList<L2Skill> getLrangeSkill()
	{
		ArrayList<L2Skill> skilldata = new ArrayList<L2Skill>();
		boolean hasLrange = false;
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		
		if (AI == null || AI.getLongRangeSkill() == 0)
			return null;
		
		switch (AI.getLongRangeSkill())
		{
			case -1:
			{
				L2Skill[] skills = null;
				skills = getAllSkills();
				if (skills != null)
				{
					for (L2Skill sk : skills)
					{
						if (sk == null || sk.isPassive() || sk.getTargetType() == L2SkillTargetType.TARGET_SELF)
							continue;
						
						if (sk.getCastRange() >= 200)
						{
							skilldata.add(sk);
							hasLrange = true;
						}
					}
				}
				break;
			}
			case 1:
			{
				if (getTemplate()._universalskills != null)
				{
					for (L2Skill sk : getTemplate()._universalskills)
					{
						if (sk.getCastRange() >= 200)
						{
							skilldata.add(sk);
							hasLrange = true;
						}
					}
				}
				break;
			}
			default:
			{
				for (L2Skill sk : getAllSkills())
				{
					if (sk.getId() == AI.getLongRangeSkill())
					{
						skilldata.add(sk);
						hasLrange = true;
					}
				}
			}
		}
		
		return (hasLrange ? skilldata : null);
	}
	
	public ArrayList<L2Skill> getSrangeSkill()
	{
		ArrayList<L2Skill> skilldata = new ArrayList<L2Skill>();
		boolean hasSrange = false;
		L2NpcAIData AI = getTemplate().getAIDataStatic();
		
		if (AI == null || AI.getShortRangeSkill() == 0)
			return null;
		
		switch (AI.getShortRangeSkill())
		{
			case -1:
			{
				L2Skill[] skills = null;
				skills = getAllSkills();
				if (skills != null)
				{
					for (L2Skill sk : skills)
					{
						if (sk == null || sk.isPassive() || sk.getTargetType() == L2SkillTargetType.TARGET_SELF)
							continue;
						
						if (sk.getCastRange() <= 200)
						{
							skilldata.add(sk);
							hasSrange = true;
						}
					}
				}
				break;
			}
			case 1:
			{
				if (getTemplate()._universalskills != null)
				{
					for (L2Skill sk : getTemplate()._universalskills)
					{
						if (sk.getCastRange() <= 200)
						{
							skilldata.add(sk);
							hasSrange = true;
						}
					}
				}
				break;
			}
			default:
			{
				for (L2Skill sk : getAllSkills())
				{
					if (sk.getId() == AI.getShortRangeSkill())
					{
						skilldata.add(sk);
						hasSrange = true;
					}
				}
			}
		}
		
		return (hasSrange ? skilldata : null);
	}
	public class DespawnTask implements Runnable
	{
		L2Npc _npc;

		public DespawnTask(L2Npc npc)
		{
			_npc = npc;
		}

		@Override
		public void run()
		{
			if (_npc != null)
				_npc.deleteMe();
		}
	}
	
	public int getScriptValue()
	{
		return _scriptValue;
	}
	
	public void setScriptValue(int val)
	{
		_scriptValue = val;
	}
	
	public boolean isScriptValue(int val)
	{
		return _scriptValue == val;
	}
	
	/**
	 * Make the NPC speaks to his current knownlist.
	 * @param message The String message to send.
	 */
	public void broadcastNpcSay(String message)
	{
		broadcastPacket(new NpcSay(getObjectId(), Say2.ALL, getNpcId(), message));
	}
}