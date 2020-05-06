package hw6;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Ordered maps implemented as (basic) binary search trees.
 *
 * <P>This BST is <b>not</b> balanced so all operations (except for size) are
 * O(n) in the worst case. Iterators operate on a copy of the keys, so
 * changing the tree will not change iterations in progress. (Iterating
 * over the tree directly would require a "threaded" representation, a
 * much more complicated beast.)</P>
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public class BinarySearchTreeMap<K extends Comparable<K>, V>
    implements OrderedMap<K, V> {

  private Node<K, V> root;
  private int size;


  @Override
  public int size() {
    return size;
  }

  // Return node for given key. This one is iterative, but a recursive
  // one would also work. It's just that there's no real advantage to
  // using recursion for this operation.
  private Node<K, V> find(K k) {
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
  public boolean has(K k) {
    if (k == null) {
      return false;
    }
    return find(k) != null;
  }

  // Return node for given key, throw an exception if the key is not
  // in the tree.
  private Node<K, V> findForSure(K k) {
    Node<K, V> n = find(k);
    if (n == null) {
      throw new IllegalArgumentException("cannot find key " + k);
    }
    return n;
  }

  @Override
  public void put(K k, V v) {
    Node<K, V> n = findForSure(k);
    n.value = v;
  }

  @Override
  public V get(K k) {
    Node<K, V> n = findForSure(k);
    return n.value;
  }

  // Insert given key and value into subtree rooted at given node;
  // return changed subtree with a new node added. Unlike in find()
  // above, doing this recursively *has* benefits: First we get
  // away with simpler code that doesn't need parent pointers,
  // second the recursive structure makes it easier to add fancy
  // tree balancing code (later).
  private Node<K, V> insert(Node<K, V> n, K k, V v) {
    if (n == null) {
      return new Node<>(k, v);
    }

    int cmp = k.compareTo(n.key);
    if (cmp < 0) {
      n.left = insert(n.left, k, v);
    } else if (cmp > 0) {
      n.right = insert(n.right, k, v);
    } else {
      throw new IllegalArgumentException("duplicate key " + k);
    }

    return n;
  }

  @Override
  public void insert(K k, V v) {
    if (k == null) {
      throw new IllegalArgumentException("cannot handle null key");
    }
    root = insert(root, k, v);
    size++;
  }

  @Override
  public V remove(K k) {
    Node<K, V> node = findForSure(k);
    root = remove(root, node);
    size--;
    return node.value;
  }

  // Remove node with given key from subtree rooted at given node;
  // Return changed subtree with given key missing.
  // Doing this recursively makes it easier to
  // add fancy tree balancing code later.
  private Node<K, V> remove(Node<K, V> subtreeRoot, Node<K, V> toRemove) {
    int cmp = subtreeRoot.key.compareTo(toRemove.key);
    if (cmp == 0) {
      return remove(subtreeRoot);
    } else if (cmp > 0) {
      subtreeRoot.left = remove(subtreeRoot.left, toRemove);
    } else {
      subtreeRoot.right = remove(subtreeRoot.right, toRemove);
    }

    return subtreeRoot;
  }

  // Remove given node and return the remaining tree (structural change).
  private Node<K, V> remove(Node<K, V> node) {
    // Easy if the node has 0 or 1 child.
    if (node.right == null) {
      return node.left;
    } else if (node.left == null) {
      return node.right;
    }

    // If it has two children, find the predecessor (max in left subtree),
    Node<K, V> toReplaceWith = max(node);
    // then copy its data to the given node (value change),
    node.key = toReplaceWith.key;
    node.value = toReplaceWith.value;
    // then remove the predecessor node (structural change).
    node.left = remove(node.left, toReplaceWith);

    return node;
  }

  // Return a node with maximum key in subtree rooted at given node.
  private Node<K, V> max(Node<K, V> node) {
    Node<K, V> curr = node.left;
    while (curr.right != null) {
      curr = curr.right;
    }
    return curr;
  }

  // Recursively add keys from subtree rooted at given node into the
  // given list in order.
  private void iteratorHelper(Node<K, V> n, List<K> keys) {
    if (n == null) {
      return;
    }
    iteratorHelper(n.left, keys);
    keys.add(n.key);
    iteratorHelper(n.right, keys);
  }

  @Override
  public Iterator<K> iterator() {
    List<K> keys = new ArrayList<K>();
    iteratorHelper(root, keys);
    return keys.iterator();
  }

  // Recursively append string representations of keys and values from
  // subtree rooted at given node in order.
  private void toStringHelper(Node<K, V> n, StringBuilder s) {
    if (n == null) {
      return;
    }
    toStringHelper(n.left, s);
    s.append(n.key);
    s.append(": ");
    s.append(n.value);
    s.append(", ");
    toStringHelper(n.right, s);
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("{");

    toStringHelper(root, stringBuilder);

    int length = stringBuilder.length();
    if (length > 1) {
      // If anything was appended at all, get rid of the last ", "
      // toStringHelper put in; easier to correct this after the
      // fact than to avoid making the mistake in the first place.
      stringBuilder.setLength(length - 2);
    }
    stringBuilder.append("}");

    return stringBuilder.toString();
  }

  // Node class holds a key (which is what we sort the BST by)
  // as well as a value.
  // We don't need a parent pointer as long as we use recursive
  // insert/remove helpers.
  private static class Node<K, V> {
    Node<K, V> left;
    Node<K, V> right;
    K key;
    V value;

    // Constructor to make node creation easier to read.
    Node(K k, V v) {
      // left and right default to null
      key = k;
      value = v;
    }

    // Just for debugging purposes.
    public String toString() {
      return "Node<key: " + key
          + "; value: " + value
          + ">";
    }
  }
}
