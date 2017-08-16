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

import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.templates.L2Item;

/**
 * 5e<BR>
 * 01 00 00 00 01 - added ? 02 - modified<BR>
 * 7b 86 73 42 object id<BR>
 * 08 00 00 00 body slot<BR>
 * body slot<BR>
 * 0000 ?? underwear<BR>
 * 0001 ear<BR>
 * 0002 ear<BR>
 * 0003 neck<BR>
 * 0004 finger (magic ring)<BR>
 * 0005 finger (magic ring)<BR>
 * 0006 head (l.cap)<BR>
 * 0007 r.hand (dagger)<BR>
 * 0008 l.hand (arrows)<BR>
 * 0009 hands (short gloves)<BR>
 * 000a chest (squire shirt)<BR>
 * 000b legs (squire pants)<BR>
 * 000c feet<BR>
 * 000d ?? back<BR>
 * 000e lr.hand (bow)<BR>
 * format ddd
 */
public class EquipUpdate extends L2GameServerPacket
{
	private static Logger _log = Logger.getLogger(EquipUpdate.class.getName());
	private static final String _S__5E_EQUIPUPDATE = "[S] 4b EquipUpdate";

	private final L2ItemInstance _item;
	private final int _change;

	public EquipUpdate(L2ItemInstance item, int change)
	{
		_item = item;
		_change = change;
	}

	@Override
	protected final void writeImpl()
	{
		int bodypart = 0;
		writeC(0x4b);
		writeD(_change);
		writeD(_item.getObjectId());
		switch (_item.getItem().getBodyPart())
		{
			case L2Item.SLOT_L_EAR:
				bodypart = 0x01;
			break;
			case L2Item.SLOT_R_EAR:
				bodypart = 0x02;
			break;
			case L2Item.SLOT_NECK:
				bodypart = 0x03;
			break;
			case L2Item.SLOT_R_FINGER:
				bodypart = 0x04;
			break;
			case L2Item.SLOT_L_FINGER:
				bodypart = 0x05;
			break;
			case L2Item.SLOT_HEAD:
				bodypart = 0x06;
			break;
			case L2Item.SLOT_R_HAND:
				bodypart = 0x07;
			break;
			case L2Item.SLOT_L_HAND:
				bodypart = 0x08;
			break;
			case L2Item.SLOT_GLOVES:
				bodypart = 0x09;
			break;
			case L2Item.SLOT_CHEST:
				bodypart = 0x0a;
			break;
			case L2Item.SLOT_LEGS:
				bodypart = 0x0b;
			break;
			case L2Item.SLOT_FEET:
				bodypart = 0x0c;
			break;
			case L2Item.SLOT_BACK:
				bodypart = 0x0d;
			break;
			case L2Item.SLOT_LR_HAND:
				bodypart = 0x0e;
			break;
			case L2Item.SLOT_HAIR:
				bodypart = 0x0f;
			break;
		}

		if (Config.DEBUG)
			_log.fine("body:" + bodypart);
		writeD(bodypart);
	}

	@Override
	public String getType()
	{
		return _S__5E_EQUIPUPDATE;
	}
}