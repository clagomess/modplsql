package com.github.clagomess.modplsql.server;

import com.github.clagomess.modplsql.dto.ConfigDto;
import com.github.clagomess.modplsql.jdbc.Database;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.ClientBuilder;
import java.sql.SQLException;

@Slf4j
public class ModPlsqlControllerTest {
    private static final String endpoint = "http://localhost:8000";

    @BeforeAll
    static void init() throws SQLException, InterruptedException {
        ConfigDto dto = new ConfigDto();

        dto.setDbUrl("jdbc:oracle:thin:@localhost:1521:ORCLCDB");
        dto.setDbUser("MODPLSQL");
        dto.setDbPass("010203");
        dto.setIndexPage("MODPLSQL.HELLO_WORLD");

        Database.init(dto);
        ModPlsqlServer.run();

        Thread.sleep(3000L);
    }

    @Test
    public void status() {
        String response = ClientBuilder.newClient()
                .target(endpoint + "/status")
                .request()
                .get(String.class);

        log.info("{}", response);
        Assertions.assertEquals("OK", response);
    }

    @Test
    public void index() {
        String response = ClientBuilder.newClient()
                .target(endpoint + "/")
                .request()
                .get(String.class);

        log.info("{}", response);
        MatcherAssert.assertThat(response, CoreMatchers.containsString("Hello World"));
    }
}
