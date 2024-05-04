import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class JDBCAdaptor {
    private Connection conn = null;

    public JDBCAdaptor() {
        try {
            String DB_URL = "jdbc:postgresql://10.244.3.16:5432/fepdb";
            String DB_USER = "postgres";
            String DB_PASSWORD = "1234";

            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            System.out.println("## in JDBC Adaptor : " + e.getMessage());
        }
    }

    public void createSwitch() {
        try {
            Statement stmt = conn.createStatement();
            String createSwitchTable = "CREATE TABLE IF NOT EXISTS dbis_switch (" +
                    "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "sw_ce_e_psr_io_description VARCHAR(256), " +
                    "sw_ce_e_psr_io_mrid VARCHAR(256), " +
                    "sw_ce_e_psr_io_name VARCHAR(256), " +
                    "sw_ce_e_psr_io_aliasname VARCHAR(256), " +
                    "sw_cf_unit BIGINT, " +
                    "sw_cf_multiplier BIGINT, " +
                    "sw_cf_value REAL," +
                    "sw_normalopen BOOLEAN, " +
                    "sw_open BOOLEAN " +
                    ");";
            stmt.executeUpdate(createSwitchTable);
        } catch (Exception e) {
            System.out.println("## in JDBC Adaptor : " + e.getMessage());
        }
    }
    public void insertSwitch(
            String desc,
            String mrid,
            String name,
            String aliasName,
            long unit,
            long multiplier,
            float value,
            boolean normalOpen,
            boolean open
    ) {
        String insertQuery = "INSERT INTO dbis_switch (" +
                "sw_ce_e_psr_io_description, " +
                "sw_ce_e_psr_io_mrid, " +
                "sw_ce_e_psr_io_name, " +
                "sw_ce_e_psr_io_aliasname, " +
                "sw_cf_unit, " +
                "sw_cf_multiplier, " +
                "sw_cf_value," +
                "sw_normalopen, " +
                "sw_open " +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement psmt = conn.prepareStatement(insertQuery);
            psmt.setString(1, desc);
            psmt.setString(2, mrid);
            psmt.setString(3, name);
            psmt.setString(4, aliasName);
            psmt.setLong(5, unit);
            psmt.setLong(6, multiplier);
            psmt.setFloat(7, value);
            psmt.setBoolean(8, normalOpen);
            psmt.setBoolean(9, open);
            psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("## in JDBC Adaptor : " + e.getMessage());
        }

    }

    public void createAcls() {
        try {
            Statement stmt = conn.createStatement();
            String createAclsTable = "CREATE TABLE IF NOT EXISTS dbis_acls (" +
                    "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "acls_c_ce_psr_io_description VARCHAR(256), " +
                    "acls_c_ce_psr_io_mrid VARCHAR(256), " +
                    "acls_c_ce_psr_io_name VARCHAR(256), " +
                    "acls_c_ce_psr_io_aliasname VARCHAR(256), " +
                    "acls_c_l_unit BIGINT, " +
                    "acls_c_l_multiplier BIGINT, " +
                    "acls_c_l_value REAL, " +
                    "acls_r_unit BIGINT, " +
                    "acls_r_multiplier BIGINT, " +
                    "acls_r_value REAL, " +
                    "acls_r0_unit BIGINT, " +
                    "acls_r0_multiplier BIGINT, " +
                    "acls_r0_value REAL, " +
                    "acls_x_unit BIGINT, " +
                    "acls_x_multiplier BIGINT, " +
                    "acls_x_value REAL," +
                    "acls_x0_unit BIGINT, " +
                    "acls_x0_multiplier BIGINT, " +
                    "acls_x0_value REAL" +
                    ");";
            stmt.executeUpdate(createAclsTable);
        } catch (Exception e) {
            System.out.println("## in JDBC Adaptor : " + e.getMessage());
        }
    }
    public void insertAcls(
            String desc,
            String mrid,
            String name,
            String aliasName,
            long lunit,
            long lmultiplier,
            float lvalue,
            long runit,
            long rmultiplier,
            float rvalue,
            long r0unit,
            long r0multiplier,
            float r0value,
            long xunit,
            long xmultiplier,
            float xvalue,
            long x0unit,
            long x0multiplier,
            float x0value
    ) {
        String insertQuery = "INSERT INTO dbis_acls (" +
                "acls_c_ce_psr_io_description, " +
                "acls_c_ce_psr_io_mrid, " +
                "acls_c_ce_psr_io_name, " +
                "acls_c_ce_psr_io_aliasname, " +
                "acls_c_l_unit, " +
                "acls_c_l_multiplier, " +
                "acls_c_l_value, " +
                "acls_r_unit, " +
                "acls_r_multiplier, " +
                "acls_r_value, " +
                "acls_r0_unit, " +
                "acls_r0_multiplier, " +
                "acls_r0_value, " +
                "acls_x_unit, " +
                "acls_x_multiplier, " +
                "acls_x_value, " +
                "acls_x0_unit, " +
                "acls_x0_multiplier, " +
                "acls_x0_value" +
                ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement psmt = conn.prepareStatement(insertQuery);
            psmt.setString(1, desc);
            psmt.setString(2, mrid);
            psmt.setString(3, name);
            psmt.setString(4, aliasName);
            psmt.setLong(5, lunit);
            psmt.setLong(6, lmultiplier);
            psmt.setFloat(7, lvalue);
            psmt.setLong(8, runit);
            psmt.setLong(9, rmultiplier);
            psmt.setFloat(10, rvalue);
            psmt.setLong(11, r0unit);
            psmt.setLong(12, r0multiplier);
            psmt.setFloat(13, r0value);
            psmt.setLong(14, xunit);
            psmt.setLong(15, xmultiplier);
            psmt.setFloat(16, xvalue);
            psmt.setLong(17, x0unit);
            psmt.setLong(18, x0multiplier);
            psmt.setFloat(19, x0value);
            psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("## in JDBC Adaptor : " + e.getMessage());
        }
    }
}