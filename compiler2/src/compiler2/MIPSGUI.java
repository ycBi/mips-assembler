package compiler2;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

class MIPSGUI extends JFrame implements ActionListener
{
	TextArea mipsarea,binarea,runarea;
	JButton Loadmips,Savemips,Asm,ViewReg,ViewMem,RuntoEnd,Help;
	PC pc;
	int currentins,nextins,lc,totalline;
	boolean stoprun;
	String mipsold;
	String binold;

	public MIPSGUI()
	{
		super("MIPS Assembler Simulator ");
	    int width=1024;                           
		int height=768;
		setSize(width,height);
	    mipsarea=new TextArea(40,38);
		binarea=new TextArea(40,38);
		runarea=new TextArea(40,54);
		Loadmips=new JButton("            Load MIPS            ");
		Savemips=new JButton("            Save MIPS            ");
		Asm=new JButton("             Asm             ");
		ViewReg=new JButton("                View Regs                   ");
		ViewMem=new JButton("                View Memory                 ");
		RuntoEnd=new JButton("Run the Whole MIPS Program");
		Help=new JButton("                 Help                 ");

		Loadmips.addActionListener(this);
		Savemips.addActionListener(this);
		Asm.addActionListener(this);
		ViewReg.addActionListener(this);
		ViewMem.addActionListener(this);
		RuntoEnd.addActionListener(this);
		Help.addActionListener(this);
		JPanel  pane=new JPanel();
		pc=new PC();
		currentins=-1;
		stoprun=false;
		mipsold="";
		binold="";

		pane.add(mipsarea);		
		pane.add(binarea);
		pane.add(runarea);
		pane.add(Loadmips);
		pane.add(Savemips);
		pane.add(Asm);
		pane.add(RuntoEnd);
		pane.add(ViewReg);
		pane.add(ViewMem);
		pane.add(Help);
		add(pane);
		

	}

	public void actionPerformed(ActionEvent e)
	{

		if (e.getSource()==ViewReg)
		{
			RegViewer rv=new RegViewer(pc);
			rv.show();
		}
		if (e.getSource()==Help)
		{
			HelpViewer hv=new HelpViewer();
			hv.show();
		}
		if (e.getSource()==ViewMem)
		{
			MemViewer mv=new MemViewer(pc,lc);
			mv.show();
		}
		if (e.getSource()==Loadmips)
		{		
			JFileChooser jfc=new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("MIPS Assemble Files(.Asm;.Mips;.Txt)", "mips", "asm","txt");
		    jfc.setFileFilter(filter);

			if (jfc.showOpenDialog(Loadmips)== JFileChooser.APPROVE_OPTION)
			{
				File fi;
				fi=jfc.getSelectedFile();
				LoadMIPS(fi);
			}
		}

		if (e.getSource()==RuntoEnd)
		{
			if (mipsarea.getText().equals("") && !binarea.getText().equals(""))
			{
				JOptionPane.showMessageDialog(null,"Please UnAsm First!");
			}
			else
			{
				runarea.setText(null);
				{
					if (ASM())
					{
						stoprun=false;
						lc=toInstr();
						currentins=-1;
						nextins=lc;
						int r=run(currentins+1,nextins-1,lc);
					}
				}
			}
		}
		if(e.getSource()==Savemips)
		{
			JFileChooser jfc=new JFileChooser("");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("MIPS Assemble Files(.Asm)", "asm");
		    jfc.setFileFilter(filter);

			if (jfc.showSaveDialog(Savemips)== JFileChooser.APPROVE_OPTION)
			{
				File fi;
				fi=jfc.getSelectedFile();
				String fn;
				if (fi.getPath().contains("."))
					fn=fi.getPath();
				else fn=fi.getPath()+".asm";
				File f2=new File(fn);
				SaveMIPS(f2);
			}
		}
		if(e.getSource()== Asm)
		{
			ASM();
		}
	}


	public int toInstr()
	{

			String s=mipsarea.getText();
			int index;
			index=s.indexOf("\n");
			String Instruction;
			int Linecount=0;
			Instruction=s.substring(0,index);
			int index2;
			index2=Instruction.indexOf(">");
			Instruction=Instruction.substring(index2+1).trim();
			s=s.substring(index+1);
			while(!Instruction.contains("END"))
			{
				  index=Instruction.trim().indexOf(":");
				  if (index!=-1)
				  {
					  pc.label.put(Instruction.trim().substring(0,index),Linecount);
					  Instruction=Instruction.trim().substring(index+1).trim();
				  }
				  pc.instr[Linecount]=Instruction;
				  Linecount++;
				  index=s.indexOf("\n");
				  Instruction=s.substring(0,index);
				  index2=Instruction.indexOf(">");
				  Instruction=Instruction.substring(index2+1).trim();
				  s=s.substring(index+1);
			}
 		    pc.instr[Linecount]="END 00000004";

			int j=Linecount+1;

	//////////////////Instruction END tag
			pc.Memory[Linecount*4]=(new Integer(-4)).byteValue();
			pc.Memory[Linecount*4+1]=(new Integer(0)).byteValue();
			pc.Memory[Linecount*4+2]=(new Integer(0)).byteValue();
			pc.Memory[Linecount*4+3]=(new Integer(1)).byteValue();
			
	//////////////////////////////////////
			String Datai;
		    index=s.indexOf("\n");
			if (index!=-1)
			{
				Datai=s.substring(0,index);
				s=s.substring(index+1);
				int datains=0;
				while(true)
				{
					index=Datai.trim().indexOf(":");
					if (index!=-1)
					{
						 pc.label.put(Datai.trim().substring(0,index),Linecount);
						 Datai=Datai.trim().substring(index+1).trim();
					}
					index=Datai.trim().indexOf(" ");
					Datai=Datai.trim().substring(index+1).trim();
					String value="";
					try{
						value=new NewHEX().toHEX(Integer.parseInt(Datai),8);
						pc.MemWrite(value,(Linecount+datains+1)*4);
						pc.instr[j++]="DW "+Datai;
					}
					catch (Exception ee){}
					datains++;
					index=s.indexOf("\n");
					if (index==-1) break;
					Datai=s.substring(0,index);
					s=s.substring(index+1);
				}
			}
			totalline=j;
			return Linecount;
	}

	public int run(int startli, int endli, int lc)
	{
	//execute
			int lin=0,oldlin=-1;
			for (lin=startli;oldlin!=endli && !(pc.instr[lin].contains("END"));lin++ )
			{
				oldlin=lin;
				try{
					lin=new MIPScompiler().compile(pc.instr[lin],lin,pc,this);
				}
				catch (Exception ee){}
			}
			if (pc.instr[lin].toLowerCase().contains("end"))
			{
				stoprun=false;
				runarea.append("\nExecuting Complete!\n");
				return -1;
			}
			return lin;
	}

	public void LoadMIPS(File fi)
	{
			int nins=0;
			try{
				String Instruction;
				FileReader fr=new FileReader(fi);
				BufferedReader br=new BufferedReader(fr);
				mipsarea.setText(null);
				Instruction=br.readLine();
				while(Instruction!=null)
				{
					if (!Instruction.trim().equals(""))
					{
						mipsarea.append("<"+nins+">\t"+Instruction.trim()+"\n");
						nins++;
					}
					Instruction=br.readLine();
				}
			}
			catch (Exception ee){}
			mipsold=mipsarea.getText();
	}

	public void LoadBIN(File fi)
	{
			int nins=0;
			try{
				String Instruction_b;
				FileReader fr=new FileReader(fi);
				BufferedReader br=new BufferedReader(fr);
				binarea.setText(null);
				Instruction_b=br.readLine();
				while(Instruction_b!=null)
				{
					if (!Instruction_b.trim().equals(""))
					{
						binarea.append("<"+nins+">\t"+Instruction_b.trim()+"\n");
						nins++;
					}
					Instruction_b=br.readLine();
				}
			}
			catch (Exception ee){}
			binold=binarea.getText();
	}

	public void SaveMIPS(File fi)
	{
			int nins=0;
			try{
				FileWriter fw=new FileWriter(fi);
				BufferedWriter bw=new BufferedWriter(fw);
                String text = mipsarea.getText();
				String text2="";
				int i=0;
				while (i<text.length())
				{
					if (text.charAt(i)=='<')
					{
						while(text.charAt(i)!='>')
							i++;
						i++;
					}
					if (text.charAt(i)=='\t' && text.charAt(i-1)=='>')
						i++;
					text2=text2+text.charAt(i);
					i++;
				}
                bw.write(text2, 0, text2.length());
                bw.flush();
                fw.close();
				mipsarea.setText(null);
				LoadMIPS(fi);
			}
			catch (Exception ee){}
	}

	public void SaveBIN(File fi)
	{
			int nins=0;
			try{
				FileWriter fw=new FileWriter(fi);
				BufferedWriter bw=new BufferedWriter(fw);
                String text = binarea.getText();
				String text2="";
				int i=0;
				while (i<text.length())
				{
					if (text.charAt(i)=='<')
					{
						while(text.charAt(i)!='>')
							i++;
						i++;
					}
					if (text.charAt(i)=='\t' && text.charAt(i-1)=='>')
						i++;
					text2=text2+text.charAt(i);
					i++;
				}
                bw.write(text2, 0, text2.length());
                bw.flush();
                fw.close();
				binarea.setText(null);
				LoadBIN(fi);
			}
			catch (Exception ee){}
	}
	public boolean ASM()
	{

		if (!mipsarea.getText().equals(mipsold))
		{
			JOptionPane.showMessageDialog(null,"Please Save First!");
			return false;
		}
		else
		{
			pc.clear();
			binarea.setText(null);
			int lb=	toInstr();
			for (int i=0;i<totalline ;i++ )
			{
				////System.out.println(pc.instr[i]);
			}
			Asm aa = new Asm();
			for (int i=0;i<totalline ;i++ )
			{
				String m = aa.StoB(pc.instr[i],i,pc);
				m = aa.BtoH(m);
				if (i<lb)
				{
					try{
						pc.MemWrite(m,i*4);
						}
					catch (Exception ee){}
				}
				pc.binstr[i] = m;  //store the code
				binarea.append("<"+i+">\t"+m+"\n"); //display the code
			}
			return true;
		}

	}

	public static void main(String[] args) 
	{
		MIPSGUI mipsg=new MIPSGUI();
		mipsg.addWindowListener(
			new WindowAdapter() 
			{
				public void windowClosing(WindowEvent e)
				{
					System.exit(0);
				}
			}
		);
		mipsg.show();
	}
}
