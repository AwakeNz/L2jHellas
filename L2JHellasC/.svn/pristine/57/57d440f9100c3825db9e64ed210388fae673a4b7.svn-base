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

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.datatables.sql.ClanTable;
import com.l2jhellas.gameserver.datatables.xml.MapRegionTable;
import com.l2jhellas.gameserver.model.L2Clan;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.L2Npc;
import com.l2jhellas.gameserver.model.actor.L2Summon;
import com.l2jhellas.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jhellas.gameserver.model.actor.instance.L2PetInstance;
import com.l2jhellas.gameserver.model.zone.ZoneId;
import com.l2jhellas.gameserver.model.zone.type.L2TownZone;

public final class NpcInfo extends L2GameServerPacket
{
	private static final String _S__22_NPCINFO = "[S] 16 NpcInfo";
	private L2Character _activeChar;
	private int _x, _y, _z, _heading;
	private int _idTemplate;
	private boolean _isAttackable, _isSummoned;
	private int _mAtkSpd, _pAtkSpd;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
	private int _rhand, _lhand, _chest, _val;
	private int _collisionHeight, _collisionRadius;
	private String _name = "";
	private L2Summon _summon;
	private String _title = "";
	int _clanCrest = 0;
	int _allyCrest = 0;
	int _allyId = 0;
	int _clanId = 0;
	private int _form = 0;

	/**
	 * @param _characters
	 */
	public NpcInfo(L2Npc cha, L2Character attacker)
	{
		if (cha.getMxcPoly() != null)
		{
			attacker.sendPacket(new MxCPolyInfo(cha));
			return;
		}

		_activeChar = cha;
		_idTemplate = cha.getTemplate().idTemplate;
		_isAttackable = cha.isAutoAttackable(attacker);
		_rhand = cha.getRightHandItem();
		_lhand = cha.getLeftHandItem();
		_isSummoned = false;
		_collisionHeight = cha.getCollisionHeight();
		_collisionRadius = cha.getCollisionRadius();
		if (cha.getTemplate().serverSideName)
			_name = cha.getTemplate().name;

		if (cha.isChampion())
			_title = (Config.CHAMPION_TITLE);
		else if (cha.getTemplate().serverSideTitle)
			_title = cha.getTemplate().title;
		else
			_title = cha.getTitle();

		if (Config.SHOW_NPC_LVL && _activeChar instanceof L2MonsterInstance)
		{
			String t = "Lv " + cha.getLevel() + (cha.getAggroRange() > 0 ? "*" : "");
			if (_title != null)
				t += " " + _title;

			_title = t;
		}

		if (Config.SHOW_NPC_CREST)
		{
			if (cha instanceof L2Npc && cha.isInsideZone(ZoneId.PEACE) && cha.getCastle().getOwnerId() != 0)
			{
				int _x, _y, _z;
				_x = cha.getX();
				_y = cha.getY();
				_z = cha.getZ();
				L2TownZone Town;
				Town = MapRegionTable.getTown(_x, _y, _z);
				if (Town != null)
				{
					int townId = Town.getTownId();
					if (townId != 33 && townId != 22)
					{
						L2Clan clan;
						clan = ClanTable.getInstance().getClan(cha.getCastle().getOwnerId());
						_clanCrest = clan.getCrestId();
						_clanId = clan.getClanId();
						_allyCrest = clan.getAllyCrestId();
						_allyId = clan.getAllyId();
					}
				}
			}
		}

		_x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		_mAtkSpd = _activeChar.getMAtkSpd();
		_pAtkSpd = _activeChar.getPAtkSpd();
		_runSpd = _activeChar.getRunSpeed();
		_walkSpd = _activeChar.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
	}

	public NpcInfo(L2Summon cha, L2Character attacker, int val)
	{
		_activeChar = cha;
		_summon = cha;
		_idTemplate = cha.getTemplate().idTemplate;
		_isAttackable = cha.isAutoAttackable(attacker); // (cha.getKarma() > 0);
		_rhand = cha.getWeapon();
		_lhand = 0;
		setChest(cha.getArmor());
		setVal(val);
		_collisionHeight = _activeChar.getTemplate().collisionHeight;
		_collisionRadius = _activeChar.getTemplate().collisionRadius;
		_name = cha.getName();
		_title = cha.getOwner() != null ? (cha.getOwner().isOnline() == 0 ? "" : cha.getOwner().getName()) : ""; // when owner online, summon will show in title owner name
		int npcId = _summon.getTemplate().npcId;
		if (npcId == 16041 || npcId == 16042)
		{
			if (_summon.getLevel() > 84)
				setForm(3);
			else if (_summon.getLevel() > 79)
				setForm(2);
			else if (_summon.getLevel() > 74)
				setForm(1);
		}
		else if (npcId == 16025 || npcId == 16037)
		{
			if (_summon.getLevel() > 69)
				setForm(3);
			else if (_summon.getLevel() > 64)
				setForm(2);
			else if (_summon.getLevel() > 59)
				setForm(1);
		}

		_x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		_mAtkSpd = _activeChar.getMAtkSpd();
		_pAtkSpd = _activeChar.getPAtkSpd();
		_runSpd = _summon.getPetSpeed();
		_walkSpd = _summon.isMountable() ? 45 : 30;
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
	}

	public NpcInfo(L2Summon cha, L2Character attacker)
	{
		_activeChar = cha;
		_idTemplate = cha.getTemplate().idTemplate;
		_isAttackable = cha.isAutoAttackable(attacker); // (cha.getKarma() > 0);
		_rhand = 0;
		_lhand = 0;
		_isSummoned = cha.isShowSummonAnimation();
		_collisionHeight = _activeChar.getTemplate().collisionHeight;
		_collisionRadius = _activeChar.getTemplate().collisionRadius;
		if (cha.getTemplate().serverSideName || cha instanceof L2PetInstance)
		{
			_name = _activeChar.getName();
			_title = cha.getTitle();
		}

		_x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		_mAtkSpd = _activeChar.getMAtkSpd();
		_pAtkSpd = _activeChar.getPAtkSpd();
		_runSpd = _activeChar.getRunSpeed();
		_walkSpd = _activeChar.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
	}

	@Override
	protected final void writeImpl()
	{
		if (_activeChar == null)
			return;

		if (_activeChar instanceof L2Summon)
			if (((L2Summon) _activeChar).getOwner() != null && ((L2Summon) _activeChar).getOwner().getAppearance().getInvisible())
				return;
		writeC(0x16);
		writeD(_activeChar.getObjectId());
		writeD(_idTemplate + 1000000); // npctype id
		writeD(_isAttackable ? 1 : 0);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
		writeD(0x00);
		writeD(_mAtkSpd);
		writeD(_pAtkSpd);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd/* 0x32 */); // swimspeed
		writeD(_swimWalkSpd/* 0x32 */); // swimspeed
		writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(1.1/* _activeChar.getProperMultiplier() */);
		// writeF(1/*_activeChar.getAttackSpeedMultiplier()*/);
		writeF(_pAtkSpd / 277.478340719);
		writeF(_collisionRadius);
		writeF(_collisionHeight);
		writeD(_rhand); // right hand weapon
		writeD(0);
		writeD(_lhand); // left hand weapon
		writeC(1); // name above char 1=true ... ??
		writeC(_activeChar.isRunning() ? 1 : 0);
		writeC(_activeChar.isInCombat() ? 1 : 0);
		writeC(_activeChar.isAlikeDead() ? 1 : 0);
		writeC(_isSummoned ? 2 : 0); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
		writeS(_name);
		writeS(_title);
		writeD(0);
		writeD(0);
		writeD(0000); // hmm karma ??

		writeD(_activeChar.getAbnormalEffect()); // C2
		if (Config.SHOW_NPC_CREST)
		{
			writeD(_clanId);
			writeD(_clanCrest);
			writeD(_allyId);
			writeD(_allyCrest);
		}
		else
		{
			writeD(0000);
			writeD(0000);
			writeD(0000);
			writeD(0000);
			writeC(0000);
		}

		writeC(0x00); // C3 team circle 1-blue, 2-red
		writeF(_collisionRadius);
		writeF(_collisionHeight);
		writeD(0x00); // C4
		writeD(0x00); // C6
	}

	@Override
	public String getType()
	{
		return _S__22_NPCINFO;
	}

	/**
	 * @param chest
	 *        The chest to set.
	 */
	public void setChest(int chest)
	{
		_chest = chest;
	}

	/**
	 * @return Returns the chest.
	 */
	public int getChest()
	{
		return _chest;
	}

	/**
	 * @param val
	 *        The val to set.
	 */
	public void setVal(int val)
	{
		_val = val;
	}

	/**
	 * @return Returns the val.
	 */
	public int getVal()
	{
		return _val;
	}

	/**
	 * @param form
	 *        The form to set.
	 */
	public void setForm(int form)
	{
		_form = form;
	}

	/**
	 * @return Returns the form.
	 */
	public int getForm()
	{
		return _form;
	}
}