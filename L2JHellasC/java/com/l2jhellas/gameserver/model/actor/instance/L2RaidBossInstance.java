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
package com.l2jhellas.gameserver.model.actor.instance;

import com.l2jhellas.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.entity.Hero;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;

/**
 * This class manages all RaidBoss.
 * In a group mob, there are one master called RaidBoss and several slaves called Minions.
 */
public final class L2RaidBossInstance extends L2MonsterInstance
{
	private RaidBossSpawnManager.StatusEnum _raidStatus;

	/**
	 * Constructor of L2RaidBossInstance (use L2Character and L2NpcInstance constructor).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Call the L2Character constructor to set the _template of the L2RaidBossInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2RaidBossInstance</li> <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li><BR>
	 * <BR>
	 * 
	 * @param objectId
	 *        Identifier of the object to initialized
	 * @param L2NpcTemplate
	 *        Template to apply to the NPC
	 */
	public L2RaidBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		setIsRaid(true);
		super.onSpawn();
	}


	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		if (killer instanceof L2Playable)
		{
			if (killer.isInParty())
			{
				for (L2PcInstance member : killer.getParty().getPartyMembers())
				{
					Hero.getInstance().setRBkilled(member.getObjectId(), getNpcId());
				}
			}
			else
			{
				Hero.getInstance().setRBkilled(killer.getObjectId(), getNpcId());
			}
			SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL);
			broadcastPacket(msg);
		}

		RaidBossSpawnManager.getInstance().updateStatus(this, true);
		return true;
	}

	public void setRaidStatus(RaidBossSpawnManager.StatusEnum status)
	{
		_raidStatus = status;
	}

	public RaidBossSpawnManager.StatusEnum getRaidStatus()
	{
		return _raidStatus;
	}

	/**
	 * Reduce the current HP of the L2Attackable, update its _aggroList and launch the doDie Task if necessary.<BR>
	 * <BR>
	 */
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
	{
		super.reduceCurrentHp(damage, attacker, awake);
	}

	public void healFull()
	{
		super.setCurrentHp(super.getMaxHp());
		super.setCurrentMp(super.getMaxMp());
	}
}