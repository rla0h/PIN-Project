package Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TopicConvert {
    static Map<String, Map> topic_jsonMap = new LinkedHashMap<>();
    Set<String> userTopics;
    Set<String> allTopics = new HashSet<String>();
    static Set<String> structGroup = new HashSet<>();
    static Set<String> typedef_name = new HashSet<>();
    static Set<String> typedef_structGroup = new HashSet<>();
    List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();

    public TopicConvert(String filename){
        save_AllTopics(filename);
        this.userTopics = allTopics;
    }

    public TopicConvert(String[] args, String filename) {
        save_AllTopics(filename);
        check_in_topic(args);
    }
    public int check_in_topic(String[] topics) {
        for(String topic : topics) {
            if(!allTopics.contains(topic)) {
                return 1;
            }
        }
        this.userTopics = new HashSet<>(Arrays.asList(Arrays.copyOfRange(topics, 0, topics.length)));
        return 0;
    }
    public void save_AllTopics(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("@topic")) {
                    if ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("struct")) {
                            String[] topicName = line.split(" ");
                            if (topicName.length >= 2) {
                                String topic = topicName[1];
                                allTopics.add(topic);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void save_typedef_struct (String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("struct")) {
                    String[] structName = line.split(" ");
                    if (structName.length >= 3) {
                        structGroup.add(structName[1]);
                    }
                } else if (line.startsWith("typedef")) {
                    String[] structName = line.split(" ");
                    if (structName.length >= 3) {
                        if (!structName[1].equals("long")) {
                            String last_struct = structName[structName.length - 1].replaceAll(";$", "").trim();
                            typedef_name.add(structName[1]);
                            structGroup.add(last_struct);
                            typedef_structGroup.add(last_struct);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inherited_process (String filename, List<Object> struct_list, Set<String> typedef_name, Set<String> typedef_structGroup, Set<String> structGroup, Map<String, Object> inherit_structMap, List<Map<String, Object>> listMap) {
        try (BufferedReader br_2 = new BufferedReader(new FileReader(filename))) {
            String line_2;
            int break_code = 0;
            int j = 0;
            int i = 0;
            br_2.mark(100000);
            while ((line_2 = br_2.readLine()) != null) {
                if (break_code > 0)
                    break;
                line_2 = line_2.trim();
                if (line_2.startsWith("struct") || line_2.startsWith("typedef")) {
                    String[] structName = line_2.split(" ");
                    structName[2] = structName[2].replaceAll(";$", "").trim();
                    if (structName.length >= 3) {
                        if (structName[1].equals(struct_list.get(i)) || structName[2].equals(struct_list.get(i))) {
                            if (typedef_name.contains(structName[1]) && typedef_structGroup.contains(structName[2])) {
                                try (BufferedReader br_3 = new BufferedReader(new FileReader(filename))) {
                                    String line_3;
                                    while ((line_3 = br_3.readLine()) != null) {
                                        line_3 = line_3.trim();
                                        if (line_3.startsWith("struct")) {
                                            String[] structName_2 = line_3.split(" ");
                                            if (structName_2.length == 3 && typedef_name.contains(structName_2[1]) && structName[1].equals(structName_2[1])) {
                                                while ((line_3 = br_3.readLine()) != null) {
                                                    line_3 = line_3.trim();
                                                    if (line_3.equals("};")) {
                                                        break;
                                                    }
                                                    String[] inheritStruct = line_3.split(" ");
                                                    String inherit_first = inheritStruct[0]; // Reactance
                                                    String inherit_second = inheritStruct[1];
                                                    String inherit_last; // r0;
                                                    if (inheritStruct.length >= 3) {
                                                        if (inherit_second.equals("boolean") || inherit_second.equals("float") || inherit_second.equals("int") || inherit_second.equals("long") || inherit_second.equals("double") || inherit_second.equals("octet")) {
                                                            inherit_last = inheritStruct[2];
                                                        } else {
                                                            inherit_last = inherit_second;
                                                        }
                                                    } else {
                                                        inherit_last = inheritStruct[inheritStruct.length - 1];
                                                    }
                                                    inherit_last = inherit_last.replaceAll(";$", "").trim();
                                                    if (structGroup.contains(inherit_first)) {
                                                        struct_list.add(inherit_first);
                                                        struct_list.add(inherit_last);

                                                        inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), new LinkedHashMap<>());

                                                    } else {
                                                        if (inherit_first.equals("unsigned")) {
                                                            if (inherit_second.equals("boolean")) {
                                                                struct_list.add(inherit_second);
                                                                struct_list.add(inherit_last);
                                                                inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), "false");
                                                            } else if (inherit_second.equals("float") || inherit_second.equals("int") || inherit_second.equals("long") || inherit_second.equals("double") || inherit_second.equals("octet")) {
                                                                struct_list.add(inherit_second);
                                                                struct_list.add(inherit_last);
                                                                inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), "0");
                                                            } else {
                                                                struct_list.add(inherit_second);
                                                                struct_list.add(inherit_last);
                                                                inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), "");
                                                            }
                                                        } else {
                                                            if (inherit_first.equals("boolean")) {
                                                                struct_list.add(inherit_first);
                                                                struct_list.add(inherit_last);
                                                                inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), "false");
                                                            } else if (inherit_first.equals("float") || inherit_first.equals("int") || inherit_first.equals("long") || inherit_first.equals("double") || inherit_first.equals("octet")) {
                                                                struct_list.add(inherit_first);
                                                                struct_list.add(inherit_last);
                                                                inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), "0");
                                                            } else {
                                                                struct_list.add(inherit_first);
                                                                struct_list.add(inherit_last);
                                                                inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), "");
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (structGroup.contains(structName[1]) && !typedef_structGroup.contains(structName[2])) {
                                while ((line_2 = br_2.readLine()) != null) {
                                    line_2 = line_2.trim();
                                    if (line_2.equals("};")) {
                                        break;
                                    }
                                    if (line_2.startsWith("//")) {
                                        continue;
                                    }
                                    String[] inheritStruct = line_2.split(" ");
                                    String inherit_first = inheritStruct[0]; // Reactance
                                    String inherit_last; // r0;
                                    if (inheritStruct.length >= 3) {
                                        if(inheritStruct[1].equals("float") || inheritStruct[1].equals("int") || inheritStruct[1].equals("long") || inheritStruct[1].equals("double") || inheritStruct[1].equals("octet")) {
                                            inherit_last = inheritStruct[2];
                                        }
                                        else {
                                            inherit_last = inheritStruct[1];
                                        }
                                    } else {
                                        inherit_last = inheritStruct[inheritStruct.length - 1];
                                    }
                                    inherit_last = inherit_last.replaceAll(";$", "").trim();
                                    if (structGroup.contains(inherit_first)) {
                                        struct_list.add(inherit_first);
                                        struct_list.add(inherit_last);

                                        inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), new LinkedHashMap<>());

                                    } else if (!inherit_first.equals("typedef")) {
                                        if (inherit_first.equals("boolean")) {
                                            struct_list.add(inherit_first);
                                            struct_list.add(inherit_last);
                                            inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), "false");
                                        } else if (inherit_first.equals("float") || inherit_first.equals("int") || inherit_first.equals("long") || inherit_first.equals("double") || inherit_first.equals("octet")) {
                                            struct_list.add(inherit_first);
                                            struct_list.add(inherit_last);
                                            inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), "0");
                                        } else {
                                            struct_list.add(inherit_first);
                                            struct_list.add(inherit_last);
                                            inherit_structMap.put((String) struct_list.get(struct_list.size() - 1), "");
                                        }
                                    }
                                }
                            }
                            br_2.reset();
                        }
                        if (!inherit_structMap.isEmpty()) {
                            listMap.add(inherit_structMap);
                            inherit_structMap = new LinkedHashMap<>();
                            if (j == 0) {
                                i = 2;
                                j++;
                            } else {
                                i += 2;
                                while (!structGroup.contains(struct_list.get(i))) {
                                    i++;
                                    if (i == struct_list.size()) {
                                        break_code++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else if (line_2.startsWith("@topic")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void struct_process(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("@topic")) {
                    if ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("struct")) {
                            String[] topicName = line.split(" ");
                            if (topicName.length >= 2) {
                                String topic = topicName[1];
                                if (userTopics.contains(topic)) {
                                    Map<String, Object> structMap = new LinkedHashMap<>();
                                    List<Object> struct_list = new LinkedList<>();

                                    while ((line = br.readLine()) != null) {
                                        line = line.trim();
                                        if (line.equals("};")) {
                                            break;
                                        }
                                        String[] structSplit = line.split(" ");
                                        if (structSplit.length < 3) {
                                            String first = structSplit[0];
                                            String last = structSplit[structSplit.length - 1];
                                            last = last.replaceAll(";$", "").trim();
                                            struct_list.add(first);
                                            struct_list.add(last);
                                            if (structGroup.contains(structSplit[0])) {
                                                // inherit struct name
                                                Map<String, Object> inherit_structMap = new LinkedHashMap<>();
                                                
                                                inherited_process(filename, struct_list, typedef_name, typedef_structGroup, structGroup,inherit_structMap, listMap);

                                                if (listMap.size() >= 1) {
                                                    int buf = 0;
                                                    for (int k = 0; k < listMap.size(); k++) {
                                                        int l;
                                                        Map<String, Object> firstMap = listMap.get(k);
                                                        List<String> keysList = new ArrayList<>(firstMap.keySet());
                                                        int size = 0;
                                                        if (!listMap.get(k).containsValue("") && !listMap.get(k).containsValue("false") && !listMap.get(k).containsValue("0")) {
                                                            for (l = buf; l < listMap.size(); l++) {
                                                                listMap.get(k).put(keysList.get(size), listMap.get(l + 1));
                                                                if (size == keysList.size() - 1)
                                                                    break;
                                                                size++;
                                                            }
                                                            l++;
                                                            buf = 0;
                                                            buf += l;
                                                        } else {
                                                            int cnt = 0;
                                                            int ck = 0;
                                                            int in = 0;
                                                            for (l = buf; l < listMap.size(); l++) {
                                                                int index = struct_list.indexOf(keysList.get(size));
                                                                if (structGroup.contains(struct_list.get(index - 1))) {
                                                                    listMap.get(k).put(keysList.get(size), listMap.get(l - cnt + 1));
                                                                    size++;
                                                                    in++;
                                                                } else {
                                                                    size++;
                                                                    cnt++;
                                                                }
                                                                if (size == keysList.size()) {
                                                                    ck++;
                                                                    break;
                                                                }
                                                            }
                                                            if (ck == 0 && in == 0) {
                                                                l--;
                                                            }
                                                            l -= cnt;
                                                            l++;
                                                            buf = 0;
                                                            buf += l;
                                                        }
                                                    }
                                                    // listMap 1~limtMap.size() delete Because it's connected to index 0
                                                    for (int i = listMap.size() - 1; i >= 1; i--) {
                                                        listMap.remove(i);
                                                    }
                                                    if (!listMap.isEmpty()) {
                                                        List<Map<String, Object>> listMapCopy = new ArrayList<>();
                                                        for (Map<String, Object> item : listMap) {
                                                            listMapCopy.add(new HashMap<>(item));
                                                        }
                                                        structMap.put(last, listMapCopy);
                                                        listMap.clear();
                                                    }
                                                }
                                            } else {
                                                // String || int || long || float
                                                if (structSplit[0].equals("string")) {
                                                    structMap.put(last, "");
                                                } else {
                                                    structMap.put(last, "0");
                                                }
                                            }
                                        } else {
                                            //topicCount
                                            String last = structSplit[structSplit.length - 1];
                                            last = last.replaceAll(";$", "").trim();
                                            structMap.put(last, "0");
                                        }
                                        topic_jsonMap.put(topic, structMap);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Can't convert number");
            e.printStackTrace();
        }
    }
    public void ConvertJsonFile (){
        try (FileWriter writer = new FileWriter("/home/pin/jsonconvert/result/result.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(topic_jsonMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}