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
import com.l2jhellas.gameserver.model.actor.instance.L2EventManagerInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;

/**
 * @author Dezmond_snz
 * @ Rework AbsolutePower
 */
public final class DlgAnswer extends L2GameClientPacket
{
	private static final String _C__C5_DLGANSWER = "[C] C5 DlgAnswer";
	
	private int _messageId;
	private int _answer;
	private int _reqId;

	@Override
	protected void readImpl()
	{
		_messageId = readD();
		_answer = readD();
		_reqId = readD();
	}

	@Override
	public void runImpl()
	{
	  final L2PcInstance player = getClient().getActiveChar();
		
	if(player!=null)
	{	
		switch (_messageId)
		{
			//RESSURECTION
			case 1510:
			case 332:
				player.reviveAnswer(_answer);
				break;	
			//Summon tp request
			case 1842:
				player.teleportAnswer(_answer, _reqId);
				break;
			//OPEN_GATE
			case 1140:
				player.gatesAnswer(_answer, 1);
				break;
			//CLOSE_GATE
			case 1141:
				player.gatesAnswer(_answer, 0);
				break;	
			//WEDDING 
			case 1983:
			case 614:
				if (player.awaitingAnswer && Config.MOD_ALLOW_WEDDING)
				{
					player.EngageAnswer(_answer);
					player.awaitingAnswer = false;
				}
				if (L2EventManagerInstance._awaitingplayers.contains(player))
				{
					player.setRaidAnswear(_answer);
					L2EventManagerInstance._awaitingplayers.remove(player);
				}
				break;	
			default:
				player.sendPacket(ActionFailed.STATIC_PACKET);
				break;
		}
	  }
	}

	@Override
	public String getType()
	{
		return _C__C5_DLGANSWER;
	}
}