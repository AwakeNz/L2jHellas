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

import java.util.HashMap;
import java.util.Map;

public class ItemHandler implements IHandler<IItemHandler, Integer>
{
	private final Map<Integer, IItemHandler> _datatable;
	
	protected ItemHandler()
	{
		_datatable = new HashMap<>();
	}
	
	@Override
	public void registerHandler(IItemHandler handler)
	{
		int[] ids = handler.getItemIds();

		for(int id : ids)
		{
			_datatable.put(new Integer(id), handler);
		}
	}
	
	@Override
	public void removeHandler(IItemHandler handler)
	{
		int[] ids = handler.getItemIds();

		for(int id : ids)
		{
			_datatable.remove(new Integer(id));
		}
	}
	
	@Override
	public IItemHandler getHandler(Integer val)
	{
		return _datatable.get(new Integer(val));
	}
	
	@Override
	public int size()
	{
		return _datatable.size();
	}
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler _instance = new ItemHandler();
	}
}