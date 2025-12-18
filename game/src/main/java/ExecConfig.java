import com.moandjiezana.toml.Toml;
import java.io.File;
import java.io.IOException;

public class ExecConfig {
    private GraphConfig graphConfig_;
    private int nbSimu_;
    private Policy policy_;
    private ExecType execType_;

    public ExecConfig(GraphConfig graphConfig,
                      int nbSimu,
                      Policy policy,
                      ExecType execType) {
        if (graphConfig.getGraphType_() == GraphType.N_K_REGULAR &&
            execType != ExecType.FAMILY) {
            throw new IllegalArgumentException(
                "N_K_REGULAR graph type is only allowed with FAMILY " +
                "execution type.");
        }
        graphConfig_ = graphConfig;
        nbSimu_ = nbSimu;
        policy_ = policy;
        execType_ = execType;
    }

    public ExecConfig(String filePath) throws IOException {
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            throw new IOException("Configuration file not found: " + filePath);
        }

        Toml toml = new Toml().read(configFile);

        // Load graphConfig
        Toml graphConfigToml = toml.getTable("graphConfig");
        if (graphConfigToml == null) {
            throw new IOException(
                "Missing [graphConfig] table in configuration file.");
        }

        GraphType graphType = GraphType.valueOf(
            graphConfigToml.getString("graphType").toUpperCase());
        ExecType execType =
            ExecType.valueOf(toml.getString("execType").toUpperCase());

        if (graphType == GraphType.N_K_REGULAR && execType != ExecType.FAMILY) {
            throw new IllegalArgumentException(
                "N_K_REGULAR graph type is only allowed with FAMILY " +
                "execution type.");
        }

        this.graphConfig_ =
            new GraphConfig(graphType, graphConfigToml.getString("filename"),
                            graphConfigToml.getLong("N", 0L).intValue(),
                            graphConfigToml.getLong("K", 0L).intValue());

        this.nbSimu_ = toml.getLong("nbSimu").intValue();
        this.policy_ = Policy.valueOf(toml.getString("policy").toUpperCase());
        this.execType_ = execType;
    }

    public GraphConfig getGraphConfig_() { return graphConfig_; }

    public int getNbSimu_() { return nbSimu_; }

    public Policy getPolicy_() { return policy_; }

    public ExecType getExecType_() { return execType_; }
}
