package compiler2;

import java.util.*;
import java.lang.String;
public class PC 
{
	 byte Memory[]=new byte[16777216];  //32bit too large reduce to 24bit 0-FFFFFC with step 4
	 int register[]=new int[32];     //32 regs
	 String instr[]=new String[4194305]; // 1 more for end tag  2097153
	 String binstr[]=new String[4194305]; // 1 more for end tag
	 HashMap label = new HashMap();

	 int ninstr,ngp,nsp;


	 int $zero=register[0]=0;
	 int $0=register[0]=0;

	 int $v0=register[2];
	 int $v1=register[3];

	 int $a0=register[4];
	 int $a1=register[5];
 	 int $a2=register[6];
	 int $a3=register[7];
	 
	 int $t0=register[8];
	 int $t1=register[9];
	 int $t2=register[10];
	 int $t3=register[11];
	 int $t4=register[12];
	 int $t5=register[13];
	 int $t6=register[14];
	 int $t7=register[15];
	 
	 int $s0=register[16];
	 int $s1=register[17];
	 int $s2=register[18];
	 int $s3=register[19];
	 int $s4=register[20];
	 int $s5=register[21];
	 int $s6=register[22];
	 int $s7=register[23];

	 int $t8=register[24];
	 int $t9=register[25];

	 int $gp=register[28]=8388608;   //800000
	 int $sp=register[29]=16777212;  //fffffc
	 int $fp=register[30];
	 
	 int $ra=register[31];
	 public void clear()
 	 {
		 Arrays.fill(Memory,((Integer)0).byteValue());
		 Arrays.fill(register,0);
		 for (int i=0;i<4194305; i++)
			 instr[i]=binstr[i]="";
		 label.clear();


		 $zero=register[0]=0;
		 $0=register[0]=0;

		 $v0=register[2];
		 $v1=register[3];

		 $a0=register[4];
		 $a1=register[5];
		 $a2=register[6];
		 $a3=register[7];
		 
		 $t0=register[8];
		 $t1=register[9];
		 $t2=register[10];
		 $t3=register[11];
		 $t4=register[12];
		 $t5=register[13];
		 $t6=register[14];
		 $t7=register[15];
		 
		 $s0=register[16];
		 $s1=register[17];
		 $s2=register[18];
		 $s3=register[19];
		 $s4=register[20];
		 $s5=register[21];
		 $s6=register[22];
		 $s7=register[23];

		 $t8=register[24];
		 $t9=register[25];

		 $gp=register[28]=8388608;   //800000
		 $sp=register[29]=16777212;  //fffffc
		 $fp=register[30];
		 
		 $ra=register[31];
	 }

	 public void MemWrite(String ss, int addr) throws Exception
	 {
		 int j=ss.length();
		 int i,t;
		 String st;
		 for (i=0;i<j/2;i++ )
		 {
			 t=0;
			 st=ss.substring(0,2);
			 if (st.charAt(0)>='0' && st.charAt(0)<='9')
				 t=st.charAt(0)-'0';				 
			 else	t=st.charAt(0)-'a'+10;				  
			 if (st.charAt(1)>='0' && st.charAt(1)<='9')
				 t=t*16+(st.charAt(1)-'0');				 
			 else	t=t*16+(st.charAt(1)-'a'+10);				  
			 if (t<=127)
				 Memory[addr+i]=((Integer)t).byteValue();
			 else
				 Memory[addr+i]=((Integer)(-(256-t))).byteValue();
			 ss=ss.substring(2);
		 }
	 }
	 public String MemRead(String operand1,int regKey1, int addr, int d) throws Exception
	 {
		 int ba=-2147483648,num=0,t=0;
		 boolean flag=true;
		 int i;
		 for (i=0;i<d ;i++ )
		 {
			 t=ba+num*256+Memory[addr+i];
			 if (t<0)
				 num=t+2147483647+1;
			 else
				 flag=false;
		 }
		 if (flag==false)
		 {
			 num=-(2147483647-t+1);
		 }
		 register[regKey1]=num;
		 return "Register "+regKey1+"("+operand1+") is updated=> "+num+" ( "+new NewHEX().toHEX(num,d*2)+" )\n";
	 }

	 public String ViewReg() throws Exception
	 {
		 int i;
		 String s;
		 s="\n";
		 for (i=0;i<32 ;i++ )
			 s=s+"       Register "+i+"\t:\t"+new NewHEX().toHEX(register[i],8)+" = "+register[i]+" (10)\n";
		 return s;
	 }
	 public String ViewMemory(int st) throws Exception	
	 {
		 int ed,addr;
		 String outs="";
		 String s="\n\n";
		 if (st<0) st=0;
		 ed=(st+256>register[29]+4)?register[29]+4:st+256;
		 for (addr=st;addr<ed ;addr++ )
		 {
			outs=outs+new NewHEX().toHEX(Memory[addr],2)+" ";
			if (addr%16==15)
			{
				s=s+"          "+new NewHEX().toHEX(addr-15,8)+":  "+outs+"\n";
				outs="";
			}
		 }
		 return s;
	 }
}
