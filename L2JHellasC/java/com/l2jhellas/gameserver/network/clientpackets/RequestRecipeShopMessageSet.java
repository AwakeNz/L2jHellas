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
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.util.Util;

public class RequestRecipeShopMessageSet extends L2GameClientPacket
{
	private static final String _C__B1_RequestRecipeShopMessageSet = "[C] b1 RequestRecipeShopMessageSet";

	private String _name;

	@Override
	protected void readImpl()
	{
		_name = readS();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		
		if (player == null)
			return;

		if (_name != null && _name.length() > 29)
		{
			Util.handleIllegalPlayerAction(player, player.getName() + " tried to overflow recipe shop message", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (player.getCreateList() != null)
		{
			player.getCreateList().setStoreName(_name);
		}
	}

	@Override
	public String getType()
	{
		return _C__B1_RequestRecipeShopMessageSet;
	}
}