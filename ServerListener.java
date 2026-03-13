import javax.swing.*;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
class ServerListener extends Thread
{
	
	ServerSocket ss;
	int port_id=0;
	Vector avail_nodes;
	String final_selected_path="";
	String curr_sender="";
	String curr_dest="";
	
	ServerListener(int port)
	{
		super();
		port_id=port;
		start();
	}
	
	public void run()
	{
		try
		{
			ss=new ServerSocket(port_id);
			while(true)
			{
				Socket s=ss.accept();
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				String req=(String)ois.readObject();
				System.out.println("REQ "+req);
				
				if(req.equals("LOGIN"))
				{
					boolean avail_node=false;
					int port=2000;
					int uport=0;
					String uip="";
					boolean update_status=false;
					String node_name=(String)ois.readObject();
					String sys_info=(String)ois.readObject();
					
					avail_nodes=new Vector();
					
					Connection con=DBConnection.getConnection();
					PreparedStatement p=con.prepareStatement("Select DISTINCT Source_Node from topo_const");
					ResultSet rs0=p.executeQuery();
					while(rs0.next()){
						
						avail_nodes.add(rs0.getString(1).trim());
					}
					System.out.println("DATA "+avail_nodes);
					if(avail_nodes.contains(node_name.trim()))
					{
					
						PreparedStatement ps=con.prepareStatement("Select * from node_info");
						ResultSet rs = ps.executeQuery();
						
						String status="";
						while(rs.next())
						{
							String name=rs.getString(1).trim();
							String ipu=rs.getString(2).trim();
							port=rs.getInt(3);
							if(name.equals(node_name))
							{
								avail_node=true;
								uport=port;
								uip=ipu;
								status=rs.getString(4);
							}
						}						
							
						if(avail_node==true)
						{
							if(status.equals("ON"))
							{
								oos.writeObject("FAILED#USER ALREADY EXIST IN NETWORK");
							}
							else
							{
								
								String info[]=sys_info.split("/");	
								if(info[1].equals(uip))
								{
									PreparedStatement ps1=con.prepareStatement("Update node_info set Node_Status=? where Node_Name=?");
									ps1.setString(1,"ON");
									ps1.setString(2,node_name);
									ps1.executeUpdate();
									update_status=true;
									oos.writeObject("SUCCESS#"+node_name+"#"+uport);
								}
								else
								{
									oos.writeObject("FAILED#UNAUTHORIZED ACCESS");
								}
								
							}
						}
						else
						{
							String info[]=sys_info.split("/");
							port+=1;
							PreparedStatement ps2=con.prepareStatement("Insert into node_info values(?,?,?,?)");
							ps2.setString(1,node_name);
							ps2.setString(2,info[1]);
							ps2.setInt(3,port);
							ps2.setString(4,"ON");
							ps2.executeUpdate();
							update_status=true;
							oos.writeObject("SUCCESS#"+node_name+"#"+port);
						}
						
						
						if(update_status==true)
						{
							ProcessData.updateNodeDetail();
						}
					}
					else
					{
						oos.writeObject("FAILED# "+node_name+" DOES NOT EXIST IN TOPOLOGY");	
					}
					
				}
				else if(req.equals("STATUS"))
				{
					String Usr_Name=(String)ois.readObject();
					String Usr_Status=(String)ois.readObject();
					
					if(Usr_Status.equals("ON"))
					{
						if(DeployNode.node_OFF.contains(Usr_Name))
						{
							DeployNode.node_OFF.remove(Usr_Name);
						}
					}
					else if(Usr_Status.equals("OFF"))
					{
						if(!DeployNode.node_OFF.contains(Usr_Name))
						{
							DeployNode.node_OFF.add(Usr_Name);
						}
					}
					
					Connection con=DBConnection.getConnection();
					PreparedStatement ps=con.prepareStatement("Update node_info set Node_Status=? where Node_Name=?");
					ps.setString(1,Usr_Status);
					ps.setString(2,Usr_Name);
					int i=ps.executeUpdate();
					if(i==1)
					{
						oos.writeObject("STATUS UPDATED ");
						ProcessData.updateNodeDetail();
					}
				}
				else if(req.equals("RREQ"))
				{
					String send_dest=(String)ois.readObject();
					String req_node=(String)ois.readObject();
					boolean long_path=(Boolean)ois.readObject();
					String snd_dst[]=send_dest.split("#");
					
					boolean flg1 = false;
					String Node_Ip_Port="";
					
					if(TopologyCreation.jcb.isSelected())
					{
						System.out.println("IN CHECK");
						if(curr_sender.equals("")&curr_dest.equals(""))
						{
							curr_sender=snd_dst[0].trim();
							curr_dest=snd_dst[1].trim();
							flg1=true;
						}
						else
						{
							if(curr_sender.equals(snd_dst[0].trim())&curr_dest.equals(snd_dst[1].trim()))
							{
								/*if(req_node.equals(curr_sender))
								{
									flg1=true;
								}
								else
								{*/
									Node_Ip_Port = getNxtPeer(req_node.trim());
								//}
							}
							else
							{
								flg1=false;
								Node_Ip_Port="DROP PACKET";
								curr_sender="";
								curr_dest="";
							}
						}
					}
					else
					{
						System.out.println("IN CREATE");
						curr_sender=snd_dst[0].trim();
						curr_dest=snd_dst[1].trim();
						flg1 = true;
					}
					
					String cur_path="";
					
					Vector temp_path=new Vector();
					Vector final_path=new Vector();
					
					temp_path.add(snd_dst[1]+"<=");
					  
					if(flg1)
					{
						do
						{
							cur_path=temp_path.elementAt(0).toString();
							String split_cur_path[]=cur_path.split("<=");
							int len=split_cur_path.length;
							if(!split_cur_path[len-1].equals(snd_dst[0]))
							{
								String nxt_nds=findNXTNode(split_cur_path[len-1]);
								String[] split_nxt_nds=nxt_nds.split("#");
								temp_path.remove(0);
								
								for(int i=0;i<split_nxt_nds.length;i++)
								{
									if(cur_path.indexOf(split_nxt_nds[i])==-1)
									{
										if(split_nxt_nds[i].equals(snd_dst[0]))
										{
											final_path.add(cur_path+split_nxt_nds[i]);
										}
										else
										{
											temp_path.add(cur_path+split_nxt_nds[i]+"<=");
										}
										
									}
									
								}
								
							}
							
							
								
						}while(!temp_path.isEmpty());
						
						String un_avail_node=findUnAvailNode().trim();
						String[] split_un_avail_node=un_avail_node.split("#");
						
						if((split_un_avail_node.length>0)&(un_avail_node.indexOf("#")!=-1))
						{
							for(int i=0;i<split_un_avail_node.length;i++)
							{
								for(int j=0;j<final_path.size();j++)
								{
									if(final_path.get(j).toString().indexOf(split_un_avail_node[i].trim())!=-1)
									{
										final_path.remove(j);
										j=0;	
									}
								}
							}
							for(int ii=0;ii<final_path.size();ii++)
							{
								System.out.println(ii+" - "+final_path.elementAt(ii));
							}
							if(!long_path)
							{
								findBestPath(final_path);
							}
							else
							{
								findLongestPath(final_path);
							}
							
						}
						else
						{
							for(int ii=0;ii<final_path.size();ii++)
							{
								System.out.println(ii+" - "+final_path.elementAt(ii));
							}
							if(!long_path)
							{
								findBestPath(final_path);
							}
							else
							{
								findLongestPath(final_path);
							}
						}
						
						if(!final_selected_path.equals("NO ROUTE AVAILABLE!!!"))
						{
							Node_Ip_Port = getNxtPeer(req_node.trim());
							oos.writeObject(true);
						}
						else
						{
							Node_Ip_Port = "NO ROUTE AVAILABLE!!!";
							oos.writeObject(false);
						}
					}
					
					if(Node_Ip_Port.equals("DROP PACKET"))
					{
						oos.writeObject(false);
					}
					oos.writeObject(Node_Ip_Port);
					
				}	
				else if (req.equals("FRWDB"))
				{
					String req_node=(String)ois.readObject();
					
					Connection con=DBConnection.getConnection();
					PreparedStatement ps=con.prepareStatement("Select * from node_info where Node_Name=?");
					ps.setString(1,req_node);
					ResultSet rs=ps.executeQuery();
					if(rs.next())
					{
						String NAME=rs.getString(1);
						String IP=rs.getString(2);
						String PORT=rs.getString(3);
				
						String IP_PORT = NAME+"#"+IP+"#"+PORT;
					
					
					System.out.println(IP_PORT);
						oos.writeObject(IP_PORT);
					}		
					
				}			
				else if(req.equals("FRWD"))
				{
					
					String send_dest=(String)ois.readObject();
					String req_node=(String)ois.readObject();
					String snd_dst[]=send_dest.split("#");
					String Node_Ip_Port="";
					boolean flg1=false;
					boolean flg2=false;

					if(curr_sender.equals(snd_dst[0].trim())&curr_dest.equals(snd_dst[1].trim()))
					{
						Node_Ip_Port = getNxtPeer(req_node.trim());
						oos.writeObject(Node_Ip_Port);
					}
					/*if(flg2)
					{
						Node_Ip_Port="DROP PACKET";
						curr_sender="";
						curr_dest="";
						
					}*/
					
					
				}
				
			}
		}
		catch(BindException be)
		{
			System.exit(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/*---------------------------------------------------------------------------------------*/
	
	
	public String findNXTNode(String node)
	{
		String intr_node="";
		try
		{
			Connection con=DBConnection.getConnection();
			PreparedStatement ps=con.prepareStatement("Select * from topo_const where Dest_Node=?");
			ps.setString(1,node);
			ResultSet rs=ps.executeQuery();
			
			while(rs.next())
			{
				intr_node+=rs.getString(1).trim()+"#";
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	return intr_node;
	}
	
	
	/*---------------------------------------------------------------------------------------*/
	
	
	public String findUnAvailNode()
	{
		String un_avail_node="";
		try
		{
			Connection con=DBConnection.getConnection();
			PreparedStatement ps=con.prepareStatement("Select * from node_info where Node_Status=?");
			ps.setString(1,"OFF");
			ResultSet rs=ps.executeQuery();
			
			while(rs.next())
			{
				un_avail_node+=rs.getString(1).trim()+"#";
			}
			System.out.println("UAN "+un_avail_node);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	return un_avail_node;
	}
		
	/*---------------------------------------------------------------------------------------*/
	
	
	public void findLongestPath(Vector vec)
	{
		final_selected_path="";
		int len=0;
		int index=-1;
		try
		{
			System.out.println("VEC SIZ "+vec.size());
			if(!vec.isEmpty())
			{
				for(int i=0;i<vec.size();i++)
				{
					
					String path[]=vec.elementAt(i).toString().split("<="); 
					int nu_len = path.length;
					if(nu_len>len)
					{
						len=nu_len;
						index=i;
					}
				}	
				
				final_selected_path=vec.elementAt(index).toString();
			}
			else
			{
				final_selected_path="NO ROUTE AVAILABLE!!!";
				curr_sender="";
				curr_dest="";
			}
			
			System.out.println(" FINAL PATH "+final_selected_path);		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*---------------------------------------------------------------------------------------*/
	
	
	public void findBestPath(Vector vec)
	{
		final_selected_path="";
		int len=15;
		int index=-1;
		try
		{
			System.out.println("VEC SIZ "+vec.size());
			if(!vec.isEmpty())
			{
				for(int i=0;i<vec.size();i++)
				{
					
					String path[]=vec.elementAt(i).toString().split("<="); 
					int nu_len = path.length;
					if(nu_len<len)
					{
						len=nu_len;
						index=i;
					}
				}	
				
				final_selected_path=vec.elementAt(index).toString();
			}
			else
			{
				final_selected_path="NO ROUTE AVAILABLE!!!";
				curr_sender="";
				curr_dest="";
			}
			
			System.out.println(" FINAL PATH "+final_selected_path);		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/*---------------------------------------------------------------------------------------*/
	
	
	public String getNxtPeer(String cur_req_node)
	{
		String IP_PORT="";
		
		String split_final_selected_path[]=final_selected_path.split("<=");
		int indx=-1;
		for(int i=0;i<split_final_selected_path.length;i++)
		{
			if(cur_req_node.equals(split_final_selected_path[i]))
			{
				indx=i;
			}
		}
		try
		{
			System.out.println("INDX "+indx);
			Connection con=DBConnection.getConnection();
			PreparedStatement ps=con.prepareStatement("Select * from node_info where Node_Name=?");
			ps.setString(1,split_final_selected_path[indx-1]);
			ResultSet rs=ps.executeQuery();
			if(rs.next())
			{
				String NAME=rs.getString(1);
				String IP=rs.getString(2);
				String PORT=rs.getString(3);
				
				IP_PORT = NAME+"#"+IP+"#"+PORT;
				
				if(cur_req_node.equals(split_final_selected_path[1]))
				{
					curr_sender="";
					curr_dest="";
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("INDX "+IP_PORT);
	return IP_PORT;
	}
}
