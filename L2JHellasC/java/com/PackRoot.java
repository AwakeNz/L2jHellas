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
package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.Server;

/**
 * @author l2jhellas
 */
public final class PackRoot
{
	private static final Logger _log = Logger.getLogger(PackRoot.class.getName());
	private static final String CONFIGURATION_FILE = "./config/Others/Pack Root.ini";
	public static File DATAPACK_ROOT;
	public static void load()
	{
		if (Server.serverMode == Server.MODE_GAMESERVER)
		{
			/**
			 * Game Server
			 */
			Properties serverSettings = new Properties();
			try (InputStream is = new FileInputStream(new File(CONFIGURATION_FILE)))
			{
				serverSettings.load(is);
			}
			catch (Exception e)
			{
				_log.severe(PackRoot.class.getName() + ": Error while " + CONFIGURATION_FILE + " settings!");
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
			try
			{
				DATAPACK_ROOT = new File(serverSettings.getProperty("PackRootGame", ".").replaceAll("\\\\", "/")).getCanonicalFile();
			}
			catch (IOException e)
			{
				_log.severe(PackRoot.class.getName() + ": Error setting datapack root!");
				DATAPACK_ROOT = new File(".");
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
		}
		else if (Server.serverMode == Server.MODE_LOGINSERVER)
		{
			/**
			 * Login Server
			 */
			Properties serverSettings = new Properties();
			try (InputStream is = new FileInputStream(new File(CONFIGURATION_FILE)))
			{
				serverSettings.load(is);
			}
			catch (Exception e)
			{
				_log.severe(PackRoot.class.getName() + ": Error while " + CONFIGURATION_FILE + " settings!");
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
			try
			{
				DATAPACK_ROOT = new File(serverSettings.getProperty("PackRootLogin", ".").replaceAll("\\\\", "/")).getCanonicalFile();
			}
			catch (IOException e)
			{
				_log.severe(PackRoot.class.getName() + ": Error setting datapack root!");
				DATAPACK_ROOT = new File(".");
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
		}
		else
		{
			_log.severe("Could not Load Config: server mode was not set for network!");
		}
	}
}