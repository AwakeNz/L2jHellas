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
package com.l2jhellas.gameserver.handlers.itemhandlers;

import com.l2jhellas.gameserver.datatables.sql.ItemTable;
import com.l2jhellas.gameserver.datatables.xml.RecipeData;
import com.l2jhellas.gameserver.handler.IItemHandler;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2RecipeList;
import com.l2jhellas.gameserver.model.actor.L2Playable;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.SystemMessage;

public class Recipes implements IItemHandler
{
	private final int[] ITEM_IDS;

	public Recipes()
	{
		RecipeData rc = RecipeData.getInstance();
		ITEM_IDS = new int[rc.getRecipesCount()];
		for (int i = 0; i < rc.getRecipesCount(); i++)
		{
			ITEM_IDS[i] = rc.getRecipeList(i).getRecipeId();
		}
	}

	@Override
	public void useItem(L2Playable playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2RecipeList rp = RecipeData.getInstance().getRecipeByItemId(item.getItemId());
		if (activeChar.hasRecipeList(rp.getId()))
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.RECIPE_ALREADY_REGISTERED);
			activeChar.sendPacket(sm);
		}
		else
		{
			if (rp.isDwarvenRecipe())
			{
				if (activeChar.hasDwarvenCraft())
				{
					if (rp.getLevel() > activeChar.getDwarvenCraft())
					{
						// can't add recipe, because create item level too low
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CREATE_LVL_TOO_LOW_TO_REGISTER);
						activeChar.sendPacket(sm);
					}
					else if (activeChar.getDwarvenRecipeBook().length >= activeChar.GetDwarfRecipeLimit())
					{
						// Up to $s1 recipes can be registered.
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER);
						sm.addNumber(activeChar.GetDwarfRecipeLimit());
						activeChar.sendPacket(sm);
					}
					else
					{
						activeChar.registerDwarvenRecipeList(rp);
						activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
						String itemName = ItemTable.getInstance().getTemplate(rp.getItemId()).getItemName();
						activeChar.sendMessage("Added recipe " + itemName + " to Dwarven RecipeBook.");
					}
				}
				else
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
					activeChar.sendPacket(sm);
				}
			}
			else
			{
				if (activeChar.hasCommonCraft())
				{
					if (rp.getLevel() > activeChar.getCommonCraft())
					{
						// can't add recipe, because create item level too low
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CREATE_LVL_TOO_LOW_TO_REGISTER);
						activeChar.sendPacket(sm);
					}
					else if (activeChar.getCommonRecipeBook().length >= activeChar.GetCommonRecipeLimit())
					{
						// Up to $s1 recipes can be registered.
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER);
						sm.addNumber(activeChar.GetCommonRecipeLimit());
						activeChar.sendPacket(sm);
					}
					else
					{
						activeChar.registerCommonRecipeList(rp);
						activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
						String itemName = ItemTable.getInstance().getTemplate(rp.getItemId()).getItemName();
						activeChar.sendMessage("Added recipe " + itemName + " to Common RecipeBook.");
					}
				}
				else
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
					activeChar.sendPacket(sm);
				}
			}
		}
	}

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}