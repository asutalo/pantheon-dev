package com.eu.atit.pantheon.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

class Scratch {
    record Content(String name, String link, String family, String zone) {
    }

    record Member(String name, String link) {
    }

    record FamilyContent(String family, List<Member> members) {
    }

    record ZoneContent(String zone, List<FamilyContent> families) {
    }

    public static List<Content> readJsonFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), new TypeReference<>() {
        });
    }

    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\asuta\\Documents\\projects\\pantheon-dev\\misc\\output.json";
        List<Content> records = readJsonFile(path);

        Stream<ZoneContent> info = records.stream().map(record -> record.zone).distinct().map(zone -> new ZoneContent(zone, records.stream()
                .filter(record -> record.zone.equals(zone))
                .map(record -> record.family)
                .distinct()
                .map(family -> {
                    List<Member> members = records.stream()
                            .filter(record -> record.family.equals(family) && record.zone.equals(zone))
                            .map(record -> new Member(record.name, record.link))
                            .toList();
                    return new FamilyContent(family, members);
                })
                .toList()));

        String linkBase = "https://www.wow-petopia.com/";

//        try (PrintWriter writer = new PrintWriter("output.md", StandardCharsets.UTF_8)) {
//            info.forEach(zoneContent -> {
//                writer.println("- [ ] Zone: " + zoneContent.zone);
//                zoneContent.families.forEach(familyContent -> {
//                    writer.println("  - [ ] Family: " + familyContent.family);
//                    familyContent.members.forEach(member -> {
//                        writer.println("    - [ ] Member: " + member.name + " - " + linkBase + member.link);
//                    });
//                });
//            });
//        }

        try (PrintWriter writer = new PrintWriter("output.csv", StandardCharsets.UTF_8)) {
            writer.println("Zone,Family,Member,Link");
            info.forEach(zoneContent -> {
                zoneContent.families.forEach(familyContent -> {
                    familyContent.members.forEach(member -> {
                        writer.println(zoneContent.zone + "," + familyContent.family + "," + member.name + "," + linkBase + member.link);
                    });
                });
            });
        }
    }
}