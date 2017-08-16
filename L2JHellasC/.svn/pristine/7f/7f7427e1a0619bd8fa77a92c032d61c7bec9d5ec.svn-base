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

import com.l2jhellas.gameserver.model.L2Clan.SubPledge;

/**
 * @author -Wooden-
 */
public class PledgeReceiveSubPledgeCreated extends L2GameServerPacket
{
	private static final String _S__FE_3F_PLEDGERECEIVESUBPLEDGECREATED = "[S] FE:3F PledgeReceiveSubPledgeCreated";
	private final SubPledge _subPledge;

	/**
	 * @param member
	 */
	public PledgeReceiveSubPledgeCreated(SubPledge subPledge)
	{
		_subPledge = subPledge;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x3f);

		writeD(0x01);
		writeD(_subPledge.getId());
		writeS(_subPledge.getName());
		writeS(_subPledge.getLeaderName());
	}

	@Override
	public String getType()
	{
		return _S__FE_3F_PLEDGERECEIVESUBPLEDGECREATED;
	}
}