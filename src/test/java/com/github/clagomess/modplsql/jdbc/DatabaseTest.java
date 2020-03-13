package com.github.clagomess.modplsql.jdbc;

import com.github.clagomess.modplsql.dto.ConfigDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
public class DatabaseTest {
    @BeforeAll
    static void init() throws SQLException {
        ConfigDto dto = new ConfigDto();

        dto.setDbUrl("jdbc:oracle:thin:@localhost:1521:ORCLCDB");
        dto.setDbUser("MODPLSQL");
        dto.setDbPass("010203");
        dto.setIndexPage("MODPLSQL.HELLO_WORLD");

        Database.init(dto);
    }

    @Test
    public void getProcedureArguments() throws SQLException {
        val list = Database.getProcedureArguments("MODPLSQL.HELLO_WORLD");

        log.info("{}", list);

        Assertions.assertTrue(list.contains("NUM_ENTRIES"));
        Assertions.assertTrue(list.contains("NAME_ARRAY"));
        Assertions.assertTrue(list.contains("VALUE_ARRAY"));
        Assertions.assertTrue(list.contains("RESERVED"));
    }

    @Test
    public void getProcedureArguments_noparam() throws SQLException {
        val list = Database.getProcedureArguments("MODPLSQL.HTTP_NO_PARAM");

        log.info("{}", list);

        Assertions.assertTrue(list.isEmpty());
    }
}
