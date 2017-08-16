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
package com.l2jhellas.gameserver.scrips.quests.ai.group;

import com.l2jhellas.gameserver.ai.CtrlEvent;
import com.l2jhellas.gameserver.datatables.sql.SpawnTable;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.L2Spawn;
import com.l2jhellas.gameserver.model.actor.L2Attackable;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.quest.QuestEventType;
import com.l2jhellas.gameserver.scrips.quests.ai.AbstractNpcAI;
import com.l2jhellas.gameserver.skills.SkillTable;
import com.l2jhellas.util.Util;

/**
 * Primeval Isle AIs. This script controls following behaviors :
 * <ul>
 * <li>Sprigant : casts a spell if you enter in aggro range, finish task if die or none around.</li>
 * <li>Ancient Egg : call all NPCs in a 2k range if attacked.</li>
 * <li>Pterosaurs and Tyrannosaurus : can see through Silent Move.</li>
 * </ul>
 */
public class PrimevalIsle extends AbstractNpcAI
{
	private static final int[] _sprigants =
	{
		18345,
		18346
	};
	
	private static final int[] MOBIDS =
	{
		22199,
		22215,
		22216,
		22217
	};
	
	private static final int ANCIENT_EGG = 18344;
	
	private static final L2Skill ANESTHESIA = SkillTable.getInstance().getInfo(5085, 1);
	private static final L2Skill POISON = SkillTable.getInstance().getInfo(5086, 1);
	
	public PrimevalIsle()
	{
		super(PrimevalIsle.class.getSimpleName(), "ai/group");
		
		for (L2Spawn npc : SpawnTable.getInstance().getSpawnTable().values())
			if (Util.contains(MOBIDS, npc.getNpcid()) && npc.getLastSpawn() != null && npc.getLastSpawn() instanceof L2Attackable)
				((L2Attackable) npc.getLastSpawn()).seeThroughSilentMove(true);
		
		registerMobs(_sprigants, QuestEventType.ON_AGGRO_RANGE_ENTER, QuestEventType.ON_KILL);
		addAttackId(ANCIENT_EGG);
		addSpawnId(MOBIDS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (!(npc instanceof L2Attackable))
			return null;
		
		if (event.equalsIgnoreCase("skill"))
		{
			int playableCounter = 0;
			for (L2Playable playable : npc.getKnownList().getKnownTypeInRadius(L2Playable.class, npc.getAggroRange()))
			{
				if (!playable.isDead())
					playableCounter++;
			}
			
			// If no one is inside aggro range, drop the task.
			if (playableCounter == 0)
			{
				cancelQuestTimer("skill", npc, null);
				return null;
			}
			
			npc.setTarget(npc);
			npc.doCast((npc.getNpcId() == 18345) ? ANESTHESIA : POISON);
		}
		return null;
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (player == null)
			return null;
		
		// Instant use
		npc.setTarget(npc);
		npc.doCast((npc.getNpcId() == 18345) ? ANESTHESIA : POISON);
		
		// Launch a task every 15sec.
		if (getQuestTimer("skill", npc, null) == null)
			startQuestTimer("skill", 15000, npc, null, true);
		
		return super.onAggroRangeEnter(npc, player, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (getQuestTimer("skill", npc, null) != null)
			cancelQuestTimer("skill", npc, null);
		
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet)
	{
		if (player == null)
			return null;
		
		// Retrieve the attacker.
		final L2Character originalAttacker = (isPet ? player.getPet() : player);
		
		// Make all mobs found in a radius 2k aggressive towards attacker.
		for (L2Attackable obj : player.getKnownList().getKnownTypeInRadius(L2Attackable.class, 2000))
		{
			if (obj.isDead() || obj == npc)
				continue;
			
			obj.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, originalAttacker, 1);
		}
		
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc instanceof L2Attackable)
			((L2Attackable) npc).seeThroughSilentMove(true);
		
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new PrimevalIsle();
	}
}