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
 *                <p>
 *                7d<BR>
 *                c1 b2 e0 4<BR>
 *                00 00 00 00<BR>
 *                <p>
 *                format cdd
 * @formatter:on
 */
public class AskJoinAlly extends L2GameServerPacket
{
	private static final String _S__A8_ASKJOINALLY_0XA8 = "[S] a8 AskJoinAlly 0xa8";

	private final String _requestorName;
	private final int _requestorObjId;

	/**
	 * @param requestorObjId
	 * @param requestorName
	 */
	public AskJoinAlly(int requestorObjId, String requestorName)
	{
		_requestorName = requestorName;
		_requestorObjId = requestorObjId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xa8);
		writeD(_requestorObjId);
		writeS(_requestorName);
	}

	@Override
	public String getType()
	{
		return _S__A8_ASKJOINALLY_0XA8;
	}
}