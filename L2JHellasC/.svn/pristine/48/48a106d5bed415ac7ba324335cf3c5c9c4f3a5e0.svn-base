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
package com.l2jhellas.gameserver.network.clientpackets;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SocialAction;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.util.Util;

public class RequestSocialAction extends L2GameClientPacket
{
	// private static Logger _log = Logger.getLogger(RequestSocialAction.class.getName());
	private static final String _C__1B_REQUESTSOCIALACTION = "[C] 1B RequestSocialAction";

	// format cd
	private int _actionId;

	@Override
	protected void readImpl()
	{
		_actionId = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		// You cannot do anything else while fishing
		if (activeChar.isFishing())
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}
		if(activeChar.isDead())
		{
			activeChar.sendMessage("You can't perform social actions while dead");
			return;
		}
		// check if its the actionId is allowed
		if (_actionId < 2 || _actionId > 13)
		{
			Util.handleIllegalPlayerAction(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " requested an internal Social Action.", Config.DEFAULT_PUNISH);
			return;
		}

		if ((activeChar.getPrivateStoreType() == 0) && (activeChar.getActiveRequester() == null) && !activeChar.isAlikeDead() && (!activeChar.isAllSkillsDisabled() || activeChar.isInDuel()) && activeChar.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
		{
			if (Config.DEBUG)
				_log.fine("Social Action:" + _actionId);

			SocialAction atk = new SocialAction(activeChar.getObjectId(), _actionId);
			activeChar.broadcastPacket(atk,2000);
			/*
			 * // Schedule a social task to wait for the animation to finish
			 * ThreadPoolManager.getInstance().scheduleGeneral(new SocialTask(this), 2600);
			 * activeChar.setIsParalyzed(true);
			 */
		}
	}

	/*
	 * class SocialTask implements Runnable
	 * {
	 * L2PcInstance _player;
	 * SocialTask(RequestSocialAction action)
	 * {
	 * _player = getClient().getActiveChar();
	 * }
	 * public void run()
	 * {
	 * _player.setIsParalyzed(false);
	 * }
	 * }
	 */

	@Override
	public String getType()
	{
		return _C__1B_REQUESTSOCIALACTION;
	}
}