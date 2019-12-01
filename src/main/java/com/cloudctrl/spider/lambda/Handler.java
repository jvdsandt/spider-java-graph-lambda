package com.cloudctrl.spider.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.cloudctrl.spider.graph.GraphQLInvocationData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler implements RequestStreamHandler {

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private static final ZonedDateTime START_TIME = ZonedDateTime.now();

    private final Gson gson = new Gson();
    private final JsonParser parser = new JsonParser();

    private GraphQL graphQL;

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        JsonObject event = parser.parse(reader).getAsJsonObject();
        String httpMethod = event.get("httpMethod").getAsString();

        if ("GET".equals(httpMethod)) {
            JsonObject params = event.get("queryStringParameters").getAsJsonObject();
            handleGet(params, output, context);
        } else if ("POST".equals(httpMethod)) {
            var body = event.get("body").getAsString();
            JsonObject bodyJson = parser.parse(body).getAsJsonObject();
            handlePost(bodyJson, output, context);
        } else {
            respondError("Unsupported method", output);
        }
    }

    private void handleGet(JsonObject params, OutputStream output, Context context) throws IOException {
        var invocationData = gson.fromJson(params, GraphQLInvocationData.class);
        handle(invocationData, output, context);
    }

    private void handlePost(JsonObject params, OutputStream output, Context context) throws IOException {
        var invocationData = gson.fromJson(params, GraphQLInvocationData.class);
        handle(invocationData, output, context);
    }

    private void handle(GraphQLInvocationData invocationData, OutputStream output, Context context) throws IOException {
        if (this.graphQL == null) {
            this.setup(context);
        }
        ExecutionInput input = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables())
                .build();
        ExecutionResult result = graphQL.execute(input);
        respond(200, gson.toJson(result.toSpecification()), output);
    }

    private void respondError(String msg, OutputStream outputStream) throws IOException {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("message", msg);

        respond(400, gson.toJson(responseJson), outputStream);
    }

    private void respondException(Exception ex, OutputStream outputStream) throws IOException {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("message", ex.getMessage());

        respond(400, gson.toJson(responseJson), outputStream);
    }

    private void respond(int statusCode, String body, OutputStream outputStream) throws IOException {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("isBase64Encoded", false);
        responseJson.addProperty("statusCode", statusCode);
        responseJson.addProperty("body", body);

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        gson.toJson(responseJson, writer);
        writer.close();
    }

    void setGraphQL(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    void setup(Context context) throws IOException {
        Setup setup = new Setup(context);
        this.graphQL = setup.getGraphQL();
    }
}
