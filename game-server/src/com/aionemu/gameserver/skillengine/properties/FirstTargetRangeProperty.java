package com.aionemu.gameserver.skillengine.properties;

import com.aionemu.gameserver.geoEngine.collision.IgnoreProperties;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.properties.Properties.CastState;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.PositionUtil;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
public class FirstTargetRangeProperty {

	public static boolean set(Skill skill, Properties properties, CastState castState) {
		float firstTargetRange = properties.getFirstTargetRange();
		if (!skill.isFirstTargetRangeCheck())
			return true;

		Creature effector = skill.getEffector();
		Creature firstTarget = skill.getFirstTarget();

		if (properties.getFirstTarget() == FirstTargetAttribute.POINT) {
			 if (!GeoService.getInstance().canSee(effector, skill.getX(), skill.getY(), skill.getZ(), IgnoreProperties.of(effector.getRace()))) {
				 if (effector instanceof Player) {
					 PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_SKILL_OBSTACLE());
				 }
				 return false;
			 }
			 return true;
		}

		if (firstTarget == null)
			return false;

		if (firstTarget.equals(effector))
			return true;

		if (castState != CastState.CAST_START && !(effector instanceof Player)) // NPCs don't cancel skills once started, could be abused -> no range or geo to check
			return true;

		// on end cast check add revision distance value
		if (castState == CastState.CAST_END)
			firstTargetRange += properties.getRevisionDistance();

		// Add Weapon Range to distance
		if (properties.isAddWeaponRange())
			firstTargetRange += effector.getGameStats().getAttackRange().getCurrent() / 1000f;

		// fixes first hit sometimes incorrectly not going through
		if (effector.getMoveController().isInMove() && !firstTarget.getAggroList().isHating(effector))
			firstTargetRange += PositionUtil.calculateMaxCoveredDistance(effector, 50);

		if (!firstTarget.getEffectController().isInAnyAbnormalState(AbnormalState.CANT_MOVE_STATE)
			&& !PositionUtil.isInAttackRange(effector, firstTarget, firstTargetRange)) {
			if (effector instanceof Player)
				PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_SKILL_NOT_ENOUGH_DISTANCE());
			return false;
		}

		// TODO check for all targets too
		if (!GeoService.getInstance().canSee(effector, firstTarget)) {
			if (effector instanceof Player) {
				PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_SKILL_OBSTACLE());
			}
			return false;
		}
		return true;
	}

}
