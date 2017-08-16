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
package com.l2jhellas.gameserver;

import java.util.Collection;
import java.util.logging.Logger;

import Extensions.RankSystem.PvpTable;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.controllers.GameTimeController;
import com.l2jhellas.gameserver.controllers.TradeController;
import com.l2jhellas.gameserver.instancemanager.CastleManorManager;
import com.l2jhellas.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jhellas.gameserver.instancemanager.FourSepulchersManager;
import com.l2jhellas.gameserver.instancemanager.GrandBossManager;
import com.l2jhellas.gameserver.instancemanager.ItemsOnGroundManager;
import com.l2jhellas.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.entity.Hero;
import com.l2jhellas.gameserver.model.entity.olympiad.Olympiad;
import com.l2jhellas.gameserver.network.L2GameClient;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.gameserverpackets.ServerStatus;
import com.l2jhellas.gameserver.network.serverpackets.ServerClose;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;
import com.l2jhellas.util.Broadcast;
import com.l2jhellas.util.Util;
import com.l2jhellas.util.database.L2DatabaseFactory;

/**
 * This class provides functions for shutting down and restarting the server. It closes all client connections and saves data.
 */
public class Shutdown extends Thread
{
	private static Logger _log = Logger.getLogger(Shutdown.class.getName());
	private static Shutdown _counterInstance = null;
	private static Shutdown _instance;
	private int _secondsShut;
	private int _shutdownMode;
	
	public static final int SIGTERM = 0;
	public static final int GM_SHUTDOWN = 1;
	public static final int GM_RESTART = 2;
	public static final int ABORT = 3;
	private static final String[] MODE_TEXT =
	{
	"SIGTERM", "shutting down", "restarting", "aborting"
	};
	
	public void autoRestart(int time)
	{
		_secondsShut = time;
		countdown();
		_shutdownMode = GM_RESTART;
		_instance.setMode(GM_RESTART);
		System.exit(2);
	}
	
	private static void SendServerQuit(int seconds)
	{
		SystemMessage sysm = SystemMessage.getSystemMessage(SystemMessageId.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS);
		sysm.addNumber(seconds);
		Broadcast.toAllOnlinePlayers(sysm);
	}
	
	/**
	 * Default constructor is only used internal to create the shutdown-hook instance
	 */
	protected Shutdown()
	{
		_secondsShut = -1;
		_shutdownMode = SIGTERM;
	}
	
	/**
	 * This creates a countdown instance of Shutdown.
	 * 
	 * @param seconds
	 *        how many seconds until shutdown
	 * @param restart
	 *        true is the server shall restart after shutdown
	 */
	public Shutdown(int seconds, boolean restart)
	{
		if (seconds < 0)
			seconds = 0;
		
		_secondsShut = seconds;
		
		if (restart)
			_shutdownMode = GM_RESTART;
		else
			_shutdownMode = GM_SHUTDOWN;
	}
	
	/**
	 * get the shutdown-hook instance the shutdown-hook instance is created by the first call of this function, but it has to be registrered externaly.
	 * 
	 * @return instance of Shutdown, to be used as shutdown hook
	 */
	public static Shutdown getInstance()
	{
		return SingletonHolder._instance;
	}
	
	/**
	 * this function is called, when a new thread starts if this thread is the thread of getInstance, then this is the shutdown hook and we save all data and disconnect all
	 * clients. after this thread ends, the server will completely exit if this is not the thread of getInstance, then this is a
	 * countdown thread. we start the countdown, and when we finished it, and it was not aborted, we tell the shutdown-hook why we call exit, and then call exit when the exit
	 * status of the server is 1, startServer.sh / startServer.bat will restart the server.
	 */
	@Override
	public void run()
	{
		if (this == SingletonHolder._instance)
		{
			TimeCounter tc = new TimeCounter();
			TimeCounter tc1 = new TimeCounter();
			
			Util.printSection("Under " + MODE_TEXT[_shutdownMode] + " process");

			// disconnect players
			try
			{
				disconnectAllCharacters();
				_log.info(Shutdown.class.getSimpleName() + ": All players have been disconnected.(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			catch (Throwable t)
			{
			}
			
			// ensure all services are stopped
			try
			{
				GameTimeController.getInstance().stopTimer();
				_log.info(Shutdown.class.getSimpleName() + ": Services have been stopped.(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			catch (Throwable t)
			{
			}
			
			// stop all threadpolls
			try
			{
				ThreadPoolManager.getInstance().shutdown();
				_log.info(Shutdown.class.getSimpleName() + ": Threads have been shutdown.(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			catch (Throwable t)
			{
			}
			
			try
			{
				LoginServerThread.getInstance().interrupt();
				_log.info(Shutdown.class.getSimpleName() + ": Disconnect from login.(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			catch (Throwable t)
			{
			}
			
			// Seven Signs data is now saved along with Festival data.
			if (!SevenSigns.getInstance().isSealValidationPeriod())
				SevenSignsFestival.getInstance().saveFestivalData(false);
			
			// Save Seven Signs data && status.
			
			// Save Seven Signs data before closing. :)
			SevenSigns.getInstance().saveSevenSignsData(null, true);
			
			_log.info(Shutdown.class.getSimpleName() + ": Seven Signs Festival, general data && status have been saved in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Four Sepulchers, stop any working task.
			FourSepulchersManager.getInstance().stop();
			
			// Save raidbosses status
			RaidBossSpawnManager.getInstance().cleanUp();
			_log.info(Shutdown.class.getSimpleName() + ": Raid Bosses data have been saved in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Save grandbosses status
			GrandBossManager.getInstance().cleanUp();
			_log.info(Shutdown.class.getSimpleName() + ": World Bosses data have been saved in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Save TradeController
			_log.info(Shutdown.class.getSimpleName() + ": TradeController is save data in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			TradeController.getInstance().dataCountStore();
			_log.info(Shutdown.class.getSimpleName() + ": All items have been saved in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Save olympiads
			Olympiad.getInstance().saveOlympiadStatus();
			_log.info(Shutdown.class.getSimpleName() + ": Olympiad data has been saved in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Save Hero data
			Hero.getInstance().shutdown();
			_log.info(Shutdown.class.getSimpleName() + ": Hero data has been saved in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Save all manor data
			CastleManorManager.getInstance().save();
			_log.info(Shutdown.class.getSimpleName() + ": Manor Data saved in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			CursedWeaponsManager.getInstance().saveData();
			_log.info(Shutdown.class.getSimpleName() + ": Cursed weapons Data saved in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			// Rank PvP System by Masterio:
			if (Config.RANK_PVP_SYSTEM_ENABLED)
			{
				int[] up = PvpTable.getInstance().updateDB();
				
				if(up[0] == 0)
				{
					_log.info("PvpTable: Data saved ["+up[1]+" inserts and "+up[2]+" updates] in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
				}
			}
			
			// Save items on ground before closing
			if (Config.SAVE_DROPPED_ITEM)
			{
				ItemsOnGroundManager.getInstance().saveInDb();
				ItemsOnGroundManager.getInstance().cleanUp();
				_log.info(Shutdown.class.getSimpleName() + ": All items on ground saved in (" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			_log.info(Shutdown.class.getSimpleName() + ": Data saved.(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			
			try
			{
				Thread.sleep(5000);
			}
			catch (InterruptedException e)
			{
			}
			
			try
			{
				GameServer.gameServer.getSelectorThread().shutdown();
			}
			catch (Throwable t)
			{
			}
			
			try
			{
				L2DatabaseFactory.getInstance().shutdown();
				_log.info(Shutdown.class.getSimpleName() + ": Database connection shutdown successfull.(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
			}
			catch (Throwable t)
			{
				
			}
			_log.info(Shutdown.class.getSimpleName() + ": The server has been successfully shut down in " + tc1.getEstimatedTime() + " ms.");
			
			// server will quit, when this function ends.
			if (SingletonHolder._instance._shutdownMode == GM_RESTART)
				Runtime.getRuntime().halt(2);
			else
				Runtime.getRuntime().halt(0);
		}
		else
		{
			// shutdown: send warnings and then call exit to start shutdown sequence
			countdown();
			
			switch (_shutdownMode)
			{
				case GM_SHUTDOWN:
					SingletonHolder._instance.setMode(GM_SHUTDOWN);
					SingletonHolder._instance.run();
					System.exit(0);
				break;
				case GM_RESTART:
					SingletonHolder._instance.setMode(GM_RESTART);
					SingletonHolder._instance.run();
					System.exit(2);
				break;
			}
		}
	}
	
	/**
	 * This functions starts a shutdown countdown.<br>
	 * A choice must be made between activeChar or ghostEntity.
	 * 
	 * @param activeChar
	 *        GM who issued the shutdown command
	 * @param ghostEntity
	 *        the entity who issued the shutdown command
	 * @param seconds
	 *        seconds until shutdown
	 * @param restart
	 *        true if the server will restart after shutdown
	 */
	public void startShutdown(L2PcInstance activeChar, String ghostEntity, int seconds, boolean restart)
	{
		if (restart)
			_shutdownMode = GM_RESTART;
		else
			_shutdownMode = GM_SHUTDOWN;
		
		if (activeChar != null)
			_log.warning(Shutdown.class.getName() + ": GM: " + activeChar.getName() + " (" + activeChar.getObjectId() + ") issued shutdown command, " + MODE_TEXT[_shutdownMode] + " in " + seconds + " seconds.");
		else if (!ghostEntity.isEmpty())
			_log.warning(Shutdown.class.getName() + ": Entity: " + ghostEntity + " issued shutdown command, " + MODE_TEXT[_shutdownMode] + " in " + seconds + " seconds.");
		
		if (_shutdownMode > 0)
		{
			switch (seconds)
			{
				case 540:
				case 480:
				case 420:
				case 360:
				case 300:
				case 240:
				case 180:
				case 120:
				case 60:
				case 30:
				case 10:
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
				break;
				default:
					SendServerQuit(seconds);
			}
		}
		
		if (_counterInstance != null)
			_counterInstance._abort();
		
		// the main instance should only run for shutdown hook, so we start a new instance
		_counterInstance = new Shutdown(seconds, restart);
		_counterInstance.start();
	}
	
	/**
	 * This function aborts a running countdown
	 * 
	 * @param activeChar
	 *        GM who issued the abort command
	 */
	public void abort(L2PcInstance activeChar)
	{
		if (_counterInstance != null)
		{
			if (activeChar != null)
				_log.warning(Shutdown.class.getName() + ": GM: " + activeChar.getName() + " (" + activeChar.getObjectId() + ") issued shutdown abort, " + MODE_TEXT[_shutdownMode] + " has been stopped.");
			else
				_log.warning(Shutdown.class.getName() + ": ControlPanel issued shutdown abort, " + MODE_TEXT[_shutdownMode] + " has been stopped.");
			_counterInstance._abort();
			
			Announcements.getInstance().announceToAll("Server aborts " + MODE_TEXT[_shutdownMode] + " and continues normal operation.");
		}
	}
	
	/**
	 * set the shutdown mode
	 * 
	 * @param mode
	 *        what mode shall be set
	 */
	private void setMode(int mode)
	{
		_shutdownMode = mode;
	}
	
	/**
	 * set shutdown mode to ABORT
	 */
	private void _abort()
	{
		_shutdownMode = ABORT;
	}
	
	/**
	 * this counts the countdown and reports it to all players countdown is aborted if mode changes to ABORT
	 */
	private void countdown()
	{
		try
		{
			while (_secondsShut > 0)
			{
				switch (_secondsShut)
				{
					case 540:
						SendServerQuit(540);
					break;
					case 480:
						SendServerQuit(480);
					break;
					case 420:
						SendServerQuit(420);
					break;
					case 360:
						SendServerQuit(360);
					break;
					case 300:
						SendServerQuit(300);
					break;
					case 240:
						SendServerQuit(240);
					break;
					case 180:
						SendServerQuit(180);
					break;
					case 120:
						SendServerQuit(120);
					break;
					case 60:
						// avoids new players from logging in
						LoginServerThread.getInstance().setServerStatus(ServerStatus.STATUS_DOWN);
						SendServerQuit(60);
					break;
					case 30:
						SendServerQuit(30);
					break;
					case 10:
						SendServerQuit(10);
					break;
					case 5:
						SendServerQuit(5);
					break;
					case 4:
						SendServerQuit(4);
					break;
					case 3:
						SendServerQuit(3);
					break;
					case 2:
						SendServerQuit(2);
					break;
					case 1:
						SendServerQuit(1);
					break;
				}
				
				_secondsShut--;
				
				Thread.sleep(1000);
				
				if (_shutdownMode == ABORT)
				{
					// Rehabilitate previous server status if shutdown is aborted.
					if (LoginServerThread.getInstance().getServerStatus() == ServerStatus.STATUS_DOWN)
						LoginServerThread.getInstance().setServerStatus((Config.SERVER_GMONLY) ? ServerStatus.STATUS_GM_ONLY : ServerStatus.STATUS_AUTO);
					
					break;
				}
			}
		}
		catch (InterruptedException e)
		{
		}
	}
	
	/**
	 * A simple class used to track down the estimated time of method
	 * executions.
	 * Once this class is created, it saves the start time, and when you want to
	 * get
	 * the estimated time, use the getEstimatedTime() method.
	 */
	private static final class TimeCounter
	{
		private long _startTime;
		
		private TimeCounter()
		{
			restartCounter();
		}
		
		private void restartCounter()
		{
			_startTime = System.currentTimeMillis();
		}
		
		private long getEstimatedTimeAndRestartCounter()
		{
			final long toReturn = System.currentTimeMillis() - _startTime;
			restartCounter();
			return toReturn;
		}
		
		private long getEstimatedTime()
		{
			return System.currentTimeMillis() - _startTime;
		}
	}
	
	/**
	 * Disconnects all clients from the server
	 */
	private static void disconnectAllCharacters()
	{
		final Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();
		for (L2PcInstance player : pls)
		{
			if (player == null)
				continue;
			
			// Logout Character
			try
			{
				final L2GameClient client = player.getClient();
				if (client != null)
				{
					player.store();
					client.close(ServerClose.STATIC_PACKET);
					client.setActiveChar(null);
					player.setClient(null);
				}
				player.deleteMe();
			}
			catch (Throwable t)
			{
				_log.warning(Shutdown.class.getSimpleName() + ": Failed to logout chararacter: " + player);
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final Shutdown _instance = new Shutdown();
	}
}