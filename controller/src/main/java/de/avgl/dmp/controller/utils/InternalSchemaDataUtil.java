package de.avgl.dmp.controller.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.google.common.collect.AbstractIterator;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.avgl.dmp.controller.DMPControllerException;
import de.avgl.dmp.persistence.DMPPersistenceException;
import de.avgl.dmp.persistence.model.internal.Model;
import de.avgl.dmp.persistence.model.jsonschema.JSRoot;
import de.avgl.dmp.persistence.model.resource.Configuration;
import de.avgl.dmp.persistence.model.resource.Resource;
import de.avgl.dmp.persistence.model.types.Tuple;
import de.avgl.dmp.persistence.service.InternalService;
import de.avgl.dmp.persistence.service.InternalServiceFactory;
import de.avgl.dmp.persistence.service.resource.ResourceService;
import de.avgl.dmp.persistence.service.schema.SchemaService;

@Singleton
public class InternalSchemaDataUtil {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InternalSchemaDataUtil.class);
	private final ObjectMapper objectMapper;
	private final Provider<ResourceService> resourceServiceProvider;
	private final Provider<InternalServiceFactory> internalServiceFactoryProvider;
	private final Provider<SchemaService> schemaServiceProvider;

	@Inject
	public InternalSchemaDataUtil(final ObjectMapper objectMapper, final Provider<ResourceService> resourceServiceProvider,
								  final Provider<InternalServiceFactory> internalServiceFactoryProvider,
								  final Provider<SchemaService> schemaServiceProvider) {
		this.objectMapper = objectMapper;
		this.resourceServiceProvider = resourceServiceProvider;
		this.internalServiceFactoryProvider = internalServiceFactoryProvider;
		this.schemaServiceProvider = schemaServiceProvider;
	}


	public Optional<Iterator<Tuple<String, JsonNode>>> getData(final long resourceId, final long configurationId) {
		return getData(resourceId, configurationId, Optional.<Integer>absent());
	}

	public Optional<Iterator<Tuple<String, JsonNode>>> getData(long resourceId, long configurationId, Optional<Integer> atMost) {

		LOG.debug(String.format("try to get data for configuration with id [%d] for resource with id [%d]", configurationId, resourceId));

		final Optional<Configuration> configurationOptional = fetchConfiguration(resourceId, configurationId);

		if (!configurationOptional.isPresent()) {

			return Optional.absent();
		}

		final InternalService internalService;
		try {
			internalService = determineInternalService(configurationOptional.get());
		} catch (DMPControllerException e) {
			return Optional.absent();
		}

		Optional<Map<String, Model>> maybeTriples;

		try {

			maybeTriples = internalService.getObjects(resourceId, configurationId, atMost);
		} catch (final DMPPersistenceException e1) {

			LOG.debug(e1);
			return Optional.absent();
		}

		if (!maybeTriples.isPresent()) {

			LOG.debug("couldn't find data");
			return Optional.absent();
		}

		final Iterator<Map.Entry<String, Model>> iterator = maybeTriples.get().entrySet().iterator();

		return Optional.of(dataIterator(iterator));
	}

	public Optional<ObjectNode> getSchema(long resourceId, long configurationId) {

		final Optional<Configuration> configurationOptional = fetchConfiguration(resourceId, configurationId);

		if (!configurationOptional.isPresent()) {

			return Optional.absent();
		}

		final Configuration configuration = configurationOptional.get();

		// TODO: fixme
		
		final Optional<JSRoot> rootOptional = null;
				
//				schemaServiceProvider.get().getSchema(resourceId, configurationId)
//				.or(getConfiguredSchema(configuration));

		if (rootOptional.isPresent()) {

			final JSRoot jsElements = rootOptional.get();

			try {
				return Optional.of(jsElements.toJson(objectMapper));
			} catch (IOException e) {
				LOG.warn(e.getMessage(), e);
				return Optional.absent();
			}
		}

		final InternalService internalService;
		try {
			internalService = determineInternalService(configurationOptional.get());
		} catch (DMPControllerException e) {

			return Optional.absent();
		}

		final Optional<Set<String>> schemaOptional = internalService.getSchema(resourceId, configurationId);

		if (!schemaOptional.isPresent()) {

			LOG.debug("couldn't find schema");
			return Optional.absent();
		}

		final ObjectNode node = objectMapper.createObjectNode();
		node.put("title", configuration.getName());
		node.put("type", "object");
		final ObjectNode properties = node.putObject("properties");

		for (final String schemaProp : schemaOptional.get()) {
			properties.putObject(schemaProp).put("type", "string");
		}

		return Optional.of(node);
	}

	public Optional<Resource> fetchResource(final long resourceId) {

		final ResourceService resourceService = resourceServiceProvider.get();
		final Resource resource = resourceService.getObject(resourceId);

		return Optional.fromNullable(resource);
	}

	public Optional<Configuration> fetchConfiguration(final long resourceId, final long configurationId) {
		final Optional<Resource> resourceOptional = fetchResource(resourceId);

		if (!resourceOptional.isPresent()) {

			LOG.debug("couldn't find  resource '" + resourceId);
			return Optional.absent();
		}

		final Configuration configuration = resourceOptional.get().getConfiguration(configurationId);

		return Optional.fromNullable(configuration);
	}

	private InternalService determineInternalService(final Configuration configuration) throws DMPControllerException {

		final JsonNode storageType = configuration.getParameters().get("storage_type");

		if (storageType != null) {

			if ("schema".equals(storageType.asText())) {

				return internalServiceFactoryProvider.get().getMemoryDbInternalService();
			} else if ("csv".equals(storageType.asText())) {

				return internalServiceFactoryProvider.get().getMemoryDbInternalService();
			} else if ("xml".equals(storageType.asText())) {

				return internalServiceFactoryProvider.get().getInternalTripleService();
			} else {

				throw new DMPControllerException("couldn't determine internal service type from storage type = '" + storageType.asText() + "'");
			}
		} else {

			throw new DMPControllerException("couldn't determine storage type from configuration");
		}
	}

	private Optional<JSRoot> getConfiguredSchema(Configuration configuration) {
		final ObjectNode parameters = configuration.getParameters();
		final JsonNode storageType = parameters.get("storage_type");
		if ("xml".equals(storageType.asText())) {

			final JsonNode schemaFile = parameters.get("schema_file");
			if (schemaFile != null) {
				final long schemaId = Long.valueOf(schemaFile.get("id").asText());
				final Optional<Resource> schemaOptional = fetchResource(schemaId);
				if (schemaOptional.isPresent()) {

					long latestConfigId = Integer.MIN_VALUE;

					for (Configuration schemaConfiguration : schemaOptional.get().getConfigurations()) {

						if (schemaConfiguration.getId() > latestConfigId) {
							latestConfigId = schemaConfiguration.getId();
						}
					}

					if (latestConfigId != Integer.MIN_VALUE) {
						
						// TODO: fixme

						// return schemaServiceProvider.get().getSchema(schemaId, latestConfigId);
					}
				}
			}
		}

		return Optional.absent();
	}

	private Iterator<Tuple<String, JsonNode>> dataIterator(final Iterator<Map.Entry<String, Model>> triples) {
		return new AbstractIterator<Tuple<String, JsonNode>>() {
			@Override
			protected Tuple<String, JsonNode> computeNext() {
				if (triples.hasNext()) {
					final Map.Entry<String, Model> nextTriple = triples.next();
					return Tuple.tuple(nextTriple.getKey(), nextTriple.getValue().toJSON());
				}
				return endOfData();
			}
		};
	}
}
