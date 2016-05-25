package edu.berkeley.ground;

import edu.berkeley.ground.db.CassandraClient;
import edu.berkeley.ground.db.PostgresClient;
import edu.berkeley.ground.db.TitanClient;
import edu.berkeley.ground.resources.*;
import edu.berkeley.ground.util.CassandraFactories;
import edu.berkeley.ground.util.PostgresFactories;
import edu.berkeley.ground.util.TitanFactories;
import org.junit.Before;

import java.io.File;


public class GroundResourceTest {
    private static final String BACKING_STORE_TYPE = "titan";
    private static final String TEST_DB_NAME = "test";

    protected NodesResource nodesResource;
    protected EdgesResource edgesResource;
    protected GraphsResource graphsResource;
    protected LineageEdgesResource lineageEdgesResource;
    protected StructuresResource structuresResource;

    @Before
    public void setUp() {
        try {
            switch (BACKING_STORE_TYPE) {
                case "postgres": {
                    setBackingStore();

                    Process p = Runtime.getRuntime().exec("python2.7 postgres_setup.py " + TEST_DB_NAME, null, new File("scripts/postgres/"));
                    p.waitFor();

                    break;
                }

                case "cassandra": {
                    setBackingStore();

                    Process p = Runtime.getRuntime().exec("python2.7 cassandra_setup.py " + TEST_DB_NAME, null, new File("scripts/cassandra/"));
                    p.waitFor();

                    break;
                }

                case "titan": {
                    Process p = Runtime.getRuntime().exec("python2.7 drop_cassandra.py", null, new File("scripts/titan/"));
                    p.waitFor();

                    setBackingStore();

                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("FATAL: Unexpected Exception. " + e.getMessage());
        }
    }

    private void setBackingStore() {
        switch (BACKING_STORE_TYPE) {
            case "postgres": {
                PostgresClient dbClient = new PostgresClient("localhost", 5432, "test", "ground", "metadata");
                PostgresFactories factoryGenerator = new PostgresFactories(dbClient);

                nodesResource = new NodesResource(factoryGenerator.getNodeFactory(), factoryGenerator.getNodeVersionFactory());
                edgesResource = new EdgesResource(factoryGenerator.getEdgeFactory(), factoryGenerator.getEdgeVersionFactory());
                graphsResource = new GraphsResource(factoryGenerator.getGraphFactory(), factoryGenerator.getGraphVersionFactory());
                lineageEdgesResource = new LineageEdgesResource(factoryGenerator.getLineageEdgeFactory(), factoryGenerator.getLineageEdgeVersionFactory());
                structuresResource = new StructuresResource(factoryGenerator.getStructureFactory(), factoryGenerator.getStructureVersionFactory());
                break;
            }

            case "cassandra": {
                CassandraClient dbClient = new CassandraClient("localhost", 5432, "test", "ground", "metadata");
                CassandraFactories factoryGenerator = new CassandraFactories(dbClient);

                nodesResource = new NodesResource(factoryGenerator.getNodeFactory(), factoryGenerator.getNodeVersionFactory());
                edgesResource = new EdgesResource(factoryGenerator.getEdgeFactory(), factoryGenerator.getEdgeVersionFactory());
                graphsResource = new GraphsResource(factoryGenerator.getGraphFactory(), factoryGenerator.getGraphVersionFactory());
                lineageEdgesResource = new LineageEdgesResource(factoryGenerator.getLineageEdgeFactory(), factoryGenerator.getLineageEdgeVersionFactory());
                structuresResource = new StructuresResource(factoryGenerator.getStructureFactory(), factoryGenerator.getStructureVersionFactory());
                break;
            }

            case "titan": {
                TitanClient dbClient = new TitanClient(false);
                TitanFactories factoryGenerator = new TitanFactories(dbClient);

                nodesResource = new NodesResource(factoryGenerator.getNodeFactory(), factoryGenerator.getNodeVersionFactory());
                edgesResource = new EdgesResource(factoryGenerator.getEdgeFactory(), factoryGenerator.getEdgeVersionFactory());
                graphsResource = new GraphsResource(factoryGenerator.getGraphFactory(), factoryGenerator.getGraphVersionFactory());
                lineageEdgesResource = new LineageEdgesResource(factoryGenerator.getLineageEdgeFactory(), factoryGenerator.getLineageEdgeVersionFactory());
                structuresResource = new StructuresResource(factoryGenerator.getStructureFactory(), factoryGenerator.getStructureVersionFactory());

                dbClient.createSchema();
                break;
            }

        }
    }
}