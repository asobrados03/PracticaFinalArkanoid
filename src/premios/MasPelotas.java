package premios;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;

public class MasPelotas extends Premio{

public static final int BW = 15;
	
	public static final int BH = 15;
	
	public MasPelotas(int posx, int posy){
		super(posx,posy,BW,BH);
		setFondo(new ImageIcon(this.getClass().getResource("/imagenes/bola_trans.png")).getImage());
	}
	
	public void pinta(Graphics gr){
		gr.setColor(Color.GREEN);
		gr.fillOval((int)getPosX(),(int)getPosY(),BW-1,BH-1);
		super.pinta(gr);
	}
}
