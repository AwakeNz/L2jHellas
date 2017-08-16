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

import com.l2jhellas.gameserver.model.actor.L2Character;

/**
 * 0000: 01 7a 73 10 4c b2 0b 00 00 a3 fc 00 00 e8 f1 ff .zs.L...........<BR>
 * 0010: ff bd 0b 00 00 b3 fc 00 00 e8 f1 ff ff .............<BR>
 * ddddddd
 */
public class CharMoveToLocation extends L2GameServerPacket
{
	private static final String _S__01_CHARMOVETOLOCATION = "[S] 01 CharMoveToLocation";
	private final int _charObjId, _x, _y, _z, _xDst, _yDst, _zDst;

	public CharMoveToLocation(L2Character cha)
	{
		_charObjId = cha.getObjectId();
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_xDst = cha.getXdestination();
		_yDst = cha.getYdestination();
		_zDst = cha.getZdestination();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x01);

		writeD(_charObjId);

		writeD(_xDst);
		writeD(_yDst);
		writeD(_zDst);

		writeD(_x);
		writeD(_y);
		writeD(_z);
	}

	@Override
	public String getType()
	{
		return _S__01_CHARMOVETOLOCATION;
	}
}