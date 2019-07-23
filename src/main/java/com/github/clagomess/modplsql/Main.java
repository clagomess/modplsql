package com.github.clagomess.modplsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.modplsql.dto.ConfigDto;
import com.github.clagomess.modplsql.jdbc.Database;
import com.github.clagomess.modplsql.server.ModPlsqlServer;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Throwable {
        // init config
        ObjectMapper mapper = new ObjectMapper();
        ConfigDto dto = mapper.readValue(new File("config.json"), ConfigDto.class);

        // init services
        Database.init(dto);
        ModPlsqlServer.run();
    }
}
