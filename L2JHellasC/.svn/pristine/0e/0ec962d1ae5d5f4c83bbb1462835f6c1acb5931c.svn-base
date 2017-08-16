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
package com.l2jhellas.gameserver.instancemanager;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.L2Clan;
import com.l2jhellas.gameserver.model.L2ClanMember;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.util.database.L2DatabaseFactory;

/**
 * @author Setekh
 */
public class SiegeReward
{
	protected static final Logger _log = Logger.getLogger(SiegeReward.class.getName());

	private static SiegeReward _instance;

	public static boolean ACTIVATED_SYSTEM;
	public static boolean REWARD_ACTIVE_MEMBERS_ONLY;

	private final ArrayList<RewardInfoz> _list;
	private final HashMap<Integer, ArrayList<ToReward>> _toReward; // Offline players that didn't get rewarded. =( poor guys, But they'll have a surprise

	public SiegeReward()
	{
		_list = new ArrayList<RewardInfoz>();
		_toReward = new HashMap<Integer, ArrayList<ToReward>>();
		_log.info(SiegeReward.class.getSimpleName() + ": Activated.");
	}

	public static SiegeReward getInstance()
	{
		if (_instance == null)
		{
			_instance = new SiegeReward();
			_instance.loadConfigs();
			_instance.loadOfflineMembers();
		}

		return _instance;
	}

	private void loadOfflineMembers()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement st = con.prepareStatement("SELECT charId, itemId, count, castle_name, rewarded FROM reward_list");
			ResultSet rs = st.executeQuery();

			while (rs.next())
			{
				int charId = rs.getInt("charId");
				int itemId = rs.getInt("itemId");
				int count = rs.getInt("count");
				String castle_name = rs.getString("castle_name");
				boolean rewarded = rs.getBoolean("rewarded");

				if (rewarded)
				{
					deleteRewarded(charId, itemId);
					continue;
				}

				ToReward tr = new ToReward();
				tr.charId = charId;
				tr.itemId = itemId;
				tr.count = count;
				tr.castleName = castle_name;

				if (!_toReward.containsKey(charId))
				{
					try
					// prevent errors
					{
						_toReward.put(charId, new ArrayList<ToReward>());
					}
					finally
					{
						_toReward.get(charId).add(tr);
					}
				}
				else
					_toReward.get(charId).add(tr);

			}

			rs.close();
			st.close();
		}
		catch (Exception e)
		{
			_log.warning(SiegeReward.class.getName() + " Could not load offline members from DB.");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	public void deleteRewarded(int charId, int itemId)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement st = con.prepareStatement("DELETE FROM reward_list WHERE charId=? AND itemId=?");
			st.setInt(1, charId);
			st.setInt(2, itemId);
			st.execute();
			st.close();
		}
		catch (Exception e)
		{
			_log.warning(SiegeReward.class.getName() + " Could not connect to rewards table.");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	private void loadConfigs()
	{
		try
		{
			Properties prop = new Properties();
			prop.load(new FileInputStream(new File("./config/Mods/SiegeRewards.ini")));

			ACTIVATED_SYSTEM = Boolean.parseBoolean(prop.getProperty("ActivateSystem", "false"));
			REWARD_ACTIVE_MEMBERS_ONLY = Boolean.parseBoolean(prop.getProperty("RewardOnlineOnly", "false"));

			if (ACTIVATED_SYSTEM)
			{
				String[] splitz = prop.getProperty("RewardInfo").split(";");

				for (String str : splitz)
				{
					String[] splits = str.split(",");
					_list.add(new RewardInfoz(splits));
				}
			}
		}
		catch (Exception e)
		{
			_log.warning(SiegeReward.class.getName() + " Could not connect to rewards table.");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
		finally
		{
			_log.log(Level.FINE, getClass().getSimpleName() + "  Loaded: " + _list.size() + " Reward Item(s).");
		}
	}

	public void storeDataBase(int charId, String castleName)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			for (RewardInfoz rewz : _list)
			{
				PreparedStatement st = con.prepareStatement("REPLACE INTO reward_list VALUES (?,?,?,?,?)");
				st.setInt(1, charId);
				st.setInt(2, rewz.getItemId());
				st.setInt(3, rewz.getItemCount());
				st.setString(4, castleName);
				st.setInt(5, 0);
				st.execute();
				st.close();
			}
		}
		catch (Exception e)
		{
			_log.warning(SiegeReward.class.getName() + " Could not store into database.");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	public void processWorldEnter(L2PcInstance activeChar)
	{
		if (_toReward.containsKey(activeChar.getObjectId()))
		{
			String castleName = "";

			for (ToReward tr : _toReward.get(activeChar.getObjectId()))
			{
				activeChar.addItem("SiegeReward", tr.itemId, tr.count, activeChar, true);
				castleName = tr.castleName;
				tr.rewarded = true;
			}
			activeChar.sendMessage("Congratulations! You have been rewarded for the " + castleName + " siege victory!");
		}
	}

	public class ToReward
	{
		String castleName;
		int charId, itemId, count;
		boolean rewarded;
	}

	public class RewardInfoz
	{
		// Constants
		private final int _itemId;
		private final int _itemCount;

		public RewardInfoz(String... strings)
		{
			_itemId = Integer.parseInt(strings[0]);
			_itemCount = Integer.parseInt(strings[1]);
		}

		/**
		 * @return Returns the itemId.
		 */
		public int getItemId()
		{
			return _itemId;
		}

		/**
		 * @return Returns the itemCount.
		 */
		public int getItemCount()
		{
			return _itemCount;
		}
	}

	public void notifySiegeEnded(L2Clan clan, String castleName)
	{
		for (L2ClanMember member : clan.getMembers())
		{
			if (member.isOnline())
			{
				L2PcInstance activeChar = member.getPlayerInstance();

				for (RewardInfoz tr : _list)
					activeChar.addItem("SiegeReward", tr.getItemId(), tr.getItemCount(), activeChar, true);

				activeChar.sendMessage("Congratulations! You have been rewarded for the " + castleName + " siege victory!");
			}
			else
				storeDataBase(member.getObjectId(), castleName);
		}
	}
}