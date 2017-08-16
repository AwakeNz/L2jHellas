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
package com.l2jhellas.gameserver.network.gameserverpackets;

import java.io.IOException;

/**
 * @formatter:off<BR>
 *                    Format: cccSddb<BR>
 *                    c desired ID<BR>
 *                    c accept alternative ID<BR>
 *                    c reserve Host<BR>
 *                    s ExternalHostName<BR>
 *                    s InetranlHostName<BR>
 *                    d max players<BR>
 *                    d hexid size<BR>
 *                    b hexid<BR>
 * @param id
 * <BR>
 * @param acceptAlternate
 * <BR>
 * @param hexid
 * <BR>
 * @param externalHost
 * <BR>
 * @param internalHost
 * <BR>
 * @param reserveHost
 * <BR>
 * @param maxplayer
 * @formatter:on
 */
public class AuthRequest extends GameServerBasePacket
{
	public AuthRequest(int id, boolean acceptAlternate, byte[] hexid, String externalHost, String internalHost, int port, boolean reserveHost, int maxplayer)
	{
		writeC(0x01);
		writeC(id);
		writeC(acceptAlternate ? 0x01 : 0x00);
		writeC(reserveHost ? 0x01 : 0x00);
		writeS(externalHost);
		writeS(internalHost);
		writeH(port);
		writeD(maxplayer);
		writeD(hexid.length);
		writeB(hexid);
	}

	@Override
	public byte[] getContent() throws IOException
	{
		return getBytes();
	}
}