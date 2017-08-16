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

import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.cache.CrestCache;
import com.l2jhellas.gameserver.cache.CrestCache.CrestType;
import com.l2jhellas.gameserver.network.serverpackets.AllyCrest;

public final class RequestAllyCrest extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestAllyCrest.class.getName());
	private static final String _C__88_REQUESTALLYCREST = "[C] 88 RequestAllyCrest";

	private int _crestId;

	/**
	 * packet type id 0x88 format: cd
	 * 
	 * @param rawPacket
	 */
	@Override
	protected void readImpl()
	{
		_crestId = readD();
	}

	@Override
	protected void runImpl()
	{
		if (Config.DEBUG)
			_log.fine("allycrestid " + _crestId + " requested");

		byte[] data = CrestCache.getCrest(CrestType.ALLY, _crestId);

		if (data != null)
		{
			AllyCrest ac = new AllyCrest(_crestId, data);
			sendPacket(ac);
		}
		else
		{
			if (Config.DEBUG)
				_log.fine("allycrest is missing:" + _crestId);
		}
	}

	@Override
	public String getType()
	{
		return _C__88_REQUESTALLYCREST;
	}
}