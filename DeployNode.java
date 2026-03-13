import java.awt.*;
import java.awt.Shape;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

public class DeployNode extends JPanel implements ActionListener, MouseListener
{
    static Vector xx=new Vector();
    static Vector yy=new Vector();
    static Vector node_OFF=new Vector();
    static Vector topo_const=new Vector();
    int ch=65;
    
    public DeployNode() {
    	
    	this.setBackground(Color.white);
        this.setPreferredSize(new Dimension(600,500));
        this.setVisible(true);
        addMouseListener(this);
    }

    public void drawCircle(Vector vx,Vector vy,Vector nd_st) 
    {
    	
     	Graphics g = this.getGraphics();
        Graphics2D g2d = (Graphics2D)g;
		super.paint(g2d);
		
		TopologyCreation.addNode(vx,vy);
		//Enables antialiasing to the graphics object
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		boolean flg=false;
	    int nd_int[]=new int[nd_st.size()];;
	    if(nd_st.size()>0)
	    {
	    	for(int h=0;h<nd_st.size();h++)
	    	{
	    		String node=(String)nd_st.elementAt(h);
	    		nd_int[h]=(int)node.charAt(0);
	    		nd_int[h]-=65;
	    		flg=true;
	    	}
	    }
	    
	    for(int i=0;i<vx.size();i++)
	    {
	    	
		    int x=(Integer)vx.elementAt(i);
		    int y=(Integer)vy.elementAt(i);
		    
		    g2d.setColor(new Color(0,255, 0));
		    g2d.fillOval(x,y, 25, 25);
		    
		    if(flg)
		    {
		    	for(int j=0;j<nd_int.length;j++)
		    	{
		    		
		    		if(nd_int[j]==i)
		    		{
		    			g2d.setColor(new Color(255, 0, 0));
		    			g2d.fillOval(x,y, 25, 25);
		    		}
		    	}
		    }
		    
		    g2d.setColor(new Color(0, 0, 255));
		    g2d.drawString(String.valueOf((char)ch), x, y);
		   
	    	ch++;
        }
        if(topo_const.size()!=0)
		{
			int ch=65;
			for(int j=0;j<topo_const.size();j++)
			{
				String data=(String)topo_const.elementAt(j);
				String[] split=data.split("#");
			
				int source=(int)split[0].charAt(0);
				int dest=(int)split[1].charAt(0);
				
				source-=ch;
				dest-=ch;
				
				int sx=(Integer)xx.elementAt(source);
				int sy=(Integer)yy.elementAt(source);
				
				int dx=(Integer)xx.elementAt(dest);
				int dy=(Integer)yy.elementAt(dest);
				
				//int mx=(sx+dx)/2;
				//int my=(sy+dy)/2;
				
				//int red = (int) (Math.random()*256);
			    //int green = (int)(Math.random()*256);
			    //int blue = (int)(Math.random()*256);
					
				g2d.setColor(new Color(0,0,0));
				//g2d.drawString(split[2], mx, my);
				g2d.drawLine(sx,sy,dx,dy);
			}
		}
    }
    
   
    
    public void actionPerformed(ActionEvent ae) {

    }
    
    
    public void mouseClicked(MouseEvent e) 
    {
    		xx.add(e.getX());
    		yy.add(e.getY());
    		ch=65;
        	drawCircle(xx, yy,node_OFF);
    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {
    	ch=65;
		drawCircle(xx, yy,node_OFF);
		
    }
    
	
	
    public static void main(String[] args) {
        DeployNode test = new DeployNode(); 
    }
}