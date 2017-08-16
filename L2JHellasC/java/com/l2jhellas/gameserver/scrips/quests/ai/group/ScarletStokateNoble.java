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

import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.model.actor.L2Attackable;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.quest.QuestEventType;
import com.l2jhellas.gameserver.scrips.quests.ai.AbstractNpcAI;
import com.l2jhellas.util.Rnd;

public class ScarletStokateNoble extends AbstractNpcAI
{
	private static final int NPC[] = {21378,21652};
	private static final int[] mobs ={20965,20966,20967,20968,20969,20970,20971,20972,20973};
	public ScarletStokateNoble()
	{
		super("ScarletStokateNoble","ai");

		addEventId(NPC[0], QuestEventType.ON_KILL);
		
		for (int id : mobs)
			addAttackId(id);
	}

		@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet)
	{
			if (player == null)
				return null;
			
			int npcid = npc.getNpcId();
			
			for (int id : mobs)
			{
		       if(id==npcid)
		       {
			     npc.setIsRunning(true);
			     ((L2Attackable) npc).addDamageHate(player, 0, 999);
			     npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		       }
			}
			return super.onAttack(npc, player, damage, isPet);
			
	}		
		
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if(npc.getNpcId() == NPC[0])
		{
			if(Rnd.get(100) <= 20)
			{
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0,false);
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0,false);
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0,false);
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0,false);
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0,false);
			}
		}
		return super.onKill(npc,killer,isPet);
	}

    public static void main(String[] args)
    {
		new ScarletStokateNoble();
    }

}