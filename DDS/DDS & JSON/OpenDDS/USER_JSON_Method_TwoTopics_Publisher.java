/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

import DDS.*;
import OpenDDS.DCPS.*;
import org.omg.CORBA.StringSeqHolder;
import org.omg.CORBA.StringHolder;
import java.nio.charset.*;
import java.nio.file.*;
import PIN.*;
import org.json.JSONObject;

public class PinPublisher {

  private static final int N_MSGS = 80;

  public static boolean checkReliable(String[] args) {
    for (int i = 0; i < args.length; ++i) {
      if (args[i].equals("-r")) {
        return true;
      }
    }
    return false;
  }

  public static void main(String[] args) throws Exception
  {
    System.out.println("Start Publisher");
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

    PinJsonTypeSupportImpl servant = new PinJsonTypeSupportImpl();
    if (servant.register_type(dp, "") != RETCODE_OK.value) {
        System.err.println("ERROR: register_type failed");
        return;
    }
    
    Topic topa = dp.create_topic("A",
                                servant.get_type_name(),
                                TOPIC_QOS_DEFAULT.get(),
                                null,
                                DEFAULT_STATUS_MASK.value);
    Topic topb = dp.create_topic("B",
                                servant.get_type_name(),
                                TOPIC_QOS_DEFAULT.get(),
                                null,
                                DEFAULT_STATUS_MASK.value);
    
    if ((topa == null) && (topb == null)) {
        System.err.println("ERROR: Topic creation failed");
        return;
    }

    Publisher pub = dp.create_publisher(PUBLISHER_QOS_DEFAULT.get(), null,
                                        DEFAULT_STATUS_MASK.value);
    if (pub == null) {
        System.err.println("ERROR: Publisher creation failed");
        return;
    }

    // Use the default transport configuration (do nothing)

    DataWriterQos dw_qos = new DataWriterQos();
    dw_qos.durability = new DurabilityQosPolicy();
    dw_qos.durability.kind = DurabilityQosPolicyKind.from_int(0);
    dw_qos.durability_service = new DurabilityServiceQosPolicy();
    dw_qos.durability_service.history_kind = HistoryQosPolicyKind.from_int(0);
    dw_qos.durability_service.service_cleanup_delay = new Duration_t();
    dw_qos.deadline = new DeadlineQosPolicy();
    dw_qos.deadline.period = new Duration_t();
    dw_qos.latency_budget = new LatencyBudgetQosPolicy();
    dw_qos.latency_budget.duration = new Duration_t();
    dw_qos.liveliness = new LivelinessQosPolicy();
    dw_qos.liveliness.kind = LivelinessQosPolicyKind.from_int(0);
    dw_qos.liveliness.lease_duration = new Duration_t();
    dw_qos.reliability = new ReliabilityQosPolicy();
    dw_qos.reliability.kind = ReliabilityQosPolicyKind.from_int(0);
    dw_qos.reliability.max_blocking_time = new Duration_t();
    dw_qos.destination_order = new DestinationOrderQosPolicy();
    dw_qos.destination_order.kind = DestinationOrderQosPolicyKind.from_int(0);
    dw_qos.history = new HistoryQosPolicy();
    dw_qos.history.kind = HistoryQosPolicyKind.from_int(0);
    dw_qos.resource_limits = new ResourceLimitsQosPolicy();
    dw_qos.transport_priority = new TransportPriorityQosPolicy();
    dw_qos.lifespan = new LifespanQosPolicy();
    dw_qos.lifespan.duration = new Duration_t();
    dw_qos.user_data = new UserDataQosPolicy();
    dw_qos.user_data.value = new byte[0];
    dw_qos.ownership = new OwnershipQosPolicy();
    dw_qos.ownership.kind = OwnershipQosPolicyKind.from_int(0);
    dw_qos.ownership_strength = new OwnershipStrengthQosPolicy();
    dw_qos.writer_data_lifecycle = new WriterDataLifecycleQosPolicy();
    dw_qos.representation = new DataRepresentationQosPolicy();
    dw_qos.representation.value = new short[0];

    DataWriterQosHolder qosh = new DataWriterQosHolder(dw_qos);
    pub.get_default_datawriter_qos(qosh);
    qosh.value.history.kind = HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;
    if (reliable) {
      qosh.value.reliability.kind =
        ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
    }

    DataWriter dwa = pub.create_datawriter(topa,
                                          qosh.value,
                                          null,
                                          DEFAULT_STATUS_MASK.value
    );
    DataWriter dwb = pub.create_datawriter(topb,
                                          qosh.value,
                                          null,
                                          DEFAULT_STATUS_MASK.value
    );

    if ((dwa == null) || (dwb == null)) {
        System.err.println("ERROR: DataWriter creation failed");
        return;
    }

    System.out.println("Publisher Created DataWriter");

    StatusCondition sca = dwa.get_statuscondition();
    sca.set_enabled_statuses(PUBLICATION_MATCHED_STATUS.value);
    StatusCondition scb = dwb.get_statuscondition();
    scb.set_enabled_statuses(PUBLICATION_MATCHED_STATUS.value);

    WaitSet ws = new WaitSet();
    ws.attach_condition(sca);
    ws.attach_condition(scb);

    PublicationMatchedStatusHolder matched =
      new PublicationMatchedStatusHolder(new PublicationMatchedStatus());
    Duration_t timeout = new Duration_t(DURATION_INFINITE_SEC.value,
                                        DURATION_INFINITE_NSEC.value);

    while (true) {
        final int resulta = dwa.get_publication_matched_status(matched);
        final int resultb = dwb.get_publication_matched_status(matched);

        if ((resulta != RETCODE_OK.value) && (resultb != RETCODE_OK.value)) {
            System.err.println("ERROR: get_publication_matched_status()" +
                              "failed.");
            return;
        }
        if (matched.value.current_count >= 1) {
            System.out.println("Publisher Matched");
            break;
        }

        ConditionSeqHolder cond = new ConditionSeqHolder(new Condition[]{});
        if (ws.wait(cond, timeout) != RETCODE_OK.value) {
            System.err.println("ERROR: wait() failed.");
            return;
        }
    }

    ws.detach_condition(sca);
    ws.detach_condition(scb);

    // Generate JSON
    String fileDir = "PinJson.json";

    String json_str = new String(Files.readAllBytes(Paths.get("PinJson.json")), Charset.defaultCharset());
    JSONObject json = new JSONObject(json_str);
    JSONObject io = json.getJSONObject("ACDCTerminal");
    String io_str = io.toString();
    JSONObject length = json.getJSONObject("ACLineSegment");
    String l_str = length.toString();

    PinJsonDataWriter mda = PinJsonDataWriterHelper.narrow(dwa);
    PinJsonDataWriter mdb = PinJsonDataWriterHelper.narrow(dwb);

    PinJson pja = new PinJson();
    PinJson pjb = new PinJson();
    //pj.version = 1;
    pja.count = 0;
    pjb.count = 40;
    pja.body = io_str;
    pjb.body = l_str;

    int handlea = mda.register_instance(pja);
    int handleb = mdb.register_instance(pjb);

    int ret = RETCODE_TIMEOUT.value;
    for (; pja.count < 40; ++pja.count) {
        while((ret = mda.write(pja, handlea))== RETCODE_TIMEOUT.value){ 
          
        }
        if (ret != RETCODE_OK.value) {
            System.err.println("ERROR " + pja.count +
                              " write() returned " + pja.count);
        }
        try {
          Thread.sleep(100);
        } catch(InterruptedException ie) {
      }
    }
    for (; pjb.count < N_MSGS; ++pjb.count) {
        while((ret = mdb.write(pjb, handleb))== RETCODE_TIMEOUT.value){ 
        }
        if (ret != RETCODE_OK.value) {
            System.err.println("ERROR " + pjb.count +
                              " write() returned " + ret);
        }
        try {
          Thread.sleep(100);
        } catch(InterruptedException ie) {
      }
    }

    while (matched.value.current_count != 0) {
      final int resulta = mda.get_publication_matched_status(matched);
      final int resultb = mda.get_publication_matched_status(matched);
      try {
        Thread.sleep(100);
      } catch(InterruptedException ie) {
      }
    }

    System.out.println("Stop Publisher");

    // Clean up
    dp.delete_contained_entities();
    dpf.delete_participant(dp);
    TheServiceParticipant.shutdown();

    System.out.println("Publisher exiting");
  }
}
 