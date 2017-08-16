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
 *                c1 b2 e0 4a<BR>
 *                00 00 00 00<BR>
 *                <p>
 *                format cdd
 * @formatter:on
 */
public class AskJoinFriend extends L2GameServerPacket
{
	private static final String _S__7d_ASKJoinFriend_0X7d = "[S] 7d AskJoinFriend 0x7d";

	private final String _requestorName;

	// private int _itemDistribution;

	/**
	 * @param int objectId of the target
	 * @param int
	 */
	// public AskJoinFriend(String requestorName, int itemDistribution)
	public AskJoinFriend(String requestorName)
	{
		_requestorName = requestorName;
		// _itemDistribution = itemDistribution;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x7d);
		writeS(_requestorName);
		writeD(0);
	}

	@Override
	public String getType()
	{
		return _S__7d_ASKJoinFriend_0X7d;
	}
}