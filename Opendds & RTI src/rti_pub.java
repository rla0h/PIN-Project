import java.util.Objects;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.infrastructure.HistoryQosPolicyKind;
import com.rti.dds.infrastructure.ReliabilityQosPolicyKind;
import com.rti.dds.infrastructure.StatusKind;
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

        String message = String.format("%-" + 39995 + "s", "00");
        data.r.description=message;
        data.r.aliasName="0";
        data.r.mRID="0";
        data.r.name="0";
        startTime = System.currentTimeMillis();
        for (int samplesWritten = 0; !isShutdownRequested()
        && samplesWritten < getMaxSampleCount(); samplesWritten++) {
            // Modify the data to be written here
            //data.topicCount = samplesWritten;
            //System.out.println("Writing RecloserTopic, count " + samplesWritten);
            writer.write(data, InstanceHandle_t.HANDLE_NIL);
            try {
                final long sendPeriodMillis = 100; // 0.1 second
                Thread.sleep(sendPeriodMillis);
            } catch (InterruptedException ix) {
                System.err.println("INTERRUPTED");
                break;
            }
        }
        endTime=System.currentTimeMillis();
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