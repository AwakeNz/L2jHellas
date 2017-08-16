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
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.PackRoot;
import com.l2jhellas.gameserver.model.L2SummonItem;

public class SummonItemsData
{
	protected static final Logger _log = Logger.getLogger(SummonItemsData.class.getName());

	private HashMap<Integer, L2SummonItem> _summonitems;

	private static SummonItemsData _instance;

	public static SummonItemsData getInstance()
	{
		if (_instance == null)
		{
			_instance = new SummonItemsData();
		}

		return _instance;
	}

	public static void reload()
	{
		_instance = null;
		getInstance();
	}
	
	public SummonItemsData()
	{
		_summonitems = new HashMap<Integer, L2SummonItem>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File f = new File(PackRoot.DATAPACK_ROOT, "data/xml/summon_items.xml");
		if (!f.exists())
		{
			_log.warning(SummonItemsData.class.getName() + ": summon_items.xml could not be loaded: file not found");
			return;
		}
		int itemID = 0, npcID = 0;
		byte summonType = 0;
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
						if (d.getNodeName().equalsIgnoreCase("summon_item"))
						{
							itemID = Integer.valueOf(d.getAttributes().getNamedItem("itemID").getNodeValue());
							npcID = Integer.valueOf(d.getAttributes().getNamedItem("npcID").getNodeValue());
							summonType = Byte.valueOf(d.getAttributes().getNamedItem("summonType").getNodeValue());
							L2SummonItem summonitem = new L2SummonItem(itemID, npcID, summonType);
							_summonitems.put(itemID, summonitem);
						}
					}
				}
			}
		}
		catch (SAXException e)
		{
			_log.warning(SummonItemsData.class.getName() + ": Error while creating table");
		}
		catch (IOException e)
		{
			_log.warning(SummonItemsData.class.getName() + ": Error while creating table");
		}
		catch (ParserConfigurationException e)
		{
			_log.warning(SummonItemsData.class.getName() + ": Error while creating table");
		}

		_log.info("Summon: Loaded " + _summonitems.size() + " summon items.");
	}

	public L2SummonItem getSummonItem(int itemId)
	{
		return _summonitems.get(itemId);
	}

	public int[] itemIDs()
	{
		int size = _summonitems.size();
		int[] result = new int[size];
		int i = 0;

		for (L2SummonItem si : _summonitems.values())
		{
			result[i] = si.getItemId();
			i++;
		}
		return result;
	}
}