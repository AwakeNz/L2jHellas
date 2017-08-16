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

import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.CreatureSay;
import com.l2jhellas.gameserver.scrips.quests.ai.AbstractNpcAI;
import com.l2jhellas.util.Rnd;

public class DeluLizardmanSpecialCommander extends AbstractNpcAI
{
	private static final int LIZARDMAN = 21107;

	private static boolean _FirstAttacked;

	public DeluLizardmanSpecialCommander()
	{
		super("DeluLizardmanSpecialCommander", "ai");
		int[] mobs = {LIZARDMAN};
		registerMobs(mobs);
		_FirstAttacked = false;
	}

	@Override
	public String onAttack (L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
        if (npc.getNpcId() == LIZARDMAN)
        {
            if (_FirstAttacked)
            {
               if (Rnd.get(100) == 40)
            	   attacker.sendPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Come on, Ill take you on!"));
            }
            else
            {
               _FirstAttacked = true;
               attacker.sendPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "How dare you interrupt a sacred duel! You must be taught a lesson!"));
		}
        }
        return super.onAttack(npc, attacker, damage, isPet);
    }

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
        int npcId = npc.getNpcId();
        if (npcId == LIZARDMAN)
        {
            _FirstAttacked = false;
        }
        return super.onKill(npc,killer,isPet);
    }

	public static void main(String[] args)
	{
		new DeluLizardmanSpecialCommander();
	}
}