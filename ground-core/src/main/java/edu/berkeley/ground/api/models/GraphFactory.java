package edu.berkeley.ground.api.models;

import edu.berkeley.ground.db.DBClient.GroundDBConnection;
import edu.berkeley.ground.exceptions.GroundException;

import java.util.Optional;

public abstract class GraphFactory {
    public abstract Graph create(String name) throws GroundException;

    public abstract Graph retrieveFromDatabase(String name) throws GroundException;

    public abstract void update(GroundDBConnection connection, String itemId, String childId, Optional<String> parent) throws GroundException;

    protected static Graph construct(String id, String name) {
        return new Graph(id, name);
    }
}
