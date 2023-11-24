/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

import DDS.*;
import OpenDDS.DCPS.*;
import org.omg.CORBA.StringSeqHolder;
import NWT.*;

public class NWT_TestPublisher {

    private static final int N_MSGS = 1000;
        private static final UnitSymbol UnitSymbol = null;

    public static boolean checkReliable(String[] args) {
      for (int i = 0; i < args.length; ++i) {
        if (args[i].equals("-r")) {
          return true;
        }
      }
      return false;
    }

    public static void main(String[] args) {
        
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

        RecloserTopicTypeSupportImpl servant = new RecloserTopicTypeSupportImpl();
        if (servant.register_type(dp, "") != RETCODE_OK.value) {
            System.err.println("ERROR: register_type failed");
            return;
        }

        Topic top = dp.create_topic("RecloserTopic",
                                    servant.get_type_name(),
                                    TOPIC_QOS_DEFAULT.get(),
                                    null,
                                    DEFAULT_STATUS_MASK.value);
        if (top == null) {
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
        qosh.value.reliability.kind =
            ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        
        qosh.value.durability.kind = DurabilityQosPolicyKind.PERSISTENT_DURABILITY_QOS;
        qosh.value.resource_limits.max_samples = LENGTH_UNLIMITED.value;
        qosh.value.resource_limits.max_instances = LENGTH_UNLIMITED.value;
        qosh.value.resource_limits.max_samples_per_instance = LENGTH_UNLIMITED.value;
        DataWriter dw = pub.create_datawriter(top,
                                            qosh.value,
                                            null,
                                            DEFAULT_STATUS_MASK.value);
        if (dw == null) {
            System.err.println("ERROR: DataWriter creation failed");
            return;
        }
        System.out.println("Publisher Created DataWriter");

        StatusCondition sc = dw.get_statuscondition();
        sc.set_enabled_statuses(PUBLICATION_MATCHED_STATUS.value);
        WaitSet ws = new WaitSet();
        ws.attach_condition(sc);
        PublicationMatchedStatusHolder matched =
        new PublicationMatchedStatusHolder(new PublicationMatchedStatus());
        Duration_t timeout = new Duration_t(DURATION_INFINITE_SEC.value,
                                            DURATION_INFINITE_NSEC.value);

        while (true) {
            final int result = dw.get_publication_matched_status(matched);
            if (result != RETCODE_OK.value) {
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

        //double startTime = System.currentTimeMillis();

        ws.detach_condition(sc);

        RecloserTopicDataWriter rtdw = RecloserTopicDataWriterHelper.narrow(dw);
        RecloserTopic rt = new RecloserTopic();
        UnitSymbol us;
        us = UnitSymbol;
        String text = String.format("%-" + Integer.parseInt(args[args.length-1]) + "s", "00");
        rt.r.io.aliasName = text;
        rt.r.io.description = "0";

        rt.r.io.mRID = "0";
        rt.r.io.name = "0";
    
        
        int handle = rtdw.register_instance(rt);

        int ret = RETCODE_TIMEOUT.value;

        double all_time = 0;
        double all_startTime = System.currentTimeMillis();
        for (; rt.topicCount < N_MSGS; ++rt.topicCount) {
            //double one_check = 0;
            //double startTime = System.currentTimeMillis();
            while ((ret = rtdw.write(rt, handle)) == RETCODE_TIMEOUT.value) {
            }
            //double endTime = System.currentTimeMillis();

            //double RTT = endTime - startTime;
            //System.out.println("Data per RTT : " + RTT);
            //all_time += RTT;
            if (ret != RETCODE_OK.value) {
                System.err.println("ERROR " + rt.topicCount +
                                " write() returned " + ret);
            }
            //double endTime = System.currentTimeMillis();
            //all_time += endTime - startTime;
            //one_check = endTime - startTime;
            //System.out.printf("count : %d\nRTT per data : %f Average RTT : %f\n", count, one_check, all_time/count);
            //count++;
            try {
            Thread.sleep(10);
            
            } catch(InterruptedException ie) {
            }
            //System.out.println("SEND MESSAGE");
            //System.out.println("REAL msg.cout : " + rt.topicCount);
            //System.out.print("\n\n");
            //if(rt.topicCount % 2 == 0) {
            
            //  System.out.println("UnitSymbol Value: " + us._UnitSymbol_none);
            //  System.out.println("msg.cout : " + rt.topicCount * us._UnitSymbol_none);            //}
            //else
            //{
            //  System.out.println("UnitSymbol Value: " + us._UnitSymbol_m);
            //  System.out.println("msg.cout : " + rt.topicCount * us._UnitSymbol_m);
            //}
        }
        double all_endTime = System.currentTimeMillis();

        while (matched.value.current_count != 0) {
        final int result = rtdw.get_publication_matched_status(matched);
        //try {
        //    Thread.sleep(100);
        //} catch(InterruptedException ie) {
        //}
        }
        

        double all_RTT = all_endTime - all_startTime;
        System.out.println("Stop Publisher");

        // Clean up
        dp.delete_contained_entities();
        dpf.delete_participant(dp);
        TheServiceParticipant.shutdown();

        System.out.println("Publisher exiting");

        //System.out.println("Throughput (messages/RTT): " + (sendmsg.length() * 100 / latency));
        //all_RTT = all_RTT - (10 * 1000);
        System.out.printf("Mean RTT : %.4f\n",all_RTT / 1000);
    }
}