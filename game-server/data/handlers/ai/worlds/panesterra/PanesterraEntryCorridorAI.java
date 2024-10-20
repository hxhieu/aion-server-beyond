package ai.worlds.panesterra;

import static com.aionemu.gameserver.model.DialogAction.SETPRO1;

import java.util.stream.Stream;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.event.EventService;
import com.aionemu.gameserver.services.panesterra.PanesterraService;
import com.aionemu.gameserver.services.panesterra.ahserion.PanesterraFaction;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldPosition;

import ai.GeneralNpcAI;

/**
 * @author Estrayl
 */
@AIName("panesterra_entry_corridor")
public class PanesterraEntryCorridorAI extends GeneralNpcAI {

	private int relatedFortressId;

	public PanesterraEntryCorridorAI(Npc owner) {
		super(owner);
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		relatedFortressId = switch (getNpcId()) {
			case 730946, 730950 -> 10111;
			case 730947, 730951 -> 10211;
			case 730948, 730952 -> 10311;
			case 730949, 730953 -> 10411;
			default -> 0;
		};
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogActionId, int questId, int extendedRewardIndex) {
		if (dialogActionId == SETPRO1 && canTeleport(player)) {
			// teleportToFortress(player);
			teleportToEventLocation(player);
		}
		return true;
	}

	private boolean canTeleport(Player player) {
		if (player.getLevel() < 65) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_TELEPOTER_GAB1_USER04());
			return false;
		}
		// TODO: Remove after event
		if (EventService.getInstance().getActiveEvents().stream()
			.anyMatch(event -> event.getEventTemplate().getName().equalsIgnoreCase("Beyond Aion Birthday Event"))) {
			return true;
		}
		// Don't allow access if any fortress is under siege
		if (Stream.of(10111, 10211, 10311, 10411).anyMatch(id -> SiegeService.getInstance().getSiege(id) != null)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_READY_PANGAEA());
			return false;
		}

		if (SiegeService.getInstance().getFortress(relatedFortressId).getRace() != SiegeRace.getByRace(player.getRace())) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE());
			return false;
		}
		return true;
	}

	private void teleportToFortress(Player player) {
		// TODO
		player.setPanesterraFaction(PanesterraFaction.getByFortressId(relatedFortressId));
	}

	private void teleportToEventLocation(Player player) {
		switch (player.getRace()) {
			case ELYOS -> {
				// North + South
				WorldPosition pos = Rnd.nextBoolean() ? new WorldPosition(400020000, 11.173f, 1024.187f, 1428.60f, (byte) 0)
					: new WorldPosition(400020000, 2037.754f, 1023.808f, 1428.60f, (byte) 0);
				TeleportService.teleportTo(player, pos);
			}
			case ASMODIANS -> {
				// West + East
				WorldPosition pos = Rnd.nextBoolean() ? new WorldPosition(400020000, 1023.702f, 10.531f, 1428.60f, (byte) 90)
					: new WorldPosition(400020000, 1024.310f, 2036.593f, 1428.60f, (byte) 90);
				TeleportService.teleportTo(player, pos);
			}
		}
		PanesterraService.getInstance().onEnterPanesterra(player);
	}
}
