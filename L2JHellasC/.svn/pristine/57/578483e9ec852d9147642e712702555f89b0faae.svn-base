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
package Extensions.RankSystem;

import java.util.logging.Level;
import java.util.logging.Logger;

import Extensions.RankSystem.Util.ServerSideImage;

import com.l2jhellas.Config;

/**
 * This class initializes all global variables for configuration.<br>
 * If the key doesn't appear in properties file, a default value is set by this class.<br>
 * 
 * @author Masterio
 */
public final class RankLoader
{
	private static final Logger log = Logger.getLogger(RankLoader.class.getSimpleName());
	public static final String RANK_PVP_SYSTEM_VERSION = "3.8.9";
	public static final String CHAR_ID_COLUMN_NAME = "obj_Id";

	/**
	 * All required tables are initialized here.
	 */
	public static void load()
	{
		log.log(Level.INFO, "> Initializing Rank PvP System (" + RANK_PVP_SYSTEM_VERSION + "):");

		// initializing system
		PvpTable.getInstance();

		if (Config.RPC_REWARD_ENABLED || Config.RANK_RPC_ENABLED || Config.RPC_TABLE_FORCE_UPDATE_ENABLED)
			RPCTable.getInstance();
		else
			log.log(Level.INFO, " - RPCTable: Disabled, players RPC will be not updated!");

		if (Config.RPC_REWARD_ENABLED || Config.RPC_EXCHANGE_ENABLED)
			RPCRewardTable.getInstance();
		else
			log.log(Level.INFO, " - RPCRewardTable: Disabled.");

		if (Config.PVP_REWARD_ENABLED || Config.RANK_PVP_REWARD_ENABLED)
			RewardTable.getInstance();
		else
			log.log(Level.INFO, " - RewardTable: Disabled.");

		if (Config.TOP_LIST_ENABLED)
			TopTable.getInstance();
		else
			log.log(Level.INFO, " - TopTable: Disabled.");
		
		ServerSideImage.getInstance();
		log.log(Level.INFO, " - Rank Pvp System initialization complete.");
	}
}
