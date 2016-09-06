package morph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DaumApi {

   public String excuteDaumAPI(String query)
   {
      StringBuffer json = new StringBuffer();

      URLConnection urlC = null;
      URL url = null;

      String surl = "https://apis.daum.net/grammar-checker/v1/check.json";
      String apikey = "5fc8ef4e59fdb7e66b3754271e7ead55 ";

      try 
      {
         surl = surl + "?query=" + URLEncoder.encode(query,"UTF-8");

         url = new URL(surl);
         urlC = url.openConnection();

         urlC.setRequestProperty("appkey", apikey);
         urlC.setRequestProperty("query", "abcd");

         BufferedReader rd = new BufferedReader(new InputStreamReader(urlC.getInputStream(),"UTF-8"));

         String line = "";

         while((line = rd.readLine()) != null)
         {
            json.append(line);
         }

      }

      catch (IOException e) {
         e.printStackTrace();
      }

      return parseJSON(json.toString());
   }

   private String parseJSON(String jsondata)
   {
      String outputData = "" ;

      JSONParser jsonParser = new JSONParser();

      try 
      {
         JSONObject sentences = (JSONObject) jsonParser.parse(jsondata);

         JSONArray sentenceArr = (JSONArray) sentences.get("sentences");

         for(int i=0 ; i < sentenceArr.size() ; i++)
         {
            JSONObject sentence = (JSONObject) sentenceArr.get(i);
            JSONArray resultArr = (JSONArray) sentence.get("result");

            if(i!=0)
               outputData = outputData + "\n"; 
                        
            for(int j=0 ; j < resultArr.size(); j++)
            {
               JSONObject result = (JSONObject) resultArr.get(j);
               
               if(j!=0)
                  outputData = outputData + " ";
               
               outputData = outputData + result.get("output");
            }

         }
      }

      catch (ParseException e) 
      {
         e.printStackTrace();
      }

      return outputData;
   }

   public static void main(String[] args)
   {
      DaumApi dkle = new DaumApi();
      
      System.out.println(dkle.excuteDaumAPI("그동안 수행한"));
   }
}