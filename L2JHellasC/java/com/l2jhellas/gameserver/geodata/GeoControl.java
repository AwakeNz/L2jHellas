package com.l2jhellas.gameserver.geodata;

import java.util.HashMap;

import com.l2jhellas.gameserver.model.L2Territory;

public interface GeoControl
{
	public abstract L2Territory getGeoPos();

	public abstract void setGeoPos(L2Territory value);

	public abstract HashMap<Long, Byte> getGeoAround();

	public abstract void setGeoAround(HashMap<Long, Byte> value);

	public abstract boolean isGeoCloser();
}
