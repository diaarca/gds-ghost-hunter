public class GhostHunter {
    public static void main(String[] args) {

        Graph graph;

        // if (args.length > 0) {
        //     String filename = args[0];
        //     g = new Graph(filename);
        //     System.out.println("Loaded graph from file: " + filename);
        // } else {
        //     int n = 3;
        //     Boolean[][] adj = {
        //         {false, true, true}, {true, false, true}, {true, true,
        //         false}};
        //
        //     g = new Graph(n, adj);
        //     System.out.println(
        //         "Using built-in graph (no filename provided). To load from "
        //         + "file, pass the filename as an argument.");
        // }

        Game game;
        boolean end;
        double meanTime, ratio;
        int nbVertices, nbSimu, nbGuess, guess, newGuess, randTotal,
            nextVertexTotal;

        int[] config = {3, 4, 5, 6, 7, 8, 9, 10};

        nbSimu = 1000000;
        nbGuess = randTotal = nextVertexTotal = guess = 0;

        for (int c : config) {

            graph = new Graph(c);
            nbVertices = graph.getN();
            System.out.println("NEW GRAPH\n" + graph);

            newGuess = (int)(Math.random() * nbVertices);
            game = new Game(graph);

            // random policy
            for (int j = 0; j < nbSimu; j++) {
                nbGuess = 0;
                end = false;
                game.resetGhostPos();;

                while (!end) {
                    if (nbGuess != 0) {
                        while ((newGuess = (int)(Math.random() * nbVertices)) ==
                               guess)
                            ;
                    }
                    end = game.play(newGuess) == -1;
                    guess = newGuess;
                    nbGuess++;
                }
                randTotal += nbGuess;
            }
            meanTime = (double)((double)randTotal / (double)nbSimu);
            System.out.println("Won in mean time of: " + meanTime +
                               " guesses with random policy on " + c +
                               "-cycle");

            // next vertex policy
            for (int j = 0; j < nbSimu; j++) {
                nbGuess = 0;
                end = false;
                game = new Game(graph);

                // System.out.println();
                while (!end) {
                    end = game.play(nbGuess % nbVertices) == -1;
                    nbGuess++;
                }
                nextVertexTotal += nbGuess;
            }
            meanTime = (double)((double)nextVertexTotal / (double)nbSimu);
            System.out.println("Won in mean time of: " + meanTime +
                               " guesses next vertex policy on " + c +
                               "-cycle");

            ratio = (double)((double)randTotal / (double)nextVertexTotal);
            System.out.println("Ratio of " + ratio +
                               "for rand / nextV policies on " + c +
                               "-cycle\n");
        }
    }
}
