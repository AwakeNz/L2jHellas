/*
 * Copyright (C) 2004-2016 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jhellas.gameserver.taskmanager;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.L2WorldRegion;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2GuardInstance;

public class KnownListUpdateTaskManager
{
	protected static final Logger _log = Logger.getLogger(KnownListUpdateTaskManager.class.getName());
	
	private static final int FULL_UPDATE_TIMER = 100;
	
	public static boolean updatePass = true;
	
	public static int _fullUpdateTimer = FULL_UPDATE_TIMER;
	
	protected static final Set<L2WorldRegion> FAILED_REGIONS = ConcurrentHashMap.newKeySet(1);
	
	protected KnownListUpdateTaskManager()
	{
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new KnownListUpdate(), 1000, 1300);
	}
	
	private class KnownListUpdate implements Runnable
	{
		public KnownListUpdate()
		{
		}
		
		@Override
		public void run()
		{
			try
			{
				boolean failed;
				for (L2WorldRegion regions[] : L2World.getInstance().getAllWorldRegions())
				{
					for (L2WorldRegion r : regions)
					{
						try
						{
							failed = FAILED_REGIONS.contains(r); 
							if (r.isActive())
							{
								updateRegion(r, ((_fullUpdateTimer == FULL_UPDATE_TIMER) || failed), updatePass);
							}
							if (failed)
							{
								FAILED_REGIONS.remove(r); 
							}
						}
						catch (Exception e)
						{
							_log.log(Level.WARNING, "KnownListUpdateTaskManager: updateRegion(" + _fullUpdateTimer + "," + updatePass + ") failed for region " + r.getName() + ". Full update scheduled. " + e.getMessage(), e);
							FAILED_REGIONS.add(r);
						}
					}
				}
				
				updatePass = !updatePass;
				
				if (_fullUpdateTimer > 0)
				{
					_fullUpdateTimer--;
				}
				else
				{
					_fullUpdateTimer = FULL_UPDATE_TIMER;
				}
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "", e);
			}
		}
	}
	
	public void updateRegion(L2WorldRegion region, boolean fullUpdate, boolean forgetObjects)
	{
		Collection<L2Object> vObj = region.getVisibleObjects().values();
		
		for (L2Object object : vObj) 
		{
			if ((object == null) || !object.isVisible())
			{
				continue; 
			}
			
			final boolean aggro = (Config.ALLOW_GUARDS && (object instanceof L2GuardInstance));
			
			if (forgetObjects)
			{
				object.getKnownList().forgetObjects();
				continue;
			}
			
			for (L2WorldRegion regi : region.getSurroundingRegions())
			{
				if ((object instanceof L2Playable) || (aggro && regi.isActive()) || fullUpdate)
				{
					Collection<L2Object> inrObj = regi.getVisibleObjects().values();
					for (L2Object obj : inrObj)
					{
						if (obj != object && obj.isVisible())
						{
							object.getKnownList().addKnownObject(obj);
						}
					}
				}
				else if (object instanceof L2Character)
				{
					if (regi.isActive())
					{
						Collection<L2Object> inrPls = regi.getVisibleObjects().values();
						
						for (L2Object obj : inrPls)
						{
							if (obj != object && obj.isVisible())
							{
								object.getKnownList().addKnownObject(obj);
							}
						}
					}
				}
			}
		}
	}
	
	public static KnownListUpdateTaskManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final KnownListUpdateTaskManager _instance = new KnownListUpdateTaskManager();
	}
}