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

import java.util.concurrent.ScheduledFuture;

import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.model.actor.L2Attackable;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.knownlist.MonsterKnownList;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;
import com.l2jhellas.util.MinionList;

/**
 * This class manages all Monsters.
 * L2MonsterInstance :<BR>
 * <BR>
 * <li>L2MinionInstance</li> <li>L2RaidBossInstance</li>
 */
public class L2MonsterInstance extends L2Attackable
{
	private L2MonsterInstance _master;
	private MinionList _minionList;
	
	protected ScheduledFuture<?> _minionMaintainTask = null;

	private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;
	
	/**
	 * Constructor of L2MonsterInstance (use L2Character and L2NpcInstance constructor).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Call the L2Character constructor to set the _template of the L2MonsterInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li> <li>
	 * Set the name of the L2MonsterInstance</li> <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li><BR>
	 * <BR>
	 * 
	 * @param objectId
	 *        Identifier of the object to initialized
	 * @param L2NpcTemplate
	 *        Template to apply to the NPC
	 */
	public L2MonsterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		getKnownList();	// init knownlist
		_minionList = new MinionList(this);
	}

	@Override
	public final MonsterKnownList getKnownList()
	{
		if ((super.getKnownList() == null) || !(super.getKnownList() instanceof MonsterKnownList))
			setKnownList(new MonsterKnownList(this));
		return (MonsterKnownList) super.getKnownList();
	}

	/**
	 * Return True if the attacker is not another L2MonsterInstance.<BR>
	 * <BR>
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		if (attacker instanceof L2MonsterInstance)
			return false;

		return !isEventMob;
	}

	/**
	 * Return True if the L2MonsterInstance is Aggressive (aggroRange > 0).<BR>
	 * <BR>
	 */
	@Override
	public boolean isAggressive()
	{
		return (getTemplate().aggroRange > 0) && !isEventMob;
	}

	@Override
	public void onSpawn()
	{
		
		if (!isTeleporting())
		{
			if (_master != null)
			{
				setIsRaidMinion(_master.isRaid());
				_master.getMinionList().onMinionSpawn(this);
			}
			// delete spawned minions before dynamic minions spawned by script
			else if (_minionList != null)
				getMinionList().deleteSpawnedMinions();
			
			manageMinions();
		}
		
		// dynamic script-based minions spawned here, after all preparations.
		super.onSpawn();
	}	

	/**
	 * Spawn all minions at a regular interval
	 */
	protected void manageMinions()
	{
		_minionMaintainTask = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
		{
			@Override
			public void run()
			{
				_minionList.spawnMinions();
			}
		}, getMaintenanceInterval());
	}
	
	protected int getMaintenanceInterval()
	{
		return MONSTER_MAINTENANCE_INTERVAL;
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
			
		if (_master != null)
			_master.getMinionList().onMinionDie(this, _master.getSpawn().getRespawnDelay() / 2);
				
		return true;
	}

	@Override
	public void deleteMe()
	{
		if (_minionList != null)
			getMinionList().onMasterDie(true);
		else if (_master != null)
			_master.getMinionList().onMinionDie(this, 0);
		
		super.deleteMe();
	}
	
	@Override
	public L2MonsterInstance getLeader()
	{
		return _master;
	}
	
	public void setLeader(L2MonsterInstance leader)
	{
		_master = leader;
	}
	
	public boolean hasMinions()
	{
		return _minionList != null;
	}
	
	public MinionList getMinionList()
	{
		if (_minionList == null)
			_minionList = new MinionList(this);
		
		return _minionList;
	}
}