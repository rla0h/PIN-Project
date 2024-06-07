package Converter;

import java.util.*;
import java.io.File;
public class ConvertMain {
    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: java Converter.TopicConvert <IDL Filename> ");
            return;
        }
        String filename = args[0];

        File textFile = new File(filename);
        boolean check = textFile.exists();
        if (!check) {
            System.err.println("File Not Found!");
            return;
        }

//        CommonConvert cm = new CommonConvert();
//        cm.extractTypedef(filename);
//        cm.convert_CommonJson_File();
        Scanner sc = new Scanner(System.in);

        while(true) {
            System.out.println("**************************************");
            System.out.println("*                                    *");
            System.out.println("*      IDL to JSON Converter         *");
            System.out.println("*                                    *");
            System.out.println("**************************************\n");
            System.out.println("Please select an option to convert:");
            System.out.println("1. All (Convert ALL Topics)");
            System.out.println("2. User Topic (User's specific topic)");
            int choice = sc.nextInt();
            if (choice > 3 || choice < 1)
                System.out.println("Wrong Number !!");
            else if (choice == 1) {
                TopicConvert tc = new TopicConvert(filename);
                tc.save_typedef_struct(filename);
                tc.struct_process(filename);
                tc.ConvertJsonFile();
                System.out.println("Finish Convert !!");
                break;
            } else if (choice == 2) {
                sc.nextLine();
                System.out.println("Write 'Topic Name' to convert:");
                String input = sc.nextLine();
                String[] topics = input.split(" ");
                System.out.println("You have entered the following topics:");
                for (String topic : topics) {
                    System.out.print(topic + " ");
                }
                TopicConvert tc = new TopicConvert(topics, filename);
                if(tc.check_in_topic(topics)==0) {
                    tc.save_typedef_struct(filename);
                    tc.struct_process(filename);
                    tc.ConvertJsonFile();
                    System.out.println("Finish Convert !!");
                    break;
                } else {
                    System.out.println("IDL doesn't have your topics !!");
                    System.out.println("Try Again..");
                    System.out.println();
                    System.out.println();
                }
            } else {
                System.out.println("Exit..");
                break;
            }
        }

        //TopicConvert tc = new TopicConvert(args);

    }
}
