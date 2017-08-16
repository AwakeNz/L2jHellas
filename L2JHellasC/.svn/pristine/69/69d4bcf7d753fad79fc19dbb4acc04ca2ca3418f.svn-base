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

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ExAutoSoulShot;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;

public final class RequestAutoSoulShot extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestAutoSoulShot.class.getName());
	private static final String _C__CF_REQUESTAUTOSOULSHOT = "[C] CF RequestAutoSoulShot";

	// format cd
	private int _itemId;
	private int _type; // 1 = on : 0 = off;

	@Override
	protected void readImpl()
	{
		_itemId = readD();
		_type = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null)
			return;
		
		if (activeChar.isSitting())
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CANNOT_AUTO_USE_LACK_OF_S1);
			sm.addItemName(_itemId);
			activeChar.sendPacket(sm);
			return;
		}
		
		if (activeChar.getPrivateStoreType() == 0 && activeChar.getActiveRequester() == null && !activeChar.isDead())
		{
			if (Config.DEBUG)
				_log.fine("AutoSoulShot:" + _itemId);

			L2ItemInstance item = activeChar.getInventory().getItemByItemId(_itemId);

			if (item != null)
			{
				if (_type == 1)
				{
					// Fishing shots are not automatic on retail
					if (_itemId < 6535 || _itemId > 6540)
					{
						// Attempt to charge first shot on activation
						if (_itemId == 6645 || _itemId == 6646 || _itemId == 6647)
						{
							activeChar.addAutoSoulShot(_itemId);
							ExAutoSoulShot atk = new ExAutoSoulShot(_itemId, _type);
							activeChar.sendPacket(atk);

							// start the auto soulshot use
							SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO);
							sm.addString(item.getItemName());
							activeChar.sendPacket(sm);
							sm = null;

							activeChar.rechargeAutoSoulShot(true, true, true);
						}
						else
						{
							if (activeChar.getActiveWeaponItem() != activeChar.getFistsWeaponItem() && item.getItem().getCrystalType() == activeChar.getActiveWeaponItem().getCrystalType())
							{
								if (_itemId >= 3947 && _itemId <= 3952 && activeChar.isInOlympiadMode())
								{
									SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
									sm.addString(item.getItemName());
									activeChar.sendPacket(sm);
									sm = null;
								}
								else
								{
									activeChar.addAutoSoulShot(_itemId);
									ExAutoSoulShot atk = new ExAutoSoulShot(_itemId, _type);
									activeChar.sendPacket(atk);

									// start the auto soulshot use
									SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO);
									sm.addString(item.getItemName());
									activeChar.sendPacket(sm);
									sm = null;

									activeChar.rechargeAutoSoulShot(true, true, false);
								}
							}
							else
							{
								if ((_itemId >= 2509 && _itemId <= 2514) || (_itemId >= 3947 && _itemId <= 3952) || _itemId == 5790)
									activeChar.sendPacket(SystemMessageId.SPIRITSHOTS_GRADE_MISMATCH);
								else
									activeChar.sendPacket(SystemMessageId.SOULSHOTS_GRADE_MISMATCH);
							}
						}
					}
				}
				else if (_type == 0)
				{
					activeChar.removeAutoSoulShot(_itemId);
					ExAutoSoulShot atk = new ExAutoSoulShot(_itemId, _type);
					activeChar.sendPacket(atk);

					// cancel the auto soulshot use
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED);
					sm.addString(item.getItemName());
					activeChar.sendPacket(sm);
					sm = null;
				}
			}
		}
	}

	@Override
	public String getType()
	{
		return _C__CF_REQUESTAUTOSOULSHOT;
	}
}