/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jhellas.gameserver.model;

public final class Location
{
	public volatile int _x, _y, _z;
	private int _heading;

	public Location(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
	}

	public Location(int x, int y, int z, int heading)
	{
		_x = x;
		_y = y;
		_z = z;
		_heading = heading;
	}

	public int getX()
	{
		return _x;
	}

	public int getY()
	{
		return _y;
	}

	public int getZ()
	{
		return _z;
	}

	public int getHeading()
	{
		return _heading;
	}

	public void setXYZ(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
    public Location world2geo()
    {
        _x = _x - L2World.MAP_MIN_X >> 4;
        _y = _y - L2World.MAP_MIN_Y >> 4;
        return this;
    }

    public Location geo2world()
    {
        _x = (_x << 4) + L2World.MAP_MIN_X + 8;
        _y = (_y << 4) + L2World.MAP_MIN_Y + 8;
        return this;
    }
	@Override
	public String toString()
	{
		return "(" + _x + ", " + _y + ", " + _z + ")";
	}
	
	@Override
	public int hashCode()
	{
		return _x ^ _y ^ _z;
	}
	
	
	public Location setH(int h)
	{
		this._heading = h;
		return this;
	}

	public void set(int x, int y, int z, int h)
	{
		this._x = x;
		this._y = y;
		this._z = z;
		this._heading = h;
	}

	public void set(int x, int y, int z)
	{
		this._x = x;
		this._y = y;
		this._z = z;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Location)
		{
			Location point3D = (Location) o;
			return (point3D._x == _x && point3D._y == _y && point3D._z == _z);
		}
		
		return false;
	}
	
	public boolean equals(int x, int y, int z)
	{
		return _x == x && _y == y && _z == z;
	}
	
	@Override
	public Location clone()
	{
		return new Location(_x, _y, _z, _heading);
	}
	
	public double getDistance(Location location)
	{
		double dx = location.getX() - getX();
		double dy = location.getY() - getY();
		double dz = location.getZ() - getZ();

		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}