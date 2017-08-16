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

import java.util.List;

import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.templates.L2Henna;

public class HennaEquipList extends L2GameServerPacket
{
	private static final String _S__E2_HennaEquipList = "[S] E2 HennaEquipList";

	private final L2PcInstance _player;
	private final List<L2Henna> _hennaEquipList;

	public HennaEquipList(L2PcInstance player, List<L2Henna> hennaEquipList)
	{
		_player = player;
		_hennaEquipList = hennaEquipList;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe2);
		writeD(_player.getAdena());
		writeD(3);
		writeD(_hennaEquipList.size());
		
		for (L2Henna temp : _hennaEquipList)
		{
			// Player must have at least one dye in inventory to be able to see the henna that can be applied with it.
			if ((_player.getInventory().getItemByItemId(temp.getDyeId())) != null)
			{
				writeD(temp.getSymbolId()); // symbolid
				writeD(temp.getDyeId()); // itemid of dye
				writeD(L2Henna.getAmountDyeRequire()); // amount of dyes required
				writeD(temp.getPrice()); // amount of adenas required
				writeD(1); // meet the requirement or not
			}
		}
	}

	@Override
	public String getType()
	{
		return _S__E2_HennaEquipList;
	}
}