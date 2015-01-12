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
package org.dswarm.persistence;

import java.util.Properties;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.typesafe.config.Config;

public class JpaHibernateModule extends AbstractModule {

	private final String uri;
	private final String username;
	private final String password;
	private final boolean isLogSql;
	private final String jpaUnit;
	private final String graphDBPath;

	public JpaHibernateModule(final Injector configInjector) {
		Preconditions.checkNotNull(configInjector);
		final Config config = configInjector.getInstance(Config.class);
		final Config metadataConfig = config.getConfig("dswarm.db.metadata");
		final Config datahubConfig = config.getConfig("dswarm.db.datahub");

		uri = metadataConfig.getString("uri");
		username = metadataConfig.getString("username");
		password = metadataConfig.getString("password");
		isLogSql = metadataConfig.getBoolean("log-sql");
		jpaUnit = metadataConfig.getString("jpa-unit");
		graphDBPath = datahubConfig.getString("path");
	}

	@Override
	protected void configure() {
		install(persistModule());
	}

	private Properties persistenceConfig() {

		final Properties properties = new Properties();

		/*properties.setProperty("javax.persistence.jdbc.url", uri);
		properties.setProperty("javax.persistence.jdbc.user", username);
		properties.setProperty("javax.persistence.jdbc.password", password);

		// TODO: load more values from config, e.g. connection pool settings
		properties.setProperty("hibernate.c3p0.acquireRetryAttempts", "3");
		properties.setProperty("hibernate.c3p0.acquireRetryDelay", "100");
		properties.setProperty("hibernate.c3p0.breakAfterAcquireFailure", "false");
		properties.setProperty("hibernate.c3p0.checkoutTimeout", "0");
		properties.setProperty("hibernate.c3p0.idle_test_period", "30");
		properties.setProperty("hibernate.c3p0.max_size", "20");
		properties.setProperty("hibernate.c3p0.max_statements", "0");
		properties.setProperty("hibernate.c3p0.maxConnectionAge", "500");
		properties.setProperty("hibernate.c3p0.maxIdleTimeExcessConnections", "45");
		properties.setProperty("hibernate.c3p0.maxStatementsPerConnection", "20");
		properties.setProperty("hibernate.c3p0.min_size", "5");
		properties.setProperty("hibernate.c3p0.numHelperThreads", "1");
		properties.setProperty("hibernate.c3p0.preferredTestQuery", "SELECT 1");
		properties.setProperty("hibernate.c3p0.testConnectionOnCheckout", "true");
		properties.setProperty("hibernate.c3p0.timeout", "10");
		properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.internal.NoCachingRegionFactory");
		properties.setProperty("hibernate.cache.use_query_cache", "false");
		properties.setProperty("hibernate.cache.use_second_level_cache", "false");
		properties.setProperty("hibernate.connection.autoReconnect", "true");
		properties.setProperty("hibernate.connection.autoReconnectForPools", "true");
		properties.setProperty("hibernate.connection.is-connection-validation-required", "true");
		properties.setProperty("hibernate.connection.provider_class", "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider");
		properties.setProperty("hibernate.connection.shutdown", "true");
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
		properties.setProperty("hibernate.discriminator.ignore_explicit_for_joined", "true");
		properties.setProperty("hibernate.show_sql", String.valueOf(isLogSql));
		properties.setProperty("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		properties.setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
		properties.setProperty("hibernate.ejb.entitymanager_factory_name", "DMPAppFactory"); */

		/* < <property name="kundera.datastore.file.path" value="target/imdb.db" />
            <property name="kundera.dialect" value="neo4j" />
            <property name="kundera.client.lookup.class"
                value="com.impetus.client.neo4j.Neo4JClientFactory" />
            <property name="kundera.cache.provider.class"
                value="com.impetus.kundera.cache.ehcache.EhCacheProvider" />
            <property name="kundera.cache.config.resource" value="/ehcache-test.xml" />
            <property name="kundera.client.property" value="kunderaNeo4JTest.xml"/>
            <property name="kundera.transaction.resource.class" value="com.impetus.client.neo4j.Neo4JTransaction" />
    */

		properties.setProperty("kundera.datastore.file.path", "target/graph.db");
		properties.setProperty("kundera.dialect", "neo4j");
		properties.setProperty("kundera.client.lookup.class", "com.impetus.client.neo4j.Neo4JClientFactory");
		properties.setProperty("kundera.transaction.resource.class", "com.impetus.client.neo4j.Neo4JTransaction");
		properties.setProperty("kundera.ddl.auto.prepare", "update");

		return properties;
	}

	private Module persistModule() {
		return new JpaPersistModule(jpaUnit).properties(persistenceConfig());
	}
}
