package de.avgl.dmp.controller.resources.utils;

import javax.inject.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.avgl.dmp.persistence.model.BasicDMPJPAObject;
import de.avgl.dmp.persistence.service.BasicDMPJPAService;

/**
 * @author tgaengler
 * @param <POJOCLASSPERSISTENCESERVICE>
 * @param <POJOCLASS>
 * @param <POJOCLASSIDTYPE>
 */
public abstract class BasicDMPResourceUtils<POJOCLASSPERSISTENCESERVICE extends BasicDMPJPAService<POJOCLASS>, POJOCLASS extends BasicDMPJPAObject>
		extends BasicIDResourceUtils<POJOCLASSPERSISTENCESERVICE, POJOCLASS> {

	private static final org.apache.log4j.Logger	LOG	= org.apache.log4j.Logger.getLogger(BasicDMPResourceUtils.class);

	public BasicDMPResourceUtils(final Class<POJOCLASS> pojoClassArg, final Provider<POJOCLASSPERSISTENCESERVICE> persistenceServiceProviderArg,
			final Provider<ObjectMapper> objectMapperProviderArg) {

		super(pojoClassArg, persistenceServiceProviderArg, objectMapperProviderArg);
	}
}
