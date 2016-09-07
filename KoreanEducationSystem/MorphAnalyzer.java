package morph;

import java.util.ArrayList;

import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.ChartMorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger.HMMTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor.UnknownProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter.InformalSentenceFilter;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor.SentenceSegmentor;

public class MorphAnalyzer
{
   private Workflow workflow = new Workflow();

   public MorphAnalyzer() 
   {
      init();
   }

   public void init()
   {
      try 
      {
         /* Setting plug-in for work flow  */
         /* Phase1. 문장 경계 인식, 필터링, 자동 띄어쓰기 등 형태소 분석 이 전에 필요한 전처리 작업 */
         workflow.appendPlainTextProcessor(new SentenceSegmentor(), null);
         workflow.appendPlainTextProcessor(new InformalSentenceFilter(), null);

         /* Phase2. 입력 문장에 대해서 어절 단위로 발생 가능한 모든 형태소 분석 결과를 생성 */
         workflow.setMorphAnalyzer(new ChartMorphAnalyzer(), "conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
         workflow.appendMorphemeProcessor(new UnknownProcessor(), null);   //unk태그 -> ncn태그로 변환

         /*
          * For simpler morphological analysis result with 22 tags, decomment the following line.
          * Notice: If you use SimpleMAResult22 plug-in, POSTagger will not work correctly.
          *         So don't add phase3 plug-ins after SimpleMAResult22.
          */
         // workflow.appendMorphemeProcessor(new SimpleMAResult22(), null);
         
         /*
          * For simpler morphological analysis result with 9 tags, decomment the following line.
          * Notice: If you use SimpleMAResult09 plug-in, POSTagger will not work correctly.
          *         So don't add phase3 plug-ins after SimpleMAResult09.
          */
         // workflow.appendMorphemeProcessor(new SimpleMAResult09(), null);
         
         /* Phase3.  가장 유망한 형태소 분석 결과들을 선택하여 입력 문장에 대한 최종 품사 태깅 결과를 반환 */
         workflow.setPosTagger(new HMMTagger(), "conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");

         /* For simpler POS tagging result with 22 tags, decomment the following line. */
         // workflow.appendPosProcessor(new SimplePOSResult22(), null);
         
         /* For simpler POS tagging result with 9 tags, decomment the following line. */
         // workflow.appendPosProcessor(new SimplePOSResult09(), null);
         
         /* work flow 활성화 */
         workflow.activateWorkflow(true);
      
      } 
      catch (Exception e) 
      {
         e.printStackTrace();
         System.exit(0);
      }
   }
   
   public void close()
   {
      /* work flow 비활성화 및 종료*/
      workflow.close();  
   }
   
   public static String getSementic(String input)
   {
      return input.split("/")[0];
   }
   
   public static String getTag(String input)
   {
      return input.split("/")[1];
   }

   public ArrayList<String> getUnk(String inputText)
   {
      ArrayList<String> result = new ArrayList<String>();

      workflow.analyze(inputText);
      String document = workflow.getResultOfDocument();
      
      String[] temp1 = document.split("\t");
      for(int i = 0; i < temp1.length; ++i)
      {
         String[] temp2 = temp1[i].split("/");
         if(temp2.length > 1 && temp2[1].contains("unk"))
         {
            result.add(temp2[0]);
         }
      }

      return result;
   }

   public ArrayList<String> getMorph(String inputText)
   {
      ArrayList<String> arr = new ArrayList<String>();
      ArrayList<String> result = new ArrayList<String>();

      workflow.analyze(inputText);
      String document = workflow.getResultOfDocument();
      //System.out.println("document : " + document);
      String[] temp1 = document.split("\n");
      for(int i = 0; i < temp1.length; ++i)
      {
         try
         {
            if(i % 3 == 1)
            {
               String[] temp2 = temp1[i].split("\t");
               //result.add(temp2[1].split("[+]")[0]);
               //System.out.println("temp2[1] : " + temp2[1]);
               String[] temp3 = temp2[1].split("[+]");
               for(int j = 0; j < temp3.length; ++j)
               {
                  arr.add(temp3[j]);
               }
            }
         }
         catch(Exception e)
         {
            System.out.println(temp1[i]);
            e.printStackTrace();
         }
         
         
      }
      
      ///
      
      String buffer = getSementic(arr.get(0));
      char tag = getTag(arr.get(0)).charAt(0);
      int count = 0;
      int size = arr.size();
      for(int i = 1; i < size; ++i)
      {
         if(tag != getTag(arr.get(i)).charAt(0))
         {
            String temp = buffer + "/";
            if(count > 1)
            {
               temp += tag + "z";
            }
            else
            {
               temp += getTag(arr.get(i-1));
            }
            
            result.add(temp);
            buffer = "";
            count = 0;
         }
         
         ++count;
         buffer += getSementic(arr.get(i));
         tag = getTag(arr.get(i)).charAt(0);
      }
      
      if(!buffer.equals(""))
      {
         String temp = buffer + "/";
         if(count > 1)
         {
            temp += tag + "z";
         }
         else
         {
            temp += getTag(arr.get(size-1));
         }
         
         result.add(temp);
      }

      return result;
   }

   public ArrayList<String> makeTrainingset(String inputText)
   {
      ArrayList<String> result = new ArrayList<String>();
      
      workflow.analyze(inputText);
      String document = workflow.getResultOfDocument();
      String[] temp1 = document.split("\t");
      String buffer = "";
      for(int i = 0; i < temp1.length; ++i)
      {
         if(!temp1[i].contains("\n") || !temp1[i].contains("/")
               || temp1[i].contains("ma"))
         {
            continue;
         }

         String temp2 = temp1[i].split("\n")[0];
         
         if(buffer.equals(""))
         {
            if(temp2.contains("+"))
            {
               String[] temp3 = temp2.split("[+]");
               for(int j = 0; j < temp3.length; ++j)
               {
                  buffer += temp3[j].split("/")[0] + " ";
               }
            }
            else
            {
               buffer = temp2.split("/")[0];
            }
         }
         else
         {
            if(temp2.contains("+"))
            {
               String[] temp3 = temp2.split("[+]");
               
               buffer += temp3[0].split("/")[0];
               result.add(buffer);
               buffer = temp3[0].split("/")[0] + " ";
               
               for(int j = 1; j < temp3.length; ++j)
               {
                  buffer += temp3[j].split("/")[0] + " ";
               }
            }
            else
            {
               buffer += temp2.split("/")[0];
               result.add(buffer);
               buffer = temp2.split("/")[0] + " ";
            }
         }
      }
      result.add(buffer);
      
      // System.out.println(result);
      return result;
   }
   
   
   public static void main(String[] args)
   {
      MorphAnalyzer ma = new MorphAnalyzer();
      
      String inputText = "섭취했다.";
      System.out.println(ma.getMorph(inputText));
      System.out.println(ma.makeTrainingset(inputText));
      
      ma.close();
   }

}

