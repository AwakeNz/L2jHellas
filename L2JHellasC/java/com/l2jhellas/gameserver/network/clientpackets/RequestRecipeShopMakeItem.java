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
package com.l2jhellas.gameserver.network.clientpackets;

import com.l2jhellas.gameserver.controllers.RecipeController;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.util.Util;

/**
 * @author Administrator
 */
public final class RequestRecipeShopMakeItem extends L2GameClientPacket
{
	private static final String _C__AF_REQUESTRECIPESHOPMAKEITEM = "[C] B6 RequestRecipeShopMakeItem";

	private int _id;
	private int _recipeId;
	@SuppressWarnings("unused")
	private int _unknow;

	@Override
	protected void readImpl()
	{
		_id = readD();
		_recipeId = readD();
		_unknow = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		L2PcInstance manufacturer = (L2PcInstance) L2World.getInstance().findObject(_id);
		if (manufacturer == null)
			return;

		if (activeChar.getPrivateStoreType() != 0)
		{
			activeChar.sendMessage("Cannot make items while trading");
			return;
		}
		if (manufacturer.getPrivateStoreType() != 5)
		{
			// activeChar.sendMessage("Cannot make items while trading");
			return;
		}

		if (activeChar.isInCraftMode() || manufacturer.isInCraftMode())
		{
			activeChar.sendMessage("Currently in Craft Mode");
			return;
		}
		if (manufacturer.isInDuel() || activeChar.isInDuel())
		{
			activeChar.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		if (Util.checkIfInRange(150, activeChar, manufacturer, true))
			RecipeController.getInstance().requestManufactureItem(manufacturer, _recipeId, activeChar);
	}

	@Override
	public String getType()
	{
		return _C__AF_REQUESTRECIPESHOPMAKEITEM;
	}
}