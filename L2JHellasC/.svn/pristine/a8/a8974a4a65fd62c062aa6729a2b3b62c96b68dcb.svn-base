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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2jhellas.gameserver.templates.L2Henna;
import com.l2jhellas.gameserver.templates.StatsSet;
import com.l2jhellas.util.XMLDocumentFactory;

public class HennaData
{
	private static Logger _log = Logger.getLogger(HennaData.class.getName());
	
	private final Map<Integer, L2Henna> _henna = new HashMap<>();
	private final Map<Integer, List<L2Henna>> _hennaTrees = new HashMap<>();
	
	protected HennaData()
	{
		try
		{
			final File f = new File("./data/xml/henna.xml");
			final Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			final Node n = doc.getFirstChild();
			
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (!d.getNodeName().equalsIgnoreCase("henna"))
					continue;
				
				final StatsSet hennaDat = new StatsSet();
				final Integer id = Integer.valueOf(d.getAttributes().getNamedItem("symbol_id").getNodeValue());
				
				hennaDat.set("symbol_id", id);
				
				hennaDat.set("dye", Integer.valueOf(d.getAttributes().getNamedItem("dye_id").getNodeValue()));
				hennaDat.set("price", Integer.valueOf(d.getAttributes().getNamedItem("price").getNodeValue()));
				
				hennaDat.set("INT", Integer.valueOf(d.getAttributes().getNamedItem("INT").getNodeValue()));
				hennaDat.set("STR", Integer.valueOf(d.getAttributes().getNamedItem("STR").getNodeValue()));
				hennaDat.set("CON", Integer.valueOf(d.getAttributes().getNamedItem("CON").getNodeValue()));
				hennaDat.set("MEN", Integer.valueOf(d.getAttributes().getNamedItem("MEN").getNodeValue()));
				hennaDat.set("DEX", Integer.valueOf(d.getAttributes().getNamedItem("DEX").getNodeValue()));
				hennaDat.set("WIT", Integer.valueOf(d.getAttributes().getNamedItem("WIT").getNodeValue()));
				final String[] classes = d.getAttributes().getNamedItem("classes").getNodeValue().split(",");
				
				final L2Henna template = new L2Henna(hennaDat);
				_henna.put(id, template);
				
				for (String clas : classes)
				{
					final Integer classId = Integer.valueOf(clas);
					if (!_hennaTrees.containsKey(classId))
					{
						List<L2Henna> list = new ArrayList<>();
						list.add(template);
						_hennaTrees.put(classId, list);
					}
					else
						_hennaTrees.get(classId).add(template);
				}
			}
		}
		catch (Exception e)
		{
			_log.warning(HennaData.class.getSimpleName() + ": HennaTable: Error loading from database:" + e.getMessage());
		}
		_log.config("HennaTable: Loaded " + _henna.size() + " templates.");
	}
	
	public L2Henna getTemplate(int id)
	{
		return _henna.get(id);
	}
	
	public List<L2Henna> getAvailableHenna(int classId)
	{
		final List<L2Henna> henna = _hennaTrees.get(classId);
		if (henna == null)
			return Collections.emptyList();
		
		return henna;
	}
	
	public static HennaData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final HennaData _instance = new HennaData();
	}
}