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

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.datatables.sql.CharNameTable;
import com.l2jhellas.gameserver.datatables.sql.ItemTable;
import com.l2jhellas.gameserver.datatables.xml.CharTemplateData;
import com.l2jhellas.gameserver.datatables.xml.ExperienceData;
import com.l2jhellas.gameserver.datatables.xml.SkillTreeData;
import com.l2jhellas.gameserver.idfactory.IdFactory;
import com.l2jhellas.gameserver.instancemanager.QuestManager;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2ShortCut;
import com.l2jhellas.gameserver.model.L2SkillLearn;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.quest.Quest;
import com.l2jhellas.gameserver.network.L2GameClient;
import com.l2jhellas.gameserver.network.serverpackets.CharCreateFail;
import com.l2jhellas.gameserver.network.serverpackets.CharCreateOk;
import com.l2jhellas.gameserver.network.serverpackets.CharSelectInfo;
import com.l2jhellas.gameserver.skills.SkillTable;
import com.l2jhellas.gameserver.templates.L2Item;
import com.l2jhellas.gameserver.templates.L2PcTemplate;
import com.l2jhellas.util.Util;

@SuppressWarnings("unused")
public final class CharacterCreate extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(CharacterCreate.class.getName());
	private static final String _C__0B_CHARACTERCREATE = "[C] 0B CharacterCreate";
	// cSdddddddddddd
	private String _name;
	private int _race;
	private byte _sex;
	private int _classId;
	private int _int;
	private int _str;
	private int _con;
	private int _men;
	private int _dex;
	private int _wit;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	private long _exp;

	@Override
	protected void readImpl()
	{
		_name = readS();
		_race = readD();
		_sex = (byte) readD();
		_classId = readD();
		_int = readD();
		_str = readD();
		_con = readD();
		_men = readD();
		_dex = readD();
		_wit = readD();
		_hairStyle = (byte) readD();
		_hairColor = (byte) readD();
		_face = (byte) readD();
	}

	@Override
	protected void runImpl()
	{
		if (CharNameTable.getInstance().accountCharNumber(getClient().getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT && Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0)
		{
			if (Config.DEBUG)
			{
				_log.fine("Max number of characters reached. Creation failed.");
			}

			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS);
			sendPacket(ccf);
			return;
		}
		else if (CharNameTable.getInstance().doesCharNameExist(_name))
		{
			if (Config.DEBUG)
			{
				_log.fine("charname: " + _name + " already exists. creation failed.");
			}

			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS);
			sendPacket(ccf);
			return;
		}
		else if ((_name.length() < 3) || (_name.length() > 16) || !Util.isAlphaNumeric(_name) || !isValidName(_name))
		{
			if (Config.DEBUG)
			{
				_log.fine("charname: " + _name + " is invalid. creation failed.");
			}
			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS);
			sendPacket(ccf);
			return;
		}

		if (_face > 2 || _face < 0)
		{
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		if (_hairStyle < 0 || (_sex == 0 && _hairStyle > 4) || (_sex != 0 && _hairStyle > 6))
		{
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		if (_hairColor > 3 || _hairColor < 0)
		{
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		if (Config.DEBUG)
		{
			_log.fine("charname: " + _name + " classId: " + _classId);
		}

		L2PcTemplate template = CharTemplateData.getInstance().getTemplate(_classId);
		if (template == null || template.classBaseLevel > 1)
		{
			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED);
			sendPacket(ccf);
			return;
		}

		int objectId = IdFactory.getInstance().getNextId();
		L2PcInstance newChar = L2PcInstance.create(objectId, template, getClient().getAccountName(), _name, _hairStyle, _hairColor, _face, _sex != 0);
		
		// set hp/mp/cp
		newChar.setCurrentCp(0);
		newChar.setCurrentHp(newChar.getMaxHp());
		newChar.setCurrentMp(newChar.getMaxMp());

		// send acknowledgment
		CharCreateOk cco = new CharCreateOk();
		sendPacket(cco);

		initNewChar(getClient(), newChar);
	}

	private boolean isValidName(String text)
	{
		boolean result = true;
		String test = text;
		Pattern pattern;
		try
		{
			pattern = Pattern.compile(Config.CNAME_TEMPLATE);
		}
		catch (PatternSyntaxException e) // case of illegal pattern
		{
			_log.warning(CharacterCreate.class.getName() + ": ERROR : Character name pattern of config is wrong!using default .*");
			pattern = Pattern.compile(".*");
		}
		Matcher regexp = pattern.matcher(test);
		if (!regexp.matches())
		{
			result = false;
		}
		return result;
	}

	private void initNewChar(L2GameClient client, L2PcInstance newChar)
	{
		if (Config.DEBUG)
		{
			_log.fine("Character init start");
		}
		L2World.getInstance().storeObject(newChar);

		L2PcTemplate template = newChar.getTemplate();

		newChar.addAdena("Init", Config.STARTING_ADENA, null, false);
		newChar.addAncientAdena("Init", Config.STARTING_ANCIENT, null, false);

		newChar.setXYZInvisible(template.spawnX, template.spawnY, template.spawnZ);

		if (Config.ALLOW_CREATE_LVL)
		{
			long tXp = ExperienceData.getInstance().getExpForLevel(Config.CUSTOM_START_LVL);
			newChar.addExpAndSp(tXp, 0);
		}
		if (Config.CHAR_TITLE)
		{
			newChar.setTitle(Config.ADD_CHAR_TITLE);
		}
		if (Config.PVP_PK_TITLE)
		{
			newChar.setTitle(Config.PVP_TITLE_PREFIX + "0" +" | "+ Config.PK_TITLE_PREFIX + "0 ");
		}
		L2ShortCut shortcut;
		// add attack shortcut
		shortcut = new L2ShortCut(0, 0, 3, 2, -1, 1);
		newChar.registerShortCut(shortcut);

		// add take shortcut
		shortcut = new L2ShortCut(3, 0, 3, 5, -1, 1);
		newChar.registerShortCut(shortcut);

		// add sit shortcut
		shortcut = new L2ShortCut(10, 0, 3, 0, -1, 1);
		newChar.registerShortCut(shortcut);

		ItemTable itemTable = ItemTable.getInstance();
		L2Item[] items = template.getItems();
		for (int i = 0; i < items.length; i++)
		{
			L2ItemInstance item = newChar.getInventory().addItem("Init", items[i].getItemId(), 1, newChar, null);
			if (item.getItemId() == 5588)
			{
				// add tutbook shortcut
				shortcut = new L2ShortCut(11, 0, 1, item.getObjectId(), -1, 1);
				newChar.registerShortCut(shortcut);
			}
			if (item.isEquipable())
			{
				if (newChar.getActiveWeaponItem() == null || !(item.getItem().getType2() != L2Item.TYPE2_WEAPON))
				{
					newChar.getInventory().equipItemAndRecord(item);
				}
			}
		}

		L2SkillLearn[] startSkills = SkillTreeData.getInstance().getAvailableSkills(newChar, newChar.getClassId());
		for (int i = 0; i < startSkills.length; i++)
		{
			newChar.addSkill(SkillTable.getInstance().getInfo(startSkills[i].getId(), startSkills[i].getLevel()), true);
			if (startSkills[i].getId() == 1001 || startSkills[i].getId() == 1177)
			{
				shortcut = new L2ShortCut(1, 0, 2, startSkills[i].getId(), 1, 1);
				newChar.registerShortCut(shortcut);
			}
			if (startSkills[i].getId() == 1216)
			{
				shortcut = new L2ShortCut(10, 0, 2, startSkills[i].getId(), 1, 1);
				newChar.registerShortCut(shortcut);
			}
			if (Config.DEBUG)
			{
				_log.fine("adding starter skill:" + startSkills[i].getId() + " / " + startSkills[i].getLevel());
			}
		}
		startTutorialQuest(newChar);
		L2GameClient.saveCharToDisk(newChar);
		newChar.deleteMe(); // release the world of this character and it's inventory
		L2World.getInstance().removeObject(newChar);

		// send char list
		CharSelectInfo cl = new CharSelectInfo(client.getAccountName(), client.getSessionId().playOkID1);
		client.getConnection().sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
		if (Config.DEBUG)
		{
			_log.fine("Character init end");
		}
	}
	public void startTutorialQuest(L2PcInstance player)
	{
		Quest qest = QuestManager.getInstance().getQuest("255_Tutorial");
		if (qest != null)
			qest.newQuestState(player);
	}
	@Override
	public String getType()
	{
		return _C__0B_CHARACTERCREATE;
	}
}