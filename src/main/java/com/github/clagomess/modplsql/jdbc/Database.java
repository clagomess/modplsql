package com.github.clagomess.modplsql.jdbc;

import com.github.clagomess.modplsql.dto.ConfigDto;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Map;

@Slf4j
public class Database {
    public static ConfigDto configDto;
    private static Connection conn;

    public static void init(ConfigDto dto) throws SQLException {
        configDto = dto;
        conn = DriverManager.getConnection(dto.getDbUrl(), dto.getDbUser(), dto.getDbPass());

        // inicia OWA
        owaInit();
    }

    private static void owaInit() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeQuery("declare\n" +
                "    nm  owa.vc_arr;\n" +
                "    vl  owa.vc_arr;\n" +
                "begin\n" +
                "    nm(1) := 'SERVER_PORT';\n" +
                "    vl(1) := '80';\n" +
                "    owa.init_cgi_env( 1, nm, vl );\n" +
                "end;");
        stmt.close();
    }

    public static String runPl(String plName, Map<String, String> param) throws SQLException {
        // fill parans
        param.putAll(configDto.getParamsAsMap());

        StringBuilder sql = new StringBuilder();
        sql.append("DECLARE\n" +
                "  NUM_ENTRIES NUMBER;\n" +
                "  NAME_ARRAY OWA.VC_ARR;\n" +
                "  VALUE_ARRAY OWA.VC_ARR;\n" +
                "  RESERVED OWA.VC_ARR;\n" +
                "BEGIN\n");

        sql.append(String.format("  NUM_ENTRIES := %s;\n", param.size()));

        int idx = 1;
        for (Map.Entry<String, String> entry : param.entrySet()) {
            sql.append(String.format("  NAME_ARRAY(%s) := '%s';\n", idx, entry.getKey()));
            sql.append(String.format("  VALUE_ARRAY(%s) := '%s';\n", idx, entry.getValue()));
            idx++;
        }

        sql.append(plName);
        sql.append("(\n" +
                "    NUM_ENTRIES => NUM_ENTRIES,\n" +
                "    NAME_ARRAY => NAME_ARRAY,\n" +
                "    VALUE_ARRAY => VALUE_ARRAY,\n" +
                "    RESERVED => RESERVED\n" +
                "  );\n" +
                "END;");

        log.info("QUERY:\n{}", sql.toString());

        Statement stmt = conn.createStatement();
        stmt.executeQuery(sql.toString());

        log.info("GET RESULT");
        StringBuilder result = new StringBuilder();
        while (true){
            String buff = getResult();

            if(buff == null){
                break;
            }

            result.append(buff);
        }

        return result.toString();
    }

    private static String getResult() throws SQLException {
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

        return cs.getString(2);
    }
}
