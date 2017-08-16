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
package com.l2jhellas.gameserver.handlers.admincommandhandlers;

import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.logs.GMAudit;

/**
 * This class handles following admin commands:
 * - heal = restores HP/MP/CP on target, name or radius
 *
 * @author Nightwolf
 */
public class AdminHeal implements IAdminCommandHandler
{
	protected static final Logger _log = Logger.getLogger(AdminHeal.class.getName());

	private static final String[] ADMIN_COMMANDS =
	{
		"admin_heal"
	};

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");

		if (command.equals("admin_heal"))
		{
			handleRes(activeChar);
		}
		else if (command.startsWith("admin_heal"))
		{
			try
			{
				String healTarget = command.substring(11);
				handleRes(activeChar, healTarget);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				_log.warning(AdminHeal.class.getName() + ": Heal error: " + e);
				if (Config.DEVELOPER)
					e.printStackTrace();
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
				sm.addString("Incorrect target/radius specified.");
				activeChar.sendPacket(sm);
			}
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private void handleRes(L2PcInstance activeChar)
	{
		handleRes(activeChar, null);
	}

	private void handleRes(L2PcInstance activeChar, String player)
	{
		L2Object obj = activeChar.getTarget();
		if (player != null)
		{
			L2PcInstance plyr = L2World.getInstance().getPlayer(player);

			if (plyr != null)
			{
				obj = plyr;
			}
			else
			{
				try
				{
					int radius = Integer.parseInt(player);
					for (L2Object object : activeChar.getKnownList().getKnownObjects().values())
					{
						if (object instanceof L2Character)
						{
							L2Character character = (L2Character) object;
							character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
							if (object instanceof L2PcInstance)
							{
								character.setCurrentCp(character.getMaxCp());
							}
						}
					}
					activeChar.sendMessage("Healed within " + radius + " unit radius.");
					return;
				}
				catch (NumberFormatException nbe)
				{
				}
			}
		}
		if (obj == null)
		{
			obj = activeChar;
			activeChar.sendMessage("No target heal self casted.");
		}
		if ((obj != null) && (obj instanceof L2Character))
		{
			L2Character target = (L2Character) obj;
			if (target instanceof L2PcInstance && !target.isDead())
			{
				target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
				target.setCurrentCp(target.getMaxCp());
				_log.warning(AdminHeal.class.getSimpleName() + ": GM " + activeChar.getName() + "(" + activeChar.getObjectId() + ") healed character " + target.getName() + "(" + target.getObjectId() + ")");
			}
			else if (target instanceof L2MonsterInstance)
			{
				if (target.isChampion() && Config.CHAMPION_ENABLE)
				{
					_log.warning(AdminHeal.class.getSimpleName() + ": GM " + activeChar.getName() + "(" + activeChar.getObjectId() + ")" + " healed champion " + target.getName() + "(" + target.getObjectId() + ")");
				}
				if (target.isBoss())
				{
					_log.warning(AdminHeal.class.getSimpleName() + ": GM " + activeChar.getName() + "(" + activeChar.getObjectId() + ")" + " healed grand boss " + target.getName() + "(" + target.getObjectId() + ")");
				}
				if (target.isRaidMinion())
				{
					_log.warning(AdminHeal.class.getSimpleName() + ": GM " + activeChar.getName() + "(" + activeChar.getObjectId() + ")" + " healed raid minion " + target.getName() + "(" + target.getObjectId() + ")");
				}
				else if (target.isRaid())
				{
					_log.warning(AdminHeal.class.getSimpleName() + ": GM " + activeChar.getName() + "(" + activeChar.getObjectId() + ")" + " healed raid " + target.getName() + "(" + target.getObjectId() + ")");
				}
				else if (((L2MonsterInstance) target).isMob())
				{
					_log.warning(AdminHeal.class.getSimpleName() + ": GM " + activeChar.getName() + "(" + activeChar.getObjectId() + ")" + " healed monster " + target.getName() + "(" + target.getObjectId() + ")");
				}
				else
				{
					_log.warning(AdminHeal.class.getSimpleName() + ": GM " + activeChar.getName() + "(" + activeChar.getObjectId() + ")" + " healed etc " + target.getName() + "(" + target.getObjectId() + ")");
				}
			}
		}
	}
}