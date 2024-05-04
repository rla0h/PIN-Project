/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

import DDS.*;
import OpenDDS.DCPS.*;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.omg.CORBA.StringSeqHolder;
import Topology.*;

import java.time.Instant;

public class FEP_Subscriber {
    public static boolean checkReliable(String[] args) {
      for (int i = 0; i < args.length; ++i) {
        if (args[i].equals("-r")) {
          return true;
        }
      }
      return false;
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Start Subscriber");
        boolean reliable = checkReliable(args);

        DomainParticipantFactory dpf =
            TheParticipantFactory.WithArgs(new StringSeqHolder(args));
        if (dpf == null) {
            System.err.println("ERROR: Domain Participant Factory not found");
            return;
        }
        DomainParticipant dp = dpf.create_participant(4,
            PARTICIPANT_QOS_DEFAULT.get(), null, DEFAULT_STATUS_MASK.value);
        if (dp == null) {
            System.err.println("ERROR: Domain Participant creation failed");
            return;
        }

        SwitchTopicTypeSupportImpl servant1 = new SwitchTopicTypeSupportImpl();
        ACLineSegmentTopicTypeSupportImpl servant2 = new ACLineSegmentTopicTypeSupportImpl();
        if (servant1.register_type(dp, "") != RETCODE_OK.value) {
            System.err.println("ERROR: register_type failed");
            return;
        }
        if (servant2.register_type(dp, "") != RETCODE_OK.value) {
            System.err.println("ERROR: register_type failed");
            return;
        }

        Topic switch_topic = dp.create_topic("Switch",
                                    servant1.get_type_name(),
                                    TOPIC_QOS_DEFAULT.get(),
                                    null,
                                    DEFAULT_STATUS_MASK.value);

        Topic aclinesegment_topic = dp.create_topic("AClineSegment",
                                    servant2.get_type_name(),
                                    TOPIC_QOS_DEFAULT.get(),
                                    null,
                                    DEFAULT_STATUS_MASK.value);
        if (switch_topic == null) {
            System.err.println("ERROR: Topic creation failed");
            return;
        }
        if (aclinesegment_topic == null) {
            System.err.println("ERROR: Topic creation failed");
            return;
        }

        Subscriber sub = dp.create_subscriber(SUBSCRIBER_QOS_DEFAULT.get(),
                                              null, DEFAULT_STATUS_MASK.value);
        if (sub == null) {
            System.err.println("ERROR: Subscriber creation failed");
            return;
        }

        // Use the default transport (do nothing)

        DataReaderQos dr_qos = new DataReaderQos();
        dr_qos.durability = new DurabilityQosPolicy();
        dr_qos.durability.kind = DurabilityQosPolicyKind.from_int(0);
        dr_qos.deadline = new DeadlineQosPolicy();
        dr_qos.deadline.period = new Duration_t();
        dr_qos.latency_budget = new LatencyBudgetQosPolicy();
        dr_qos.latency_budget.duration = new Duration_t();
        dr_qos.liveliness = new LivelinessQosPolicy();
        dr_qos.liveliness.kind = LivelinessQosPolicyKind.from_int(0);
        dr_qos.liveliness.lease_duration = new Duration_t();
        dr_qos.reliability = new ReliabilityQosPolicy();
        dr_qos.reliability.kind = ReliabilityQosPolicyKind.from_int(0);
        dr_qos.reliability.max_blocking_time = new Duration_t();
        dr_qos.destination_order = new DestinationOrderQosPolicy();
        dr_qos.destination_order.kind = DestinationOrderQosPolicyKind.from_int(0);
        dr_qos.history = new HistoryQosPolicy();
        dr_qos.history.kind = HistoryQosPolicyKind.from_int(0);
        dr_qos.resource_limits = new ResourceLimitsQosPolicy();
        dr_qos.user_data = new UserDataQosPolicy();
        dr_qos.user_data.value = new byte[0];
        dr_qos.ownership = new OwnershipQosPolicy();
        dr_qos.ownership.kind = OwnershipQosPolicyKind.from_int(0);
        dr_qos.time_based_filter = new TimeBasedFilterQosPolicy();
        dr_qos.time_based_filter.minimum_separation = new Duration_t();
        dr_qos.reader_data_lifecycle = new ReaderDataLifecycleQosPolicy();
        dr_qos.reader_data_lifecycle.autopurge_nowriter_samples_delay = new Duration_t();
        dr_qos.reader_data_lifecycle.autopurge_disposed_samples_delay = new Duration_t();
        dr_qos.representation = new DataRepresentationQosPolicy();
        dr_qos.representation.value = new short[0];
        dr_qos.type_consistency = new TypeConsistencyEnforcementQosPolicy();
        dr_qos.type_consistency.kind = 2;
        dr_qos.type_consistency.ignore_member_names = false;
        dr_qos.type_consistency.force_type_validation = false;

        DataReaderQosHolder qosh = new DataReaderQosHolder(dr_qos);
        sub.get_default_datareader_qos(qosh);
        if (reliable) {
          qosh.value.reliability.kind =
            ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        }
        qosh.value.history.kind = HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;
        qosh.value.durability.kind = DurabilityQosPolicyKind.PERSISTENT_DURABILITY_QOS;
        qosh.value.resource_limits.max_samples = LENGTH_UNLIMITED.value;
        qosh.value.resource_limits.max_instances = LENGTH_UNLIMITED.value;
        qosh.value.resource_limits.max_samples_per_instance = LENGTH_UNLIMITED.value;

        SwitchTopicDataReaderListenImpl switch_impl = new SwitchTopicDataReaderListenImpl();
        ACLineSegmentTopicDataReaderListenerImpl acl_impl = new ACLineSegmentTopicDataReaderListenerImpl();

        GuardCondition gc = new GuardCondition();
        WaitSet ws = new WaitSet();
        ws.attach_condition(gc);

        DataReader dra = sub.create_datareader(switch_topic,
                                               qosh.value,
                                               switch_impl,
                                               DEFAULT_STATUS_MASK.value);
        DataReader drb = sub.create_datareader(aclinesegment_topic,
                                               qosh.value,
                                               acl_impl,
                                               DEFAULT_STATUS_MASK.value);

        CheckQoSValue checkQoSValue = new CheckQoSValue();
        String qos_val = checkQoSValue.checkQoS(qosh);
        System.out.println(qos_val);

        String token = "QNVLjuNOvfoyiD-ulB7vtsoGSdWDEsCvOTixn4r_JVSBKv751MOseGojkzvqTSROctskIXCNx92M5dRYqI7PUA==";
        String bucket = "pin";
        String org = "pin";


//        InfluxDBClient client = InfluxDBClientFactory.create("http://10.244.3.2:8086", token.toCharArray());
//        Point point = Point
//                .measurement("qos")
//                .addField("qos", qos_val)
//                .time(Instant.now(), WritePrecision.NS);
//        WriteApi writeApi = client.getWriteApi();
//        writeApi.writePoint(bucket, org, point);
//        client.close();

        if (dra== null) {
            System.err.println("ERROR: DataReader creation failed");
            return;
        }
        if (drb== null) {
            System.err.println("ERROR: DataReader creation failed");
            return;
        }

        Duration_t timeout = new Duration_t(DURATION_INFINITE_SEC.value,
                                            DURATION_INFINITE_NSEC.value);

        ConditionSeqHolder cond = new ConditionSeqHolder(new Condition[]{});
        if (ws.wait(cond, timeout) != RETCODE_OK.value) {
            System.err.println("ERROR: wait() failed.");
            return;
        }
        ws.detach_condition(gc);

        System.out.println("Stop Subscriber");

        dp.delete_contained_entities();
        dpf.delete_participant(dp);
        TheServiceParticipant.shutdown();

        System.out.println("Subscriber exiting");
    }
}
