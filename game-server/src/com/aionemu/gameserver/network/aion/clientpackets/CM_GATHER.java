package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.Set;

import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer
 */
public class CM_GATHER extends AionClientPacket {

	private int actionId;

	public CM_GATHER(int opcode, Set<State> validStates) {
		super(opcode, validStates);
	}

	@Override
	protected void readImpl() {
		actionId = readD();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		VisibleObject target = player.getTarget();
		if (!(target instanceof Gatherable gatherable)) {
			if (target != null || actionId != -1) // client sends actionId -1 twice for cancellation requests, the second one after deselecting the target
				AuditLogger.log(player, "tried to gather from " + target + " (action ID: " + actionId + ")");
			return;
		}
		switch (actionId) {
			case -1 -> {
				if (gatherable.getController().getGatheringPlayerId() == player.getObjectId())
					gatherable.getController().cancelGathering();
			}
			case 0 -> gatherable.getController().startGathering(player);
			// case 128 -> TODO identify corresponding action
			default -> LoggerFactory.getLogger(getClass()).warn("Unhandled gathering action ID {} (sent by {} at {})", actionId, player, player.getPosition());
		}
	}
}
