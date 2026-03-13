import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.Border;

class Index extends JFrame implements ActionListener
{
	
	static JCheckBox jcbx,jcb,jcb1;
	
	
	JLabel j,j1,j2,jl1,jl2,jl3,jl11,jl12,jl21,jl22,jl31,jl13;
	JTextField jtf1;
	JTextArea jta;
	JButton jb,jb1,jb2;
	Container c;
	static JTextArea jta2,jta21,jta3;
	JPanel jp,jp1,jp2;
	JScrollPane jsp,jsp1,jsp2;
	JRadioButton jrb,jrb1;
	ButtonGroup bg;
	
	
	public static JComboBox jcmb;
	public static String Usr_Name="";
	
	byte [] fileData;
	String file_name="";
	
	Index( String nme,String port) 
	{
		super("Node : "+nme.toUpperCase());
		new ServerListener(Integer.parseInt(port));
		this.setLayout(null);
		this.setSize(409,635);
		c=this.getContentPane();
		c.setBackground(Color.white);
		
		Usr_Name=nme.toUpperCase();
		
		jp=new JPanel();
		jp1=new JPanel();
		jp2=new JPanel();
		j=new JLabel(new ImageIcon("red-mail-send-icon.png"));
		j1=new JLabel(new ImageIcon("Nuvola_apps_remote.png"));
		j2=new JLabel(new ImageIcon("red-mail-receive-icon.png"));
		
		jl1=new JLabel("SENDER");
		jl11=new JLabel("Dest : ");
		jl12=new JLabel("Message : ");
		jl13=new JLabel("Status : ");
		jrb=new JRadioButton("ON");
		jrb1=new JRadioButton("OFF");
		bg=new ButtonGroup();
		jtf1=new JTextField();
		jrb.setSelected(true);
		jcmb=new JComboBox();
		
		bg.add(jrb);
		bg.add(jrb1);
		
		
		jrb.setBackground(Color.white);
		jrb1.setBackground(Color.white);
		
		jl2=new JLabel("HOP");
		jl21=new JLabel("Received :");
		jl22=new JLabel("Sending :");
		jta2=new JTextArea();
		jta21=new JTextArea();
		jsp=new JScrollPane();
		jsp.setViewportView(jta2);
		jsp1=new JScrollPane();
		jsp1.setViewportView(jta21);
		jcb=new JCheckBox("Carousel Attack");
		jcb1=new JCheckBox("Stretch Attack");
		jcbx=new JCheckBox("Reset");
		
		jl3=new JLabel("RECEIVER");
		jl31=new JLabel("Message :");
		jsp2=new JScrollPane();
		jta3=new JTextArea();
		jsp2.setViewportView(jta3);
		
		
		
		
		jta=new JTextArea();
		jb=new JButton("Send");
		jb1=new JButton("Clear");
		jb2=new JButton("Browse");
		
		
		
		jcb.setBackground(Color.white);
		jcb1.setBackground(Color.white);
		jcbx.setBackground(Color.white);
		
		jp.setLayout(null);
		jp1.setLayout(null);
		jp2.setLayout(null);
		
		addComponent(this,jp,0,0,400,200);
		addComponent(this,jp1,0,200,400,200);
		addComponent(this,jp2,0,400,400,200);
		
		addComponent(jp,j,235,-10,200,200);
		addComponent(jp,jl1,165,0,150,30);
		addComponent(jp,jl11,20,30,100,30);
		addComponent(jp,jcmb,85,35,100,25);
		addComponent(jp,jl13,195,35,100,25);
		addComponent(jp,jrb,238,35,45,25);
		addComponent(jp,jrb1,285,35,50,25);
		addComponent(jp,jl12,20,67,100,30);
		addComponent(jp,jb2,85,70,100,25);
		addComponent(jp,jtf1,85,107,175,25);
		addComponent(jp,jb,20,145,100,25);
		addComponent(jp,jb1,125,145,100,25);
		
		addComponent(jp1,j1,250,40,150,150);
		addComponent(jp1,jl2,175,0,150,30);
		addComponent(jp1,jcb,20,30,125,30);
		addComponent(jp1,jcb1,150,30,125,30);
		addComponent(jp1,jl21,20,75,100,30);
		addComponent(jp1,jl22,20,125,100,30);
		addComponent(jp1,jsp,80,65,175,50);
		addComponent(jp1,jsp1,80,120,175,50);
		addComponent(jp1,jcbx,285,20,100,30);
		
		
		
		addComponent(jp2,j2,235,-10,200,200);
		addComponent(jp2,jl3,165,0,150,30);
		addComponent(jp2,jl31,20,33,100,30);
		addComponent(jp2,jsp2,80,45,195,140);
		
		jcmb.addItem("Select");
				
		this.setVisible(true);
		
		jp.setBackground(Color.WHITE);
		jp1.setBackground(Color.WHITE);
		jp2.setBackground(Color.WHITE);
		
		Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		jp.setBorder(raisedetched );
		jp1.setBorder(raisedetched );
		jp2.setBorder(raisedetched );
		
		jrb.addActionListener(this);
		jcbx.addActionListener(this);
		jcb.addActionListener(this);
		jcb1.addActionListener(this);
		jrb1.addActionListener(this);
		jb.addActionListener(this);
		jb1.addActionListener(this);
		jb2.addActionListener(this);
		
	}
	
	public void addComponent(Container con,Component cc,int x,int y,int w,int h)
	{
		con.add(cc);
		cc.setBounds(x,y,w,h);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		Object source=ae.getSource();
		if(source==jrb || source==jrb1)
		{
			try
			{
				int ch=0;
				String status="",IP="";
				if(jrb.isSelected())
				{
					status="ON";
				}
				else
				{
					status="OFF";
				}
				
				FileInputStream fin=new FileInputStream("ServerIP.txt");
				while((ch=fin.read())!=-1)
				IP+=(char)ch;
				IP.trim();
										
				Socket s=new Socket(IP,6996);
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				oos.writeObject("STATUS");
				oos.writeObject(Usr_Name);
				oos.writeObject(status);
				
				String resp=(String)ois.readObject();
				s.close();
				JOptionPane.showMessageDialog(null,resp);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		else if(source==jb)
		{
			try
			{
				String dest=(String)jcmb.getSelectedItem();
			
				int ch=0;
				String IP="";
				FileInputStream fin=new FileInputStream("ServerIP.txt");
				while((ch=fin.read())!=-1)
				IP+=(char)ch;
				IP.trim();
										
				Socket s=new Socket(IP,6996);
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				oos.writeObject("RREQ");
				oos.writeObject(Usr_Name+"#"+dest);
				oos.writeObject(Usr_Name);
				oos.writeObject(false);
				
				boolean flg = (Boolean)ois.readObject();
				
				if(flg)
				{
					String resp=(String)ois.readObject();
					
					String split_resp[]=resp.split("#");
				
					String nxt_peer=split_resp[0].trim();
					String nxt_peer_ip=split_resp[1].trim();
					int port = Integer.parseInt(split_resp[2].trim());
					
					jta21.setText("Sending file using node "+nxt_peer);
					
					sendDataToNxtPeer(Usr_Name+"#"+dest,Usr_Name,fileData,nxt_peer_ip,port,file_name);
				
				}
				else
				{
					String resp=(String)ois.readObject();
					JOptionPane.showMessageDialog(null,resp);
				}
				
				s.close();
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
		}
		else if(source==jb1)
		{
			jtf1.setText("");
		}
		else if(source==jb2)
		{
			try
			{
				File file;
				JFileChooser jf=new JFileChooser(".");
				int m=jf.showOpenDialog(null);
				if (m==JFileChooser.APPROVE_OPTION)
				{
					file=jf.getSelectedFile();
					if(file.exists())
					{
						String fpath=file.getPath();
						file_name=file.getName();
						jtf1.setText(fpath);
						fileData = new byte[(int)file.length()];
						DataInputStream dis = new DataInputStream((new FileInputStream(file)));
						dis.readFully(fileData);
						dis.close();
					}
					else
					{
						JOptionPane.showMessageDialog(null,"File dose not exists!!!");
						jtf1.setText("File dose not exists!!!");
					}
					
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		else if(source==jcbx|source==jcb|source==jcb1)
		{
			System.out.println("IN");
			if(source==jcbx)
			{
				jcb.setSelected(false);
				jcb1.setSelected(false);
				jcbx.setSelected(false);	
			}
			else if(source==jcb)
			{
				jcb1.setSelected(false);
			}
			else if(source==jcb1)
			{
				jcb.setSelected(false);
			}
			
			
		}
		
		
	}
	
	public void sendDataToNxtPeer(String snd_dest,String cur_node,byte[] file,String nxt_pr_ip,int nxt_pr_prt,String fil_nm)
	{
		try
		{
			Socket s=new Socket(nxt_pr_ip,nxt_pr_prt);
			ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			oos.writeObject("FRWD");
			oos.writeObject(snd_dest);
			oos.writeObject(cur_node);
			oos.writeObject(fil_nm);
			oos.writeObject(file);
			s.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		new Index("A","2000");
	}
}