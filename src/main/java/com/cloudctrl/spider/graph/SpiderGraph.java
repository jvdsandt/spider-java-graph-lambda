package com.cloudctrl.spider.graph;

import java.io.File;
import java.net.URL;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

public class SpiderGraph {

    private SpiderDataFetchers dataFetchers;

    private GraphQL graphQL;

    public SpiderGraph(SpiderDataFetchers dataFetchers) {
        super();
        this.dataFetchers = dataFetchers;
        init();
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }

    private void init() {
        GraphQLSchema graphQLSchema = buildSchema();
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema() {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(getSchemaFile());
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("methodById", dataFetchers.getMethodByIdDataFetcher()))
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("methods", dataFetchers.getMethods()))
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("classById", dataFetchers.getClassByIdDataFetcher()))
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("classes", dataFetchers.getClasses()))

                .type(TypeRuntimeWiring.newTypeWiring("Class")
                        .dataFetcher("methods", dataFetchers.getClassMethods()))
                .build();
    }

    private File getSchemaFile() {
        URL url = getClass().getResource("/schema.graphqls");
        return new File(url.getFile());
    }

}
