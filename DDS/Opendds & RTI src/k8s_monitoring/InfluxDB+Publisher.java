/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

 import DDS.*;
 import OpenDDS.DCPS.*;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.ServerSocket;
 import java.net.Socket;
 
 import org.omg.CORBA.StringSeqHolder;
 import Fep_module.*;
 //import NWT.IdentifiedObject;
 
 
 public class FEP_Publisher {
 
     private static final int N_MSGS = 50;
 
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
 
         FepTopicTypeSupportImpl servant = new FepTopicTypeSupportImpl();
         if (servant.register_type(dp, "") != RETCODE_OK.value) {
             System.err.println("ERROR: register_type failed");
             return;
         }
 
         Topic topa = dp.create_topic("ACLineSegment",
                                     servant.get_type_name(),
                                     TOPIC_QOS_DEFAULT.get(),
                                     null,
                                     DEFAULT_STATUS_MASK.value);
         if (topa == null) {
             System.err.println("ERROR: Topic creation failed");
             return;
         }
 
         Topic topb = dp.create_topic("Switch",
                                     servant.get_type_name(),
                                     TOPIC_QOS_DEFAULT.get(),
                                     null,
                                     DEFAULT_STATUS_MASK.value);
         if (topb == null) {
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
                                               DEFAULT_STATUS_MASK.value);
         if (dwa == null) {
             System.err.println("ERROR: DataWriter creation failed");
             return;
         }
 
         DataWriter dwb = pub.create_datawriter(topb,
                                               qosh.value,
                                               null,
                                               DEFAULT_STATUS_MASK.value);
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
           new PublicationMatchedStatusHolder(new PublicationMatchedStatus());         Duration_t timeout = new Duration_t(DURATION_INFINITE_SEC.value,
                                             DURATION_INFINITE_NSEC.value);
 
         while (true) {
             final int resulta = dwa.get_publication_matched_status(matched);
             final int resultb = dwb.get_publication_matched_status(matched);
             if (resulta != RETCODE_OK.value && resultb != RETCODE_OK.value) {                 System.err.println("ERROR: get_publication_matched_status()" +
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
         
         FepTopicDataWriter fdwa = FepTopicDataWriterHelper.narrow(dwa);
         FepTopicDataWriter fdwb = FepTopicDataWriterHelper.narrow(dwb);
         Fep_module.IdentifiedObject io = new Fep_module.IdentifiedObject();
         Length l = new Length();
         Fep f = new Fep(io, l);
         FepTopic fta = new FepTopic(f, 0);
         FepTopic ftb = new FepTopic(f, 50);
         int port = 1234;
         try (ServerSocket serverSocket = new ServerSocket(port)) {
             System.out.println("Server is listening on port " + port);
  
             while (true) {
                 try (Socket socket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) 
                     {
                     
                       StringBuilder message = new StringBuilder();
                       int character;
                       
                       while ((character = reader.read()) != -1) {
                           if (character == '\n') {
                               String line = message.toString();
                               if(line.equals("ZLINname")) {
                                 fta.f.io.name = line;
                                 ftb.f.io.name = line;
                               }
                               else if(line.equals("ZLINmRID")) {
                                 fta.f.io.mRID = line;
                                 ftb.f.io.mRID = line;
                               }
                               else if(line.equals("2")) {
                                 fta.f.l.unit = line;
                                 ftb.f.l.unit = line;
                               }
                               else if(line.equals("-3")) {
                                 fta.f.l.multiplier = line;
                                 ftb.f.l.multiplier = line;
                               } 
                               else if(line.equals("Bye")) {
                                   break;
                               } else {
                                 fta.f.l.value = Float.parseFloat(line);
                                 ftb.f.l.value = Float.parseFloat(line);
                               }
                               message = new StringBuilder(); // Reset for the next message
                           }
                           else {
                             message.append((char) character);
                           }
                       }
                     } 
                     catch (IOException e) {
                     System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
                     System.out.println(e.getMessage());
                 }
                 break;
             }
         } catch (IOException ex) {
             System.out.println("Server exception: " + ex.getMessage());
             ex.printStackTrace();
         }
   
         int handlea = fdwa.register_instance(fta);
         int handleb = fdwb.register_instance(ftb);
         // fta.f.io.aliasName="FEP_aliasName";
         // fta.f.io.description ="only_IEC61850_ZLIN";
         // fta.f.io.mRID = "FEP_mRID";
         // fta.f.io.name = "FEP_name";    
         // fta.f.l.unit = "FEP_Unit";
         // fta.f.l.multiplier = "FEP_Multiplier";
         // fta.f.l.value = UnitSymbol_ohm.value;
 
         // ftb.f.io.aliasName="FEP_aliasName";
         // ftb.f.io.description ="only_IEC61850_ZLIN";
         // ftb.f.io.mRID = "FEP_mRID";
         // ftb.f.io.name = "FEP_name";    
         // ftb.f.l.unit = "FEP_Unit";
         // ftb.f.l.multiplier = "FEP_Multiplier";
         // ftb.f.l.value = UnitSymbol_ohm.value;
     
         int ret = RETCODE_TIMEOUT.value;
         for (; fta.topicCount < N_MSGS; ++fta.topicCount) {
             while ((ret = fdwa.write(fta, handlea)) == RETCODE_TIMEOUT.value) {
             }
             while ((ret = fdwb.write(ftb, handleb)) == RETCODE_TIMEOUT.value) {
             }
             ftb.topicCount++;
             if (ret != RETCODE_OK.value) {
                 System.err.println("ERROR " + fta.topicCount +
                                    " write() returned " + ret);
             }
             try {
               Thread.sleep(100);
             } catch(InterruptedException ie) {
             }
         }
 
         while (matched.value.current_count != 0) {
           final int resulta = fdwa.get_publication_matched_status(matched);
           final int resultb = fdwb.get_publication_matched_status(matched);
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