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
package com.l2jhellas.gameserver.Threads;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.l2jhellas.util.ArrayUtils;

/**
 * @author ProGramMoS
 */
public final class RunnableStatsManager
{
	protected static final Map<Class<?>, ClassStat> _classStats = new HashMap<Class<?>, ClassStat>();

	private static final class ClassStat
	{
		private String[] _methodNames = new String[0];
		private MethodStat[] _methodStats = new MethodStat[0];

		protected ClassStat(Class<?> clazz)
		{
			_classStats.put(clazz, this);
		}

		protected MethodStat getMethodStat(String methodName, boolean synchronizedAlready)
		{
			for (int i = 0; i < _methodNames.length; i++)
				if (_methodNames[i].equals(methodName))
					return _methodStats[i];

			if (!synchronizedAlready)
			{
				synchronized (this)
				{
					return getMethodStat(methodName, true);
				}
			}

			methodName = methodName.intern();

			final MethodStat methodStat = new MethodStat();

			_methodNames = ArrayUtils.add(_methodNames, methodName);
			_methodStats = ArrayUtils.add(_methodStats, methodStat);

			return methodStat;
		}
	}

	protected static final class MethodStat
	{
		private final ReentrantLock _lock = new ReentrantLock();

		private long _min = Long.MAX_VALUE;
		private long _max = Long.MIN_VALUE;

		protected void handleStats(long runTime)
		{
			_lock.lock();
			try
			{
				_min = Math.min(_min, runTime);
				_max = Math.max(_max, runTime);
			}
			finally
			{
				_lock.unlock();
			}
		}
	}

	private static ClassStat getClassStat(Class<?> clazz, boolean synchronizedAlready)
	{
		ClassStat classStat = _classStats.get(clazz);

		if (classStat != null)
			return classStat;

		if (!synchronizedAlready)
		{
			synchronized (RunnableStatsManager.class)
			{
				return getClassStat(clazz, true);
			}
		}

		return new ClassStat(clazz);
	}

	public static void handleStats(Class<? extends Runnable> clazz, long runTime)
	{
		handleStats(clazz, "run()", runTime);
	}

	public static void handleStats(Class<?> clazz, String methodName, long runTime)
	{
		getClassStat(clazz, false).getMethodStat(methodName, false).handleStats(runTime);
	}
}