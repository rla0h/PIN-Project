import DDS.*;
import Topology.*;
import java.sql.*;

public class neo4j_Adaptor {

    private Connection conn;

    public void neo4j_connect() {
        try {
            Class.forName("org.neo4j.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:neo4j:bolt://10.244.3.20:7687", "neo4j", "1234");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void neo4j_insert_switch(
            String desc,
            String mrid,
            String name,
            String aliasName,
            boolean normalOpen,
            boolean open
    ) throws SQLException {

        Statement stmt = conn.createStatement();
        String str_normalOpen = String.valueOf(normalOpen);
        String str_Open = String.valueOf(open);
        String query =
        "merge (sw: Switch_st {name : 'Switch_st'})" +
        "merge (desc : description {data: \"" +desc+ "\"})" +
        "merge (name : name {data : \""+ name+ "\"})" +
        "merge (aliasName : LocKey {data : \"" + aliasName+ "\"})" +
        "merge (mrid : mRID {data : \"" + mrid + "\"})" +
        "merge (normalOpen : Pos {data : "+ str_normalOpen +"})" +
        "merge (open : SwTyp {data : "+ str_Open + " })" +

        "merge (sw)-[:desc]->(desc)" +
        "merge (sw)-[:name]->(name)" +
        "merge (sw)-[:aliasName]->(aliasName)" +
        "merge (sw)-[:mrid]->(mrid)" +
        "merge (sw)-[:normalOpen]->(normalOpen)" +
        "merge (sw)-[:open]->(open)";


        ResultSet rs = stmt.executeQuery(query);
        rs.close();
    }

    public void neo4j_insert_acl(
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
    ) throws SQLException {

        Statement stmt = conn.createStatement();
        String query = "merge (ac: ACLineSegment {name : 'ACLineSegment'})" +
                "merge (l : LinLenkm {name:'LinLenkm'})" +
                "merge (r : RPs {name:'RPs'})" +
                "merge (x : XPs {name:'XPs'})" +
                "merge (r0 : RZer {name:'RZer'})" +
                "merge (x0 : XZer {name:'XZer'})" +

                "merge (l_d: l_data {multi : " + lmultiplier + ", unit : " + lunit +
                ", value : " + lvalue + "}) " +
                "merge (r_d: r_data {multi : " + rmultiplier + ", unit : " + runit + ", value : " + rvalue + "}) " +
                "merge (rz_d: r0_data {multi : " + r0multiplier + ", unit : " + r0unit + ", value : " + r0value + "}) " +
                "merge (x_d: x_data {multi : " + xmultiplier + ", unit : " + xunit + ", value : " + xvalue + "}) " +
                "merge (xz_d: x0_data {multi : " + x0multiplier + ", unit : " + x0unit + ", value : " + x0value + "}) " +

                "merge (ac)-[:l]->(l)-[:Data]->(l_d)" +
                "merge (ac)-[:r]->(r)-[:Data]->(r_d)" +
                "merge (ac)-[:r0]->(r0)-[:Data]->(rz_d)" +
                "merge (ac)-[:x]->(x)-[:Data]->(x_d)" +
                "merge (ac)-[:x0]->(x0)-[:Data]->(xz_d)";

        ResultSet rs = stmt.executeQuery(query);
        rs.close();
    }
}
