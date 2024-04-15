package fblog2gmcsv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void main(String[] args) throws IOException {
        List<String> result = new ArrayList<>();


        String inputJsonFile = "";

        //parameter check
        if(1 > args.length){
            inputJsonFile = "./data/your_posts__check_ins__photos_and_videos_1.json";
        }else{
            inputJsonFile = args[0];
        }


        // Read JSON file
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> dataList = objectMapper.readValue(
                new File(inputJsonFile),
                new TypeReference<List<Map<String, Object>>>() {
                });

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

        // output for Console
        for (String s : result) {
            System.out.println(s);
        }

        // Path out = Paths.get("data/output.csv");
        // Files.write(out, result, Charset.forName("UTF-8"));

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Map<String, Object> mapCharSetConv(Map<String, Object> node) {
        node.forEach((key, value) -> {

            List<Class> c = Arrays.asList(value.getClass().getInterfaces());

            if (c.contains(java.util.List.class)) { // 要素がリストの場合
                listCharSetConv((List) value);
            } else if (c.contains(java.util.Map.class)) { // 要素がMapの場合
                ((Map) value).forEach((key2, Value2) -> {
                    mapCharSetConv((Map) value);
                });
            } else {
                if (java.lang.String.class == value.getClass()) {
                    node.put(key, convlatin1toUTF8(value.toString()));
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
                if (c.contains(java.util.List.class)) { // この要素がリストの場合
                    listCharSetConv((List) value);
                } else if (c.contains(java.util.Map.class)) {
                    mapCharSetConv((Map) value);
                } else {
                    if (java.lang.String.class == value.getClass()) {
                        m.put(key, convlatin1toUTF8(value.toString()));
                    }
                }
            });
        }
        return node;
    }

    private static String convlatin1toUTF8(String latin1String) {
        try {
            // Latin-1 to  UTF-8 Convert
            byte[] latin1Bytes = latin1String.getBytes("ISO-8859-1");
            String utf8String = new String(latin1Bytes, "UTF-8");
            return utf8String;
        } catch (Exception e) {
            throw new Error("Can't Convert : " + latin1String);
        }
    }

}