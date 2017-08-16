/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jhellas.gameserver.datatables.xml;

import java.io.File;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.idfactory.IdFactory;
import com.l2jhellas.gameserver.model.actor.instance.L2StaticObjectInstance;
import com.l2jhellas.util.XMLDocumentFactory;

public class StaticObjData
{
	private static Logger _log = Logger.getLogger(StaticObjData.class.getName());
	
	public static void load()
	{
		try
		{
			File f = new File("./data/xml/static_objects.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equalsIgnoreCase("staticobject"))
				{
					NamedNodeMap node = d.getAttributes();
					
					L2StaticObjectInstance obj = new L2StaticObjectInstance(IdFactory.getInstance().getNextId());
					obj.setType(Integer.valueOf(node.getNamedItem("type").getNodeValue()));
					obj.setStaticObjectId(Integer.valueOf(node.getNamedItem("id").getNodeValue()));
					obj.setXYZ(Integer.valueOf(node.getNamedItem("x").getNodeValue()), Integer.valueOf(node.getNamedItem("y").getNodeValue()), Integer.valueOf(node.getNamedItem("z").getNodeValue()));
					obj.setMap(node.getNamedItem("texture").getNodeValue(), Integer.valueOf(node.getNamedItem("map_x").getNodeValue()), Integer.valueOf(node.getNamedItem("map_y").getNodeValue()));
					obj.spawnMe();
				}
			}
		}
		catch (Exception e)
		{
			_log.warning(StaticObjData.class.getName() + ": Error while creating StaticObjects table: ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
		_log.info(StaticObjData.class.getSimpleName() + ": Loaded!");
	}
}