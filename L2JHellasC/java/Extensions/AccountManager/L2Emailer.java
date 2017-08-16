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
package Extensions.AccountManager;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.l2jhellas.Config;

public class L2Emailer
{
	protected final static Logger log = Logger.getLogger(L2Emailer.class.getName());
	
	public static void sendL2Mail(String[] towho, String sub, String text) throws MessagingException
	{
		String host = "smtp.gmail.com";
		String from = Config.EMAIL_USER;
		String pass = Config.EMAIL_PASS;
		String[] email = towho;
		String subject = sub;
		String content = text;

		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));

		InternetAddress[] toAddress = new InternetAddress[email.length];

		for (int i = 0; i < email.length; i++)
		{
			toAddress[i] = new InternetAddress(email[i]);
		}

		for (int i = 0; i < toAddress.length; i++)
		{
			message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		}

		message.setSubject(subject);
		message.setText(content);
		Transport transport = session.getTransport("smtp");
		transport.connect(host, from, pass);
		transport.sendMessage(message, message.getAllRecipients());
		if (Config.DEBUG)
			log.log(Level.WARNING, "An email was successfully sent to " + toAddress);
		transport.close();
	}
}