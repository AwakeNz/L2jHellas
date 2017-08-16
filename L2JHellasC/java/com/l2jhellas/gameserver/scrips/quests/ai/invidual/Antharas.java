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
package com.l2jhellas.gameserver.scrips.quests.ai.invidual;


import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.audio.Music;
import com.l2jhellas.gameserver.instancemanager.GrandBossManager;
import com.l2jhellas.gameserver.model.L2CharPosition;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.zone.type.L2BossZone;
import com.l2jhellas.gameserver.network.serverpackets.Earthquake;
import com.l2jhellas.gameserver.network.serverpackets.SpecialCamera;
import com.l2jhellas.gameserver.scrips.quests.ai.AbstractNpcAI;
import com.l2jhellas.gameserver.templates.StatsSet;
import com.l2jhellas.util.Rnd;

public class Antharas extends AbstractNpcAI
{
	
	public static final int ANTHARAS = 29019;
	
	// Antharas Status Tracking :
	public static final byte DORMANT = 0;     	// Antharas is spawned and no one has entered yet. Entry is unlocked
	public static final byte WAITING = 1;     	// Antharas is spawend and someone has entered, triggering a 30 minute window for additional people to enter
											// before he unleashes his attack. Entry is unlocked
	public static final byte FIGHTING = 2;    	// Antharas is engaged in battle, annihilating his foes. Entry is locked
	public static final byte DEAD = 3;        	// Antharas has been killed. Entry is locked
	
	private static long _LastAction = 0;
	
	private static L2BossZone _Zone;
	
	// Boss: Antharas
	public Antharas()
	{
		super("antharas", "ai");
		int[] mob =
		{
			ANTHARAS
		};
		this.registerMobs(mob);
		_Zone = GrandBossManager.getInstance().getZone(179700, 113800, -7709);
		StatsSet info = GrandBossManager.getStatsSet(ANTHARAS);
		int status = GrandBossManager.getBossStatus(ANTHARAS);
		if (status == DEAD)
		{
			// load the unlock date and time for antharas from DB
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			// if antharas is locked until a certain time, mark it so and start the unlock timer
			// the unlock time has not yet expired. Mark Antharas as currently locked. Setup a timer
			// to fire at the correct time (calculate the time between now and the unlock time,
			// setup a timer to fire after that many msec)
			if (temp > 0)
			{
				this.startQuestTimer("antharas_unlock", temp, null, null,false);
			}
			else
			{
				// the time has already expired while the server was offline. Immediately spawn antharas in his cave.
				// also, the status needs to be changed to DORMANT
				L2GrandBossInstance antharas = (L2GrandBossInstance) addSpawn(ANTHARAS, 185708, 114298, -8221, 32768, false, 0,false);
				GrandBossManager.setBossStatus(ANTHARAS, DORMANT);
				antharas.broadcastPacket(new Earthquake(185708, 114298, -8221, 20, 10));
				GrandBossManager.addBoss(antharas);
			}
		}
		else
		{
			int loc_x = info.getInteger("loc_x");
			int loc_y = info.getInteger("loc_y");
			int loc_z = info.getInteger("loc_z");
			int heading = info.getInteger("heading");
			int hp = info.getInteger("currentHP");
			int mp = info.getInteger("currentMP");
			L2GrandBossInstance antharas = (L2GrandBossInstance) addSpawn(ANTHARAS, loc_x, loc_y, loc_z, heading, false, 0,false);
			GrandBossManager.addBoss(antharas);
			antharas.setCurrentHpMp(hp, mp);
			if (status == WAITING)
			{
				// Start timer to lock entry after 30 minutes
				this.startQuestTimer("waiting", Config.Antharas_Wait_Time, antharas, null,false);
			}
			else if (status == FIGHTING)
			{
				_LastAction = System.currentTimeMillis();
				// Start repeating timer to check for inactivity
				this.startQuestTimer("antharas_despawn", 60000, antharas, null, true);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc != null)
		{
			long temp = 0;
			if (event.equalsIgnoreCase("waiting"))
			{
				npc.teleToLocation(185452, 114835, -8221);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(181911, 114835, -7678, 0));
				this.startQuestTimer("antharas_has_arrived", 2000, npc, null, true);
				npc.broadcastPacket(Music.BS02_A_10000.getPacket());
				GrandBossManager.setBossStatus(ANTHARAS, FIGHTING);
			}
			else if (event.equalsIgnoreCase("camera_1"))
			{
				this.startQuestTimer("camera_2", 3000, npc, null,false);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 13, -19, 0, 20000));
			}
			else if (event.equalsIgnoreCase("camera_2"))
			{
				this.startQuestTimer("camera_3", 10000, npc, null,false);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 13, 0, 6000, 20000));
			}
			else if (event.equalsIgnoreCase("camera_3"))
			{
				this.startQuestTimer("camera_4", 200, npc, null,false);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 3700, 0, -3, 0, 10000));
			}
			else if (event.equalsIgnoreCase("camera_4"))
			{
				this.startQuestTimer("camera_5", 10800, npc, null,false);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 0, -3, 22000, 30000));
			}
			else if (event.equalsIgnoreCase("camera_5"))
			{
				this.startQuestTimer("antharas_despawn", 60000, npc, null, true);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 0, -3, 300, 7000));
				_LastAction = System.currentTimeMillis();
			}
			else if (event.equalsIgnoreCase("antharas_despawn"))
			{
				temp = (System.currentTimeMillis() - _LastAction);
				if (temp > 900000)
				{
					npc.teleToLocation(185708, 114298, -8221);
					GrandBossManager.setBossStatus(ANTHARAS, DORMANT);
					npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
					_Zone.oustAllPlayers();
					this.cancelQuestTimer("antharas_despawn", npc, null);
				}
			}
			else if (event.equalsIgnoreCase("antharas_has_arrived"))
			{
				int dx = Math.abs(npc.getX() - 181911);
				int dy = Math.abs(npc.getY() - 114835);
				if (dx <= 50 && dy <= 50)
				{
					this.startQuestTimer("camera_1", 2000, npc, null,false);
					npc.getSpawn().setLocx(181911);
					npc.getSpawn().setLocy(114835);
					npc.getSpawn().setLocz(-7678);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
					this.getQuestTimer("antharas_has_arrived", npc, null);
				}
				else
				{
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(181911, 114835, -7678, 0));
				}
			}
			else if (event.equalsIgnoreCase("spawn_cubes"))
			{
				addSpawn(31859, 177615, 114941, -7709, 0, false, 900000,false);
				int radius = 1500;
				for (int i = 0; i < 20; i++)
				{
					int x = (int) (radius * Math.cos(i * .331)); // .331~2pi/19
					int y = (int) (radius * Math.sin(i * .331));
					addSpawn(31859, 177615 + x, 114941 + y, -7709, 0, false, 900000,false);
				}
				this.cancelQuestTimer("antharas_despawn", npc, null);
				this.startQuestTimer("remove_players", 900000, null, null,false);
			}
		}
		else
		{
			if (event.equalsIgnoreCase("antharas_unlock"))
			{
				L2GrandBossInstance antharas = (L2GrandBossInstance) addSpawn(ANTHARAS, 185708, 114298, -8221, 32768, false, 0,false);
				GrandBossManager.addBoss(antharas);
				GrandBossManager.setBossStatus(ANTHARAS, DORMANT);
				antharas.broadcastPacket(new Earthquake(185708, 114298, -8221, 20, 10));
			}
			else if (event.equalsIgnoreCase("remove_players"))
			{
				_Zone.oustAllPlayers();
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (GrandBossManager.getBossStatus(ANTHARAS) != FIGHTING)
		{
			_Zone.oustAllPlayers();
			return null;
		}
		_LastAction = System.currentTimeMillis();
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getNpcId() == ANTHARAS)
		{		
			npc.broadcastPacket(Music.BS01_D_10000.getPacket());
			this.startQuestTimer("spawn_cubes", 10000, npc, null,false);
			GrandBossManager.setBossStatus(ANTHARAS, DEAD);
			long respawnTime = (Config.Interval_Of_Antharas_Spawn + Rnd.get(Config.Random_Of_Antharas_Spawn)* 3600000);
			this.startQuestTimer("antharas_unlock", respawnTime, null, null,false);
			// also save the respawn time so that the info is maintained past reboots
			StatsSet info = GrandBossManager.getStatsSet(ANTHARAS);
			info.set("respawn_time", (System.currentTimeMillis() + respawnTime));
			GrandBossManager.setStatsSet(ANTHARAS, info);
		}
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new Antharas();
	}
}