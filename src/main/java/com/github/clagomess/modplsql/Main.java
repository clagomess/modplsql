package com.github.clagomess.modplsql;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws Throwable {
        Connection conn = DriverManager.getConnection(
                "xxx",
                "xxx",
                "xxx"
        );

        // owa init
        Statement stmt2 = conn.createStatement();
        stmt2.executeQuery("declare\n" +
                "    nm  owa.vc_arr;\n" +
                "    vl  owa.vc_arr;\n" +
                "begin\n" +
                "    nm(1) := 'SERVER_PORT';\n" +
                "    vl(1) := '80';\n" +
                "    owa.init_cgi_env( 1, nm, vl );\n" +
                "end;");

        Statement stmt = conn.createStatement();
        stmt.executeQuery("call teste()");


        // get result
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


        System.out.println(cs.getString(2));
        System.out.println(cs.getInt(3));
    }
}
