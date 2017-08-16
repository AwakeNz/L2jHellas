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
package com.l2jhellas.util.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.jolbox.bonecp.BoneCPDataSource;
import com.l2jhellas.Config;

public class L2DatabaseFactory
{
	private static final Logger _log = Logger.getLogger(L2DatabaseFactory.class.getName());

	private BoneCPDataSource _source;
	private int database_partition_count = 3;
	private int database_timeout = 10;

	public L2DatabaseFactory()
	{
		try
		{
			if (Config.DATABASE_MAX_CONNECTIONS < 10)
			{
				Config.DATABASE_MAX_CONNECTIONS = 10;
			}

			if (database_partition_count > 4)
			{
				database_partition_count = 4;
			}

			if (Config.DATABASE_MAX_CONNECTIONS * database_partition_count > 200)
			{
				Config.DATABASE_MAX_CONNECTIONS = 50;
				database_partition_count = 4;
			}
			
			_source = new BoneCPDataSource();
			
			_source.getConfig().setDefaultAutoCommit(true);
			_source.getConfig().setPoolAvailabilityThreshold(10);
			_source.getConfig().setMinConnectionsPerPartition(10);
			_source.getConfig().setMaxConnectionsPerPartition(Config.DATABASE_MAX_CONNECTIONS);
			_source.getConfig().setPartitionCount(database_partition_count);

			_source.setAcquireRetryAttempts(0);
			_source.setAcquireRetryDelayInMs(500);
			_source.setAcquireIncrement(5);
			if (Config.DEVELOPER)
				_source.setCloseConnectionWatch(true); // for debugging unclosed connections
			_source.setConnectionTimeoutInMs(database_timeout);

			_source.setIdleConnectionTestPeriodInMinutes(1);

			_source.setIdleMaxAgeInSeconds(1800);

			_source.setTransactionRecoveryEnabled(true);
			_source.setDriverClass(Config.DATABASE_DRIVER);
			_source.setJdbcUrl(Config.DATABASE_URL);
			_source.setUsername(Config.DATABASE_LOGIN);
			_source.setPassword(Config.DATABASE_PASSWORD);

			_source.getConnection().close();
			
			_log.info(L2DatabaseFactory.class.getSimpleName() + ": Database Connected.");
		}
		catch (Exception e)
		{
			_log.severe(L2DatabaseFactory.class.getSimpleName() + ": Failed to init database connections: "+e);
		}
	}

	public void shutdown()
	{
		try
		{
			_source.close();
		}
		catch (Exception e)
		{
			_log.info(L2DatabaseFactory.class.getName() + "");
		}
		try
		{
			_source = null;
		}
		catch (Exception e)
		{
			_log.info(L2DatabaseFactory.class.getName() + "");
		}
	}

	public static L2DatabaseFactory getInstance()
	{
		return SingletonHolder._instance;
	}

	/**
	 * Gets the connection.
	 * 
	 * @return the connection
	 */
	public Connection getConnection()
	{
		Connection con = null;
		while (con == null)
		{
			try
			{
				con = _source.getConnection();
			}
			catch (SQLException e)
			{
				_log.warning(L2DatabaseFactory.class.getSimpleName() + ": Database connection failed, trying again.");
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
		}
		return con;
	}


	public int getBusyConnectionCount()
	{
		return _source.getTotalLeased();
	}
	
	/**
	 * mono gia to territory na ginei delete
	 * @param fields
	 * @param tableName
	 * @param whereClause
	 * @return
	 */
	public final String prepQuerySelect(String[] fields, String tableName, String whereClause)
	{
		String mySqlTop1 = " Limit 1 ";
		String query = "SELECT " + fields + " FROM " + tableName + " WHERE " + whereClause + mySqlTop1;
		return query;
	}
	
	private static class SingletonHolder
	{
		protected static final L2DatabaseFactory _instance;
		
		static
		{
			try
			{
				_instance = new L2DatabaseFactory();
			}
			catch (Exception e)
			{
				throw new ExceptionInInitializerError(e);
			}
		}
	}
}