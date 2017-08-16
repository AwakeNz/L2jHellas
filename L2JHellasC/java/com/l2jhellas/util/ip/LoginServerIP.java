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
package com.l2jhellas.util.ip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.PackRoot;

public class LoginServerIP
{
	private static final String dirLogin = PackRoot.DATAPACK_ROOT + "/config/Network/IPConfig/IPLoginServer.ini";
	
	public static void load()
	{
		// login server
		if (IPConfigData.AUTO_IP)
		{
			try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(dirLogin))))
			{
				/** @formatter:off */
				out.write(
					"##########################################################################################\r\n" +
					"# Server configuration file. Here you can set up the connection for your server.         #\r\n" +
					"# = you can use the NO-IP system for dynamic DNS > http://www.no-ip.com/                 #\r\n" +
					"# * if you want to restore default settings delete this file. and run the server         #\r\n" +
					"##########################################################################################\r\n" +
					"# Configure your External IP(WAN)\r\n" +
					"# Default: 127.0.0.1 (LOCALHOST)\r\n" +
					"ExternalHostname = " + IPConfigData.externalIp + "\r\n" +
					"\r\n"+
					"# Configure your Internal IP(LAN)\r\n" +
					"# Default: 127.0.0.1 (LOCALHOST)\r\n" +
					"InternalHostname = " + IPConfigData.sub.getIPAddress() + "\r\n" +
					"\r\n" +
					"# Bind ip of the LoginServer, use * to bind on all available IPs\r\n" +
					"LoginserverHostname = *\r\n" +
					"LoginserverPort = 2106\r\n" +
					"\r\n" +
					"# The address on which login will listen for GameServers, use * to bind on all available IPs\r\n" +
					"LoginHostname = 127.0.0.1\r\n" +
					"\r\n" +
					"# The port on which login will listen for GameServers\r\n" +
					"LoginPort = 9014");
					/** @formatter:on */
			}
			catch (Exception e)
			{
				System.err.println("Network Config: could not create " + dirLogin);
			}
		}
	}
}