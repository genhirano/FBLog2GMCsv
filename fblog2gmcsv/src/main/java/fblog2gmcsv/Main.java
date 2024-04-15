package fblog2gmcsv;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {


    private static void convlatin1toUTF8(String latin1String) {
        try {
            // Latin-1からUTF-8に変換
            byte[] latin1Bytes = latin1String.getBytes("ISO-8859-1");
            String utf8String = new String(latin1Bytes, "UTF-8");

            System.out.println("UTF-8 String: " + utf8String);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {

        List<String> result = new ArrayList<>();

        /*
         * try {
         * // Latin-1からUTF-8に変換
         * byte[] latin1Bytes = latin1String.getBytes("ISO-8859-1");
         * String utf8String = new String(latin1Bytes, "UTF-8");
         * 
         * System.out.println("UTF-8 String: " + utf8String);
         * } catch (UnsupportedEncodingException e) {
         * e.printStackTrace();
         * }
         */

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> dataList = objectMapper.readValue(
                new File("./data/your_posts__check_ins__photos_and_videos_1.json"),
                new TypeReference<List<Map<String, Object>>>() {
                });

        Set<String> set = new HashSet<String>();



                


        System.out.println(dataList.size());
        for (Map<String, Object> map : dataList) {
            for (String key : map.keySet()) {
                //System.out.println(map.get(key).getClass().getName());
                set.add(map.get(key).getClass().getName());
            }
        }   
        System.out.println(set);



        // パースしたデータを使って何か処理を行う
        for (Map<String, Object> map : dataList) {
            String post = "";
            Map<String, String> place = new TreeMap<>();

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
                } else {
                    // System.out.println(key);
                }
            }

            String tmp = "";
            if (0 < place.size() && !place.get("latitude").toString().equals("")) {
                tmp = place.get("name").replaceAll(",", "_")
                        + ","
                        + place.get("latitude")
                        + ","
                        + place.get("longitude")
                        + ","
                        + place.get("address")
                        + ","
                        + post.replaceAll("\n", " ").replaceAll("\r", " ");
                result.add(tmp);
            }
        }

        result.add(0, "name,lat,long,adress,message");
        ;
        Path out = Paths.get("data/output.csv");
        
        Files.write(out, result, Charset.forName("UTF-8"));

    }

}