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
package com.l2jhellas.gameserver.model.actor.instance;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import Extensions.AccountManager.L2Emailer;

import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jhellas.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jhellas.gameserver.network.serverpackets.ValidateLocation;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;
import com.l2jhellas.util.Rnd;
import com.l2jhellas.util.database.L2DatabaseFactory;

public class L2AccountManagerInstance extends L2NpcInstance
{
	private static String ans = "";

	public L2AccountManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	class maildelay implements Runnable
	{
		private final L2PcInstance p;

		public maildelay(L2PcInstance player)
		{
			p = player;
		}

		@Override
		public void run()
		{
			String[] email =
			{
				getEmailAddress(p)
			};
			generateCode(p);
			try
			{
				L2Emailer.sendL2Mail(email, "Security Code", "Your security code is : " + getCode(p));
			}
			catch (MessagingException e)
			{
				e.printStackTrace();
			}
			successhtml(p);
		}
	}

	class secmaildelay implements Runnable
	{
		private final L2PcInstance p;

		public secmaildelay(L2PcInstance player)
		{
			p = player;
		}

		@Override
		public void run()
		{
			String[] email =
			{
				getEmailAddress(p)
			};
			generateSecCode(p);
			try
			{
				L2Emailer.sendL2Mail(email, "Security Question Change Code", "Your security question change code is : " + getSecCode(p));
			}
			catch (MessagingException e)
			{
				e.printStackTrace();
			}
			secsuccesshtml(p);
		}
	}

	class changemaildelay implements Runnable
	{
		private final L2PcInstance p;

		public changemaildelay(L2PcInstance player)
		{
			p = player;
		}

		@Override
		public void run()
		{
			String[] email =
			{
				getEmailAddress(p)
			};
			generatechangeCode(p);
			try
			{
				L2Emailer.sendL2Mail(email, "Email Change Code", "Your email change code is : " + getChangeCode(p));
			}
			catch (MessagingException e)
			{
				e.printStackTrace();
			}
			successchangehtml(p);
		}
	}

	@Override
	public void onBypassFeedback(final L2PcInstance player, String command)
	{
		if (player == null || command == null)
			return;

		if (command.startsWith("changeEmail"))
		{
			sendingHtml(player);
			ThreadPoolManager.getInstance().scheduleGeneral(new changemaildelay(player), 1000);
		}

		if (command.startsWith("generateCode"))
		{
			sendingHtml(player);
			ThreadPoolManager.getInstance().scheduleGeneral(new maildelay(player), 1000);

		}

		if (command.startsWith("changeSec"))
		{
			if (!hasSubSec(player))
			{
				player.sendMessage("You have not submitted a security question");
			}
			else
			{
				sendingHtml(player);
				ThreadPoolManager.getInstance().scheduleGeneral(new secmaildelay(player), 1000);
			}
		}

		if (command.startsWith("forgPass"))
		{
			forgPassHtml(player);
		}

		if (command.startsWith("change_password"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			String newPass = null;
			String repeatNewPass = null;
			try
			{
				if (st.hasMoreTokens())
				{
					newPass = st.nextToken();
					repeatNewPass = st.nextToken();
				}
				else
				{
					player.sendMessage("Please fill in all the blanks before requesting for a password change.");
					return;
				}
				changePassword(newPass, repeatNewPass, player);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}

		if (command.startsWith("change_sec"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			String newSec = null;
			String repeatNewSec = null;
			try
			{
				if (st.hasMoreTokens())
				{
					newSec = st.nextToken();
					repeatNewSec = st.nextToken();
				}
				else
				{
					player.sendMessage("Please fill in all the blanks before requesting for a answer change.");
					return;
				}
				changeAnswer(newSec, repeatNewSec, player);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}

		if (command.startsWith("resetPass"))
		{
			// if we allow here players to type acc/name we might have issues
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			String acc = player.getAccountName();
			String cha = player.getName();
			try
			{
				if (st.hasMoreTokens())
				{
					ans = st.nextToken();
					_log.warning(L2AccountManagerInstance.class.getSimpleName() + ": 2 "+ans +" "+ acc+ " " + cha);
				}
				else
				{
					player.sendMessage("Please type your secret answer to use password reset.");
					return;
				}
				resetPass(acc, cha, player);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}

		if (command.startsWith("change_email"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			String newMail = null;
			String repeatNewMail = null;
			try
			{
				if (st.hasMoreTokens())
				{
					newMail = st.nextToken();
					repeatNewMail = st.nextToken();
				}
				else
				{
					player.sendMessage("Please fill in all the blanks before requesting for a email change.");
					return;
				}
				changeEmail(newMail, repeatNewMail, player);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}

		if (command.startsWith("submitcode"))
		{
			try
			{
				String value = command.substring(10);
				StringTokenizer s = new StringTokenizer(value, " ");
				int _code = 0;
				int _dbcode = getCode(player);

				try
				{
					_code = Integer.parseInt(s.nextToken());

					if (Integer.toString(_code).length() != 4)
					{
						player.sendMessage("You have to fill the pin box with 4 numbers.Not more, not less.");
						return;
					}

					if (_code == _dbcode)
					{
						changepasshtml(player);
					}
					else
					{
						player.sendMessage("The code we sent and the code you submitted does not match. You are not able to change your pass. Try again.");
					}
				}

				catch (Exception e)
				{
					player.sendMessage("The Code must be 4 numbers.");
				}
			}
			catch (Exception e)
			{
				player.sendMessage("The Code must be 4 numbers.");
			}
		}

		if (command.startsWith("submitchangecode"))
		{
			try
			{
				String value = command.substring(16);
				StringTokenizer s = new StringTokenizer(value, " ");
				int _code = 0;
				int _dbcode = getMailCode(player);

				try
				{
					_code = Integer.parseInt(s.nextToken());

					if (Integer.toString(_code).length() != 4)
					{
						player.sendMessage("You have to fill the pin box with 4 numbers.Not more, not less.");
						return;
					}

					if (_code == _dbcode)
					{
						changeemailhtml(player);
					}
					else
					{
						player.sendMessage("The code we sent and the code you submitted does not match. You are not able to change your email. Try again.");
					}
				}
				catch (Exception e)
				{
					player.sendMessage("The Code must be 4 numbers.");
				}
			}
			catch (Exception e)
			{
				player.sendMessage("The Code must be 4 numbers.");
			}
		}

		if (command.startsWith("submitseccode"))
		{
			try
			{
				String value = command.substring(13);
				StringTokenizer s = new StringTokenizer(value, " ");
				int _code = 0;
				int _dbcode = getSecCode(player);

				try
				{
					_code = Integer.parseInt(s.nextToken());

					if (Integer.toString(_code).length() != 4)
					{
						player.sendMessage("You have to fill the pin box with 4 numbers.Not more, not less.");
						return;
					}

					if (_code == _dbcode)
					{
						changesechtml(player);
					}
					else
					{
						player.sendMessage("The code we sent and the code you submitted does not match. You are not able to change your security answer. Try again.");
					}
				}
				catch (Exception e)
				{
					player.sendMessage("The Code must be 4 numbers.");
				}
			}
			catch (Exception e)
			{
				player.sendMessage("The Code must be 4 numbers.");
			}
		}

		if (command.startsWith("submitemail"))
		{
			try
			{
				String value = command.substring(11);
				StringTokenizer s = new StringTokenizer(value, " ");
				String email1 = null;
				// String email2 = null;

				try
				{
					email1 = s.nextToken();
					// email2 = s.nextToken();

					try
					{
						try (Connection con = L2DatabaseFactory.getInstance().getConnection())
						{
							PreparedStatement statement = con.prepareStatement("UPDATE characters SET email=? WHERE obj_Id=?");
							statement.setString(1, email1);
							statement.setInt(2, player.getObjectId());
							statement.execute();
							statement.close();

						}
						catch (Exception e)
						{
						}
						player.sendMessage("We successfully added your email " + email1 + " to our database");
						setHasSubEmail(player);
					}
					catch (Exception e)
					{
					}
				}
				catch (Exception e)
				{
					player.sendMessage("Something went wrong.");
				}
			}
			catch (Exception e)
			{
				player.sendMessage("Something went wrong.");
			}
		}

		if (command.startsWith("submitanswer"))
		{
			try
			{
				String value = command.substring(12);
				StringTokenizer s = new StringTokenizer(value, " ");
				String answer = null;
				// String email2 = null;

				try
				{
					answer = s.nextToken();

					try (Connection con = L2DatabaseFactory.getInstance().getConnection())
					{
						PreparedStatement statement = con.prepareStatement("UPDATE characters SET answer=? WHERE obj_Id=?");
						statement.setString(1, answer);
						statement.setInt(2, player.getObjectId());
						statement.execute();
						statement.close();
					}
					catch (Exception e)
					{
					}
					player.sendMessage("We successfully added your security answer " + answer + " to our database");
					setHasSubSec(player);
				}
				catch (Exception e)
				{
					player.sendMessage("Something went wrong.");
				}
			}
			catch (Exception e)
			{
				player.sendMessage("Something went wrong.");
			}

		}

		if (command.startsWith("addSec"))
		{
			if (hasSubSec(player))
				player.sendMessage("You have already submitted a security question");
			else
				addSecHtml(player);
		}

		if (command.startsWith("forgSec"))
		{
			if (!hasSubSec(player))
			{
				player.sendMessage("You have not submitted a security question");
			}
			else
			{
				String answer = null;
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement statement = con.prepareStatement("SELECT answer FROM characters WHERE obj_Id=?");
					statement.setInt(1, player.getObjectId());

					ResultSet rset = statement.executeQuery();

					while (rset.next())
					{
						answer = rset.getString("answer");
					}
					
					rset.close();
					statement.close();
				}
				catch (Exception e)
				{
				}
				String[] email =
				{
					getEmailAddress(player)
				};
				try
				{
					L2Emailer.sendL2Mail(email, "Security Answer", "Your security answer is : " + answer);
					player.sendMessage("We successfully sent the security answer to your email address: " + getEmailAddress(player));
				}
				catch (MessagingException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void addSecHtml(L2PcInstance player)
	{
		StringBuilder tb = new StringBuilder();
		NpcHtmlMessage html = new NpcHtmlMessage(1);

		tb.append("<html><head><title>Add Security Question</title></head>");
		tb.append("<body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Security Question</font>");
		tb.append("<br1><font color=\"00FF00\">" + player.getName() + "</font>, submit here your security answer.</td></tr></table></center>");
		tb.append("<center>");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32 align=center><br>");
		tb.append("</center>");
		tb.append("<table width=\"350\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"Icon.etc_old_key_i02\" width=\"32\" height=\"32\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF0000\">What is your maiden mother's name.</font>");
		tb.append("<td valign=\"top\">Enter your answer:<multiedit var=\"answer\" width=180 height=15>");
		tb.append("<br1>IMPORTANT: It has to be one word.</td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<button value=\"Submit\" action=\"bypass -h npc_" + getObjectId() + "_submitanswer $answer\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\">");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32 align=center>");
		tb.append("</center>");
		tb.append("</body></html>");

		html.setHtml(tb.toString());
		player.sendPacket(html);
	}

	private boolean hasSubSec(L2PcInstance player)
	{
		int hasSubSec = -1;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT hasSubSec FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				hasSubSec = rset.getInt("hasSubSec");
			}

			rset.close();
			statement.close();
			
			if (hasSubSec == 1)
				return true;
			else if (hasSubSec == 0)
				return false;
		}
		catch (Exception e)
		{
		}
		return false;
	}

	@Override
	public void onAction(L2PcInstance player)
	{
		if (!canTarget(player))
		{
			return;
		}

		if (this != player.getTarget())
		{
			player.setTarget(this);

			player.sendPacket(new MyTargetSelected(getObjectId(), 0));

			player.sendPacket(new ValidateLocation(this));
		}
		else if (!canInteract(player))
		{
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
		}
		else
		{
			showHtmlWindow(player);
		}

		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	private void showHtmlWindow(L2PcInstance activeChar)
	{
		if (hasSubEmail(activeChar))
			mainHtml(activeChar);
		else
			subhtml(activeChar);

		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}

	private void sendingHtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder tb = new StringBuilder("");

		tb.append("<html><head><title>Please Wait</title></head><body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Sending Email</font>");
		tb.append("<br1><font color=\"00FF00\">" + activeChar.getName() + "</font>, please wait while we send you the email.<br1></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("</center>");
		tb.append("</body></html>");

		nhm.setHtml(tb.toString());
		activeChar.sendPacket(nhm);
	}

	void secsuccesshtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder tb = new StringBuilder("");

		tb.append("<html><head><title>Success!</title></head><body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Sending Email</font>");
		tb.append("<br1><font color=\"00FF00\">" + activeChar.getName() + "</font>, we succeed in sending you the code.<br1></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("</center>");
		tb.append("<table width=\"350\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"Icon.etc_old_key_i02\" width=\"32\" height=\"32\"></td>");
		tb.append("<td valign=\"top\">Please enter your code:<edit var=\"code\" width=80 height=15>");
		tb.append("<br1>Enter the code we sent at your email address.</td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<center>");
		tb.append("<button value=\"Submit\" action=\"bypass -h npc_" + getObjectId() + "_submitseccode $code\" width=150 height=21 back=\"sek.cbui75\" fore=\"sek.cbui75\">");
		tb.append("</center>");
		tb.append("<br>");
		tb.append("</body></html>");

		nhm.setHtml(tb.toString());
		activeChar.sendPacket(nhm);
	}

	void successchangehtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder tb = new StringBuilder("");

		tb.append("<html><head><title>Success!</title></head><body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Sending Email</font>");
		tb.append("<br1><font color=\"00FF00\">" + activeChar.getName() + "</font>, we succeed in sending you the code.<br1></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("</center>");
		tb.append("<table width=\"350\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"Icon.etc_old_key_i02\" width=\"32\" height=\"32\"></td>");
		tb.append("<td valign=\"top\">Please enter your code:<edit var=\"code\" width=80 height=15>");
		tb.append("<br1>Enter the code we sent at your email address.</td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<center>");
		tb.append("<button value=\"Submit\" action=\"bypass -h npc_" + getObjectId() + "_submitchangecode $code\" width=204 height=21 back=\"sek.cbui75\" fore=\"sek.cbui75\">");
		tb.append("</center>");
		tb.append("<br>");
		tb.append("</body></html>");

		nhm.setHtml(tb.toString());
		activeChar.sendPacket(nhm);
	}

	void successhtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder tb = new StringBuilder("");

		tb.append("<html><head><title>Success!</title></head><body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Sending Email</font>");
		tb.append("<br1><font color=\"00FF00\">" + activeChar.getName() + "</font>, we succeed in sending you the code.<br1></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("</center>");
		tb.append("<table width=\"350\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"Icon.etc_old_key_i02\" width=\"32\" height=\"32\"></td>");
		tb.append("<td valign=\"top\">Please enter your code:<edit var=\"code\" width=80 height=15>");
		tb.append("<br1>Enter the code we sent at your email address.</td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<center>");
		tb.append("<button value=\"Submit\" action=\"bypass -h npc_" + getObjectId() + "_submitcode $code\" width=204 height=21 back=\"sek.cbui75\" fore=\"sek.cbui75\">");
		tb.append("</center>");
		tb.append("<br>");
		tb.append("</body></html>");

		nhm.setHtml(tb.toString());
		activeChar.sendPacket(nhm);
	}

	private void mainHtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder tb = new StringBuilder("");

		tb.append("<html><head><title>Account manager</title></head><body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Account Manager</font>");
		tb.append("<br1><font color=\"00FF00\">" + activeChar.getName() + "</font>, use this menu for everything related to your account.<br1></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<button value=\"Change Password\" action=\"bypass -h npc_" + getObjectId() + "_generateCode\" width=204 height=21 back=\"sek.cbui75\" fore=\"sek.cbui75\"><br>");
		tb.append("<button value=\"Add Security Question\" action=\"bypass -h npc_" + getObjectId() + "_addSec\" width=204 height=21 back=\"sek.cbui75\" fore=\"sek.cbui75\"><br>");
		tb.append("<button value=\"Forgot Security Question\" action=\"bypass -h npc_" + getObjectId() + "_forgSec\" width=204 height=21 back=\"sek.cbui75\" fore=\"sek.cbui75\"><br>");
		tb.append("<button value=\"Change Security Question\" action=\"bypass -h npc_" + getObjectId() + "_changeSec\" width=204 height=21 back=\"sek.cbui75\" fore=\"sek.cbui75\"><br>");
		tb.append("<button value=\"Change Email Address\" action=\"bypass -h npc_" + getObjectId() + "_changeEmail\" width=204 height=21 back=\"sek.cbui75\" fore=\"sek.cbui75\"><br>");
		tb.append("<button value=\"Forgot My Password\" action=\"bypass -h npc_" + getObjectId() + "_forgPass\" width=204 height=21 back=\"sek.cbui75\" fore=\"sek.cbui75\"><br>");
		tb.append("</center>");

		tb.append("</body></html>");

		nhm.setHtml(tb.toString());
		activeChar.sendPacket(nhm);
	}

	public static boolean hasSubEmail(L2PcInstance player)
	{
		int hasSubEmail = -1;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT hasSubEmail FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				hasSubEmail = rset.getInt("hasSubEmail");
			}
			
			rset.close();
			statement.close();

			if (hasSubEmail == 1)
				return true;
			else if (hasSubEmail == 0)
				return false;
		}
		catch (Exception e)
		{
		}
		return false;
	}

	public static void setHasSubEmail(L2PcInstance player)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET hasSubEmail=? WHERE obj_Id=?");
			statement.setInt(1, 1);
			statement.setInt(2, player.getObjectId());
			statement.execute();
			statement.close();

		}
		catch (Exception e)
		{
		}
	}

	private void setHasSubSec(L2PcInstance player)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET hasSubSec=? WHERE obj_Id=?");
			statement.setInt(1, 1);
			statement.setInt(2, player.getObjectId());
			statement.execute();
			statement.close();

		}
		catch (Exception e)
		{
		}
	}

	public void generateCode(L2PcInstance player)
	{
		int code = Rnd.get(1000, 9999);
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET emailcode=? WHERE obj_Id=?");
			statement.setInt(1, code);
			statement.setInt(2, player.getObjectId());
			statement.execute();
			statement.close();

		}
		catch (Exception e)
		{
		}
	}

	public void generatechangeCode(L2PcInstance player)
	{
		int code = Rnd.get(1000, 9999);
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET emailchangecode=? WHERE obj_Id=?");
			statement.setInt(1, code);
			statement.setInt(2, player.getObjectId());
			statement.execute();
			statement.close();

		}
		catch (Exception e)
		{
		}
	}

	public void generateSecCode(L2PcInstance player)
	{
		int code = Rnd.get(1000, 9999);
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET secCode=? WHERE obj_Id=?");
			statement.setInt(1, code);
			statement.setInt(2, player.getObjectId());
			statement.execute();
			statement.close();

		}
		catch (Exception e)
		{
		}
	}

	public int getCode(L2PcInstance player)
	{
		int code = -1;
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT emailcode FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				code = rset.getInt("emailcode");
			}
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
		}
		return code;
	}

	public int getMailCode(L2PcInstance player)
	{
		int code = -1;
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT emailchangecode FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				code = rset.getInt("emailchangecode");
			}

			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
		}
		return code;
	}

	public int getChangeCode(L2PcInstance player)
	{
		int code = -1;
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT emailchangecode FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				code = rset.getInt("emailchangecode");
			}

			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
		}
		return code;
	}

	public int getSecCode(L2PcInstance player)
	{
		int code = -1;
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT secCode FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				code = rset.getInt("secCode");
			}

			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
		}
		return code;
	}

	public void changesechtml(L2PcInstance player)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder tb = new StringBuilder("");

		tb.append("<html><title>Change your security answer.</title>");
		tb.append("<body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Security answer.</font>");
		tb.append("<br1><font color=\"00FF00\">" + player.getName() + "</font>, you can now change your answer.<br1></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("What is your maiden mother's name?<br1>");
		tb.append("New Answer: <edit var=\"new\" width=100 height=15><br>");
		tb.append("Repeat New Answer: <edit var=\"repeatnew\" width=100 height=15><br><br>");
		tb.append("<button value=\"Change Answer\" action=\"bypass -h npc_" + getObjectId() + "_change_sec $new $repeatnew\" width=204 height=20 back=\"sek.cbui75\" fore=\"sek.cbui75\">");
		tb.append("</center></body></html>");

		nhm.setHtml(tb.toString());
		player.sendPacket(nhm);
	}

	public void forgPassHtml(L2PcInstance player)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder tb = new StringBuilder("");

		tb.append("<html><title>Forgot my password.</title>");
		tb.append("<body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Recover Password.</font>");
		tb.append("<br1><font color=\"00FF00\">" + player.getName() + "</font>, you can reset your pass here.<br1></td>");
		tb.append("</tr>");
		tb.append("</table>");
		//tb.append("Account Name: <edit var=\"acc\" width=100 height=15><br>");
		//tb.append("Character's Name: <edit var=\"cha\" width=100 height=15><br>");
		tb.append("Char's Security Answer: <edit var=\"ans\" width=100 height=15><br><br>");              //$acc $cha
		tb.append("<button value=\"Reset Password\" action=\"bypass -h npc_" + getObjectId() + "_resetPass $ans\" width=204 height=20 back=\"sek.cbui75\" fore=\"sek.cbui75\">");
		tb.append("</center></body></html>");

		nhm.setHtml(tb.toString());
		player.sendPacket(nhm);
	}

	public void changepasshtml(L2PcInstance player)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder tb = new StringBuilder("");

		tb.append("<html><title>Change your pass.</title>");
		tb.append("<body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Change Password</font>");
		tb.append("<br1><font color=\"00FF00\">" + player.getName() + "</font>, you can now change your password.<br1></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("New Password: <edit var=\"new\" width=100 height=15><br>");
		tb.append("Repeat New Password: <edit var=\"repeatnew\" width=100 height=15><br><br>");
		tb.append("<button value=\"Change Password\" action=\"bypass -h npc_" + getObjectId() + "_change_password $new $repeatnew\" width=204 height=20 back=\"sek.cbui75\" fore=\"sek.cbui75\">");
		tb.append("</center></body></html>");

		nhm.setHtml(tb.toString());
		player.sendPacket(nhm);
	}

	public void changeemailhtml(L2PcInstance player)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder tb = new StringBuilder("");

		tb.append("<html><title>Change your Email.</title>");
		tb.append("<body>");
		tb.append("<center>");
		tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
		tb.append("<tr>");
		tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
		tb.append("<td valign=\"top\"><font color=\"FF6600\">Change Email</font>");
		tb.append("<br1><font color=\"00FF00\">" + player.getName() + "</font>, you can now change your email.<br1></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("New Email: <multiedit var=\"new\" width=150 height=15><br>");
		tb.append("Repeat New Email: <multiedit var=\"repeatnew\" width=150 height=15><br><br>");
		tb.append("<button value=\"Change Email\" action=\"bypass -h npc_" + getObjectId() + "_change_email $new $repeatnew\" width=204 height=20 back=\"sek.cbui75\" fore=\"sek.cbui75\">");
		tb.append("</center></body></html>");

		nhm.setHtml(tb.toString());
		player.sendPacket(nhm);
	}

	public static boolean changePassword(String newPass, String repeatNewPass, L2PcInstance activeChar)
	{
		if (newPass.length() < 5)
		{
			activeChar.sendMessage("The new password is too short!");
			return false;
		}
		if (newPass.length() > 20)
		{
			activeChar.sendMessage("The new password is too long!");
			return false;
		}
		if (!newPass.equals(repeatNewPass))
		{
			activeChar.sendMessage("Repeated password doesn't match the new password.");
			return false;
		}

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] password = newPass.getBytes("UTF-8");
			password = md.digest(password);

			PreparedStatement statement = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?");
			statement.setString(1, Base64.getEncoder().encodeToString(password));
			statement.setString(2, activeChar.getAccountName());
			statement.execute();
			statement.close();
			activeChar.sendMessage("Congratulations! Your password has been changed succesfully.");
		}
		catch (Exception e)
		{
			_log.warning(L2AccountManagerInstance.class.getSimpleName() + ": could not update the password of account: " + activeChar.getAccountName());
		}
		return true;
	}

	public static boolean resetPass(String acc, String cha, L2PcInstance activeChar)
	{
		String answer = null;
		String email = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT answer FROM characters WHERE obj_Id=?");
			statement.setInt(1, activeChar.getObjectId());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				answer = rset.getString("answer");
				if (!ans.equalsIgnoreCase(answer))
				{
					activeChar.sendMessage("Some of your data you submitted does not fit. Please try again.");
					return false;
				}
			}
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning(L2AccountManagerInstance.class.getSimpleName() + ": could not select answer fields from characters to reset pass for " + activeChar.getAccountName());
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT email FROM characters WHERE account_name=?");
			statement.setString(1, acc);

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				email = rset.getString("email");
			}
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
		}
		
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			String newPass = Integer.toString(Rnd.get(1000000, 9999999));

			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] password = newPass.getBytes("UTF-8");
			password = md.digest(password);

			PreparedStatement statement2 = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?");
			statement2.setString(1, Base64.getEncoder().encodeToString(password));
			statement2.setString(2, acc);
			statement2.execute();
			statement2.close();

			String[] mail =
			{
				email
			};
			try
			{
				L2Emailer.sendL2Mail(mail, "Password Reset", "Your account's " + acc + " password has been reset to : " + newPass);
			}
			catch (MessagingException e)
			{
				e.printStackTrace();
			}

			activeChar.sendMessage("Congratulations! The password of the account " + acc + " has been reset succesfully. Go to your email and get your new password!");
		}
		catch (Exception e)
		{
			_log.warning(L2AccountManagerInstance.class.getSimpleName() + ": could not update the password of account: " + activeChar.getAccountName());
		}
		return true;
	}

	public static boolean changeAnswer(String newSec, String repeatNewSec, L2PcInstance activeChar)
	{
		if (newSec.length() > 50)
		{
			activeChar.sendMessage("The new answer is too long!");
			return false;
		}
		if (!newSec.equals(repeatNewSec))
		{
			activeChar.sendMessage("Repeated answer doesn't match the new answer.");
			return false;
		}

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET answer=? WHERE obj_Id=?");
			statement.setString(1, newSec);
			statement.setInt(2, activeChar.getObjectId());
			statement.execute();
			statement.close();
			activeChar.sendMessage("Congratulations! Your security answer has been changed succesfully.");
		}
		catch (Exception e)
		{
			_log.warning(L2AccountManagerInstance.class.getSimpleName() + ": could not update the answer of account: " + activeChar.getAccountName());
		}
		return true;
	}

	public static boolean changeEmail(String newMail, String repeatNewMail, L2PcInstance activeChar)
	{
		if (newMail.length() > 50)
		{
			activeChar.sendMessage("The new email is too long!");
			return false;
		}
		if (!newMail.equals(repeatNewMail))
		{
			activeChar.sendMessage("Repeated email doesn't match the new email.");
			return false;
		}

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET email=? WHERE obj_Id=?");
			statement.setString(1, newMail);
			statement.setInt(2, activeChar.getObjectId());
			statement.execute();
			statement.close();
			activeChar.sendMessage("Congratulations! Your email address has been changed succesfully to " + newMail + " .");
		}
		catch (Exception e)
		{
			_log.warning(L2AccountManagerInstance.class.getSimpleName() + ": could not update the email of account: " + activeChar.getAccountName());
		}
		return true;
	}

	public static String getEmailAddress(L2PcInstance player)
	{
		String email = null;
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT email FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				email = rset.getString("email");
			}

			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			
		}
		return email;
	}

	public void subhtml(L2PcInstance player)
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
		tb.append("<button value=\"Submit\" action=\"bypass -h npc_" + getObjectId() + "_submitemail $email1\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\">");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32 align=center>");
		tb.append("</center>");
		tb.append("</body></html>");

		html.setHtml(tb.toString());
		player.sendPacket(html);
	}
}