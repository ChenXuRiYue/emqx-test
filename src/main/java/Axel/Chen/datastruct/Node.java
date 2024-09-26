package Axel.Chen.datastruct;

public class Node<K extends Comparable<K>, V> {
    private K key;
    private V value;

    private Node<K, V> next;
    private Node<K, V> down;

    // 计算跨度：虽然当前语境不需要，但是，如果复用到排行榜中，这里可以产生重要的作用。
    Integer Length;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.Length = 1;
    }

    // 若干 get set 接口
    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public Node<K, V> getNext() {
        return next;
    }

    public Node<K, V> getDown() {
        return down;
    }

    public void setNext(Node<K, V> next) {
        this.next = next;
    }

    public void setDown(Node<K, V> down) {
        this.down = down;
    }

    public void setLength(Integer length) {
        Length = length;
    }
}
