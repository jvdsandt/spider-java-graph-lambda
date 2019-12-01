package com.cloudctrl.spider.core;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpiderClass {

    public final long id;
    public final int type;
    public final String name;
    public final String comment;

    public SpiderClass(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.type = rs.getInt("type");
        this.name = rs.getString("name");
        this.comment = rs.getString("comment");
    }

}
