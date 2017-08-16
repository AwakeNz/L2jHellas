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

import java.util.HashMap;
import java.util.Map;

import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.quest.Quest;
import com.l2jhellas.gameserver.model.quest.QuestState;
import com.l2jhellas.util.Rnd;

public class Q038_DragonFangs extends Quest
{
	private static final String qn = "Q038_DragonFangs";
	
	// Items
	private static final int FEATHER_ORNAMENT = 7173;
	private static final int TOOTH_OF_TOTEM = 7174;
	private static final int TOOTH_OF_DRAGON = 7175;
	private static final int LETTER_OF_IRIS = 7176;
	private static final int LETTER_OF_ROHMER = 7177;
	
	// NPCs
	private static final int LUIS = 30386;
	private static final int IRIS = 30034;
	private static final int ROHMER = 30344;
	
	// Reward { item, adena }
	private static final int REWARD[][] =
	{
		{
			45,
			5200
		},
		{
			627,
			1500
		},
		{
			1123,
			3200
		},
		{
			605,
			3200
		}
	};
	
	// Droplist
	private static final Map<Integer, int[]> DROPLIST = new HashMap<Integer, int[]>();
	{
		DROPLIST.put(21100, new int[]
		{
			1,
			FEATHER_ORNAMENT,
			100,
			1000000
		});
		DROPLIST.put(20357, new int[]
		{
			1,
			FEATHER_ORNAMENT,
			100,
			1000000
		});
		DROPLIST.put(21101, new int[]
		{
			6,
			TOOTH_OF_DRAGON,
			50,
			500000
		});
		DROPLIST.put(20356, new int[]
		{
			6,
			TOOTH_OF_DRAGON,
			50,
			500000
		});
	}
	
	public Q038_DragonFangs()
	{
		super(38, qn, "Dragon Fangs");
		
		setItemsIds(FEATHER_ORNAMENT, TOOTH_OF_TOTEM, TOOTH_OF_DRAGON, LETTER_OF_IRIS, LETTER_OF_ROHMER);
		
		addStartNpc(LUIS);
		addTalkId(LUIS, IRIS, ROHMER);
		
		for (int mob : DROPLIST.keySet())
			addKillId(mob);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30386-02.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30386-04.htm"))
		{
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(FEATHER_ORNAMENT, 100);
			st.giveItems(TOOTH_OF_TOTEM, 1);
		}
		else if (event.equalsIgnoreCase("30034-02a.htm"))
		{
			if (st.hasQuestItems(TOOTH_OF_TOTEM))
			{
				htmltext = "30034-02.htm";
				st.set("cond", "4");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(TOOTH_OF_TOTEM, 1);
				st.giveItems(LETTER_OF_IRIS, 1);
			}
		}
		else if (event.equalsIgnoreCase("30344-02a.htm"))
		{
			if (st.hasQuestItems(LETTER_OF_IRIS))
			{
				htmltext = "30344-02.htm";
				st.set("cond", "5");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(LETTER_OF_IRIS, 1);
				st.giveItems(LETTER_OF_ROHMER, 1);
			}
		}
		else if (event.equalsIgnoreCase("30034-04a.htm"))
		{
			if (st.hasQuestItems(LETTER_OF_ROHMER))
			{
				htmltext = "30034-04.htm";
				st.set("cond", "6");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(LETTER_OF_ROHMER, 1);
			}
		}
		else if (event.equalsIgnoreCase("30034-06a.htm"))
		{
			if (st.getQuestItemsCount(TOOTH_OF_DRAGON) >= 50)
			{
				int position = Rnd.get(REWARD.length);
				
				htmltext = "30034-06.htm";
				st.takeItems(TOOTH_OF_DRAGON, 50);
				st.giveItems(REWARD[position][0], 1);
				st.rewardItems(57, REWARD[position][1]);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(false);
			}
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
				htmltext = (player.getLevel() < 19) ? "30386-01a.htm" : "30386-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case LUIS:
						if (cond == 1)
							htmltext = "30386-02a.htm";
						else if (cond == 2)
							htmltext = "30386-03.htm";
						else if (cond > 2)
							htmltext = "30386-03a.htm";
						break;
					
					case IRIS:
						if (cond == 3)
							htmltext = "30034-01.htm";
						else if (cond == 4)
							htmltext = "30034-02b.htm";
						else if (cond == 5)
							htmltext = "30034-03.htm";
						else if (cond == 6)
							htmltext = "30034-05a.htm";
						else if (cond == 7)
							htmltext = "30034-05.htm";
						break;
					
					case ROHMER:
						if (cond == 4)
							htmltext = "30344-01.htm";
						else if (cond > 4)
							htmltext = "30344-03.htm";
						break;
				}
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
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		final int droplist[] = DROPLIST.get(npc.getNpcId());
		
		if (st.getInt("cond") == droplist[0] && st.dropItems(droplist[1], 1, droplist[2], droplist[3]))
			st.set("cond", String.valueOf(droplist[0] + 1));
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q038_DragonFangs();
	}
}