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

public class SocialAction extends L2GameServerPacket
{
	private static final String _S__3D_SOCIALACTION = "[S] 2D SocialAction";
	private final int _charObjId;
	private final int _actionId;

	/**
	 * 0x3d SocialAction dd
	 * 
	 * @param _characters
	 */
	public SocialAction(int playerId, int actionId)
	{
		_charObjId = playerId;
		_actionId = actionId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x2d);
		writeD(_charObjId);
		writeD(_actionId);
	}

	@Override
	public String getType()
	{
		return _S__3D_SOCIALACTION;
	}
}