package org.dswarm.converter.export;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.dswarm.converter.DMPConverterException;
import org.dswarm.graph.json.Predicate;
import org.dswarm.graph.json.Statement;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tgaengler on 07.03.16.
 */
public class QuadRDFExporter extends RDFExporter<Quad> {

	private final String dataModelURI;
	private final Node dataModelNode;

	public QuadRDFExporter(final MediaType mediaTypeArg, final String dataModelURIArg) {

		super(mediaTypeArg);

		dataModelURI = dataModelURIArg;
		dataModelNode = NodeFactory.createURI(dataModelURI);
	}

	@Override
	protected Quad generateTuple(final Statement statement,
	                             final ConcurrentHashMap<String, Node> resourceNodeCache,
	                             final ConcurrentHashMap<Long, Node> bnodeCache,
	                             final ConcurrentHashMap<String, Node> predicateCache) throws DMPConverterException {

		return generateQuad(statement, dataModelNode, resourceNodeCache, bnodeCache, predicateCache);
	}

	@Override
	protected void writeTuple(final Quad tuple,
	                          final StreamRDF writer) {

		writer.quad(tuple);
	}

	private static Quad generateQuad(final Statement statement,
	                                 final Node dataModelNode,
	                                 final ConcurrentHashMap<String, Node> resourceNodeCache,
	                                 final ConcurrentHashMap<Long, Node> bnodeCache,
	                                 final ConcurrentHashMap<String, Node> predicateCache) throws DMPConverterException {

		final org.dswarm.graph.json.Node gdmSubject = statement.getSubject();
		final Predicate gdmPredicate = statement.getPredicate();
		final org.dswarm.graph.json.Node gdmObject = statement.getObject();

		final Node subject = generateSubjectNode(gdmSubject, resourceNodeCache, bnodeCache);
		final Node predicate = generatePredicate(gdmPredicate, predicateCache);
		final Node object = generateObjectNode(gdmObject, resourceNodeCache, bnodeCache);

		return Quad.create(dataModelNode, subject, predicate, object);
	}
}
