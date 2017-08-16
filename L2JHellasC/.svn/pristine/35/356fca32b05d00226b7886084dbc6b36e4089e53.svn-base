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
package Extensions.RankSystem;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

/**
 * Class used in L2PcInstance. Contains some system variables used in game.
 * From Killer side.
 * 
 * @author Masterio
 */
public class RPSCookie
{
	private RPSHtmlDeathStatus _deathStatus = null;
	private RPSHtmlComboKill _comboKill = null;

	private L2PcInstance _target = null;

	public void runPvpTask(L2PcInstance player, L2Character target)
	{
		if (Config.RANK_PVP_SYSTEM_ENABLED)
		{
			if (player != null && target != null && target instanceof L2PcInstance)
			{
				// set Victim handler for Killer
				//setTarget((L2PcInstance)target);	// [not required]

				// set Killer handler for Victim
				((L2PcInstance) target).getRPSCookie().setTarget(player);

				ThreadPoolManager.getInstance().executeTask(new RankPvpSystemPvpTask(player, (L2PcInstance) target));
			}
		}
	}

	public class RankPvpSystemPvpTask implements Runnable
	{
		private L2PcInstance _killer = null;
		private L2PcInstance _victim = null;

		public RankPvpSystemPvpTask(L2PcInstance killer, L2PcInstance victim)
		{
			_killer = killer;
			_victim = victim;
		}

		@Override
		public void run()
		{
			RankPvpSystem rps = new RankPvpSystem(_killer, _victim);

			rps.doPvp();
		}
	}

	public RPSHtmlDeathStatus getDeathStatus()
	{
		return _deathStatus;
	}

	public boolean isDeathStatusActive()
	{
		if (_deathStatus == null)
			return false;

		return true;
	}

	public void setDeathStatus(RPSHtmlDeathStatus deathStatus)
	{
		_deathStatus = deathStatus;
	}

	public RPSHtmlComboKill getComboKill()
	{
		return _comboKill;
	}

	public boolean isComboKillActive()
	{
		if (_comboKill == null)
			return false;

		return true;
	}

	public void setComboKill(RPSHtmlComboKill comboKill)
	{
		_comboKill = comboKill;
	}

	/**
	 * The player's Target.
	 * 
	 * @return
	 */
	public L2PcInstance getTarget()
	{
		return _target;
	}

	/**
	 * The player's Target.
	 * 
	 * @param target
	 */
	public void setTarget(L2PcInstance target)
	{
		_target = target;
	}
}
