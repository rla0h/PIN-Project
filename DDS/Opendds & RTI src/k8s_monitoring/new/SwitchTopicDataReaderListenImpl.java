/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

import DDS.*;
import Topology.*;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

public class SwitchTopicDataReaderListenImpl extends DDS._DataReaderListenerLocalBase {
    //protected JDBCAdaptor JDBCSwitchAdaptor = null;
    //protected InfluxAdaptor influxAdaptor = null;
    protected neo4j_Adaptor neo4jAdaptor = new neo4j_Adaptor();
    public synchronized void on_data_available(DataReader reader) {

//        if (JDBCSwitchAdaptor == null) {
//            JDBCSwitchAdaptor = new JDBCAdaptor();
//            JDBCSwitchAdaptor.createSwitch();
//        }
//        neo4jAdaptor.neo4j_connect();
//        String token = "QNVLjuNOvfoyiD-ulB7vtsoGSdWDEsCvOTixn4r_JVSBKv751MOseGojkzvqTSROctskIXCNx92M5dRYqI7PUA==";
//        String bucket = "pin";
//        String org = "pin";
//
//        InfluxDBClient client = InfluxDBClientFactory.create("http://10.244.3.22:8086", token.toCharArray());

        SwitchTopicDataReader std = SwitchTopicDataReaderHelper.narrow(reader);

        if (std == null) {
            System.err.println("ERROR: read: narrow failed.");
            return;
        }

        IdentifiedObject io = new IdentifiedObject();
        PowerSystemResource psr = new PowerSystemResource(io, (byte) 0);
        Equipment eq = new Equipment(psr, (byte) 0);
        ConductingEquipment ce = new ConductingEquipment(eq, (byte) 0);
        ActivePower ap = new ActivePower(0, 0, 0);
        SSwitch ss = new SSwitch(ce, false, false, ap);
        SwitchTopicHolder sh = new SwitchTopicHolder(new SwitchTopic(ss, 0));

        SampleInfoHolder sih = new SampleInfoHolder(new SampleInfo(0, 0, 0,
            new Time_t(), 0, 0, 0, 0, 0, 0, 0, false, 0));
        int status_std = std.take_next_sample(sh, sih);

        if (status_std == RETCODE_OK.value) {

            System.out.println("SampleInfo.sample_rank = "
                                + sih.value.sample_rank);
            System.out.println("SampleInfo.instance_state = "
                                + sih.value.instance_state);

            if (sih.value.valid_data) {
                String switch_topic = std.get_topicdescription().get_name().toString();
//                Point point = Point
//                        .measurement("topic")
//                        .addField("topic", switch_topic)
//                        .time(Instant.now(), WritePrecision.NS);
//                WriteApi writeApi = client.getWriteApi();
//                writeApi.writePoint(bucket, org, point);
                System.out.println("topicCount : " + sh.value.topicCount);
                System.out.println(switch_topic + " :");
                System.out.println("    description  = " + sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.description);
                System.out.println("         mRID = "
                        + sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.mRID);
                System.out.println("         name = "
                        + sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.name);
                System.out.println("         aliasName = "
                        + sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.aliasName);
                System.out.println(sh.value.s.conducting_equipment.dummy_prevent_empty_class_ConductingEquipment);
                System.out.println(sh.value.s.conducting_equipment.equipment_.dummy_prevent_empty_class_Equipment);
                System.out.println(sh.value.s.conducting_equipment.equipment_.power_system_resource.dummy_prevent_empty_class_PowerSystemResource);
                System.out.println("         normalOpen       = " + sh.value.s.normalOpen);
                System.out.println("         open      = " + sh.value.s.open);

//                try {
//                    neo4jAdaptor.neo4j_insert_switch(
//                            sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.description,
//                            sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.mRID,
//                            sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.name,
//                            sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.aliasName,
//                            sh.value.s.normalOpen,
//                            sh.value.s.open
//                    );
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//                JDBCSwitchAdaptor.insertSwitch(
//                        sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.description,
//                        sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.mRID,
//                        sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.name,
//                        sh.value.s.conducting_equipment.equipment_.power_system_resource.identified_object.aliasName,
//                        sh.value.s.conducting_equipment.dummy_prevent_empty_class_ConductingEquipment,
//                        sh.value.s.conducting_equipment.equipment_.dummy_prevent_empty_class_Equipment,
//                        sh.value.s.conducting_equipment.equipment_.power_system_resource.dummy_prevent_empty_class_PowerSystemResource,
//                        sh.value.s.normalOpen,
//                        sh.value.s.open
//                );
//                JDBCSwitchAdaptor = null;
            }
        } else if (status_std == RETCODE_NO_DATA.value) {
            System.err.println("ERROR: reader received DDS::RETCODE_NO_DATA!");
        } else {
            System.err.println("ERROR: read Message: Error: " + status_std);
        }
    }

    public void on_requested_deadline_missed(DataReader reader, RequestedDeadlineMissedStatus status) {
        System.err.println("DataReaderListenerImpl.on_requested_deadline_missed");
    }

    public void on_requested_incompatible_qos(DataReader reader, RequestedIncompatibleQosStatus status) {
        System.err.println("DataReaderListenerImpl.on_requested_incompatible_qos");
    }

    public void on_sample_rejected(DataReader reader, SampleRejectedStatus status) {
        System.err.println("DataReaderListenerImpl.on_sample_rejected");
    }

    public void on_liveliness_changed(DataReader reader, LivelinessChangedStatus status) {
        System.err.println("DataReaderListenerImpl.on_liveliness_changed");
    }

    public void on_subscription_matched(DataReader reader, SubscriptionMatchedStatus status) {
        System.err.println("DataReaderListenerImpl.on_subscription_matched");
    }

    public void on_sample_lost(DataReader reader, SampleLostStatus status) {
        System.err.println("DataReaderListenerImpl.on_sample_lost");
    }
}
