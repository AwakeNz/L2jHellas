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
package com.l2jhellas.gameserver.handlers.usercommandhandlers;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.controllers.GameTimeController;
import com.l2jhellas.gameserver.datatables.xml.MapRegionTable;
import com.l2jhellas.gameserver.handler.IUserCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jhellas.gameserver.network.serverpackets.SetupGauge;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.util.Broadcast;

public class Escape implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		52
	};

	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (activeChar.isCastingNow()|| activeChar.isSitting()  || activeChar.isMovementDisabled() || activeChar.isMuted() || activeChar.isAlikeDead() || activeChar.isInOlympiadMode())
			return false;

		int unstuckTimer = (activeChar.isGM() ? 5000 : Config.UNSTUCK_INTERVAL * 1000);

		// Check to see if the current player is in TvT , CTF or ViP events.
		if (activeChar.isInFunEvent())
		{
			activeChar.sendMessage("You may not escape from an Event.");
			return false;
		}

		// Check to see if the player is in a festival.
		if (activeChar.isFestivalParticipant())
		{
			activeChar.sendMessage("You may not use an escape command in a festival.");
			return false;
		}

		// Check to see if player is in jail
		if (activeChar.isInJail())
		{
			activeChar.sendMessage("You can not escape from jail.");
			return false;
		}

		if (activeChar.inClanEvent || activeChar.inPartyEvent || activeChar.inSoloEvent)
		{
			activeChar.sendPacket(SystemMessage.sendString("You can't escape while in Event."));
			return false;
		}
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2);
		sm.addString("After " + unstuckTimer / 60000 + " min. you be returned to near village.");

		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		// SoE Animation section
		activeChar.setTarget(activeChar);
		activeChar.disableAllSkills();


		MagicSkillUse msk = new MagicSkillUse(activeChar, 1050, 1, unstuckTimer, 0);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, msk, 810000/* 900 */);
		SetupGauge sg = new SetupGauge(0, unstuckTimer);
		activeChar.sendPacket(sg);
		// End SoE Animation section

		EscapeFinalizer ef = new EscapeFinalizer(activeChar);
		
		// continue execution later
		activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(ef, unstuckTimer));
		activeChar.setSkillCastEndTime(10 + GameTimeController.getInstance().getGameTicks() + unstuckTimer / GameTimeController.MILLIS_IN_TICK);
		return true;
	}

	static class EscapeFinalizer implements Runnable
	{
		private final L2PcInstance _activeChar;

		EscapeFinalizer(L2PcInstance activeChar)
		{
			_activeChar = activeChar;
		}

		@Override
		public void run()
		{
			if (_activeChar.isDead())
				return;

			_activeChar.setIsIn7sDungeon(false);

			_activeChar.enableAllSkills();

			try
			{
				_activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			}
			catch (Throwable e)
			{
				if (Config.DEBUG)
					e.printStackTrace();
			}
		}
	}

	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}