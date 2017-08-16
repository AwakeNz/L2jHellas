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
 * 60<BR>
 * d6 6d c0 4b door id<BR>
 * 8f 14 00 00 x<BR>
 * b7 f1 00 00 y<BR>
 * 60 f2 ff ff z<BR>
 * 00 00 00 00 ??<BR>
 * <BR>
 * format dddd ID:%d X:%d Y:%d Z:%d<BR>
 * ddddd
 */
public class DoorInfo extends L2GameServerPacket
{
	private static final String _S__60_DOORINFO = "[S] 4c DoorInfo";
	private final L2DoorInstance _door;
	private final int _type;
	private final boolean _isTargetable;
	private final boolean _isClosed;
	private final int _maxHp;
	private final int _currentHp;
	private final boolean _showHp;
	private final int _damageGrade;

	public DoorInfo(L2DoorInstance door, boolean showHp)
	{
		door.getDoorId();
		door.getObjectId();
		_type = 1;
		_door = door;
		_isTargetable = true;
		_isClosed = !door.getOpen();
		door.isEnemyOf(_door);
		_maxHp = door.getMaxHp();
		_currentHp = (int) door.getCurrentHp();
		_showHp = showHp;
		_damageGrade = door.getDamage();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4c);
		writeD(_door.getObjectId());
		writeD(_door.getDoorId());
		writeD(_type);
		writeD(_isTargetable ? 1 : 0);
		writeD(_isClosed ? 1 : 0);
		writeD(_door.isEnemyOf(getClient().getActiveChar()) ? 1 : 0);
		writeD(_currentHp);
		writeD(_maxHp);
		writeD(_showHp ? 1 : 0);
		writeD(_damageGrade);
	}

	@Override
	public String getType()
	{
		return _S__60_DOORINFO;
	}
}