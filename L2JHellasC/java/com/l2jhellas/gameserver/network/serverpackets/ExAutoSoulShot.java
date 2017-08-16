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

public class ExAutoSoulShot extends L2GameServerPacket
{
	private static final String _S__FE_12_EXAUTOSOULSHOT = "[S] FE:12 ExAutoSoulShot";
	private final int _itemId;
	private final int _type;

	/**
	 * 0xfe:0x12 ExAutoSoulShot (ch)dd
	 * 
	 * @param _characters
	 */
	public ExAutoSoulShot(int itemId, int type)
	{
		_itemId = itemId;
		_type = type;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xFE);
		writeH(0x12); // sub id
		writeD(_itemId);
		writeD(_type);
	}

	@Override
	public String getType()
	{
		return _S__FE_12_EXAUTOSOULSHOT;
	}
}