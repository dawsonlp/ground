package edu.berkeley.ground.api.versions.cassandra;

import edu.berkeley.ground.api.versions.*;
import edu.berkeley.ground.db.CassandraClient.CassandraConnection;
import edu.berkeley.ground.db.DBClient;
import edu.berkeley.ground.db.DBClient.GroundDBConnection;
import edu.berkeley.ground.db.DbDataContainer;
import edu.berkeley.ground.db.QueryResults;
import edu.berkeley.ground.exceptions.GroundException;

import java.util.ArrayList;
import java.util.List;

public class CassandraVersionHistoryDAGFactory extends VersionHistoryDAGFactory {
    private CassandraVersionSuccessorFactory versionSuccessorFactory;

    public CassandraVersionHistoryDAGFactory(CassandraVersionSuccessorFactory versionSuccessorFactory) {
        this.versionSuccessorFactory = versionSuccessorFactory;
    }

    public <T extends Version> VersionHistoryDAG<T> create(String itemId) throws GroundException {
        return construct(itemId);
    }

    public <T extends Version> VersionHistoryDAG<T> retrieveFromDatabase(GroundDBConnection connectionPointer, String itemId) throws GroundException {
        CassandraConnection connection = (CassandraConnection) connectionPointer;

        List<DbDataContainer> predicates = new ArrayList<>();
        predicates.add(new DbDataContainer("item_id", Type.STRING, itemId));

        QueryResults resultSet = connection.equalitySelect("VersionHistoryDAGs", DBClient.SELECT_STAR, predicates);

        List<VersionSuccessor<T>> edges = new ArrayList<>();
        do {
            edges.add(this.versionSuccessorFactory.retrieveFromDatabase(connection, resultSet.getString(1)));
        } while (resultSet.next());

        return VersionHistoryDAGFactory.construct(itemId, edges);
    }

    public void addEdge(GroundDBConnection connectionPointer, VersionHistoryDAG dag, String parentId, String childId, String itemId) throws GroundException {
        CassandraConnection connection = (CassandraConnection) connectionPointer;

        VersionSuccessor successor = this.versionSuccessorFactory.create(connection, parentId, childId);

        List<DbDataContainer> insertions = new ArrayList<>();
        insertions.add(new DbDataContainer("item_id", Type.STRING, itemId));
        insertions.add(new DbDataContainer("successor_id", Type.STRING, successor.getId()));

        connection.insert("VersionHistoryDAGs", insertions);

        dag.addEdge(parentId, childId, successor.getId());
    }
}
