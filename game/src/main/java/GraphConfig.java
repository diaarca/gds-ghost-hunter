public class GraphConfig {
    private GraphType graphType_;
    private String filename_;
    private int N_;
    private int K_;

    public GraphConfig(GraphType graphType, String filename, int N, int K) {
        graphType_ = graphType;
        filename_ = filename;
        N_ = N;

        switch (graphType) {
            case N_CYCLE:
            case N_COMP:
            case N_K_REGULAR:
                if (N_ <= 0) {
                    System.err.println("N must be positive");
                    System.exit(1);
                }
                break;
            default:
                break;
        }

        K_ = K;

        switch (graphType) {
            case N_K_REGULAR:
                if (K_ <= 0) {
                    System.err.println("K must be positive");
                    System.exit(1);
                }
                break;
            default:
                break;
        }
    }

    public GraphType getGraphType_() { return graphType_; }

    public String getFilename_() { return filename_; }

    public int getN_() { return N_; }

    public int getK_() { return K_; }

    // maybe generate the graph from all paramaters here ?
}
