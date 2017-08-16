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

import com.l2jhellas.gameserver.model.L2Object;

/**
 * sample
 * 0000: 0c 9b da 12 40 ....@
 * format d
 */
public class Revive extends L2GameServerPacket
{
	private static final String _S__0C_REVIVE = "[S] 07 Revive";
	private final int _objectId;

	public Revive(L2Object obj)
	{
		_objectId = obj.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x07);
		writeD(_objectId);
	}

	@Override
	public String getType()
	{
		return _S__0C_REVIVE;
	}
}