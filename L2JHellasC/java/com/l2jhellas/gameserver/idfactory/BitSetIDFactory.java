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
package com.l2jhellas.gameserver.idfactory;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.util.PrimeFinder;

public class BitSetIDFactory extends IdFactory
{
	private static Logger _log = Logger.getLogger(BitSetIDFactory.class.getName());

	private BitSet _freeIds;
	private AtomicInteger _freeIdCount;
	private AtomicInteger _nextFreeId;

	public class BitSetCapacityCheck implements Runnable
	{
		@Override
		public void run()
		{
			if (reachingBitSetCapacity())
			{
				increaseBitSetCapacity();
			}
		}
	}

	protected BitSetIDFactory()
	{
		super();
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);
		initialize();
		_log.info(BitSetIDFactory.class.getSimpleName() + ": " + _freeIds.size() + " id's available.");
	}

	public synchronized void initialize()
	{
		try
		{
			_freeIds = new BitSet(PrimeFinder.nextPrime(100000));
			_freeIds.clear();
			_freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);

			for (int usedObjectId : extractUsedObjectIDTable())
			{
				int objectID = usedObjectId - FIRST_OID;
				if (objectID < 0)
				{
					_log.warning(BitSetIDFactory.class.getName() + ": Object ID " + usedObjectId + " in DB is less than minimum ID of " + FIRST_OID);
					continue;
				}
				_freeIds.set(usedObjectId - FIRST_OID);
				_freeIdCount.decrementAndGet();
			}

			_nextFreeId = new AtomicInteger(_freeIds.nextClearBit(0));
			_initialized = true;
		}
		catch (Exception e)
		{
			_initialized = false;
			_log.warning(BitSetIDFactory.class.getName() + ": BitSet ID Factory could not be initialized correctly");
			if (Config.DEVELOPER)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void releaseId(int objectID)
	{
		if ((objectID - FIRST_OID) > -1)
		{
			_freeIds.clear(objectID - FIRST_OID);
			_freeIdCount.incrementAndGet();
		}
		else
			_log.warning(BitSetIDFactory.class.getName() + ": BitSet ID Factory: release objectID " + objectID + " failed (< " + FIRST_OID + ")");
	}

	@Override
	public synchronized int getNextId()
	{
		int newID = _nextFreeId.get();
		_freeIds.set(newID);
		_freeIdCount.decrementAndGet();

		int nextFree = _freeIds.nextClearBit(newID);

		if (nextFree < 0)
		{
			nextFree = _freeIds.nextClearBit(0);
		}
		if (nextFree < 0)
		{
			if (_freeIds.size() < FREE_OBJECT_ID_SIZE)
			{
				increaseBitSetCapacity();
			}
			else
			{
				_log.warning(BitSetIDFactory.class.getName() + ": Ran out of valid Id's.");
			}
		}

		_nextFreeId.set(nextFree);

		return newID + FIRST_OID;
	}

	@Override
	public synchronized int size()
	{
		return _freeIdCount.get();
	}

	protected synchronized int usedIdCount()
	{
		return (size() - FIRST_OID);
	}

	protected synchronized boolean reachingBitSetCapacity()
	{
		return PrimeFinder.nextPrime(usedIdCount() * 11 / 10) > _freeIds.size();
	}

	protected synchronized void increaseBitSetCapacity()
	{
		BitSet newBitSet = new BitSet(PrimeFinder.nextPrime(usedIdCount() * 11 / 10));
		newBitSet.or(_freeIds);
		_freeIds = newBitSet;
	}
}