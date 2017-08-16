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
package com.l2jhellas.gameserver.idfactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.util.database.L2DatabaseFactory;

public class StackIDFactory extends IdFactory
{
	private static Logger _log = Logger.getLogger(StackIDFactory.class.getName());

	private int _curOID;
	private int _tempOID;

	private final Stack<Integer> _freeOIDStack = new Stack<Integer>();

	protected StackIDFactory()
	{
		super();
		_curOID = FIRST_OID;
		_tempOID = FIRST_OID;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			// con.createStatement().execute("drop table if exists tmp_obj_id");

			int[] tmp_obj_ids = extractUsedObjectIDTable();
			if (tmp_obj_ids.length > 0)
			{
				_curOID = tmp_obj_ids[tmp_obj_ids.length - 1];
			}
			_log.info(StackIDFactory.class.getSimpleName() + ": Max Id = " + _curOID);

			int N = tmp_obj_ids.length;
			for (int idx = 0; idx < N; idx++)
			{
				N = insertUntil(tmp_obj_ids, idx, N, con);
			}

			_curOID++;
			_log.info(StackIDFactory.class.getSimpleName() + ": Next usable Object ID is: " + _curOID);
			_initialized = true;
		}
		catch (SQLException e)
		{
			_log.warning(StackIDFactory.class.getName() + ": ID Factory could not be initialized correctly:");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	private int insertUntil(int[] tmp_obj_ids, int idx, int N, java.sql.Connection con) throws SQLException
	{
		int id = tmp_obj_ids[idx];
		if (id == _tempOID)
		{
			_tempOID++;
			return N;
		}
		// check these IDs not present in DB
		if (Config.BAD_ID_CHECKING)
		{
			for (String check : ID_CHECKS)
			{
				PreparedStatement ps = con.prepareStatement(check);
				ps.setInt(1, _tempOID);
				// ps.setInt(1, _curOID);
				ps.setInt(2, id);
				ResultSet rs = ps.executeQuery();
				while (rs.next())
				{
					int badId = rs.getInt(1);
					_log.warning(StackIDFactory.class.getName() + ": Bad ID " + badId + " in DB found by: " + check);
					throw new RuntimeException();
				}
				rs.close();
				ps.close();
			}
		}

		// int hole = id - _curOID;
		int hole = id - _tempOID;
		if (hole > N - idx)
			hole = N - idx;
		for (int i = 1; i <= hole; i++)
		{
			if (Config.DEBUG)
				_log.config(StackIDFactory.class.getName() + "Free ID added " + (_tempOID));
			_freeOIDStack.push(_tempOID);
			_tempOID++;
			// _curOID++;
		}
		if (hole < N - idx)
			_tempOID++;
		return N - hole;
	}

	public static IdFactory getInstance()
	{
		return _instance;
	}

	@Override
	public synchronized int getNextId()
	{
		int id;
		if (!_freeOIDStack.empty())
			id = _freeOIDStack.pop();
		else
		{
			id = _curOID;
			_curOID = _curOID + 1;
		}
		return id;
	}

	/**
	 * return a used Object ID back to the pool
	 * 
	 * @param object
	 *        ID
	 */
	@Override
	public synchronized void releaseId(int id)
	{
		_freeOIDStack.push(id);
	}

	@Override
	public int size()
	{
		return FREE_OBJECT_ID_SIZE - _curOID + FIRST_OID + _freeOIDStack.size();
	}
}