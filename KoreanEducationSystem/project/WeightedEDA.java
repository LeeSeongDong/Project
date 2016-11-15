package project;

import java.text.Normalizer;

public class WeightedEDA
{
	private String CHO = "ᄀᄏᄁᄃᄐᄄᄇᄑᄈᄌᄎᄍᄉᄊᄆᄅᄂᄋᄒ"; 				//19
	private String JUNG = "ᅡᅣᅪᅥᅧᅯᅢᅦᅫᅰᅬᅤᅨᅩᅭᅮᅲᅳᅴᅵᅱ"; 			//21
	private String JONG = "ᆨᆿᆩᆪᆫᆬᆭᆮᆺᆻᆽᆾᇀᆯᆰᆱᆲᆳᆴᆵᆶᆸᇁᆹᆷᆼᇂ"; 	//27
	
	public double getEditDistance(String word1, String word2)
	{
		String nfd1 = Normalizer.normalize(word1, Normalizer.Form.NFD);
		String nfd2 = Normalizer.normalize(word2, Normalizer.Form.NFD);
		System.out.println(nfd1 + " : " + nfd1.length());
		System.out.println(nfd2 + " : " + nfd2.length());
		double result = analyze(nfd1, nfd2);
		
		return (double)Math.round(result*1000)/1000;
	}

	public double getChoWeight(char a, char b)
	{
		double res = 2;
		//ᄀᄏᄁ/ᄃᄐᄄ/ᄇᄑᄈ/ᄌᄎᄍ/ᄉᄊ/ᄆᄅᄂᄋᄒ
		//0 1 2 / 3 4 5 / 6 7 8 / 9 10 11 / 12 13 / 14 15 16 17 18
		if((0 <= CHO.indexOf(a) && CHO.indexOf(a) <= 2) && (0 <= CHO.indexOf(b) && CHO.indexOf(b) <= 2))
		{
			res = 1.5;
		}
		else if((3 <= CHO.indexOf(a) && CHO.indexOf(a) <= 5) && (3 <= CHO.indexOf(b) && CHO.indexOf(b) <= 5))
		{
			res = 1.5;
		}
		else if((6 <= CHO.indexOf(a) && CHO.indexOf(a) <= 8) && (6 <= CHO.indexOf(b) && CHO.indexOf(b) <= 8))
		{
			res = 1.5;
		}
		else if((9 <= CHO.indexOf(a) && CHO.indexOf(a) <= 11) && (9 <= CHO.indexOf(b) && CHO.indexOf(b) <= 11))
		{
			res = 1.5;
		}
		else if((12 <= CHO.indexOf(a) && CHO.indexOf(a) <= 13) && (12 <= CHO.indexOf(b) && CHO.indexOf(b) <= 13))
		{
			res = 1.5;
		}

		return res;
	}
	
	public double getJungWeight(char a, char b)
	{
		double res = 2;
		//ᅡᅣᅪ/ᅥᅧᅯ/ᅢᅦᅫᅰᅬ/ᅤᅨ/ᅩᅭ/ᅮᅲ/ᅳᅴᅵᅱ
		//012/345/678910/1112/1314/1516/17181920
		if((0 <= JUNG.indexOf(a) && JUNG.indexOf(a) <= 2) && (0 <= JUNG.indexOf(b) && JUNG.indexOf(b) <= 2))
		{
			res = 1.5;
		}
		else if((3 <= JUNG.indexOf(a) && JUNG.indexOf(a) <= 5) && (3 <= JUNG.indexOf(b) && JUNG.indexOf(b) <= 5))
		{
			res = 1.5;
		}
		else if((6 <= JUNG.indexOf(a) && JUNG.indexOf(a) <= 10) && (6 <= JUNG.indexOf(b) && JUNG.indexOf(b) <= 10))
		{
			res = 1.5;
		}
		else if((11 <= JUNG.indexOf(a) && JUNG.indexOf(a) <= 12) && (11 <= JUNG.indexOf(b) && JUNG.indexOf(b) <= 12))
		{
			res = 1.5;
		}
		else if((13 <= JUNG.indexOf(a) && JUNG.indexOf(a) <= 14) && (13 <= JUNG.indexOf(b) && JUNG.indexOf(b) <= 14))
		{
			res = 1.5;
		}
		else if((15 <= JUNG.indexOf(a) && JUNG.indexOf(a) <= 16) && (15 <= JUNG.indexOf(b) && JUNG.indexOf(b) <= 16))
		{
			res = 1.5;
		}
		else if((17 <= JUNG.indexOf(a) && JUNG.indexOf(a) <= 20) && (17 <= JUNG.indexOf(b) && JUNG.indexOf(b) <= 20))
		{
			res = 1.5;
		}
		
		return res;
	}
	
	public double getJongWeight(char a, char b)
	{
		double res = 2;
		//ᆨᆿᆩᆪ/ᆫᆬᆭ/ᆮᆺᆻᆽᆾᇀ/ᆯᆰᆱᆲᆳᆴᆵᆶ/ᆸᇁᆹ/ᆷᆼᇂ
		//0123/456/789101112/1314151617181920/212223/242526
		if((0 <= JONG.indexOf(a) && JONG.indexOf(a) <= 3) && (0 <= JONG.indexOf(b) && JONG.indexOf(b) <= 3))
		{
			res = 1.5;
		}
		else if((4 <= JONG.indexOf(a) && JONG.indexOf(a) <= 6) && (4 <= JONG.indexOf(b) && JONG.indexOf(b) <= 6))
		{
			res = 1.5;
		}
		else if((7 <= JONG.indexOf(a) && JONG.indexOf(a) <= 12) && (7 <= JONG.indexOf(b) && JONG.indexOf(b) <= 12))
		{
			res = 1.5;
		}
		else if((13 <= JONG.indexOf(a) && JONG.indexOf(a) <= 20) && (13 <= JONG.indexOf(b) && JONG.indexOf(b) <= 20))
		{
			res = 1.5;
		}
		else if((21 <= JONG.indexOf(a) && JONG.indexOf(a) <= 23) && (21 <= JONG.indexOf(b) && JONG.indexOf(b) <= 23))
		{
			res = 1.5;
		}
		else if((24 <= JONG.indexOf(a) && JONG.indexOf(a) <= 26) && (24 <= JONG.indexOf(b) && JONG.indexOf(b) <= 26))
		{
			res = 1.5;
		}
		
		return res;
	}
	
	public double getWeight(char a, char b)
	{
		double dis = 2;
		
		// 초성
		if(a >= 4352 && a <= 4370 && b >= 4352 && b <= 4370)
		{
			double ai = (double)CHO.indexOf(a);
			double bi = (double)CHO.indexOf(b);
			
			dis = Math.abs((ai - bi)/20);
			dis += 1;
		}
		// 중성
		else if(a >= 4449 && a <= 4469 && b >= 4449 && b <= 4469)
		{
			double ai = (double)JUNG.indexOf(a);
			double bi = (double)JUNG.indexOf(b);

			dis = Math.abs((ai - bi)/22);
			dis += 1;
		}
		// 종성
		else if((a >= 4520 && a <= 4546) && (b >= 4520 && b <= 4546))
		{
			double ai = (double)JONG.indexOf(a);
			double bi = (double)JONG.indexOf(b);
			
			dis = Math.abs((ai - bi)/28);
			dis += 1;
		}
		
		return dis;
	}
	
	public double getWeight2(char a, char b)
	{
		// 초성
		if(a >= 4352 && a <= 4370 && b >= 4352 && b <= 4370)
		{
			return getChoWeight(a, b);
		}
		// 중성
		else if(a >= 4449 && a <= 4469 && b >= 4449 && b <= 4469)
		{
			return getJungWeight(a, b);
		}
		// 종성
		else if((a >= 4520 && a <= 4546) && (b >= 4520 && b <= 4546))
		{
			return getJongWeight(a, b);
		}
		
		return 2;
	}
	
	public double analyze(String nfd1, String nfd2) 
	{
		int len1 = nfd1.length();
		int len2 = nfd2.length();

		double[][] dp = new double[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) 
		{
			dp[i][0] = i;
		}

		for (int j = 0; j <= len2; j++) 
		{
			dp[0][j] = j;
		}

		for (int i = 0; i < len1; i++) 
		{
			char c1 = nfd1.charAt(i);
			for (int j = 0; j < len2; j++) 
			{
				char c2 = nfd2.charAt(j);

				if (c1 == c2) 
				{
					dp[i + 1][j + 1] = dp[i][j];
				} 
				else 
				{
					double replace = dp[i][j] + getWeight(c1, c2);
					double insert = dp[i][j + 1] + 1;
					double delete = dp[i + 1][j] + 1;

					double min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
		
		return dp[len1][len2];
	}

	public static void main(String[] args)
	{
		WeightedEDA weda = new WeightedEDA();
		System.out.println(weda.getEditDistance("밥을 찌다", "밥을 빕다"));
		Mongo m = new Mongo();
		m.insertCorpus();
	}
}
