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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.ai.L2CharacterAI;
import com.l2jhellas.gameserver.controllers.GameTimeController;
import com.l2jhellas.gameserver.datatables.xml.MapRegionTable;
import com.l2jhellas.gameserver.model.L2CharPosition;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.Location;
import com.l2jhellas.gameserver.model.VehiclePathPoint;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.knownlist.BoatKnownList;
import com.l2jhellas.gameserver.model.zone.ZoneId;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jhellas.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jhellas.gameserver.templates.L2CharTemplate;
import com.l2jhellas.gameserver.templates.L2Weapon;
import com.l2jhellas.util.Util;

public abstract class L2Vehicle extends L2Character
{
	protected int _dockId = 0;
	protected final List<L2PcInstance> _passengers = new ArrayList<L2PcInstance>();
	protected Location _oustLoc = null;
	private Runnable _engine = null;
	
	protected VehiclePathPoint[] _currentPath = null;
	protected int _runState = 0;
	
	public L2Vehicle(int objectId, L2CharTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isFlying()
	{
		return true;
	}
	
	public boolean isBoat()
	{
		return false;
	}
	
	public boolean canBeControlled()
	{
		return _engine == null;
	}
	
	public void registerEngine(Runnable r)
	{
		_engine = r;
	}
	
	public void runEngine(int delay)
	{
		if (_engine != null)
			ThreadPoolManager.getInstance().scheduleGeneral(_engine, delay);
	}
	
	private int _moveSpeed = 0;
	private int _rotationSpeed = 0;
	
	@Override
	public int getMoveSpeed()
	{
		return _moveSpeed;
	}
	
	public final void setMoveSpeed(int speed)
	{
		_moveSpeed = speed;
	}
	
	public final int getRotationSpeed()
	{
		return _rotationSpeed;
	}
	
	public final void setRotationSpeed(int speed)
	{
		_rotationSpeed = speed;
	}
	
	public void executePath(VehiclePathPoint[] path)
	{
		_runState = 0;
		_currentPath = path;
		
		if (_currentPath != null && _currentPath.length > 0)
		{
			final VehiclePathPoint point = _currentPath[0];
			if (point.moveSpeed > 0)
				setMoveSpeed(point.moveSpeed);
			if (point.rotationSpeed > 0)
				setRotationSpeed(point.rotationSpeed);
			
			getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(point.x, point.y, point.z, 0));
			return;
		}


		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}
	
	@Override
	public boolean moveToNextRoutePoint()
	{
		_move = null;
		
		if (_currentPath != null)
		{
			_runState++;
			if (_runState < _currentPath.length)
			{
				final VehiclePathPoint point = _currentPath[_runState];
				if (!isMovementDisabled())
				{
					if (point.moveSpeed == 0)
					{
						teleToLocation(point.x, point.y, point.z, false);
						_currentPath = null;
					}
					else
					{
						if (point.moveSpeed > 0)
							setMoveSpeed(point.moveSpeed);
						if (point.rotationSpeed > 0)
							setRotationSpeed(point.rotationSpeed);
						
						MoveData m = new MoveData();
						m.onGeodataPathIndex = -1;
						m._xDestination = point.x;
						m._yDestination = point.y;
						m._zDestination = point.z;
						m._heading = 0;
						
						final double dx = point.x - getX();
						final double dy = point.y - getY();
						final double distance = Math.sqrt(dx * dx + dy * dy);
						if (distance > 1) // vertical movement heading check
							setHeading(Util.calculateHeadingFrom(getX(), getY(), point.x, point.y));
						
						m._moveStartTime = GameTimeController.getInstance().getGameTicks();
						_move = m;
						
						GameTimeController.getInstance().registerMovingObject(this);
						return true;
					}
				}
			}
			else
				_currentPath = null;
		}
		
		runEngine(10);
		return false;
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new BoatKnownList(this));
	}


	public boolean isInDock()
	{
		return _dockId > 0;
	}
	
	public int getDockId()
	{
		return _dockId;
	}
	
	public void setInDock(int d)
	{
		_dockId = d;
	}
	
	public void setOustLoc(Location loc)
	{
		_oustLoc = loc;
	}
	
	public Location getOustLoc()
	{
		return _oustLoc != null ? _oustLoc : MapRegionTable.getInstance().getTeleToLocation(this, MapRegionTable.TeleportWhereType.Town);
	}
	
	public void oustPlayers()
	{
		// Use iterator because oustPlayer will try to remove player from _passengers
		final Iterator<L2PcInstance> iter = _passengers.iterator();
		while (iter.hasNext())
		{
			L2PcInstance player = iter.next();
			if (player != null)
				oustPlayer(player);
			
			iter.remove();
		}
	}
	
	public void oustPlayer(L2PcInstance player)
	{
		player.setVehicle(null);
		player.setInVehiclePosition(null);
		removePassenger(player);
		
		player.setInsideZone(ZoneId.PEACE, false);
		player.sendPacket(SystemMessageId.EXIT_PEACEFUL_ZONE);
	}
	
	public boolean addPassenger(L2PcInstance player)
	{
		if (player == null || _passengers.contains(player))
			return false;
		
		// already in other vehicle
		if (player.getVehicle() != null && player.getVehicle() != this)
			return false;
		
		_passengers.add(player);
		
		player.setInsideZone(ZoneId.PEACE, true);
		player.sendPacket(SystemMessageId.ENTER_PEACEFUL_ZONE);
		
		return true;
	}
	
	public void removePassenger(L2PcInstance player)
	{
		
		try
		{
			_passengers.remove(player);
		}
		catch (Exception e)
		{
		}
	}
	
	public boolean isEmpty()
	{
		return _passengers.isEmpty();
	}
	
	public List<L2PcInstance> getPassengers()
	{
		return _passengers;
	}
	
	public void broadcastToPassengers(L2GameServerPacket sm)
	{
		for (L2PcInstance player : _passengers)
		{
			if (player != null)
				player.sendPacket(sm);
		}
	}
	
	/**
	 * Consume ticket(s) and teleport player from boat if no correct ticket
	 * @param itemId Ticket itemId
	 * @param count Ticket count
	 * @param oustX
	 * @param oustY
	 * @param oustZ
	 */
	public void payForRide(int itemId, int count, int oustX, int oustY, int oustZ)
	{
		for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, 1000))
		{
			if (player.isInBoat() && player.getBoat() == this)
			{
				if (itemId > 0)
				{
					final L2ItemInstance ticket = player.getInventory().getItemByItemId(itemId);
					if (ticket == null || player.getInventory().destroyItem("Boat", ticket.getItemId(), count, player, this) == null)
					{
						player.sendPacket(SystemMessageId.NOT_CORRECT_BOAT_TICKET);
						player.teleToLocation(oustX, oustY, oustZ, true);
						continue;
					}
					
					final InventoryUpdate iu = new InventoryUpdate();
					if (ticket.getCount() == 0)
						iu.addRemovedItem(ticket);
					else
						iu.addModifiedItem(ticket);
					
					player.sendPacket(iu);
				}
				addPassenger(player);
			}
		}
	}
	
	@Override
	public boolean updatePosition()
	{
		final boolean result = super.updatePosition();
		
		for (L2PcInstance player : _passengers)
		{
			if (player != null && player.getVehicle() == this)
			{
				player.getPosition().setXYZ(getX(), getY(), getZ());
				player.revalidateZone(false);
			}
		}
		return result;
	}
	
	@Override
	public void teleToLocation(int x, int y, int z, boolean randomOffset)
	{
		if (isMoving())
			stopMove(null);
		
		setIsTeleporting(true);
		
		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		
		for (L2PcInstance player : _passengers)
		{
			if (player != null)
				player.teleToLocation(x, y, z, randomOffset);
		}
		
		decayMe();
		setXYZ(x, y, z);
		
		onTeleported();
		revalidateZone();
	}
	
	@Override
	public void stopMove(L2CharPosition pos)
	{
		_move = null;
		if (pos != null)
		{
			setXYZ(pos.x, pos.y, pos.z);
			setHeading(pos.heading);
			revalidateZone();
		}
	}
	
	@Override
	public void updateAbnormalEffect()
	{
	}
	
	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public L2Weapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public L2Weapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	@Override
	public int getLevel()
	{
		return 0;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
	
	@Override
	public void setAI(L2CharacterAI newAI)
	{
		if (_ai == null)
			_ai = newAI;
	}
	
	public class AIAccessor extends L2Character.AIAccessor
	{
		@Override
		public void detachAI()
		{
		}
	}
	
}