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
package com.l2jhellas.gameserver.instancemanager;

import java.util.ArrayList;

import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.zone.type.L2OlympiadStadiumZone;

public class OlympiadStadiaManager
{
	private static OlympiadStadiaManager _instance;

	public static final OlympiadStadiaManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new OlympiadStadiaManager();
		}
		return _instance;
	}

	private ArrayList<L2OlympiadStadiumZone> _olympiadStadias;

	public OlympiadStadiaManager()
	{
	}

	public void addStadium(L2OlympiadStadiumZone arena)
	{
		if (_olympiadStadias == null)
			_olympiadStadias = new ArrayList<L2OlympiadStadiumZone>();

		_olympiadStadias.add(arena);
	}

	public final L2OlympiadStadiumZone getStadium(L2Character character)
	{
		for (L2OlympiadStadiumZone temp : _olympiadStadias)
			if (temp.isCharacterInZone(character))
				return temp;

		return null;
	}

	@Deprecated
	public final L2OlympiadStadiumZone getOlympiadStadiumById(int olympiadStadiumId)
	{
		for (L2OlympiadStadiumZone temp : _olympiadStadias)
			if (temp.getStadiumId() == olympiadStadiumId)
				return temp;
		return null;
	}
}