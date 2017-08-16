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
package Extensions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.PackRoot;
import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author AbsolutePower
 */
public class IpCatcher
{
	public static ArrayList<String> ips = new ArrayList<>();
	static File file = new File(PackRoot.DATAPACK_ROOT, "data/ips.txt");
	
	public String getMacAddr(L2PcInstance p)
	{
		StringBuilder sb = new StringBuilder();
		byte[] mac = null;
		
		if (p != null)
			try
			{
				@SuppressWarnings("static-access")
				final InetAddress ip = p.getClient().getConnection().getInetAddress().getLocalHost();
				
				NetworkInterface network = NetworkInterface.getByInetAddress(ip);
				
				if (network != null)
				{
					mac = network.getHardwareAddress();
				}
				
				if (mac != null)
				{
					for (int i = 0; i < mac.length; i++)
					{
						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
					}
				}
				else
					sb.append("No mac found");
			}
			catch (SocketException e)
			{
				e.printStackTrace();
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
		
		return sb.toString();
	}
	
	public String getIp(L2PcInstance p)
	{
		return p.getClient().getConnection().getInetAddress().getHostAddress();
	}
	
	// return true if catched :D
	public boolean isCatched(L2PcInstance p)
	{
		if (ips.contains(getMacAddr(p)) || ips.contains(getIp(p)))
			return true;
		
		return false;
	}
	
	// make textfile if not exist
	public static void MkTiNe()
	{
		try
		{
			File file = new File(PackRoot.DATAPACK_ROOT, "data/ips.txt");
			boolean exist = file.createNewFile();
			if (!exist)
				return;
		}
		catch (IOException e)
		{
			System.err.print("could not create new file:" + file);
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}
	
	public void addIp(L2PcInstance p)
	{
		MkTiNe();
		final String ip = getIp(p);
		final String name = p.getName();
		
		if (ip != null && name != null)
		ips.add("Name");
		ips.add(name);
		ips.add("Ip");
		ips.add(ip);
		ips.add("Mac Addres");
		ips.add(getMacAddr(p));
		ips.add("-");
		
		FileWriter s = null;
		try
		{
			s = new FileWriter(file);
			for (int i = 0; i < ips.size(); i++)
			{
				s.write(ips.get(i));
				s.write("\r\n");
			}
			s.flush();
			s.close();
			s = null;
		}
		catch (IOException e)
		{
			System.err.print("could not add ip in:" + file);
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
		ipsLoad();
	}
	
	public void removeIp(L2PcInstance p)
	{
		final String ip = getIp(p);
		final String name = p.getName();
		
		if (ip != null && name != null)
			ips.remove("Name");
		ips.remove(name);
		ips.remove("Ip");
		ips.remove(ip);
		ips.remove("Mac Addres");
		ips.remove(getMacAddr(p));
		ips.remove("-");
		
		FileWriter s = null;
		try
		{
			s = new FileWriter(file);
			for (int i = 0; i < ips.size(); i++)
			{
				s.write(ips.get(i));
				s.write("\r\n");
			}
			s.flush();
			s.close();
			s = null;
		}
		catch (IOException e)
		{
			System.err.print("could not remove ip new file:" + file);
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
		ipsLoad();
	}
	
	public static void ipsLoad()
	{
		LineNumberReader l = null;
		try
		{
			MkTiNe();
			String line = null;
			l = new LineNumberReader(new FileReader(file));
			while ((line = l.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, "\n\r");
				if (st.hasMoreTokens())
				{
					String n = st.nextToken();
					ips.add(n);
				}
			}
		}
		catch (Exception e)
		{
			System.err.print("could not load ips from file:" + file);
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
		finally
		{
			try
			{
				if (l != null)
					l.close();
			}
			catch (Exception e)
			{
			}
		}
	}
}