package com.cloudctrl.spider.core;

import java.util.Arrays;

import com.cloudctrl.spider.graph.SpiderDataFetchers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockRepos {

    public final MethodsRepo methodsRepo;

    public final SpiderClassesRepo classesRepo;

    public MockRepos() {
        this.methodsRepo = mock(MethodsRepo.class);
        this.classesRepo = mock(SpiderClassesRepo.class);
        setup();
    }

    public SpiderDataFetchers createDataFetcher() {
        return new SpiderDataFetchers(methodsRepo, classesRepo);
    }

    public void setup() {
        var m1 = new Method(100L, "test", "test-source");
        var m2 = new Method(100L, "twoHundred", "twoHundred-source");

        when(methodsRepo.findById(100L)).thenReturn(m1);
        when(methodsRepo.findById(200L)).thenReturn(m2);
        when(methodsRepo.findAllLike(anyString(), anyInt(), anyInt())).thenReturn(Arrays.asList(m1, m2));
    }

}
