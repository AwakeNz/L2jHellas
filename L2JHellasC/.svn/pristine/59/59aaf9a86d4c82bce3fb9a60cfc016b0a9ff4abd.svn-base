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
package com.l2jhellas.gameserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.L2Territory;
import com.l2jhellas.util.database.L2DatabaseFactory;

public class Territory
{
	private static Logger _log = Logger.getLogger(Territory.class.getName());
	private static final Territory _instance = new Territory();
	private static Map<Integer, L2Territory> _territory;

	public static Territory getInstance()
	{
		return _instance;
	}

	private Territory()
	{
		reload_data(); // load all data at server start
	}

	public int[] getRandomPoint(int terr)
	{
		return _territory.get(terr).getRandomPoint();
	}

	public int getProcMax(int terr)
	{
		return _territory.get(terr).getProcMax();
	}

	public void reload_data()
	{
		_territory = new HashMap<Integer, L2Territory>();

		Integer[][] point = get2DIntArray(new String[]
		{
		"loc_id", "loc_x", "loc_y", "loc_zmin", "loc_zmax", "proc"
		}, "locations", "loc_id > 0");
		for (Integer[] row : point)
		{
			// _log.info("row = "+row[0]);
			Integer terr = row[0];
			if (terr == null)
			{
				_log.warning(Territory.class.getName() + ": Null territory!");
				continue;
			}

			if (_territory.get(terr) == null)
			{
				L2Territory t = new L2Territory(terr);
				_territory.put(terr, t);
			}
			_territory.get(terr).add(row[1], row[2], row[3], row[4], row[5]);
		}
	}
	
	private static Integer[][] get2DIntArray(String[] resultFields, String usedTables, String whereClause)
	{
		long start = System.currentTimeMillis();
		String query = "";
		Integer res[][] = null;
		try
		{
			query = L2DatabaseFactory.getInstance().prepQuerySelect(resultFields, usedTables, whereClause);
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement ps = con.prepareStatement(query);
				ResultSet rs = ps.executeQuery();

				int rows = 0;
				while (rs.next())
				{
					rows++;
				}

				res = new Integer[rows - 1][resultFields.length];

				rs.first();

				int row = 0;
				while (rs.next())
				{
					for (int i = 0; i < resultFields.length; i++)
					{
						res[row][i] = rs.getInt(i + 1);
					}
					row++;
				}
				
				rs.close();
				ps.close();
			}
		}
		catch (Exception e)
		{
			_log.warning(Territory.class.getSimpleName() + ": Error in query '" + query + "':");
		}
		if (Config.DEBUG)
			_log.fine("Get all rows in query '" + query + "' in " + (System.currentTimeMillis() - start) + "ms");
		return res;
	}
}
