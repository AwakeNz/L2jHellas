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
package com.l2jhellas.gameserver.handlers.skillhandlers;

import java.util.List;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.handler.ISkillHandler;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.L2SkillType;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jhellas.gameserver.model.zone.ZoneId;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.util.Util;

public class SummonFriend implements ISkillHandler
{
	protected static final Logger _log = Logger.getLogger(SummonFriend.class.getName());

	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.SUMMON_FRIEND
	};

	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return; // currently not implemented for others
		L2PcInstance activePlayer = (L2PcInstance) activeChar;

		if (activePlayer.isInOlympiadMode())
		{
			activePlayer.sendPacket(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}

		// Checks summoner not in arenas, siege zones, jail
		if (activePlayer.isInsideZone(ZoneId.PVP))
		{
			activePlayer.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT);
			return;
		}

		// check for summoner not in raid areas
		List<L2Object> objects = L2World.getVisibleObjects(activeChar, 5000);

		if (objects != null)
		{
			for (L2Object object : objects)
			{
				if (object instanceof L2RaidBossInstance)
				{
					activePlayer.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
					return;
				}
			}
		}

		try
		{
			for (int index = 0; index < targets.length; index++)
			{
				if (!(targets[index] instanceof L2Character))
					continue;

				L2Character target = (L2Character) targets[index];

				if (activeChar == target)
					continue;

				if (target instanceof L2PcInstance)
				{
					L2PcInstance targetChar = (L2PcInstance) target;

					// CHECK TARGET CONDITIONS

					// This message naturally doesn't bring up a box...
					// $s1 wishes to summon you from $s2. Do you accept?
					// SystemMessage sm2 = new
					// SystemMessage(SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT);
					// sm2.addString(activeChar.getName());
					// String nearestTown =
					// MapRegionTable.getInstance().getClosestTownName(activeChar);
					// sm2.addString(nearestTown);
					// targetChar.sendPacket(sm2);

					// is in same party (not necessary any more)
					// if (!(targetChar.getParty() != null &&
					// targetChar.getParty().getPartyMembers().contains(activeChar)))
					// continue;

					if (targetChar.isAlikeDead())
					{
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED);
						sm.addString(targetChar.getName());
						activeChar.sendPacket(sm);
						continue;
					}

					if (targetChar.isInStoreMode())
					{
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CURRENTLY_TRADING_OR_OPERATING_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED);
						sm.addString(targetChar.getName());
						activeChar.sendPacket(sm);
						continue;
					}

					// Target cannot be in combat (or dead, but that's checked
					// by TARGET_PARTY)
					if (targetChar.isRooted() || targetChar.isInCombat())
					{
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED);
						sm.addString(targetChar.getName());
						activeChar.sendPacket(sm);
						continue;
					}

					// Check for the the target's festival status
					if (targetChar.isInOlympiadMode())
					{
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_IN_OLYMPIAD));
						continue;
					}

					// Check for the the target's festival status
					if (targetChar.isFestivalParticipant())
					{
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
						continue;
					}

					// Check for the target's jail status, arenas and siege
					// zones
					if (targetChar.isInsideZone(ZoneId.PVP))
					{
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
						continue;
					}

					// Requires a Summoning Crystal
					if (targetChar.getInventory().getItemByItemId(8615) == null)
					{
						activePlayer.sendMessage("Your target cannot be summoned while he hasn't got a Summoning Crystal");
						targetChar.sendMessage("You cannot be summoned while you haven't got a Summoning Crystal");
						continue;
					}

					if (!Util.checkIfInRange(0, activeChar, target, false))
					{
						targetChar.getInventory().destroyItemByItemId("Consume", 8615, 1, targetChar, activeChar);
						targetChar.sendPacket(SystemMessage.sendString("You are summoned to a party member."));

						targetChar.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ(), true);
					}
				}
			}
		}
		catch (Throwable e)
		{
			_log.warning(SummonFriend.class.getName() + ": error ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}