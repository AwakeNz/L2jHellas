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

import com.l2jhellas.gameserver.instancemanager.CastleManorManager.CropProcure;
import com.l2jhellas.gameserver.model.L2Manor;

/**
 * Format: ch cddd[ddddcdcdcd]<BR>
 * c - id (0xFE)<BR>
 * h - sub id (0x1D)<BR>
 * <BR>
 * c<BR>
 * d - manor id<BR>
 * d<BR>
 * d - size<BR>
 * [<BR>
 * d - crop id<BR>
 * d - residual buy<BR>
 * d - start buy<BR>
 * d - buy price<BR>
 * c - reward type<BR>
 * d - seed level<BR>
 * c - reward 1 items<BR>
 * d - reward 1 item id<BR>
 * c - reward 2 items<BR>
 * d - reward 2 item id<BR>
 * ]<BR>
 * 
 * @author l3x
 */

public class ExShowCropInfo extends L2GameServerPacket
{
	private static final String _S__FE_1C_EXSHOWSEEDINFO = "[S] FE:1D ExShowCropInfo";
	private ArrayList<CropProcure> _crops;
	private final int _manorId;

	public ExShowCropInfo(int manorId, ArrayList<CropProcure> arrayList)
	{
		_manorId = manorId;
		_crops = arrayList;
		if (_crops == null)
		{
			_crops = new ArrayList<CropProcure>();
		}
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE); // Id
		writeH(0x1D); // SubId
		writeC(0);
		writeD(_manorId); // Manor ID
		writeD(0);
		writeD(_crops.size());
		for (CropProcure crop : _crops)
		{
			writeD(crop.getId());          // Crop id
			writeD(crop.getAmount());      // Buy residual
			writeD(crop.getStartAmount()); // Buy
			writeD(crop.getPrice());       // Buy price
			writeC(crop.getReward());      // Reward
			writeD(L2Manor.getInstance().getSeedLevelByCrop(crop.getId())); // Seed Level
			writeC(1); // reward 1 Type
			writeD(L2Manor.getInstance().getRewardItem(crop.getId(), 1));   // Reward 1 Type Item Id
			writeC(1); // reward 2 Type
			writeD(L2Manor.getInstance().getRewardItem(crop.getId(), 2));   // Reward 2 Type Item Id
		}
	}

	@Override
	public String getType()
	{
		return _S__FE_1C_EXSHOWSEEDINFO;
	}
}