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

public class GameServerIP
{
	private static final String dirGame = PackRoot.DATAPACK_ROOT + "/config/Network/IPConfig/IPGameServer.ini";
	
	public static void load()
	{
		// game server
		if(IPConfigData.AUTO_IP)
		{
			try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(dirGame))))
			{
				/** @formatter:off */
				out.write(
					"##########################################################################################\r\n" +
					"# This is the server configuration file. Here you can set up your server.                #\r\n" +
					"# * you can use the NO-IP system for dynamic DNS > http://www.no-ip.com/                 #\r\n" +
					"# * if you want to restore default settings delete this file. and run the server         #\r\n" +
					"##########################################################################################\r\n" +
					"# Configure your External IP(WAN)\r\n" +
					"# Default: 127.0.0.1 (LOCALHOST)\r\n" +
					"ExternalHostname = " + IPConfigData.externalIp + "\r\n" +
					"\r\n" +
					"# Configure your Internal IP(LAN)\r\n" +
					"# Default: 127.0.0.1 (LOCALHOST)\r\n" +
					"InternalHostname = " + IPConfigData.sub.getIPAddress() + "\r\n" +
					"\r\n"+
					"# Bind IP of the gameserver, use * to bind on all available IPs\r\n" +
					"GameserverHostname =  *\r\n" +
					"GameserverPort = 7777\r\n" +
					"\r\n" +
					"# The Loginserver host and port\r\n" +
					"LoginPort = 9014\r\n" +
					"LoginHost = 127.0.0.1\r\n");
				/** @formatter:on */
			}
			catch (Exception e)
			{
				System.err.println("Network Config: could not create " + dirGame);
			}
		}
	}
}