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
package com.l2jhellas.gameserver.model.entity.engines;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ThreadPoolManager;

/**
 * @author Boorinio
 */
public class EventHandlerCtf
{
	private static final Logger _log = Logger.getLogger(EventHandlerCtf.class.getName());
	public List<Long> datesCtf = new ArrayList<Long>();
	
	public void startHandler()
	{
		loadConfisCtf(false);
		getNextTimeStampCTF();
		_log.info(EventHandlerCtf.class.getSimpleName() + ": Ctf handler initiated");
	}
	
	public void loadConfisCtf(boolean NextDay)
	{
		datesCtf.clear();
		for (String times : Config.CTF_EVENT_TIMES.split(","))
		{
			String[] timesSplited = times.split(":");
			int hour = Integer.parseInt(timesSplited[0]);
			int minute = Integer.parseInt(timesSplited[1]);
			Calendar time = Calendar.getInstance();
			if (!NextDay)
			{
				time.set(Calendar.HOUR_OF_DAY, hour);
				time.set(Calendar.MINUTE, minute);
			}
			else
			{
				time.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1);
				time.set(Calendar.HOUR_OF_DAY, hour);
				time.set(Calendar.MINUTE, minute);
			}
			datesCtf.add(time.getTimeInMillis());
		}
	}
	
	public void getNextTimeStampCTF()
	{
		boolean found = false;
		for (Long stamp : datesCtf)
		{
			if (stamp > System.currentTimeMillis())
			{
				ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
				{
					@Override
					public void run()
					{
						CTF.loadData();
						CTF.autoEvent();
						getNextTimeStampCTF();
					}
				}, stamp - System.currentTimeMillis());
				found = true;
				break;
			}
		}
		if (!found)
		{
			loadConfisCtf(true);
			getNextTimeStampCTF();
		}
	}
}
