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

import com.l2jhellas.gameserver.instancemanager.RaidBossPointsManager;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Summon;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;
import com.l2jhellas.util.Rnd;

/**
 * This class manages all Grand Bosses.
 */
public final class L2EventBossInstance extends L2MonsterInstance
{
	/**
	 * Constructor for L2GrandBossInstance. This represent all Grand Boss.
	 * 
	 * @param objectId
	 *        ID of the instance
	 * @param template
	 *        L2NpcTemplate of the instance
	 */
	public L2EventBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onSpawn()
	{
		if (getNpcId() == 29020 || getNpcId() == 29028) // Baium and Valakas are all the time in passive mode, theirs attack AI handled in AI scripts
			super.disableCoreAI(true);
		super.onSpawn();
	}

	@Override
	public boolean isRaid()
	{
		return true;
	}

	/**
	 * Reduce the current HP of the L2Attackable, update its _aggroList and launch the doDie Task if necessary.<BR>
	 * <BR>
	 */
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
	{
		if (this.IsInSocialAction() || this.isInvul())
			return;

		super.reduceCurrentHp(damage, attacker, awake);
	}

	protected boolean _isInSocialAction = false;

	public boolean IsInSocialAction()
	{
		return _isInSocialAction;
	}

	public void setIsInSocialAction(boolean value)
	{
		_isInSocialAction = value;
	}

	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		L2PcInstance player = null;

		if (killer instanceof L2PcInstance)
			player = (L2PcInstance) killer;
		else if (killer instanceof L2Summon)
			player = ((L2Summon) killer).getOwner();

		if (player != null)
		{
			broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
			if (player.getParty() != null)
			{
				for (L2PcInstance member : player.getParty().getPartyMembers())
				{
					RaidBossPointsManager.getInstance().addPoints(member, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
				}
			}
			else
				RaidBossPointsManager.getInstance().addPoints(player, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
		}
		return true;
	}

	@Override
	public void doAttack(L2Character target)
	{
		if (_isInSocialAction)
			return;
		else
			super.doAttack(target);
	}

	@Override
	public void doCast(L2Skill skill)
	{
		if (_isInSocialAction)
			return;
		else
			super.doCast(skill);
	}

	public boolean getTeleported()
	{
		return false;
	}

	/**
	 * Check if the server allows Random Animation.<BR>
	 * <BR>
	 */
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}