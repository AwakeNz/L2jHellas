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
package com.l2jhellas.gameserver.network.clientpackets;

import com.l2jhellas.gameserver.ai.CtrlEvent;
import com.l2jhellas.gameserver.model.L2CharPosition;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.PartyMemberPosition;

public final class CannotMoveAnymore extends L2GameClientPacket
{
	private static final String _C__36_STOPMOVE = "[C] 36 CannotMoveAnymore";

	private int _x;
	private int _y;
	private int _z;
	private int _heading;

	@Override
	protected void readImpl()
	{
		_x = readD();
		_y = readD();
		_z = readD();
		_heading = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2Character player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (player.hasAI())
			player.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, new L2CharPosition(_x, _y, _z, _heading));
		
		if (player instanceof L2PcInstance && ((L2PcInstance) player).getParty() != null)
			((L2PcInstance) player).getParty().broadcastToPartyMembers(((L2PcInstance) player), new PartyMemberPosition( player.getParty()));
	}

	@Override
	public String getType()
	{
		return _C__36_STOPMOVE;
	}
}