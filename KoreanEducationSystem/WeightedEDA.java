package morph;

import java.text.Normalizer;

public class WeightedEDA
{
	private String CHO = "ᄀᄏᄁᄂᄒᄃᄐᄄᄋᄅᄆᄇᄑᄈᄉᄊᄌᄍᄎ"; //19
	private String JONG = "ᆨᆿᆩᆪᆫᆬᆭᆮᆺᆻᆽᆾᇀᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸᇁᆹᆼᇂ"; //27
	
	public double getEditDistance(String word1, String word2) 
	{
		String nfd1 = Normalizer.normalize(convert(word1), Normalizer.Form.NFD);
		String nfd2 = Normalizer.normalize(convert(word2), Normalizer.Form.NFD);
		double result = analyze(nfd1, nfd2);
		
		return (double)Math.round(result*1000)/1000;
	}

	
	public double getWeight(char a, char b)
	{
		double dis = 0;
		
		// 초성
		if(a >= 4352 && a <= 4370)
		{
			double ai = (double)CHO.indexOf(a);
			double bi = (double)CHO.indexOf(b);
			
			dis = Math.abs((ai - bi)/20);
			dis += 1;
		}
		// 종성
		else if(a >= 4520 && a <= 4546)
		{
			double ai = (double)JONG.indexOf(a);
			double bi = (double)JONG.indexOf(b);
			
			dis = Math.abs((ai - bi)/28);
			dis += 1;
		}
		else
		{
			dis = 2;
		}
		
		return dis;
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

	
	public String convert(String input)
	{
		input = input.replaceAll(" ", "");
		StringBuffer text = new StringBuffer(input);
		
		for(int i = 0; i < text.length(); ++i)
		{
			int code = text.codePointAt(i) - 44032;
			if(code % 28 == 0)
			{
				text.insert(i+1, ' ');
				++i;
			}
		}
		
		String output = text.toString();
		output = Normalizer.normalize(output, Normalizer.Form.NFD);
		return output;
	}
	
	public static void main(String[] args)
	{
		WeightedEDA weda = new WeightedEDA();
		
		//System.out.println(weda.convert("각는 밥을 먹다."));
		System.out.println(weda.getEditDistance("을", "를"));
	}
}
