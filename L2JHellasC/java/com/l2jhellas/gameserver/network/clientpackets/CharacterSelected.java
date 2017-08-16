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

import com.l2jhellas.gameserver.datatables.sql.CharNameTable;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.L2GameClient;
import com.l2jhellas.gameserver.network.L2GameClient.GameClientState;
import com.l2jhellas.gameserver.network.serverpackets.CharSelected;
import com.l2jhellas.gameserver.network.serverpackets.SkyInfo;

public class CharacterSelected extends L2GameClientPacket
{
	private static final String _C__0D_CHARACTERSELECTED = "[C] 0D CharacterSelected";

	// cd
	private int _charSlot;

	@SuppressWarnings("unused")
	private int _unk1; 	// new in C4
	@SuppressWarnings("unused")
	private int _unk2;	// new in C4
	@SuppressWarnings("unused")
	private int _unk3;	// new in C4
	@SuppressWarnings("unused")
	private int _unk4;	// new in C4

	@Override
	protected void readImpl()
	{
		_charSlot = readD();
		_unk1 = readH();
		_unk2 = readD();
		_unk3 = readD();
		_unk4 = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2GameClient client = getClient();
		
		if (client.getActiveCharLock().tryLock())
		{
			try
			{
				if (client.getActiveChar() == null)
				{
					final L2PcInstance cha = client.loadCharFromDisk(_charSlot);
					
					if (cha == null)
						return;
					
					if (cha.getAccessLevel().getLevel() < 0)
					{
						cha.closeNetConnection(true);
						return;
					}
					
					cha.setClient(client);
					client.setActiveChar(cha);
					cha.setOnlineStatus(true);
					
					sendPacket(SkyInfo.sendSky());
					
					client.setState(GameClientState.IN_GAME);
					
					sendPacket(new CharSelected(cha, client.getSessionId().playOkID1));
					
					CharNameTable.getInstance().addName(cha);
				}
			}
			finally
			{
				client.getActiveCharLock().unlock();
			}
		}
	}

	@Override
	public String getType()
	{
		return _C__0D_CHARACTERSELECTED;
	}
}