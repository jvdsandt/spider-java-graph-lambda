package com.cloudctrl.spider.graph;

import com.cloudctrl.spider.core.MethodsRepo;
import com.cloudctrl.spider.core.SpiderClassesRepo;
import graphql.schema.DataFetcher;

public class SpiderDataFetchers {

    private MethodsRepo methodsRepo;

    private SpiderClassesRepo classesRepo;

    public SpiderDataFetchers(MethodsRepo methodsRepo, SpiderClassesRepo classesRepo) {
        this.methodsRepo = methodsRepo;
        this.classesRepo = classesRepo;
    }

    public DataFetcher getMethodByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String id = dataFetchingEnvironment.getArgument("id");
            return methodsRepo.findById(Long.parseLong(id));
        };
    }

    public DataFetcher getMethods() {
        return dataFetchingEnvironment -> {
            String selector = dataFetchingEnvironment.getArgument("selector");
            int limit = dataFetchingEnvironment.getArgument("limit");
            int offset = dataFetchingEnvironment.getArgument("offset");
            return methodsRepo.findAllLike(selector, limit, offset);
        };
    }

    public DataFetcher getClassByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String id = dataFetchingEnvironment.getArgument("id");
            return classesRepo.findById(Long.parseLong(id));
        };
    }

    public DataFetcher getClasses() {
        return dataFetchingEnvironment -> {
            String name = dataFetchingEnvironment.getArgument("name");
            int limit = dataFetchingEnvironment.getArgument("limit");
            int offset = dataFetchingEnvironment.getArgument("offset");
            return classesRepo.findAllLike(name, limit, offset);
        };
    }

    public DataFetcher getClassMethods() {
        return env -> {
            String name = env.getArgument("name");
            int limit = env.getArgument("limit");
            int offset = env.getArgument("offset");
            return classesRepo.findAllLike(name, limit, offset);
        };
    }

}
