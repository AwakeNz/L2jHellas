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
package com.l2jhellas.gameserver.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.PackRoot;
import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.datatables.sql.ItemTable;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2TradeList;
import com.l2jhellas.util.database.L2DatabaseFactory;

public class TradeController
{
	private static Logger _log = Logger.getLogger(TradeController.class.getName());
	private static TradeController _instance;

	private static final String SELECT_MERCHANT_DATA = "SELECT * FROM merchant_shopids";
	private static final String SELECT_MERCHANT_ORDER = "SELECT * FROM merchant_buylists WHERE shop_id=? ORDER BY `order` ASC";
	private static final String SELECT_MERCHANT_TIME = "SELECT DISTINCT time, savetimer FROM merchant_buylists WHERE time <> 0 ORDER BY time";
	private static final String UPDATE_MERCHANT_TIMER = "UPDATE merchant_buylists SET savetimer =? WHERE time =?";
	private static final String UPDATE_MERCHANT_CURCOUNT = "UPDATE merchant_buylists SET currentCount=? WHERE item_id=? AND shop_id=?";
	
	private int _nextListId;
	private final Map<Integer, L2TradeList> _lists;
	private final Map<Integer, L2TradeList> _listsTaskItem;

	/** Task launching the function for restore count of Item (Clan Hall) */
	public class RestoreCount implements Runnable
	{
		private final int _timer;

		public RestoreCount(int time)
		{
			_timer = time;
		}

		@Override
		public void run()
		{
			try
			{
				restoreCount(_timer);
				dataTimerSave(_timer);
				ThreadPoolManager.getInstance().scheduleGeneral(new RestoreCount(_timer), (long) _timer * 60 * 60 * 1000);
			}
			catch (Throwable t)
			{

			}
		}
	}

	public static TradeController getInstance()
	{
		if (_instance == null)
			_instance = new TradeController();

		return _instance;
	}

	private TradeController()
	{
		_lists = new HashMap<Integer, L2TradeList>();
		_listsTaskItem = new HashMap<Integer, L2TradeList>();
		File buylistData = new File(PackRoot.DATAPACK_ROOT, "data/buylists.csv");
		if (buylistData.exists())
		{
			_log.warning(TradeController.class.getName() + ": Do, please, remove buylists from data folder and use SQL buylist instead.");
			String line = null;
			LineNumberReader lnr = null;
			int dummyItemCount = 0;

			try
			{
				lnr = new LineNumberReader(new BufferedReader(new FileReader(buylistData)));

				while ((line = lnr.readLine()) != null)
				{
					if (line.trim().length() == 0 || line.startsWith("#"))
						continue;

					dummyItemCount += parseList(line);
				}

				if (Config.DEBUG)
					_log.log(Level.FINE, getClass().getName() + ": " + dummyItemCount + " Dummy-Items for buylists");

				_log.info(TradeController.class.getSimpleName() + ": Loaded " + _lists.size() + " Buylists.");
			}
			catch (Exception e)
			{
				_log.warning(TradeController.class.getName() + ": error while creating trade controller in linenr: " + lnr.getLineNumber());
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
		}
		else
		{
			_log.log(Level.FINER, getClass().getSimpleName() + ": No buylists were found in data folder, using SQL buylist instead.");

			/*
			 * Initialize Shop buylist
			 */
			int dummyItemCount = 0;
			boolean LimitedItem = false;
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement1 = con.prepareStatement(SELECT_MERCHANT_DATA);
				ResultSet rset1 = statement1.executeQuery();
				while (rset1.next())
				{
					PreparedStatement statement = con.prepareStatement(SELECT_MERCHANT_ORDER);
					statement.setString(1, String.valueOf(rset1.getInt("shop_id")));
					ResultSet rset = statement.executeQuery();
					if (rset.next())
					{
						LimitedItem = false;
						dummyItemCount++;
						L2TradeList buy1 = new L2TradeList(rset1.getInt("shop_id"));
						int itemId = rset.getInt("item_id");
						int price = rset.getInt("price");
						int count = rset.getInt("count");
						int currentCount = rset.getInt("currentCount");
						int time = rset.getInt("time");
						L2ItemInstance item = ItemTable.getInstance().createDummyItem(itemId);
						if (item == null)
						{
							rset.close();
							statement.close();
							continue;
						}
						if (count > -1)
						{
							item.setCountDecrease(true);
							LimitedItem = true;
						}
						item.setPriceToSell(price);
						item.setTime(time);
						item.setInitCount(count);
						if (currentCount > -1)
							item.setCount(currentCount);
						else
							item.setCount(count);
						buy1.addItem(item);
						buy1.setNpcId(rset1.getString("npc_id"));
						try
						{
							while (rset.next())
							{
								dummyItemCount++;
								itemId = rset.getInt("item_id");
								price = rset.getInt("price");
								count = rset.getInt("count");
								time = rset.getInt("time");
								currentCount = rset.getInt("currentCount");
								L2ItemInstance item2 = ItemTable.getInstance().createDummyItem(itemId);
								if (item2 == null)
									continue;
								if (count > -1)
								{
									item2.setCountDecrease(true);
									LimitedItem = true;
								}
								item2.setPriceToSell(price);
								item2.setTime(time);
								item2.setInitCount(count);
								if (currentCount > -1)
									item2.setCount(currentCount);
								else
									item2.setCount(count);
								buy1.addItem(item2);
							}
						}
						catch (Exception e)
						{
							_log.warning(TradeController.class.getName() + ": Problem with buylist " + buy1.getListId() + " item " + itemId);
							if (Config.DEVELOPER)
								e.printStackTrace();
						}
						if (LimitedItem)
							_listsTaskItem.put(new Integer(buy1.getListId()), buy1);
						else
							_lists.put(new Integer(buy1.getListId()), buy1);

						_nextListId = Math.max(_nextListId, buy1.getListId() + 1);
					}
					rset.close();
					statement.close();
				}
				rset1.close();
				statement1.close();

				if (Config.DEBUG)
					_log.log(Level.FINE, getClass().getName() + ": created " + dummyItemCount + " Dummy-Items for buylists");

				_log.info(TradeController.class.getSimpleName() + ": Loaded " + _lists.size() + " Buylists.");
				_log.info(TradeController.class.getSimpleName() + ": Loaded " + _listsTaskItem.size() + " Limited Buylists.");
				/*
				 * Restore Task for reinitialize count of buy item
				 */
				try
				{
					int time = 0;
					long savetimer = 0;
					long currentMillis = System.currentTimeMillis();
					PreparedStatement statement2 = con.prepareStatement(SELECT_MERCHANT_TIME);
					ResultSet rset2 = statement2.executeQuery();
					while (rset2.next())
					{
						time = rset2.getInt("time");
						savetimer = rset2.getLong("savetimer");
						if (savetimer - currentMillis > 0)
							ThreadPoolManager.getInstance().scheduleGeneral(new RestoreCount(time), savetimer - System.currentTimeMillis());
						else
							ThreadPoolManager.getInstance().scheduleGeneral(new RestoreCount(time), 0);
					}
					rset2.close();
					statement2.close();
				}
				catch (SQLException e)
				{
					_log.warning(TradeController.class.getName() + ": Could not restore Timer for Item count.");
					if (Config.DEVELOPER)
						e.printStackTrace();
				}
			}
			catch (Exception e)
			{
				// problem with initializing spawn, go to next one
				_log.warning(TradeController.class.getName() + ": Buylists could not be initialized.");
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
		}
	}

	private int parseList(String line)
	{
		int itemCreated = 0;
		StringTokenizer st = new StringTokenizer(line, ";");

		int listId = Integer.parseInt(st.nextToken());
		L2TradeList buy1 = new L2TradeList(listId);
		while (st.hasMoreTokens())
		{
			int itemId = Integer.parseInt(st.nextToken());
			int price = Integer.parseInt(st.nextToken());
			L2ItemInstance item = ItemTable.getInstance().createDummyItem(itemId);
			item.setPriceToSell(price);
			buy1.addItem(item);
			itemCreated++;
		}
		_lists.put(new Integer(buy1.getListId()), buy1);
		return itemCreated;
	}

	public L2TradeList getBuyList(int listId)
	{
		if (_lists.get(new Integer(listId)) != null)
			return _lists.get(new Integer(listId));

		return _listsTaskItem.get(new Integer(listId));
	}

	public List<L2TradeList> getBuyListByNpcId(int npcId)
	{
		List<L2TradeList> lists = new ArrayList<L2TradeList>();

		for (L2TradeList list : _lists.values())
		{
			if (list.getNpcId().startsWith("gm"))
				continue;
			if (npcId == Integer.parseInt(list.getNpcId()))
				lists.add(list);
		}
		for (L2TradeList list : _listsTaskItem.values())
		{
			if (list.getNpcId().startsWith("gm"))
				continue;
			if (npcId == Integer.parseInt(list.getNpcId()))
				lists.add(list);
		}
		return lists;
	}

	protected void restoreCount(int time)
	{
		if (_listsTaskItem == null)
			return;
		for (L2TradeList list : _listsTaskItem.values())
			list.restoreCount(time);
	}

	protected void dataTimerSave(int time)
	{
		long timerSave = System.currentTimeMillis() + (long) time * 60 * 60 * 1000;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(UPDATE_MERCHANT_TIMER);
			statement.setLong(1, timerSave);
			statement.setInt(2, time);
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warning(TradeController.class.getName() + ": Could not update Timer save in Buylist.");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	public void dataCountStore()
	{
		int listId;
		if (_listsTaskItem == null)
			return;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{

			for (L2TradeList list : _listsTaskItem.values())
			{
				if (list == null)
					continue;
				listId = list.getListId();

				for (L2ItemInstance Item : list.getItems())
				{
					if (Item.getCount() < Item.getInitCount()) // needed?
					{
						PreparedStatement statement = con.prepareStatement(UPDATE_MERCHANT_CURCOUNT);
						statement.setInt(1, Item.getCount());
						statement.setInt(2, Item.getItemId());
						statement.setInt(3, listId);
						statement.executeUpdate();
						statement.close();
					}
				}
			}
		}
		catch (SQLException e)
		{
			_log.warning(TradeController.class.getName() + ": Could not store Count Item");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public synchronized int getNextId()
	{
		return _nextListId++;
	}

	/**
	 * @reload ofc
	 */
	public static void reload()
	{
		_instance = new TradeController();
	}
}