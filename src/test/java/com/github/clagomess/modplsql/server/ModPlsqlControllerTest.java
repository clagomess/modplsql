package com.github.clagomess.modplsql.server;

import com.github.clagomess.modplsql.dto.ConfigDto;
import com.github.clagomess.modplsql.jdbc.Database;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    @Test
    public void httpGetParam(){
        String p1 = RandomStringUtils.randomAlphanumeric(10);
        String p2 = RandomStringUtils.randomAlphanumeric(10);

        String response = ClientBuilder.newClient()
                .target(endpoint + String.format("/!MODPLSQL.HTTP_PARAM?p1=%s&p2=%s", p1, p2))
                .request()
                .get(String.class);

        log.info("{}", response);
        MatcherAssert.assertThat(response, CoreMatchers.containsString(String.format("P1 => %s", p1)));
        MatcherAssert.assertThat(response, CoreMatchers.containsString(String.format("P2 => %s", p2)));
    }

    @Test
    public void httpPostParam(){
        String p1 = RandomStringUtils.randomAlphanumeric(10);
        String p2 = RandomStringUtils.randomAlphanumeric(10);

        Form form = new Form();
        form.param("p1", p1);
        form.param("p2", p2);

        Entity<Form> entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        String response = ClientBuilder.newClient()
                .target(endpoint + String.format("/!MODPLSQL.HTTP_PARAM?p1=%s&p2=%s", p1, p2))
                .request()
                .post(entity, String.class);

        log.info("{}", response);
        MatcherAssert.assertThat(response, CoreMatchers.containsString(String.format("P1 => %s", p1)));
        MatcherAssert.assertThat(response, CoreMatchers.containsString(String.format("P2 => %s", p2)));
    }

    @Test
    public void setCookie(){
        String cookie = RandomStringUtils.randomAlphanumeric(10);

        String response = ClientBuilder.newClient()
                .target(endpoint + "/!MODPLSQL.SET_COOKIE")
                .request()
                .cookie("MODPLSQL_SESSION", cookie)
                .get(String.class);

        log.info("{}", response);
        MatcherAssert.assertThat(response, CoreMatchers.containsString(String.format("COOKIE => %s", cookie)));
    }

    @Test
    public void getCookie(){
        Response response = ClientBuilder.newClient()
                .target(endpoint + "/!MODPLSQL.GET_COOKIE")
                .request()
                .get();

        log.info("{}", response);
        Assertions.assertTrue(response.getCookies().size() > 0);
    }

    @Test
    public void httpNoParam(){
        String response = ClientBuilder.newClient()
                .target(endpoint + "/!MODPLSQL.HTTP_NO_PARAM")
                .request()
                .get(String.class);

        log.info("{}", response);
        MatcherAssert.assertThat(response, CoreMatchers.containsString("HTTP_NO_PARAM => OK"));
    }

    @Test
    public void httpNamedParam(){
        String p_vl1 = RandomStringUtils.randomAlphanumeric(10);

        String response = ClientBuilder.newClient()
                .target(endpoint + "/!MODPLSQL.HTTP_NAMED_PARAM?P_VL1=" + p_vl1)
                .request()
                .get(String.class);

        log.info("{}", response);
        MatcherAssert.assertThat(response, CoreMatchers.containsString("P_VL1 => " + p_vl1));
    }

    @Test
    public void httpNamedParam_lowercase(){
        String p_vl1 = RandomStringUtils.randomAlphanumeric(10);

        String response = ClientBuilder.newClient()
                .target(endpoint + "/!MODPLSQL.HTTP_NAMED_PARAM?p_vl1=" + p_vl1)
                .request()
                .get(String.class);

        log.info("{}", response);
        MatcherAssert.assertThat(response, CoreMatchers.containsString("P_VL1 => " + p_vl1));
    }
}
