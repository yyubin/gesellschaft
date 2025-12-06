package org.yyubin.gesellschaftneo4j.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories(basePackages = "org.yyubin.gesellschaftneo4j.repository")
@EnableTransactionManagement
public class Neo4jConfig {

}
