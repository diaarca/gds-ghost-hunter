import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;
import javax.sound.midi.SysexMessage;

public class Graph {
    private int n_;
    private Boolean[][] edges_;
    private ArrayList<Integer>[] neighbors_;

    public Graph(int nbNode, Boolean[][] adjacencyMatrix) {
        n_ = nbNode;
        edges_ = new Boolean[n_][n_];
        neighbors_ = new ArrayList[n_];

        for (int i = 0; i < n_; i++) {
            neighbors_[i] = new ArrayList<Integer>();
            for (int j = 0; j < n_; j++) {
                this.edges_[i][j] = adjacencyMatrix[i][j];
            }
        }

        for (int i = 0; i < n_; i++) {
            for (int j = i + 1; j < n_; j++) {
                if (edges_[i][j]) {
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

    public Graph(String filename) throws IOException {
        initializeFromFile(filename);
    }

    public Graph(GraphConfig config) {
        GraphType graphType = config.getGraphType_();
        switch (graphType) {
        case N_CYCLE:
            initializeNCycle(config.getN_());
            break;
        case N_COMP:
            initializeNComp(config.getN_());
            break;
        case N_K_REGULAR:
            System.err.println("ERROR: The N_K_REGULAR graphs must be "
                               + "generated via generateGraphFamily method");
            System.exit(1);
        case N_CONN:
            System.err.println("ERROR: The N_CONN graphs must be "
                               + "generated via generateGraphFamily method");
            System.exit(1);
        case FROM_FILE:
            initializeFromFile(config.getFilename_());
            break;
        default:
            System.err.println("ERROR: Unknown graphType = " + graphType);
            System.exit(1);
        }

        if (!isConnected(edges_, n_)) {
            System.err.println("ERROR: The constructed graph isn't connected");
            System.exit(1);
        }
    }

    private void initializeFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            if (line == null) {
                System.err.println("ERROR: Empty file: " + filename);
                System.exit(1);
            }
            line = line.trim();
            if (line.length() == 0) {
                System.err.println("ERROR: The first line in" + filename +
                                   "must contain the number of vertices");
                System.exit(1);
            }
            int nbNode = Integer.parseInt(line);
            n_ = nbNode;
            edges_ = new Boolean[n_][n_];

            for (int i = 0; i < n_; i++) {
                String row = br.readLine();
                if (row == null) {
                    System.err.println(
                        "ERROR: Not enough adjacency rows in the file: " +
                        filename);
                    System.exit(1);
                }
                row = row.trim();
                if (row.length() == 0) {
                    i--; // skip empty lines
                    continue;
                }

                StringTokenizer st = new StringTokenizer(row);
                for (int j = 0; j < n_; j++) {
                    if (!st.hasMoreTokens()) {
                        System.err.println(
                            "Adjacency row has too few columns at line " +
                            (i + 2) + " in the file: " + filename);
                        System.exit(1);
                    }
                    String tok = st.nextToken();
                    if (tok.equals("1") || tok.equalsIgnoreCase("true")) {
                        edges_[i][j] = Boolean.TRUE;
                    } else {
                        edges_[i][j] = Boolean.FALSE;
                    }
                }
            }

            neighbors_ = new ArrayList[n_];
            for (int i = 0; i < n_; i++) {
                neighbors_[i] = new ArrayList<Integer>();
            }
            for (int i = 0; i < n_ - 1; i++) {
                for (int j = i + 1; j < n_; j++) {
                    if (edges_[i][j]) {
                        neighbors_[i].add(j);
                        neighbors_[j].add(i);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("ERROR: Cannot read the file: " + filename);
            System.exit(1);
        }
    }

    private void initializeNCycle(int cycleLength) {
        int n = cycleLength;
        n_ = n;
        edges_ = new Boolean[n][n];
        neighbors_ = new ArrayList[n];

        for (int i = 0; i < n; i++) {
            int prev = i == 0 ? (n - 1) : (i - 1) % n;
            int next = (i + 1) % n;
            Arrays.fill(edges_[i], Boolean.FALSE);
            edges_[i][prev] = true;
            edges_[i][next] = true;
            neighbors_[i] = new ArrayList<Integer>();
            neighbors_[i].add(prev);
            neighbors_[i].add(next);
        }
    }

    private void initializeNComp(int nbNodes) {
        n_ = nbNodes;
        edges_ = new Boolean[n_][n_];
        neighbors_ = new ArrayList[n_];

        for (int i = 0; i < n_; i++) {
            neighbors_[i] = new ArrayList<Integer>();
            for (int j = 0; j < n_; j++) {
                if (i == j) {
                    edges_[i][j] = false;
                } else {
                    edges_[i][j] = true;
                    neighbors_[i].add(j);
                }
            }
        }
    }

    public static List<Graph> generateGraphFamily(GraphConfig config) {
        switch (config.getGraphType_()) {
        case N_K_REGULAR:
            return generateNKRegularGraphs(config.getN_(), config.getK_());
        case N_CONN:
            return generateNConnGraphs(config.getN_());
        case N_CYCLE:
        case N_COMP:
        case FROM_FILE:
        default:
            return Arrays.asList(new Graph(config));
        }
    }

    private static List<Graph> generateNConnGraphs(int N) {
        List<Graph> solutions = new ArrayList<>();
        combGenerate(N,solutions);
        return solutions;
    }

private static void combGenerate(int N, List<Graph> solutions) {
        // This loops through all possible combinations of edges:
        // For N=4: 6 (N * (N - 1) / 2) possible edges, so i goes from 0 to 63 (2^6 - 1)
        // Each value of i represents a different graph; SO AFTER N=8 OVERFLOW use long
        // Each bit in i represents whether a specific edge exists
        int possibleGraph = (int)Math.pow(2, N * (N - 1) / 2);
        for (int i = 0; i < possibleGraph; i++) {
            Boolean[][] newAdj = new Boolean[N][N];
            for (int x = 0; x < N; x++)
                Arrays.fill(newAdj[x], false);

            int idx = 0;
            for (int x = 0; x < N; x++) {
                for (int y = x + 1; y < N; y++) {
                    /*
                    idx 0: (0,1);idx 1: (0,2);idx 2: (0,3);idx 3: 
                    (1,2);idx 4: (1,3);idx 5: (2,3)
                    ALL THE POSSIBLE EDGES FOR N=4
                    */
                    if ((i & (1 << idx)) != 0) {
                        newAdj[x][y] = newAdj[y][x] = true;
                    }
                    idx++;
                }
            }
            if (isConnected(newAdj, N)) {
                solutions.add(new Graph(N, newAdj));
            }
        }
    }

    private static List<Graph> generateNKRegularGraphs(int N, int K) {
        if ((N * K) % 2 != 0) {
            System.err.println(
                "N * K must be even to generate a k-regular graph");
            System.exit(1);
        }
        if (K >= N) {
            System.err.println("K must be less than N");
            System.exit(1);
        }

        List<Graph> solutions = new ArrayList<>();
        Boolean[][] adjMatrix = new Boolean[N][N];

        for (int i = 0; i < N; i++)
            Arrays.fill(adjMatrix[i], false);

        int[] degrees = new int[N];
        backtrackGenerate(0, 1, N, K, adjMatrix, degrees, solutions);

        return solutions;
    }

    private static void backtrackGenerate(int u,
                                          int v,
                                          int N,
                                          int K,
                                          Boolean[][] adj,
                                          int[] degrees,
                                          List<Graph> solutions) {
        // Base case: all edges considered
        if (u == N - 1) {
            boolean isKRegular = true;
            for (int deg : degrees) {
                if (deg != K) {
                    isKRegular = false;
                    break;
                }
            }
            if (isKRegular && isConnected(adj, N)) {
                solutions.add(new Graph(N, adj));
            }
            return;
        }

        int nextU, nextV;
        if (v == N - 1) {
            nextU = u + 1;
            nextV = u + 2;
        } else {
            nextU = u;
            nextV = v + 1;
        }

        // Choice 1: Add edge (u, v)
        if (degrees[u] < K && degrees[v] < K) {
            adj[u][v] = adj[v][u] = true;
            degrees[u]++;
            degrees[v]++;
            backtrackGenerate(nextU, nextV, N, K, adj, degrees, solutions);
            degrees[u]--;
            degrees[v]--;
            adj[u][v] = adj[v][u] = false;
        }

        // Choice 2: Do not add edge (u, v)
        backtrackGenerate(nextU, nextV, N, K, adj, degrees, solutions);
    }

    private static boolean isConnected(Boolean[][] adj, int N) {

        if (N == 0)
            return true;

        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[N];
        int startNode = 0;
        queue.add(startNode);
        visited[startNode] = true;
        int count = 1;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < N; v++) {
                if (adj[u][v] && !visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                    count++;
                }
            }
        }

        return count == N;
    }

    public static Graph genNCycle(int cycleLenght) {
        Graph graph = new Graph(0, new Boolean[0][0]); // Dummy initialization
        graph.initializeNCycle(cycleLenght);
        return graph;
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
