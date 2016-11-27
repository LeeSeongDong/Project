package db;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


public class SentenceModifier {

	private static final char[] CHO = 
		{0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
		0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

	public String getCHO(char c)
	{
		char test = c;

		if(test >= 0xAC00)
		{
			char uniVal = (char) (test - 0xAC00);
			char cho = (char) (((uniVal - (uniVal % 28))/28)/21);

			c = CHO[cho];
		}

		return Character.toString(c);
	}

	public ArrayList<String> getSentence(ArrayList<String> arr)
	{
		if(arr == null)
			return null;
		
		StringTokenizer st = null;
		ArrayList<String> senList = new ArrayList<String>();
		
		for(int i=0; i < arr.size(); i++)
		{
			st = new StringTokenizer(arr.get(i),".");
			
			while(st.hasMoreTokens())
				senList.add(st.nextToken());
		}
		
		return senList;
	}
	
	
	public ArrayList<String> getEojeol(String str)
	{
		str = str.replaceAll("[^[0-9]]","ⓝ");
		
		String replaceN = "ⓝⓝ";
		
		while(str.contains(replaceN))
			str = str.replaceAll(replaceN, "ⓝ");
		
		ArrayList<String> eojeolList = new ArrayList<String>();
		
		StringTokenizer st_special = new StringTokenizer(str, "()[]\",/'“”");
		
		ArrayList<String> temp = new ArrayList<String>();
		
		while(st_special.hasMoreTokens())
			temp.add(st_special.nextToken().trim());
		
		for(int i=0 ; i < temp.size() ; i++)
		{
			String[] candidates = temp.get(i).split(" ");
			
			if(candidates.length < 2)
				continue;
			
			for(int j=0; j < candidates.length-1 ; j++)
			{
				if(candidates[j].equals(" ") || candidates[j].equals(" ") || candidates[j+1].equals(" ") || candidates[j+1].equals(""))
					continue;
				
				String eojeol = candidates[j] + " " + candidates[j+1];
				if(Pattern.matches("^[가-힣ⓝ!? ]*$", eojeol))
					eojeolList.add(eojeol);
			}
		}
		
		return eojeolList;
		
	}
	
	public static void main(String[] args)
	{
		SentenceModifier sm = new SentenceModifier();

		System.out.println(sm.getCHO('휗'));
		
		ArrayList<String> sentence = new ArrayList<String>();
		String str = " 한국이 진정한 선진국이 되기 위해서는 소득수준을 높이는 것 뿐아니라 타인에 대한 관용과 존중 등을 통해 사회통합력을 높여야 한다는 의견이 나오고 있다. 박명호 한국외국어대 경제학과 교수가 8일 한국경제학회에 발표한 '지표를 활용한 한국의 경제사회발전 연구: OECD 회원국과의 비교분석' 논문을 보면, 1995년 21위였던 한국의 사회통합지수는 15년 뒤인 2009년 24위로 3계단 미끄러졌다. 경제의 발전수준을 보여주는 '성장동력' 지표는 20위에서 13위로 올랐고 환경문제에 대한 대응력을 보여주는 '환경' 지표는 24위에서 27위로 악화했다. 특히 사회통합지수의 경우 주요 구성항목의 순위가 줄줄이 떨어졌다.";
		
		sentence.add(str);
		ArrayList<String> arr = sm.getEojeol(sm.getSentence(sentence).get(1));
		
		System.out.println("======================뽑힌글자=================");
		for(int i=0; i < arr.size() ; i++)
			System.out.println(arr.get(i));
		
	}
}
