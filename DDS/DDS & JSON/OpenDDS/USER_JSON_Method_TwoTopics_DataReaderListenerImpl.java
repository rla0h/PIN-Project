/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

import DDS.*;
import OpenDDS.DCPS.*;
import OpenDDS.DCPS.transport.*;
import org.omg.CORBA.StringSeqHolder;
import org.omg.CORBA.StringHolder;
import PIN.*;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataReaderListenerImpl extends DDS._DataReaderListenerLocalBase {

    private int num_msgs = 0;

    private int expected_count = 80;

    private static final int N_EXPECTED = 80;
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

        PinJsonDataReader mdr = PinJsonDataReaderHelper.narrow(reader);
        if (mdr == null) {
            System.err.println("ERROR: read: narrow failed.");
            return;
        }

        PinJsonHolder mh = new PinJsonHolder(new PinJson());
        // json
        PinJsonTypeSupport ts = new PinJsonTypeSupportImpl();
        RepresentationFormat format =ts.make_format(JSON_DATA_REPRESENTATION.value);
        StringHolder holder = new StringHolder();
        
        SampleInfoHolder sih = new SampleInfoHolder(new SampleInfo(0, 0, 0,
            new DDS.Time_t(), 0, 0, 0, 0, 0, 0, 0, false, 0));
        int status = mdr.take_next_sample(mh, sih);

        if (status == RETCODE_OK.value) {
            if (sih.value.valid_data) {
                String prefix = "";
                boolean invalid_count = false;
                if (mh.value.count < 0 || mh.value.count >= counts.size()) {
                    invalid_count = true;
                }
                else {
                    if (counts.get(mh.value.count) == false){
                        counts.set(mh.value.count, true);
                    }
                    else {
                        prefix = "ERROR: Repeat ";
                    }
                }
                System.out.println(prefix + "count    = " + mh.value.count);
                //System.out.println("version       = " + mh.value.version);
                
                // System.out.println(mh.value.body);
                ts.encode_to_string(mh.value, holder, format);
                // System.out.println(holder.value);
                JSONObject jObject = new JSONObject(holder.value);
                String bodyString = jObject.getString("body");
                JSONObject body = new JSONObject(bodyString);
                
                
                if(mh.value.count<40) {
                    JSONObject io = body.getJSONObject("io");
                    System.out.println("=====ACDCTerminal=====");
                    System.out.println(io.toString());
                }
                System.out.printf("\n\n");
                if(mh.value.count >=40){
                    JSONObject r = body.getJSONObject("r");
                    JSONObject r0 = body.getJSONObject("r0");
                    JSONObject x = body.getJSONObject("x");
                    JSONObject x0 = body.getJSONObject("x0");
                    JSONObject c = body.getJSONObject("c");
                    System.out.println("=====ACLineSegment=====");
                    System.out.println(r.toString());
                    System.out.println(r0.toString());
                    System.out.println(x.toString());
                    System.out.println(x0.toString());
                    System.out.println(c.toString());
                }           

                if (invalid_count == true) {
                    System.out.println("ERROR: Invalid PinJson.count (" + mh.value.count + ")");
                }
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
            System.err.println("ERROR: read PinJson: Error: " + status);
        }

        if (mh.value.count + 1 == expected_count) {
            gc.set_trigger_value(true);
        }
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

    public void report_validity() {
        int count = 0;
        int missed_counts = 0;
        for (Boolean val : counts) {
            if (val == false)
                ++missed_counts;
        }
        if (missed_counts > N_EXPECTED - expected_count) {
            System.out.println("ERROR: Missing " + missed_counts + " PinJsons");
        }
    }
}
 