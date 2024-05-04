/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

import DDS.*;
import OpenDDS.DCPS.*;
import OpenDDS.DCPS.transport.*;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.omg.CORBA.StringSeqHolder;

import Topology.*;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

public class ACLineSegmentTopicDataReaderListenerImpl extends DDS._DataReaderListenerLocalBase {
    //protected JDBCAdaptor JDBCACLSAdaptor = null;
    //protected neo4j_Adaptor neo4jAdaptor = new neo4j_Adaptor();
    public synchronized void on_data_available(DDS.DataReader reader) {

//        if (JDBCACLSAdaptor == null) {
//            JDBCACLSAdaptor = new JDBCAdaptor();
//            JDBCACLSAdaptor.createAcls();
//        }
//
//        neo4jAdaptor.neo4j_connect();

        String token = "QNVLjuNOvfoyiD-ulB7vtsoGSdWDEsCvOTixn4r_JVSBKv751MOseGojkzvqTSROctskIXCNx92M5dRYqI7PUA==";
        String bucket = "pin";
        String org = "pin";

        //InfluxDBClient client = InfluxDBClientFactory.create("http://10.244.3.22:8086", token.toCharArray());
        ACLineSegmentTopicDataReader act = ACLineSegmentTopicDataReaderHelper.narrow(reader);

        if (act == null) {
            System.err.println("ERROR: read: narrow failed.");
            return;
        }
        IdentifiedObject io = new IdentifiedObject();
        PowerSystemResource psr = new PowerSystemResource(io, (byte) 0);
        Equipment eq = new Equipment(psr, (byte) 0);
        ConductingEquipment ce = new ConductingEquipment(eq, (byte) 0);

        ActivePower length = new ActivePower(0, 0, 0);
        ActivePower r = new ActivePower(0, 0, 0);
        ActivePower r0 = new ActivePower(0, 0, 0);
        ActivePower x = new ActivePower(0, 0, 0);
        ActivePower x0 = new ActivePower(0, 0, 0);

        Conductor c = new Conductor(ce, length);
        ACLineSegment ac = new ACLineSegment(c, r, r0, x, x0);
        ACLineSegmentTopicHolder ah = new ACLineSegmentTopicHolder(new ACLineSegmentTopic(ac, 0));


        SampleInfoHolder sih = new SampleInfoHolder(new SampleInfo(0, 0, 0,
            new DDS.Time_t(), 0, 0, 0, 0, 0, 0, 0, false, 0));
        int status_acl = act.take_next_sample(ah, sih);

        if (status_acl == RETCODE_OK.value) {
            System.out.println("SampleInfo.sample_rank = "
                                + sih.value.sample_rank);
            System.out.println("SampleInfo.instance_state = "
                                + sih.value.instance_state);

            if (sih.value.valid_data) {
                String acl_topic = act.get_topicdescription().get_name().toString();
                Point point = Point
                        .measurement("topic")
                        .addField("topic", acl_topic)
                        .time(Instant.now(), WritePrecision.NS);
//                WriteApi writeApi = client.getWriteApi();
//                writeApi.writePoint(bucket, org, point);
                System.out.println("topicCount : " + ah.value.topicCount);
                System.out.println(acl_topic + " :");
                System.out.println("name : " + ah.value.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.name);
                System.out.println("descriptino : " + ah.value.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.description);
                System.out.println("length :");
                System.out.println("    mulitplier :");
                System.out.println("        UnitMulitplier :" + ah.value.acls.conductor_.length_.multiplier);
                System.out.println("    unit :");
                System.out.println("        UnitSymbol :" + ah.value.acls.conductor_.length_.unit);
                System.out.println("            value :" + ah.value.acls.conductor_.length_.value);
                System.out.println("r:");
                System.out.println("    multiplier:");
                System.out.println("        UnitMultiplier:" + ah.value.acls.r.multiplier);
                System.out.println("    unit:");
                System.out.println("        UnitSymbol:" + ah.value.acls.r.unit);
                System.out.println("    value:" + ah.value.acls.r.value);
                System.out.println("r0:");
                System.out.println("    multiplier:");
                System.out.println("        UnitMultiplier:" + ah.value.acls.r0.multiplier);
                System.out.println("    unit:");
                System.out.println("        UnitSymbol:" + ah.value.acls.r0.unit);
                System.out.println("    value:" + ah.value.acls.r0.value);
                System.out.println("x:");
                System.out.println("    multiplier:");
                System.out.println("        UnitMultiplier:" + ah.value.acls.x.multiplier);
                System.out.println("    unit:");
                System.out.println("        UnitSymbol:" + ah.value.acls.x.unit);
                System.out.println("    value:" + ah.value.acls.x.value);
                System.out.println("x0:");
                System.out.println("    multiplier:");
                System.out.println("        UnitMultiplier:" + ah.value.acls.x0.multiplier);
                System.out.println("    unit:");
                System.out.println("        UnitSymbol:" + ah.value.acls.x0.unit);
                System.out.println("    value:" + ah.value.acls.x0.value);

//                try {
//                    neo4jAdaptor.neo4j_insert_acl(
//                            (long) ah.value.acls.conductor_.length_.unit,
//                            (long) ah.value.acls.conductor_.length_.multiplier,
//                            (float) ah.value.acls.conductor_.length_.value,
//                            (long) ah.value.acls.r.unit,
//                            (long) ah.value.acls.r.multiplier,
//                            (float) ah.value.acls.r.value,
//                            (long) ah.value.acls.r0.unit,
//                            (long) ah.value.acls.r0.multiplier,
//                            (float) ah.value.acls.r0.value,
//                            (long) ah.value.acls.x.unit,
//                            (long) ah.value.acls.x.multiplier,
//                            (float) ah.value.acls.x.value,
//                            (long) ah.value.acls.x0.unit,
//                            (long) ah.value.acls.x0.multiplier,
//                            (float) ah.value.acls.x0.value
//                    );
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//                JDBCACLSAdaptor.insertAcls(
//                        ah.value.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.description,
//                        ah.value.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.mRID,
//                        ah.value.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.name,
//                        ah.value.acls.conductor_.conducting_equipment.equipment_.power_system_resource.identified_object.aliasName,
//                        (long) ah.value.acls.conductor_.length_.unit,
//                        (long) ah.value.acls.conductor_.length_.multiplier,
//                        (float) ah.value.acls.conductor_.length_.value,
//                        (long) ah.value.acls.r.unit,
//                        (long) ah.value.acls.r.multiplier,
//                        (float) ah.value.acls.r.value,
//                        (long) ah.value.acls.r0.unit,
//                        (long) ah.value.acls.r0.multiplier,
//                        (float) ah.value.acls.r0.value,
//                        (long) ah.value.acls.x.unit,
//                        (long) ah.value.acls.x.multiplier,
//                        (float) ah.value.acls.x.value,
//                        (long) ah.value.acls.x0.unit,
//                        (long) ah.value.acls.x0.multiplier,
//                        (float) ah.value.acls.x0.value
//                );
            }
        } else if (status_acl == RETCODE_NO_DATA.value) {
            System.err.println("ERROR: reader received DDS::RETCODE_NO_DATA!");
        } else {
            System.err.println("ERROR: read Message: Error: " + status_acl);
        }
        //JDBCACLSAdaptor = null;
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

}
