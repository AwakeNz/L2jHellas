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
package com.l2jhellas.gameserver.taskmanager.tasks;

import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.entity.olympiad.Olympiad;
import com.l2jhellas.gameserver.taskmanager.Task;
import com.l2jhellas.gameserver.taskmanager.TaskManager;
import com.l2jhellas.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.l2jhellas.gameserver.taskmanager.TaskTypes;

/**
 * Updates all data of Olympiad nobles in db
 * 
 * @author godson
 */
public class TaskOlympiadSave extends Task
{
	private static final Logger _log = Logger.getLogger(TaskOlympiadSave.class.getName());
	public static final String NAME = "OlympiadSave";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		try
		{
			Olympiad.getInstance().saveOlympiadStatus();
			_log.info("Olympiad System: Data updated successfully.");
		}
		catch (Exception e)
		{
			_log.warning(TaskOlympiadSave.class.getName() + ": Olympiad System: Failed to save Olympiad configuration: ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_FIXED_SHEDULED, "900000", "1800000", "");
	}
}
