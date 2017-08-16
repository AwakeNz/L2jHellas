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
import com.l2jhellas.gameserver.model.quest.Quest;
import com.l2jhellas.gameserver.model.quest.QuestState;

/**
 * 98
 * 05 00 number of quests
 * ff 00 00 00
 * 0a 01 00 00
 * 39 01 00 00
 * 04 01 00 00
 * a2 00 00 00
 * format h (d) h (dddh) rev 377
 * format h (dd) h (dddd) rev 417
 */
public class QuestList extends L2GameServerPacket
{
	private static final String _S__98_QUESTLIST = "[S] 80 QuestList";

	private final List<Quest> _quests;
	private final L2PcInstance _activeChar;
	
	public QuestList(L2PcInstance player)
	{
		_activeChar = player;
		_quests = player.getAllQuests(true);
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0x80);
		writeH(_quests.size());
		for (Quest q : _quests)
		{
			writeD(q.getQuestId());
			QuestState qs = _activeChar.getQuestState(q.getName());
			if (qs == null)
			{
				writeD(0);
				continue;
			}
			
			int states = qs.getInt("__compltdStateFlags");
			if (states > 0)
				writeD(states);
			else
				writeD(qs.getInt("cond"));
		}
	}

	@Override
	public String getType()
	{
		return _S__98_QUESTLIST;
	}
}