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

/**
 * format ddddd
 * sample
 * 0000: 39 0b 07 10 48 3e 31 10 48 3a f6 00 00 91 5b 00 9...H>1.H:....[.
 * 0010: 00 4c f1 ff ff .L...
 */
public class TargetSelected extends L2GameServerPacket
{
	private static final String _S__39_TARGETSELECTED = "[S] 29 TargetSelected";
	private final int _objectId;
	private final int _targetObjId;
	private final int _x;
	private final int _y;
	private final int _z;

	/**
	 * @param _characters
	 */
	public TargetSelected(int objectId, int targetId, int x, int y, int z)
	{
		_objectId = objectId;
		_targetObjId = targetId;
		_x = x;
		_y = y;
		_z = z;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x29);
		writeD(_objectId);
		writeD(_targetObjId);
		writeD(_x);
		writeD(_y);
		writeD(_z);
	}

	@Override
	public String getType()
	{
		return _S__39_TARGETSELECTED;
	}
}