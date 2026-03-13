/****************************************************************/
/*                      TopologyCreation	                            */
/*                                                              */
/****************************************************************/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;
/**
 * Summary description for TopologyCreation
 *
 */
public class TopologyCreation extends JPanel
{
	
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private static JComboBox jComboBox1;
	private static JComboBox jComboBox2;
	private JButton jButton1;
	private JButton jButton3;
	private JButton jButton4;
	static JCheckBox jcb;
	
	Font f=new Font("Dialog",Font.PLAIN,25);
	
	static Vector allNodesx;
	static Vector allNodesy;



	public TopologyCreation()
	{
		super();

		initializeComponent();
	}


	private void initializeComponent()
	{
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		jLabel4 = new JLabel(new ImageIcon("communications-icon.jpg"));
		jComboBox1 = new JComboBox();
		jComboBox2 = new JComboBox();
		jButton1 = new JButton();
		jButton3 = new JButton();
		jButton4 = new JButton();
		jcb=new JCheckBox("Activate IDS");
		jcb.setBackground(Color.white);

		//
		// jLabel1
		//
		jLabel1.setText(" TOPOLOGY CONSTRUCTION ");
		//
		// jLabel2
		//
		jLabel2.setText("Nodes");
		//
		// jLabel3
		//
		jLabel3.setText("Nodes");
		
		//
		// jComboBox1
		//
		jComboBox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				jComboBox1_actionPerformed(e);
			}

		});
		//
		// jComboBox2
		//
		jComboBox2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				jComboBox2_actionPerformed(e);
			}

		});
		//
		// jButton1
		//
		jButton1.setText("Enter");
		jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				jButton1_actionPerformed(e);
			}

		});
		
		
		jButton3.setText("Clear Topology");
		jButton3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				jButton3_actionPerformed(e);
			}

		});
		
		
		jButton4.setText("Clear Nodes");
		jButton4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				jButton4_actionPerformed(e);
			}

		});
		
		this.setLayout(null);
		
		addComponent(this, jcb, 450,30,100,30);
		addComponent(this, jLabel1, 100,-40,535,111);
		addComponent(this, jLabel2, 165,140,100,30);
		addComponent(this, jLabel3, 165,200,100,30);
		addComponent(this, jComboBox1, 340,140,100,25);
		addComponent(this, jComboBox2, 340,200,100,25);
		addComponent(this, jButton1, 230,260,150,35);
		addComponent(this, jButton3, 125,325,150,35);
		addComponent(this, jButton4, 330,325,150,35);
		addComponent(this, jLabel4, 0,0,610,458);
		
		//
		// TopologyCreation
		//
		
		
		jLabel1.setFont(f);
		jLabel2.setFont(f);
		jLabel3.setFont(f);
		
		//this.setLocation(new Point(0, 0));
		this.setSize(new Dimension(600, 500));
	}
	
	
	public static void addNode(Vector x,Vector y)
	{
		int ch=65;
		allNodesx=x;
		allNodesy=y;
		jComboBox1.removeAllItems();
		jComboBox2.removeAllItems();
		for(int i=0;i<x.size();i++)
		{
			int j=ch+i;
			jComboBox1.addItem(String.valueOf((char)j));
		}
	}

	/** Add Component Without a Layout Manager (Absolute Positioning) */
	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	{
		c.setBounds(x,y,width,height);
		container.add(c);
	}

	private void jComboBox1_actionPerformed(ActionEvent e)
	{
	
		String source =(String) jComboBox1.getSelectedItem();
		int ch=65;
		if(source!=null)
		{
			jComboBox2.removeAllItems();
			for(int ii=0;ii<allNodesx.size();ii++)
			{
				int j=ch+ii;
				if(!source.equals(String.valueOf((char)j)))
				{
					jComboBox2.addItem(String.valueOf((char)j));
				}
			}
		}
		
	}

	private void jComboBox2_actionPerformed(ActionEvent e)
	{
		
		
		Object o = jComboBox2.getSelectedItem();
		
		
	}

	private void jButton1_actionPerformed(ActionEvent e)
	{
		
		updateNodeInfo();

	}

	
	private void jButton3_actionPerformed(ActionEvent e)
	{
		clearTopo();
	}
	
	private void jButton4_actionPerformed(ActionEvent e)
	{
		clearNodes();		
	}
	
	public static void clearTopo()
	{
		try
		{
			Connection con=DBConnection.getConnection();
			PreparedStatement ps=con.prepareStatement("Delete from topo_const");
			int i=ps.executeUpdate();
				if(i==1|i==0)
				{
					DeployNode.topo_const.removeAllElements();
					JOptionPane.showMessageDialog(null,"Cleared Topology !!! Re-Construct Topology");
				}
		
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	public static void clearNodes()
	{
		try
		{
			Connection con=DBConnection.getConnection();
			PreparedStatement ps=con.prepareStatement("Delete from topo_const");
			int i=ps.executeUpdate();
			System.out.println("IN "+i);
				if(i==1|i==0)
				{
					DeployNode.topo_const.removeAllElements();
					DeployNode.xx.removeAllElements();
					DeployNode.yy.removeAllElements();
					jComboBox1.removeAllItems();
					jComboBox2.removeAllItems();
					JOptionPane.showMessageDialog(null,"Nodes Cleared !!! ");
				}
		
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}

	public void updateNodeInfo()
	{
		Socket soc;

		ObjectInputStream dis;

		ObjectOutputStream dos;

		try
		{
			
			String startingNode = jComboBox1.getSelectedItem().toString().trim();

			String endingNode = jComboBox2.getSelectedItem().toString().trim();

			try
			{
				Connection con=DBConnection.getConnection();
				PreparedStatement ps=con.prepareStatement("Select * from topo_const where Source_Node=? and Dest_Node=?");
				ps.setString(1,startingNode);
				ps.setString(2,endingNode);
				ResultSet rs=ps.executeQuery();
				if(rs.next())
				{
					JOptionPane.showMessageDialog(null,"Path "+startingNode+" => "+endingNode+" already exists");
				}
				else
				{
					PreparedStatement ps1=con.prepareStatement("Insert into topo_const values (?,?)");
					ps1.setString(1,startingNode);
					ps1.setString(2,endingNode);
					int p1=ps1.executeUpdate();
					PreparedStatement ps2=con.prepareStatement("Insert into topo_const values (?,?)");
					ps2.setString(1,endingNode);
					ps2.setString(2,startingNode);
					int p2=ps2.executeUpdate();
					if((p1==1)&(p2==1))
					{
						JOptionPane.showMessageDialog(null,"Path "+startingNode+" => "+endingNode+" created successfully !!!");
						DeployNode.topo_const.add(startingNode+"#"+endingNode);
					}
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}




















 

//============================= Testing ================================//
//=                                                                    =//
//= The following main method is just for testing this class you built.=//
//= After testing,you may simply delete it.                            =//
//======================================================================//
	public static void main(String[] args)
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception ex)
		{
			System.out.println("Failed loading L&F: ");
			System.out.println(ex);
		}
		new TopologyCreation();
	}
//= End of Testing =


}
