import java.util.ArrayList;
import java.util.List;

public class GhostHunter {

    private static class PolicyResult {
        int totalLosses;
        double meanGuesses;

        PolicyResult(int totalLosses, double meanGuesses) {
            this.totalLosses = totalLosses;
            this.meanGuesses = meanGuesses;
        }
    }

    private static PolicyResult randomPolicy(Game game, int nbSimu) {
        boolean end;
        int totalNbGuesses, guess, newGuess, nbVertices;
        totalNbGuesses = 0;
        int totalLosses = 0;

        nbVertices = game.getGraph().getN();

        for (int j = 0; j < nbSimu; j++) {
            end = false;
            game.resetGhostPos();
            guess = newGuess = -1;
            int count = 0;

            while (!end) {
                while ((newGuess = (int)(Math.random() * nbVertices)) == guess)
                    ;
                end = game.play(newGuess) == -1;
                guess = newGuess;
                totalNbGuesses++;
                if (count > Math.pow(nbVertices, nbVertices)) {
                    System.out.println("Lost");
                    totalLosses++;
                    break;
                }
                count++;
            }
        }

        return new PolicyResult(totalLosses, (double)totalNbGuesses / (double)nbSimu);
    }

    private static PolicyResult highDegreePrioPolicy(Game game, int nbSimu) {
        boolean end;
        int totalNbGuesses, sumOfDegrees, guess, newGuess, nbVertices;
        double proba;
        double[] probaTresholdPerVertex;

        totalNbGuesses = 0;

        nbVertices = game.getGraph().getN();
        probaTresholdPerVertex = new double[nbVertices];

        // computing the sum of degree over all the graph
        sumOfDegrees = 0;
        ArrayList<Integer>[] neighbors = game.getGraph().getNeighbors();
        for (ArrayList<Integer> iNeighbors : neighbors) {
            sumOfDegrees += iNeighbors.size();
        }

        // computing the proba treshold for each vertex based on their degrees
        for (int i = 0; i < nbVertices; i++) {
            proba = (double)neighbors[i].size() / (double)sumOfDegrees;
            probaTresholdPerVertex[i] =
                i > 0 ? probaTresholdPerVertex[i - 1] + proba : proba;
        }

        // execution of the nbSimu games
        for (int j = 0; j < nbSimu; j++) {
            end = false;
            game.resetGhostPos();
            guess = newGuess = -1;

            while (!end) {
                do {
                    proba = Math.random();
                    for (int i = 0; i < nbVertices; i++) {
                        if (proba < probaTresholdPerVertex[i]) {
                            newGuess = i;
                            break;
                        }
                    }
                } while (newGuess == guess);

                end = game.play(newGuess) == -1;
                guess = newGuess;
                totalNbGuesses++;
            }
        }

        return new PolicyResult(0, (double)((double)totalNbGuesses / (double)nbSimu));
    }


    private static PolicyResult
    nextVertexPolicy(Game game, int nbSimu, GraphType graphType) {

        switch (graphType) {
        case N_CONN:
        case N_COMP:
        case N_K_REGULAR:
        case FROM_FILE:
            System.err.println(
                "ERROR: " + graphType +
                "graphType isn't compatible with the NEXT_VERTEX policy");
            System.exit(1);
            break;
        case N_CYCLE:
        default:
            break;
        }

        int totalNbGuesses = 0, currentNbGuesses = 0, nbVertices;
        int totalLosses = 0;

        nbVertices = game.getGraph().getN();
        for (int j = 0; j < nbSimu; j++) {
            currentNbGuesses = 0;
            game = new Game(game.getGraph());
            while (game.play(currentNbGuesses % nbVertices) != -1) {
                currentNbGuesses++;
                int max = (int)Math.pow(nbVertices, nbVertices);
                // System.out.println(".(max: " + max + ")");
                // System.out.println(".(current: " + currentNbGuesses + ")");
                if (currentNbGuesses > max) {
                    // System.out.println("Lost");
                    totalLosses++;
                    break;
                }
            }
            totalNbGuesses += currentNbGuesses;
        }

        double meanGuesses = (double) totalNbGuesses / (double) nbSimu;
        return new PolicyResult(totalLosses, meanGuesses);
    }

    private static PolicyResult singleExecution(ExecConfig config, Graph graph) {

        GraphType graphType = config.getGraphConfig_().getGraphType_();
        PolicyResult result = null;  // Store the result here
        
        System.out.println(graph);

        Game game = new Game(graph);

        switch (config.getPolicy_()) {
        case RANDOM:
            result = randomPolicy(game, config.getNbSimu_());
            break;
        case NEXT_VERTEX:
            result = nextVertexPolicy(game, config.getNbSimu_(), graphType);
            break;
        case HIGH_DEGREE_PRIO:
            result = highDegreePrioPolicy(game, config.getNbSimu_());
            break;
        default:
            System.err.println("ERROR: wrong policy entered: " +
                            config.getPolicy_());
            System.err.println(
                "ERROR: Only RANDOM and NEXT_VERTEX are handled");
            System.exit(1);
        }

        return result;  // Return the single result
    }

    private static int upToSizeExecution(ExecConfig config) {

        GraphConfig graphConfig = config.getGraphConfig_();

        GraphType graphType = graphConfig.getGraphType_();

        switch (graphType) {
        case N_CONN:
        case N_K_REGULAR:
        case FROM_FILE:
            System.err.println(
                "ERROR: the " + graphType +
                " graphType isn't compatible with the UP_TO_SIZE execType");
            System.exit(1);
            break;
        case N_CYCLE:
        case N_COMP:
        default:
            break;
        }

        double sumOfMeanTime = 0;
        int loss = 0;

        for (int n = 3; n <= graphConfig.getN_(); n++) {

            GraphConfig newGraphConfig = new GraphConfig(
                graphConfig.getGraphType_(), graphConfig.getFilename_(), n,
                graphConfig.getK_());

            ExecConfig newExecConfig =
                new ExecConfig(newGraphConfig, config.getNbSimu_(),
                               config.getPolicy_(), config.getExecType_());

            Graph graph = new Graph(newExecConfig.getGraphConfig_());

            PolicyResult result = singleExecution(newExecConfig, graph);
            double meanTime = result.meanGuesses;
            loss += result.totalLosses;  
            sumOfMeanTime += meanTime;

            System.out.println("Won in average in: " + meanTime +
                               " guesses with " + config.getPolicy_() +
                               " policy on " + n + " " +
                               graphConfig.getGraphType_() + "\n");
        }

        System.out.println(
            "\nWon in average: " + (sumOfMeanTime / (graphConfig.getN_() - 2)) +
            " guesses with " + config.getPolicy_() + " policy over all " +
            graphConfig.getGraphType_() +
            " graphs of the UP_TO_SIZE execution");
        return loss;
    }

    private static int familyExecution(ExecConfig config) {
        GraphConfig gC = config.getGraphConfig_();

        List<Graph> graphFamily = Graph.generateGraphFamily(gC);

        System.out.println("Executing on a family of " + graphFamily.size() +
                           " graphs.");

        int graphIndex = 1;

        double sumOfMeanTime = 0;
        int loss = 0;

        for (Graph graph : graphFamily) {
            System.out.println("--- Graph " + graphIndex++ + " ---");
            GraphType graphType = gC.getGraphType_();
            PolicyResult result = singleExecution(config, graph);
            double meanTime = result.meanGuesses;
            loss += result.totalLosses;  
            sumOfMeanTime += meanTime;
            switch (graphType) {
            case N_K_REGULAR:
                System.out.println("Won in average in: " + meanTime +
                                   " guesses with " + config.getPolicy_() +
                                   " policy on " + gC.getN_() + "," +
                                   gC.getK_() + " " + graphType + "\n");
                break;
            case N_CONN:
                System.out.println("Won in average in: " + meanTime +
                                   " guesses with " + config.getPolicy_() +
                                   " policy on " + gC.getN_() + " " +
                                   graphType + "\n");
                break;
            default:
                break;
            }
        }

        System.out.println(
            "\nWon in average: " + (sumOfMeanTime / graphFamily.size()) +
            " guesses with " + config.getPolicy_() + " policy over all " +
            gC.getGraphType_() + " graphs of the FAMILY execution");
        return loss;
    }
    private static void getLossStatistics(int totalLosses, int totalSimulations) {
        System.out.println(
            "\n--- LOSS STATISTICS ---\n" + 
            "Total losses: " + totalLosses + "\n" +
            "Total wins: " + (totalSimulations - totalLosses) + "\n" +
            "Win rate: " + 
            ((double)(totalSimulations - totalLosses) / (double)totalSimulations * 100) + " %\n");

    }

    public static void main(String[] args) {

        ExecConfig execConfig = null;
        int loss = 0;

        // Load the configuration
        execConfig = new ExecConfig(args[0]);

        // Begin the experiment
        switch (execConfig.getExecType_()) {
        case SINGLE:
            Graph graph = new Graph(execConfig.getGraphConfig_());
            PolicyResult result = singleExecution(execConfig, graph);
            if (result.totalLosses > 0) {
                getLossStatistics(result.totalLosses, execConfig.getNbSimu_());
            } 
            else {
                System.out.println(
                    "Won in average in: " + result.meanGuesses +
                    " guesses\n");
            }
            break;
        case UP_TO_SIZE:
            loss = upToSizeExecution(execConfig);
            if (loss > 0) {
                getLossStatistics(loss,execConfig.getNbSimu_());
            }
            break;
        case FAMILY:
            loss = familyExecution(execConfig);
            if (loss > 0) {
                getLossStatistics(loss,execConfig.getNbSimu_());
            }
            break;
        default:
            System.err.println("ERROR: wrong execution type entered: " +
                               execConfig.getExecType_());
            System.err.println("Only: SINGLE, UP_TO_SIZE and FAMILY are"
                               + "allowed");
            System.exit(1);
        }
    }
}
