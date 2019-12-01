package com.cloudctrl.spider.core;

public class Method {

    public final long id;
    public final String selector;
    public final String source;

    public Method(long id, String selector, String source) {
        this.id = id;
        this.selector = selector;
        this.source = source;
    }
}
