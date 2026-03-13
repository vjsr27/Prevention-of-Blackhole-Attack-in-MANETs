import java.sql.*;
import java.net.*;
import java.util.Vector;
import java.io.*;
class ProcessData
{
	ProcessData()
	{
		
	}
	
	public static void updateNodeDetail()
	{
		try
		{
			Thread.sleep(700);
			Vector ND_NM=new Vector();
			String IPS="";
			String PORT="";
			String DB_NM="",DB_IP="",DB_PRT="",DB_STATUS="";
			Connection con=DBConnection.getConnection();	
			PreparedStatement ps=con.prepareStatement("Select * from node_info");
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				DB_NM=rs.getString(1).trim();
				DB_IP=rs.getString(2).trim();
				DB_PRT=rs.getString(3);
				DB_STATUS=rs.getString(4).trim();
				System.out.println("Status "+DB_STATUS);
				if(DB_STATUS.equals("ON"))
				{
					ND_NM.add(DB_NM);
					IPS+=DB_IP+"#";
					PORT+=DB_PRT+"#";	
				}
				
				String ND_IP[]=IPS.split("#");
				String ND_PRT[]=PORT.split("#");
				//here
				if(ND_IP.length>1)
				{
					for(int i=0;i<ND_IP.length;i++)
					{
						Socket s=new Socket(ND_IP[i],Integer.parseInt(ND_PRT[i]));
						ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
						ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
						oos.writeObject("UPDATE_NODES");
						oos.writeObject(ND_NM);
						s.close();
					}	
					System.out.println("DATAE "+ND_NM);
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
	}
}