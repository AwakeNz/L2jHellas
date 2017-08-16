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

import com.l2jhellas.gameserver.model.actor.instance.L2DoorInstance;

/**
 * 61<br>
 * d6 6d c0 4b door id<br>
 * 8f 14 00 00 x<br>
 * b7 f1 00 00 y<br>
 * 60 f2 ff ff z<br>
 * 00 00 00 00 ??<br>
 * <br>
 * format dddd ID:%d X:%d Y:%d Z:%d<br>
 * ddddd <br>
 */
public class DoorStatusUpdate extends L2GameServerPacket
{
	private static final String _S__61_DOORSTATUSUPDATE = "[S] 4d DoorStatusUpdate";
	private final L2DoorInstance _door;

	public DoorStatusUpdate(L2DoorInstance door)
	{
		_door = door;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4d);
		writeD(_door.getObjectId());
		writeD(_door.getOpen() ? 0 : 1);
		writeD(_door.getDamage());
		writeD(_door.isEnemyOf(getClient().getActiveChar()) ? 1 : 0);
		writeD(_door.getDoorId());
		writeD(_door.getMaxHp());
		writeD((int) _door.getCurrentHp());
	}

	@Override
	public String getType()
	{
		return _S__61_DOORSTATUSUPDATE;
	}
}