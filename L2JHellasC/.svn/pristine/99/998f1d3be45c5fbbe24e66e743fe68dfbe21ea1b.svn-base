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
package com.l2jhellas.gameserver.network.serverpackets;

import java.util.Map;

/**
 * Format: (c) d[dS]
 * d: list size
 * [
 * d: char ID
 * S: char Name
 * ]
 * 
 * @author -Wooden-
 */
public class PackageToList extends L2GameServerPacket
{
	private static final String _S__C2_PACKAGETOLIST = "[S] C2 PackageToList";
	private final Map<Integer, String> _players;

	// Lecter : i put a char list here, but i'm unsure these really are Pc. I duno how freight work tho...
	public PackageToList(Map<Integer, String> players)
	{
		_players = players;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xC2);
		writeD(_players.size());
		
		for (Map.Entry<Integer, String> player : _players.entrySet())
		{
			writeD(player.getKey());//id
			writeS(player.getValue());
		}
	}

	@Override
	public String getType()
	{
		return _S__C2_PACKAGETOLIST;
	}
}