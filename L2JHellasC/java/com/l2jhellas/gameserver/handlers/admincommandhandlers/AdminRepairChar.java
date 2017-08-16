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
package com.l2jhellas.gameserver.handlers.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.handler.IAdminCommandHandler;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.logs.GMAudit;
import com.l2jhellas.util.database.L2DatabaseFactory;

/**
 * This class handles following admin commands: - delete = deletes target
 */
public class AdminRepairChar implements IAdminCommandHandler
{
	private static Logger _log = Logger.getLogger(AdminRepairChar.class.getName());

	private static final String[] ADMIN_COMMANDS =
	{/** @formatter:off */
		"admin_repair"
	};/** @formatter:on */

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_repair"))
		{
			String target = (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target");
			GMAudit.auditGMAction(activeChar.getName(), command, target, "");
			handleRepair(command);
			return true;
		}
		return false;
	}

	private void handleRepair(String command)
	{
		String[] parts = command.split(" ");
		if (parts.length != 2)
		{
			return;
		}

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			int objId = 0;
			try (PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE char_name=?"))
			{
				statement.setString(1, parts[1]);
				statement.execute();
			}
			try (PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?"))
			{
				statement.setString(1, parts[1]);
				try (ResultSet rset = statement.executeQuery())
				{
					if (rset.next())
						objId = rset.getInt(1);
				}
			}

			if (objId == 0)
				return;

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?"))
			{
				statement.setInt(1, objId);
				statement.execute();
			}
			try (PreparedStatement statement = con.prepareStatement("UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=?"))
			{
				statement.setInt(1, objId);
				statement.execute();
			}
		}
		catch (SQLException e)
		{
			_log.warning(AdminRepairChar.class.getName() + ": could not repair char:");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}