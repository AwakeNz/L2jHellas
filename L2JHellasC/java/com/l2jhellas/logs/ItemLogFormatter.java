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
package com.l2jhellas.logs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.l2jhellas.gameserver.model.L2ItemInstance;

/**
 * @author Advi
 */
public class ItemLogFormatter extends Formatter
{
	private static final String CRLF = "\r\n";
	private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM H:mm:ss");

	@Override
	public String format(LogRecord record)
	{
		StringBuilder output = new StringBuilder();
		output.append('[');
		output.append(dateFmt.format(new Date(record.getMillis())));
		output.append(']');
		output.append(' ');
		output.append(record.getMessage());
		for (Object p : record.getParameters())
		{
			if (p == null)
				continue;
			output.append(',');
			output.append(' ');
			if (p instanceof L2ItemInstance)
			{
				L2ItemInstance item = (L2ItemInstance) p;
				output.append("item " + item.getObjectId() + ":");
				if (item.getEnchantLevel() > 0)
					output.append("+" + item.getEnchantLevel() + " ");
				output.append(item.getItem().getItemName());
				output.append("(" + item.getCount() + ")");
			}
			// else if (p instanceof L2PcInstance)
			// output.append(((L2PcInstance)p).getName());
			else
				output.append(p.toString()/* + ":" + ((L2Object)p).getObjectId() */);
		}
		output.append(CRLF);

		return output.toString();
	}
}