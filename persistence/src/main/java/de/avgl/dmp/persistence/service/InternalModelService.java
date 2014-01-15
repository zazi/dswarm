package de.avgl.dmp.persistence.service;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;

import de.avgl.dmp.persistence.DMPPersistenceException;
import de.avgl.dmp.persistence.model.internal.Model;
import de.avgl.dmp.persistence.model.resource.DataModel;
import de.avgl.dmp.persistence.model.schema.Schema;

/**
 * A service for persistence work with internal models, i.e., data of {@link DataModel}s.
 * 
 * @author phorn
 * @author tgaengler
 */
public interface InternalModelService {

	@Deprecated
	Optional<Set<String>> getSchema(Long resourceId, Long configurationId);

	/**
	 * Persists an object (model) to a data model.
	 * 
	 * @param dataModelId the identifier of the data model
	 * @param model the model of the object that should be persisted
	 * @throws DMPPersistenceException
	 */
	void createObject(Long dataModelId, Object model) throws DMPPersistenceException;

	/**
	 * Retrieves a collection of objects from a data model.
	 * 
	 * @param dataModelId the identifier of the data model
	 * @param atMost the number of objects that should be retrieved at most
	 * @return (optional) a map of objects and their identifier
	 * @throws DMPPersistenceException
	 */
	Optional<Map<String, Model>> getObjects(Long dataModelId, Optional<Integer> atMost) throws DMPPersistenceException;

	/**
	 * Deletes a whole data model (incl. all its objects).
	 * 
	 * @param dataModelId the identifier of the data model
	 * @throws DMPPersistenceException
	 */
	void deleteObject(Long dataModelId) throws DMPPersistenceException;

	/**
	 * Retrieves the schema of the data model.
	 * 
	 * @param dataModelId the identifier of the data model
	 * @return (optional) the schema of the data model
	 * @throws DMPPersistenceException
	 */
	Optional<Schema> getSchema(Long dataModelId) throws DMPPersistenceException;
}
