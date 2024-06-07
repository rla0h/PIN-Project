package Converter;

import java.io.BufferedReader;
import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.google.gson.*;
public class CommonConvert {
    static Map<String,Map> common_jsonMap = new LinkedHashMap<>();
    static Set<String> definedGroupNames = new HashSet<>();

    public void extractTypedef(String filename) {
        String currentGroupName;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("typedef long")) {
                    String[] parts = line.split(" ");
                    if (parts.length >= 3) {
                        currentGroupName = parts[2].replaceAll(";", "").trim();
                        common_jsonMap.putIfAbsent(currentGroupName, new LinkedHashMap<>());
                        definedGroupNames.add(currentGroupName);
                    }
                }

                if (line.startsWith("const long")) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim().split(" ")[2];
                        String valueString = parts[1].replaceAll(";", "").trim();
                        long value = Long.parseLong(valueString);

                        String[] keyParts = key.split("_");
                        String groupName = "Common";

                        if (keyParts.length > 1) {
                            String possibleGroupName = keyParts[0];
                            if (definedGroupNames.contains(possibleGroupName)) {
                                groupName = possibleGroupName;
                            }
                        }

                        common_jsonMap.computeIfAbsent(groupName, k -> new LinkedHashMap<>()).put(key, String.valueOf(value));
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
    public void convert_CommonJson_File () {
        try (FileWriter writer = new FileWriter("Common.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(common_jsonMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}