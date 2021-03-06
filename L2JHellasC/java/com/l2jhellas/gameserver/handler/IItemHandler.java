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
package com.l2jhellas.gameserver.handler;

import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.actor.L2Playable;

/**
 * Mother class of all itemHandlers.
 */
public interface IItemHandler
{
	/**
	 * Launch task associated to the item.
	 * 
	 * @param activeChar
	 *        : L2PlayableInstance designating the player
	 * @param item
	 *        : L2ItemInstance designating the item to use
	 */
	public void useItem(L2Playable playable, L2ItemInstance item);

	/**
	 * Returns the list of item IDs corresponding to the type of item.<BR>
	 * <BR>
	 * <B><I>Use :</I></U><BR>
	 * This method is called at initialization to register all the item IDs
	 * automatically
	 * 
	 * @return int[] designating all itemIds for a type of item.
	 */
	public int[] getItemIds();
}