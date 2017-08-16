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

package com.l2jhellas.gameserver.model.entity;

/**
 * @author SkyLanceR
 */
public class RestartVoteVariable
{
	public int _voteCountRestart = 0;
	private int _voteCount = 0;

	public int getVoteCount(String name)
	{
		if (name == "restart")
			_voteCount = _voteCountRestart;

		return _voteCount;
	}

	public void increaseVoteCount(String name)
	{
		if (name == "restart")
			_voteCountRestart = _voteCountRestart + 1;
	}
}