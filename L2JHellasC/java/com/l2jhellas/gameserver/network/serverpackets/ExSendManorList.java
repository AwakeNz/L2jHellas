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

import java.util.ArrayList;

/**
 * Format : (h) d [dS]<BR>
 * h sub id<BR>
 * <BR>
 * d: number of manors<BR>
 * [<BR>
 * d: id<BR>
 * S: manor name<BR>
 * ]<BR>
 * 
 * @author l3x
 */
public class ExSendManorList extends L2GameServerPacket
{
	private static final String _S__FE_1B_EXSENDMANORLIST = "[S] FE:1B ExSendManorList";

	private final ArrayList<String> _manors;

	public ExSendManorList(ArrayList<String> manorsName)
	{
		_manors = manorsName;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x1B);
		writeD(_manors.size());
		for (int i = 0; i < _manors.size(); i++)
		{
			int j = i + 1;
			writeD(j);
			writeS(_manors.get(i));
		}

	}

	@Override
	public String getType()
	{
		return _S__FE_1B_EXSENDMANORLIST;
	}
}