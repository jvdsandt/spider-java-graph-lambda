package com.cloudctrl.spider.lambda;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.cloudctrl.spider.core.MockRepos;
import com.cloudctrl.spider.graph.SpiderGraph;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class HandlerTest {

    private final Gson gson = new Gson();

    private MockRepos mockRepos = new MockRepos();

    private Context mockContext = Mockito.mock(Context.class);

    private Handler handler;

    @Before
    public void init() {
        var graph = new SpiderGraph(mockRepos.createDataFetcher());

        this.handler = new Handler();
        this.handler.setGraphQL(graph.getGraphQL());
    }

    @Test
    public void testGET() throws IOException {
        Map<String, Object> input = Map.of(
                "httpMethod", "GET",
                "queryStringParameters", Map.of(
                        "query", "{ methodById(id: 100) {id, selector} }"));

        InputStream is = new ByteArrayInputStream(gson.toJson(input, Map.class).getBytes(StandardCharsets.UTF_8));
        var os = new ByteArrayOutputStream(1000);

        handler.handleRequest(is, os, mockContext);

        var result = gson.fromJson(new StringReader(new String(os.toByteArray(), StandardCharsets.UTF_8)), Map.class);

        assertEquals(200, ((Number) result.get("statusCode")).intValue());
    }
}
