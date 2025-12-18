import java.io.IOException;
import java.util.List;

public class GhostHunter {

    private static double randomPolicy(Game game, int nbSimu) {
        boolean end;
        int totalNbGuesses, guess, newGuess, nbVertices;
        totalNbGuesses = 0;
        guess = -1;

        nbVertices = game.getGraph().getN();

        newGuess = (int)(Math.random() * nbVertices);

        for (int j = 0; j < nbSimu; j++) {
            end = false;
            game.resetGhostPos();

            while (!end) {
                while ((newGuess = (int)(Math.random() * nbVertices)) == guess)
                    ;
                end = game.play(newGuess) == -1;
                guess = newGuess;
                totalNbGuesses++;
            }
        }

        return (double)((double)totalNbGuesses / (double)nbSimu);
    }

    private static double nextVertexPolicy(Game game, int nbSimu) {
        int totalNbGuesses, currentNbGuesses, nbVertices;
        totalNbGuesses = currentNbGuesses = 0;

        nbVertices = game.getGraph().getN();

        for (int j = 0; j < nbSimu; j++) {
            currentNbGuesses = 0;
            game = new Game(game.getGraph());

            while (game.play(currentNbGuesses % nbVertices) != -1) {
                currentNbGuesses++;
            }
            totalNbGuesses += currentNbGuesses;
        }
        return (double)((double)totalNbGuesses / (double)nbSimu);
    }

    private static double singleExecution(ExecConfig config, Graph graph)
        throws IOException, UnsupportedOperationException,
               IllegalArgumentException {
        double meanTime = -1.0;
        System.out.println(graph);

        Game game = new Game(graph);

        switch (config.getPolicy_()) {
        case RANDOM:
            meanTime = randomPolicy(game, config.getNbSimu_());
            break;
        case NEXT_VERTEX:
            meanTime = nextVertexPolicy(game, config.getNbSimu_());
            break;
        default:
            System.err.println("ERROR: wrong policy entered: " +
                               config.getPolicy_());
            System.err.println("Only: RANDOM and NEXT_VERTEX are handled");
            System.exit(1);
        }

        return meanTime;
    }

    private static void upToSizeExecution(ExecConfig config)
        throws IOException, UnsupportedOperationException,
               IllegalArgumentException {
        GraphConfig graphConfig = config.getGraphConfig_();
        for (int n = 3; n <= graphConfig.getN_(); n++) {
            GraphConfig newGraphConfig = new GraphConfig(
                graphConfig.getGraphType_(), graphConfig.getFilename_(), n,
                graphConfig.getK_());
            ExecConfig newExecConfig =
                new ExecConfig(newGraphConfig, config.getNbSimu_(),
                               config.getPolicy_(), config.getExecType_());
            Graph graph = new Graph(newExecConfig.getGraphConfig_());
            System.out.println(
                "Won in average in: " + singleExecution(newExecConfig, graph) +
                " guesses with " + config.getPolicy_() + " policy on " + n +
                " " + graphConfig.getGraphType_());
        }
    }

    private static void familyExecution(ExecConfig config)
        throws IOException, UnsupportedOperationException,
               IllegalArgumentException {
        List<Graph> graphFamily =
            Graph.generateGraphFamily(config.getGraphConfig_());
        System.out.println("Executing on a family of " + graphFamily.size() +
                           " graphs.");

        int graphIndex = 1;
        for (Graph graph : graphFamily) {
            System.out.println("--- Graph " + graphIndex++ + " ---");
            System.out.println("Won in average in: " +
                               singleExecution(config, graph) + " guesses");
        }
    }

    public static void main(String[] args) {

        ExecConfig execConfig = null;
        try {
            execConfig = new ExecConfig(args[0]);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading configuration file: " +
                               e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            switch (execConfig.getExecType_()) {
            case SINGLE:
                Graph graph = new Graph(execConfig.getGraphConfig_());
                System.out.println(
                    "Won in average in: " + singleExecution(execConfig, graph) +
                    " guesses");
                break;
            case UP_TO_SIZE:
                upToSizeExecution(execConfig);
                break;
            case FAMILY:
                familyExecution(execConfig);
                break;
            default:
                System.err.println("ERROR: wrong execution type entered: " +
                                   execConfig.getExecType_());
                System.err.println("Only: SINGLE, UP_TO_SIZE and FAMILY are"
                                   + "allowed");
                System.exit(1);
            }
        } catch (IOException | UnsupportedOperationException |
                 IllegalArgumentException e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
