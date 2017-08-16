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

import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ExConfirmVariationGemstone;

/**
 * Format:(ch) dddd
 * 
 * @author -Wooden-
 */
public final class RequestConfirmGemStone extends AbstractRefinePacket
{
	private static final String _C__D0_2B_REQUESTCONFIRMGEMSTONE = "[C] D0:2B RequestConfirmGemStone";
	
	private int _targetItemObjId;
	private int _refinerItemObjId;
	private int _gemstoneItemObjId;
	private int _gemStoneCount;

	/**
	 * @param buf
	 * @param client
	 */
	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
		_refinerItemObjId = readD();
		_gemstoneItemObjId = readD();
		_gemStoneCount = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
			return;
		
		final L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
		
		if (targetItem == null)
			return;
		
		final L2ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_refinerItemObjId);
		
		if (refinerItem == null)
			return;
		
		final L2ItemInstance gemStoneItem = activeChar.getInventory().getItemByObjectId(_gemstoneItemObjId);
		
		if (gemStoneItem == null)
			return;
		
		if (!isValid(activeChar, targetItem, refinerItem, gemStoneItem))
		{
			activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}

		final LifeStone ls = getLifeStone(refinerItem.getItemId());
		
		if (ls == null)
			return;
		
		if (_gemStoneCount != getGemStoneCount(targetItem.getItem().getCrystalType()))
		{
			activeChar.sendPacket(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT);
			return;
		}
		
		activeChar.sendPacket(new ExConfirmVariationGemstone(_gemstoneItemObjId,_gemStoneCount));
		activeChar.sendPacket(SystemMessageId.PRESS_THE_AUGMENT_BUTTON_TO_BEGIN);
	}

	@Override
	public String getType()
	{
		return _C__D0_2B_REQUESTCONFIRMGEMSTONE;
	}
}