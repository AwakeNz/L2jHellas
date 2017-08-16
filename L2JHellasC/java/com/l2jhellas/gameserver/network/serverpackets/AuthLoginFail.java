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

public class AuthLoginFail extends L2GameServerPacket
{
	private static final String _S__12_AUTHLOGINFAIL = "[S] 14 AuthLoginFail";
	
	public enum FailReason
	{
		NO_TEXT,
		SYSTEM_ERROR_LOGIN_LATER,
		PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT,
		PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT2,
		ACCESS_FAILED_TRY_LATER,
		INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT,
		ACCESS_FAILED_TRY_LATER2,
		ACOUNT_ALREADY_IN_USE,
		ACCESS_FAILED_TRY_LATER3,
		ACCESS_FAILED_TRY_LATER4,
		ACCESS_FAILED_TRY_LATER5
	}
	
	private final FailReason _reason;
	
	public AuthLoginFail(FailReason reason)
	{
		_reason = reason;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x14);
		writeD(_reason.ordinal());
	}

	@Override
	public String getType()
	{
		return _S__12_AUTHLOGINFAIL;
	}
}