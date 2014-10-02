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
package org.dswarm.controller.resources;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.controller.DMPControllerException;
import org.dswarm.controller.resources.utils.AdvancedDMPResourceUtils;
import org.dswarm.controller.status.DMPStatus;
import org.dswarm.persistence.DMPPersistenceException;
import org.dswarm.persistence.model.AdvancedDMPJPAObject;
import org.dswarm.persistence.model.BasicDMPJPAObject;
import org.dswarm.persistence.model.proxy.ProxyAdvancedDMPJPAObject;
import org.dswarm.persistence.service.AdvancedDMPJPAService;

/**
 * A generic resource (controller service) implementation for {@link BasicDMPJPAObject}s, i.e., objects where the identifier will
 * be generated by the database and that can have a name.
 * 
 * @author tgaengler
 * @param <POJOCLASSPERSISTENCESERVICE> the concrete persistence service of the resource that is related to the concrete POJO
 *            class
 * @param <POJOCLASS> the concrete POJO class of the resource
 */
public abstract class AdvancedDMPResource<POJOCLASSRESOURCEUTILS extends AdvancedDMPResourceUtils<POJOCLASSPERSISTENCESERVICE, PROXYPOJOCLASS, POJOCLASS>, POJOCLASSPERSISTENCESERVICE extends AdvancedDMPJPAService<PROXYPOJOCLASS, POJOCLASS>, PROXYPOJOCLASS extends ProxyAdvancedDMPJPAObject<POJOCLASS>, POJOCLASS extends AdvancedDMPJPAObject>
		extends BasicDMPResource<POJOCLASSRESOURCEUTILS, POJOCLASSPERSISTENCESERVICE, PROXYPOJOCLASS, POJOCLASS> {

	private static final Logger	LOG	= LoggerFactory.getLogger(AdvancedDMPResource.class);

	/**
	 * Creates a new resource (controller service) for the given concrete POJO class with the provider of the concrete persistence
	 * service, the object mapper and metrics registry.
	 * 
	 * @param clasz a concrete POJO class
	 * @param persistenceServiceProviderArg the concrete persistence service that is related to the concrete POJO class
	 * @param objectMapperArg an object mapper
	 * @param dmpStatusArg a metrics registry
	 */
	public AdvancedDMPResource(final POJOCLASSRESOURCEUTILS pojoClassResourceUtilsArg, final DMPStatus dmpStatusArg) {

		super(pojoClassResourceUtilsArg, dmpStatusArg);
	}

	@Override
	protected POJOCLASS retrieveObject(final Long id, final String jsonObjectString) throws DMPControllerException {

		if (jsonObjectString == null) {

			return super.retrieveObject(id, jsonObjectString);
		}

		// what should we do if the uri is a different one, i.e., someone tries to manipulate the uri? => check whether an entity
		// with this uri exists and manipulate this one instead
		// note: we could also throw an exception instead

		final POJOCLASS objectFromJSON = pojoClassResourceUtils.deserializeObjectJSONString(jsonObjectString);

		// get persistent object per uri

		final POJOCLASSPERSISTENCESERVICE persistenceService = pojoClassResourceUtils.getPersistenceService();

		POJOCLASS object = null;
		try {
			object = persistenceService.getObjectByUri(objectFromJSON.getUri());
		} catch (final DMPPersistenceException e) {

			AdvancedDMPResource.LOG
					.debug("couldn't retrieve " + pojoClassResourceUtils.getClaszName() + " for uri '" + objectFromJSON.getUri() + "'");

			return null;
		}

		if (object == null) {

			AdvancedDMPResource.LOG.debug(pojoClassResourceUtils.getClaszName() + " for uri '" + objectFromJSON.getUri()
					+ "' does not exist, i.e., it cannot be updated");

			return null;
		}

		AdvancedDMPResource.LOG.debug("got " + pojoClassResourceUtils.getClaszName() + " with uri '" + objectFromJSON.getUri() + "'");
		AdvancedDMPResource.LOG.trace(" = '" + ToStringBuilder.reflectionToString(object) + "'");

		return object;
	}
}
