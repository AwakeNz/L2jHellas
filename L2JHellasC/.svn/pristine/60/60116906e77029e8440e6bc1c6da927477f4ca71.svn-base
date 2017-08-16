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
package com.l2jhellas.gameserver.model.actor.instance;

import com.l2jhellas.Config;
import com.l2jhellas.gameserver.ai.CtrlIntention;
import com.l2jhellas.gameserver.datatables.xml.CharTemplateData;
import com.l2jhellas.gameserver.datatables.xml.NpcData;
import com.l2jhellas.gameserver.model.Inventory;
import com.l2jhellas.gameserver.model.L2ItemInstance;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.base.ClassId;
import com.l2jhellas.gameserver.model.base.ClassLevel;
import com.l2jhellas.gameserver.model.base.PlayerClass;
import com.l2jhellas.gameserver.network.SystemMessageId;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jhellas.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jhellas.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jhellas.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jhellas.gameserver.network.serverpackets.ValidateLocation;
import com.l2jhellas.gameserver.templates.L2NpcTemplate;

public final class L2ClassMasterInstance extends L2NpcInstance
{
	private static final int[] SECONDN_CLASS_IDS =
	{
	2, 3, 5, 6, 9, 8, 12, 13, 14, 16, 17, 20, 21, 23, 24, 27, 28, 30, 33, 34, 36, 37, 40, 41, 43, 46, 48, 51, 52, 55, 57
	};
	public static L2ClassMasterInstance ClassMaster = new L2ClassMasterInstance(31228, NpcData.getInstance().getTemplate(31228));
	static
	{
		L2World.getInstance().storeObject(ClassMaster);
	}

	/**
	 * @param template
	 */
	public L2ClassMasterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onAction(L2PcInstance player)
	{
		if (this != player.getTarget() && !Config.ALLOW_REMOTE_CLASS_MASTER)
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
			player.sendPacket(new ValidateLocation(this));
		}
		else if (isInsideRadius(player, INTERACTION_DISTANCE, false, false) || Config.ALLOW_REMOTE_CLASS_MASTER)
		{
			if (Config.DEBUG)
				_log.fine("ClassMaster activated");

			ClassId classId = player.getClassId();
			int jobLevel = 0;
			int level = player.getLevel();
			ClassLevel lvl = PlayerClass.values()[classId.getId()].getLevel();
			switch (lvl)
			{
				case First:
					jobLevel = 1;
				break;
				case Second:
					jobLevel = 2;
				break;
				case Third:
					jobLevel = 3;
				break;
				default:
					jobLevel = 4;
			}

			if (player.isGM())
				showChatWindowChooseClass(player);

			else if (((level >= 20 && jobLevel == 1) || (level >= 40 && jobLevel == 2)) && Config.ALLOW_CLASS_MASTER)
				showChatWindow(player, classId.getId());

			else if (level >= 76 && Config.ALLOW_CLASS_MASTER && classId.getId() < 88)
			{
				for (int i = 0; i < SECONDN_CLASS_IDS.length; i++)
				{
					if (classId.getId() == SECONDN_CLASS_IDS[i])
					{
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						StringBuilder sb = new StringBuilder();
						sb.append("<html><body<table width=200>");
						sb.append("<tr><td><center>" + CharTemplateData.getInstance().getClassNameById(player.getClassId().getId()) + " Class Master:</center></td></tr>");
						sb.append("<tr><td><br></td></tr>");
						sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class " + (88 + i) + "\">Advance to " + CharTemplateData.getInstance().getClassNameById(88 + i) + "</a></td></tr>");
						sb.append("<tr><td><br></td></tr>");
						sb.append("</table></body></html>");
						html.setHtml(sb.toString());
						player.sendPacket(html);
						break;
					}
				}
			}
			else
			{
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				StringBuilder sb = new StringBuilder();
				sb.append("<html><body>");
				switch (jobLevel)
				{
					case 1:
						sb.append("Come back here when you reach level 20 to change your class.<br>");
					break;
					case 2:
						sb.append("Come back here when you reach level 40 to change your class.<br>");
					break;
					case 3:
						sb.append("There are no more class changes for you.<br>");
					break;
				}

				//for (Quest q : Quest.findAllEvents())
					//sb.append("Event: <a action=\"bypass -h Quest " + q.getName() + "\">" + q.getDescr() + "</a><br>");

				sb.append("</body></html>");
				html.setHtml(sb.toString());
				player.sendPacket(html);
			}
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		return "data/html/classmaster/" + val + ".htm";
	}

	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("1stClass"))
		{
			if (player.isGM())
			{
				showChatWindow1st(player);
			}
		}
		else if (command.startsWith("2ndClass"))
		{
			if (player.isGM())
			{
				showChatWindow2nd(player);
			}
		}
		else if (command.startsWith("3rdClass"))
		{
			if (player.isGM())
			{
				showChatWindow3rd(player);
			}
		}
		else if (command.startsWith("baseClass"))
		{
			if (player.isGM())
			{
				showChatWindowBase(player);
			}
		}
		else if (command.startsWith("change_class"))
		{
			int val = Integer.parseInt(command.substring(13));

			// Exploit prevention
			ClassId classId = player.getClassId();
			int level = player.getLevel();
			int jobLevel = 0;
			int newJobLevel = 0;

			ClassLevel lvlnow = PlayerClass.values()[classId.getId()].getLevel();

			if (player.isGM())
			{
				changeClass(player, val);
				player.rewardSkills();

				if (val >= 88)
					player.sendPacket(SystemMessageId.THIRD_CLASS_TRANSFER); // system sound 3rd occupation
				else
					player.sendPacket(SystemMessageId.CLASS_TRANSFER);    // system sound for 1st and 2nd occupation

				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				StringBuilder sb = new StringBuilder();
				sb.append("<html><body>");
				sb.append("You have now become a <font color=\"LEVEL\">" + CharTemplateData.getInstance().getClassNameById(player.getClassId().getId()) + "</font>.");
				sb.append("</body></html>");

				html.setHtml(sb.toString());
				player.sendPacket(html);
				return;
			}
			switch (lvlnow)
			{
				case First:
					jobLevel = 1;
				break;
				case Second:
					jobLevel = 2;
				break;
				case Third:
					jobLevel = 3;
				break;
				default:
					jobLevel = 4;
			}

			if (jobLevel == 4)
				return; // no more job changes

			ClassLevel lvlnext = PlayerClass.values()[val].getLevel();
			switch (lvlnext)
			{
				case First:
					newJobLevel = 1;
				break;
				case Second:
					newJobLevel = 2;
				break;
				case Third:
					newJobLevel = 3;
				break;
				default:
					newJobLevel = 4;
			}

			// prevents changing between same level jobs
			if (newJobLevel != jobLevel + 1)
				return;

			if (level < 20 && newJobLevel > 1)
				return;
			if (level < 40 && newJobLevel > 2)
				return;
			if (level < 75 && newJobLevel > 3)
				return;
			// -- prevention ends

			changeClass(player, val);
			player.rewardSkills();

			if (val >= 88)
				player.sendPacket(SystemMessageId.THIRD_CLASS_TRANSFER); // system sound 3rd occupation
			else
				player.sendPacket(SystemMessageId.CLASS_TRANSFER); // system sound for 1st and 2nd occupation

			player.broadcastPacket(new MagicSkillUse(player, player, 5103, 1, 1000, 0));

			HtmlShow(player);						
			
			checkAutoEq(player,newJobLevel);
			
			checks(player);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	private void showChatWindowChooseClass(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<body>");
		sb.append("<table width=200>");
		sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
		sb.append("<tr><td><br></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_baseClass\">Base Classes.</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_1stClass\">1st Classes.</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_2ndClass\">2nd Classes.</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_3rdClass\">3rd Classes.</a></td></tr>");
		sb.append("<tr><td><br></td></tr>");
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
		return;
	}

	private void showChatWindow1st(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<body>");
		sb.append("<table width=200>");
		sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
		sb.append("<tr><td><br></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 1\">Advance to " + CharTemplateData.getInstance().getClassNameById(1) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 4\">Advance to " + CharTemplateData.getInstance().getClassNameById(4) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 7\">Advance to " + CharTemplateData.getInstance().getClassNameById(7) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 11\">Advance to " + CharTemplateData.getInstance().getClassNameById(11) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 15\">Advance to " + CharTemplateData.getInstance().getClassNameById(15) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 19\">Advance to " + CharTemplateData.getInstance().getClassNameById(19) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 22\">Advance to " + CharTemplateData.getInstance().getClassNameById(22) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 26\">Advance to " + CharTemplateData.getInstance().getClassNameById(26) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 29\">Advance to " + CharTemplateData.getInstance().getClassNameById(29) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 32\">Advance to " + CharTemplateData.getInstance().getClassNameById(32) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 35\">Advance to " + CharTemplateData.getInstance().getClassNameById(35) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 39\">Advance to " + CharTemplateData.getInstance().getClassNameById(39) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 42\">Advance to " + CharTemplateData.getInstance().getClassNameById(42) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 45\">Advance to " + CharTemplateData.getInstance().getClassNameById(45) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 47\">Advance to " + CharTemplateData.getInstance().getClassNameById(47) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 50\">Advance to " + CharTemplateData.getInstance().getClassNameById(50) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 54\">Advance to " + CharTemplateData.getInstance().getClassNameById(54) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 56\">Advance to " + CharTemplateData.getInstance().getClassNameById(56) + "</a></td></tr>");
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
		return;
	}

	private void showChatWindow2nd(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<body>");
		sb.append("<table width=200>");
		sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
		sb.append("<tr><td><br></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 2\">Advance to " + CharTemplateData.getInstance().getClassNameById(2) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 3\">Advance to " + CharTemplateData.getInstance().getClassNameById(3) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 5\">Advance to " + CharTemplateData.getInstance().getClassNameById(5) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 6\">Advance to " + CharTemplateData.getInstance().getClassNameById(6) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 8\">Advance to " + CharTemplateData.getInstance().getClassNameById(8) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 9\">Advance to " + CharTemplateData.getInstance().getClassNameById(9) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 12\">Advance to " + CharTemplateData.getInstance().getClassNameById(12) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 13\">Advance to " + CharTemplateData.getInstance().getClassNameById(13) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 14\">Advance to " + CharTemplateData.getInstance().getClassNameById(14) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 16\">Advance to " + CharTemplateData.getInstance().getClassNameById(16) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 17\">Advance to " + CharTemplateData.getInstance().getClassNameById(17) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 20\">Advance to " + CharTemplateData.getInstance().getClassNameById(20) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 21\">Advance to " + CharTemplateData.getInstance().getClassNameById(21) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 23\">Advance to " + CharTemplateData.getInstance().getClassNameById(23) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 24\">Advance to " + CharTemplateData.getInstance().getClassNameById(24) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 27\">Advance to " + CharTemplateData.getInstance().getClassNameById(27) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 28\">Advance to " + CharTemplateData.getInstance().getClassNameById(28) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 30\">Advance to " + CharTemplateData.getInstance().getClassNameById(30) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 33\">Advance to " + CharTemplateData.getInstance().getClassNameById(33) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 34\">Advance to " + CharTemplateData.getInstance().getClassNameById(34) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 36\">Advance to " + CharTemplateData.getInstance().getClassNameById(36) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 37\">Advance to " + CharTemplateData.getInstance().getClassNameById(37) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 40\">Advance to " + CharTemplateData.getInstance().getClassNameById(40) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 41\">Advance to " + CharTemplateData.getInstance().getClassNameById(41) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 43\">Advance to " + CharTemplateData.getInstance().getClassNameById(43) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 46\">Advance to " + CharTemplateData.getInstance().getClassNameById(46) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 48\">Advance to " + CharTemplateData.getInstance().getClassNameById(48) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 51\">Advance to " + CharTemplateData.getInstance().getClassNameById(51) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 52\">Advance to " + CharTemplateData.getInstance().getClassNameById(52) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 55\">Advance to " + CharTemplateData.getInstance().getClassNameById(55) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 57\">Advance to " + CharTemplateData.getInstance().getClassNameById(57) + "</a></td></tr>");
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
		return;
	}

	private void showChatWindow3rd(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<body>");
		sb.append("<table width=200>");
		sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
		sb.append("<tr><td><br></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 88\">Advance to " + CharTemplateData.getInstance().getClassNameById(88) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 89\">Advance to " + CharTemplateData.getInstance().getClassNameById(89) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 90\">Advance to " + CharTemplateData.getInstance().getClassNameById(90) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 91\">Advance to " + CharTemplateData.getInstance().getClassNameById(91) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 92\">Advance to " + CharTemplateData.getInstance().getClassNameById(92) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 93\">Advance to " + CharTemplateData.getInstance().getClassNameById(93) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 94\">Advance to " + CharTemplateData.getInstance().getClassNameById(94) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 95\">Advance to " + CharTemplateData.getInstance().getClassNameById(95) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 96\">Advance to " + CharTemplateData.getInstance().getClassNameById(96) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 97\">Advance to " + CharTemplateData.getInstance().getClassNameById(97) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 98\">Advance to " + CharTemplateData.getInstance().getClassNameById(98) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 99\">Advance to " + CharTemplateData.getInstance().getClassNameById(99) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 100\">Advance to " + CharTemplateData.getInstance().getClassNameById(100) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 101\">Advance to " + CharTemplateData.getInstance().getClassNameById(101) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 102\">Advance to " + CharTemplateData.getInstance().getClassNameById(102) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 103\">Advance to " + CharTemplateData.getInstance().getClassNameById(103) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 104\">Advance to " + CharTemplateData.getInstance().getClassNameById(104) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 105\">Advance to " + CharTemplateData.getInstance().getClassNameById(105) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 106\">Advance to " + CharTemplateData.getInstance().getClassNameById(106) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 107\">Advance to " + CharTemplateData.getInstance().getClassNameById(107) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 108\">Advance to " + CharTemplateData.getInstance().getClassNameById(108) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 109\">Advance to " + CharTemplateData.getInstance().getClassNameById(109) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 110\">Advance to " + CharTemplateData.getInstance().getClassNameById(110) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 111\">Advance to " + CharTemplateData.getInstance().getClassNameById(111) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 112\">Advance to " + CharTemplateData.getInstance().getClassNameById(112) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 113\">Advance to " + CharTemplateData.getInstance().getClassNameById(113) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 114\">Advance to " + CharTemplateData.getInstance().getClassNameById(114) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 115\">Advance to " + CharTemplateData.getInstance().getClassNameById(115) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 116\">Advance to " + CharTemplateData.getInstance().getClassNameById(116) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 117\">Advance to " + CharTemplateData.getInstance().getClassNameById(117) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 118\">Advance to " + CharTemplateData.getInstance().getClassNameById(118) + "</a></td></tr>");
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
		return;
	}

	private void showChatWindowBase(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<body>");
		sb.append("<table width=200>");
		sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
		sb.append("<tr><td><br></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 0\">Advance to " + CharTemplateData.getInstance().getClassNameById(0) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 10\">Advance to " + CharTemplateData.getInstance().getClassNameById(10) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 18\">Advance to " + CharTemplateData.getInstance().getClassNameById(18) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 25\">Advance to " + CharTemplateData.getInstance().getClassNameById(25) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 31\">Advance to " + CharTemplateData.getInstance().getClassNameById(31) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 38\">Advance to " + CharTemplateData.getInstance().getClassNameById(38) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 44\">Advance to " + CharTemplateData.getInstance().getClassNameById(44) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 49\">Advance to " + CharTemplateData.getInstance().getClassNameById(49) + "</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_" + getObjectId() + "_change_class 53\">Advance to " + CharTemplateData.getInstance().getClassNameById(53) + "</a></td></tr>");
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
		return;
	}

	private void changeClass(L2PcInstance player, int val)
	{
		final ClassId currentClassId = player.getClassId();
		if (getMinLevel(currentClassId.level()) > player.getLevel())
			return;
		
		if (!validateClassId(currentClassId, val))
			return;
		
		player.setClassId(val);

		if (player.isSubClassActive())
			player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
		else
		{
			ClassId classId = ClassId.getClassIdByOrdinal(player.getActiveClass());

            if (classId.getParent() != null)
            {
                while (classId.level() == 0)
                { // go to root
                    classId = classId.getParent();
                }
            }

            player.setBaseClass(classId);

            // player.setBaseClass(player.getActiveClass());
		}
		player.broadcastUserInfo();
	}
	
	private final void HtmlShow(L2PcInstance player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("You have now become a <font color=\"LEVEL\">" + CharTemplateData.getInstance().getClassNameById(player.getClassId().getId()) + "</font>.");
		sb.append("</body></html>");

		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	private final void checks(L2PcInstance player)
	{
		if (!Config.ALLOW_ARCHERS_WEAR_HEAVY)
		{		
			switch (player.getClassId().getId())
			{			
				case 9:
				case 24:
				case 37:
				case 92:
				case 102:
				case 109:
					final L2ItemInstance armor = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
					if (armor != null)
					{
						L2ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(armor);
						InventoryUpdate iu = new InventoryUpdate();
						
						for (L2ItemInstance element : unequipped)
						{
							iu.addModifiedItem(element);
						}
					}
				break;		
			}				
		}
		if (!Config.ALLOW_DAGGERS_WEAR_HEAVY)
		{
			switch (player.getClassId().getId())
			{			
				case 8:
				case 23:
				case 36:
				case 93:
				case 101:
				case 108:
					final L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
					if (chest != null)
					{
						L2ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(chest);
						InventoryUpdate iu = new InventoryUpdate();
						for (L2ItemInstance element : unequipped)
						{
							iu.addModifiedItem(element);
						}
						sendPacket(iu);
					}
					final L2ItemInstance legs = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
					if (legs != null)
					{
						L2ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(legs);
						InventoryUpdate iu = new InventoryUpdate();
						for (L2ItemInstance element : unequipped)
						{
							iu.addModifiedItem(element);
						}
						sendPacket(iu);
					}
				break;		
			}							
		}
	}
	
	private final void checkAutoEq(L2PcInstance player,int newJobLevel)
	{	
		if(Config.CLASS_AUTO_EQUIP_AW && newJobLevel == 4 && !player.isSubClassActive())
			player.giveClassItems(player.getClassId());
	}
	
	/**
	 * Returns true if class change is possible
	 * @param oldCID current player ClassId
	 * @param newCID new ClassId
	 * @return true if class change is possible
	 */
	private static final boolean validateClassId(ClassId oldCID, ClassId newCID)
	{
		if (newCID == null || newCID.getRace() == null)
			return false;
		
		if (oldCID.equals(newCID.getParent()))
			return true;
		
		return false;
	}
	
	/**
	 * @param level - current skillId level (0 - start, 1 - first, etc)
	 * @return minimum player level required for next class transfer
	 */
	private static final int getMinLevel(int level)
	{
		switch (level)
		{
			case 0:
				return 20;
			case 1:
				return 40;
			case 2:
				return 76;
			default:
				return Integer.MAX_VALUE;
		}
	}
	
	/**
	 * Returns true if class change is possible
	 * @param oldCID current player ClassId
	 * @param val new class index
	 * @return
	 */
	private static final boolean validateClassId(ClassId oldCID, int val)
	{
		try
		{
			return validateClassId(oldCID, ClassId.values()[val]);
		}
		catch (Exception e)
		{
			// possible ArrayOutOfBoundsException
		}
		return false;
	}
}