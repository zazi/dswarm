package de.avgl.dmp.controller.resources.schema;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.servlet.RequestScoped;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import de.avgl.dmp.controller.DMPControllerException;
import de.avgl.dmp.controller.resources.AdvancedDMPResource;
import de.avgl.dmp.controller.resources.schema.utils.ClaszesResourceUtils;
import de.avgl.dmp.controller.resources.utils.ResourceUtilsFactory;
import de.avgl.dmp.controller.status.DMPStatus;
import de.avgl.dmp.persistence.model.schema.Clasz;
import de.avgl.dmp.persistence.service.schema.ClaszService;

/**
 * A resource (controller service) for {@link Clasz}es.
 * 
 * @author tgaengler
 */
@RequestScoped
@Api(value = "/classes", description = "Operations about classes.")
@Path("classes")
public class ClaszesResource extends AdvancedDMPResource<ClaszesResourceUtils, ClaszService, Clasz> {

	private static final org.apache.log4j.Logger	LOG	= org.apache.log4j.Logger.getLogger(ClaszesResource.class);

	/**
	 * Creates a new resource (controller service) for {@link Clasz}s with the provider of the class persistence service, the
	 * object mapper and metrics registry.
	 * 
	 * @param claszServiceProviderArg the class persistence service provider
	 * @param objectMapperArg an object mapper
	 * @param dmpStatusArg a metrics registry
	 */
	@Inject
	public ClaszesResource(final ResourceUtilsFactory utilsFactory, final DMPStatus dmpStatusArg) throws DMPControllerException {

		super(utilsFactory.reset().get(ClaszesResourceUtils.class), dmpStatusArg);
	}

	/**
	 * This endpoint returns a class as JSON representation for the provided class identifier.<br/>
	 * 
	 * @param id a class identifier
	 * @return a JSON representation of a class
	 */
	@ApiOperation(value = "get the class that matches the given id", notes = "Returns the Clasz object that matches the given id.")
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getObject(@ApiParam(value = "class identifier", required = true) @PathParam("id") final Long id) throws DMPControllerException {

		return super.getObject(id);
	}

	/**
	 * This endpoint consumes a class as JSON representation and persists this class in the database.
	 * 
	 * @param jsonObjectString a JSON representation of one class
	 * @return the persisted class as JSON representation
	 * @throws DMPControllerException
	 */
	@ApiOperation(value = "create a new class", notes = "Returns a new Clasz object.", response = Clasz.class)
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response createObject(@ApiParam(value = "class (as JSON)", required = true) final String jsonObjectString) throws DMPControllerException {

		return super.createObject(jsonObjectString);
	}

	/**
	 * This endpoint returns a list of all classes as JSON representation.
	 * 
	 * @return a list of all classes as JSON representation
	 * @throws DMPControllerException
	 */
	@ApiOperation(value = "get all classes", notes = "Returns a list of Clasz objects.")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getObjects() throws DMPControllerException {

		return super.getObjects();
	}
	
	// TODO: add put
}