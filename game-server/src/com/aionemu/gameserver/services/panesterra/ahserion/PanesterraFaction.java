package com.aionemu.gameserver.services.panesterra.ahserion;

import com.aionemu.gameserver.model.TribeClass;

/**
 * @author Estrayl
 */
public enum PanesterraFaction {

	BALAUR(TribeClass.GAB1_MONSTER, TribeClass.GAB1_SUB_DRAKAN),

	BELUS(TribeClass.GAB1_01_POINT_01, TribeClass.GAB1_SUB_DEST_69),
	IVY_TEMPLE(TribeClass.GAB1_01_POINT_02),
	HIGHLAND_TEMPLE(TribeClass.GAB1_01_POINT_03),
	ALPINE_TEMPLE(TribeClass.GAB1_01_POINT_04),
	GRANDWEIR_TEMPLE(TribeClass.GAB1_01_POINT_05),

	ASPIDA(TribeClass.GAB1_02_POINT_01, TribeClass.GAB1_SUB_DEST_70),
	NOERREN_TEMPLE(TribeClass.GAB1_02_POINT_02),
	BOREALIS_TEMPLE(TribeClass.GAB1_02_POINT_03),
	MYRKREN_TEMPLE(TribeClass.GAB1_02_POINT_04),
	GLUMVEILEN_TEMPLE(TribeClass.GAB1_02_POINT_05),

	ATANATOS(TribeClass.GAB1_03_POINT_01, TribeClass.GAB1_SUB_DEST_71),
	MEMORIA_TEMPLE(TribeClass.GAB1_03_POINT_02),
	SYBILLINE_TEMPLE(TribeClass.GAB1_03_POINT_03),
	AUSTERITY_TEMPLE(TribeClass.GAB1_03_POINT_04),
	SERENITY_TEMPLE(TribeClass.GAB1_03_POINT_05),

	DISILLON(TribeClass.GAB1_04_POINT_01, TribeClass.GAB1_SUB_DEST_72),
	NECROLUCE_TEMPLE(TribeClass.GAB1_04_POINT_02),
	ESMERAUDUS_TEMPLE(TribeClass.GAB1_04_POINT_03),
	VOLTAIC_TEMPLE(TribeClass.GAB1_04_POINT_04),
	ILLUMINATUS_TEMPLE(TribeClass.GAB1_04_POINT_05),

	PEACE(TribeClass.GAB1_PEACE);

	private final TribeClass tribe;
	private final TribeClass subTribe;

	PanesterraFaction(TribeClass tribe, TribeClass subTribe) {
		this.tribe = tribe;
		this.subTribe = subTribe;
	}

	PanesterraFaction(TribeClass tribe) {
		this(tribe, null);
	}

	public TribeClass getTribe() {
		return tribe;
	}

	public TribeClass getSubTribe() {
		return subTribe;
	}

	public static PanesterraFaction getByFortressId(int fortressId) {
		return switch (fortressId) {
			case 10111 -> BELUS;
			case 10211 -> ASPIDA;
			case 10311 -> ATANATOS;
			case 10411 -> DISILLON;
			default -> PEACE;
		};
	}
}
