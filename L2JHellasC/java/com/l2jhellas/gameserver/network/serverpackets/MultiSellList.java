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

import com.l2jhellas.gameserver.datatables.sql.ItemTable;
import com.l2jhellas.gameserver.datatables.xml.MultisellData.MultiSellEntry;
import com.l2jhellas.gameserver.datatables.xml.MultisellData.MultiSellIngredient;
import com.l2jhellas.gameserver.datatables.xml.MultisellData.MultiSellListContainer;
import com.l2jhellas.gameserver.templates.L2Item;

public class MultiSellList extends L2GameServerPacket
{
	private static final String _S__D0_MULTISELLLIST = "[S] D0 MultiSellList";

	protected int _listId, _page, _finished;
	protected MultiSellListContainer _list;

	public MultiSellList(MultiSellListContainer list, int page, int finished)
	{
		_list = list;
		_listId = list.getListId();
		_page = page;
		_finished = finished;
	}

	@Override
	protected void writeImpl()
	{
		// [ddddd] [dchh] [hdhdh] [hhdh]

		writeC(0xd0);
		writeD(_listId);    // list id
		writeD(_page);		// page
		writeD(_finished);	// finished
		writeD(0x28);	    // size of pages
		writeD(_list == null ? 0 : _list.getEntries().size()); // list length

		if (_list != null)
		{
			for (MultiSellEntry ent : _list.getEntries())
			{
				writeD(ent.getEntryId());
				writeD(0x00); // C6
				writeD(0x00); // C6
				writeC(1);
				writeH(ent.getProducts().size());
				writeH(ent.getIngredients().size());

				for (MultiSellIngredient i : ent.getProducts())
				{
					int item = i.getItemId();
					int bodyPart = 0;
					int type2 = 65535;

					if (item > 0)
					{
						L2Item template = ItemTable.getInstance().getTemplate(item);
						if (template != null)
						{
							bodyPart = template.getBodyPart();
							type2 = template.getType2();
						}
					}

					writeH(item);
					writeD(bodyPart);
					writeH(type2);
					writeD(i.getItemCount());
					writeH(i.getEnchantmentLevel()); // Enchant Level
					writeD(0x00); // C6
					writeD(0x00); // C6
				}

				for (MultiSellIngredient i : ent.getIngredients())
				{
					L2Item template = ItemTable.getInstance().getTemplate(i.getItemId());				
					writeH(i.getItemId());
					writeH(template != null ? template.getType2() : 65535);
					writeD(i.getItemCount());
				    writeH(i.getEnchantmentLevel());
					writeD(0x00); // i.getAugmentId()
					writeD(0x00); // i.getManaLeft()
				}
			}
		}
	}

	@Override
	public String getType()
	{
		return _S__D0_MULTISELLLIST;
	}
}