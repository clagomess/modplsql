package com.github.clagomess.modplsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.modplsql.dto.ConfigDto;
import com.github.clagomess.modplsql.jdbc.Database;
import com.github.clagomess.modplsql.server.ModPlsqlServer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Main {
    public static void main(String[] args) throws Throwable {
        // init config
        ObjectMapper mapper = new ObjectMapper();
        ConfigDto dto = mapper.readValue(new File("config.json"), ConfigDto.class);

        // init services
        Database.init(dto);
        ModPlsqlServer.run();

        log.info("Running at: http://localhost:8000");
    }
}
