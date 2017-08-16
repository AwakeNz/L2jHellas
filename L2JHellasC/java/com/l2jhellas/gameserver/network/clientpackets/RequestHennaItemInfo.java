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

import com.l2jhellas.gameserver.datatables.xml.HennaData;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.HennaItemInfo;
import com.l2jhellas.gameserver.templates.L2Henna;

public final class RequestHennaItemInfo extends L2GameClientPacket
{
	private static final String _C__BB_RequestHennaItemInfo = "[C] bb RequestHennaItemInfo";
	private int _symbolId;

	// format cd

	@Override
	protected void readImpl()
	{
		_symbolId = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final L2Henna template = HennaData.getInstance().getTemplate(_symbolId);
		if (template == null)
			return;
		
		activeChar.sendPacket(new HennaItemInfo(template, activeChar));
	}

	@Override
	public String getType()
	{
		return _C__BB_RequestHennaItemInfo;
	}
}