package com.github.clagomess.modplsql.server;

import com.github.clagomess.modplsql.jdbc.Database;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Path("/")
public class ModPlsqlController {
    @GET
    @Path("status")
    @Produces(MediaType.TEXT_PLAIN)
    public String status(){
        return "OK";
    }

    @GET
    @Path("/!{path}")
    @Produces("text/html; charset=windows-1252")
    public String handleGet(
            @PathParam("path") String path,
            @Context Request request
    ){
        Map<String, String> param = new HashMap<>();

        try {
            return Database.runPl(path, param);
        }catch (Throwable e){
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @POST
    @Path("/!{path}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("text/html; charset=windows-1252")
    public String handlePost(
            @PathParam("path") String path,
            MultivaluedMap<String, String> form
    ){
        Map<String, String> param = new HashMap<>();

        for(Map.Entry<String, List<String>> entry : form.entrySet()) {
            param.put(entry.getKey(), entry.getValue().get(0));
        }

        try {
            return Database.runPl(path, param);
        }catch (Throwable e){
            log.error(e.getMessage());
            return e.getMessage();
        }
    }
}
