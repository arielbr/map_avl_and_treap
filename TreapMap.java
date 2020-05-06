package hw6;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Map implemented as a Treap.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public class TreapMap<K extends Comparable<K>, V>
    implements OrderedMap<K, V> {

  /*** Do not change variable name of 'rand'. ***/
  private static Random rand;
  /*** Do not change variable name of 'root'. ***/
  private Node<K, V> root;
  private int size;

  /**
   * Make a TreapMap.
   */
  public TreapMap() {
    rand = new Random();
  }

  /**
   * Make a TreapMap with set seed.
   * @param seed the seed for the Random object rand.
   */
  public TreapMap(int seed) {
    this();
    rand.setSeed(seed);
  }

  /**
   * insert a node with given key and value.
   * @param k The key.
   * @param v The value to be associated with k.
   * @throws IllegalArgumentException thrown at null key.
   */
  @Override
  public void insert(K k, V v) throws IllegalArgumentException {
    if (k == null) {
      throw new IllegalArgumentException("cannot insert null key.");
    }
    root = insertHelper(root, k, v);
    size++;
  }

  /**
   * insert a node with given key and value to a subtree.
   * @param node subtree root.
   * @param k key.
   * @param v value.
   * @return modified subtree.
   */
  private Node<K, V> insertHelper(Node<K, V> node, K k, V v) {
    if (node == null) {
      return new Node<>(k, v);
    }
    if (k.compareTo(node.key) < 0) {
      node.left = insertHelper(node.left, k, v);
      if (node.left.priority < node.priority) {
        node = rightRotation(node);
      }
    } else if (k.compareTo(node.key) > 0) {
      node.right = insertHelper(node.right, k, v);
      if (node.right.priority < node.priority) {
        node = leftRotation(node);
      }
    } else {
      throw new IllegalArgumentException("duplicate key" + k);
    }
    return node;
  }

  private Node<K,V> rightRotation(Node<K,V> node) {
    Node<K, V> leftChild = node.left;
    node.left = leftChild.right;
    leftChild.right = node;
    //update height
    return leftChild;
  }

  private Node<K, V> leftRotation(Node<K,V> node) {
    Node<K,V> rightChild = node.right;
    node.right = rightChild.left;
    rightChild.left = node;
    //update height
    return rightChild;
  }

  private Node<K, V> findForSure(K k) {
    Node<K, V> n = find(k);
    if (n == null) {
      throw new IllegalArgumentException("cannot find key " + k);
    }
    return n;
  }

  private Node<K,V> find(K k) {
    if (k == null) {
      throw new IllegalArgumentException("cannot handle null key");
    }
    Node<K, V> n = root;
    while (n != null) {
      int cmp = k.compareTo(n.key);
      if (cmp < 0) {
        n = n.left;
      } else if (cmp > 0) {
        n = n.right;
      } else {
        return n;
      }
    }
    return null;
  }

  @Override
  public V remove(K k) throws IllegalArgumentException {
    Node<K, V> toRemove = findForSure(k);
    root = remove(root, toRemove);
    size--;
    return toRemove.value;
  }

  private Node<K, V> remove(Node<K, V> node, Node<K, V> toRemove) {
    int cmp = toRemove.key.compareTo(node.key);
    if (cmp == 0) {
      return remove(node);
    } else if (cmp < 0) {
      node.left = remove(node.left, toRemove);
    } else {
      node.right = remove(node.right, toRemove);
    }
    return node;
  }

  private Node<K, V> remove(Node<K, V> node) {
    if (node.left == null) {
      return node.right;
    } else if (node.right == null) {
      return node.left;
    }
    Node<K, V> toReplaceWith = max(node.left);
    node.key = toReplaceWith.key;
    node.value = toReplaceWith.value;
    node.priority = toReplaceWith.priority;
    node.left = remove(node.left, toReplaceWith);
    return reBalance(node);
  }

  private Node<K,V> reBalance(Node<K,V> node) {
    if (node.left == null || node.right == null) {
      return reBalanceHelper(node);
    } else {
      if (node.left.priority < node.right.priority) {
        node = rightRotation(node);
        node.right = reBalance(node.right);
      } else {
        node = leftRotation(node);
        node.left = reBalance(node.left);
      }
    }
    return node;
  }

  private Node<K, V> reBalanceHelper(Node<K, V> node) {
    if (node.left == null && node.right == null) {
      return node;
    } else if (node.left == null) {
      if (node.priority <= node.right.priority) {
        return node;
      } else {
        node = leftRotation(node);
        node.left = reBalance(node.left);
      }
    } else if (node.right == null) {
      if (node.priority < node.left.priority) {
        return node;
      } else {
        node = rightRotation(node);
        node.right = reBalance(node.right);
      }
    }
    return node;
  }

  private Node<K, V> max(Node<K, V> node) {
    Node<K, V> cur = node;
    while (cur.right != null) {
      cur = cur.right;
    }
    return cur;
  }

  @Override
  public void put(K k, V v) throws IllegalArgumentException {
    Node<K, V> n = findForSure(k);
    n.value = v;
  }

  @Override
  public V get(K k) throws IllegalArgumentException {
    Node<K, V> n = findForSure(k);
    return n.value;
  }

  @Override
  public boolean has(K k) {
    if (k == null) {
      return false;
    }
    Node<K, V> n = find(k);
    return n != null;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Iterator<K> iterator() {
    List<K> keys = new ArrayList<>();
    iteratorHelper(root, keys);
    return keys.iterator();
  }

  private void iteratorHelper(Node<K,V> n, List<K> keys) {
    if (n == null) {
      return;
    }
    iteratorHelper(n.left, keys);
    keys.add(n.key);
    iteratorHelper(n.right, keys);
  }

  /*** Do not change this function's name or modify its code. ***/
  // Breadth first traversal that prints binary tree out level by level.
  // Each existing node is printed as follows:
  // 'node.key:node.value:node.priority'.
  // Non-existing nodes are printed as 'null'.
  // There is a space between all nodes at the same level.
  // The levels of the binary tree are separated by new lines.
  // Returns empty string if the root is null.
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    Queue<Node<K, V>> q = new LinkedList<>();

    q.add(root);
    boolean onlyNullChildrenAdded = root == null;
    while (!q.isEmpty() && !onlyNullChildrenAdded) {
      onlyNullChildrenAdded = true;

      int levelSize = q.size();
      while (levelSize-- > 0) {
        boolean nonNullChildAdded = handleNextNodeToString(q, s);
        if (nonNullChildAdded) {
          onlyNullChildrenAdded = false;
        }
        s.append(" ");
      }

      s.deleteCharAt(s.length() - 1);
      s.append("\n");
    }

    return s.toString();
  }

  /*** Do not change this function's name or modify its code. ***/
  // Helper function for toString() to build String for a single node
  // and add its children to the queue.
  // Returns true if a non-null child was added to the queue, false otherwise
  private boolean handleNextNodeToString(Queue<Node<K, V>> q, StringBuilder s) {
    Node<K, V> n = q.remove();
    if (n != null) {
      s.append(n.key);
      s.append(":");
      s.append(n.value);
      s.append(":");
      s.append(n.priority);

      q.add(n.left);
      q.add(n.right);

      return n.left != null || n.right != null;
    } else {
      s.append("null");

      q.add(null);
      q.add(null);

      return false;
    }
  }

  /*** Do not change the name of the Node class.
   * Feel free to add whatever you want to the Node class (e.g. new fields).
   * Just avoid changing what we've provided already.
   * ***/
  // Inner node class, each holds a key (which is what we sort the
  // BST by) as well as a value. We don't need a parent pointer as
  // long as we use recursive insert/remove helpers. Since this
  // is a node class for a Treap we also include a priority field.
  private static class Node<K, V> {
    /***  Do not change variable names in this section. ***/
    Node<K, V> left;
    Node<K, V> right;
    K key;
    V value;
    int priority;

    /*** End of section. ***/

    // Constructor to make node creation easier to read.
    Node(K k, V v) {
      // left and right default to null
      key = k;
      value = v;
      priority = generateRandomInteger();
      //just to make reading and debugging easier a little bit
    }

    // Use this function to generate random values
    // to use as node priorities as you insert new
    // nodes into your TreapMap.
    private int generateRandomInteger() {
      return rand.nextInt();
    }

    // Just for debugging purposes.
    public String toString() {
      return "Node<key: " + key
          + "; value: " + value
          + ">";
    }
  }
}
