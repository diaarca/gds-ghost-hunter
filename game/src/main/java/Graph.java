import java.util.ArrayList;

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
