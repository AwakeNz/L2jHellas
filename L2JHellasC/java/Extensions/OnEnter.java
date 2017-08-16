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

import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.serverpackets.NpcHtmlMessage;

public class OnEnter
{
	public static void subhtml(L2PcInstance player)
	{
		StringBuilder tb = new StringBuilder();
		NpcHtmlMessage html = new NpcHtmlMessage(1);

		tb.append("<html><head><title>Submit your Email</title></head>");
		tb.append("<body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Email submitance</font>");
		tb.append("<br1><font color=\"00FF00\">" + player.getName() + "</font>, please submit here your REAL email address.</td></tr></table></center>");
		tb.append("<center>");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32 align=center><br>");
		tb.append("</center>");
		tb.append("<table width=\"350\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"Icon.etc_old_key_i02\" width=\"32\" height=\"32\"></td>");
		tb.append("<td valign=\"top\">Please enter your Email:<multiedit var=\"email1\" width=180 height=15>");
		tb.append("<br1>We need this to be your real one.</td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<button value=\"Submit\" action=\"bypass -h submitemail $email1\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\">");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32 align=center>");
		tb.append("</center>");
		tb.append("</body></html>");

		html.setHtml(tb.toString());
		player.sendPacket(html);
	}
}