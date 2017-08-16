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
package com.l2jhellas.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.l2jhellas.gameserver.model.quest.Quest;

public class QuestManager
{
	protected static final Logger _log = Logger.getLogger(QuestManager.class.getName());

	public static final QuestManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private final List<Quest> _quests = new ArrayList<>();

	public QuestManager()
	{
		
	}

	public void addQuest(Quest quest)
	{
		_quests.add(quest);
	}

	public final Quest getQuest(String name)
	{
		for (Quest q : _quests)
			if (q.getName().equalsIgnoreCase(name))
				return q;
		
		return null;
	}

	public final Quest getQuest(int questId)
	{
		for (Quest q : _quests)
			if (q.getQuestId() == questId)
				return q;

		return null;
	}

	public void cleanQuests()
	{
		_quests.clear();;
	}

	public List<Quest> getAllManagedScripts()
	{
		return _quests;
	}

	public final void report()
	{
		_log.info(QuestManager.class.getSimpleName() + ": Loaded: " + _quests.size() + " quests.");
	}

	private static class SingletonHolder
	{
		protected static final QuestManager _instance = new QuestManager();
	}
}