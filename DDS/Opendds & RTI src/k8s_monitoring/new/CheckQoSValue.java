import DDS.*;

public class CheckQoSValue {
    public String checkQoS(DataReaderQosHolder qosh) {
        String[] history_qos_array = {"KEEP_LAST", "KEEP_ALL"};
        String[] reliability_qos_array = {"BEST_EFFORT", "RELIABLE"};
        String[] liveliness_qos_array = {"AUTOMATIC", "MANUAL_BY_NODE", "MANUAL_BY"};
        String[] durability_qos_array = {"VOLATILE","LOCAL_DURABILITY", "TRANSIENT_DURABILITY", "PERSISTENT"};
        String[] ownership_qos_array = {"SHARED", "EXCLUSIVE"};

        int value1 = qosh.value.history.kind.value();
        int value2 = qosh.value.reliability.kind.value();
        int value3 = qosh.value.liveliness.kind.value();
        int value4 = qosh.value.durability.kind.value();
        int value5 = qosh.value.ownership.kind.value();

        String[] qosValuesToWrite = new String[5];
        int count = 0;

        if (value1 != 0) {
            qosValuesToWrite[count] = history_qos_array[value1];
            count++;
        }

        if (value2 != 0) {
            qosValuesToWrite[count] = reliability_qos_array[value2];
            count++;
        }

        if (value3 != 0) {
            qosValuesToWrite[count] = liveliness_qos_array[value3];
            count++;
        }

        if (value4 != 0) {
            qosValuesToWrite[count] = durability_qos_array[value4];
            count++;
        }

        if (value5 != 0) {
            qosValuesToWrite[count] = ownership_qos_array[value5];
        }

        String joinedQosValues = String.join(",", qosValuesToWrite);

        return joinedQosValues;
    }
}
