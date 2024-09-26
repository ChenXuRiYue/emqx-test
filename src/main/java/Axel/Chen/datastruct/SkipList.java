package Axel.Chen.datastruct;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
public class SkipList<K extends Comparable<K>, V> {
    // 跳表相关的一些配置：
    // 1. 新建层次随机概率
    private final double PROBABILITY = 0.5;
    // 2. 跳表最大层数
    private final int MAX_LEVEL = 32;
    // 3. 跳表层数
    private int level;
    // 4. 头结点
    private final List<Node<K, V>> head;
    // 5. 链表元素个数
    private int size;
    // 读写场景共存。使用读写锁来处理并发场景
    private final ReentrantReadWriteLock lock;

    public SkipList() {
        level = 0;
        head = new LinkedList<>();
        head.add(new Node<>(null, null));
        // 7，并发锁优化
        lock = new ReentrantReadWriteLock();
    }

    public Integer getRandomLevel() {
        int res = 0;
        while (res < MAX_LEVEL && Math.random() < PROBABILITY) res++;
        return res;
    }

    public void insert(K key, V value) {
        // 加锁
        lock.writeLock().lock();
        // 1.特判插入第一个节点，直接构造
        // 2. 已经初始化跳表
        List<Node<K, V>> skipList = getSplitNodeList(key);
        updateSkipStructureWhenInsert(skipList, key, value);
        // 更新元素个数
        size++;
        // 解锁
        lock.writeLock().unlock();
    }
    // TODO delete方法

    List<Node<K, V>> getSplitNodeList(K key){
        List<Node<K, V>> split = new LinkedList<>();
        // 从顶层头节点开始向下递归查找
        Node<K, V> current = head.get(level);
        while (current != null) {
            // 分为几种情况：
            Node<K, V> next = current.getNext();
            // 1. 下一个为 null， 需要向下走
            if (next == null) {
                split.add(current);
                current = current.getDown();
            }
            // 2. 比较next.key 和 大小
            else {
                // 2.1 比较，如果 left > next.key ，则向右走
                if (key.compareTo(next.getKey()) > 0) {
                    current = current.getNext();
                }
                // 2.2 比较，如果 left < next.key ，则向下走
                else {
                    // 转折点，无论如何都记录到 update中
                    split.add(current);
                    // 2.2.3 判断是否在最底层
                    if (current.getDown() == null) {
                        break;
                    } else {
                        // 记录转折点。
                        current = current.getDown();
                    }
                }
            }
        }
        // 对 update 数组取反， 方便处理
        Collections.reverse(split);
        return split;
    }

    public void updateSkipStructureWhenInsert(List<Node<K, V>> split, K key, V value){
        // 1. 随机化当前新插入节点的层数
        int randomLevel = getRandomLevel();

        if (randomLevel > level) {
            // 2.1 新建一层
            ++level;
            Node<K, V> newNode = new Node<>(key, value);
            newNode.setNext(null);
            newNode.setDown(head.get(level - 1));
            split.add(newNode);
            head.add(newNode);
        } else{
            // 2.2 取子数组
            split = split.subList(0, randomLevel + 1);
        }
        // 3. skipList 内容链表以及索引
        Node<K, V> downNode = null;
        for (Node<K, V> kvNode : split) {
            Node<K, V> newNode = new Node<>(key, value);
            newNode.setDown(downNode);
            downNode = newNode;
            newNode.setNext(kvNode.getNext());
            kvNode.setNext(newNode);
        }
    }


    /**
     * 查出列表中，在 left 和 right 之间的数据
     */
    public List<Node<K, V>> search(K left, K right) {
        lock.readLock().lock();
        Node<K, V> last = getLastNodeSmallerThanKey(left);
        // 底层链表搜索出对应的范围。
        List<Node<K, V>> res = getListByIntervalInBottom(left, right,last);
        lock.readLock().unlock();
        return res;
    }

    private Node<K, V> getLastNodeSmallerThanKey(K key){
        Node<K, V> current = head.get(level);
        // 定位到最底层的分界点。
        while (current != null) {
            // 分类讨论
            Node<K, V> next = current.getNext();
            // 1. next.key 为 null， 需要向下走
            if (next == null) {
                current = current.getDown();
            }
            // 2. 比较next.key和 大小
            else {
                // 2.1 如果 left > next.key ，则向右走
                if (key.compareTo(next.getKey()) > 0) {
                    current = current.getNext();
                }
                // 2.2 如果 left 大于，则向下走
                else {
                    // 2.2.3 判断是否在最底层
                    if (current.getDown() == null) {
                        break;
                    } else {
                        current = current.getDown();
                    }
                }
            }
        }
        return current;
    }

    private List<Node<K, V>> getListByIntervalInBottom(K left, K right, Node<K, V> Last){
        List<Node<K, V>> res = new LinkedList<>();
        Node<K, V> current = Last;
        // 跳出循环时候 current 指向最后一个比它小的元素。 对其进行重新定位 -> 指向下一个即第一个大于等于 left 的元素
        if (current != null) current = current.getNext();
        while (current != null && current.getKey().compareTo(right) <= 0) {
            res.add(current);
            current = current.getNext();
        }
        return res;
    }

    public Integer getSize(){
        return size;
    }
}