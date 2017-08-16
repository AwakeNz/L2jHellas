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
package com.l2jhellas.gameserver.model.actor.knownlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.util.Util;

public class ObjectKnownList
{
	protected final L2Object _activeObject;
	protected Map<Integer, L2Object> _knownObjects;

	public ObjectKnownList(L2Object activeObject)
	{
		_activeObject = activeObject;
		_knownObjects = new ConcurrentHashMap<Integer, L2Object>();
	}

	public boolean addKnownObject(L2Object object)
	{
		return addKnownObject(object, null);
	}

	public boolean addKnownObject(L2Object object, L2Character dropper)
	{
		if ((object == null) || (object == getActiveObject()) || !object.isVisible())
			return false;

		// Check if already know object
		if (knowsObject(object))
		{

			if (!object.isVisible())
				removeKnownObject(object);
			return false;
		}

		// Check if object is not inside distance to watch object
		if (!Util.checkIfInRange(getDistanceToWatchObject(object), getActiveObject(), object, true))
			return false;

		return (getKnownObjects().put(object.getObjectId(), object) == null);
	}

	public final boolean knowsObject(L2Object object)
	{
		return getActiveObject() == object || getKnownObjects().containsKey(object.getObjectId());
	}

	/** Remove all L2Object from _knownObjects */
	public void removeAllKnownObjects()
	{
		getKnownObjects().clear();
	}

	public boolean removeKnownObject(L2Object object)
	{
		if (object == null)
			return false;
		return (getKnownObjects().remove(object.getObjectId()) != null);
	}
	
	/**
	 * Remove object from known list, which are beyond distance to forget.
	 */
	public final void forgetObjects()
	{
		for (L2Object object : getKnownObjects().values())
		{
			// object is not visible or out of distance to forget, remove from known list
			if (!object.isVisible() || !Util.checkIfInShortRadius(getDistanceToForgetObject(object), _activeObject, object, true))
				removeKnownObject(object);
		}
	}
	
	public L2Object getActiveObject()
	{
		return _activeObject;
	}

	public int getDistanceToForgetObject(L2Object object)
	{
		return 0;
	}

	public int getDistanceToWatchObject(L2Object object)
	{
		return 0;
	}

	/** Return the _knownObjects containing all L2Object known by the L2Character. */
	public final Map<Integer, L2Object> getKnownObjects()
	{
		return _knownObjects;
	}
	
	/**
	 * Return the known list of given object type.
	 * @param <A> : Object type must be instance of {@link L2Object}.
	 * @param type : Class specifying object type.
	 * @return List<A> : Known list of given object type.
	 */
	@SuppressWarnings("unchecked")
	public final <A> List<A> getKnownType(Class<A> type)
	{
		// create result list
		List<A> result = new ArrayList<>();
		
		// for all objects in known list
		for (L2Object obj : _knownObjects.values())
		{
			// object type is correct, add to the list
			if (type.isAssignableFrom(obj.getClass()))
				result.add((A) obj);
		}
		
		// return result
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public final <A> Collection<A> getKnownTypeInRadius(Class<A> type, int radius)
	{
		List<A> result = new ArrayList<>();
		
		for (L2Object obj : getKnownObjects().values())
		{
			if (type.isAssignableFrom(obj.getClass()) && Util.checkIfInRange(radius, getActiveObject(), obj, true))
				result.add((A) obj);
		}
		return result;
	}		
}