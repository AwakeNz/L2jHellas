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
import com.l2jhellas.gameserver.model.quest.Quest;
import com.l2jhellas.gameserver.model.quest.QuestState;

public class Q018_MeetingWithTheGoldenRam extends Quest
{
	private static final String qn = "Q018_MeetingWithTheGoldenRam";
	
	// Items
	private static final int SUPPLY_BOX = 7245;
	
	// NPCs
	private static final int DONAL = 31314;
	private static final int DAISY = 31315;
	private static final int ABERCROMBIE = 31555;
	
	public Q018_MeetingWithTheGoldenRam()
	{
		super(18, qn, "Meeting with the Golden Ram");
		
		setItemsIds(SUPPLY_BOX);
		
		addStartNpc(DONAL);
		addTalkId(DONAL, DAISY, ABERCROMBIE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31314-03.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31315-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(SUPPLY_BOX, 1);
		}
		else if (event.equalsIgnoreCase("31555-02.htm"))
		{
			st.takeItems(SUPPLY_BOX, 1);
			st.rewardItems(57, 15000);
			st.rewardExpAndSp(50000, 0);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = (player.getLevel() < 66) ? "31314-02.htm" : "31314-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case DONAL:
						htmltext = "31314-04.htm";
						break;
					
					case DAISY:
						if (cond == 1)
							htmltext = "31315-01.htm";
						else if (cond == 2)
							htmltext = "31315-03.htm";
						break;
					
					case ABERCROMBIE:
						if (cond == 2)
							htmltext = "31555-01.htm";
						break;
				}
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Q018_MeetingWithTheGoldenRam();
	}
}