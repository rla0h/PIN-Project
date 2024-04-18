/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

 import DDS.*;
 import OpenDDS.DCPS.*;
 import OpenDDS.DCPS.transport.*;
 
 import java.time.Instant;
 import java.util.List;
 import java.util.concurrent.TimeUnit;
 import com.influxdb.annotations.Column;
 import com.influxdb.annotations.Measurement;
 import com.influxdb.client.InfluxDBClient;
 import com.influxdb.client.InfluxDBClientFactory;
 import com.influxdb.client.WriteApi;
 import com.influxdb.client.domain.WritePrecision;
 import com.influxdb.client.write.Point;
 import com.influxdb.query.FluxTable;
 
 import org.omg.CORBA.StringSeqHolder;
 import Fep_module.*;
 import java.util.ArrayList;
  
 public class DataReaderListenerImpl extends DDS._DataReaderListenerLocalBase {
 
     private int num_msgs = 0;
 
     private int expected_count = 100;
 
     private static final int N_EXPECTED = 100;
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
 
         FepTopicDataReader fdr = FepTopicDataReaderHelper.narrow(reader);
         if (fdr == null) {
             System.err.println("ERROR: read: narrow failed.");
             return;
         }
         IdentifiedObject io = new IdentifiedObject();
         Length l = new Length();
         Fep f = new Fep(io, l);
         FepTopicHolder fh = new FepTopicHolder(new FepTopic(f, 0));
         SampleInfoHolder sih = new SampleInfoHolder(new SampleInfo(0, 0, 0,
             new DDS.Time_t(), 0, 0, 0, 0, 0, 0, 0, false, 0));
         int status = fdr.take_next_sample(fh, sih);
         String token = "__PkmbiitPLhELBQeEJTep6iQM7iY5uZwGl_hEIvi4T-JEXdCzZAsJUfzJ6zkLgGi2WBeTdsektLrI1q4KRY8w==";
         String bucket = "pin";
         String org = "pin";
 
         InfluxDBClient client = InfluxDBClientFactory.create("http://192.168.0.201:30086", token.toCharArray());
         if (status == RETCODE_OK.value) {
 
             System.out.println("SampleInfo.sample_rank = "
                                 + sih.value.sample_rank);
             System.out.println("SampleInfo.instance_state = "
                                 + sih.value.instance_state);
 
             if (sih.value.valid_data) {
 
                 String prefix = "";
                 boolean invalid_count = false;
                 if (fh.value.topicCount < 0 || fh.value.topicCount >= counts.size()) {
                     invalid_count = true;
                 }
                 else {
                     if (counts.get(fh.value.topicCount) == false){
                         counts.set(fh.value.topicCount, true);
                     }
                     else {
                         prefix = "ERROR: Repeat ";
                     }
                 }
                 String topic_name = fdr.get_topicdescription().get_name().toString();
                 Point point = Point
                     .measurement("topic")
                     .addField("topic_field", topic_name)
                     .time(Instant.now(), WritePrecision.NS);
                 WriteApi writeApi = client.getWriteApi();
                 writeApi.writePoint(bucket, org, point);
                 System.out.println(fdr.get_topicdescription().get_name());
                 System.out.println(prefix + "Message: aliasName    = " + fh.value.f.io.aliasName);                System.out.println("         description       = " + fh.value.f.io.description);
                 System.out.println("         count      = " + fh.value.topicCount);
                 System.out.println("         mRID       = " + fh.value.f.io.mRID);
                 System.out.println("         name       = " + fh.value.f.io.name);
                 System.out.println("         unit       = " + fh.value.f.l.unit);
                 System.out.println("         multiplier  = " + fh.value.f.l.multiplier);
                 System.out.println("         value       = " + fh.value.f.l.value);
 
                 // if (invalid_count == true) {
                 //     System.out.println("ERROR: Invalid message.count (" + mh.value.count + ")");
                 // }
                 // if (!mh.value.from.equals("Comic Book Guy") && !mh.value.from.equals("OpenDDS-Java")) {
                 //     System.out.println("ERROR: Invalid message.from (" + mh.value.from + ")");
                 // }
                 // if (!mh.value.subject.equals("Review")) {
                 //     System.out.println("ERROR: Invalid message.subject (" + mh.value.subject + ")");
                 // }
                 // if (!mh.value.text.equals("Worst. Movie. Ever.")) {
                 //     System.out.println("ERROR: Invalid message.text (" + mh.value.text + ")");
                 // }
                 // if (mh.value.subject_id != 99) {
                 //     System.out.println("ERROR: Invalid message.subject_id (" + mh.value.subject_id + ")");
                 // }
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
 
         if (fh.value.topicCount + 1 == expected_count) {
             gc.set_trigger_value(true);
         }
         client.close();
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
 
     public void on_liveliness_changed(DDS.DataReader reader, DDS.LivelinessChangedStatus status) {        System.err.println("DataReaderListenerImpl.on_liveliness_changed");
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