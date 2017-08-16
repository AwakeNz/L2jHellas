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

import java.util.logging.Logger;

import com.l2jhellas.gameserver.datatables.sql.CharNameTable;
import com.l2jhellas.gameserver.model.BlockList;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;

public final class RequestBlock extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestBlock.class.getName());
	private static final String _C__A0_REQUESTBLOCK = "[C] A0 RequestBlock";

	private final static int BLOCK = 0;
	private final static int UNBLOCK = 1;
	private final static int BLOCKLIST = 2;
	private final static int ALLBLOCK = 3;
	private final static int ALLUNBLOCK = 4;

	private String _name;
	private Integer _type;

	@Override
	protected void readImpl()
	{
		_type = readD(); // 0x00 - block, 0x01 - unblock, 0x03 - allblock, 0x04 - allunblock

		if (_type == BLOCK || _type == UNBLOCK)
			_name = readS();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
			return;
		
		switch (_type)
		{
			case BLOCK:
			case UNBLOCK:

				final int targetId = CharNameTable.getInstance().getIdByName(_name);
				
				if (targetId <= 0 || activeChar.getObjectId() == targetId)
				{
					activeChar.sendPacket(SystemMessageId.FAILED_TO_REGISTER_TO_IGNORE_LIST);
					return;
				}
				
				if (CharNameTable.getInstance().getAccessLevelById(targetId) > 0)
				{
					activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_GM);
					return;
				}
				
				if (_type == BLOCK)
					BlockList.addToBlockList(activeChar, targetId);
				else
					BlockList.removeFromBlockList(activeChar, targetId);
				break;
			
			case BLOCKLIST:
				BlockList.sendListToOwner(activeChar);
				break;
			
			case ALLBLOCK:
				activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
				BlockList.setBlockAll(activeChar, true);
				break;
			
			case ALLUNBLOCK:
				activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
				BlockList.setBlockAll(activeChar, false);
				break;
			
			default:
				_log.info("Unknown 0x0a block type: " + _type);
		}
	}

	@Override
	public String getType()
	{
		return _C__A0_REQUESTBLOCK;
	}
}