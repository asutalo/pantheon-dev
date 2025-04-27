#!/bin/bash

# Convert HTML table to JSON format
awk '
BEGIN {
    print "["
    first = 1
}
{
    if ($0 ~ /<tr>/) {
        if (!first) {
            print ","
        }
        first = 0
        print "  {"
    }

    if ($0 ~ /<td class="npcname">/) {
        match($0, /<a href="([^"]+)"[^>]*>([^<]+)<\/a>/, arr)
        gsub(/"/, "\\\"", arr[1])
        gsub(/"/, "\\\"", arr[2])
        print "    \"name\": \"" arr[2] "\","
        print "    \"link\": \"" arr[1] "\","
    }
    if ($0 ~ /<td class="npcfamily">/) {
        match($0, /<td class="npcfamily">([^<]+)<\/td>/, arr)
        print "    \"family\": \"" arr[1] "\","
    }
    if ($0 ~ /<td class="npczone">/) {
        match($0, /<td class="npczone">([^<]+)<\/td>/, arr)
        print "    \"zone\": \"" arr[1] "\""
    }
    if ($0 ~ /<\/tr>/) {
        print "  }"
    }
}
END {
    print "]"
}
' scratch_5.html > output.json
