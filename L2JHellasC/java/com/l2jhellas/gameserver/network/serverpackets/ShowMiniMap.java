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

import com.l2jhellas.gameserver.SevenSigns;

/**
 * sample
 * format
 * d
 */
public class ShowMiniMap extends L2GameServerPacket
{
	private static final String _S__B6_SHOWMINIMAP = "[S] 9d ShowMiniMap";
	private final int _mapId;

	public ShowMiniMap(int mapId)
	{
		_mapId = mapId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9d);
		writeD(_mapId);
		writeD(SevenSigns.getInstance().getCurrentPeriod());
	}

	@Override
	public String getType()
	{
		return _S__B6_SHOWMINIMAP;
	}
}