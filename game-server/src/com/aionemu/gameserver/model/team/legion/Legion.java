package com.aionemu.gameserver.model.team.legion;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team.legion.LegionHistoryAction.Type;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ICON_INFO;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * @author Simple
 */
public class Legion extends AionObject {

	private String legionName;
	private int legionLevel = 1;
	private long contributionPoints = 0;
	private Collection<Integer> legionMembers = new ArrayList<>();
	private int onlineMembersCount = 0;
	private short deputyPermission = 0x1E0C;
	private short centurionPermission = 0x1C08;
	private short legionaryPermission = 0x1800;
	private short volunteerPermission = 0x800;
	private int disbandTime;
	private Announcement announcement;
	private LegionEmblem legionEmblem = new LegionEmblem();
	private final LegionWarehouse legionWarehouse;
	private final Map<Type, List<LegionHistoryEntry>> legionHistoryByType = new EnumMap<>(Type.class);
	private AtomicBoolean hasBonus = new AtomicBoolean(false);
	private int occupiedLegionDominion = 0;
	private int currentLegionDominion = 0;
	private int lastLegionDominion = 0;

	public Legion(int legionId, String legionName) {
		super(legionId);
		this.legionName = legionName;
		this.legionWarehouse = new LegionWarehouse(this);
		setHistory(Collections.emptyMap());
	}

	public int getLegionId() {
		return getObjectId();
	}

	@Override
	public String getName() {
		return legionName;
	}

	/**
	 * @param legionName
	 *          the legionName to set
	 */
	public void setName(String legionName) {
		this.legionName = legionName;
	}

	/**
	 * @param legionMembers
	 *          the legionMembers to set
	 */
	public void setLegionMembers(List<Integer> legionMembers) {
		this.legionMembers = legionMembers;
	}

	/**
	 * @return the legionMembers
	 */
	public Collection<Integer> getLegionMembers() {
		return legionMembers;
	}

	/**
	 * @return the online legionMembers
	 */
	public List<Player> getOnlineLegionMembers() {
		List<Player> onlineLegionMembers = new ArrayList<>();
		for (int legionMemberObjId : legionMembers) {
			Player onlineLegionMember = World.getInstance().getPlayer(legionMemberObjId);
			if (onlineLegionMember != null)
				onlineLegionMembers.add(onlineLegionMember);
		}
		return onlineLegionMembers;
	}

	public int getBrigadeGeneral() {
		for (int memberObjId : legionMembers) {
			LegionMember legionMember = LegionService.getInstance().getLegionMember(memberObjId);
			if (legionMember.getRank() == LegionRank.BRIGADE_GENERAL)
				return memberObjId;
		}
		return 0;
	}

	/**
	 * Add a legionMember to the legionMembers list
	 *
	 * @param legionMember
	 */
	public boolean addLegionMember(int playerObjId) {
		if (canAddMember()) {
			legionMembers.add(playerObjId);
			return true;
		}
		return false;
	}

	/**
	 * Delete a legionMember from the legionMembers list
	 *
	 * @param playerObjId
	 */
	public void deleteLegionMember(int playerObjId) {
		legionMembers.remove(playerObjId);
	}

	public int getOnlineMembersCount() {
		return this.onlineMembersCount;
	}

	public void decreaseOnlineMembersCount() {
		this.onlineMembersCount--;
	}

	public void increaseOnlineMembersCount() {
		this.onlineMembersCount++;
	}

	/**
	 * This method will set the permissions
	 *
	 * @param legionarPermission2
	 * @param centurionPermission1
	 * @param centurionPermission2
	 * @return true or false
	 */
	public boolean setLegionPermissions(short deputyPermission, short centurionPermission, short legionaryPermission, short volunteerPermission) {
		this.deputyPermission = deputyPermission;
		this.centurionPermission = centurionPermission;
		this.legionaryPermission = legionaryPermission;
		this.volunteerPermission = volunteerPermission;
		return true;
	}

	/**
	 * @return the deputyPermission
	 */
	public short getDeputyPermission() {
		return deputyPermission;
	}

	/**
	 * @return the centurionPermission
	 */
	public short getCenturionPermission() {
		return centurionPermission;
	}

	/**
	 * @return the legionarPermission
	 */
	public short getLegionaryPermission() {
		return legionaryPermission;
	}

	/**
	 * @return the volunteerPermission
	 */
	public short getVolunteerPermission() {
		return volunteerPermission;
	}

	/**
	 * @return the legionLevel
	 */
	public int getLegionLevel() {
		return legionLevel;
	}

	public void setLegionLevel(int legionLevel) {
		this.legionLevel = legionLevel;
		getLegionWarehouse().updateLimit(getWarehouseExpansions());
	}

	/**
	 * @param contributionPoints
	 *          the contributionPoints to set
	 */
	public void addContributionPoints(long contributionPoints) {
		this.contributionPoints += contributionPoints;
	}

	/**
	 * @param newPoints
	 */
	public void setContributionPoints(long contributionPoints) {
		this.contributionPoints = contributionPoints;
	}

	/**
	 * @return the contributionPoints
	 */
	public long getContributionPoints() {
		return contributionPoints;
	}

	/**
	 * This method will check whether a legion has enough members to level up
	 *
	 * @return true or false
	 */
	public boolean hasRequiredMembers() {
		int memberSize = getLegionMembers().size();
		switch (getLegionLevel()) {
			case 1:
				return memberSize >= LegionConfig.LEGION_LEVEL2_REQUIRED_MEMBERS;
			case 2:
				return memberSize >= LegionConfig.LEGION_LEVEL3_REQUIRED_MEMBERS;
			case 3:
				return memberSize >= LegionConfig.LEGION_LEVEL4_REQUIRED_MEMBERS;
			case 4:
				return memberSize >= LegionConfig.LEGION_LEVEL5_REQUIRED_MEMBERS;
			case 5:
				return memberSize >= LegionConfig.LEGION_LEVEL6_REQUIRED_MEMBERS;
			case 6:
				return memberSize >= LegionConfig.LEGION_LEVEL7_REQUIRED_MEMBERS;
			case 7:
				return memberSize >= LegionConfig.LEGION_LEVEL8_REQUIRED_MEMBERS;
		}
		return false;
	}

	/**
	 * This method will return the kinah price required to level up
	 *
	 * @return int
	 */
	public int getKinahPrice() {
		switch (getLegionLevel()) {
			case 1:
				return LegionConfig.LEGION_LEVEL2_REQUIRED_KINAH;
			case 2:
				return LegionConfig.LEGION_LEVEL3_REQUIRED_KINAH;
			case 3:
				return LegionConfig.LEGION_LEVEL4_REQUIRED_KINAH;
			case 4:
				return LegionConfig.LEGION_LEVEL5_REQUIRED_KINAH;
			case 5:
				return LegionConfig.LEGION_LEVEL6_REQUIRED_KINAH;
			case 6:
				return LegionConfig.LEGION_LEVEL7_REQUIRED_KINAH;
			case 7:
				return LegionConfig.LEGION_LEVEL8_REQUIRED_KINAH;
		}
		return 0;
	}

	/**
	 * This method will return the contribution points required to level up
	 *
	 * @return int
	 */
	public int getContributionPrice() {
		switch (getLegionLevel()) {
			case 1:
				return LegionConfig.LEGION_LEVEL2_REQUIRED_CONTRIBUTION;
			case 2:
				return LegionConfig.LEGION_LEVEL3_REQUIRED_CONTRIBUTION;
			case 3:
				return LegionConfig.LEGION_LEVEL4_REQUIRED_CONTRIBUTION;
			case 4:
				return LegionConfig.LEGION_LEVEL5_REQUIRED_CONTRIBUTION;
			case 5:
				return LegionConfig.LEGION_LEVEL6_REQUIRED_CONTRIBUTION;
			case 6:
				return LegionConfig.LEGION_LEVEL7_REQUIRED_CONTRIBUTION;
			case 7:
				return LegionConfig.LEGION_LEVEL8_REQUIRED_CONTRIBUTION;
		}
		return 0;
	}

	/**
	 * This method will return true if a legion is able to add a member
	 *
	 * @return
	 */
	private boolean canAddMember() {
		int memberSize = getLegionMembers().size();
		switch (getLegionLevel()) {
			case 1:
				return memberSize < LegionConfig.LEGION_LEVEL1_MAX_MEMBERS;
			case 2:
				return memberSize < LegionConfig.LEGION_LEVEL2_MAX_MEMBERS;
			case 3:
				return memberSize < LegionConfig.LEGION_LEVEL3_MAX_MEMBERS;
			case 4:
				return memberSize < LegionConfig.LEGION_LEVEL4_MAX_MEMBERS;
			case 5:
				return memberSize < LegionConfig.LEGION_LEVEL5_MAX_MEMBERS;
			case 6:
				return memberSize < LegionConfig.LEGION_LEVEL6_MAX_MEMBERS;
			case 7:
				return memberSize < LegionConfig.LEGION_LEVEL7_MAX_MEMBERS;
			case 8:
				return memberSize < LegionConfig.LEGION_LEVEL8_MAX_MEMBERS;
		}
		return false;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	/**
	 * @param disbandTime
	 *          the disbandTime to set
	 */
	public void setDisbandTime(int disbandTime) {
		this.disbandTime = disbandTime;
	}

	/**
	 * @return the disbandTime
	 */
	public int getDisbandTime() {
		return disbandTime;
	}

	/**
	 * @return true if currently disbanding
	 */
	public boolean isDisbanding() {
		return disbandTime > 0;
	}

	/**
	 * This function checks if object id is in list
	 *
	 * @param playerObjId
	 * @return true if ID is found in the list
	 */
	public boolean isMember(int playerObjId) {
		return legionMembers.contains(playerObjId);
	}

	/**
	 * @param legionEmblem
	 *          the legionEmblem to set
	 */
	public void setLegionEmblem(LegionEmblem legionEmblem) {
		this.legionEmblem = legionEmblem;
	}

	/**
	 * @return the legionEmblem
	 */
	public LegionEmblem getLegionEmblem() {
		return legionEmblem;
	}

	public LegionWarehouse getLegionWarehouse() {
		return legionWarehouse;
	}

	public int getWarehouseExpansions() {
		return getLegionLevel() - 1;
	}

	public List<LegionHistoryEntry> getHistory(Type type) {
		List<LegionHistoryEntry> history = legionHistoryByType.get(type);
		synchronized (history) {
			return new ArrayList<>(history);
		}
	}

	/**
	 * Adds the history entry at the top of the list and removes entries older than a year (except the ones on the first page)
	 */
	public List<LegionHistoryEntry> addHistory(LegionHistoryEntry entry) {
		List<LegionHistoryEntry> removedEntries = new ArrayList<>();
		Type type = entry.action().getType();
		List<LegionHistoryEntry> history = legionHistoryByType.get(type);
		synchronized (history) {
			history.addFirst(entry);
			if (type == Type.REWARD || type == Type.WAREHOUSE) {
				long maxMillis = System.currentTimeMillis() / 1000 - Duration.ofDays(365).toSeconds();
				while (history.getLast().epochSeconds() < maxMillis)
					removedEntries.add(history.removeLast());
			}
		}
		return removedEntries;
	}

	public void setHistory(Map<Type, List<LegionHistoryEntry>> history) {
		for (Type type : Type.values()) {
			List<LegionHistoryEntry> entries = history.get(type);
			legionHistoryByType.put(type, entries == null ? new ArrayList<>(1) : entries);
		}
	}

	public void addBonus() {
		List<Player> members = getOnlineLegionMembers();
		if (members.size() >= 10) {
			if (hasBonus.compareAndSet(false, true)) {
				for (Player member : members) {
					PacketSendUtility.sendPacket(member, new SM_ICON_INFO(1, true));
				}
			}
		}
	}

	public void removeBonus() {
		List<Player> members = getOnlineLegionMembers();
		if (members.size() < 10) {
			if (hasBonus.compareAndSet(true, false)) {
				for (Player member : members) {
					PacketSendUtility.sendPacket(member, new SM_ICON_INFO(1, false));
				}
			}
		}
	}

	public boolean hasBonus() {
		return hasBonus.get();
	}

	public int getOccupiedLegionDominion() {
		return occupiedLegionDominion;
	}

	public int getCurrentLegionDominion() {
		return currentLegionDominion;
	}

	public int getLastLegionDominion() {
		return lastLegionDominion;
	}

	public void setOccupiedLegionDominion(int occupiedLegionDominion) {
		this.occupiedLegionDominion = occupiedLegionDominion;
	}

	public void setCurrentLegionDominion(int currentLegionDominion) {
		this.currentLegionDominion = currentLegionDominion;
	}

	public void setLastLegionDominion(int lastLegionDominion) {
		this.lastLegionDominion = lastLegionDominion;
	}

	@Override
	public String toString() {
		return "Legion [id=" + getObjectId() + ", name=" + getName() + "]";
	}

	public record Announcement(String message, Timestamp time) {}
}
