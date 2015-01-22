/**
 * Copyright (C) 2013, 2014 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dswarm.persistence.model.resource.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import org.dswarm.persistence.model.ExtendedBasicDMPJPAObject;
import org.dswarm.persistence.model.resource.Configuration;
import org.dswarm.persistence.model.resource.Resource;
import org.dswarm.persistence.service.UUIDService;

/**
 * @author tgaengler
 */
public class ConfigurationDeserializer extends JsonDeserializer<Configuration> {

	private static final String UUID_KEY        = "uuid";
	private static final String NAME_KEY        = "name";
	private static final String DESCRIPTION_KEY = "description";
	private static final String PARAMETERS_KEY  = "parameters";
	private static final String RESOURCES_KEY   = "resources";

	@Override public Configuration deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {

		final String uuid = UUIDService.getUUID(Configuration.class.getSimpleName());

		Configuration configuration = new Configuration(uuid);

		String currentFieldName = null;
		JsonToken currentToken = jp.getCurrentToken();

		while (currentToken != JsonToken.END_OBJECT) {
			switch (currentToken) {
				case FIELD_NAME:
					currentFieldName = jp.getText();
					break;

				case VALUE_STRING:
					if (currentFieldName != null) {
						switch (currentFieldName) {
							case ConfigurationDeserializer.UUID_KEY:
								configuration = Configuration.withId(configuration, jp.getText());
								break;
							default:
								ConfigurationDeserializer.setStringValue(jp, configuration, currentFieldName);
								break;
						}
					}
					break;

				case START_ARRAY:

					if (ConfigurationDeserializer.RESOURCES_KEY.equals(currentFieldName)) {
						setResources(jp, configuration);
					}
					break;

				case START_OBJECT:

					// parameters are a JSON object
					if (ConfigurationDeserializer.PARAMETERS_KEY.equals(currentFieldName)) {
						ConfigurationDeserializer.setParameters(jp, configuration);
					}
					break;

				default: // no-op
			}

			currentToken = jp.nextToken();
		}

		return configuration;
	}

	/**
	 * Set common string values (name, and description) for either a configuration
	 *
	 * @param jp               the current json parser
	 * @param object           either the configuration
	 * @param currentFieldName the json field name
	 * @throws IOException
	 */
	private static void setStringValue(final JsonParser jp, final ExtendedBasicDMPJPAObject object, final String currentFieldName)
			throws IOException {
		if (ConfigurationDeserializer.NAME_KEY.equals(currentFieldName)) {

			object.setName(jp.getText());

		} else if (ConfigurationDeserializer.DESCRIPTION_KEY.equals(currentFieldName)) {

			object.setDescription(jp.getText());
		}
	}

	/**
	 * Set the parameter list of a configuration.
	 *
	 * @param jp            the current json parser
	 * @param configuration the target {@code Configuration}
	 * @throws IOException
	 */
	private static void setParameters(final JsonParser jp, final Configuration configuration) throws IOException {

		final ObjectNode parameters = jp.readValueAs(ObjectNode.class);
		configuration.setParameters(parameters);
	}

	/**
	 * Set the resources of a configuration. This will parse all resources and cache them into a map. Simultaneously, it will
	 * parse the (in|out)put_components and build a map of {@code id -> (inIds, outIds)}. After having parsed all components, the
	 * actual input components and output components are resolved, linked, and finally inserted into each component.
	 *
	 * @param jp            the current json parser
	 * @param configuration the target {@code Transformation}
	 * @throws IOException
	 */
	private void setResources(final JsonParser jp, final Configuration configuration) throws IOException {
		final Map<String, Resource> resources = Maps.newLinkedHashMap();

		JsonToken currentToken = jp.getCurrentToken();
		String currentFieldName = null;

		while (currentToken != JsonToken.END_ARRAY) {

			while (currentToken != JsonToken.START_OBJECT && currentToken != JsonToken.END_ARRAY) {
				currentToken = jp.nextToken();
			}
			if (currentToken == JsonToken.END_ARRAY) {
				break;
			}
			currentToken = jp.nextToken();

			Optional<String> currentResourceId = Optional.absent();

			Resource currentResource = new Resource(UUIDService.getUUID(Resource.class.getSimpleName()));

			while (currentToken != JsonToken.END_OBJECT) {
				switch (currentToken) {
					case FIELD_NAME:
						currentFieldName = jp.getText();
						break;

					case VALUE_STRING:

						if (ConfigurationDeserializer.UUID_KEY.equals(currentFieldName)) {
							currentResourceId = Optional.fromNullable(Strings.emptyToNull(jp.getText()));
							if (!currentResourceId.isPresent()) {
								throw JsonMappingException.from(jp, "could not create resource, i.e., uuid is not provided");
							}

							currentResource = createNewResourceWithId(currentResource, currentResourceId.get());
							resources.put(currentResourceId.get(), currentResource);
						} else {
							ConfigurationDeserializer.setStringValue(jp, currentResource, currentFieldName);
						}
						break;

					default: // no-op
				}

				currentToken = jp.nextToken();
			}

			if (!currentResourceId.isPresent()) {
				throw JsonMappingException.from(jp, String.format("This resource [%s] is missing an ID", currentResource));
			}

			currentToken = jp.nextToken();
		}

		ConfigurationDeserializer.assignResourceIds(resources);

		ConfigurationDeserializer.addResources(configuration, resources);
	}

	private Resource createNewResourceWithId(final Resource currentResource, final String resourceId) throws JsonMappingException {
		return Resource.withId(currentResource, resourceId);
	}

	/**
	 * Update the cache by making sure, that every resource as a valid id set. The reason behind this is, that {@code Resource}s
	 * are compared only by their ID, if they are compared in a shallow manner (e.g. when used in a {@code Set}).
	 *
	 * @param resources a map of (id -> {@code Resource})
	 */
	private static void assignResourceIds(final Map<String, Resource> resources) {
		for (final String resourceId : resources.keySet()) {

			final Resource resource = resources.get(resourceId);
			if (resource.getUuid() == null) {
				final Resource newResource = Resource.withId(resource, resourceId);
				resources.put(resourceId, newResource);
			}
		}
	}

	/**
	 * Set the resources for a configuration.
	 *
	 * @param configuration the target {@code Configuration}
	 * @param resources     the target resources, a map of (id -> {@code Resource})
	 */
	private static void addResources(final Configuration configuration, final Map<String, Resource> resources) {
		resources.values().forEach(configuration::addResource);
	}

}
