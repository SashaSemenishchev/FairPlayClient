/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Copyright © SashaSemenishchev 2023
 * Contact: sashasemenishchev@protonmail.com
 */


import java.util.HashMap;

public class CappedHashMap<K, V> extends HashMap<K, V> {

    private final int capSize;

    public CappedHashMap(int capSize) {
        this.capSize = capSize;
    }
    @Override
    public V put(K key, V value) {
        V prev = super.put(key, value);
        if(size() > capSize && prev == null) {
            entrySet().stream().findAny().ifPresent(element -> {
                entrySet().remove(element);
            });
        }
        return prev;
    }
}
