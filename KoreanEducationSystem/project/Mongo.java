package project;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class Mongo 
{
	public DB db = null;
	public DBCollection coll;

	@SuppressWarnings("deprecation")
	public void connect()
	{
		MongoClient mongoClient = null;
		try{
			mongoClient = new MongoClient("localhost",27017);
			//쓰기권한 부여
			WriteConcern w = new WriteConcern(1,2000);//쓰게 락 갯수, 연결 시간 2000 //쓰레드 쓰게되면 2개 동시에 쓸 경우도 생기니까
			mongoClient.setWriteConcern(w);
			//데이터베이스 연결
			db = mongoClient.getDB("MG1");
			System.out.println("접속 성공");
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	public void getIndexCollection(String collName)
	{
		coll = db.getCollection(collName + "_Index");
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

	public void getEojeolCollection(String collName)
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

	public void getCacheCollection(String collName)
	{
		coll = db.getCollection(collName + "_Cache");

		DBObject index1 = new BasicDBObject("id",1);
		DBObject index2 = new BasicDBObject("eojeol",1);
		DBObject index3 = new BasicDBObject("cache1",1);
		DBObject index4 = new BasicDBObject("cache2",1);
		DBObject index5 = new BasicDBObject("cache3",1);
		DBObject option = new BasicDBObject("unique",1);


		try
		{
			coll.createIndex(index1, option);
			coll.createIndex(index2, option);
			coll.createIndex(index3, option);
			coll.createIndex(index4, option);
			coll.createIndex(index5, option);
		}
		catch (com.mongodb.DuplicateKeyException e)
		{
		}
	}

	public int insert_Cache(String eojeol, String cache1, String cache2, String cache3)
	{   
		int id = (int)coll.count();

		DBObject doc = new BasicDBObject();
		doc.put("id",id);
		doc.put("eojeol",eojeol);
		doc.put("cache1",cache1);
		doc.put("cache2",cache2);
		doc.put("cache3",cache3);

		try
		{ coll.insert(doc); }
		catch(com.mongodb.DuplicateKeyException e)
		{ return -1; }


		return id;
	}

	public int insert_Eojeol(String eojeol)
	{		
		int id = (int)coll.count();

		DBObject doc = new BasicDBObject();
		doc.put("id",id);
		doc.put("eojeol",eojeol);

		try
		{ coll.insert(doc); System.out.println(id + eojeol);
		System.out.println(eojeol);}
		catch(com.mongodb.DuplicateKeyException e)
		{ e.printStackTrace(); return -1; }

		return id;
	}

	public void insert_reverseIndex(int id, String eojeol)
	{
		eojeol = eojeol.substring(1).replace(" ", "").trim();
		
		HashSet<String> set = new HashSet<String>();
		for(int i=0; i < eojeol.length(); i++)
			set.add(Character.toString(eojeol.charAt(i)));

		ArrayList<String> umjeolList = new ArrayList<String>(set);

		for(int i=0; i < umjeolList.size(); i++)
		{      
			String umjeol = umjeolList.get(i);

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

	public ArrayList<String> getCache(String eojeol)
	{
		DBObject o = new BasicDBObject("eojeol",eojeol);
		DBCursor cursor = coll.find(o);

		if(!cursor.hasNext())
		{
			return null;
		}

		ArrayList<String> result = new ArrayList<String>();

		o = cursor.next();

		result.add(o.get("cache1").toString());
		result.add(o.get("cache2").toString());
		result.add(o.get("cache3").toString());

		return result;
	}

	public HashMap<String, HashSet<Integer>> getIndexs(String eojeol)
	{
		HashMap<String, HashSet<Integer>> hm = new HashMap<String, HashSet<Integer>>();
		eojeol = eojeol.replaceAll(" ", "").trim();

		for(int i=1; i < eojeol.length(); i++)
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

	public boolean isExist_Cache(String str)
	{
		DBObject o = new BasicDBObject();
		o.put("eojeol",str);
		DBCursor cursor = coll.find(o);

		return cursor.hasNext();
	}
	
	public void insertCorpus()
	{

		SentenceModifier sm = new SentenceModifier();
		FileHandler fh = new FileHandler();

		String c0 = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//말뭉치 원본//국어국립원 말뭉치//";
		String c1 = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//말뭉치 원본//뉴데일리 말뭉치//";
		String c2 = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//말뭉치 원본//조선일보 말뭉치//";
		String c3 = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//말뭉치 원본//중도일보 말뭉치//";
		String c4 = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//말뭉치 원본//외국인교제 말뭉치//";


		String[] corpusList = {c0, c1, c2, c3, c4};
		int [] numOfFile = {1441, 263, 367, 393, 6};

		connect();

		for(int i=4; i < 5; i++)
		{
			for(int j=0; j <= numOfFile[i] ; j++)
			{
				System.out.println("=========================================" + corpusList[i] + j + "시작!" + new Date().toString() + " ===============================================");
				ArrayList<String> sentence = sm.getSentence(fh.fileLoad(corpusList[i] + j + ".txt"));
				
				if(sentence == null)
					continue;

				for(int k=0; k < sentence.size(); k ++)
				{
					ArrayList<String> eojeol = sm.getEojeol(sentence.get(k));
					if(!eojeol.isEmpty())
					{
						System.out.println(j + " : " + k + " = " + eojeol.get(0));
					}

					for(int z=0; z < eojeol.size(); z++)
					{
						String key = eojeol.get(z).charAt(0) + "";
						int id = -1;
						String str = eojeol.get(z);
						
						System.out.println(key);
						System.out.println(str);
						
						
						getEojeolCollection(key);
						id = insert_Eojeol(str);

						if(id == -1)
							continue;

						getIndexCollection(key);
						insert_reverseIndex(id, str);
					}
				}

				System.out.println("=========================================" + corpusList[i] + j + "완료!" + new Date().toString() + " ===============================================");
			}
		}
	}
}