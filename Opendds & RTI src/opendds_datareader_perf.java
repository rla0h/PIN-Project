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
import NWT.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
//import dbis.*;
public class NWT_DataReaderListenerImpl extends DDS._DataReaderListenerLocalBase {

        // postgresql url, username, password input
        //String url = "jdbc:postgresql://192.168.86.167:30000/postgres";
        //String username = "postgres";
        //String password = "1234";


    private int num_msgs = 0;

    private int expected_count = 10000;
    //private ServiceController sc =null;
    private int resultCnt = 0;
    //public NWT_DataReaderListenerImpl() {
    //	super();
    //	try {
    		//sc = new ServiceController();
    		//String testPubkey = sc.printMsg("hello");
    		//sc.getCount(expected_count);
    		//System.out.println(testPubkey);
    //	} catch (Exception e) {
    //		e.printStackTrace();
    //	}
    //} 
    private static final int N_EXPECTED = 10000;
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

        RecloserTopicDataReader stdr = RecloserTopicDataReaderHelper.narrow(reader);
        if (stdr == null) {
            System.err.println("ERROR: read: narrow failed.");
            return;
        }
        
        RecloserTopicHolder rth = new RecloserTopicHolder(new RecloserTopic());

        SampleInfoHolder sih = new SampleInfoHolder(new SampleInfo(0, 0, 0,
            new DDS.Time_t(), 0, 0, 0, 0, 0, 0, 0, false, 0));
        int status = stdr.take_next_sample(rth, sih);

        if (status == RETCODE_OK.value) {

           // System.out.println("SampleInfo.sample_rank = "
            //                    + sih.value.sample_rank);
            //System.out.println("SampleInfo.instance_state = "
            //                    + sih.value.instance_state);

            if (sih.value.valid_data) {
            
                // try - catch connection setting
                         //try (Connection conn = DriverManager.getConnection(url, username, password))
                         //{ System.out.println("Connected to the PostgreSQL server successfully.");
                         
                         //String sql =
                         //"INSERT INTO public.\"data\" (\"aliasname\", description, \"mrid\", name) VALUES (?, ?, ?, ?)"
                         //; PreparedStatement pstmt = conn.prepareStatement(sql);
                                 

                String prefix = "";
                boolean invalid_count = false;
                if (rth.value.topicCount < 0 || rth.value.topicCount >= counts.size()) {                    invalid_count = true;
                }
                else {
                    if (counts.get(rth.value.topicCount) == false){
                        counts.set(rth.value.topicCount, true);
                    }
                    else {
                        prefix = "ERROR: Repeat ";
                    }
                }
                
                //try {
                //	sc.insertSQL(rth.value.r.io.aliasName, rth.value.r.io.description, rth.value.r.io.mRID, rth.value.r.io.name);
                //} catch (Exception e) {
                //	e.printStackTrace();
                //}
                
                //print message comment
                //System.out.println(rth.value.topicCount);
                //System.out.println("aliasName    = " + rth.value.r.io.aliasName);
                //pstmt.setString(1, rth.value.r.io.aliasName);
                //System.out.println("description = " + rth.value.r.io.description);
                //pstmt.setString(2, rth.value.r.io.description);
                //System.out.println("mRID       = " + rth.value.r.io.mRID);
                //pstmt.setString(3, rth.value.r.io.mRID);
                //System.out.println("name      = " + rth.value.r.io.name);
                //pstmt.setString(4, rth.value.r.io.name);
                //System.out.println("SampleInfo.sample_rank = "
                //                   + sih.value.sample_rank);
                
                // execute query
                //pstmt.executeUpdate();

                //if (invalid_count == true) {
                //    System.out.println("ERROR: Invalid message.count (" + rth.value.topicCount + ")");
                //}

                //              if (!rth.value.r.io.aliasName.equals("Recloser")) {
                //                      System.out.println("ERROR: aliasName (" + rth.value.r.io.aliasName + ")");
                //              }
                //              if (!rth.value.r.io.description.equals("Recloser Data")) {
                //                      System.out.println("ERROR: description (" + rth.value.r.io.description + ")");
                //              }
                //              if (!rth.value.r.io.mRID.equals("000012000001")) {
                //                      System.out.println("ERROR: mRID ("+ rth.value.r.io.mRID + ")"); } if (!rth.value.r.io.name.equals("DGSW1")) {
                //                              System.out.println("ERROR: name (" + rth.value.r.io.name + ")");
                //                      }
                                 
                 //  } catch (SQLException e) {
                //System.out.println(e.getMessage());
                //System.out.println(e.toString());
                //   }
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
            System.err.println("ERROR: read Message: Error: " + status);
        }

        if (rth.value.topicCount + 1 == expected_count) {
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
            System.out.println("ERROR: Missing " + missed_counts + " messages");
        }
    }
}