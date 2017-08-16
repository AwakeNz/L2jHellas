/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jhellas.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.datatables.sql.NpcBufferSkillIdsTable;
import com.l2jhellas.gameserver.model.L2Effect;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jhellas.gameserver.skills.Formulas;
import com.l2jhellas.gameserver.skills.SkillTable;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;
import com.l2jhellas.util.Rnd;

/**
 * L2NpcBufferInstance
 * 
 * @author FBIAgent
 */
public class L2NpcBufferInstance extends L2Npc
{
	private static final Logger _log = Logger.getLogger(L2NpcBufferInstance.class.getName());
	private class BuffTask implements Runnable
	{
		private Boolean _buffing = false;
		private L2NpcBufferInstance _me = null;
		private final List<L2PcInstance> _playerInstances = new ArrayList<L2PcInstance>();
		private final List<Integer> _skillIds = new ArrayList<Integer>();
		private final List<Integer> _skillLevels = new ArrayList<Integer>();
		private ScheduledFuture<?> _task = null;

		public BuffTask(L2NpcBufferInstance me)
		{
			_me = me;
			_task = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this, 100, 100);
		}

		
		@SuppressWarnings("unused")
		public void addBuff(L2PcInstance playerInstance, int skillId, int skillLevel)
		{
			synchronized (_playerInstances)
			{
				_playerInstances.add(playerInstance);
				_skillIds.add(skillId);
				_skillLevels.add(skillLevel);
			}
		}

		@Override
		public void run()
		{
			boolean abort = false;

			synchronized (_buffing)
			{
				abort = _buffing;
				_buffing = true;
			}

			if (abort)
				return;

			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException ie)
			{
			}

			int index = -1;
			L2PcInstance playerInstance = null;
			int skillId = 0;
			int skillLevel = 0;

			synchronized (_playerInstances)
			{
				index = _skillIds.size() - 1;

				if (index != -1)
				{
					index = Rnd.get(_skillIds.size());
					playerInstance = _playerInstances.get(index);
					skillId = _skillIds.get(index);
					skillLevel = _skillLevels.get(index);
				}
			}

			if (index == -1)
				return;

			L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);

			if (playerInstance != null && skill != null)
			{
				// if (_me.isInsideRadius(playerInstance.getX(),
				// playerInstance.getY(), skill.getCastRange(), true))
				// continue;

				int skillTime = Formulas.getInstance().calcMAtkSpd(_me, skill, skill.getHitTime());

				if (skill.isDance())
					_me.broadcastPacket(new MagicSkillUse(_me, _me, skillId, skillLevel, skillTime, 0));
				else
					_me.broadcastPacket(new MagicSkillUse(_me, playerInstance, skillId, skillLevel, skillTime, 0));

				long continueTime = System.currentTimeMillis() + skillTime;

				while (continueTime >= System.currentTimeMillis())
				{
					try
					{
						Thread.sleep(1);
					}
					catch (InterruptedException ie)
					{
					}
				}

				L2Effect[] effects = playerInstance.getAllEffects();

				if (effects != null)
				{
					for (L2Effect e : effects)
					{
						if (e != null && skill != null)
						{
							if (e.getSkill().getId() == skill.getId())
								e.exit();
						}
					}
				}

				skill.getEffects(playerInstance, playerInstance);
			}
			else
				_log.warning(L2NpcBufferInstance.class.getName() + ": NpcBuffer warning(" + getNpcId() + " at " + getX() + ", " + getY() + ", " + getZ() + "): Skill or Player null!");

			synchronized (_playerInstances)
			{
				_playerInstances.remove(index);
				_skillIds.remove(index);
				_skillLevels.remove(index);
			}

			synchronized (_buffing)
			{
				_buffing = false;
			}
		}

		protected void stopTask()
		{
			if (_task != null)
			{
				_task.cancel(true);
				_task = null;
			}
		}
	}

	private BuffTask _buffTaskInstance = null;

	public L2NpcBufferInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		_buffTaskInstance = new BuffTask(this);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;

		return "data/html/mods/buffer/" + pom + ".htm";
	}

	@Override
	public void deleteMe()
	{
		if (_buffTaskInstance != null)
		{
			_buffTaskInstance.stopTask();
			_buffTaskInstance = null;
		}
		super.deleteMe();
	}

	@Override
	public void onBypassFeedback(L2PcInstance playerInstance, String command)
	{
		if (playerInstance == null)
			return;
		int npcId = getNpcId();
		int val = 0;
		if (command.startsWith("Chat"))
		{
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			catch (NumberFormatException nfe)
			{
			}
		}
		if (command.startsWith("npc_buffer_heal"))
		{
			if (playerInstance.getCurrentHp() == 0 || playerInstance.getPvpFlag() > 0)
			{
				playerInstance.sendMessage("You can't do that in combat!!!");
			}
			else
			{
				playerInstance.setCurrentCp(playerInstance.getMaxCp());
				playerInstance.setCurrentHp(playerInstance.getMaxHp());
				playerInstance.setCurrentMp(playerInstance.getMaxMp());
			}
		}
		if (command.startsWith("npc_buffer_cancel"))
		{
			if (playerInstance.getCurrentHp() == 0 || playerInstance.getPvpFlag() > 0)
			{
				playerInstance.sendMessage("You can't do that!!!");
			}
			else
			{
				removeAllBuffs(playerInstance);
			}
		}
		if (command.startsWith("npc_buffer_buff"))
		{
			String[] params = command.split(" ");
			int skillId = Integer.parseInt(params[1]);
			val = Integer.parseInt(params[2]);
			int[] skillInfos = NpcBufferSkillIdsTable.getInstance().getSkillInfo(npcId, skillId);

			if (skillInfos == null)
			{
				_log.warning(L2NpcBufferInstance.class.getName() + ": NpcBuffer warning(" + npcId + " at " + getX() + ", " + getY() + ", " + getZ() + "): Player " + playerInstance.getName() + " tried to use skill(" + skillId + ") not assigned to npc buffer!");
				return;
			}

			int skillLevel = skillInfos[0];
			int skillFeeId = skillInfos[1];
			int skillFeeAmount = skillInfos[2];

			if (skillFeeId != 0) // take some item?
			{
				if (skillFeeAmount == 0)
				{
					_log.warning(L2NpcBufferInstance.class.getName() + ": NpcBuffer warning(" + npcId + " at " + getX() + ", " + getY() + ", " + getZ() + "): Fee amount of skill(" + skillId + ") fee id(" + skillFeeId + ") is 0!");
					return;
				}

				L2ItemInstance itemInstance = playerInstance.getInventory().getItemByItemId(skillFeeId);

				if (itemInstance == null || (!itemInstance.isStackable() && playerInstance.getInventory().getInventoryItemCount(skillFeeId, -1) < skillFeeAmount))
				{
					playerInstance.sendMessage("You do not have enough items!");
					return;
				}

				if (itemInstance.isStackable())
				{
					if (!playerInstance.destroyItemByItemId("Npc Buffer", skillFeeId, skillFeeAmount, playerInstance.getTarget(), true))
					{
						playerInstance.sendMessage("You do not have enough items!");
						return;
					}
				}
				else
				{
					for (int i = 0; i < skillFeeAmount; i++)
						playerInstance.destroyItemByItemId("Npc Buffer", skillFeeId, 1, playerInstance.getTarget(), true);
				}
			}
			final L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
			skill.getEffects(playerInstance, playerInstance);
		}
		showChatWindow(playerInstance, val);
	}

	private void removeAllBuffs(L2PcInstance player)
	{
		if (player != null)
		{
			player.stopAllEffects();
			player.sendMessage("Your buffs has been removed.");
		}
	}
}