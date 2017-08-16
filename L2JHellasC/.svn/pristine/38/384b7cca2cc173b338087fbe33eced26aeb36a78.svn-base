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
package com.l2jhellas.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.ai.L2AttackableAI;
import com.l2jhellas.gameserver.datatables.sql.SpawnTable;
import com.l2jhellas.gameserver.model.actor.L2Attackable;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.zone.L2ZoneType;
import com.l2jhellas.gameserver.model.zone.type.L2DerbyTrackZone;
import com.l2jhellas.gameserver.model.zone.type.L2PeaceZone;
import com.l2jhellas.gameserver.model.zone.type.L2TownZone;

public final class L2WorldRegion
{
	private static Logger _log = Logger.getLogger(L2WorldRegion.class.getName());

	private final Map<Integer, L2Object> _visibleObjects = new ConcurrentHashMap<>();
	
	private final List<L2WorldRegion> _surroundingRegions = new ArrayList<>();
	private final List<L2ZoneType> _zones = new ArrayList<>();
	
	private final int _tileX;
	private final int _tileY;
	
	private Boolean _active = Config.GRIDS_ALWAYS_ON;
	
	private ScheduledFuture<?> _neighborsTask = null;

	private AtomicInteger _players = new AtomicInteger();

	public L2WorldRegion(int pTileX, int pTileY)
	{
		_tileX = pTileX;
		_tileY = pTileY;		
	}

	public void addZone(L2ZoneType zone)
	{
		_zones.add(zone);
	}

	public void removeZone(L2ZoneType zone)
	{
		_zones.remove(zone);
	}

	public List<L2ZoneType> getZones()
	{
		return _zones;
	}

	public boolean checkEffectRangeInsidePeaceZone(L2Skill skill, final int x, final int y, final int z)
	{
		final int range = skill.getEffectRange();
		final int up = y + range;
		final int down = y - range;
		final int left = x + range;
		final int right = x - range;

		for (L2ZoneType e : getZones())
		{
			if ((e instanceof L2TownZone && ((L2TownZone) e).isPeaceZone()) || e instanceof L2DerbyTrackZone || e instanceof L2PeaceZone)
			{
				if (e.isInsideZone(x, up, z))
					return false;

				if (e.isInsideZone(x, down, z))
					return false;

				if (e.isInsideZone(left, y, z))
					return false;

				if (e.isInsideZone(right, y, z))
					return false;

				if (e.isInsideZone(x, y, z))
					return false;
			}
		}
		return true;
	}

	public void revalidateZones(L2Character character)
	{
		// do NOT update the world region while the character is still in the process of teleporting
		if (character.isTeleporting())
			return;
		
		_zones.forEach(z -> z.revalidateInZone(character));
	}

	public void removeFromZones(L2Character character)
	{
		_zones.forEach(z -> z.removeCharacter(character));
	}

	public void onDeath(L2Character character)
	{
		_zones.stream().filter(z -> z.isCharacterInZone(character)).forEach(z -> z.onDieInside(character));
	}

	public void onRevive(L2Character character)
	{
		_zones.stream().filter(z -> z.isCharacterInZone(character)).forEach(z -> z.onReviveInside(character));
	}

	/** Task of AI notification */
	public class NeighborsTask implements Runnable
	{
		private final boolean _isActivating;

		public NeighborsTask(boolean isActivating)
		{
			_isActivating = isActivating;
		}

		@Override
		public void run()
		{
			if (_isActivating)
			{
				// for each neighbor, if it's not active, activate.
				for (L2WorldRegion neighbor : getSurroundingRegions())
					neighbor.setActive(true);
			}
			else
			{
				if (areNeighborsEmpty())
					setActive(false);

				// check and deactivate
				for (L2WorldRegion neighbor : getSurroundingRegions())
					if (neighbor.areNeighborsEmpty())
						neighbor.setActive(false);
			}
		}
	}

	private void switchAI(Boolean isOn)
	{
		if (!isOn)
		{
			for (L2Object o : _visibleObjects.values())
			{
				if (o instanceof L2Attackable)
				{
					L2Attackable mob = (L2Attackable) o;

					// Set target to null and cancel Attack or Cast
					mob.setTarget(null);

					// Stop movement
					mob.stopMove(null);

					// Stop all active skills effects in progress on the L2Character
					mob.stopAllEffects();

					mob.clearAggroList();
					mob.getAttackByList().clear();
					mob.getKnownList().removeAllKnownObjects();

					mob.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);

					// stop the ai tasks
					((L2AttackableAI) mob.getAI()).stopAITask();

					// Stop HP/MP/CP Regeneration task
					// try this: allow regen, but only until mob is 100% full...then stop
					// it until the grid is made active.
					//mob.getStatus().stopHpMpRegeneration();
				}
			}
		}
		else
		{
			for (L2Object o : _visibleObjects.values())
			{
				if (o instanceof L2Attackable)
				{
					((L2Attackable) o).getStatus().startHpMpRegeneration();
				}
				else if (o instanceof L2Npc)
				{
					((L2Npc) o).startRandomAnimationTimer();
				}
			}
		}

	}

	public Boolean isActive()
	{
		return _active;
	}

	// check if all 9 neighbors (including self) are inactive or active but with no players.
	// returns true if the above condition is met.
	public Boolean areNeighborsEmpty()
	{
		for (L2WorldRegion neighbor : _surroundingRegions)
		{
			if (neighbor.getPlayersCount() != 0)
				return false;
		}

		// in all other cases, return true.
		return true;
	}

	
	/**
	 * this function turns this region's AI and geodata on or off
	 * 
	 * @param value
	 */
	public void setActive(boolean value)
	{
		if (_active == value)
			return;

		_active = value;

		// turn the AI on or off to match the region's activation.
		switchAI(value);

		// TODO
		// turn the geodata on or off to match the region's activation.
		if (value)
			_log.fine("Starting Grid " + _tileX + "," + _tileY);
		else
			_log.fine("Stoping Grid " + _tileX + "," + _tileY);
	}

	/**
	 * Immediately sets self as active and starts a timer to set neighbors as active
	 * this timer is to avoid turning on neighbors in the case when a person just
	 * teleported into a region and then teleported out immediately...there is no
	 * reason to activate all the neighbors in that case.
	 */
	private void startActivation()
	{
		// first set self to active and do self-tasks...
		setActive(true);

		// if the timer to deactivate neighbors is running, cancel it.
		if (_neighborsTask != null)
		{
			_neighborsTask.cancel(true);
			_neighborsTask = null;
		}

		// then, set a timer to activate the neighbors
		_neighborsTask = ThreadPoolManager.getInstance().scheduleGeneral(new NeighborsTask(true), 1000 * Config.GRID_NEIGHBOR_TURNON_TIME);
	}

	/**
	 * starts a timer to set neighbors (including self) as inactive
	 * this timer is to avoid turning off neighbors in the case when a person just
	 * moved out of a region that he may very soon return to. There is no reason
	 * to turn self & neighbors off in that case.
	 */
	private void startDeactivation()
	{
		// if the timer to activate neighbors is running, cancel it.
		if (_neighborsTask != null)
		{
			_neighborsTask.cancel(true);
			_neighborsTask = null;
		}

		// start a timer to "suggest" a deactivate to self and neighbors.
		// suggest means: first check if a neighbor has L2PcInstances in it.  If not, deactivate.
		_neighborsTask = ThreadPoolManager.getInstance().scheduleGeneral(new NeighborsTask(false), 1000 * Config.GRID_NEIGHBOR_TURNOFF_TIME);
	}

	/**
	 * Add the L2Object in the L2ObjectHashSet(L2Object) _visibleObjects containing L2Object visible in this L2WorldRegion <BR>
	 * If L2Object is a L2PcInstance, Add the L2PcInstance in the L2ObjectHashSet(L2PcInstance) _allPlayable
	 * containing L2PcInstance of all player in game in this L2WorldRegion <BR>
	 * Assert : object.getCurrentWorldRegion() == this
	 */
	public void addVisibleObject(L2Object object)
	{
		if (object == null)
			return;
		
		if (Config.ASSERT)
			assert object.getWorldRegion() == this;

		_visibleObjects.put(object.getObjectId(),object);

		if (object instanceof L2PcInstance)
				_players.incrementAndGet();
		
		if (object instanceof L2Playable)
		{
			// if this is the first player to enter the region, activate self & neighbors
			if (!isActive())
				startActivation();
		}
	}

	/**
	 * Remove the L2Object from the L2ObjectHashSet(L2Object) _visibleObjects in this L2WorldRegion <BR>
	 * <BR>
	 * If L2Object is a L2PcInstance, remove it from the L2ObjectHashSet(L2PcInstance) _allPlayable of this L2WorldRegion <BR>
	 * Assert : object.getCurrentWorldRegion() == this || object.getCurrentWorldRegion() == null
	 */
	public void removeVisibleObject(L2Object object)
	{
		if (object == null)
			return;
		
		if (Config.ASSERT)
			assert object.getWorldRegion() == this || object.getWorldRegion() == null;

		_visibleObjects.remove(object);

		if (object instanceof L2PcInstance)
			_players.decrementAndGet();
		
		if (object instanceof L2Playable)
		{
			if (areNeighborsEmpty())
				startDeactivation();
		}
	}

	public void addSurroundingRegion(L2WorldRegion region)
	{
		_surroundingRegions.add(region);
	}

	/**
	 * Return the FastList _surroundingRegions containing all L2WorldRegion around the current L2WorldRegion
	 */
	public List<L2WorldRegion> getSurroundingRegions()
	{
		//change to return L2WorldRegion[] ?
		//this should not change after initialization, so maybe changes are not necessary

		return _surroundingRegions;
	}

	public Map<Integer, L2Object> getVisibleObjects()
	{
		return _visibleObjects;
	}

	public String getName()
	{
		return "(" + _tileX + ", " + _tileY + ")";
	}

	/**
	 * Deleted all spawns in the world.
	 */
	public void deleteVisibleNpcSpawns()
	{
		for (L2Object obj : _visibleObjects.values())
		{
			if (obj instanceof L2Npc)
			{
				((L2Npc) obj).deleteMe();
				
				final L2Spawn spawn = ((L2Npc) obj).getSpawn();
				if (spawn != null)
				{
					spawn.stopRespawn();
					SpawnTable.getInstance().deleteSpawn(spawn, false);
				}
			}
		}
	}
	
 	public int getPlayersCount()
 	{
		return _players.get();
 	}
}