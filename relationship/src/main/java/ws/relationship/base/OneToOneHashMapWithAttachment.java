package ws.relationship.base;

import java.util.HashMap;

/**
 * 一一对应的Map
 */
public class OneToOneHashMapWithAttachment<K, V, A> extends OneToOneAbstractMap<K, V, A> {
    private static final long serialVersionUID = -4619838488459070883L;

    public OneToOneHashMapWithAttachment() {
        KToV = new HashMap<>();
        VToK = new HashMap<>();
        KToAttachment = new HashMap<>();
    }
}
