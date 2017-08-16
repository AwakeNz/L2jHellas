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
package com.l2jhellas.gameserver.network;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.LoginServerThread;
import com.l2jhellas.gameserver.LoginServerThread.SessionKey;
import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.communitybbs.Manager.RegionBBSManager;
import com.l2jhellas.gameserver.model.CharSelectInfoPackage;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.entity.L2Event;
import com.l2jhellas.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jhellas.gameserver.network.serverpackets.ServerClose;
import com.l2jhellas.gameserver.network.serverpackets.UserInfo;
import com.l2jhellas.gameserver.skills.SkillTable;
import com.l2jhellas.mmocore.network.MMOClient;
import com.l2jhellas.mmocore.network.MMOConnection;
import com.l2jhellas.util.EventData;
import com.l2jhellas.util.database.L2DatabaseFactory;

/**
 * Represents a client connected on Game Server
 * 
 * @author KenM
 */
public final class L2GameClient extends MMOClient<MMOConnection<L2GameClient>>
{
	protected static final Logger _log = Logger.getLogger(L2GameClient.class.getName());

	/**
	 * CONNECTED - client has just connected
	 * AUTHED - client has authed but doesn't has character attached to it yet
	 * IN_GAME - client has selected a char and is in game
	 * 
	 * @author KenM
	 */
	public static enum GameClientState
	{/** @formatter:off */
		CONNECTED,
		AUTHED,
		IN_GAME
	};/** @formatter:on */

	public GameClientState state;

	// Info
	public String accountName;
	public SessionKey sessionId;
	public L2PcInstance activeChar;
	private final ReentrantLock _activeCharLock = new ReentrantLock();

	private boolean _isAuthedGG;
	private final long _connectionStartTime;
	private final List<Integer> _charSlotMapping = new ArrayList<Integer>();

	// Task
	protected final ScheduledFuture<?> _autoSaveInDB;
	protected ScheduledFuture<?> _cleanupTask = null;

	// Crypt
	public GameCrypt crypt;

	private boolean _isDetached = false;

	public L2GameClient(MMOConnection<L2GameClient> con)
	{
		super(con);
		state = GameClientState.CONNECTED;
		_connectionStartTime = System.currentTimeMillis();
		crypt = new GameCrypt();
		_autoSaveInDB = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoSaveTask(), 300000L, 900000L);
	}
 
	public byte[] enableCrypt()
	{
		byte[] key = BlowFishKeygen.getRandomKey();
		crypt.setKey(key);
		return key;
	}

	public GameClientState getState()
	{
		return state;
	}

	public void setState(GameClientState pState)
	{
		state = pState;
	}

	public long getConnectionStartTime()
	{
		return _connectionStartTime;
	}

	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		crypt.decrypt(buf.array(), buf.position(), size);
		return true;
	}

	@Override
	public boolean encrypt(final ByteBuffer buf, final int size)
	{
		crypt.encrypt(buf.array(), buf.position(), size);
		buf.position(buf.position() + size);
		return true;
	}

	public L2PcInstance getActiveChar()
	{
		return activeChar;
	}

	public void setActiveChar(L2PcInstance pActiveChar)
	{
		activeChar = pActiveChar;
		if (activeChar != null)
		{
			L2World.getInstance().storeObject(getActiveChar());
		}
	}

	public ReentrantLock getActiveCharLock()
	{
		return _activeCharLock;
	}

	public void setGameGuardOk(boolean val)
	{
		_isAuthedGG = val;
	}

	public void setAccountName(String pAccountName)
	{
		accountName = pAccountName;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setSessionId(SessionKey sk)
	{
		sessionId = sk;
	}

	public SessionKey getSessionId()
	{
		return sessionId;
	}

	public void sendPacket(L2GameServerPacket gsp)
	{
		if (_isDetached)
			return;
		getConnection().sendPacket(gsp);
		gsp.runImpl();
	}

	public boolean isDetached()
	{
		return _isDetached;
	}

	public void isDetached(boolean b)
	{
		_isDetached = b;
	}

	public L2PcInstance markToDeleteChar(int charslot) throws Exception
	{
		int objid = getObjectIdForSlot(charslot);
		if (objid < 0)
			return null;

		L2PcInstance character = L2PcInstance.load(objid);
		if (character.getClanId() != 0)
			return character;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_Id=?");
			statement.setLong(1, System.currentTimeMillis() + Config.DELETE_DAYS * 86400000L); // 24*60*60*1000 = 86400000
			statement.setInt(2, objid);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warning(L2GameClient.class.getName() + ": Data error on update delete time of char: ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
		return null;
	}

	public L2PcInstance deleteChar(int charslot) throws Exception
	{
		int objid = getObjectIdForSlot(charslot);
		if (objid < 0)
			return null;

		L2PcInstance character = L2PcInstance.load(objid);
		if (character.getClanId() != 0)
			return character;

		deleteCharByObjId(objid);
		return null;
	}

	/**
	 * Save the L2PcInstance to the database.
	 */
	public static void saveCharToDisk(L2PcInstance cha)
	{
		try
		{
			cha.store();
		}
		catch (Exception e)
		{
			_log.severe("Error saving player character: ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	public void markRestoredChar(int charslot) throws Exception
	{
		int objid = getObjectIdForSlot(charslot);
		if (objid < 0)
			return;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_Id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.severe("Data error on restoring char: ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	public static void deleteCharByObjId(int objid)
	{
		if (objid < 0)
			return;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;

			statement = con.prepareStatement("DELETE FROM character_friends WHERE char_id=? OR friend_id=?");
			statement.setInt(1, objid);
			statement.setInt(2, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_macroses WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_recipebook WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM heroes WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM olympiad_nobles WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM seven_signs WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=?)");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM augmentations WHERE item_id IN (SELECT object_id FROM items WHERE items.owner_id=?)");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM items WHERE owner_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM merchant_lease WHERE player_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM characters WHERE obj_Id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warning(L2GameClient.class.getSimpleName() + ": Data error on deleting char: ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}

	public L2PcInstance loadCharFromDisk(int charslot)
	{
		
		final int objId = getObjectIdForSlot(charslot);
		if (objId < 0)
			return null;
		
		L2PcInstance character = L2World.getInstance().getPlayer(objId);
		if (character != null)
		{
			// double login exploit prevention
			_log.severe("Attempt of double login: " + character.getName() + "(" + objId + ") " + getAccountName());
			if (character.getClient() != null)
				character.getClient().closeNow();
			else
				character.deleteMe();
			
			return null;
		}
		
		character = L2PcInstance.load(getObjectIdForSlot(charslot));

		if (character != null)
		{
			// Print some values for each login
			character.setRunning();	// running is default
			character.standUp(); // standing is default

			character.refreshOverloaded();
			character.refreshExpertisePenalty();
			character.sendPacket(new UserInfo(character));
			character.broadcastKarma();
			character.setOnlineStatus(true);
		}
		else
		{
			_log.severe("could not restore in slot: " + charslot);
		}
		return character;
	}

	/**
	 * @param chars
	 */
	public void setCharSelection(CharSelectInfoPackage[] chars)
	{
		_charSlotMapping.clear();

		for (int i = 0; i < chars.length; i++)
		{
			int objectId = chars[i].getObjectId();
			_charSlotMapping.add(Integer.valueOf(objectId));
		}
	}

	 public void close(L2GameServerPacket gsp)
	 {
	   getConnection().close(gsp);
	 }
	/**
	 * @param charslot
	 * @return
	 */
	private int getObjectIdForSlot(int charslot)
	{
		if (charslot < 0 || charslot >= _charSlotMapping.size())
		{
			_log.warning(L2GameClient.class.getName() + ": " + toString() + " tried to delete Character in slot " + charslot + " but no characters exits at that slot.");
			return -1;
		}
		Integer objectId = _charSlotMapping.get(charslot);
		return objectId.intValue();
	}

	@Override
	protected void onForcedDisconnection()
	{
		_log.info("Client " + toString() + " disconnected abnormally.");
	}

	@Override
	protected void onDisconnection()
	{
		// no long running tasks here, do it async
		try
		{
			ThreadPoolManager.getInstance().executeTask(new DisconnectTask());
		}
		catch (RejectedExecutionException e)
		{
			// server is closing
		}
	}
	
	public void closeNow()
	{	  
		 _isDetached = true; // prevents more packets execution
		  close(ServerClose.STATIC_PACKET);
		  synchronized (this)
		  {
		   if (_cleanupTask != null)
		   {
		    cancelCleanup();
		   }
		   _cleanupTask = ThreadPoolManager.getInstance().scheduleGeneral(new CleanupTask(), 0); // instant
		  }
	}
	
	private boolean cancelCleanup()
	 {
	  final Future<?> task = _cleanupTask;
	  if (task != null)
	  {
	   _cleanupTask = null;
	   return task.cancel(true);
	  }
	  return false;
	 }
	/**
	 * Produces the best possible string representation of this client.
	 */
	@Override
	public String toString()
	{
		try
		{
			InetAddress address = getConnection().getInetAddress();
			switch (getState())
			{
				case CONNECTED:
					return "[IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				case AUTHED:
					return "[Account: " + getAccountName() + " - IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				case IN_GAME:
					return "[Character: " + (getActiveChar() == null ? "disconnected" : getActiveChar().getName()) + " - Account: " + getAccountName() + " - IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				default:
					throw new IllegalStateException("Missing state on switch");
			}
		}
		catch (NullPointerException e)
		{
			return "[Character read failed due to disconnect]";
		}
	}

	class DisconnectTask implements Runnable
	{
		@Override
		public void run()
		{
			boolean fast = true;

			try
			{		
				L2PcInstance player = L2GameClient.this.getActiveChar();
				if (player != null && !player.getClient().isDetached())
				{
					player.getClient().isDetached(true);
					fast = !player.isInCombat();
				}
				cleanMe(fast);
			}
			catch (Exception e1)
			{
				_log.warning(L2GameClient.class.getSimpleName() + ": Error while disconnecting client.");
			}
		}
	}

	public void cleanMe(boolean fast)
	{
		try
		{
			synchronized (this)
			{

				if (_cleanupTask == null)
				{
					_cleanupTask = ThreadPoolManager.getInstance().scheduleGeneral(new CleanupTask(), fast ? 5 : 15000L);
				}
			}
		}
		catch (Exception e1)
		{
			_log.warning(L2GameClient.class.getSimpleName() + ": Error during cleanup.");
		}
	}

	class CleanupTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				// Update BBS
				try
				{
					RegionBBSManager.getInstance().changeCommunityBoard();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				// we are going to manually save the char bellow thus we can force the cancel
				if (_autoSaveInDB != null)
				{
					_autoSaveInDB.cancel(true);
				}

				L2PcInstance player = L2GameClient.this.getActiveChar();
				if (player != null) // this should only happen on connection loss
				{
					// we store all data from players who are disconnected while in an event in order to restore it in the next login
					if (player.atEvent)
					{
						EventData data = new EventData(player.eventX, player.eventY, player.eventZ, player.eventkarma, player.eventpvpkills, player.eventpkkills, player.eventTitle, player.kills, player.eventSitForced);
						L2Event.connectionLossData.put(player.getName(), data);
					}
					if (player.isFlying())
					{
						player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
					}
					// to prevent call cleanMe() again
					isDetached(false);
					// notify the world about our disconnect
					player.deleteMe();

					try
					{
						saveCharToDisk(player);
					}
					catch (Exception e2)
					{
						/* ignore any problems here */
					}
				}
				L2GameClient.this.setActiveChar(null);
			}
			catch (Exception e1)
			{
				_log.warning(L2GameClient.class.getSimpleName() + ": Error while cleanup client.");
			}
			finally
			{
				LoginServerThread.getInstance().sendLogout(L2GameClient.this.getAccountName());
			}
		}
	}

	class AutoSaveTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				L2PcInstance player = L2GameClient.this.getActiveChar();
				if (player != null)
				{
					saveCharToDisk(player);
				}
			}
			catch (Throwable e)
			{
				_log.severe(e.toString());
			}
		}
	}

	public boolean isAuthedGG()
	{
		return _isAuthedGG;
	}
}