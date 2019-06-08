package compiler2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.lang.Integer;

public class Asm 
{
	public String hashreg(String s)
	{
		  if     (s.equals("$t0"))return "01000";
		  else if(s.equals("$t1"))return "01001";
		  else if(s.equals("$t2"))return "01010";
		  else if(s.equals("$t3"))return "01011";
		  else if(s.equals("$t4"))return "01100";
		  else if(s.equals("$t5"))return "01101";
		  else if(s.equals("$t6"))return "01110";
		  else if(s.equals("$t7"))return "01111";
		  else if(s.equals("$t8"))return "11000";
		  else if(s.equals("$t9"))return "11001";
		  else if(s.equals("$s0"))return "10000";
		  else if(s.equals("$s1"))return "10001";
		  else if(s.equals("$s2"))return "10010";
		  else if(s.equals("$s3"))return "10011";
		  else if(s.equals("$s4"))return "10100";
		  else if(s.equals("$s5"))return "10101";
		  else if(s.equals("$s6"))return "10110";
		  else if(s.equals("$s7"))return "10111";
		  else if(s.equals("$zero") || s.equals("$0"))return "00000";
		  else if(s.equals("$v0"))return "00010";		  
		  else if(s.equals("$v1"))return "00011";
		  else if(s.equals("$a0"))return "00100";
		  else if(s.equals("$a1"))return "00101";
		  else if(s.equals("$a2"))return "00110";
		  else if(s.equals("$a3"))return "00111";
		  else if(s.equals("$gp"))return "11100";
		  else if(s.equals("$sp"))return "11101";
		  else if(s.equals("$fp"))return "11110";
		  else if(s.equals("$ra"))return "11111";
		  else return "";
	}
	//R型指令的func位
	private String hashRop(String s)
	{
		  if     (s.equals("add"))return "100000";
		  else if(s.equals("sub"))return "100010";
		  else if(s.equals("and"))return "100100";
		  else if(s.equals("or"))return "100101";
		  else if(s.equals("nor"))return "100111";
		  else if(s.equals("sll"))return "000000";
		  else if(s.equals("srl"))return "000010";
		  else if(s.equals("slt"))return "101010";
		  else if(s.equals("jr"))return "001000";
		  else return "";
	}
	//I型指令的操作码位
	private String hashIop(String s)
	{
		  if     (s.equals("lw"))return "100011";
		  else if(s.equals("sw"))return "101011";
		  else if(s.equals("andi"))return "001100";
		  else if(s.equals("ori"))return "001101";
		  else if(s.equals("beq"))return "000100";
		  else if(s.equals("bne"))return "000101";
		  else if(s.equals("addi"))return "001000";
		  else if(s.equals("sb"))return "101000";
		  else if(s.equals("sh"))return "101001";
		  else if(s.equals("lb"))return "100100";
		  else if(s.equals("lh"))return "100101";
		  else if(s.equals("lui"))return "001111";
		  else if(s.equals("subi"))return "100011";
		  else return "";
	}
	//J型指令的操作码位
	private String hashJop(String s)
	{
		  if     (s.equals("j"))return "000010";
		  else if(s.equals("jal"))return "000011";
		  else return "";
	}
	//该方法将十进制转换为二进制
	private String toBin(int ii, int dd)
	{
		String s1,s2;
		int len,i;
		s1=Integer.toBinaryString(ii);
		len=s1.length();
		s2="";
		for (i=0;i<dd-len;i++ )
			s2=s2+"0";
		if (len>dd) s2=s1.substring(len-dd,len);
		else	s2=s2+s1;
		return s2;
	}
	
	//构造R型指令的二进制
	private String Rtype(String operator,String s)
	{
		int index;
		String tem="000000";
		String operand1,operand2,operand3;

		if(operator.equals("jr"))
		{
			tem = tem + hashreg(s) + "00000" + "00000" + "00000" + hashRop(operator);
		}
		else
		{
			index=s.indexOf(",");
			operand1=s.substring(0,index);
			s=s.substring(index+1).trim();

			index = s.indexOf(",");
			operand2 = s.substring(0,index);
			s = s.substring(index+1).trim();

			operand3 = s;

			if(operator.equals("sll")||operator.equals("srl"))
			{
				int a = Integer.parseInt(operand3);
				String m = toBin(a,5);
				tem = tem + "00000" + hashreg(operand2) + hashreg(operand1) + m + hashRop(operator); 
			}
			else
				tem = tem  + hashreg(operand2)  + hashreg(operand3)  + hashreg(operand1)  + "00000"  + hashRop(operator);

		}
		return tem;

	}
	
	//构造I型指令的二进制
	private String Itype(String operator,String s,int currentl,PC pc)
	{
		int index;
		String tem="";
		String operand1,operand2,operand3;
		
		if(operator.equals("lw")||operator.equals("sw")||operator.equals("lb")||operator.equals("sb")||operator.equals("lh")||operator.equals("sh"))
		{
			index=s.indexOf(",");
			operand1=s.substring(0,index);
			s=s.substring(index+1).trim();
		    int index1=s.indexOf("(");
			int index2=s.indexOf(")");
		    operand2=s.substring(index1+1,index2).trim();
     	    operand3=s.substring(0,index1).trim();
			int a = Integer.parseInt(operand3);
			String m = toBin(a,16);
			tem = tem + hashIop(operator) + hashreg(operand2) + hashreg(operand1) + m;
		}
		else if (operator.equals("lui"))
		{
			String m;
			index=s.indexOf(",");
			operand1=s.substring(0,index);
			s=s.substring(index+1).trim();
			int a = Integer.parseInt(s);
			m = toBin(a,16);
			tem = tem + hashIop(operator) + "00000" + hashreg(operand1) + m;
		}
		else
		{
			String m;
			index=s.indexOf(",");
			operand1=s.substring(0,index);
			s=s.substring(index+1).trim();

			index = s.indexOf(",");
			operand2 = s.substring(0,index);
			s = s.substring(index+1).trim();
			if(operator.equals("beq")||operator.equals("bne"))
			{
				m=toBin((Integer.parseInt(pc.label.get(s).toString())-currentl-1),16);
			    tem = tem + hashIop(operator) + hashreg(operand1) + hashreg(operand2) + m;
			}
			else 
			{
				int a = Integer.parseInt(s);
				m = toBin(a,16);
				tem = tem + hashIop(operator) + hashreg(operand2) + hashreg(operand1) + m;
			}
		}
		return tem;
	}
	//构造J型指令的二进制
	private String Jtype(String operator,String s,int currentl,PC pc)
	{
		int index;
		String tem="";
//要判断是否标号
			int a = Integer.parseInt(pc.label.get(s).toString());
			String m = toBin(a,26);
			tem = tem + hashJop(operator) + m;
			return tem;
	}
	
	public String StoB(String s, int currentl,PC pc) 
	{
		  int index;
		  String operator;
		  String binarycode;
		  index=s.indexOf(" ");
		  operator=s.substring(0,index).trim().toLowerCase();
		  s=s.substring(index+1).trim();
		  if (operator.equals("add")||operator.equals("sub")||operator.equals("and")||operator.equals("or")
			  ||operator.equals("nor")||operator.equals("sll")||operator.equals("srl")||operator.equals("slt")
			  ||operator.equals("jr"))
			  binarycode = Rtype(operator,s);

		  else if(operator.equals("j")||operator.equals("jal"))
			  binarycode = Jtype(operator,s,currentl,pc);

		  else if(operator.equals("end"))
			  binarycode = "11111100000000000000000000000001";

		  else if(operator.equals("dw"))
		  {
			  int a = Integer.parseInt(s);
			  binarycode = toBin(a,32);
		  }
		  else
			  binarycode = Itype(operator,s,currentl,pc);

			  return binarycode;	  
	}
	public String BtoH(String s)
	{
		String rob = "";
		String t;
		int i,a;
		for(i=0;i<8;i++)
		{
			int sum=0;
			int b = 8;
			t = s.substring(0,3);
			for(int j=0;j<4;j++)
			{
				a = Integer.parseInt(s.substring(j,j+1));
				sum += a * b;
				b /= 2;
			}
			rob += Integer.toHexString(sum);
			s=s.substring(4).trim();
		}
		return rob;

	}
}
