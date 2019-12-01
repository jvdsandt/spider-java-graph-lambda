package com.cloudctrl.spider.lambda;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import com.amazonaws.services.lambda.runtime.Context;
import com.cloudctrl.spider.core.MethodsRepo;
import com.cloudctrl.spider.core.SpiderClassesRepo;
import com.cloudctrl.spider.graph.SpiderDataFetchers;
import com.cloudctrl.spider.graph.SpiderGraph;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class Setup {

    private static final Logger log = LoggerFactory.getLogger(Setup.class);

    private Connection connection;

    private GraphQL graphQL;

    public Setup(Context context) throws IOException {
        var dburl = System.getenv("DB_URL");
        if (dburl == null || dburl.trim().length() == 0) {
            log.info("DB_URL environment variable not set.");
            throw new IOException("DB_URL environment variable not set.");
        }
        long start = System.currentTimeMillis();
        try {
            this.connection = DriverManager.getConnection(dburl);
            long time = System.currentTimeMillis() - start;
            log.info("db connection setup in " + time + "ms.");
        } catch (Exception ex) {
            log.error("db connection failed", ex);
            throw new IOException("db connection failed", ex);
        }
        var ds = new SingleConnectionDataSource(this.connection,true);
        var methodRepo = new MethodsRepo(ds);
        var classesRepo = new SpiderClassesRepo(ds);
        var dataFetchers = new SpiderDataFetchers(methodRepo, classesRepo);
        var spiderGraph = new SpiderGraph(dataFetchers);
        this.graphQL = spiderGraph.getGraphQL();
    }

    public Connection getConnection() {
        return connection;
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }
}
