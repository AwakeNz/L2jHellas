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

import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2CabaleBufferInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2FestivalGuideInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.util.Util;

public class NpcKnownList extends CharKnownList
{
	public NpcKnownList(L2Npc activeChar)
	{
		super(activeChar);
	}

	@Override
	public L2Npc getActiveChar()
	{
		return (L2Npc) super.getActiveChar();
	}

	@Override
	public boolean addKnownObject(L2Object object, L2Character dropper)
	{
		if(object instanceof L2PcInstance)
		{
			
		if (!super.addKnownObject(object, dropper))
			return false;
		
		return true;
		}
		return false;
	}
	
	@Override
	public boolean removeKnownObject(L2Object object)
	{
		if (!super.removeKnownObject(object))
			return false;

		// object is not visible or out of distance to forget, remove from known list
		if (!object.isVisible() || !Util.checkIfInShortRadius(getDistanceToForgetObject(object), _activeObject, object, true))
			removeKnownObject(object);
		
		return true;
	}
	
	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		return 2 * getDistanceToWatchObject(object);
	}

	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		if (object instanceof L2FestivalGuideInstance)
			return 10000;

		if (object instanceof L2NpcInstance || !(object instanceof L2Character))
			return 0;

		if (object instanceof L2CabaleBufferInstance)
			return 900;

		if (object instanceof L2Playable)
			return 1500;

		return 500;
	}
}