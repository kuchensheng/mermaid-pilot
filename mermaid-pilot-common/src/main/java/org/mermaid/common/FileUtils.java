package org.mermaid.common;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static String getFileContent(String filePath) throws IOException {
        Stream<String> stringStream = Files.lines(Paths.get(filePath), Charset.defaultCharset())
                .flatMap(line -> Arrays.stream(line.split(" ")));
        List<String> collect = stringStream.collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        collect.forEach(item ->sb.append(item));
        return sb.toString();

    }
}
