import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Arrays;

public class Graph {
    private int n_;
    private Boolean[][] edges_;
    private ArrayList<Integer>[] neighbors_;

    public Graph(int nbNode, Boolean[][] adjacencyMatrix) {
        n_ = nbNode;
        edges_ = adjacencyMatrix;

        neighbors_ = new ArrayList[n_];

        for (int i = 0; i < n_; i++) {
            neighbors_[i] = new ArrayList<Integer>();
        }
        for (int i = 0; i < n_ - 1; i++) {
            for (int j = i + 1; j < n_; j++) {
                if (edges_[i][j] == true) {
                    neighbors_[i].add(j);
                    neighbors_[j].add(i);
                }
            }
        }
    }

    public Graph(int nbNode,
                 Boolean[][] adjacencyMatrix,
                 ArrayList<Integer>[] neighborList) {
        n_ = nbNode;
        edges_ = adjacencyMatrix;
        neighbors_ = neighborList;
    }

    public Graph(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            if (line == null) {
                throw new IllegalArgumentException("Empty file: " + filename);
            }
            line = line.trim();
            if (line.length() == 0) {
                throw new IllegalArgumentException(
                    "First line must contain number of vertices");
            }
            int nbNode = Integer.parseInt(line);
            n_ = nbNode;
            edges_ = new Boolean[n_][n_];

            for (int i = 0; i < n_; i++) {
                String row = br.readLine();
                if (row == null) {
                    throw new IllegalArgumentException(
                        "Not enough adjacency rows in file: " + filename);
                }
                row = row.trim();
                if (row.length() == 0) {
                    i--; // skip empty lines
                    continue;
                }

                StringTokenizer st = new StringTokenizer(row);
                for (int j = 0; j < n_; j++) {
                    if (!st.hasMoreTokens()) {
                        throw new IllegalArgumentException(
                            "Adjacency row has too few tokens at line " +
                            (i + 2));
                    }
                    String tok = st.nextToken();
                    if (tok.equals("1") || tok.equalsIgnoreCase("true")) {
                        edges_[i][j] = Boolean.TRUE;
                    } else {
                        edges_[i][j] = Boolean.FALSE;
                    }
                }
            }

            // build neighbors
            neighbors_ = new ArrayList[n_];
            for (int i = 0; i < n_; i++) {
                neighbors_[i] = new ArrayList<Integer>();
            }
            for (int i = 0; i < n_ - 1; i++) {
                for (int j = i + 1; j < n_; j++) {
                    if (edges_[i][j] == true) {
                        neighbors_[i].add(j);
                        neighbors_[j].add(i);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading graph file: " + filename,
                                       e);
        }
    }

    public static Graph genNCycle(int cycleLenght) {
        int n = cycleLenght;

        Boolean[][] edges = new Boolean[n][n];
        ArrayList<Integer>[] neighbors = new ArrayList[n];

        int prev, next;

        for (int i = 0; i < n; i++) {
            prev = i == 0 ? (n - 1) : (i - 1) % n;
            next = (i + 1) % n;

            Arrays.fill(edges[i], Boolean.FALSE);
            edges[i][prev] = true;
            edges[i][next] = true;

            neighbors[i] = new ArrayList<Integer>();
            neighbors[i].add(prev);
            neighbors[i].add(next);
        }

        return new Graph(n, edges, neighbors);
    }

    int getN() { return n_; }

    Boolean[][] getEdges() { return edges_; }

    ArrayList<Integer>[] getNeighbors() { return neighbors_; }

    ArrayList<Integer> getNeighborList(int vertex) {
        return neighbors_[vertex];
    }

    public String toString() {
        String str = "|V| = " + n_ + " and E =\n";

        for (int i = 0; i < n_; i++) {
            str += "[";
            for (int j = 0; j < n_; j++) {
                if (edges_[i][j]) {
                    str += "1 ";
                } else {
                    str += "0 ";
                }
            }
            str = str.substring(0, str.length() - 1) + "]\n";
        }

        return str;
    }
}
