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
package com.l2jhellas.gameserver.handlers.skillhandlers;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.handler.ISkillHandler;
import com.l2jhellas.gameserver.instancemanager.FishingZoneManager;
import com.l2jhellas.gameserver.model.Inventory;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.L2Skill;
import com.l2jhellas.gameserver.model.L2SkillType;
import com.l2jhellas.gameserver.model.Location;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.zone.type.L2FishingZone;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jhellas.gameserver.network.serverpackets.ItemList;
import com.l2jhellas.gameserver.templates.L2Weapon;
import com.l2jhellas.gameserver.templates.L2WeaponType;
import com.l2jhellas.util.Rnd;
import com.l2jhellas.util.Util;

public class Fishing implements ISkillHandler
{
	// protected SkillType[] _skillIds = {SkillType.FISHING};
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.FISHING
	};

	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (activeChar == null || !(activeChar instanceof L2PcInstance))
			return;

		L2PcInstance player = (L2PcInstance) activeChar;

		/*
		 * If fishing is disabled, there isn't much point in doing anything
		 * else, unless you are GM.
		 * so this got moved up here, before anything else.
		 */
		if (!Config.ALLOWFISHING && !player.isGM())
		{
			player.sendMessage("Not Working Yet");
			return;
		}

		/*
		 * If fishing is enabled, here is the code that was striped from
		 * startFishing() in L2PcInstance.
		 * Decide now where will the hook be cast...
		 */
		int rnd = Rnd.get(200) + 200;
		double angle = Util.convertHeadingToDegree(player.getHeading());
		// this.sendMessage("Angel: "+angle+" Heading: "+getHeading());
		double radian = Math.toRadians(angle - 90);
		double sin = Math.sin(radian);
		double cos = Math.cos(radian);
		int x1 = -(int) (sin * rnd); // Somthing wrong with L2j Heding
										// calculation o_0?
		int y1 = (int) (cos * rnd); // Somthing wrong with L2j Heding
									// calculation o_0?
		int x = player.getX() + x1;
		int y = player.getY() + y1;
		int z = player.getZ() - 30;

		/*
		 * ...and if the spot is in a fishing zone. If it is, it will then
		 * position the hook on the water
		 * surface. If not, you have to be GM to proceed past here... in that
		 * case, the hook will be
		 * positioned using the old Z lookup method.
		 */
		L2FishingZone aimingTo = FishingZoneManager.getInstance().isInsideFishingZone(x, y, z);
		if (aimingTo != null)
		{
			z = aimingTo.getWaterZ();
			// player.sendMessage("Hook x,y: " + x + "," + y +
			// " - Water Z, Player Z:" + z + ", " + player.getZ()); //debug
			// line, shows hook landing related coordinates. Uncoment if needed.
		}
		else
		{
			// You can't fish here
			player.sendPacket(SystemMessageId.CANNOT_FISH_HERE);
			if (!player.isGM())
			{
				return;
			}
		}

		if (player.isFishing())
		{
			if (player.GetFishCombat() != null)
				player.GetFishCombat().doDie(false);
			else
				player.EndFishing(false);
			// Cancels fishing
			player.sendPacket(SystemMessageId.FISHING_ATTEMPT_CANCELLED);
			return;
		}
		if (player.isInBoat())
		{
			// You can't fish while you are on boat
			player.sendPacket(SystemMessageId.CANNOT_FISH_ON_BOAT);
			if (!player.isGM())
				return;
		}

		/*
		 * Of course since you can define fishing water volumes of any height,
		 * the function needs to be
		 * changed to cope with that. Still, this is assuming that fishing zones
		 * water surfaces, are
		 * always above "sea level".
		 */
		if (player.getZ() <= -3800 || player.getZ() < (z - 32))
		{
			// You can't fish in water
			player.sendPacket(SystemMessageId.CANNOT_FISH_UNDER_WATER);
			if (!player.isGM())
				return;
		}
		if (player.isInCraftMode() || player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.CANNOT_FISH_WHILE_USING_RECIPE_BOOK);
			if (!player.isGM())
				return;
		}
		L2Weapon weaponItem = player.getActiveWeaponItem();
		if ((weaponItem == null || weaponItem.getItemType() != L2WeaponType.ROD))
		{
			// Fishing poles are not installed
			player.sendPacket(SystemMessageId.FISHING_POLE_NOT_EQUIPPED);
			return;
		}
		L2ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (lure == null)
		{
			// Bait not equiped.
			player.sendPacket(SystemMessageId.BAIT_ON_HOOK_BEFORE_FISHING);
			return;
		}
		player.SetLure(lure);
		L2ItemInstance lure2 = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);

		if (lure2 == null || lure2.getCount() < 1) // Not enough bait.
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_BAIT);
			player.sendPacket(new ItemList(player, false));
		}
		else
		// Has enough bait, consume 1 and update inventory. Start fishing follows.
		{
			lure2 = player.getInventory().destroyItem("Consume", player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1, player, null);
			InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(lure2);
			player.sendPacket(iu);
		}
         Location loc = new Location(player.getX(),player.getY(),player.getZ());
		// If everything else checks out, actually cast the hook and start fishing... :P
		player.startFishing(loc);

	}

	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}