public class Game {

    private Graph graph_;
    private int ghostPosition_;

    public Game(Graph graph) {
        graph_ = graph;
        ghostPosition_ = (int)(Math.random() * graph_.getN());
    }

    public Graph getGraph() { return graph_; }

    public int getGhostPosition() { return ghostPosition_; }

    public int play(int guessVertex) {
        // System.out.println("The hunter guess on the vertex " + guessVertex);
        int res;
        if (guessVertex == ghostPosition_) {
            // System.out.println("Ghost hunter won the game");
            res = -1;
        } else {
            // System.out.println(
            //     "Ghost hunter missed the ghost, it was on vertex " +
            //     ghostPosition_);
            // System.out.println("Ghost moves on a neighbor vertex...");
            var neighbors = graph_.getNeighborList(ghostPosition_);
            ghostPosition_ =
                neighbors.get((int)(Math.random() * neighbors.size()));
            res = ghostPosition_;
        }
        return res;
    }

    public void resetGhostPos() {
        ghostPosition_ = (int)(Math.random() * graph_.getN());
    }

    public String toString() {
        String str = "Game state:\nGraph: " + graph_;
        str += "The ghost is on vertex " + ghostPosition_;
        return str;
    }
}
