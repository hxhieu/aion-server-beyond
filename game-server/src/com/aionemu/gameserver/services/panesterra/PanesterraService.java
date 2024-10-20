package com.aionemu.gameserver.services.panesterra;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.base.BaseOccupier;
import com.aionemu.gameserver.model.base.BaseType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeRelatedBases;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.panesterra.ahserion.AhserionRaid;
import com.aionemu.gameserver.services.panesterra.ahserion.PanesterraFaction;
import com.aionemu.gameserver.services.panesterra.ahserion.PanesterraTeam;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Workflow for Panesterra sieges:
 * 1. Stop all outer bases
 * 2. Start artifacts as PEACE
 * 3. Start camps as PEACE or
 * Gab1_StartTimeCheck_03 + _04
 * STR_MSG_LDF5_Gab1_End01
 * TODO-List:
 * - Teleportation to other bases should be deactivated, if the target location is not occupied by the same faction
 * - Basic matchmaking, regarding priority slots
 * - Announcements during prep time
 * - Siege tests
 * 
 * @author Estrayl
 */
public class PanesterraService {

	private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");

	private final Map<PanesterraFaction, PanesterraTeam> activeFactionTeams = new ConcurrentHashMap<>();

	/**
	 * 1. Stop outer bases
	 * 2. Start faction camps
	 * 3. Despawn default corridors
	 * 4. Spawn corridors
	 * 
	 * @param loc
	 *          - Fortress location
	 */
	public void prepareFortressSiege(FortressLocation loc) {
		createTeams(loc.getLocationId());
		spawnAdvanceCorridors();
		prepareBases(loc.getTemplate().getSiegeRelatedBases());
	}

	private void prepareBases(SiegeRelatedBases relatedBases) {
		if (relatedBases == null)
			return;

		relatedBases.getBaseIds().stream().map(BaseService.getInstance()::getBaseLocation).forEach(baseLoc -> {
			switch (baseLoc.getType()) {
				case PANESTERRA -> BaseService.getInstance().capture(baseLoc.getId(), BaseOccupier.PEACE);
				case PANESTERRA_ARTIFACT -> BaseService.getInstance().start(baseLoc.getId());
				case PANESTERRA_FACTION_CAMP -> BaseService.getInstance().capture(baseLoc.getId(), baseLoc.getTemplate().getDefaultOccupier());
			}
		});
	}

	public void startFortressSiege(FortressLocation loc) {
		SiegeRelatedBases relatedBases = loc.getTemplate().getSiegeRelatedBases();
		if (relatedBases != null) {
			relatedBases.getBaseIds().stream().map(BaseService.getInstance()::getBaseLocation)
				.filter(baseLoc -> baseLoc.getType() == BaseType.PANESTERRA_ARTIFACT)
				.forEach(baseLoc -> BaseService.getInstance().capture(baseLoc.getId(), BaseOccupier.BALAUR));
		}
	}

	public void stopFortressSiege(FortressLocation loc) {
		// Remove Teams
		switch (loc.getLocationId()) {
			case 10111 -> removeTeams(PanesterraFaction.IVY_TEMPLE, PanesterraFaction.HIGHLAND_TEMPLE, PanesterraFaction.ALPINE_TEMPLE,
				PanesterraFaction.GRANDWEIR_TEMPLE);
			case 10211 -> removeTeams(PanesterraFaction.NOERREN_TEMPLE, PanesterraFaction.BOREALIS_TEMPLE, PanesterraFaction.MYRKREN_TEMPLE,
				PanesterraFaction.GLUMVEILEN_TEMPLE);
			case 10311 -> removeTeams(PanesterraFaction.MEMORIA_TEMPLE, PanesterraFaction.SYBILLINE_TEMPLE, PanesterraFaction.AUSTERITY_TEMPLE,
				PanesterraFaction.SERENITY_TEMPLE);
			case 10411 -> removeTeams(PanesterraFaction.NECROLUCE_TEMPLE, PanesterraFaction.ESMERAUDUS_TEMPLE, PanesterraFaction.VOLTAIC_TEMPLE,
				PanesterraFaction.ILLUMINATUS_TEMPLE);
		}
		// Change base states
		SiegeRelatedBases relatedBases = loc.getTemplate().getSiegeRelatedBases();
		if (relatedBases != null) {
			relatedBases.getBaseIds().stream().map(BaseService.getInstance()::getBaseLocation).forEach(baseLoc -> {
				switch (baseLoc.getType()) {
					case PANESTERRA -> BaseService.getInstance().capture(baseLoc.getId(), BaseOccupier.BALAUR);
					case PANESTERRA_ARTIFACT -> BaseService.getInstance().stop(baseLoc.getId());
					case PANESTERRA_FACTION_CAMP -> BaseService.getInstance().capture(baseLoc.getId(), BaseOccupier.PEACE);
				}
			});
		}
	}

	/**
	 * Up to 100 players from each faction, with a rank of 1-Star Officer or higher, can apply for the fortress siege.
	 * Out of those 100 slots, 1 will be reserved for the Governor, 50 will be reserved for 5-Star Officers and above, and
	 * the remaining 49 slots will be reserved for 1-Star Officers and above.
	 * The preparation time is set to 10 minutes, with the first 5 minutes being dedicated to the ranked applications
	 * and the remaining 5 minutes being open to all eligible applications.
	 * <p/>
	 * While some sources suggest that every slot except the Governor is randomly assigned, the matchmaking system has made
	 * sure to guarantee basic group formations by assigning at least one tank and one healer per theoretical group.
	 * <br/>
	 * <br/>
	 * Note: Starting as of v4.9 Rank-1 players were also allowed to apply after the initial 5 minutes.
	 */
	private void spawnAdvanceCorridors() {
		// Elyos
		PacketSendUtility.broadcastToMap(World.getInstance().getWorldMap(110070000).getMainWorldMapInstance(),
			SM_SYSTEM_MESSAGE.STR_MSG_SVS_INVADE_DIRECT_PORTAL_OPEN());
		// Governor exclusive
		spawnCorridor(SpawnEngine.newSingleTimeSpawn(110070000, 730940, 503.624f, 460.202f, 132.081f, (byte) 90), 257);
		// Advance Corridor for Contributors | Officer 5-Star to Commander
		spawnCorridor(SpawnEngine.newSingleTimeSpawn(110070000, 730942, 490.262f, 409.850f, 126.79f, (byte) 90), 256);
		// Walk of Honor | Officer 1-Star to 4-Star
		spawnCorridor(SpawnEngine.newSingleTimeSpawn(110070000, 731193, 518.142f, 409.967f, 126.79f, (byte) 90), 252);
		// Asmodians
		PacketSendUtility.broadcastToMap(World.getInstance().getWorldMap(120080000).getMainWorldMapInstance(),
			SM_SYSTEM_MESSAGE.STR_MSG_SVS_INVADE_DIRECT_PORTAL_OPEN());
		// Governor exclusive
		spawnCorridor(SpawnEngine.newSingleTimeSpawn(120080000, 730941, 342.298f, 251.135f, 98.553f, (byte) 0), 338);
		// Advance Corridor for Contributors | Officer 5-Star to Commander
		spawnCorridor(SpawnEngine.newSingleTimeSpawn(120080000, 730943, 393.321f, 236.963f, 93.113f, (byte) 0), 337);
		// Walk of Glory | Officer 1-Star to 4-Star
		spawnCorridor(SpawnEngine.newSingleTimeSpawn(120080000, 731194, 393.476f, 263.704f, 93.113f, (byte) 0), 336);
	}

	private void spawnCorridor(SpawnTemplate template, int staticId) {
		template.setStaticId(staticId);
		SpawnEngine.spawnObject(template, 1);
	}

	public void startAhserionRaid() {
		if (Stream.of(10111, 10211, 10311, 10411).anyMatch(id -> SiegeService.getInstance().getSiege(id) != null)) {
			log.error("Ahserion raid cannot be started while any Panesterra fortress is under siege.");
			return;
		}
		AhserionRaid.getInstance().start();
	}

	public void stopAhserionRaid() {
		AhserionRaid.getInstance().stop();
	}

	public void handleTeamElimination(PanesterraFaction faction) {
		PanesterraTeam team = activeFactionTeams.get(faction);
		if (team == null)
			return; // Using the //base command

		team.setIsEliminated(true);
		team.moveTeamMembersToOriginPosition();
	}

	private void createTeams(int siegeId) {
		// TODO: Ahserion Teams
		switch (siegeId) {
			case 10111 -> {
				activeFactionTeams.put(PanesterraFaction.IVY_TEMPLE, new PanesterraTeam(PanesterraFaction.IVY_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.HIGHLAND_TEMPLE, new PanesterraTeam(PanesterraFaction.HIGHLAND_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.ALPINE_TEMPLE, new PanesterraTeam(PanesterraFaction.ALPINE_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.GRANDWEIR_TEMPLE, new PanesterraTeam(PanesterraFaction.GRANDWEIR_TEMPLE));
			}
			case 10211 -> {
				activeFactionTeams.put(PanesterraFaction.NOERREN_TEMPLE, new PanesterraTeam(PanesterraFaction.NOERREN_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.BOREALIS_TEMPLE, new PanesterraTeam(PanesterraFaction.BOREALIS_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.MYRKREN_TEMPLE, new PanesterraTeam(PanesterraFaction.MYRKREN_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.GLUMVEILEN_TEMPLE, new PanesterraTeam(PanesterraFaction.GLUMVEILEN_TEMPLE));
			}
			case 10311 -> {
				activeFactionTeams.put(PanesterraFaction.MEMORIA_TEMPLE, new PanesterraTeam(PanesterraFaction.MEMORIA_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.SYBILLINE_TEMPLE, new PanesterraTeam(PanesterraFaction.SYBILLINE_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.AUSTERITY_TEMPLE, new PanesterraTeam(PanesterraFaction.AUSTERITY_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.SERENITY_TEMPLE, new PanesterraTeam(PanesterraFaction.SERENITY_TEMPLE));
			}
			case 10411 -> {
				activeFactionTeams.put(PanesterraFaction.NECROLUCE_TEMPLE, new PanesterraTeam(PanesterraFaction.NECROLUCE_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.ESMERAUDUS_TEMPLE, new PanesterraTeam(PanesterraFaction.ESMERAUDUS_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.VOLTAIC_TEMPLE, new PanesterraTeam(PanesterraFaction.VOLTAIC_TEMPLE));
				activeFactionTeams.put(PanesterraFaction.ILLUMINATUS_TEMPLE, new PanesterraTeam(PanesterraFaction.ILLUMINATUS_TEMPLE));
			}
		}
	}

	private void removeTeams(PanesterraFaction... factions) {
		for (PanesterraFaction faction : factions) {
			PanesterraTeam team = activeFactionTeams.remove(faction);
			team.moveTeamMembersToOriginPosition();
		}
	}

	private void spawnAhserionCorridors(int fortressId) {
		switch (fortressId) {
			case 10111 -> spawnCorridor(SpawnEngine.newSingleTimeSpawn(400020000, 802219, 1024.159f, 1076.24f, 1530.2688f, (byte) 90), 0);
			case 10211 -> spawnCorridor(SpawnEngine.newSingleTimeSpawn(400040000, 802221, 1024.159f, 1076.24f, 1530.2688f, (byte) 90), 0);
			case 10311 -> spawnCorridor(SpawnEngine.newSingleTimeSpawn(400050000, 802223, 1024.159f, 1076.24f, 1530.2688f, (byte) 90), 0);
			case 10411 -> spawnCorridor(SpawnEngine.newSingleTimeSpawn(400060000, 802225, 1024.159f, 1076.24f, 1530.2688f, (byte) 90), 0);
		}
	}

	public void onEnterPanesterra(Player player) {
		int siegeId = getSiegeId(player.getWorldId());
		if (siegeId == 0)
			return;
		// Player is in Transidium Annex or on an active map with an active siege
		if (siegeId == -1 || SiegeService.getInstance().isSiegeInProgress(siegeId)) {
			PanesterraTeam team = getPanesterraFactionTeam(player);
			if (team == null)
				TeleportService.moveToBindLocation(player);
			else if (team.isEliminated())
				team.movePlayerToOriginPosition(player);
			else
				player.setPanesterraFaction(team.getFaction());

		} else {
			// Check if the player's faction owns any related fortress
			PanesterraFaction faction = Stream.of(10111, 10211, 10311, 10411)
				.filter(id -> SiegeService.getInstance().getFortress(id).getRace() == SiegeRace.getByRace(player.getRace()))
				.map(PanesterraFaction::getByFortressId).findFirst().orElse(PanesterraFaction.PEACE);

			if (faction == PanesterraFaction.PEACE)
				TeleportService.moveToBindLocation(player);

			player.setPanesterraFaction(faction);
		}
	}

	private int getSiegeId(int worldId) {
		return switch (WorldMapType.getWorld(worldId)) {
			case BELUS -> 10111; // Belus
			case TRANSIDIUM_ANNEX -> -1; // Transidium Annex
			case ASPIDA -> 10211; // Aspida
			case ATANATOS -> 10311; // Atanatos
			case DISILLON -> 10411; // Disillon
			case null, default -> 0;
		};
	}

	public PanesterraTeam getPanesterraFactionTeam(Player player) {
		for (PanesterraTeam team : activeFactionTeams.values()) {
			if (team.isTeamMember(player.getObjectId()))
				return team;
		}
		return null;
	}

	public boolean isAhserionRaidStarted() {
		return AhserionRaid.getInstance().isStarted();
	}

	public int getTeamMemberCount(PanesterraFaction faction) {
		PanesterraTeam team = activeFactionTeams.get(faction);
		return team != null ? team.getMemberCount() : 0;
	}

	public PanesterraTeam getTeam(PanesterraFaction faction) {
		return activeFactionTeams.get(faction);
	}

	public static PanesterraService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {

		protected static final PanesterraService INSTANCE = new PanesterraService();
	}
}
