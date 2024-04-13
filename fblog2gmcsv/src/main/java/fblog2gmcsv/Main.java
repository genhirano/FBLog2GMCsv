package fblog2gmcsv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {


public static class Obj {
    public String name;
    public int value;

    public Obj(){
    }

    public Obj(String name, int value){
        this.name = name;
        this.value = value;
    }
}


        
    public static void main(String[] args) {

        Obj obj = new Obj("apple", 100);
        String json = getJSONFromObj(obj);
        Obj newObj = getObjFromJSON(json);       
        System.out.println(newObj.name); 

    }

        // JavaオブジェクトをJSONに変換
        private static String getJSONFromObj(Obj obj) {

            String json = null;
            ObjectMapper mapper = new ObjectMapper();
            try {
                json = mapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
    
            return json;
        }
    
        // JSONをJavaオブジェクトに変換
        private static Obj getObjFromJSON(String json) {
            ObjectMapper mapper = new ObjectMapper();
            Obj obj = null;
            try {
                obj = mapper.readValue(json, Obj.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        }
        
}