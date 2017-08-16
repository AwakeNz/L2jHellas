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
package Extensions.RankSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.util.database.L2DatabaseFactory;

/**
 * This class contains informations about RPC Rewards.<br>
 * Each reward is defined in database and it is static table in game.
 * 
 * @author Masterio
 */
public class RPCRewardTable
{
	public static final Logger log = Logger.getLogger(RPCRewardTable.class.getSimpleName());

	private static RPCRewardTable _instance = null;

	/** [RPCReward ID, RPCReward object] */
	private Map<Integer, RPCReward> _rpcRewardList = new HashMap<>();

	private RPCRewardTable()
	{
		long startTime = Calendar.getInstance().getTimeInMillis();

		load();

		RPSHtmlRPCRewardList.init(_rpcRewardList.size());

		long endTime = Calendar.getInstance().getTimeInMillis();

		log.log(Level.INFO, " - RPCRewardTable: Data loaded. " + (_rpcRewardList.size()) + " objects in " + (endTime - startTime) + " ms.");
	}

	public static RPCRewardTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new RPCRewardTable();
		}

		return _instance;
	}

	/**
	 * [RPCReward ID, RPCReward object]
	 * 
	 * @return
	 */
	public Map<Integer, RPCReward> getRpcRewardList()
	{
		return _rpcRewardList;
	}

	/**
	 * [RPCReward ID, RPCReward object]
	 * 
	 * @param rpcRewardList
	 */
	public void setRpcRewardList(Map<Integer, RPCReward> rpcRewardList)
	{
		_rpcRewardList = rpcRewardList;
	}

	/**
	 * Updates RPC table in database & gives RPC Reward for player.
	 * 
	 * @param player
	 * @param rpcReward
	 */
	public void giveReward(L2PcInstance player, RPCReward rpcReward)
	{
		if (rpcReward == null)
		{
			return;
		}

		RPC rpc = RPCTable.getInstance().getRpcByPlayerId(player.getObjectId());

		if (rpc == null)
		{
			// Player have no RPC Current!!!
			player.sendMessage("You need more RPC!");
			return;
		}

		if (rpc.getRpcCurrent() < rpcReward.getRpc())
		{
			// Player's RPC Current is not enough!!!
			player.sendMessage("You need more RPC!");
			return;
		}

		if (player.getInventory().getSize() >= player.GetInventoryLimit())
		{
			// Player inventory limit!!!
			player.sendMessage("Inventory is full!");
			return;
		}

		// update database for this player:
		boolean ok = false;

		Connection conn = null;
		Statement stat = null;

		try
		{
			conn = L2DatabaseFactory.getInstance().getConnection();
			stat = conn.createStatement();

			// remove RPC from RPC Current (from model):
			long rpcCurrent = rpc.decreaseRpcCurrentBy(rpcReward.getRpc());

			if (rpc.getDbStatus() == DBStatus.UPDATED)
			{
				rpc.setDbStatus(DBStatus.NONE);
				stat.execute("UPDATE rank_pvp_system_rpc SET rpc_total=" + rpc.getRpcTotal() + ", rpc_current=" + rpcCurrent + " WHERE player_id=" + player.getObjectId());
			}
			else if (rpc.getDbStatus() == DBStatus.INSERTED)
			{
				rpc.setDbStatus(DBStatus.NONE);
				stat.execute("INSERT INTO rank_pvp_system_rpc (player_id, rpc_total, rpc_current) values (" + player.getObjectId() + "," + rpc.getRpcTotal() + "," + rpcCurrent + ")");
			}

			stat.close();

			ok = true;

		}
		catch (Exception e)
		{
			log.log(Level.WARNING, e.getMessage());
			try
			{
				if (conn != null)
				{
					conn.rollback();
				}
			}
			catch (SQLException e1)
			{
				log.log(Level.WARNING, e1.getMessage());
			}
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, e.getMessage());
			}
		}

		// add item into player's inventory:
		if (ok)
		{
			player.addItem("RPC", rpcReward.getItemId(), rpcReward.getItemAmount(), player, true);
		}

	}

	private void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT * FROM rank_pvp_system_rpc_reward ORDER BY id ASC");

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				RPCReward rpcr = new RPCReward();

				rpcr.setId(rset.getInt("id"));
				rpcr.setItemId(rset.getInt("item_id"));
				rpcr.setItemAmount(rset.getLong("item_amount"));
				rpcr.setRpc(rset.getLong("rpc"));

				_rpcRewardList.put(rpcr.getId(), rpcr);
			}

			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			log.log(Level.WARNING, e.getMessage());
		}
	}
}