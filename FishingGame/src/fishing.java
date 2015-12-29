import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class fishing {
	
	static BufferedImage PoolImage;
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame("捕鱼达人");
		Pool pool = new Pool(); //在Java中，类中的静态方法不能直接调用动态方法。只有将某个内部类修饰为静态类，
			//然后才能够在静态类中调用该类的成员变量与成员方法。所以解决办法是将public class改为public static class.
		
		//frame.setSize(PoolImage.getWidth(), PoolImage.getHeight());
		frame.setSize(800, 480);
		frame.setLocationRelativeTo(null);  //设置窗口居中，必须在size后面
		frame.add(pool);
		frame.setVisible(true);
		frame.setResizable(false);  //不允许用户改变窗口大小
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //关闭窗口时结束程序
		pool.action();
		
	}
	
	public static class Pool extends JPanel {
		Fish[] fishes = new Fish[14];
		Net net;
		int score = 100;
		int fontsize = 20;
		String fonttype = "楷体";
		Font font = new Font(fonttype, Font.BOLD, fontsize);
		
		public Pool() throws IOException {
			PoolImage = ImageIO.read(getClass().getResourceAsStream("/images/bg.jpg"));
			for(int i = 1;i <= 14; i++){
				String fishname;
				if(i <= 9) {
					fishname = "fish0"+String.valueOf(i);
				}
				else {
					fishname = "fish"+String.valueOf(i);
				}
				Fish fish = new Fish(fishname);
				fishes[i-1] = fish;
				fish.start();
			}
			net = new Net();
		}
		
		public void paint(Graphics g) {  //在类Container及其子类（如：Frame，Panel）的对象需要重绘时，JVM会自动调用它的public void paint(Graphics g)方法
			
			g.drawImage(PoolImage, 0, 0, null);
			for(int i = 0;i < fishes.length; i++) {
				Fish temp_fish = fishes[i];
				//System.out.println(i);
				g.drawImage(temp_fish.cur_FishImage, temp_fish.x, temp_fish.y, null);
			}
			
			if(net.show) {
				g.drawImage(net.netImage, net.x-net.width/2, net.y-net.height/2, null);
			}
			
			g.setFont(font);
			g.setColor(Color.white);
			g.drawString("Score: ", 20, 30);
			g.setColor(Color.white);
			g.drawString(score+"", 90, 30);
		}
		
		public void action() {
			MouseAdapter adapater = new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					net.show = true;
				}
				
				public void mouseExited(MouseEvent e) {
					net.show = false;
				}
				
				public void mouseMoved(MouseEvent e) {
					int x = e.getX();
					int y = e.getY();
					
					net.x = x;
					net.y = y;
				}
				
				public void mousePressed(MouseEvent e) {
					int x = e.getX();
					int y = e.getY();
					net.x = x;
					net.y = y;
					
					score -= 10;
					
					catch_fish();
				}
			};
			
			this.addMouseListener(adapater);
			this.addMouseMotionListener(adapater);
			net.show = true;
			
			while(true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		public void catch_fish() {
			for(int i = 0;i < fishes.length; i++) {
				Fish fish = fishes[i];
				if(fish.is_in_net(net.x, net.y)) {
					fish.fish_catched();
					score += fish.width/40*fish.speed;
				}
				System.out.println(fish.is_in_net(net.x, net.y));
			}
		}
		
	}
	
	public static class Net {
		int width,height;
		int x,y;
		boolean show;
		BufferedImage netImage;
		public Net() throws IOException {
			netImage = ImageIO.read(getClass().getResourceAsStream("/images/net.png"));
			width = netImage.getWidth();
			height = netImage.getHeight();
			show = false;
		}
	}
	
	public static class Fish extends Thread {
		int width,height,speed;
		int x,y;
		boolean is_catched = false;
		
		BufferedImage[] FishImages = new BufferedImage[15];
		BufferedImage cur_FishImage;
		int current = 0;
		Random rand = new Random();
		
		public Fish(String FishName) throws IOException {
			for(int i = 1;i <= 12; i++) {
				String state;
				if(i > 10) {
					state = "_catch_0"+String.valueOf(i-10);
				}
				else if(i > 9) state = "_"+String.valueOf(i);
				else state = "_0"+String.valueOf(i);
				//String str = "/images/" + FishName + state + ".png";
				//System.out.println(str);
				BufferedImage FishImage = ImageIO.read(getClass().getResourceAsStream(
						"/images/" + FishName + state + ".png"));
				FishImages[i-1] = FishImage;
			}
			
			cur_FishImage = FishImages[current];
			
			rand = new Random();
			x = PoolImage.getWidth();
			y = rand.nextInt(PoolImage.getHeight());
			rand = new Random();
			speed = rand.nextInt(5);		
			is_catched = false;
		}
		public void run() {
			while(true) {
				try{
					sleep(100);
					if(is_catched) {
						current = (current+1);
						if(current == 12) {
							go_away();
						}
						else {
							cur_FishImage = FishImages[current];
						}
					}
					else {
						current = (current+1)%10;
						cur_FishImage = FishImages[current];
						x -= speed;
						if(x <= 0) {
							go_away();
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		public void go_away() {
			// TODO Auto-generated method stub
			current = 0;
			cur_FishImage = FishImages[current];
			rand = new Random();
			x = PoolImage.getWidth();
			y = rand.nextInt(PoolImage.getHeight()-height);
			rand = new Random();
			speed = 3+rand.nextInt(20);
			is_catched = false;
		}
		
		public void fish_catched() {
			is_catched = true;
			current = 9;
		}
		
		public boolean is_in_net(int net_x,int net_y) {
			int dx = net_x-x;
			int dy = net_y-y;
			
			width = cur_FishImage.getWidth();
			height = cur_FishImage.getHeight();
			//System.out.println(net_x+" "+net_y+" "+x+ " "+y);
			//System.out.println(dx+" "+dy+" "+width+" "+height);
			return (dx>=0 && dx<=width && dy>=0 && dy<=height);
		}
	}

}
