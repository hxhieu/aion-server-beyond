package com.aionemu.gameserver.services.rift;

import com.aionemu.gameserver.model.Race;

/**
 * @author Source
 */
public enum RiftEnum {

	KAISINEL_AM(1170, "KAISINEL_AM", "KAISINEL_AS", 24, 45, 65, Race.ASMODIANS, true),
	ELTNEN_AM(2120, "ELTNEN_AM", "MORHEIM_AS", 12, 20, 65, Race.ASMODIANS),
	ELTNEN_BM(2121, "ELTNEN_BM", "MORHEIM_BS", 20, 20, 65, Race.ASMODIANS),
	ELTNEN_CM(2122, "ELTNEN_CM", "MORHEIM_CS", 35, 20, 65, Race.ASMODIANS),
	ELTNEN_DM(2123, "ELTNEN_DM", "MORHEIM_DS", 35, 20, 65, Race.ASMODIANS),
	ELTNEN_EM(2124, "ELTNEN_EM", "MORHEIM_ES", 45, 20, 65, Race.ASMODIANS),
	ELTNEN_FM(2125, "ELTNEN_FM", "MORHEIM_FS", 50, 20, 65, Race.ASMODIANS),
	ELTNEN_GM(2126, "ELTNEN_GM", "MORHEIM_GS", 50, 20, 65, Race.ASMODIANS),
	HEIRON_AM(2140, "HEIRON_AM", "BELUSLAN_AS", 24, 20, 65, Race.ASMODIANS),
	HEIRON_BM(2141, "HEIRON_BM", "BELUSLAN_BS", 36, 20, 65, Race.ASMODIANS),
	HEIRON_CM(2142, "HEIRON_CM", "BELUSLAN_CS", 48, 20, 65, Race.ASMODIANS),
	HEIRON_DM(2143, "HEIRON_DM", "BELUSLAN_DS", 48, 20, 65, Race.ASMODIANS),
	HEIRON_EM(2144, "HEIRON_EM", "BELUSLAN_ES", 60, 20, 65, Race.ASMODIANS),
	HEIRON_FM(2145, "HEIRON_FM", "BELUSLAN_FS", 72, 20, 65, Race.ASMODIANS),
	HEIRON_GM(2146, "HEIRON_GM", "BELUSLAN_GS", 72, 20, 65, Race.ASMODIANS),
	INGGISON_AM(2150, "INGGISON_AM", "GELKMAROS_AS", 150, 20, 65, Race.ASMODIANS),
	INGGISON_BM(2151, "INGGISON_BM", "GELKMAROS_BS", 150, 20, 65, Race.ASMODIANS),
	INGGISON_CM(2152, "INGGISON_CM", "GELKMAROS_CS", 150, 20, 65, Race.ASMODIANS),
	INGGISON_DM(2153, "INGGISON_DM", "GELKMAROS_DS", 150, 20, 65, Race.ASMODIANS),
	CYGNEA_AM(2170, "CYGNEA_AM", "ENSHAR_AS", 12, 50, 65, Race.ASMODIANS),
	CYGNEA_BM(2171, "CYGNEA_BM", "ENSHAR_BS", 36, 50, 65, Race.ASMODIANS),
	CYGNEA_CM(2172, "CYGNEA_CM", "ENSHAR_CS", 48, 55, 65, Race.ASMODIANS),
	CYGNEA_DM(2173, "CYGNEA_DM", "ENSHAR_DS", 48, 55, 65, Race.ASMODIANS),
	CYGNEA_EM(2174, "CYGNEA_EM", "ENSHAR_ES", 48, 55, 65, Race.ASMODIANS),
	CYGNEA_FM(2175, "CYGNEA_FM", "ENSHAR_FS", 48, 55, 65, Race.ASMODIANS),
	CYGNEA_GM(2176, "CYGNEA_GM", "ENSHAR_GS", 144, 60, 65, Race.ASMODIANS, false, true),
	CYGNEA_HM(2177, "CYGNEA_HM", "ENSHAR_HS", 144, 60, 65, Race.ASMODIANS, false, true),
	CYGNEA_IM(2178, "CYGNEA_IM", "ENSHAR_IS", 144, 60, 65, Race.ASMODIANS, false, true),
	CYGNEA_VIL1M(2189, "CYGNEA_VIL1M", "ENSHAR_VIL1S", 72, 55, 65, Race.ASMODIANS, false, false, true),
	CYGNEA_VIL2M(2190, "CYGNEA_VIL2M", "ENSHAR_VIL2S", 72, 55, 65, Race.ASMODIANS, false, false, true),
	CYGNEA_VIL3M(2191, "CYGNEA_VIL3M", "ENSHAR_VIL3S", 72, 55, 65, Race.ASMODIANS, false, false, true),
	MARCHUTAN_AM(1280, "MARCHUTAN_AM", "MARCHUTAN_AS", 24, 45, 65, Race.ELYOS, true),
	MORHEIM_AM(2220, "MORHEIM_AM", "ELTNEN_AS", 12, 20, 65, Race.ELYOS),
	MORHEIM_BM(2221, "MORHEIM_BM", "ELTNEN_BS", 20, 20, 65, Race.ELYOS),
	MORHEIM_CM(2222, "MORHEIM_CM", "ELTNEN_CS", 35, 20, 65, Race.ELYOS),
	MORHEIM_DM(2223, "MORHEIM_DM", "ELTNEN_DS", 35, 20, 65, Race.ELYOS),
	MORHEIM_EM(2224, "MORHEIM_EM", "ELTNEN_ES", 45, 20, 65, Race.ELYOS),
	MORHEIM_FM(2225, "MORHEIM_FM", "ELTNEN_FS", 50, 20, 65, Race.ELYOS),
	MORHEIM_GM(2226, "MORHEIM_GM", "ELTNEN_GS", 50, 20, 65, Race.ELYOS),
	BELUSLAN_AM(2240, "BELUSLAN_AM", "HEIRON_AS", 24, 20, 65, Race.ELYOS),
	BELUSLAN_BM(2241, "BELUSLAN_BM", "HEIRON_BS", 36, 20, 65, Race.ELYOS),
	BELUSLAN_CM(2242, "BELUSLAN_CM", "HEIRON_CS", 48, 20, 65, Race.ELYOS),
	BELUSLAN_DM(2243, "BELUSLAN_DM", "HEIRON_DS", 48, 20, 65, Race.ELYOS),
	BELUSLAN_EM(2244, "BELUSLAN_EM", "HEIRON_ES", 60, 20, 65, Race.ELYOS),
	BELUSLAN_FM(2245, "BELUSLAN_FM", "HEIRON_FS", 72, 20, 65, Race.ELYOS),
	BELUSLAN_GM(2246, "BELUSLAN_GM", "HEIRON_GS", 72, 20, 65, Race.ELYOS),
	GELKMAROS_AM(2270, "GELKMAROS_AM", "INGGISON_AS", 150, 20, 65, Race.ELYOS),
	GELKMAROS_BM(2271, "GELKMAROS_BM", "INGGISON_BS", 150, 20, 65, Race.ELYOS),
	GELKMAROS_CM(2272, "GELKMAROS_CM", "INGGISON_CS", 150, 20, 65, Race.ELYOS),
	GELKMAROS_DM(2273, "GELKMAROS_DM", "INGGISON_DS", 150, 20, 65, Race.ELYOS),
	ENSHAR_AM(2280, "ENSHAR_AM", "CYGNEA_AS", 12, 50, 65, Race.ELYOS),
	ENSHAR_BM(2281, "ENSHAR_BM", "CYGNEA_BS", 36, 50, 65, Race.ELYOS),
	ENSHAR_CM(2282, "ENSHAR_CM", "CYGNEA_CS", 48, 55, 65, Race.ELYOS),
	ENSHAR_DM(2283, "ENSHAR_DM", "CYGNEA_DS", 48, 55, 65, Race.ELYOS),
	ENSHAR_EM(2284, "ENSHAR_EM", "CYGNEA_ES", 48, 55, 65, Race.ELYOS),
	ENSHAR_FM(2285, "ENSHAR_FM", "CYGNEA_FS", 48, 55, 65, Race.ELYOS),
	ENSHAR_GM(2286, "ENSHAR_GM", "CYGNEA_GS", 144, 60, 65, Race.ELYOS, false, true),
	ENSHAR_HM(2287, "ENSHAR_HM", "CYGNEA_HS", 144, 60, 65, Race.ELYOS, false, true),
	ENSHAR_IM(2288, "ENSHAR_IM", "CYGNEA_IS", 144, 60, 65, Race.ELYOS, false, true),
	ENSHAR_VIL1M(2289, "ENSHAR_VIL1M", "CYGNEA_VIL1S", 72, 55, 65, Race.ELYOS, false, false, true),
	ENSHAR_VIL2M(2290, "ENSHAR_VIL2M", "CYGNEA_VIL2S", 72, 55, 65, Race.ELYOS, false, false, true),
	ENSHAR_VIL3M(2291, "ENSHAR_VIL3M", "CYGNEA_VIL3S", 72, 55, 65, Race.ELYOS, false, false, true);

	private int id;
	private String master;
	private String slave;
	private int entries;
	private int minLevel;
	private int maxLevel;
	private Race destination;
	private boolean vortex;
	private boolean canBeVolatile;
	private boolean isInvasionRift;

	RiftEnum(int id, String master, String slave, int entries, int minLevel, int maxLevel, Race destination) {
		this(id, master, slave, entries, minLevel, maxLevel, destination, false, false, false);
	}

	RiftEnum(int id, String master, String slave, int entries, int minLevel, int maxLevel, Race destination, boolean vortex) {
		this(id, master, slave, entries, minLevel, maxLevel, destination, vortex, false, false);
	}

	RiftEnum(int id, String master, String slave, int entries, int minLevel, int maxLevel, Race destination, boolean vortex, boolean canBeVolatile) {
		this(id, master, slave, entries, minLevel, maxLevel, destination, vortex, canBeVolatile, false);
	}

	RiftEnum(int id, String master, String slave, int entries, int minLevel, int maxLevel, Race destination, boolean vortex,
			 boolean canBeVolatile, boolean isInvasionRift) {
		this.id = id;
		this.master = master;
		this.slave = slave;
		this.entries = entries;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.destination = destination;
		this.vortex = vortex;
		this.canBeVolatile = canBeVolatile;
		this.isInvasionRift = isInvasionRift;
	}

	public static RiftEnum getRift(int id) throws IllegalArgumentException {
		for (RiftEnum rift : RiftEnum.values()) {
			if (rift.getId() == id) {
				return rift;
			}
		}
		throw new IllegalArgumentException("Unsupported rift id: " + id);
	}

	public static RiftEnum getVortex(Race race) throws IllegalArgumentException {
		for (RiftEnum rift : RiftEnum.values()) {
			if (rift.isVortex() && rift.getDestination().equals(race)) {
				return rift;
			}
		}
		throw new IllegalArgumentException("Unsupported vortex race: " + race);
	}

	/**
	 * @return rift id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the master
	 */
	public String getMaster() {
		return master;
	}

	/**
	 * @return the slave
	 */
	public String getSlave() {
		return slave;
	}

	/**
	 * @return the entries
	 */
	public int getEntries() {
		return entries;
	}

	/**
	 * @return the minLevel
	 */
	public int getMinLevel() {
		return minLevel;
	}

	/**
	 * @return the maxLevel
	 */
	public int getMaxLevel() {
		return maxLevel;
	}

	/**
	 * @return the destination
	 */
	public Race getDestination() {
		return destination;
	}

	public boolean isVortex() {
		return vortex;
	}

	public boolean canBeVolatile() {
		return canBeVolatile;
	}

	public boolean isInvasionRift() {
		return isInvasionRift;
	}

}
