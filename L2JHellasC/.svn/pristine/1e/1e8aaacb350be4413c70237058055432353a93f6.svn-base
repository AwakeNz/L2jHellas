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
package com.l2jhellas.gameserver.communitybbs.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.communitybbs.BB.Forum;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.util.database.L2DatabaseFactory;

public class ForumsBBSManager extends BaseBBSManager
{
	private static Logger _log = Logger.getLogger(ForumsBBSManager.class.getName());
	private final Map<Integer, Forum> _root;
	private final List<Forum> _table;
	private static ForumsBBSManager _instance;
	private int _lastid = 1;

	public static ForumsBBSManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new ForumsBBSManager();
			_instance.load();
		}
		return _instance;
	}

	private ForumsBBSManager()
	{
		_root = new HashMap<Integer, Forum>();
		_table = new ArrayList<Forum>();
	}

	public void addForum(Forum ff)
	{
		_table.add(ff);

		if (ff.getID() > _lastid)
		{
			_lastid = ff.getID();
		}
	}

	private void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT forum_id FROM forums WHERE forum_type=0");
			ResultSet result = statement.executeQuery();
			while (result.next())
			{
				Forum f = new Forum(Integer.parseInt(result.getString("forum_id")), null);
				_root.put(Integer.parseInt(result.getString("forum_id")), f);
			}
			result.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning(ForumsBBSManager.class.getName() + ": data error on Forum (root): ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
	}

	public Forum getForumByName(String Name)
	{
		for (Forum f : _table)
		{
			if (f != null && f.getName().equals(Name))
			{
				return f;
			}
		}
		return null;
	}

	/**
	 * @param name
	 * @param forumByName
	 * @return
	 */
	public Forum createNewForum(String name, Forum parent, int type, int perm, int oid)
	{
		Forum forum;
		forum = new Forum(name, parent, type, perm, oid);
		forum.insertindb();
		return forum;
	}

	
	@SuppressWarnings("unused")
	public int getANewID()
	{
		int temp = 0;
		for (Forum f : _table)
			temp++;
		_lastid++;
		if (temp == _lastid)
			return _lastid++;
		return _lastid;
	}

	/**
	 * @param idf
	 * @return
	 */
	public Forum getForumByID(int idf)
	{
		for (Forum f : _table)
		{
			if (f.getID() == idf)
			{
				return f;
			}
		}
		return null;
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
	}
}