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

import com.l2jhellas.gameserver.cache.CrestCache;
import com.l2jhellas.gameserver.cache.CrestCache.CrestType;
import com.l2jhellas.gameserver.network.serverpackets.ExPledgeCrestLarge;

/**
 * Fomat : chd<BR>
 * c: (id) 0xD0<BR>
 * h: (subid) 0x10<BR>
 * d: the crest id<BR>
 * This is a trigger
 * 
 * @author -Wooden-
 */
public final class RequestExPledgeCrestLarge extends L2GameClientPacket
{
	private static final String _C__D0_10_REQUESTEXPLEDGECRESTLARGE = "[C] D0:10 RequestExPledgeCrestLarge";
	private int _crestId;

	@Override
	protected void readImpl()
	{
		_crestId = readD();
	}

	@Override
	protected void runImpl()
	{
		byte[] data = CrestCache.getCrest(CrestType.PLEDGE_LARGE, _crestId);
		if (data != null)
			sendPacket(new ExPledgeCrestLarge(_crestId, data));
	}

	@Override
	public String getType()
	{
		return _C__D0_10_REQUESTEXPLEDGECRESTLARGE;
	}
}