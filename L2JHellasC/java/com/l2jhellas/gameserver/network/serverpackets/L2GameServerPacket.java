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
package com.l2jhellas.gameserver.network.serverpackets;

import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.network.L2GameClient;
import com.l2jhellas.mmocore.network.SendablePacket;

/**
 * kenrix
 */
public abstract class L2GameServerPacket extends SendablePacket<L2GameClient>
{
	protected static final Logger _log = Logger.getLogger(L2GameServerPacket.class.getName());

	@Override
	protected void write()
	{
		if (Config.DEBUG)
			_log.info(getType());
		try
		{
			writeImpl();
		}
		catch (Throwable t)
		{
			_log.severe("Client: " + getClient().toString() + " - Failed writing: " + getType());
			t.printStackTrace();
		}
	}

	public void runImpl()
	{

	}

	protected abstract void writeImpl();

	/**
	 * @return A String with this packet name for debugging purposes
	 */
	public String getType()
	{
		return "[S] " + getClass().getSimpleName();
	}
}