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
package Extensions.AchievmentsEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import Extensions.AchievmentsEngine.base.Achievement;
import Extensions.AchievmentsEngine.base.Condition;
import Extensions.AchievmentsEngine.conditions.Adena;
import Extensions.AchievmentsEngine.conditions.Castle;
import Extensions.AchievmentsEngine.conditions.ClanLeader;
import Extensions.AchievmentsEngine.conditions.ClanLevel;
import Extensions.AchievmentsEngine.conditions.CompleteAchievements;
import Extensions.AchievmentsEngine.conditions.Crp;
import Extensions.AchievmentsEngine.conditions.Hero;
import Extensions.AchievmentsEngine.conditions.HeroCount;
import Extensions.AchievmentsEngine.conditions.ItemsCount;
import Extensions.AchievmentsEngine.conditions.Karma;
import Extensions.AchievmentsEngine.conditions.Levelup;
import Extensions.AchievmentsEngine.conditions.Mage;
import Extensions.AchievmentsEngine.conditions.Marry;
import Extensions.AchievmentsEngine.conditions.MinCMcount;
import Extensions.AchievmentsEngine.conditions.Noble;
import Extensions.AchievmentsEngine.conditions.OnlineTime;
import Extensions.AchievmentsEngine.conditions.Pk;
import Extensions.AchievmentsEngine.conditions.Pvp;
import Extensions.AchievmentsEngine.conditions.RaidKill;
import Extensions.AchievmentsEngine.conditions.RaidPoints;
import Extensions.AchievmentsEngine.conditions.SkillEnchant;
import Extensions.AchievmentsEngine.conditions.Sub;
import Extensions.AchievmentsEngine.conditions.WeaponEnchant;
import Extensions.AchievmentsEngine.conditions.eventKills;
import Extensions.AchievmentsEngine.conditions.eventWins;
import Extensions.AchievmentsEngine.conditions.events;

import com.PackRoot;
import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.util.database.L2DatabaseFactory;

public class AchievementsManager
{
	private static final Logger log = Logger.getLogger(AchievementsManager.class.getSimpleName());
	private final Map<Integer, Achievement> _achievementList = new HashMap<>();
	private final ArrayList<String> _binded = new ArrayList<>();

	public AchievementsManager()
	{
		loadAchievements();
	}

	private void loadAchievements()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);

		File file = new File(PackRoot.DATAPACK_ROOT, "data/xml/achievements.xml");

		if (!file.exists())
		{
			log.log(Level.WARNING, getClass().getSimpleName(), ": Error achievements xml file does not exist, check directory!");
		}
		try
		{
			InputSource in = new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8"));

			Document doc = factory.newDocumentBuilder().parse(in);

			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if (n.getNodeName().equalsIgnoreCase("list"))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if (d.getNodeName().equalsIgnoreCase("achievement"))
						{
							int id = checkInt(d, "id");

							String name = String.valueOf(d.getAttributes().getNamedItem("name").getNodeValue());
							String description = String.valueOf(d.getAttributes().getNamedItem("description").getNodeValue());
							String reward = String.valueOf(d.getAttributes().getNamedItem("reward").getNodeValue());
							boolean repeat = checkBoolean(d, "repeatable");

							ArrayList<Condition> conditions = conditionList(d.getAttributes());

							_achievementList.put(id, new Achievement(id, name, description, reward, repeat, conditions));
							alterTable(id);
						}
					}
				}
			}

			log.log(Level.INFO, getClass().getSimpleName(), ": loaded " + getAchievementList().size() + " achievements from xml!");
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, getClass().getSimpleName(), ": Error ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	public void rewardForAchievement(int achievementID, L2PcInstance player)
	{
		Achievement achievement = _achievementList.get(achievementID);

		for (int id : achievement.getRewardList().keySet())
		{
			int count = achievement.getRewardList().get(id).intValue();
			player.addItem(achievement.getName(), id, count, player, true);

		}
	}

	private static boolean checkBoolean(Node d, String nodename)
	{
		boolean b = false;

		try
		{
			b = Boolean.valueOf(d.getAttributes().getNamedItem(nodename).getNodeValue());
		}
		catch (Exception e)
		{

		}
		return b;
	}

	private int checkInt(Node d, String nodename)
	{
		int i = 0;

		try
		{
			i = Integer.valueOf(d.getAttributes().getNamedItem(nodename).getNodeValue());
		}
		catch (Exception e)
		{

		}
		return i;
	}

	/**
	 * Alter table, catch exception if already exist.
	 * 
	 * @param fieldID
	 */
	private static void alterTable(int fieldID)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			Statement statement = con.createStatement();
			statement.executeUpdate("ALTER TABLE achievements ADD a" + fieldID + " INT DEFAULT 0");
			statement.close();
		}
		catch (SQLException e)
		{
			
		}
	}

	public ArrayList<Condition> conditionList(NamedNodeMap attributesList)
	{
		ArrayList<Condition> conditions = new ArrayList<>();

		for (int j = 0; j < attributesList.getLength(); j++)
		{
			addToConditionList(attributesList.item(j).getNodeName(), attributesList.item(j).getNodeValue(), conditions);
		}

		return conditions;
	}

	public Map<Integer, Achievement> getAchievementList()
	{
		return _achievementList;
	}

	public ArrayList<String> getBinded()
	{
		return _binded;
	}

	public boolean isBinded(int obj, int ach)
	{
		for (String binds : _binded)
		{
			String[] spl = binds.split("@");
			if (spl[0].equals(String.valueOf(obj)) && spl[1].equals(String.valueOf(ach)))
				return true;
		}
		return false;
	}

	public static AchievementsManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final AchievementsManager _instance = new AchievementsManager();
	}

	private static void addToConditionList(String nodeName, Object value, ArrayList<Condition> conditions)
	{
		/** @formatter:off */
		if (nodeName.equals("minLevel")) conditions.add(new Levelup(value));
		else if (nodeName.equals("minPvPCount")) conditions.add(new Pvp(value));
		else if (nodeName.equals("minPkCount")) conditions.add(new Pk(value));
		else if (nodeName.equals("minClanLevel")) conditions.add(new ClanLevel(value));
		else if (nodeName.equals("mustBeHero")) conditions.add(new Hero(value));
		else if (nodeName.equals("mustBeNoble")) conditions.add(new Noble(value));
		else if (nodeName.equals("minWeaponEnchant")) conditions.add(new WeaponEnchant(value));
		else if (nodeName.equals("minKarmaCount")) conditions.add(new Karma(value));
		else if (nodeName.equals("minAdenaCount")) conditions.add(new Adena(value));
		else if (nodeName.equals("minClanMembersCount")) conditions.add(new MinCMcount(value));
		else if (nodeName.equals("mustBeClanLeader")) conditions.add(new ClanLeader(value));
		else if (nodeName.equals("mustBeMarried")) conditions.add(new Marry(value));
		else if (nodeName.equals("itemAmmount")) conditions.add(new ItemsCount(value));
		else if (nodeName.equals("crpAmmount")) conditions.add(new Crp(value));
		else if (nodeName.equals("lordOfCastle")) conditions.add(new Castle(value));
		else if (nodeName.equals("mustBeMageClass")) conditions.add(new Mage(value));
		else if (nodeName.equals("minSubclassCount")) conditions.add(new Sub(value));
		else if (nodeName.equals("CompleteAchievements")) conditions.add(new CompleteAchievements(value));
		else if (nodeName.equals("minSkillEnchant")) conditions.add(new SkillEnchant(value));
		else if (nodeName.equals("minOnlineTime")) conditions.add(new OnlineTime(value));
		else if (nodeName.equals("minHeroCount")) conditions.add(new HeroCount(value));
		else if (nodeName.equals("raidToKill")) conditions.add(new RaidKill(value));
		else if (nodeName.equals("raidToKill1")) conditions.add(new RaidKill(value));
		else if (nodeName.equals("raidToKill2")) conditions.add(new RaidKill(value));
		else if (nodeName.equals("minRaidPoints")) conditions.add(new RaidPoints(value));
		else if (nodeName.equals("eventKills")) conditions.add(new eventKills(value));
		else if (nodeName.equals("events")) conditions.add(new events(value));
		else if (nodeName.equals("eventWins")) conditions.add(new eventWins(value));
		/** @formatter:on */
	}

	public void loadUsed()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;
			ResultSet rs;
			String sql = "SELECT ";
			for (int i = 1; i <= getAchievementList().size(); i++)
			{
				if (i != getAchievementList().size())
					sql = sql + "a" + i + ",";
				else
					sql = sql + "a" + i;
			}

			sql = sql + " FROM achievements";
			statement = con.prepareStatement(sql);

			rs = statement.executeQuery();
			while (rs.next())
			{
				for (int i = 1; i <= getAchievementList().size(); i++)
				{
					String ct = rs.getString(i);
					if (ct.length() > 1 && ct.startsWith("1"))
					{
						_binded.add(ct.substring(ct.indexOf("1") + 1) + "@" + i);
					}
				}
			}
			rs.close();
			statement.close();
		}
		catch (SQLException e)
		{
			log.log(Level.WARNING, getClass().getSimpleName(), ":[ACHIEVEMENTS SAVE GETDATA]");
			if (Config.DEVELOPER)
			{
				e.printStackTrace();
			}
		}
	}
}