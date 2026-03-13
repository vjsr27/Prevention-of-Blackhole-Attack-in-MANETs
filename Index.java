import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.*;
class Index extends JFrame
{
	
	JLabel jl;
	JTabbedPane jtp;
	Container c;
	
	boolean flg=true;
	Index() 
	{
		super("SERVER MENU");
		this.setLayout(null);

		this.setSize(600,500);
		c=this.getContentPane();
		c.setBackground(Color.white);
		this.setVisible(true);
		
		jtp=new JTabbedPane();
		jtp.addTab("Topology Construct",new TopologyCreation());
		jtp.addTab("DeployNode",new DeployNode());
				
		this.add(jtp);
		jtp.setBounds(0,0,600,500);
		this.setLocation(new Point(100,200));
		new ServerListener(6996);
		
		clearAllNodes();		
		
		
		
		
	}
	
	public static void clearAllNodes()
	{
		try
		{
			Connection con=DBConnection.getConnection();
			PreparedStatement ps=con.prepareStatement("Delete from topo_const");
			ps.executeUpdate();
			PreparedStatement ps1=con.prepareStatement("Delete from node_info");
			ps1.executeUpdate();
			
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		new Index();
		System.gc();
	}
}