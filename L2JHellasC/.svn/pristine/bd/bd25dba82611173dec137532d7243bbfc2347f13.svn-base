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
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.PackRoot;
import com.l2jhellas.gameserver.model.L2NpcWalkerNode;

public class NpcWalkerRoutesData
{
	protected static Logger _log = Logger.getLogger(NpcWalkerRoutesData.class.getName());

	private static NpcWalkerRoutesData _instance;

	private static FastList<L2NpcWalkerNode> _routes = new FastList<L2NpcWalkerNode>();

	public static NpcWalkerRoutesData getInstance()
	{
		if (_instance == null)
		{
			_instance = new NpcWalkerRoutesData();
		}

		return _instance;
	}

	public static void reload()
	{
		if(_routes == null)
			_log.warning(NpcWalkerRoutesData.class.getSimpleName() + ": WalkerRoutesData NPE error.");
		_routes.clear();
		load();
	}
	
	public static void load()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File f = new File(PackRoot.DATAPACK_ROOT, "data/xml/walker_routes.xml");
		if (!f.exists())
		{
			_log.warning(NpcWalkerRoutesData.class.getSimpleName() + ": walker_routes.xml could not be loaded: file not found");
			return;
		}
		try
		{
			final InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			in.setEncoding("UTF-8");
			Document doc = factory.newDocumentBuilder().parse(in);
			final L2NpcWalkerNode route = new L2NpcWalkerNode();
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if (n.getNodeName().equalsIgnoreCase("list"))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if (d.getNodeName().equalsIgnoreCase("walker_route"))
						{
							final int route_id = Integer.valueOf(d.getAttributes().getNamedItem("route_id").getNodeValue());
							final int npc_id = Integer.valueOf(d.getAttributes().getNamedItem("npc_id").getNodeValue());
							final String move_point = String.valueOf(d.getAttributes().getNamedItem("move_point").getNodeValue());
							final String chatText = String.valueOf(d.getAttributes().getNamedItem("chatText").getNodeValue());
							final int move_x = Integer.valueOf(d.getAttributes().getNamedItem("move_x").getNodeValue());
							final int move_y = Integer.valueOf(d.getAttributes().getNamedItem("move_y").getNodeValue());
							final int move_z = Integer.valueOf(d.getAttributes().getNamedItem("move_z").getNodeValue());
							final int delay = Integer.valueOf(d.getAttributes().getNamedItem("delay").getNodeValue());
							final boolean running = Boolean.valueOf(d.getAttributes().getNamedItem("running").getNodeValue());

							route.setRouteId(route_id);
							route.setNpcId(npc_id);
							route.setMovePoint(move_point);
							route.setChatText(chatText);
							route.setMoveX(move_x);
							route.setMoveY(move_y);
							route.setMoveZ(move_z);
							route.setDelay(delay);
							route.setRunning(running);
							_routes.add(route);
						}
					}
				}
			}
		}
		catch (SAXException e)
		{
			_log.warning(NpcWalkerRoutesData.class.getSimpleName() + ": Error while creating table");
		}
		catch (IOException e)
		{
			_log.warning(NpcWalkerRoutesData.class.getSimpleName() + ": Error while creating table");
		}
		catch (ParserConfigurationException e)
		{
			_log.warning(NpcWalkerRoutesData.class.getSimpleName() + ": Error while creating table");
		}

		_log.warning(NpcWalkerRoutesData.class.getSimpleName() + ": WalkerRoutesData: Loaded " + _routes.size() + " npc walker routes.");
	}

	public FastList<L2NpcWalkerNode> getRouteForNpc(int id)
	{
		FastList<L2NpcWalkerNode> _return = new FastList<L2NpcWalkerNode>();
		for (FastList.Node<L2NpcWalkerNode> n = _routes.head(), end = _routes.tail(); (n = n.getNext()) != end;)
			if (n.getValue().getNpcId() == id)
				_return.add(n.getValue());
		return _return;
	}
}