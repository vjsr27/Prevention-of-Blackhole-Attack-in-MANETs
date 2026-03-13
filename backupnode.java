import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
class backupnode extends JFrame implements ActionListener,Runnable
{
	
	JLabel title,lb1,lb2,jl0;
	JButton  submit,reset;
	JTextArea tf1;
	JPasswordField tf2;
	Boolean flag=false,flag1=false;
	Container c;
	backupnode() 
	{
		super("backup node");
		c=this.getContentPane();
		c.setLayout(null);
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize(); 
		this.setSize(500,300);
		c.setBackground(Color.red);
		
	
		
		lb1=new JLabel("Message :");
		lb1.setBounds(25,25,100,35);
		lb1.setForeground(new Color(0,100,254));
		lb1.setFont(new Font("Dialog",Font.PLAIN,18));
		c.add(lb1);
		
		tf1=new JTextArea ();
		tf1.setBounds(80,70,250,125);
		c.add(tf1);
		
	
		
		submit=new JButton("Exit");
        submit.reshape(100,200,100,30);
        c.add(submit);       
        submit.setFont(new Font("Times New Roman",Font.PLAIN,18));
       	submit.addActionListener(this);
       	
       
       	
       	this.setVisible(true);
       	
       	Thread t=new Thread(this);
       	t.start();
		
	}
	
	public void run()
	{
		try
		{
			ServerSocket sss=new ServerSocket(9999);
			while (true)
			{
				Socket s=sss.accept();
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				String req=(String)ois.readObject();
				
				if(req.equals("FRWD"))
				{
					String snd_dest = (String)ois.readObject();
					String prev_peer = (String)ois.readObject();
					String file_name = (String)ois.readObject();
					byte[] file_data= (byte[])ois.readObject();
					//String lastuser=(String)ois.readObject();
					tf1.append("Received data from "+prev_peer+"\n");
					
					FileInputStream fin1=new FileInputStream("ServerIP.txt");
							byte b[]=new byte[fin1.available()];
							fin1.read(b);
							fin1.close();
					
					String IP=new String(b).trim();
					System.out.println("sending req to server.");
					Socket ss=new Socket(IP,6996);
					System.out.println("connected.");
					ObjectOutputStream oos1=new ObjectOutputStream(ss.getOutputStream());
					ObjectInputStream ois1=new ObjectInputStream(ss.getInputStream());
					oos1.writeObject("FRWDB");
					System.out.println("snd_dest: "+snd_dest);
					String s1[]=snd_dest.split("#");
					oos1.writeObject(s1[s1.length-1]);
					//oos1.writeObject("BACKUP_NODE");
					System.out.println("receiver: "+s1[s1.length-1]);
							
					String	resp=(String)ois1.readObject();	
					System.out.println("resp: "+resp);
					
					String split_resp[]=resp.split("#");
						
					String nxt_peer=split_resp[0].trim();
					String nxt_peer_ip=split_resp[1].trim();
					int port = Integer.parseInt(split_resp[2].trim());
					System.out.println(nxt_peer);
					sendDataToNxtPeer(snd_dest,"BackupNode",file_data,nxt_peer_ip,port,file_name);
					tf1.append("Data sent to next node\n");
				}
				
			}	
		}
		catch(Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void sendDataToNxtPeer(String snd_dest,String cur_node,byte[] file,String nxt_pr_ip,int nxt_pr_prt,String fil_nme)
	{
		try
		{
			Socket s=new Socket(nxt_pr_ip,nxt_pr_prt);
			ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			oos.writeObject("FRWD");
			oos.writeObject(snd_dest);
			oos.writeObject(cur_node);
			oos.writeObject(fil_nme);
			oos.writeObject(file);
			s.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		
	}
	
	public static void main(String args[])
	{
		new backupnode();
	}
}