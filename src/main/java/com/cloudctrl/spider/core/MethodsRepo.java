package com.cloudctrl.spider.core;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class MethodsRepo {

    private NamedParameterJdbcTemplate template;

    public MethodsRepo(DataSource ds) {
        super();
        this.template = new NamedParameterJdbcTemplate(ds);
    }

    public Method findById(long id) {

        return template.queryForObject(
                "select id, selector, source from methods where id = :id",
                Map.of("id", id),
                (rs, rowNum) -> new Method(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3)));
    }

    public List<Method> findAllLike(String selector, int limit, int offset) {
        if (limit < 1 || limit > 10000) {
            throw new IllegalArgumentException("Invalid limit");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Invalid offset");
        }
        String sel = (selector == null ? "" : selector) + "%";
        return template.query(
                "select id, selector, source from methods " +
                        "where selector like :sel " +
                        "order by selector, id " +
                        "limit :limit offset :offset",
                Map.of("sel", sel,
                        "limit", limit,
                        "offset", offset),
                (rs, rowNum) -> new Method(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3)));
    }
}