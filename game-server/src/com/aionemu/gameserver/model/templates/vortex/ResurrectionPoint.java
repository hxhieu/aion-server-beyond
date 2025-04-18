package com.aionemu.gameserver.model.templates.vortex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectionPoint")
public class ResurrectionPoint {

	@XmlAttribute(name = "map")
	protected int map;
	@XmlAttribute(name = "x")
	protected float x;
	@XmlAttribute(name = "y")
	protected float y;
	@XmlAttribute(name = "z")
	protected float z;
	@XmlAttribute(name = "h")
	protected byte h;

	public int getWorldId() {
		return map;
	}

	public WorldPosition getResurrectionPoint() {
		WorldPosition home = new WorldPosition(map);
		home.setXYZH(x, y, z, h);
		return home;
	}

}
