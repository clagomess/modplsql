package com.github.clagomess.modplsql.jdbc;

import com.github.clagomess.modplsql.dto.ConfigDto;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class Database {
    public static ConfigDto configDto;
    private static Connection conn;
    private static Statement stmt;

    public static void init(ConfigDto dto) throws SQLException {
        configDto = dto;
        conn = DriverManager.getConnection(dto.getDbUrl(), dto.getDbUser(), dto.getDbPass());
        stmt = conn.createStatement();

        // init OWA
        stmt.executeUpdate("declare\n" +
                "    nm  owa.vc_arr;\n" +
                "    vl  owa.vc_arr;\n" +
                "begin\n" +
                "    nm(1) := 'SERVER_PORT';\n" +
                "    vl(1) := '8000';\n" +
                "    owa.init_cgi_env( 1, nm, vl );\n" +
                "end;");

        // init DBMS
        stmt.executeUpdate("begin dbms_output.enable(); end;");
    }

    public static String runPl(String plName, Map<String, String> param) throws SQLException {
        int idx = 1;

        // get arglist
        List<String> arguments = getProcedureArguments(plName);

        // fill parans
        param.putAll(configDto.getParamsAsMap());

        StringBuilder sql = new StringBuilder();

        if(!arguments.isEmpty()){
            sql.append("DECLARE\n");

            if(arguments.contains("NUM_ENTRIES")){
                sql.append("  NUM_ENTRIES NUMBER;\n");
            }

            if(arguments.contains("NAME_ARRAY")){
                sql.append("  NAME_ARRAY OWA.VC_ARR;\n");
            }

            if(arguments.contains("VALUE_ARRAY")){
                sql.append("  VALUE_ARRAY OWA.VC_ARR;\n");
            }

            if(arguments.contains("RESERVED")){
                sql.append("  RESERVED OWA.VC_ARR;\n");
            }

            sql.append("BEGIN\n");

            if(arguments.contains("NUM_ENTRIES")){
                sql.append(String.format("  NUM_ENTRIES := %s;\n", param.size()));
            }

            for (Map.Entry<String, String> entry : param.entrySet()) {
                if(arguments.contains("NAME_ARRAY")) {
                    sql.append(String.format("  NAME_ARRAY(%s) := ?; -- '%s'\n", idx, entry.getKey()));
                }

                if(arguments.contains("VALUE_ARRAY")) {
                    sql.append(String.format("  VALUE_ARRAY(%s) := ?; -- '%s'\n", idx, escape(entry.getValue())));
                }

                idx++;
            }
        }else{
            sql.append("BEGIN\n");
        }

        List<String> argRun = new ArrayList<>();
        List<String> reseverd = Arrays.asList("NUM_ENTRIES", "NAME_ARRAY", "VALUE_ARRAY", "RESERVED");

        reseverd.stream().filter(arguments::contains)
            .forEach(item -> argRun.add(String.format("%s => %s", item, item)));

        arguments.forEach(item -> {
            if(!reseverd.contains(item)) {
                String value = param.getOrDefault(item, "");
                argRun.add(String.format("%s => '%s'", item, escape(value)));
                param.remove(item);
            }
        });

        sql.append(plName);
        sql.append("(\n");
        sql.append(String.join(",\n", argRun));
        sql.append(");\n");
        sql.append("END;");

        log.info("QUERY:\n{}", sql.toString());

        PreparedStatement pstmt = conn.prepareStatement(sql.toString());
        idx = 1;
        for (Map.Entry<String, String> entry : param.entrySet()) {
            pstmt.setString(idx, entry.getKey());
            idx++;
            pstmt.setString(idx, entry.getValue());
            idx++;
        }

        pstmt.execute();

        return getResult();
    }

    protected static List<String> getProcedureArguments(String plName) throws SQLException {
        String owner = plName.split("\\.")[0];
        String procedure = plName.split("\\.")[1];

        String sql = "SELECT ARGUMENT_NAME FROM ALL_ARGUMENTS\n" +
                "WHERE OWNER = ?\n" +
                "AND OBJECT_NAME = ?\n" +
                "AND ARGUMENT_NAME IS NOT NULL";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, owner);
        pstmt.setString(2, procedure);
        ResultSet result = pstmt.executeQuery();

        List<String> arguments = new ArrayList<>();

        while (result.next()){
            arguments.add(result.getString("ARGUMENT_NAME"));
        }

        return arguments;
    }

    private static String escape(String vl){
        if(vl == null || vl.trim().length() == 0){
            return vl;
        }

        return vl.replaceAll("'", "''");
    }

    private static String getResult() throws SQLException {
        StringBuilder result = new StringBuilder();

        try {
            // get buffer
            String buff;

            while ((buff = getChunkResult()) != null) {
                result.append(buff);
            }
        }catch (SQLException e){
            log.error(e.getMessage());
        }finally {
            printDbmsOutput();
        }

        return result.toString();
    }

    private static String getChunkResult() throws SQLException {
        String queryResult = "declare \n" +
                " nlns number;\n" +
                " buf_t varchar2(32767);\n" +
                " lines htp.htbuf_arr;\n" +
                "begin\n" +
                "  nlns := ?;\n" +
                "  OWA.GET_PAGE(lines, nlns);\n" +
                "  if (nlns < 1) then\n" +
                "   buf_t := null;\n" +
                "  else \n" +
                "   for i in 1..nlns loop\n" +
                "     buf_t:=buf_t||lines(i);\n" +
                "   end loop;\n" +
                "  end if;\n" +
                "  ? := buf_t;\n" +
                "  ? := nlns;\n" +
                "end;";
        CallableStatement cs = conn.prepareCall(queryResult);
        cs.setInt(1, 127);
        cs.registerOutParameter(2, Types.VARCHAR);
        cs.registerOutParameter(3, Types.BIGINT);
        cs.execute();

        String result = cs.getString(2);

        cs.close();

        return result;
    }

    private static void printDbmsOutput() throws SQLException {
        String query = "declare\n" +
                "  num integer := 1000;\n" +
                "begin\n" +
                "  dbms_output.get_lines(?, num);\n" +
                "end;";
        CallableStatement cs = conn.prepareCall(query);
        cs.registerOutParameter(1, Types.ARRAY, "DBMSOUTPUT_LINESARRAY");
        cs.execute();

        String[] listLog = (String[]) cs.getArray(1).getArray();
        cs.close();

        for(String item : listLog){
            if(item != null) {
                log.info("DBMSOUTPUT: {}", item);
            }
        }
    }
}
