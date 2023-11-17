import java.util.Objects;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.DurabilityQosPolicyKind;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.HistoryQosPolicyKind;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ReliabilityQosPolicyKind;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;

/** 
* Simple example showing all Connext code in one place for readability.
*/
public class RecloserTopicPublisher extends Application implements AutoCloseable {
    static double startTime;
    static double endTime;
    // Usually one per application
    private DomainParticipant participant = null;
    
    private void runApplication() {
        // Start communicating in a domain
        participant = Objects.requireNonNull(
            DomainParticipantFactory.get_instance().create_participant(
                getDomainId(),
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
                null, // listener
                StatusKind.STATUS_MASK_NONE));

        // A Publisher allows an application to create one or more DataWriters
        Publisher publisher = Objects.requireNonNull(
            participant.create_publisher(
                DomainParticipant.PUBLISHER_QOS_DEFAULT,
                null, // listener
                StatusKind.STATUS_MASK_NONE));

        // Register the datatype to use when creating the Topic
        String typeName = RecloserTopicTypeSupport.get_type_name();
        RecloserTopicTypeSupport.register_type(participant, typeName);

        // Create a Topic with a name and a datatype
        Topic topic = Objects.requireNonNull(
            participant.create_topic(
                "Example RecloserTopic",
                typeName,
                DomainParticipant.TOPIC_QOS_DEFAULT,
                null, // listener
                StatusKind.STATUS_MASK_NONE));
        DataWriterQos dwqos = new DataWriterQos();
        participant.get_default_datawriter_qos(dwqos);

        dwqos.history.kind = HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;
        dwqos.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        dwqos.durability.kind = DurabilityQosPolicyKind.PERSISTENT_DURABILITY_QOS;
        // This DataWriter writes data on "Example RecloserTopic" Topic
        RecloserTopicDataWriter writer = (RecloserTopicDataWriter) Objects.requireNonNull(
            publisher.create_datawriter(
                topic,
                //Publisher.DATAWRITER_QOS_DEFAULT,
                dwqos,
                null, // listener
                StatusKind.STATUS_MASK_NONE));

        // Create data sample for writing
        RecloserTopic data = new RecloserTopic();
        StatusCondition sc = writer.get_statuscondition();
        WaitSet waitset = new WaitSet();
        waitset.attach_condition(sc);
        ConditionSeq activeConditions = new ConditionSeq();
        final Duration_t waitTimeout = new Duration_t(10, 0);
        boolean is_cond_triggered = false;
        String message = String.format("%-" + 9995 + "s", "00");
        data.r.description=message;
        data.r.aliasName="0";
        data.r.mRID="0";
        data.r.name="0";
        try {
            waitset.wait(activeConditions, waitTimeout);
            for (int i = 0; i < activeConditions.size(); ++i) {
                if (activeConditions.get(i) == sc) {
                    System.out.println("Triggered!");
                    is_cond_triggered = true;
                }
            }
            if (is_cond_triggered) {
                double all_time = 0;
                //startTime = System.currentTimeMillis();
                for (int samplesWritten = 0; !isShutdownRequested()
                && samplesWritten < getMaxSampleCount(); samplesWritten++) {
                    // Modify the data to be written here
                    // data.topicCount = samplesWritten;
                    //System.out.println("Writing RecloserTopic, count " + samplesWritten);
                    double startTime = System.currentTimeMillis();
                    writer.write(data, InstanceHandle_t.HANDLE_NIL);
                    double endTime = System.currentTimeMillis();
                    double RTT = endTime - startTime;
                    System.out.println("Data per RTT : " + RTT);

                    all_time += RTT;
                    /*try {
                        final long sendPeriodMillis = 100; // 0.1 second
                        Thread.sleep(sendPeriodMillis);
                    } catch (InterruptedException ix) {
                        System.err.println("INTERRUPTED");
                        break;
                    }*/
                }
                //endTime=System.currentTimeMillis();
                System.out.printf("Mean RTT : %.4f\n",all_time / 100);
            }
        } catch (RETCODE_TIMEOUT timed_out) {
            System.out.println("Wait Timted Out!! None of the conditions was triggered!");
        } catch (RETCODE_ERROR ex) {
            throw ex;
        }
        waitset.delete();
        waitset = null;
    }

    @Override
    public void close() {
        // Delete all entities (DataWriter, Topic, Publisher, DomainParticipant)
        if (participant != null) {
            participant.delete_contained_entities();

            DomainParticipantFactory.get_instance()
            .delete_participant(participant);
        }
    }

    public static void main(String[] args) {
        // Create example and run: Uses try-with-resources,
        // publisherApplication.close() automatically called
        try (RecloserTopicPublisher publisherApplication = new RecloserTopicPublisher()) {
            publisherApplication.parseArguments(args);
            publisherApplication.addShutdownHook();
            publisherApplication.runApplication();
        }

        // Releases the memory used by the participant factory. Optional at application exit.
        DomainParticipantFactory.finalize_instance();
        double latency = endTime - startTime;
        System.out.printf("Lantency : %.3f\n\n", latency/100);
    }
}