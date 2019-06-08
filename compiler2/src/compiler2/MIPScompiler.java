package compiler2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.lang.Integer;

//通过名字匹配寄存器的下标
public class MIPScompiler extends Thread
{
	public int hash(String s)
	{
		  if     (s.equals("$t0"))return 8;
		  else if(s.equals("$t1"))return 9;
		  else if(s.equals("$t2"))return 10;
		  else if(s.equals("$t3"))return 11;
		  else if(s.equals("$t4"))return 12;
		  else if(s.equals("$t5"))return 13;
		  else if(s.equals("$t6"))return 14;
		  else if(s.equals("$t7"))return 15;
		  else if(s.equals("$t8"))return 24;
		  else if(s.equals("$t9"))return 25;
		  
		  else if(s.equals("$s0"))return 16;
		  else if(s.equals("$s1"))return 17;
		  else if(s.equals("$s2"))return 18;
		  else if(s.equals("$s3"))return 19;
		  else if(s.equals("$s4"))return 20;
		  else if(s.equals("$s5"))return 21;
		  else if(s.equals("$t5"))return 22;
		  else if(s.equals("$t5"))return 23;

		  else if(s.equals("$zero") || s.equals("$0"))return 0;

		  else if(s.equals("$v0"))return 02;		  
		  else if(s.equals("$v1"))return 03;

		  else if(s.equals("$a0"))return 04;
		  else if(s.equals("$a1"))return 05;
		  else if(s.equals("$a2"))return 06;
		  else if(s.equals("$a3"))return 07;

		  else if(s.equals("$gp"))return 28;
		  else if(s.equals("$sp"))return 29;
		  else if(s.equals("$fp"))return 30;
		  else if(s.equals("$ra"))return 31;

		  else
		   return -1;
	}
	

	//进行编译代码
	public int compile(String s,int count,PC pc,MIPSGUI mps) throws Exception
	{
		  int index;
		  String operator,operand1,operand2,operand3;
		  mps.runarea.append("\n");
		  mps.runarea.append("Executing LINE "+count+" ( PC="+new NewHEX().toHEX(count*4,8)+" ) =>  "+s+"\n");
		  mps.runarea.append("\n");
		  index=s.indexOf(" ");
		  //operator为操作符
		  operator=s.substring(0,index).trim().toLowerCase();
		  s=s.substring(index+1).trim();
		  if (operator.equals("lui"))
		  {
			  index=s.indexOf(",");
			  //取得是第二个占位字符串，即原寄存器的内容
			  operand1=s.substring(0,index);
			  operand2=s.substring(index+1).trim();
			  operand3="";
		  }
		  else if(!(operator.charAt(0)=='j'))
		  {
			  index=s.indexOf(",");
			  operand1=s.substring(0,index);
			  s=s.substring(index+1).trim();
			  index=s.indexOf(",");		  
			  if (index!=-1)
			  {
				  operand2=s.substring(0,index);
				  operand3=s.substring(index+1).trim();
			  }
			  else
			  {
				  int index1=s.indexOf("(");
				  int index2=s.indexOf(")");
				  operand2=s.substring(index1+1,index2).trim();
				  operand3=s.substring(0,index1).trim();
			  }
		  }
		  else
		  {
			  operand1=s;
			  operand2="";
			  operand3="";
		  }
		  
		  int regKey1,regKey2,regKey3,num3,addr;
		  String ss;

		  
		  if (operator.equals("j"))
		  {
					count=Integer.parseInt(pc.label.get(operand1).toString())-1;
					mps.runarea.append("Jump to "+operand1+" ( PC="+new NewHEX().toHEX((count+1)*4,8)+" )...\n");
		  }
		  else if (operator.equals("jal"))
		  {
					pc.register[hash("$ra")]=count*4+4;
					mps.runarea.append("Register 31($ra) is updated=> "+pc.register[hash("$ra")]+" ( "+new NewHEX().toHEX(pc.register[hash("$ra")],8)+" )\n");
					count=Integer.parseInt(pc.label.get(operand1).toString())-1;
					mps.runarea.append("Jump to "+operand1+" ( PC="+new NewHEX().toHEX((count+1)*4,8)+" )...\n");
		  }
		  else if (operator.equals("jr"))
		  {
					count=pc.register[hash("$ra")]/4-1;
					mps.runarea.append("Jump to $ra ( PC="+new NewHEX().toHEX(pc.register[hash("$ra")],8)+" )...\n");
		  }
		  else if (operator.equals("beq"))
		  {
				    regKey1=hash(operand1);
				    regKey2=hash(operand2);
					if (pc.register[regKey1]==pc.register[regKey2])
					{
						count=Integer.parseInt(pc.label.get(operand3).toString())-1;
						mps.runarea.append("Jump to "+operand3+" ( PC="+new NewHEX().toHEX((count+1)*4,8)+" )...\n");
					}
					else
					{
						mps.runarea.append("Cannot afford to the condition, no jumping occurs.\n");
					}
		  }
		  else if (operator.equals("bne"))
		  {
				    regKey1=hash(operand1);
				    regKey2=hash(operand2);
					if (pc.register[regKey1]!=pc.register[regKey2])
					{
						count=Integer.parseInt(pc.label.get(operand3).toString())-1;
						mps.runarea.append("Jump to "+operand3+" ( PC="+new NewHEX().toHEX((count+1)*4,8)+" )...\n");
					}
					else
					{
						mps.runarea.append("Cannot afford to the condition, no jumping occurs.\n");
					}

		  }
		  else if (operator.equals("slt"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    regKey3=hash(operand3);
					if (pc.register[regKey2]<pc.register[regKey3])
					{
						pc.register[regKey1]=1;
						mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+1+" ( 00000001 )\n");
					}
					else
					{
						pc.register[regKey1]=0;
						mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+0+" ( 00000000 )\n");
					}
		  }
		  else if (operator.equals("slti"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					if (pc.register[regKey2]<num3)
					{
						pc.register[regKey1]=1;
						mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+1+" ( 00000001 )\n");
					}
					else
					{
						pc.register[regKey1]=0;
						mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+0+" ( 00000000 )\n");
					}
		  }
		  else if (operator.equals("sw"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					addr=pc.register[regKey2]+num3;
					ss=new NewHEX().toHEX(pc.register[regKey1],8);
					pc.MemWrite(ss,addr);
					mps.runarea.append("Memory[ "+new NewHEX().toHEX(addr,8)+" .. "+new NewHEX().toHEX(addr+4,8)+" ] is updated => "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }
		  else if (operator.equals("sh"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					addr=pc.register[regKey2]+num3;
					ss=new NewHEX().toHEX(pc.register[regKey1],4);
					pc.MemWrite(ss,addr);
					mps.runarea.append("Memory[ "+new NewHEX().toHEX(addr,8)+" .. "+new NewHEX().toHEX(addr+2,8)+" ] is updated => "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],4)+" )\n");
		  }
		  else if (operator.equals("sb"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					addr=pc.register[regKey2]+num3;
					ss=new NewHEX().toHEX(pc.register[regKey1],2);
					pc.MemWrite(ss,addr);
					mps.runarea.append("Memory[ "+new NewHEX().toHEX(addr,8)+" ] is updated => "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],2)+" )\n");
		  }
		  else if (operator.equals("lw"))
		  {		
					regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					addr=pc.register[regKey2]+num3;
					mps.runarea.append(pc.MemRead(operand1,regKey1, addr, 4));
		  }
		  else if (operator.equals("lh"))
		  {
					regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					addr=pc.register[regKey2]+num3;
					mps.runarea.append(pc.MemRead(operand1,regKey1, addr, 2));
		  }
		  else if (operator.equals("lb"))
		  {
					regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					addr=pc.register[regKey2]+num3;
					mps.runarea.append(pc.MemRead(operand1,regKey1, addr, 1));
		  }
		  else if (operator.equals("lui"))
		  {
					regKey1=hash(operand1);
				    num3=Integer.parseInt(operand2);
					pc.register[regKey1]=num3*(1<<16);
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }
		  else if (operator.equals("sll"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					pc.register[regKey1]=pc.register[regKey2]<<num3;
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }

		  else if (operator.equals("srl"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					pc.register[regKey1]=pc.register[regKey2]>>>num3;
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }
					
		  else if (operator.equals("andi"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					pc.register[regKey1]=pc.register[regKey2] & num3;
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }

		  else if (operator.equals("ori"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					pc.register[regKey1]=pc.register[regKey2] | num3;
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }

		  else if (operator.equals("addi"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					pc.register[regKey1]=pc.register[regKey2] + num3;
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }

		  else if (operator.equals("subi"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
				    num3=Integer.parseInt(operand3);
					pc.register[regKey1]=pc.register[regKey2] - num3;
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }
		  else if (operator.equals("add"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
					regKey3=hash(operand3);
					pc.register[regKey1]=pc.register[regKey2] + pc.register[regKey3];
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }
		  else if (operator.equals("sub"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
					regKey3=hash(operand3);
					pc.register[regKey1]=pc.register[regKey2] - pc.register[regKey3];
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }
		  else if (operator.equals("and"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
					regKey3=hash(operand3);
					pc.register[regKey1]=pc.register[regKey2] & pc.register[regKey3];
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }
		  else if (operator.equals("or"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
					regKey3=hash(operand3);
					pc.register[regKey1]=pc.register[regKey2] | pc.register[regKey3];
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }
		  else if (operator.equals("nor"))
		  {
				    regKey1=hash(operand1);
					regKey2=hash(operand2);
					regKey3=hash(operand3);
					pc.register[regKey1]=~(pc.register[regKey2] | pc.register[regKey3]);
					mps.runarea.append("Register "+regKey1+"("+operand1+") is updated=> "+pc.register[regKey1]+" ( "+new NewHEX().toHEX(pc.register[regKey1],8)+" )\n");
		  }
		
		  return count;
	}
}



