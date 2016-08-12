package comp1110.ass2;

/**
 * Implements the Union-Find algorithm.
 * Mostly taken from http://algs4.cs.princeton.edu/15uf/QuickUnionUF.java.html by Robert Sedgewick and Kevin Wayne.
 * with reference to https://www.cs.princeton.edu/~rs/AlgsDS07/01UnionFind.pdf
 * For additional documentation, see <a href="http://algs4.cs.princeton.edu/15uf">Section 1.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * Copied by Yuxi Liu (u5950011) on 8/11/16.
 */
public class UnionFind {
    private int[] parent;  // parent[i] = parent of i
    private int count;     // number of components

    /**
     * Initializes a union-find data structure with n sites 0 through n-1. Each site is initially in its own
     * component.
     * @param  n the number of sites
     */
    public UnionFind(int n) {
        parent = new int[n];
        count = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    public int getCount() {
        return count;
    }

    /**
     * Returns the component identifier (the root of the tree) for the component containing site p.
     * @param p the integer representing one object
     * @return the component identifier for the component containing site p.
     */
    public int find(int p) {
        while (p != parent[p]) {
            parent[p] = parent[parent[p]]; // path compression
            p = parent[p];
        }
        return p;
    }

    /**
     * Returns true if the the two sites are in the same component.
     * @param p the integer representing one site.
     * @param q the integer representing the other site.
     * @return true iff the two sites p and q are in the same component.
     */
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Merges the component containing site p with the component containing site q.
     * @param p the integer representing one site
     * @param q the integer representing the other site
     */
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;
        parent[rootP] = rootQ;
        count--;
    }
}
