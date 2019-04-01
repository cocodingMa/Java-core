import java.util.HashMap;

public class LRUCache {

    static int CAPACITY;
    HashMap<String, Node> hashMap = new HashMap<>();
    Node head = null;
    Node tail = null;

    class Node {
        String key;
        String value;
        Node pre;
        Node next;

        Node(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        while (this.head != null){
            stringBuilder.append(head.value + " -> ");
            head = head.next;
        }
        return stringBuilder.toString();
    }

    public LRUCache(int capacity) {
        this.CAPACITY = capacity;
    }

    /*
     *  移除指定节点
     */
    public void remove(Node node) {
        if (node.pre == null) {   //node为头节点
            head = node.next;
        } else if (node.next == null) {  //node为尾节点
            tail = node.pre;
        } else {    // node不为头尾节点
            node.next.pre = node.pre;
            node.pre.next = node.next;
        }
    }

    /*
     *  指定节点插入到表头
     */
    public void setHead(Node node) {
        node.next = head;
        node.pre = null;
        if (head != null) {
            head.pre = node;
        }
        head = node;
        if (tail == null) {
            tail = node;
        }
    }

    /*
     *  获取缓存（如果存在，将缓存节点插到表头）
     */
    public String get(String key) {
        if (!hashMap.containsKey(key)) { //key不存在
            return null;
        }
        Node node = hashMap.get(key);
        remove(node);
        setHead(node);
        return node.value;
    }

    /*
     *  更新缓存（如果存在，将缓存节点插到表头）
     */
    public void add(String key, String value) {
        if (hashMap.containsKey(key)) { //key存在
            Node node = hashMap.get(key);
            node.value = value;
            remove(node);
            setHead(node);
        } else {    //key不存在
            Node newNode = new Node(key, value);
            if (hashMap.size() >= CAPACITY) {
                hashMap.remove(tail.key);
                remove(tail);
                setHead(newNode);
            } else {
                setHead(newNode);
            }

            hashMap.put(key, newNode);
        }
    }

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(4);
        lruCache.add("11", "111");
        lruCache.add("22", "222");
        lruCache.add("33", "333");
        System.out.println(lruCache.toString());
        lruCache.add("44", "444");
        lruCache.add("55", "555");
        System.out.println(lruCache.toString());
        lruCache.get("33");
        System.out.println(lruCache.toString());
    }
}

