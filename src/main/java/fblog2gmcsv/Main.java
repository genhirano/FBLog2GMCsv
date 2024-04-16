package fblog2gmcsv;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void main(String[] args) throws FileNotFoundException {
        List<String> result = new ArrayList<>();

        String inputJsonFile = "";

        // parameter check
        if (1 > args.length) {
            inputJsonFile = "./data/your_posts__check_ins__photos_and_videos_1.json";
        } else {
            inputJsonFile = args[0];
        }

        // Read JSON file
        List<Map<String, Object>> dataList = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            dataList = objectMapper.readValue(
                    new File(inputJsonFile),
                    new TypeReference<List<Map<String, Object>>>() {
                    });
        } catch (IOException e) {
            System.out.println("InputFile Not Found!");
            System.out.println("usage: java -jar fblog2gmcsv.jar [input.json]");
            System.out
                    .println("ex.1) java -jar fblog2gmcsv.jar ./data/your_posts__check_ins__photos_and_videos_1.json");
            System.out.println(
                    "ex.2) java -jar fblog2gmcsv.jar ./data/your_posts__check_ins__photos_and_videos_1.json > output.csv");
            System.out.println();
            e.printStackTrace();
            System.exit(1);
        }

        // charactor convert lint1 to utf8
        listCharSetConv(dataList);

        for (Map<String, Object> map : dataList) {
            String post = "";
            final Map<String, String> place = new TreeMap<>();

            for (String key : map.keySet()) {

                if ("data".equals(key)) {

                    for (Map<String, Object> map2 : (ArrayList<Map<String, Object>>) map.get("data")) {

                        Object o = map2.get("post");
                        if (null == o) {
                            break;
                        }

                        post = map2.get("post").toString();
                    }

                } else if ("attachments".equals(key)) {
                    for (Map<String, Object> map2 : (ArrayList<Map<String, Object>>) map.get("attachments")) {
                        for (Map<String, Object> map3 : (ArrayList<Map<String, Object>>) map2.get("data")) {
                            if (null != map3.get("place")) {

                                Map<String, Object> map4 = (Map<String, Object>) map3.get("place");
                                if (null != map4.get("name")) {
                                    place.put("name", map4.getOrDefault("name", "").toString());

                                    if (map4.containsKey("coordinate")) {
                                        Map<String, Object> cooMap = (Map) map4.get("coordinate");
                                        place.put("latitude", cooMap.get("latitude").toString());
                                        place.put("longitude", cooMap.get("longitude").toString());
                                    } else {
                                        place.put("latitude", "");
                                        place.put("longitude", "");
                                    }

                                    place.put("address", map4.getOrDefault("address", "").toString());

                                }

                            }
                        }
                    }
                }

            }

            // create one line string
            String oneLineStr = "";
            if (0 < place.size() && !place.get("latitude").toString().trim().equals("")) {
                oneLineStr = place.get("name").replaceAll(",", " ")
                        + ","
                        + place.get("latitude")
                        + ","
                        + place.get("longitude")
                        + ","
                        + place.get("address").replaceAll(",", " ")
                        + ","
                        + post.replaceAll("\n", " ").replaceAll("\r", " ").replaceAll(",", " ");

                result.add(oneLineStr);
            }
        }

        // Title Line
        result.add(0, "name,lat,long,adress,message");

        final String outputFileName = "./output.csv";
        try {
            Path out = Paths.get(outputFileName);
            Files.write(out, result, Charset.forName("UTF-8"));
            System.out.println("SUCCESS!  Output File: " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Map<String, Object> mapCharSetConv(Map<String, Object> node) {
        node.forEach((key, value) -> {

            List<Class> c = Arrays.asList(value.getClass().getInterfaces());
            // java21以降は,HashMapではなく、SequencedMapが使われるようなので、ここで判定する
            Boolean isMap = false;
            for (Class cc : c) {
                if (cc.getName().contains("Map")) {
                    isMap = true;
                    break;
                }
            }

            if (c.contains(java.util.List.class)) { // 要素がリストの場合
                listCharSetConv((List) value);
            } else if (isMap) { // 要素がMapの場合
                ((Map) value).forEach((key2, Value2) -> {
                    mapCharSetConv((Map) value);
                });
            } else {
                if (java.lang.String.class == value.getClass()) {
                    node.put(key, convlatin1toUTF8(value.toString())); // この要素が文字列要素なら変換
                }
            }

        });

        return node;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<Map<String, Object>> listCharSetConv(List<Map<String, Object>> node) {
        for (Map<String, Object> m : node) {

            m.forEach((key, value) -> {
                List<Class> c = Arrays.asList(value.getClass().getInterfaces());

                // java21以降は,HashMapではなく、SequencedMapが使われるようなので、ここで判定する
                Boolean isMap = false;
                for (Class cc : c) {
                    if (cc.getName().contains("Map")) {
                        isMap = true;
                        break;
                    }
                }

                if (c.contains(java.util.List.class)) { // この要素がリストの場合
                    listCharSetConv((List) value);
                } else if (isMap) { // この要素がMapの場合
                    mapCharSetConv((Map) value);
                } else {
                    if (java.lang.String.class == value.getClass()) {
                        m.put(key, convlatin1toUTF8(value.toString())); // この要素が文字列要素なら変換
                    }
                }
            });
        }
        return node;
    }

    private static String convlatin1toUTF8(String latin1String) {
        try {
            // Latin-1 to UTF-8 Convert
            byte[] latin1Bytes = latin1String.getBytes("ISO-8859-1");
            String utf8String = new String(latin1Bytes, "UTF-8");
            return utf8String;
        } catch (Exception e) {
            throw new Error("Can't Convert : " + latin1String);
        }
    }

}