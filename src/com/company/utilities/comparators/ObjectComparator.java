package com.company.utilities.comparators;

import java.util.Comparator;

/**
 * Used to compare objects even if they do not implement the {@link Comparable} interface
 * <list>
 *     <li>
 *         if objects are Comparable: uses the in-build compareTo method
 *     </li>
 *     <li>
 *         if objects are NOT Comparable: compares their hashes instead
 *     </li>
 * </list>
 */
public class ObjectComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        try {
            return ((Comparable<Object>) o1).compareTo(o2);
        } catch (ClassCastException e) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    }
}
