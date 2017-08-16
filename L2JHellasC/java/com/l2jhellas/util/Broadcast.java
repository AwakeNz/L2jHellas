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
package com.l2jhellas.util;

import com.l2jhellas.gameserver.instancemanager.ZoneManager;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.L2WorldRegion;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.zone.L2ZoneType;
import com.l2jhellas.gameserver.network.clientpackets.Say2;
import com.l2jhellas.gameserver.network.serverpackets.CharInfo;
import com.l2jhellas.gameserver.network.serverpackets.CreatureSay;
import com.l2jhellas.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jhellas.gameserver.network.serverpackets.RelationChanged;

public final class Broadcast
{
	/**
	 * Send a packet to all L2PcInstance in the _KnownPlayers of the L2Character that have the Character targetted.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the L2Character, server just need to go through _knownPlayers to send Server->Client Packet<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * <BR>
	 * @param character The character to make checks on.
	 * @param mov The packet to send.
	 */
	public static void toPlayersTargettingMyself(L2Character character, L2GameServerPacket mov)
	{
		for (L2PcInstance player : character.getKnownList().getKnownPlayers().values())
		{
			if (player.getTarget() != character)
				continue;
			
			player.sendPacket(mov);
		}
	}
	
	/**
	 * Send a packet to all L2PcInstance in the _KnownPlayers of the L2Character.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the L2Character, server just need to go through _knownPlayers to send Server->Client Packet<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * <BR>
	 * @param character The character to make checks on.
	 * @param mov The packet to send.
	 */
	public static void toKnownPlayers(L2Character character, L2GameServerPacket mov)
	{
		for (L2PcInstance player : character.getKnownList().getKnownPlayers().values())
		{
			player.sendPacket(mov);
			
		if (mov instanceof CharInfo && character instanceof L2PcInstance)
		{
			int relation = ((L2PcInstance) character).getRelation(player);
			if (character.getKnownList().getKnownRelations().get(player.getObjectId()) != null && character.getKnownList().getKnownRelations().get(player.getObjectId()) != relation)
				player.sendPacket(new RelationChanged((L2PcInstance) character, relation, player.isAutoAttackable((L2PcInstance) character)));
		}
	}
	}
	
	/**
	 * Send a packet to all L2PcInstance in the _KnownPlayers (in the specified radius) of the L2Character.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the L2Character, server just needs to go through _knownPlayers to send Server->Client Packet and check the distance between the targets.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * <BR>
	 * @param character The character to make checks on.
	 * @param mov The packet to send.
	 * @param radius The given radius.
	 */
	public static void toKnownPlayersInRadius(L2Character character, L2GameServerPacket mov, int radius)
	{
		if (radius < 0)
			radius = 1500;
		
		for (L2PcInstance player : character.getKnownList().getKnownPlayers().values())
		{
			if (character.isInsideRadius(player, radius, false, false))
				player.sendPacket(mov);
		}
	}
	
	/**
	 * Send a packet to all L2PcInstance in the _KnownPlayers of the L2Character and to the specified character.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the L2Character, server just need to go through _knownPlayers to send Server->Client Packet<BR>
	 * <BR>
	 * @param character The character to make checks on.
	 * @param mov The packet to send.
	 */
	public static void toSelfAndKnownPlayers(L2Character character, L2GameServerPacket mov)
	{
		if (character instanceof L2PcInstance)
			character.sendPacket(mov);
		
		toKnownPlayers(character, mov);
	}
	
	public static void toSelfAndKnownPlayersInRadius(L2Character character, L2GameServerPacket mov, int radius)
	{
		if (radius < 0)
			radius = 600;
		
		if (character instanceof L2PcInstance)
			character.sendPacket(mov);
		
		for (L2PcInstance player : character.getKnownList().getKnownPlayers().values())
		{
			if (character.isInsideRadius(player, radius, false, false))
				player.sendPacket(mov);
		}
	}
	
	public static void toSelfAndKnownPlayersInRadiusSq(L2Character character, L2GameServerPacket mov, int radiusSq)
	{
		if (radiusSq < 0)
			radiusSq = 360000;
		
		if (character instanceof L2PcInstance)
			character.sendPacket(mov);
		
		for (L2PcInstance player : character.getKnownList().getKnownPlayers().values())
		{
			if (character.getDistanceSq(player) <= radiusSq)
				player.sendPacket(mov);
		}
	}
	
	/**
	 * Send a packet to all L2PcInstance present in the world.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * In order to inform other players of state modification on the L2Character, server just need to go through _allPlayers to send Server->Client Packet<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * <BR>
	 * @param mov The packet to send.
	 */
	public static void toAllOnlinePlayers(L2GameServerPacket mov)
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
		{
			if (player.isOnline()==1)
				player.sendPacket(mov);
		}
	}
	
	/**
	 * Send a packet to all players in a specific region.
	 * @param region : The region to send packets.
	 * @param packets : The packets to send.
	 */
	public static void toAllPlayersInRegion(L2WorldRegion region, L2GameServerPacket... packets)
	{
		for (L2Object object : region.getVisibleObjects().values())
		{
		if (object instanceof L2PcInstance)
		{
			final L2PcInstance player = (L2PcInstance) object;
			
				for (L2GameServerPacket packet : packets)
					player.sendPacket(packet);
		}
		}
	}
	
	/**
	 * Send a packet to all players in a specific zone type.
	 * @param <T> L2ZoneType.
	 * @param zoneType : The zone type to send packets.
	 * @param packets : The packets to send.
	 */
	public static <T extends L2ZoneType> void toAllPlayersInZoneType(Class<T> zoneType, L2GameServerPacket... packets)
	{
		for (L2ZoneType temp : ZoneManager.getInstance().getAllZones(zoneType))
		{
			for (L2PcInstance player : temp.getKnownTypeInside(L2PcInstance.class))
			{
				for (L2GameServerPacket packet : packets)
					player.sendPacket(packet);
			}
		}
	}
	
	public static void announceToOnlinePlayers(String text)
	{
		toAllOnlinePlayers(new CreatureSay(0, Say2.ANNOUNCEMENT, "", text));
	}
	
	public static void announceToOnlinePlayers(String text, boolean critical)
	{
		toAllOnlinePlayers(new CreatureSay(0, (critical) ? Say2.CRITICAL_ANNOUNCE : Say2.ANNOUNCEMENT, "", text));
	}
}