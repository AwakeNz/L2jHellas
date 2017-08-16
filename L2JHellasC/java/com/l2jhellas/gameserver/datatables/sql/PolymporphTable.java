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
package com.l2jhellas.gameserver.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.L2MaxPolyModel;
import com.l2jhellas.gameserver.templates.StatsSet;
import com.l2jhellas.util.database.L2DatabaseFactory;

/**
 * @author Velvet
 */
public class PolymporphTable
{
	private final Logger _log = Logger.getLogger(PolymporphTable.class.getName());

	private final HashMap<Integer, L2MaxPolyModel> _map;
	private static PolymporphTable _instance;

	private final String SQL_SELECT = "SELECT * FROM max_poly";

	public PolymporphTable()
	{
		_map = new HashMap<Integer, L2MaxPolyModel>();
	}

	public static PolymporphTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new PolymporphTable();
			_instance.load();
		}
		return _instance;
	}

	private void load()
	{
		PreparedStatement st = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			st = con.prepareStatement(SQL_SELECT);
			ResultSet rs = st.executeQuery();
			restore(rs);
		}
		catch (Exception e)
		{
			_log.warning(PolymporphTable.class.getName() + ": Error loading DB ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	private void restore(ResultSet data) throws SQLException
	{
		StatsSet set = new StatsSet();

		while (data.next())
		{
			set.set("name", data.getString("name"));
			set.set("title", data.getString("title"));
			set.set("sex", data.getInt("sex"));
			set.set("hair", data.getInt("hair"));
			set.set("hairColor", data.getInt("hairColor"));
			set.set("face", data.getInt("face"));
			set.set("classId", data.getInt("classId"));
			set.set("npcId", data.getInt("npcId"));
			set.set("weaponIdRH", data.getInt("weaponIdRH"));
			set.set("weaponIdLH", data.getInt("weaponIdLH"));
			set.set("weaponIdEnc", data.getInt("weaponIdEnc"));
			set.set("armorId", data.getInt("armorId"));
			set.set("head", data.getInt("head"));
			set.set("hats", data.getInt("hats"));
			set.set("faces", data.getInt("faces"));
			set.set("chest", data.getInt("chest"));
			set.set("legs", data.getInt("legs"));
			set.set("gloves", data.getInt("gloves"));
			set.set("feet", data.getInt("feet"));
			set.set("abnormalEffect", data.getInt("abnormalEffect"));
			set.set("pvpFlag", data.getInt("pvpFlag"));
			set.set("karma", data.getInt("karma"));
			set.set("recom", data.getInt("recom"));
			set.set("clan", data.getInt("clan"));
			set.set("isHero", data.getInt("isHero"));
			set.set("pledge", data.getInt("pledge"));
			set.set("nameColor", data.getInt("nameColor"));
			set.set("titleColor", data.getInt("titleColor"));

			L2MaxPolyModel poly = new L2MaxPolyModel(set);
			_map.put(poly.getNpcId(), poly);// xD
		}
		_log.info(PolymporphTable.class.getSimpleName() + ": Loaded " + _map.size() + " npc to pc entries.");
	}

	public L2MaxPolyModel getModelForID(int key)
	{
		return _map.get(key);
	}
}