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
package com.l2jhellas.gameserver.model.actor.knownlist;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.L2Summon;
import com.l2jhellas.gameserver.model.actor.L2Vehicle;
import com.l2jhellas.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PetInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2StaticObjectInstance;
import com.l2jhellas.gameserver.network.serverpackets.ChairSit;
import com.l2jhellas.gameserver.network.serverpackets.CharInfo;
import com.l2jhellas.gameserver.network.serverpackets.DeleteObject;
import com.l2jhellas.gameserver.network.serverpackets.DoorInfo;
import com.l2jhellas.gameserver.network.serverpackets.DoorStatusUpdate;
import com.l2jhellas.gameserver.network.serverpackets.DropItem;
import com.l2jhellas.gameserver.network.serverpackets.GetOnVehicle;
import com.l2jhellas.gameserver.network.serverpackets.NpcInfo;
import com.l2jhellas.gameserver.network.serverpackets.PetInfo;
import com.l2jhellas.gameserver.network.serverpackets.PetItemList;
import com.l2jhellas.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import com.l2jhellas.gameserver.network.serverpackets.PrivateStoreMsgSell;
import com.l2jhellas.gameserver.network.serverpackets.RecipeShopMsg;
import com.l2jhellas.gameserver.network.serverpackets.RelationChanged;
import com.l2jhellas.gameserver.network.serverpackets.SpawnItem;
import com.l2jhellas.gameserver.network.serverpackets.SpawnItemPoly;
import com.l2jhellas.gameserver.network.serverpackets.StaticObject;
import com.l2jhellas.gameserver.network.serverpackets.VehicleDeparture;
import com.l2jhellas.gameserver.network.serverpackets.VehicleInfo;

public class PcKnownList extends PlayableKnownList
{
	public PcKnownList(L2PcInstance activeChar)
	{
		super(activeChar);
	}
	/**
	 * Add a visible L2Object to L2PcInstance _knownObjects and _knownPlayer (if necessary) and send Server-Client Packets needed to inform the L2PcInstance of its state and
	 * actions in progress.<BR>
	 * <BR>
	 * <B><U> object is a L2ItemInstance </U> :</B><BR>
	 * <BR>
	 * <li>Send Server-Client Packet DropItem/SpawnItem to the L2PcInstance</li><BR>
	 * <BR>
	 * <B><U> object is a L2DoorInstance </U> :</B><BR>
	 * <BR>
	 * <li>Send Server-Client Packets DoorInfo and DoorStatusUpdate to the L2PcInstance</li> <li>Send Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the
	 * L2PcInstance</li><BR>
	 * <BR>
	 * <B><U> object is a L2NpcInstance </U> :</B><BR>
	 * <BR>
	 * <li>Send Server-Client Packet NpcInfo to the L2PcInstance</li> <li>Send Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the L2PcInstance</li><BR>
	 * <BR>
	 * <B><U> object is a L2Summon </U> :</B><BR>
	 * <BR>
	 * <li>Send Server-Client Packet NpcInfo/PetItemList (if the L2PcInstance is the owner) to the L2PcInstance</li> <li>Send Server->Client packet MoveToPawn/CharMoveToLocation
	 * and AutoAttackStart to the L2PcInstance</li><BR>
	 * <BR>
	 * <B><U> object is a L2PcInstance </U> :</B><BR>
	 * <BR>
	 * <li>Send Server-Client Packet CharInfo to the L2PcInstance</li> <li>If the object has a private store, Send Server-Client Packet PrivateStoreMsgSell to the L2PcInstance</li>
	 * <li>Send Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the L2PcInstance</li><BR>
	 * <BR>
	 * 
	 * @param object
	 *        The L2Object to add to _knownObjects and _knownPlayer
	 * @param dropper
	 *        The L2Character who dropped the L2Object
	 */
	@Override
	public boolean addKnownObject(L2Object object)
	{
		return addKnownObject(object, null);
	}

	@Override
	public boolean addKnownObject(L2Object object, L2Character dropper)
	{
		if (!super.addKnownObject(object, dropper) || !object.isVisible())
			return false;

		if (object.isVisible() && object.getPoly().isMorphed() && object.getPoly().getPolyType().equals("item"))
		{
			// if (object.getPolytype().equals("item"))
			getActiveChar().sendPacket(new SpawnItemPoly(object));
			// else if (object.getPolytype().equals("npc"))
			// sendPacket(new NpcInfoPoly(object, this));

		}
		else
		{
			if (object instanceof L2ItemInstance)
			{
				if (object.isVisible() && dropper != null)
					getActiveChar().sendPacket(new DropItem((L2ItemInstance) object, dropper.getObjectId()));
				else if(object.isVisible())
					getActiveChar().sendPacket(new SpawnItem((L2ItemInstance) object));
			}
			else if (object instanceof L2DoorInstance)
			{
				getActiveChar().sendPacket(new DoorInfo((L2DoorInstance) object, false));
				getActiveChar().sendPacket(new DoorStatusUpdate((L2DoorInstance) object));
			}
			else if (object instanceof L2BoatInstance)
			{
				if (!getActiveChar().isInBoat())
					if (object != getActiveChar().getBoat())
					{
						getActiveChar().sendPacket(new VehicleInfo((L2BoatInstance) object));
						getActiveChar().sendPacket(new VehicleDeparture((L2BoatInstance) object));
					}
			}
			else if (object instanceof L2StaticObjectInstance)
			{
				getActiveChar().sendPacket(new StaticObject((L2StaticObjectInstance) object));
			}
			else if (object instanceof L2Npc)
			{
				if (Config.CHECK_KNOWN)
					getActiveChar().sendMessage("Added NPC: " + ((L2Npc) object).getName());
				getActiveChar().sendPacket(new NpcInfo((L2Npc) object, getActiveChar()));
			}
			else if (object instanceof L2Summon)
			{
				L2Summon summon = (L2Summon) object;

				// Check if the L2PcInstance is the owner of the Pet
				if (getActiveChar().equals(summon.getOwner()))
				{
					getActiveChar().sendPacket(new PetInfo(summon, 0));
					// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
					summon.updateEffectIcons(true);
					if (summon instanceof L2PetInstance)
					{
						getActiveChar().sendPacket(new PetItemList((L2PetInstance) summon));
					}
				}
				else
					getActiveChar().sendPacket(new NpcInfo(summon, getActiveChar()));
			}
			else if (object instanceof L2PcInstance)
			{
				if (((L2PcInstance) object).inObserverMode())
					return false;
				
				L2PcInstance otherPlayer = (L2PcInstance) object;
				
				if (otherPlayer.isInBoat())
				{
					otherPlayer.getPosition().setWorldPosition(otherPlayer.getBoat().getPosition().getWorldPosition());
					getActiveChar().sendPacket(new CharInfo(otherPlayer));
					int relation = otherPlayer.getRelation(getActiveChar());
					if (otherPlayer.getKnownList().getKnownRelations().get(getActiveChar().getObjectId()) != null && otherPlayer.getKnownList().getKnownRelations().get(getActiveChar().getObjectId()) != relation)
						getActiveChar().sendPacket(new RelationChanged(otherPlayer, relation, getActiveChar().isAutoAttackable(otherPlayer)));
					getActiveChar().sendPacket(new GetOnVehicle(otherPlayer.getObjectId(), otherPlayer.getBoat().getObjectId(), otherPlayer.getInVehiclePosition()));		
				}
				else
				{
					getActiveChar().sendPacket(new CharInfo(otherPlayer));
					int relation = otherPlayer.getRelation(getActiveChar());
					if (otherPlayer.getKnownList().getKnownRelations().get(getActiveChar().getObjectId()) != null && otherPlayer.getKnownList().getKnownRelations().get(getActiveChar().getObjectId()) != relation)
						getActiveChar().sendPacket(new RelationChanged(otherPlayer, relation, getActiveChar().isAutoAttackable(otherPlayer)));
				}

				if (otherPlayer.isSeated())
				{
					final L2Object throne = L2World.getInstance().findObject(otherPlayer.getMountObjectID());
					if (throne instanceof L2StaticObjectInstance)
						getActiveChar().sendPacket(new ChairSit(otherPlayer, ((L2StaticObjectInstance)throne).getStaticObjectId()));
				}
				
				if (otherPlayer.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_SELL)
					getActiveChar().sendPacket(new PrivateStoreMsgSell(otherPlayer));
				else if (otherPlayer.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_BUY)
					getActiveChar().sendPacket(new PrivateStoreMsgBuy(otherPlayer));
				else if (otherPlayer.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_MANUFACTURE)
					getActiveChar().sendPacket(new RecipeShopMsg(otherPlayer));
			}

			if (object instanceof L2Character)
			{
				// Update the state of the L2Character object client side by sending Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the L2PcInstance
				L2Character obj = (L2Character) object;
				obj.getAI().describeStateToPlayer(getActiveChar());
			}
		}

		return true;
	}
	/**
	 * Remove a L2Object from L2PcInstance _knownObjects and _knownPlayer (if necessary) and send Server-Client Packet DeleteObject to the L2PcInstance.<BR>
	 * <BR>
	 * 
	 * @param object
	 *        The L2Object to remove from _knownObjects and _knownPlayer
	 */
	@Override
	public boolean removeKnownObject(L2Object object)
	{
		if (!super.removeKnownObject(object))
			return false;
		// Send Server-Client Packet DeleteObject to the L2PcInstance
		getActiveChar().sendPacket(new DeleteObject(object));
		if (Config.CHECK_KNOWN && object instanceof L2Npc)
			getActiveChar().sendMessage("Removed NPC: " + ((L2Npc) object).getName());
		return true;
	}

	@Override
	public final L2PcInstance getActiveChar()
	{
		return (L2PcInstance) super.getActiveChar();
	}

	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		return (int) Math.round(1.5 * getDistanceToWatchObject(object));
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		if (object instanceof L2Vehicle)
			return 8000;
		
		return Math.max(1800, 3600 - (getKnownObjects().size() * 20));
	}
	
}