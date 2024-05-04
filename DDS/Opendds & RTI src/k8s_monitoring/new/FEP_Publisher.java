/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

import DDS.*;
import OpenDDS.DCPS.*;
import org.omg.CORBA.StringSeqHolder;
import Topology.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class FEP_Publisher {

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

        SwitchTopicTypeSupportImpl servant1 = new SwitchTopicTypeSupportImpl();
        if (servant1.register_type(dp, "") != RETCODE_OK.value) {
            System.err.println("ERROR: register_type failed");
            return;
        }

        ACLineSegmentTopicTypeSupportImpl servant2 = new ACLineSegmentTopicTypeSupportImpl();
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
        qosh.value.durability.kind = DurabilityQosPolicyKind.PERSISTENT_DURABILITY_QOS;
        qosh.value.resource_limits.max_samples_per_instance = LENGTH_UNLIMITED.value;
        qosh.value.resource_limits.max_instances = LENGTH_UNLIMITED.value;
        qosh.value.resource_limits.max_samples = LENGTH_UNLIMITED.value;
        if (reliable) {
            qosh.value.reliability.kind =
                    ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        }
        DataWriter dwa = pub.create_datawriter(switch_topic,
                qosh.value,
                null,
                DEFAULT_STATUS_MASK.value);

        DataWriter dwb = pub.create_datawriter(aclinesegment_topic,
                qosh.value,
                null,
                DEFAULT_STATUS_MASK.value);
        if (dwa == null) {
            System.err.println("ERROR: DataWriter creation failed");
            return;
        }
        if (dwb == null) {
            System.err.println("ERROR: DataWriter creation failed");
            return;
        }
        System.out.println("Publisher Created DataWriter");

        StatusCondition sca = dwa.get_statuscondition();
        StatusCondition scb = dwb.get_statuscondition();
        sca.set_enabled_statuses(PUBLICATION_MATCHED_STATUS.value);
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
            if (resulta != RETCODE_OK.value && resultb != RETCODE_OK.value) {
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

        int sw_count = 0;
        int acl_count = 0;
        SwitchTopicDataWriter mdwa = SwitchTopicDataWriterHelper.narrow(dwa);
        IdentifiedObject io = new IdentifiedObject();
        PowerSystemResource psr = new PowerSystemResource(io, (byte) 0);
        Equipment eq = new Equipment(psr, (byte) 0);
        ConductingEquipment ce = new ConductingEquipment(eq, (byte) 0);
        ActivePower ap = new ActivePower(0, 0, 0);
        SSwitch ss = new SSwitch(ce, false, false, ap);
        SwitchTopic st = new SwitchTopic(ss, sw_count);

        ACLineSegmentTopicDataWriter mdwb = ACLineSegmentTopicDataWriterHelper.narrow(dwb);
        ActivePower length = new ActivePower(0, 0, 0);
        ActivePower r = new ActivePower(0, 0, 0);
        ActivePower r0 = new ActivePower(0, 0, 0);
        ActivePower x = new ActivePower(0, 0, 0);
        ActivePower x0 = new ActivePower(0, 0, 0);

        Conductor c = new Conductor(ce, length);
        ACLineSegment ac = new ACLineSegment(c, r, r0, x, x0);
        ACLineSegmentTopic ast = new ACLineSegmentTopic(ac, acl_count);

        try {
            DatagramSocket serverSocket = new DatagramSocket(12345);

            System.out.println("udp server : " + 12345);
            int dnp_ishere = 0;
            int iec_ishere = 0;
            boolean iec_loop = true;
            boolean dnp_loop = true;
            List<String> dnp_list = new ArrayList<>();
            List<String> iec_list = new ArrayList<>();
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                serverSocket.receive(receivePacket);
                String receivedMessage_first = new String(receivePacket.getData(), 0, receivePacket.getLength());
                if (receivedMessage_first.equals("DNP")) {
                    while(dnp_loop) {
                        receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        String[] dnp_arr = receivedMessage.split(" ");
                        for (String dnp_data : dnp_arr) {
                            //System.out.println("dnp_data " + dnp_data);
                            if (dnp_data.equals("dnpEND")) {
                                System.out.println("DNP Finish");
                                dnp_ishere = 1;
                                dnp_loop = false;
                            }
                            dnp_list.add(dnp_data);
                        }
                    }
                }
                else if (receivedMessage_first.equals("61850")) {
                    while (iec_loop) {
                        receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        String[] iec_arr = receivedMessage.split(" ");
                        for(String iec_data : iec_arr) {
                            //System.out.println("iec data : " + iec_data);
                            if(iec_data.equals("iecEND")){
                                System.out.println("61850 Finish");
                                iec_ishere = 1;
                                iec_loop = false;
                            }
                            iec_list.add(iec_data);
                        }
                    }
                }
                if((dnp_ishere == 1) && (!dnp_list.isEmpty())){
                    for (int i = 0; i < dnp_list.size() - 1; i += 2) {
                        String name = dnp_list.get(i);
                        String value = dnp_list.get(i + 1);
                        if (name.equals("len_unit")) {
                            ast.acls.conductor_.length_.unit = Integer.parseInt(value);
                        } else if (name.equals("len_multi")) {
                            ast.acls.conductor_.length_.multiplier = Integer.parseInt(value);
                        } else if (name.equals("len_value")) {
                            ast.acls.conductor_.length_.value = Float.parseFloat(value);
                        } else if (name.equals("r_unit")) {
                            ast.acls.r.unit = Integer.parseInt(value);
                        } else if (name.equals("r_multi")) {
                            ast.acls.r.multiplier = Integer.parseInt(value);
                        } else if (name.equals("r_value")) {
                            ast.acls.r.value = Float.parseFloat(value);
                        } else if (name.equals("r0_unit")) {
                            ast.acls.r0.unit = Integer.parseInt(value);
                        } else if (name.equals("r0_multi")) {
                            ast.acls.r0.multiplier = Integer.parseInt(value);
                        } else if (name.equals("r0_value")) {
                            ast.acls.r0.value = Float.parseFloat(value);
                        } else if (name.equals("x_unit")) {
                            ast.acls.x.unit = Integer.parseInt(value);
                        } else if (name.equals("x_multi")) {
                            ast.acls.x.multiplier = Integer.parseInt(value);
                        } else if (name.equals("x_value")) {
                            ast.acls.x.value = Float.parseFloat(value);
                        } else if (name.equals("x0_unit")) {
                            ast.acls.x0.unit = Integer.parseInt(value);
                        } else if (name.equals("x0_multi")) {
                            ast.acls.x0.multiplier = Integer.parseInt(value);
                        } else if (name.equals("x0_value")) {
                            ast.acls.x0.value = Float.parseFloat(value);
                        } else if (name.equals("normalOpen")) {
                            st.s.normalOpen = true;
                            st.s.conducting_equipment.equipment_.power_system_resource.identified_object.description = "DNP";
                            ast.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.name="";
                            ast.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.mRID = "";
                            st.s.conducting_equipment.equipment_.power_system_resource.identified_object.name = "";
                            st.s.conducting_equipment.equipment_.power_system_resource.identified_object.mRID = "";

                        }
                    }
                    dnp_loop = true;
                    dnp_ishere = 0;
                    dnp_list = new ArrayList<>();
                }
                else if ((iec_ishere == 1) && (!iec_list.isEmpty())) {
                    for (int i = 0; i < iec_list.size() - 1; i += 2) {
                        String name = iec_list.get(i);
                        String value = iec_list.get(i + 1);
                        if (name.equals("ZLIN1_name")) {
                            ast.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.name = value;
                        } else if (name.equals("ZLIN1_mRID")) {
                            ast.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.mRID = value;
                        } else if (name.equals("LinLen_unit")) {
                            ast.acls.conductor_.length_.unit = Integer.parseInt(value);
                        } else if (name.equals("LinLen_multi")) {
                            ast.acls.conductor_.length_.multiplier = Integer.parseInt(value);
                        } else if (name.equals("LinLen_f")) {
                            ast.acls.conductor_.length_.value = Float.parseFloat(value);
                        } else if (name.equals("RPs_unit")) {
                            ast.acls.r.unit = Integer.parseInt(value);
                        } else if (name.equals("RPs_multi")) {
                            ast.acls.r.multiplier = Integer.parseInt(value);
                        } else if (name.equals("RPs_f")) {
                            ast.acls.r.value = Float.parseFloat(value);
                        } else if (name.equals("XPs_unit")) {
                            ast.acls.x.unit = Integer.parseInt(value);
                        } else if (name.equals("XPs_multi")) {
                            ast.acls.x.multiplier = Integer.parseInt(value);
                        } else if (name.equals("XPs_f")) {
                            ast.acls.x.value = Float.parseFloat(value);
                        } else if (name.equals("RZer_unit")) {
                            ast.acls.r0.unit = Integer.parseInt(value);
                        } else if (name.equals("RZer_multi")) {
                            ast.acls.r0.multiplier = Integer.parseInt(value);
                        } else if (name.equals("RZer_f")) {
                            ast.acls.r0.value = Float.parseFloat(value);
                        } else if (name.equals("XZer_unit")) {
                            ast.acls.x0.unit = Integer.parseInt(value);
                        } else if (name.equals("XZer_multi")) {
                            ast.acls.x0.multiplier = Integer.parseInt(value);
                        } else if (name.equals("XZer_f")) {
                            ast.acls.x0.value = Float.parseFloat(value);
                        } else if (name.equals("XSWI1_name")) {
                            st.s.conducting_equipment.equipment_.power_system_resource.identified_object.name = value;
                        } else if (name.equals("XSWI1_mRID")) {
                            st.s.conducting_equipment.equipment_.power_system_resource.identified_object.mRID = value;
                        } else if (name.equals("Pos_normalOpen")) {
                            st.s.normalOpen = true;
                            st.s.conducting_equipment.equipment_.power_system_resource.identified_object.description = "61850";
                        }
                    }
                    iec_ishere = 0;
                    iec_loop = true;
                    iec_list = new ArrayList<>();
                }
                st.topicCount = sw_count;
                ast.topicCount = acl_count;
                int handlea = mdwa.register_instance(st);
                int handleb = mdwb.register_instance(ast);

                int ret = RETCODE_TIMEOUT.value;
                mdwa.write(st, handlea);

                mdwb.write(ast, handleb);
                sw_count++;
                acl_count++;

//                    while ((ret = mdwa.write(st, handlea)) == RETCODE_TIMEOUT.value) {
//                    }
//                    while ((ret = mdwb.write(ast, handleb)) == RETCODE_TIMEOUT.value) {
//                    }

            }

            } catch(Exception e){
                e.printStackTrace();
            }
        }
}
