public class GraphConfig {
    private GraphType graphType_;
    private String filename_;
    private int N_;
    private int K_;

    public GraphConfig(GraphType graphType, String filename, int N, int K) {
        graphType_ = graphType;
        filename_ = filename;
        N_ = N;
        K_ = K;
    }

    public GraphType getGraphType_() { return graphType_; }

    public String getFilename_() { return filename_; }

    public int getN_() { return N_; }

    public int getK_() { return K_; }

    // maybe generate the graph from all paramaters here ?
}
