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
 * @formatter:off
 *                sample<BR>
 *                0000: c7 6d 06 00 00 36 05 00 00 42 4d 36 05 00 00 00 .m...6...BM6....<BR>
 *                0010: 00 00 00 36 04 00 00 28 00 00 00 10 00 00 00 10 ...6...(........<BR>
 *                0020: 00 00 00 01 00 08 00 00 00 00 00 00 01 00 00 c4 ................<BR>
 *                0030: ...<BR>
 *                0530: 10 91 00 00 00 60 9b d1 01 e4 6e ee 52 97 dd .....`....n.R..<BR>
 *                format dd x...x
 * @formatter:on
 */
public class AllyCrest extends L2GameServerPacket
{
	private static final String _S__C7_ALLYCREST = "[S] ae AllyCrest";
	private final int _crestId;
	private final int _crestSize;
	private byte[] _data;

	public AllyCrest(int crestId, byte[] data)
	{
		_crestId = crestId;
		_data = data;
		_crestSize = _data.length;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xae);
		writeD(_crestId);
		writeD(_crestSize);
		writeB(_data);
		_data = null;
	}

	@Override
	public String getType()
	{
		return _S__C7_ALLYCREST;
	}
}