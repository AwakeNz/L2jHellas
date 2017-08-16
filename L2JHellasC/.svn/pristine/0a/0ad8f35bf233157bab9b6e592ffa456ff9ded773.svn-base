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
package com.l2jhellas.gameserver.model.actor.status;

import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Npc;

public class NpcStatus extends CharStatus
{
	public NpcStatus(L2Npc activeChar)
	{
		super(activeChar);
	}

	@Override
	public final void reduceHp(double value, L2Character attacker)
	{
		reduceHp(value, attacker, true);
	}

	@Override
	public final void reduceHp(double value, L2Character attacker, boolean awake)
	{
		if (getActiveChar().isDead())
			return;

		// Add attackers to npc's attacker list
		if (attacker != null)
			getActiveChar().addAttackerToAttackByList(attacker);

		super.reduceHp(value, attacker, awake);
	}

	@Override
	public L2Npc getActiveChar()
	{
		return (L2Npc) super.getActiveChar();
	}
}