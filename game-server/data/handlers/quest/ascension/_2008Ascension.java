package quest.ascension;

import static com.aionemu.gameserver.model.DialogAction.*;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.animations.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ASCENSION_MORPH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.questEngine.handlers.AbstractQuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.reward.WebRewardService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * @author MrPoke
 */
public class _2008Ascension extends AbstractQuestHandler {

	public _2008Ascension() {
		super(2008);
	}

	@Override
	public void register() {
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS)
			return;
		qe.registerOnLevelChanged(questId);
		qe.registerOnQuestCompleted(questId);
		qe.registerQuestNpc(203550).addOnTalkEvent(questId);
		qe.registerQuestNpc(790003).addOnTalkEvent(questId);
		qe.registerQuestNpc(790002).addOnTalkEvent(questId);
		qe.registerQuestNpc(203546).addOnTalkEvent(questId);
		qe.registerQuestNpc(205020).addOnTalkEvent(questId);
		qe.registerQuestNpc(205040).addOnKillEvent(questId);
		qe.registerQuestNpc(205041).addOnKillEvent(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnDie(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if (targetId == 205040) { // Guardian Assassin
			env.getVisibleObject().getController().delete();
			if (var >= 51 && var <= 53) {
				qs.setQuestVar(qs.getQuestVars().getQuestVars() + 1);
				updateQuestStatus(env);
				return true;
			} else if (var == 54) {
				qs.setQuestVar(5);
				updateQuestStatus(env);
				Npc mob = (Npc) spawn(205041, player, 301f, 259f, 205.5f, (byte) 0);
				mob.getAggroList().addHate(player, 1000);
				return true;
			}
		} else if (targetId == 205041 && var == 5) {
			playQuestMovie(env, 152);
			player.getWorldMapInstance().forEachNpc(npc -> npc.getController().delete());
			spawn(203550, player, 301.92999f, 274.26001f, 205.7f, (byte) 0);
			qs.setQuestVar(6);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVars();
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203550) {
				switch (env.getDialogActionId()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
						else if (var == 4)
							return sendQuestDialog(env, 2375);
						else if (var == 6)
							return sendQuestDialog(env, 2716);
						return false;
					case SELECT5_1:
						if (var == 4) {
							playQuestMovie(env, 57);
							removeQuestItem(env, 182203009, 1);
							removeQuestItem(env, 182203010, 1);
							removeQuestItem(env, 182203011, 1);
						}
						return false;
					case SETPRO1:
						qs.setQuestVar(1);
						updateQuestStatus(env);
						TeleportService.teleportTo(player, 220010000, 585.5074f, 2416.0312f, 278.625f, (byte) 102, TeleportAnimation.FADE_OUT_BEAM);
						return true;
					case SETPRO5:
						if (var == 4) {
							qs.setQuestVar(99);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
							// Create instance
							WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(WorldMapType.ATAXIAR_B.getId(), player);
							TeleportService.teleportTo(player, newInstance, 457.65f, 426.8f, 230.4f);
							return true;
						}
						return false;
					case SETPRO6:
						int dialogPageId = ClassChangeService.getClassSelectionDialogPageId(player.getRace(), player.getPlayerClass());
						if (var == 6 && dialogPageId != 0)
							return sendQuestDialog(env, dialogPageId);
						return false;
					case SETPRO7:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.GLADIATOR);
					case SETPRO8:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.TEMPLAR);
					case SETPRO9:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.ASSASSIN);
					case SETPRO10:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.RANGER);
					case SETPRO11:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.SORCERER);
					case SETPRO12:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.SPIRIT_MASTER);
					case SETPRO13:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.CHANTER);
					case SETPRO14:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.CLERIC);
					case SETPRO15:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.GUNNER);
					case SETPRO16:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.BARD);
					case SETPRO17:
						return var == 6 && setPlayerClass(env, qs, PlayerClass.RIDER);
				}
			} else if (targetId == 790003) {
				switch (env.getDialogActionId()) {
					case QUEST_SELECT:
						if (var == 1)
							return sendQuestDialog(env, 1352);
						return false;
					case SETPRO2:
						if (var == 1) {
							if (player.getInventory().getItemCountByItemId(182203009) == 0)
								giveQuestItem(env, 182203009, 1);
							qs.setQuestVar(2);
							updateQuestStatus(env);
							TeleportService.teleportTo(player, 220010000, 940.74475f, 2295.5305f, 265.65674f, (byte) 46, TeleportAnimation.FADE_OUT_BEAM);
							return true;
						}
						return false;
				}
			} else if (targetId == 790002) {
				switch (env.getDialogActionId()) {
					case QUEST_SELECT:
						if (var == 2)
							return sendQuestDialog(env, 1693);
						return false;
					case SETPRO3:
						if (var == 2) {
							if (player.getInventory().getItemCountByItemId(182203010) == 0)
								giveQuestItem(env, 182203010, 1);
							qs.setQuestVar(3);
							updateQuestStatus(env);
							TeleportService.teleportTo(player, 220010000, 1111.5637f, 1719.2745f, 270.114256f, (byte) 114, TeleportAnimation.FADE_OUT_BEAM);
							return true;
						}
						return false;
				}
			} else if (targetId == 203546) {
				switch (env.getDialogActionId()) {
					case QUEST_SELECT:
						if (var == 3)
							return sendQuestDialog(env, 2034);
						return false;
					case SETPRO4:
						if (var == 3) {
							if (player.getInventory().getItemCountByItemId(182203011) == 0)
								giveQuestItem(env, 182203011, 1);
							qs.setQuestVar(4);
							updateQuestStatus(env);
							TeleportService.teleportTo(player, 220010000, 383.10248f, 1895.3093f, 327.625f, (byte) 59, TeleportAnimation.FADE_OUT_BEAM);
							return true;
						}
						return false;
				}
			} else if (targetId == 205020) {
				switch (env.getDialogActionId()) {
					case QUEST_SELECT:
						if (var == 99) {
							SkillEngine.getInstance().applyEffectDirectly(257, player, player);
							player.setState(CreatureState.FLYING);
							player.unsetState(CreatureState.ACTIVE);
							player.setFlightTeleportId(3001);
							PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 3001, 0));
							qs.setQuestVar(50);
							updateQuestStatus(env);
							ThreadPoolManager.getInstance().schedule(() -> {
								qs.setQuestVar(51);
								updateQuestStatus(env);
								List<Npc> mobs = new ArrayList<>();
								mobs.add((Npc) spawn(205040, player, 294f, 277f, 207f, (byte) 0));
								mobs.add((Npc) spawn(205040, player, 305f, 279f, 206.5f, (byte) 0));
								mobs.add((Npc) spawn(205040, player, 298f, 253f, 205.7f, (byte) 0));
								mobs.add((Npc) spawn(205040, player, 306f, 251f, 206f, (byte) 0));
								for (Npc mob : mobs) {
									mob.getAggroList().addHate(player, 1000);
								}
							}, 43000);
							return true;
						}
						return false;
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203550) {
				switch (env.getDialogActionId()) {
					case SELECTED_QUEST_NOREWARD:
						if (player.getWorldId() == 320020000) {
							TeleportService.teleportTo(player, 220010000, 386.03476f, 1893.9309f, 327.62283f, (byte) 59, TeleportAnimation.FADE_OUT_BEAM);
						}
						break;
				}
				return sendQuestEndDialog(env); // finishes quest or shows reward selection
			}
		}
		return false;
	}

	@Override
	public void onLevelChangedEvent(Player player) {
		defaultOnLevelChangedEvent(player);
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (player.getWorldId() == 320020000)
				PacketSendUtility.sendPacket(player, new SM_ASCENSION_MORPH(1));
			else if (var > 4 && var != 6) // 6 is class selection, quest should not reset anymore after you killed hellion
				changeQuestStep(env, var, 4);
		}
		return false;
	}

	private boolean setPlayerClass(QuestEnv env, QuestState qs, PlayerClass playerClass) {
		if (ClassChangeService.setClass(env.getPlayer(), playerClass)) {
			changeQuestStep(env, 6, 6, true); // reward
			return sendQuestDialog(env, 5);
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START && player.getWorldId() == 320020000) {
			int var = qs.getQuestVars().getQuestVars();
			if (var > 4)
				changeQuestStep(env, var, 4);
		}
		return false;
	}

	@Override
	public void onQuestCompletedEvent(QuestEnv env) {
		if (env.getQuestId() == questId) {
			Player player = env.getPlayer();
			player.getCommonData().updateDaeva();
			if (WebRewardService.MaxLevelReward.isPendingAscension(player))
				WebRewardService.MaxLevelReward.reward(player);
		}
	}
}
