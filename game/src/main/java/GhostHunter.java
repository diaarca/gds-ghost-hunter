public class GhostHunter {
    public static void main(String[] args) {
        int n = 5;
        Boolean[][] adj = {{false, true, true, true, true},
                           {true, false, false, false, false},
                           {true, false, false, true, false},
                           {true, false, true, false, true},
                           {true, false, false, true, false}};
        Graph g = new Graph(n, adj);
        // System.out.println(g);

        Game game = new Game(g);
        // System.out.println(game);

        int nbGame = 1000000;
        int result = 0;
        for (int j = 0; j < nbGame; j++) {
            // System.out.println();
            while (game.play((int)(Math.random() * n)) != -1) {
                // System.out.println();
                result++;
            };
        }
        System.out.println("Won in mean time of: " +
                           (double)((double)result / (double)nbGame) +
                           " guesses");

        // System.out.println("Won in " + i + " guesses");
    }
}
