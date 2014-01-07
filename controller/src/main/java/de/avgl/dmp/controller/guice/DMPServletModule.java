package de.avgl.dmp.controller.guice;

import java.util.Properties;

import com.google.inject.name.Names;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;

import de.avgl.dmp.controller.doc.SwaggerConfig;
import de.avgl.dmp.controller.servlet.filter.MetricsFilter;

import static de.avgl.dmp.controller.utils.DMPControllerUtils.loadProperties;

/**
 * The Guice configuration of the servlet of the backend API. Mainly, servlets, filters and configuration properties are defined here.
 *
 * @author phorn
 */
public class DMPServletModule extends ServletModule {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureServlets() {

		// provides and handles the entity manager of the application
		install(new JpaPersistModule("DMPApp"));

		filter("/*").through(PersistFilter.class);

		bind(String.class).annotatedWith(Names.named("ApiVersion")).toInstance("1.0.1");

		final Properties properties = loadProperties();

		bind(String.class).annotatedWith(Names.named("ApiBaseUrl")).toInstance(properties.getProperty("swagger.base_url", "http://localhost:8087/dmp"));

		serve("/api-docs").with(SwaggerConfig.class);
		filter("/*").through(MetricsFilter.class);
	}
}
