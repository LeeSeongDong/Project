package bulk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkUpdateRequestBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteRequestBuilder;
import com.mongodb.BulkWriteResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.WriteModel;

public class Mongo {

   public int OeojeolCnt = 0;
   public int OindexCnt = 0;

   public DB db = null;
   public DBCollection coll;

   public void con()
   {
      MongoClient mongoClient = null;
      try{
         mongoClient = new MongoClient("localhost",27017);
         System.out.println("접속 성공");
         //쓰기권한 부여
         WriteConcern w = new WriteConcern(1,2000);//쓰게 락 갯수, 연결 시간 2000 //쓰레드 쓰게되면 2개 동시에 쓸 경우도 생기니까
         mongoClient.setWriteConcern(w);
         //데이터베이스 연결
         db = mongoClient.getDB("MG1");
      }
      catch(Exception e){
         System.out.println(e.getMessage());
      }
   }

   public void getCollectionIndex(String collName)
   {
      coll = db.getCollection(collName + "_In");
      DBObject index = new BasicDBObject("umjeol",1);
      DBObject option = new BasicDBObject("unique",1);

      try
      {
         coll.createIndex(index,option);
      }
      catch (com.mongodb.DuplicateKeyException e)
      {
      }
   }

   public void getCollectionEojeol(String collName)
   {
      coll = db.getCollection(collName + "_어절");

      DBObject index1 = new BasicDBObject("id",1);
      DBObject index2 = new BasicDBObject("eojeol",1);
      DBObject option = new BasicDBObject("unique",1);

      try
      {
         coll.createIndex(index1, option);
         coll.createIndex(index2, option);
      }
      catch (com.mongodb.DuplicateKeyException e)
      {
      }

   }

   public void insert_Eojeol(String key)
   {   
      int insertcount = 1;
      int count = 0;
      ArrayList<BasicDBObject> arr = new ArrayList<BasicDBObject>();

      try {
         FileReader fr = new FileReader("어절//" +key + "_어절.txt");
         BufferedReader br = new BufferedReader(fr);

         String str;

         while((str = br.readLine()) != null) 
         {
            String[] tmp = str.split("\t");
            BasicDBObject o = new BasicDBObject();
            o.put("id", Integer.parseInt(tmp[0]));
            o.put("eojeol", tmp[1]);
            arr.add(o);
            count ++;
            System.out.println(count);

            if((count%1000000) == 0 && count != 0)
            {
               coll.insert(arr);
               System.out.println(insertcount + "완료");
               insertcount ++;
               arr.clear();
            }
         }

         if(!arr.isEmpty())
         {   
            coll.insert(arr);
            System.out.println(insertcount + "완료");
            insertcount ++;
            arr.clear();
         }

         fr.close();
         br.close();

         //System.out.println(filename + " Load!");
      }
      catch (IOException e) {
      }
   }

   public void insert_reverseIndex(String Key)
   {
      BulkWriteOperation  bulkWriteOperation= coll.initializeUnorderedBulkOperation();

      try {
         FileReader fr = new FileReader("인덱스//" + Key + "_In.txt");
         BufferedReader br = new BufferedReader(fr);

         String str;

         while((str = br.readLine()) != null) 
         {
            String[] tmp1 = str.split("\t");
            String[] tmp2 = tmp1[1].split(" ");

            int[] array = Arrays.asList(tmp2).stream().mapToInt(Integer::parseInt).toArray();

            BulkWriteRequestBuilder bulkWriteRequestBuilder= bulkWriteOperation.find(new BasicDBObject("umjeol", tmp1[0]));
            BulkUpdateRequestBuilder updateReq = bulkWriteRequestBuilder.upsert();
            updateReq.updateOne(new BasicDBObject("$push", new BasicDBObject("indexs", new BasicDBObject("$each",array))));
         }

         fr.close();
         br.close();

      }
      catch (IOException e) {
      }

      BulkWriteResult br = bulkWriteOperation.execute();
   }

   public String getEojeol(int id)
   {
      DBObject o = new BasicDBObject();
      o.put("id",id);
      DBCursor cursor = coll.find(o);

      return cursor.next().get("eojeol").toString();
   }

   public HashMap<String, HashSet<Integer>> getIndexs(String eojeol)
   {
      HashMap<String, HashSet<Integer>> hm = new HashMap<String, HashSet<Integer>>();
      eojeol = eojeol.replaceAll(" ", "").trim();

      for(int i=0; i < eojeol.length(); i++)
      {
         String umjeol = Character.toString(eojeol.charAt(i));

         DBObject query = new BasicDBObject("umjeol",umjeol);
         DBCursor cursor = coll.find(query);

         if(cursor.count() <= 0)
         {
            hm.put(umjeol, null);
            continue;
         }

         DBObject result = cursor.next();   

         hm.put(umjeol, new HashSet((List<Integer>)result.get("indexs")));
      }

      return hm;
   }

   public boolean isExist_Eojeol(String eojeol)
   {
      DBObject o = new BasicDBObject();
      o.put("eojeol",eojeol);
      DBCursor cursor = coll.find(o);

      return cursor.hasNext();
   }

   public ArrayList<BasicDBObject> getEojFile(String filename)
   {
      ArrayList<BasicDBObject> arr = new ArrayList<BasicDBObject>();

      try {
         FileReader fr = new FileReader(filename + "_어절.txt");
         BufferedReader br = new BufferedReader(fr);

         String str;

         while((str = br.readLine()) != null) 
         {
            String[] tmp = str.split("\t");
            BasicDBObject o = new BasicDBObject();
            o.put("id", Integer.parseInt(tmp[0]));
            o.put("eojeol", tmp[1]);
            arr.add(o);
         }

         fr.close();
         br.close();

         //System.out.println(filename + " Load!");
         return arr ;
      }
      catch (IOException e) {
         return null;
      }
   }


   public static void main(String[] args) {

      FileHandler fh = new FileHandler();

      SentenceModifier sm = new SentenceModifier();
      Mongo mt = new Mongo();

      mt.con();
      
/*      
      String[] fns = {"학습데이터//말뭉치//","학습데이터//조선일보//","학습데이터//중도일보//","학습데이터//전자신문//","학습데이터//뉴데일리//","학습데이터//조선일보2//"};
      int [] lastfileNum = {1442,367,393,1177,263,236} ;

      for(int i=0; i <= 0; i++)
      {
         HashMap<Character, ArrayList<String>> arr = new HashMap<Character, ArrayList<String>>();

         for(int j=0; j <= 5 ; j++)
         {
            int count = 0;
            ArrayList<String> sentence = sm.getSentence(fh.fileLoad(fns[i] + j + ".txt"));

            if(sentence == null)
               continue;

            for(int k=0; k < sentence.size() ; k ++)
            {
               ArrayList<String> eojeol = sm.getEojeol(sentence.get(k));

               for(int z=0; z < eojeol.size(); z++)
               {
                  String str = eojeol.get(z);
                  char key = str.charAt(0);

                  ArrayList<String> strArr = null;

                  if(arr.containsKey(key))
                     strArr = arr.get(key);
                  else
                     strArr = new ArrayList<String>();

                  strArr.add(str);
                  arr.put(str.charAt(0), strArr);

                  count ++;
                  System.out.println(fns[i] + j + " / " + str + " / " + count);
               }

            }

            if( (j % 5 == 0) || (j == lastfileNum[i]) )
            {
               ArrayList<Character> keySet = new ArrayList<Character>(arr.keySet());

               for(int m=0 ; m < keySet.size(); m++)
               {
                  String key = Character.toString(keySet.get(m));
                  if(!key.equals("?") && !key.equals(" ") && !key.equals("!"))
                  {
                     fh.fileSave("어절//"+key + "_어절.txt", arr.get(keySet.get(m)));
                  }
               }

               arr.clear();

               for(int m=0 ; m < keySet.size(); m++)
               {
                  String key = Character.toString(keySet.get(m));
                  if(!key.equals("?") && !key.equals(" ") && !key.equals("!"))
                     fh.removeDuple(key);
               }
            }

         }
      }

      ArrayList<String> fileNames1 = fh.getFileName("어절");
      
      for(int i=0; i < fileNames1.size();i++)
         System.out.println(fileNames1.get(i));
      
      fh.setId(fileNames1);
      fh.setIndexs(fileNames1);
*/      
      
      ArrayList<String> fileNames1 = fh.getFileName("어절");
      
      for(int i=0; i < fileNames1.size() ; i++)
      {
         String key=fileNames1.get(i);
         mt.getCollectionEojeol(key);
         mt.insert_Eojeol(key);
         System.out.println(fileNames1.get(i) + "어절 인서트완료");
         
      }

      ArrayList<String> fileNames2 = fh.getFileName("인덱스");

      for(int i=0; i < fileNames2.size() ; i++)
      {
         String key=fileNames2.get(i);
         mt.getCollectionIndex(Character.toString(key.charAt(0)));
         mt.insert_reverseIndex(key);
         System.out.println(key + "인덱스 인서트완료");
      }

   }
}