public class GhostHunter {
    public static void main(String[] args) {
        int n = 5;
        Boolean[][] adj = {{false, true, true, true, true},
                           {true, false, false, false, false},
                           {true, false, false, true, false},
                           {true, false, true, false, true},
                           {true, false, false, true, false}};
        Graph g = new Graph(n, adj);
        System.out.println(g);
        for (int i = 0; i < n; i++) {
            System.out.println(g.getNeighborList(i));
        }
    }
}
