package com.cloudctrl.spider.graph;

import com.cloudctrl.spider.core.MockRepos;
import graphql.ExecutionResult;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SpiderGraphTest {

    private MockRepos mockRepos = new MockRepos();

    @Test
    public void test_methodById() {
        SpiderGraph graph = new SpiderGraph(mockRepos.createDataFetcher());

        ExecutionResult result = graph.getGraphQL().execute("{ methodById(id: 100) {id, selector} }");

        assertTrue(result.getErrors().isEmpty());
        verify(mockRepos.methodsRepo).findById(100L);
    }

    @Test
    public void test_methods() {
        SpiderGraph graph = new SpiderGraph(mockRepos.createDataFetcher());

        ExecutionResult result = graph.getGraphQL().execute("{ methods(selector:\"fill\") {id, selector} }");

        assertTrue(result.getErrors().isEmpty());
        verify(mockRepos.methodsRepo).findAllLike("fill", 100, 0);
    }

    @Test
    public void test_methodById_without_arg() {
        SpiderGraph graph = new SpiderGraph(mockRepos.createDataFetcher());

        ExecutionResult result = graph.getGraphQL().execute("{  methodById {id, selector}");

        assertFalse(result.getErrors().isEmpty());

        verify(mockRepos.methodsRepo, never()).findById(100L);
    }
}
