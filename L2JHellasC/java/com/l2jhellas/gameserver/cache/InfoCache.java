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
package com.l2jhellas.gameserver.cache;

import java.util.HashMap;
import java.util.List;

import com.l2jhellas.gameserver.model.L2DropData;

public class InfoCache
{
	private static final HashMap<Integer, List<L2DropData>> _droplistCache = new HashMap<Integer, List<L2DropData>>();

	public static void addToDroplistCache(final int id, final List<L2DropData> list)
	{
		_droplistCache.put(id, list);
	}

	public static List<L2DropData> getFromDroplistCache(final int id)
	{
		return _droplistCache.get(id);
	}

	public static void unload()
	{
		_droplistCache.clear();
	}
}