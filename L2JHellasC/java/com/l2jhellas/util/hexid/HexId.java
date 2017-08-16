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
package com.l2jhellas.util.hexid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.PackRoot;
import com.l2jhellas.Config;
import com.l2jhellas.util.database.L2DatabaseFactory;

public class HexId
{
	private static final String HEXID_FILE = PackRoot.DATAPACK_ROOT + "/config/Network/HexId/hexid.txt";
	private static final String INSERT_GS = "INSERT INTO gameservers (server_id,hexid,host) VALUES (?,?,?)";
	private static final String CLEAR_GS = "DELETE FROM gameservers WHERE server_id=1";
	
	public static void load()
	{
		try
		{
			File hexid = new File(PackRoot.DATAPACK_ROOT + "/config/Network/HexId");
			hexid.mkdir();
			File file = new File(HEXID_FILE);
			boolean exist = file.createNewFile();
			file.createNewFile();
			if (!exist)
				return;
			FileWriter fstream = new FileWriter(HEXID_FILE);
			BufferedWriter out = new BufferedWriter(fstream);
			/** @formatter:off */
			out.write(
				"# The hexID to auth into login\r\n" +
				"# Automaticly created because you forgot it..\r\n" +
				"HexID=1\r\n"+
				"ServerID=1");
			/** @formatter:on */
			out.close();
		}
		catch (Exception e)
		{
			System.err.println("HexID: could not create " + HEXID_FILE);
		}
	}
	
	public static void storeDB()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(CLEAR_GS);
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			System.err.println("SQL error while deleting gameserver: ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(INSERT_GS);
			statement.setString(1, "1");
			statement.setString(2, "1");
			statement.setString(3, "127.0.0.1");
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			System.err.println("SQL error while saving gameserver: ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}
}