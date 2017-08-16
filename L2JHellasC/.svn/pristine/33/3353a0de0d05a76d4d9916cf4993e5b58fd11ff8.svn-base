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

import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.zone.ZoneId;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;

public final class AttackRequest extends L2GameClientPacket
{
	// cddddc
	private int _objectId;
	@SuppressWarnings("unused")
	private int _originX, _originY, _originZ;
	@SuppressWarnings("unused")
	private int _attackId;

	private static final String _C__0A_ATTACKREQUEST = "[C] 0A AttackRequest";

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
		_attackId = readC(); // 0 for simple click 1 for shift-click
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
			return;
			
		if (activeChar.isSpawnProtected())
			activeChar.setProtection(false);
		
		if(_objectId==0)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.inObserverMode())
		{
			activeChar.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
	
		// avoid using expensive operations if not needed
		final L2Object target;
		if (activeChar.getTargetId() == _objectId)
			target = activeChar.getTarget();
		else
			target = L2World.getInstance().findObject(_objectId);
		
		if (target == null)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (target instanceof L2PcInstance)
		{
			final L2PcInstance ptar = (L2PcInstance)activeChar.getTarget();
			
			if(ptar.isInsideZone(ZoneId.PEACE))
			{
				activeChar.sendPacket(new ActionFailed());
				return;
			}			
		}
		
		// Like L2OFF
		if (activeChar.isAttackingNow() && activeChar.isMoving())
		{
			// If target is not attackable, send a Server->Client packet ActionFailed
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.getTarget() != target)
			target.onAction(activeChar);
		else
		{
			if ((target.getObjectId() != activeChar.getObjectId()) && !activeChar.isInStoreMode() && activeChar.getActiveRequester() == null)
				target.onForcedAttack(activeChar);
			else
				sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	@Override
	public String getType()
	{
		return _C__0A_ATTACKREQUEST;
	}
}