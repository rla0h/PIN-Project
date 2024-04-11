/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

 import DDS.*;
 import OpenDDS.DCPS.*;
 import OpenDDS.DCPS.transport.*;
 import org.omg.CORBA.StringSeqHolder;
 import org.omg.CORBA.StringHolder;
 import PIN.*;
 import java.util.ArrayList;
 import java.util.Iterator;
 import org.json.JSONArray;
 import org.json.JSONObject;
 import java.sql.*;
 public class DataReaderListenerImpl extends DDS._DataReaderListenerLocalBase {
 
     private int num_msgs = 0;
 
     private int expected_count = 80;
 
     private static final int N_EXPECTED = 80;
     private ArrayList<Boolean> counts = new ArrayList<Boolean>(N_EXPECTED);
 
     private GuardCondition gc;
 
     private void initialize_counts() {
         if (counts.size() > 0) {
         return;
         }
 
         for (int i = 0; i < N_EXPECTED; ++i) {
             counts.add(false);
         }
     }
 
     public void set_expected_count(int expected) {
         expected_count = expected;
     }
 
     public void set_guard_condition(GuardCondition guard_cond) {
         gc = guard_cond;
     }
 
     public synchronized void on_data_available(DDS.DataReader reader) {
         
         initialize_counts();
 
         PinJsonDataReader mdr = PinJsonDataReaderHelper.narrow(reader);
         if (mdr == null) {
             System.err.println("ERROR: read: narrow failed.");
             return;
         }
 
         PinJsonHolder mh = new PinJsonHolder(new PinJson());
         // json
         PinJsonTypeSupport ts = new PinJsonTypeSupportImpl();
         RepresentationFormat format =ts.make_format(JSON_DATA_REPRESENTATION.value);
         StringHolder holder = new StringHolder();
         
         SampleInfoHolder sih = new SampleInfoHolder(new SampleInfo(0, 0, 0,
             new DDS.Time_t(), 0, 0, 0, 0, 0, 0, 0, false, 0));
         int status = mdr.take_next_sample(mh, sih);
 
         // neo4j connection
         Connection connection = null;
         Statement stmt = null;
               
         if (status == RETCODE_OK.value) {
             if (sih.value.valid_data) {
                 String prefix = "";
                 boolean invalid_count = false;
                 if (mh.value.count < 0 || mh.value.count >= counts.size()) {
                     invalid_count = true;
                 }
                 else {
                     if (counts.get(mh.value.count) == false){
                         counts.set(mh.value.count, true);
                     }
                     else {
                         prefix = "ERROR: Repeat ";
                     }
                 }
                 System.out.println(prefix + "count    = " + mh.value.count);
                 //System.out.println("version       = " + mh.value.version);
                 
                 // System.out.println(mh.value.body);
                 ts.encode_to_string(mh.value, holder, format);
                 // System.out.println(holder.value);
                 JSONObject jObject = new JSONObject(holder.value);
                 String bodyString = jObject.getString("body");
                 JSONObject body = new JSONObject(bodyString);
                 System.out.println(body.toString());
                 
                 try {
                     String url = "jdbc:neo4j:bolt://localhost:7687";
                     String username = "neo4j";
                     String password = "12341234";
                     
                     connection = DriverManager.getConnection(url, username, password);
                     
                     stmt = connection.createStatement();
                     if(mh.value.count<40) {
                         JSONObject io = body.getJSONObject("io");
                         System.out.println("=====ACDCTerminal=====");
                         System.out.println(io.toString());
                         String aliasName = io.getString("aliasName");
                         double mRID_d = io.getDouble("mRID");
                         long mRID = (long) mRID_d;
                         String name = io.getString("name");
                         String des = io.getString("description");
                         //String query0 = "with { aliasName : '"+ aliasName + "', mrid : " + mRID + ", name : '" + name + "', description : '" + des + "'} AS i_o";
                         String query1 = "merge (at: ACDCTerminal {name : 'ACDCTerminal'})";
                         String query2 = "merge (io : IdentifiedObject {name:'IdentifiedObject'})";
                         String query3 = "merge (data : Data {aliasName : '"+aliasName+"', mRID: " + mRID + ", name : '" + name + "', description : '" + des +"'})";
                         String query4 = "merge (at)-[:IdentifiedObject]->(io)-[:Data]->(data)";
                         String query = query1 + query2 + query3 + query4;
                         //String query = "with { aliasName : '"+ aliasName + "', mrid : " + mRID + ", name : '" + name + "', description : '" + des + "'} AS i_o MERGE (io: IdentifiedObject{mRID: i_o.mrid}) ON CREATE SET io.aliasName = i_o.aliasName, io.name = i_o.name, io.description = i_o.description";
                         ResultSet rs = stmt.executeQuery(query);
                         rs.close();
                     }
                     if(mh.value.count >=40){
                         JSONObject r = body.getJSONObject("r");
                         JSONObject lr = r.getJSONObject("lr");
                         double r_unit = lr.getDouble("unit");
                         double r_value = lr.getDouble("value");
                         double r_multi = lr.getDouble("multi");
                         JSONObject r0 = body.getJSONObject("r0");
                         JSONObject lr0 = r0.getJSONObject("lr0");
                         double r0_unit = lr0.getDouble("unit");
                         double r0_value = lr0.getDouble("value");
                         double r0_multi = lr0.getDouble("multi");
                         JSONObject x = body.getJSONObject("x");
                         JSONObject lx = x.getJSONObject("lx");
                         double x_unit = lx.getDouble("unit");
                         double x_value = lx.getDouble("value");
                         double x_multi = lx.getDouble("multi");
                         JSONObject x0 = body.getJSONObject("x0");
                         JSONObject lx0 = x0.getJSONObject("lx0");
                         double x0_unit = lx0.getDouble("unit");
                         double x0_value = lx0.getDouble("value");
                         double x0_multi = lx0.getDouble("multi");
                         JSONObject c = body.getJSONObject("c");
                         double c_length = c.getDouble("length");
                         System.out.println("=====ACLineSegment=====");
                         System.out.println(r.toString());
                         System.out.println(r0.toString());
                         System.out.println(x.toString());
                         System.out.println(x0.toString());
                         System.out.println(c.toString());
                         String query = "merge (ac: ACLineSegment {name : 'ACLineSegment'})" +
                         "merge (r : RPs {name:'RPs'})" +
                         "merge (r0 : RZer {name:'RZer'})" +
                         "merge (x : XPs {name:'XPs'})" +
                         "merge (x0 : XZer {name:'XZer'})" +
                         "merge (lr : Length_R {name:'Length_R'})" +
                         "merge (lr0 : Length_Rz {name:'Length_Rz'})" +
                         "merge (lx : Length_X {name:'Length_X'})" +
                         "merge (lx0 : Length_X0 {name:'Length_X0'})" +
                         "merge (c : Converter {name:'Converter'})" +
                         "merge (r_d: r_data {unit : " + r_unit + ", value : " + r_value + ", multi : " + r_multi + "}) " +
                         "merge (rz_d: r0_data {unit : " + r0_unit + ", value : " + r0_value + ", multi : " + r0_multi + "}) " +
                         "merge (x_d: x_data {unit : " + x_unit + ", value : " + x_value + ", multi : " + x_multi + "}) " +
                         "merge (xz_d: x0_data {unit : " + x0_unit + ", value : " + x0_value + ", multi : " + x0_multi + "}) " +
                         "merge (c_d: c_data {length : " + c_length +"}) " +
                         "merge (ac)-[:r]->(r)-[:Length_R]->(lr)-[:Data]->(r_d)" +
                         "merge (ac)-[:r0]->(r0)-[:Length_Rz]->(lr0)-[:Data]->(rz_d)" +
                         "merge (ac)-[:x]->(x)-[:Length_X]->(lx)-[:Data]->(x_d)" +
                         "merge (ac)-[:x0]->(x0)-[:Length_Xz]->(lx0)-[:Data]->(xz_d)" +
                         "merge (ac)-[:c]->(c)-[:Data]->(c_d)";
                         ResultSet rs = stmt.executeQuery(query);
                         rs.close();
                     }           
 
                     // while (rs.next()) {
                     //     System.out.println(rs.getString("n"));
                     // }
                     
                     //rs.close();
                     stmt.close();
                     connection.close();
                 } catch (Exception e) {
                     e.printStackTrace();
                 } finally {
                     try {
                         if (stmt != null) stmt.close();
                     } catch (Exception e) {
                     }
                     try {
                         if (connection != null) connection.close();
                     } catch (Exception e) {
                     }
                 }
                 
                 
                 // System.out.printf("\n\n");
                 
 
                 if (invalid_count == true) {
                     System.out.println("ERROR: Invalid PinJson.count (" + mh.value.count + ")");
                 }
             }
             else if (sih.value.instance_state ==
                     NOT_ALIVE_DISPOSED_INSTANCE_STATE.value) {
                 System.out.println("instance is disposed");
             }
             else if (sih.value.instance_state ==
                     NOT_ALIVE_NO_WRITERS_INSTANCE_STATE.value) {
                 System.out.println("instance is unregistered");
             }
             else {
                 System.out.println("DataReaderListenerImpl::on_data_available: "
                                 + "ERROR: received unknown instance state "
                                 + sih.value.instance_state);
             }
 
         } else if (status == RETCODE_NO_DATA.value) {
             System.err.println("ERROR: reader received DDS::RETCODE_NO_DATA!");
         } else {
             System.err.println("ERROR: read PinJson: Error: " + status);
         }
 
         if (mh.value.count + 1 == expected_count) {
             gc.set_trigger_value(true);
         }
     }
 
     public void on_requested_deadline_missed(DDS.DataReader reader, DDS.RequestedDeadlineMissedStatus status) {
         System.err.println("DataReaderListenerImpl.on_requested_deadline_missed");
     }
 
     public void on_requested_incompatible_qos(DDS.DataReader reader, DDS.RequestedIncompatibleQosStatus status) {
         System.err.println("DataReaderListenerImpl.on_requested_incompatible_qos");
     }
 
     public void on_sample_rejected(DDS.DataReader reader, DDS.SampleRejectedStatus status) {
         System.err.println("DataReaderListenerImpl.on_sample_rejected");
     }
 
     public void on_liveliness_changed(DDS.DataReader reader, DDS.LivelinessChangedStatus status) {
         System.err.println("DataReaderListenerImpl.on_liveliness_changed");
     }
 
     public void on_subscription_matched(DDS.DataReader reader, DDS.SubscriptionMatchedStatus status) {
         System.err.println("DataReaderListenerImpl.on_subscription_matched");
     }
 
     public void on_sample_lost(DDS.DataReader reader, DDS.SampleLostStatus status) {
         System.err.println("DataReaderListenerImpl.on_sample_lost");
     }
 
     public void report_validity() {
         int count = 0;
         int missed_counts = 0;
         for (Boolean val : counts) {
             if (val == false)
                 ++missed_counts;
         }
         if (missed_counts > N_EXPECTED - expected_count) {
             System.out.println("ERROR: Missing " + missed_counts + " PinJsons");
         }
     }
 }
  