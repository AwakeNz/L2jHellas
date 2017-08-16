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

public class AutoAttackStop extends L2GameServerPacket
{
	// dh
	private static final String _S__3C_AUTOATTACKSTOP = "[S] 3C AutoAttackStop";
	private final int _targetObjId;

	/**
	 * @param _characters
	 */
	public AutoAttackStop(int targetObjId)
	{
		_targetObjId = targetObjId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x2c);
		writeD(_targetObjId);
	}

	@Override
	public String getType()
	{
		return _S__3C_AUTOATTACKSTOP;
	}
}