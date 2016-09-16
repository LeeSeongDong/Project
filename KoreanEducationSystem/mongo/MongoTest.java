package mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class MongoTest {

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
			db = mongoClient.getDB("MongoDB");
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

	public int insert_Eojeol(String eojeol)
	{	
		int id = (int)coll.count();

		DBObject doc = new BasicDBObject();
		doc.put("id",id);
		doc.put("eojeol",eojeol);

		try
		{ coll.insert(doc); }
		catch(com.mongodb.DuplicateKeyException e)
		{ return -1; }


		return id;
	}

	public void insert_reverseIndex(int id, String eojeol)
	{
		eojeol = eojeol.replaceAll(" ", "").trim();
		
		for(int i=0; i < eojeol.length(); i++)
		{		
			String umjeol = Character.toString(eojeol.charAt(i));
			
			HashSet<Integer> indexs = new HashSet<Integer>();
			indexs.add(id);

			DBObject doc = new BasicDBObject();
			doc.put("umjeol",umjeol);
			doc.put("indexs",indexs);

			try
			{ coll.insert(doc); }
			catch(com.mongodb.DuplicateKeyException e)
			{
				DBObject findDoc = new BasicDBObject("umjeol", umjeol);
				DBObject updateDoc = new BasicDBObject("$push", new BasicDBObject("indexs",id));
				coll.update(findDoc, updateDoc);
			}
			if(eojeol.charAt(i) == '?')
			{
				umjeol = "[?]";
			}
			eojeol = eojeol.replaceAll(umjeol, "");
		}
	}

	public String getEojeol(int id)
	{
		DBObject o = new BasicDBObject();
		o.put("id",id);
		DBCursor cursor = coll.find(o);
		
		if(!cursor.hasNext())
		{
			return null;
		}

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

	public static void main(String[] args) {
		
		SentenceModifier sm = new SentenceModifier();
		FileHandler fh = new FileHandler();
		MongoTest mt = new MongoTest();
		
		String c1 = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//말뭉치 원본//국어국립원 말뭉치//";
		String c2 = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//말뭉치 원본//뉴데일리 말뭉치//";
		String c3 = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//말뭉치 원본//조선일보 말뭉치//";
		String c4 = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//말뭉치 원본//중도일보 말뭉치//";
		

		String[] fns = {c1, c2, c3, c4};
		int [] lastfileNum = {1441, 263, 367, 393};

		mt.con();

		for(int i=0; i < 1; i++)
		{
			for(int j=467; j <= lastfileNum[i] ; j++)
			{
				System.out.println("=========================================" + fns[i] + j + "시작!" + new Date().toString() + " ===============================================");
				ArrayList<String> sentence = sm.getSentence(fh.fileLoad(fns[i] + j + ".txt"));

				if(sentence == null)
					continue;

				for(int k=0; k < sentence.size() ; k ++)
				{
					ArrayList<String> eojeol = sm.getEojeol(sentence.get(k));
					if(!eojeol.isEmpty())
					{
						System.out.println(j + " : " + k + " = " + eojeol.get(0));
					}
					
					for(int z=0; z < eojeol.size(); z++)
					{
						String key = sm.getCHO(eojeol.get(z).charAt(0));
						int id = -1;
						String str = eojeol.get(z);
						//str = 형태소분석 해바꾸기 (띄어쓰기 없애는건 할필요없음)

						mt.getCollectionEojeol(key);
						id = mt.insert_Eojeol(str);
						
						if(id == -1)
							continue;

						mt.getCollectionIndex(key);
						mt.insert_reverseIndex(id, str);
					}

				}

				System.out.println("=========================================" + fns[i] + j + "완료!" + new Date().toString() + " ===============================================");
			}
		}

	}
}