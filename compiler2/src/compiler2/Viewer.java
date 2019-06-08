package compiler2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
class RegViewer extends JFrame
{
	TextArea regarea;
	public RegViewer(PC pc)
	{
		super("View Registers");
	    int width=400;                           
		int height=460;
		setSize(width,height);
		regarea=new TextArea("",60,80,TextArea.SCROLLBARS_NONE);
		add(regarea);
		try{
			regarea.setText(pc.ViewReg());
		}
		catch (Exception ee){}
	}
}
class MemViewer extends JFrame implements ActionListener
{
	TextArea memarea;
	JButton insm,datam,gpm,spm,random,pageup,pagedown;
	JTextField m_start;
	int startm;
	PC pc;
	int Linecount;

	public MemViewer(PC pc, int Linecount)
	{
		super("View Memory");
		this.pc=pc;
		this.Linecount=Linecount;
	    int width=600;                           
		int height=430;
		setSize(width,height);

		memarea=new TextArea("",15,65,TextArea.SCROLLBARS_NONE);
		startm=0;
		insm=new JButton("        View Instruction Memory        ");
		datam=new JButton("  View Data Memory After Instruction  ");
		gpm=new JButton("  View Global Static Data Memory ");
		spm=new JButton("   View Dynamic Stack Data Memory   ");
		random=new JButton("   View Memory with Random Access   ");
		pageup=new JButton("            Page Up            ");
		pagedown=new JButton("              Page Down           ");
		m_start=new JTextField(10);
		JPanel pane=new JPanel(); 
		insm.addActionListener(this);
		datam.addActionListener(this);
		gpm.addActionListener(this);
		spm.addActionListener(this);
		random.addActionListener(this);
		pageup.addActionListener(this);
		pagedown.addActionListener(this);
		pane.add(memarea);
		pane.add(insm);
		pane.add(datam);
		pane.add(gpm);
		pane.add(spm);
		pane.add(pageup);
		pane.add(pagedown);
		pane.add(random);		
		pane.add(m_start);
		add(pane);
		memarea.setText(null);
	}
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource()==insm)
		{
			memarea.setText(null);
			try{
				startm=0;
				memarea.append(pc.ViewMemory(startm));
			}
			catch (Exception ee){}
		}
		if (e.getSource()==datam)
		{
			memarea.setText(null);
			try{
				startm=Linecount*4+4;
				memarea.append(pc.ViewMemory(startm-startm%16));
			}
			catch(Exception ee){}
		}
		if (e.getSource()==gpm)
		{
			memarea.setText(null);
			try{
				startm=pc.register[28];			
				memarea.append(pc.ViewMemory(startm-startm%16));
			}
			catch(Exception ee){}
		}
		if (e.getSource()==spm)
		{
			memarea.setText(null);
			try{
				startm=pc.register[29]-252;
				memarea.append(pc.ViewMemory(startm));
			}
			catch(Exception ee){}
		}
		if (e.getSource()==random)
		{
			String s=m_start.getText();
			memarea.setText(null);
			try{
				startm=new NewHEX().HEXtoInt(s.trim().toLowerCase());			
				memarea.append(pc.ViewMemory(startm-startm%16));
			}
			catch(Exception ee){}
		}
		if (e.getSource()==pageup)
		{
			memarea.setText(null);
			try{
				startm=startm-256;
				memarea.append(pc.ViewMemory(startm));
			}
			catch(Exception ee){}
		}
		if (e.getSource()==pagedown)
		{
			memarea.setText(null);
			try{
				if (startm+256<pc.register[29]+4)
					startm=startm+256;
				memarea.append(pc.ViewMemory(startm));
			}
			catch(Exception ee){}
		}
	}
}
class HelpViewer extends JFrame
{
	TextArea helparea;
	public HelpViewer()
	{
		super("Help");
		int width=600;                           
		int height=550;
		setSize(width,height);
		helparea=new TextArea("",60,80,TextArea.SCROLLBARS_NONE);
		add(helparea);
		helparea.append("\n\nMIPS Assembler Simulator \n\n");
		helparea.append("\n\nʹ��˵����\n\n");
		helparea.append("1����MIPSģ����֧�ֵ�ָ�����£�\nadd,addi,sub,subi,and,andi,or,ori,nor,beq,bne,j,jr,jal,lw,lh,lb,sw,sh,sb,");
		helparea.append("�������е���תָ�������������ֻ��Ϊһ���б�ǩ(��֧����Ե�ַ�Լ����Ե�ַ)����ǩ����д��һ�п�ͷ����ð�Ž�β��\n");
		helparea.append("2��������޸�MIPSָ����߶�����ָ������ȱ�����ٽ��л������С�\n");
		helparea.append("3��MIPS�Ͷ����Ƴ����ʽΪһ��һ��ָ�����ָ�ֱ��END 000004(FC000001)�������������������ڴ�д�룬ʹ��DW����PCΪ�ڴ��ַ������д�����ݣ������ƴ�����ֱ����������Ϊ���д��롣\n");
		helparea.append("4�����������˱�׼��32�����ݼĴ�����000000-ffffff���ڴ棬�ڴ��ַ�ռ��Ϊ24λ������32λ���ܵ�������ռ�����ƣ�����Ĭ��000000-7fffff��ָ���ڴ棬����������Ϊ�����ڴ棬��̬ȫ�������ڴ�ʼ��800000���ϴ洢����̬ջ�����ڴ�ʼ��fffffc���´洢����Ȼ������ָ�����κοռ�洢���ݣ�����������С��MIPS����ĽϺ÷���������ֱ����ָ���ָ���ڴ����������ڴ档\n");
		helparea.append("�洢����  �������������ɣ���ҫ����������Ա������\n\n");
		helparea.append("2018-06-21\n");
	}
}