package edu.berkeley.ground.api.models;

import edu.berkeley.ground.exceptions.GroundException;

import java.util.Map;
import java.util.Optional;

public abstract class NodeVersionFactory {
    public abstract NodeVersion create(Optional<Map<String, Tag>> tags,
                                       Optional<String> structureVersionId,
                                       Optional<String> reference,
                                       Optional<Map<String, String>> parameters,
                                       String nodeId,
                                       Optional<String> parentId) throws GroundException;

    public abstract NodeVersion retrieveFromDatabase(String id) throws GroundException;

    public static NodeVersion construct(String id,
                                        Optional<Map<String, Tag>> tags,
                                        Optional<String> structureVersionId,
                                        Optional<String> reference,
                                        Optional<Map<String, String>> parameters,
                                        String nodeId) {

        return new NodeVersion(id, tags, structureVersionId, reference, parameters, nodeId);
    }
}
