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
package com.l2jhellas.gameserver.datatables.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.PackRoot;
import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.L2Skill;

public class SkillSpellbookData
{
	protected static final Logger _log = Logger.getLogger(FishTable.class.getName());

	private static SkillSpellbookData _instance;

	private static Map<Integer, Integer> _skillSpellbooks;

	public static SkillSpellbookData getInstance()
	{
		if (_instance == null)
		{
			_instance = new SkillSpellbookData();
		}

		return _instance;
	}
	
	public static void reload()
	{
		_instance = null;
		getInstance();
	}
	
	private SkillSpellbookData()
	{
		if (!Config.SP_BOOK_NEEDED)
			return;

		_skillSpellbooks = new HashMap<Integer, Integer>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File f = new File(PackRoot.DATAPACK_ROOT, "data/xml/skill_spellbooks.xml");
		if (!f.exists())
		{
			_log.warning(SkillSpellbookData.class.getName() + ": skill_spellbooks.xml could not be loaded: file not found");
			return;
		}
		try
		{
			InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			in.setEncoding("UTF-8");
			Document doc = factory.newDocumentBuilder().parse(in);
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if (n.getNodeName().equalsIgnoreCase("list"))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if (d.getNodeName().equalsIgnoreCase("skill_spellbook"))
						{
							_skillSpellbooks.put(Integer.valueOf(d.getAttributes().getNamedItem("skill_id").getNodeValue()), Integer.valueOf(d.getAttributes().getNamedItem("item_id").getNodeValue()));
						}
					}
				}
			}
		}
		catch (SAXException e)
		{
			_log.warning(SkillSpellbookData.class.getName() + ": Error while creating table");
		}
		catch (IOException e)
		{
			_log.warning(SkillSpellbookData.class.getName() + ": Error while creating table");
		}
		catch (ParserConfigurationException e)
		{
			_log.warning(SkillSpellbookData.class.getName() + ": Error while creating table");
		}

		_log.info("SkillSpellbookTable: Loaded " + _skillSpellbooks.size() + " spellbooks.");
	}

	public int getBookForSkill(int skillId, int level)
	{
		if (skillId == L2Skill.SKILL_DIVINE_INSPIRATION && level != -1)
		{
			switch (level)
			{
				case 1:
					return 8618; // Ancient Book - Divine Inspiration (Modern Language Version)
				case 2:
					return 8619; // Ancient Book - Divine Inspiration (Original Language Version)
				case 3:
					return 8620; // Ancient Book - Divine Inspiration (Manuscript)
				case 4:
					return 8621; // Ancient Book - Divine Inspiration (Original Version)
				default:
					return -1;
			}
		}

		if (!_skillSpellbooks.containsKey(skillId))
			return -1;

		return _skillSpellbooks.get(skillId);
	}

	public int getBookForSkill(L2Skill skill)
	{
		return getBookForSkill(skill.getId(), -1);
	}

	public int getBookForSkill(L2Skill skill, int level)
	{
		return getBookForSkill(skill.getId(), level);
	}
}