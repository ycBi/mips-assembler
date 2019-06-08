package compiler2;

class NewHEX 
{
	//转换为16进制
	public String toHEX(int ii, int dd) throws Exception
	{
		String s1,s2;
		int len,i;
		s1=Integer.toHexString(ii);
		len=s1.length();
		s2="";
		for (i=0;i<dd-len;i++ )
			s2=s2+"0";
		if (len>dd) s2=s1.substring(len-dd,len);
		else s2=s2+s1;
		return s2;
	}
//将十六进制转换为十进制
	public int HEXtoInt(String s) throws Exception
	{
		int i;
		char c;
		int res=0;
		for (i=0;i<s.length() ;i++ )
		{
			c=s.charAt(i);
			if (c>='0' && c<='9')	
				res=res*16+(c-'0');
			else res=res*16+(c-'a'+10);
		}
		return res;
	}
}
