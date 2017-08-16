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
package com.l2jhellas.util.geodata;

import java.util.List;

import com.l2jhellas.util.geodata.L2Collections.Filter;

public interface Bunch<E>
{
	public int size();

	public Bunch<E> add(E value);

	public Bunch<E> remove(E value);

	public void clear();

	public boolean isEmpty();

	public E get(int index);

	public E set(int index, E value);

	public E remove(int index);

	public boolean contains(E value);

	public Bunch<E> addAll(Iterable<? extends E> c);

	public Bunch<E> addAll(E[] array);

	public Object[] moveToArray();

	public <T> T[] moveToArray(T[] array);

	public <T> T[] moveToArray(Class<T> clazz);

	public List<E> moveToList(List<E> list);

	public Bunch<E> cleanByFilter(Filter<E> filter);
}