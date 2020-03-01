package org.treblereel.gwt.crysknife.client.internal.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/20
 */
public class ImmutableMultimap<K, V> extends Multimap<K, V> {

    private ImmutableMultimap() {
        super(Collections.emptyMap());
    }

    private ImmutableMultimap(Map<K, List<V>> holder) {
        super(holder);
    }

    public static <K, V> ImmutableMultimap<K, V> of() {
        return new ImmutableMultimap();
    }

    public static <K, V> ImmutableMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
        return new ImmutableMultimap<K, V>((Map<K, List<V>>) new HashMap<K, V>((Map<? extends K, ? extends V>) multimap.holder));
    }

    public void put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Collection<V> values() {
        return values().stream().collect(Collectors.toSet());
    }

    public List<V> get(K key) {
        return super.get(key).stream().collect(Collectors.toList());
    }

    public void remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Collection<K> keys() {
        return super.keySet().stream().collect(Collectors.toSet());
    }

    public Set<K> keySet() {
        return super.keySet().stream().collect(Collectors.toSet());
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public void removeAll(K key) {
        throw new UnsupportedOperationException();
    }

    public Collection<Map.Entry<K, V>> entries() {
        Map<K, V> map = new TreeMap<>();
        super.keys().forEach(key -> {
            super.get(key).stream().forEach(v -> map.put(key, v));
        });
        return map.entrySet();
    }
}
