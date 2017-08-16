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
package com.l2jhellas.gameserver.network.serverpackets;

import java.util.ArrayList;

import com.l2jhellas.gameserver.instancemanager.CastleManorManager.SeedProduction;
import com.l2jhellas.gameserver.model.L2Manor;

/**
 * format(packet 0xFE)<BR>
 * ch ddd [dddddcdcd]<BR>
 * c - id<BR>
 * h - sub id<BR>
 * <BR>
 * d - manor id<BR>
 * d<BR>
 * d - size<BR>
 * <BR>
 * [<BR>
 * d - seed id<BR>
 * d - left to buy<BR>
 * d - started amount<BR>
 * d - sell price<BR>
 * d - seed level<BR>
 * c<BR>
 * d - reward 1 id<BR>
 * c<BR>
 * d - reward 2 id<BR>
 * ]<BR>
 * 
 * @author l3x
 */
public class ExShowSeedInfo extends L2GameServerPacket
{
	private static final String _S__FE_1C_EXSHOWSEEDINFO = "[S] FE:1C ExShowSeedInfo";
	private ArrayList<SeedProduction> _seeds;
	private final int _manorId;

	public ExShowSeedInfo(int manorId, ArrayList<SeedProduction> arrayList)
	{
		_manorId = manorId;
		_seeds = arrayList;
		if (_seeds == null)
		{
			_seeds = new ArrayList<SeedProduction>();
		}
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE); // Id
		writeH(0x1C); // SubId
		writeC(0);
		writeD(_manorId); // Manor ID
		writeD(0);
		writeD(_seeds.size());
		for (SeedProduction seed : _seeds)
		{
			writeD(seed.getId());           // Seed id
			writeD(seed.getCanProduce());   // Left to buy
			writeD(seed.getStartProduce()); // Started amount
			writeD(seed.getPrice());        // Sell Price
			writeD(L2Manor.getInstance().getSeedLevel(seed.getId())); // Seed Level
			writeC(1); // reward 1 Type
			writeD(L2Manor.getInstance().getRewardItemBySeed(seed.getId(), 1)); // Reward 1 Type Item Id
			writeC(1); // reward 2 Type
			writeD(L2Manor.getInstance().getRewardItemBySeed(seed.getId(), 2)); // Reward 2 Type Item Id
		}
	}

	@Override
	public String getType()
	{
		return _S__FE_1C_EXSHOWSEEDINFO;
	}
}