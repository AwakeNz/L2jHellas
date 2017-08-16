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
package com.l2jhellas.gameserver.model.zone.type;

import com.l2jhellas.gameserver.datatables.xml.MapRegionTable;
import com.l2jhellas.gameserver.instancemanager.CastleManager;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2SiegeSummonInstance;
import com.l2jhellas.gameserver.model.entity.Castle;
import com.l2jhellas.gameserver.model.zone.L2SpawnZone;
import com.l2jhellas.gameserver.model.zone.ZoneId;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;

/**
 * A castle zone
 * @author durgus
 */
public class L2CastleZone extends L2SpawnZone
{
	private int _castleId;
	private Castle _castle = null;
	
	public L2CastleZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId"))
			_castleId = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (getCastle() != null)
		{
			character.setInsideZone(ZoneId.CASTLE, true);
		}
		
		
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (getCastle() != null)
		{
			character.setInsideZone(ZoneId.CASTLE, false);
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
	
	/**
	 * Removes all foreigners from the castle
	 * @param owningClanId
	 */
	public void banishForeigners(int owningClanId)
	{
		for (L2PcInstance player : getKnownTypeInside(L2PcInstance.class))
		{
			if (player.getClanId() == owningClanId)
				continue;
			
			player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
		}
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
	
	private final Castle getCastle()
	{
		if (_castle == null)
			_castle = CastleManager.getInstance().getCastleById(_castleId);
		
		return _castle;
	}

	/**
	 * Sends a message to all players in this zone
	 * 
	 * @param message
	 */
	public void announceToPlayers(String message)
	{
		for (L2Character temp : _characterList)
		{
			if (temp instanceof L2PcInstance)
				((L2PcInstance) temp).sendMessage(message);
		}
	}

	public void updateZoneStatusForCharactersInside()
	{
		if (_castle.getSiege().getIsInProgress())
		{
			for (L2Character character : _characterList)
			{
				try
				{
					onEnter(character);
				}
				catch (NullPointerException e)
				{
				}
			}
		}
		else
		{
			for (L2Character character : _characterList)
			{
				try
				{
					character.setInsideZone(ZoneId.PVP, false);
					character.setInsideZone(ZoneId.SIEGE, false);

					if (character instanceof L2PcInstance)
						character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
					if (character instanceof L2SiegeSummonInstance)
					{
						((L2SiegeSummonInstance) character).unSummon(((L2SiegeSummonInstance) character).getOwner());
					}
				}
				catch (NullPointerException e)
				{
				}
			}
		}
	}

	//test
	//public boolean isActive()
	//{
	//	return true;
	//}
	
	//only in siege
	//public boolean isActive()
	//{
		//return getCastle().getSiege().getIsInProgress();
	//}

}