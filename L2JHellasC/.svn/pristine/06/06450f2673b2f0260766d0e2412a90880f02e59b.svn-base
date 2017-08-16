/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jhellas.gameserver.scrips.quests;

import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.base.Race;
import com.l2jhellas.gameserver.model.quest.Quest;
import com.l2jhellas.gameserver.model.quest.QuestState;
import com.l2jhellas.util.Rnd;

public class Q163_LegacyOfThePoet extends Quest
{
	private static final String qn = "Q163_LegacyOfThePoet";
	
	// NPC
	private static final int STARDEN = 30220;
	
	// Items
	private static final int[] RUMIELS_POEMS =
	{
		1038,
		1039,
		1040,
		1041
	};
	
	public Q163_LegacyOfThePoet()
	{
		super(163, qn, "Legacy of the Poet");
		
		setItemsIds(RUMIELS_POEMS);
		
		addStartNpc(STARDEN);
		addTalkId(STARDEN);
		
		addKillId(20372, 20373);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30220-07.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getRace() == Race.darkelf)
					htmltext = "30220-00.htm";
				else if (player.getLevel() < 11)
					htmltext = "30220-02.htm";
				else
					htmltext = "30220-03.htm";
				break;
			
			case STATE_STARTED:
				if (st.getInt("cond") == 2)
				{
					htmltext = "30220-09.htm";
					
					for (int poem : RUMIELS_POEMS)
						st.takeItems(poem, -1);
					
					st.rewardItems(57, 13890);
					st.playSound(QuestState.SOUND_FINISH);
					st.exitQuest(false);
				}
				else
					htmltext = "30220-08.htm";
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
			return null;
		
		if (Rnd.get(100) < 33)
		{
			final int randomItemId = Rnd.get(1038, 1041);
			if (!st.hasQuestItems(randomItemId))
			{
				st.giveItems(randomItemId, 1);
				if (gotAllPoems(st))
				{
					st.set("cond", "2");
					st.playSound(QuestState.SOUND_MIDDLE);
				}
				else
					st.playSound(QuestState.SOUND_ITEMGET);
			}
		}
		
		return null;
	}
	
	private static boolean gotAllPoems(QuestState st)
	{
		for (int itemId : RUMIELS_POEMS)
		{
			if (!st.hasQuestItems(itemId))
				return false;
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		new Q163_LegacyOfThePoet();
	}
}