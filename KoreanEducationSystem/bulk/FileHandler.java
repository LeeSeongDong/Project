package bulk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FileHandler {

   public void setIndexs(ArrayList<String> fileName)
   {
      //eojeolFile.length
      for(int i=0 ; i < fileName.size(); i++)
      {
         int count = 0;
         HashMap<Character,HashSet<Integer>> hm = new HashMap<Character, HashSet<Integer>>();

         try {
            FileReader fr = new FileReader("어절//"+ fileName.get(i) + "_어절.txt");
            BufferedReader br = new BufferedReader(fr);

            String str;

            while((str = br.readLine()) != null) 
            {
               String[] tmp = str.split("\t");
               int id = Integer.parseInt(tmp[0]);
               String eojeol = tmp[1].replaceAll(fileName.get(i), "").replaceAll(" ", "").trim();
               HashSet<Integer> set = null;

               System.out.println("역인덱스 과정 : " + id);

               for(int k=0; k < eojeol.length(); k++)
               {
                  char key = eojeol.charAt(k);
                  if(hm.containsKey(key))
                     set = hm.get(key);
                  else
                     set = new HashSet<Integer>();

                  set.add(id);
                  hm.put(key, set);
               }
               
               if((id%1000000) == 0 && id != 0)
               {
                  indexSave(hm,fileName.get(i));
                  count ++;
                  hm.clear();
               }
            }
            
            if(!hm.isEmpty())
            {   
               indexSave(hm,fileName.get(i)+count);
               hm.clear();
            }
            
            fr.close();
            br.close();
            System.out.println(fileName.get(i) + "인덱스 완료");
         }
         catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public void indexSave(HashMap<Character,HashSet<Integer>> hm, String fileName)
   {
      try 
      {
         BufferedWriter bw = new BufferedWriter(new FileWriter("인덱스//" + fileName + "_In.txt",true));
         PrintWriter pw = new PrintWriter(bw);

         Set<Character> keySet = hm.keySet();

         for(Character e : keySet)
         {
            pw.print(e + "\t");
            for(Integer n : hm.get(e))
            {
               pw.print(n + " ");
            }
            pw.println();
         }

         pw.flush();
         pw.close();

      } 
      catch (IOException e) 
      { e.printStackTrace(); }

   }

   public void setId(ArrayList<String> fileName)
   {      
      for(int i=0 ; i < fileName.size(); i++)
      {
         ArrayList<String> arr = fileLoad("어절//" + fileName.get(i) + "_어절.txt");
         try 
         {
            BufferedWriter bw = new BufferedWriter(new FileWriter("어절//"+fileName.get(i) + "_어절.txt"));
            PrintWriter pw = new PrintWriter(bw);

            for(int j=0; j < arr.size(); j++)
            {
               pw.println(j + "\t" + arr.get(j));
            }

            pw.flush();
            pw.close();

         } 

         catch (IOException e) 
         { e.printStackTrace(); }

      }
   }

   public ArrayList<String> fileLoad(String filename)
   {
      ArrayList<String> stringArr = new ArrayList<String>();

      try {
         FileReader fr = new FileReader(filename);
         BufferedReader br = new BufferedReader(fr);

         String str;

         while((str = br.readLine()) != null) 
         {
            stringArr.add(str);
         }

         fr.close();
         br.close();

         System.out.println(filename + " Load!");
         return stringArr ;
      }
      catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }

   public void removeDuple(String fileName)
   {

      ArrayList<String> arr = new ArrayList<String>(new HashSet<String>(fileLoad("어절//"+fileName + "_어절.txt")));

      System.out.println(fileName + "중복제거 후 총 갯수 : " + arr.size());
      try 
      {
         BufferedWriter bw = new BufferedWriter(new FileWriter("어절//"+fileName + "_어절.txt"));
         PrintWriter pw = new PrintWriter(bw);

         for(int i=0; i < arr.size(); i++)
         {
            pw.println(arr.get(i));
         }

         pw.flush();
         pw.close();

      } 

      catch (IOException e) 
      { e.printStackTrace(); }   
   }

   public ArrayList<String> getFileName(String path)
   {
      ArrayList<String> nameList = new ArrayList<String>();
      
      File dir = new File(path);
      
      File[] files = dir.listFiles();
      
      for(int i=0; i < files.length; i++)
      {
         nameList.add(files[i].getName().replaceAll("_어절.txt", "").replaceAll("_In.txt", "").trim());
      }
      
      return nameList;
   }
   
   public void fileSave(String fileName, ArrayList<String> arr)
   {
      try 
      {
         BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true));
         PrintWriter pw = new PrintWriter(bw);

         for(int i=0; i < arr.size(); i++)
         {
            pw.println(arr.get(i));
         }

         pw.flush();
         pw.close();

      } 

      catch (IOException e) 
      { e.printStackTrace(); }   
   }

   public HashMap<String, HashSet<Integer>> mapLoad(String fileName)
   {
      HashMap<String, HashSet<Integer>> result = new HashMap<String, HashSet<Integer>>();

      try 
      {
         FileReader fr = new FileReader(fileName);
         BufferedReader br = new BufferedReader(fr);

         String buffer;

         while((buffer = br.readLine()) != null)
         {
            String[] temp = buffer.split("\t");

            HashSet<Integer> hs = new HashSet<Integer>();
            int size = temp.length;
            for(int i = 1; i < size; ++i)
            {
               hs.add(Integer.parseInt(temp[i]));
            }

            result.put(temp[0], hs);
         }

         br.close();
         fr.close();
      } 
      catch (Exception e) 
      {
         e.printStackTrace();
         return null;
      }

      return result;
   }

}