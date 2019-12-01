package com.cloudctrl.spider.core;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class SpiderClassesRepo {

    private NamedParameterJdbcTemplate template;

    public SpiderClassesRepo(DataSource ds) {
        super();
        this.template = new NamedParameterJdbcTemplate(ds);
    }

    public SpiderClass findById(long id) {

        return template.queryForObject(
                "select id, type, class_type, name, comment from classes where id = :id",
                Map.of("id", id),
                (rs, rowNum) -> new SpiderClass(rs));
    }

    public List<SpiderClass> findAllLike(String name, int limit, int offset) {
        if (limit < 1 || limit > 10000) {
            throw new IllegalArgumentException("Invalid limit");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Invalid offset");
        }
        String theName = (name == null ? "" : name) + "%";
        return template.query(
                "select id, type, class_type, name, comment from classes " +
                        "where name like :name " +
                        "order by name, id " +
                        "limit :limit offset :offset",
                Map.of("name", name,
                        "limit", limit,
                        "offset", offset),
                (rs, rowNum) -> new SpiderClass(rs));
    }
}