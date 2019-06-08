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
		helparea.append("\n\n使用说明：\n\n");
		helparea.append("1、本MIPS模拟器支持的指令如下：\nadd,addi,sub,subi,and,andi,or,ori,nor,beq,bne,j,jr,jal,lw,lh,lb,sw,sh,sb,");
		helparea.append("其中所有的跳转指令第三个操作数只能为一个行标签(不支持相对地址以及绝对地址)，标签可以写在一行开头，以冒号结尾。\n");
		helparea.append("2、输入或修改MIPS指令或者二进制指令后请先保存后再进行汇编或运行。\n");
		helparea.append("3、MIPS和二进制程序格式为一行一条指令，先输指令，直到END 000004(FC000001)结束，后面则是数据内存写入，使用DW在以PC为内存地址的区域写入数据，二进制代码则直接以数据作为改行代码。\n");
		helparea.append("4、程序设置了标准的32个数据寄存器和000000-ffffff的内存，内存地址空间仅为24位而不是32位是受到了数组空间的限制，其中默认000000-7fffff是指令内存，接下来区域为数据内存，静态全局数据内存始于800000向上存储，动态栈数据内存始于fffffc向下存储。当然可以在指令后的任何空间存储数据，并且是运行小型MIPS程序的较好方法，可以直接用指令导入指令内存后面的数据内存。\n");
		helparea.append("存储器，  编译器：毕永成，樊耀坤。参与人员：曾垚\n\n");
		helparea.append("2018-06-21\n");
	}
}