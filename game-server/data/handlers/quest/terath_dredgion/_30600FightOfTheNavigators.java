package quest.terath_dredgion;

import static com.aionemu.gameserver.model.DialogAction.*;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.AbstractQuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Ritsu
 */
public class _30600FightOfTheNavigators extends AbstractQuestHandler {

	public _30600FightOfTheNavigators() {
		super(30600);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(205842).addOnQuestStart(questId); // Ancanus
		qe.registerQuestNpc(205842).addOnTalkEvent(questId);
		qe.registerQuestNpc(800325).addOnTalkEvent(questId);// Hejitor,Jerot
		qe.registerQuestNpc(219256).addOnKillEvent(questId);
		qe.registerQuestNpc(219257).addOnKillEvent(questId);
		qe.registerQuestNpc(219264).addOnKillEvent(questId);

	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int targetId = env.getTargetId();

		int dialogActionId = env.getDialogActionId();
		if (qs == null || qs.isStartable()) {
			if (targetId == 205842) {
				switch (dialogActionId) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					default:
						return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);// the station is important
			switch (targetId) {
				case 800325:
					switch (dialogActionId) {
						case QUEST_SELECT:
							return sendQuestDialog(env, 1352);
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 0);
						}
					}
					return false;
				case 205842:
					switch (dialogActionId) {
						case QUEST_SELECT:
							return sendQuestDialog(env, 2375);
						case SELECT_QUEST_REWARD: {
							if (var == 1) {
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 5);
							}
						}

					}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			switch (targetId) {
				case 205842:
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = 0;
		if (qs == null || qs.getStatus() != QuestStatus.START) {
			return false;
		} else {
			if (env.getVisibleObject() instanceof Npc)
				targetId = ((Npc) env.getVisibleObject()).getNpcId();
			switch (targetId) {
				case 219256:
				case 219257:
					if (qs.getQuestVarById(0) == 0) {
						qs.setQuestVar(1);
						updateQuestStatus(env);
						return true;
					}
					return false;
				case 219264:
					if (qs.getQuestVarById(0) == 1) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
			}
		}
		return false;
	}

}
