## Part A: Test-first development

1. Discuss the difficulties you encountered in testing rotations for Avl and Treap map implementations; what tests cases you used and why you chose those particular examples. 
You are enouraged to draw little ASCII trees to illustrate  the test cases.

Answer: 
Difficulty in testing is to come up with cases including left, right, left-right, and right-left rotations, and ensuring child nodes of a rotated node are also kept balanced
(this is more of a implementing issue).

Test cases for AVL Tree:
Insert right rotation - insert c, b, a

          3 c           2 b
          /           /    \
       2 b     =>   1 a    3 c
       /
    1 a   

Insert left rotation - insert a, b, c

       1 a                2 b
         \              /    \
         2 b     =>   1 a    3 c
           \
          3 c    

Insert Left-Right rotation - insert d, e, c, a, b

              4 d                          4 d                          4 d 
            /     \                      /     \                      /     \
          3 c     5 e      left        3 c     5 e        right     2 b     5 e
         /                  =>       /                     =>      /  \
       1 a                        2 b                           1 a  3 c
         \                        /
         2 b                    1 a

Insert Right-Left rotation - insert b, a, e, d, c

           2 b                   2 b                   2 b
         /    \                 /    \                /    \
       1 a    5 e       =>    1 a    5 e      =>   1 a    4 d         
              /                      /                   /   \
            3 c                    4 d                 3 c   5 e
              \                   /
              4 d               3 c

The first difficulty for testing TreapMap is to ensure the priorities generated are controllable. I set the seed of the Random object by writing a new contructor that takes a 
seed value as parameter, and called it in the setUp() fucntion in the test class.

The second difficulty for TreapMap is to fit in key and values such that the priorities for each map would appear as desired to test rotations in insertion and removal cases. 
I did this by first writing out the desired tree structure before insertion/removal, then assigned each node keys according to BST properties, and checked the actual string 
against the expected one after drawing insertion/removal based on min-heap priority rules on paper.

Test cases for Treap map:
Insertion left rotation - insert b, a, d

       2:b:424                          4:d:58  
       /     \               =>       2:b:424
    1:a:600  4:d:58                 1:a:600

Insertion Multiple left rotation - insert a, b, c

    1:a:424                   1:a:424                       3:c:58
          \                         \                        /
         2:b:600          =>        3:c:58        =>    1:a:424 
              \                    /                        \
              3:c:58            2:b:600                    2:b:600

Insertion right rotation - insert b, a, d, c, f, e

         2:b:424                              4:d:58                          4:d:58                                
        /     \               =>         /             \                 /             \   
    1:a:600  4:d:58                 2:b:424        6:f:734    =>     2:b:424        5:e:106
                                  /    \            /             /      \             \
                               1:a:600  3:c:598  5:e:106       1:a:600  3:c:598       6:f:734

Insertion multiple right rotations - insert c, d, e, i, g, a, h, b, f

    3:c:424                          5:e:58                      5:e:58                              5:e:58
          \               =>       /              =>       /               \                   /                 \
          4:d:600              3:c:424                  3:c:424          9:i:598       =>    1:a:106            9:i:598 
             \                      \                 /      \            /                     \                /
            5:e:58                 4:d:600          1:a:106   4:d:600   7:g:734                 3:c:424       7:g:734  
                                                                                                   \
                                                                                                   4:d:600
        
               5:e:58                                                    5:e:58                                                5:e:58
         /                 \                                      /                 \                                   /                 \
       1:a:106            9:i:598                            1:a:106               9:i:598         3 rotations    1:a:106               6:f:142 
          \                /                        =>          \                    /                  =>           \                       \ 
         3:c:424       7:g:734                                  3:c:424          8:h:687                            3:c:424                 9:i:598
           \              \                                    /    \             /                                /    \                     /
           4:d:600       8:h:687                          2:b:523   4:d:600     7:g:734                      2:b:523   4:d:600          8:h:687
                                                                                  \                                                        /
                                                                                  6:f:142                                            7:g:734

Remove right rotation - remove 6:f:424

            3:c:58                                          3:c:58                                     3:c:58      
          /        \                                      /        \                                 /        \                                                                                                                           
      2:b:106     6:f:424                            2:b:106     5:e:687                        2:b:106     4:d:598
        /         /     \               =>            /         /     \                 =>       /               \
    1:a:600   4:d:598  7:g:734                    1:a:600   4:d:598  7:g:734                 1:a:600           5:e:687
                  \                                                                                                \
                 5:e:687                                                                                          7:g:734 

Remove left rotation - remove 2:b:424

          2:b:424                   1:a:600                    3:c:58
          /     \        =>              \            =>         /
       1:a:600  3:c:58                  3:c:58               1:a:600

Remove right followed by left rotation - remove 3:c:424

                 6:f:58                                      6:f:58                                   6:f:58                              6:f:58    
              /          \                                 /          \                            /          \                       /          \ 
          3:c:424       7:g:106                       2:b:687       7:g:106                   5:e:598       7:g:106               5:e:598       7:g:106 
       /          \                    =>           /        \                      =>      /                             =>      /  
    1:a:600     5:e:598                         1:a:600     5:e:598                      2:b:687                              1:a:600
        \         /                                         /                          /     \                                   \
     2:b:687  4:d:734                                   4:d:734                   1:a:600  4:d:734                              2:b:687
                                                                                                                                   \
                                                                                                                                  4:d:734
        
## Part D: Benching Word Counts

1. Note the results of running `WordFrequencyCountExperiment` over several different test data using various implementations of `Map`.  More importantly, *describe* your observations and try to *explain* 
(justify) them using your understanding of the code you're benchmarking. (in other words, *why* are the numbers as they are?)

Experiment data: (each run 5 time and taken median of)

hotel California.txt

Simple map: 41 ms using 448 kb memory; AVL: 44ms using 448 kb memory; BST: 42ms using 448 kb memory; Treap: 46ms using 448 kb memory

Fed01.txt

Simple map: 112 ms using 3072 kb memory; AVL: 96 ms using 3071 kb memory; BST: 92 ms using 2784 kb memory; Treap: 96 ms using 3071 kb memory

 moby_dick.txt
 
 Simple map: 7806 ms using 41899 kb memory; AVL: 1063 ms using 41408 kb memory; BST: 1124 ms using 41922 kb memory; Treap: 1309 ms using 42942 kb memory
 
 Pride_and_prejudice.txt
 
 Simple map: 3293 ms using 42663 kb memory; AVL: 886 ms using 40572 kb memory; BST: 851 ms using 40161 kb memory; Treap: 861 ms using 41189 kb memory. 
 
*Observations and Explanations:*

1. For smaller data sets, the advantage of trees in time complexity is not obvious. Simple maps even take the shortest time for hotel_california.txt with a few hundred words.
The reason is O(N) and O(logN) are not very different at small N. And though they specify the upper bound, in reality simple maps may have the fastest has()
and insert() operations as rotations in Treap, BST, and AVL Tree takes extra steps.

2. The difference in time complexity between BinarySearchTree and AVL Tree is not obvious. This could be because that the text can be seen as more or less random word arrangements,
and extreme situations of like a single sided tree (linked list) would not occur, and both BST and AVL are O(logN) operations. Sometimes BST is even faster as it does not require rotations.

3. Time taken for Treap, Binary Search Tree, and AVL Tree is much smaller than that of Simple Map as words grow larger in number. This is because Simple Map implements an ArrayList
which is a linear structure, and takes O(N) time, while Treap and Trees implements binary trees and take O(logN) (AVL) and amortized O(logN) (BST, Treap) times. 

4. AVL Tree and Treap take more memory than BST. This is probably due to the additional fields of height in AVL Tree nodes, and priorities in Treap nodes.

5. Simple map and Treap map take more memory than BST and AVL Trees. Simple map's memory may be due to the internal implementation of java's built-in array list, and that of Treap may be
a result of the extra memory space for random integers as priorities.