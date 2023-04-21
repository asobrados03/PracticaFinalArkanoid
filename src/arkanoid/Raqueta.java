package arkanoid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Raqueta {

	//--------------- Valores constantes ---------------
	
	/** La anchura de la raqueta */
	public static int RACKET_W=50;
	
	/** La altura de la raqueta */
	public static int RACKET_H=15;
	
	//--------------- Atributos ---------------
	
	/** Coordenada X de la raqueta */
	private int racketX;

	/** Coordenada Y de la raqueta */
	private int racketY;
	
	private Image fondo_der = null;
	private Image fondo_cen = null;
	private Image fondo_izq = null;
	
	public Raqueta(int coordX, int coordY){
		this.racketX = coordX;
		this.racketY = coordY;
		this.fondo_der = new ImageIcon(this.getClass().getResource("/imagenes/raq_r.png")).getImage();
		this.fondo_cen = new ImageIcon(this.getClass().getResource("/imagenes/raq_c.png")).getImage();
		this.fondo_izq = new ImageIcon(this.getClass().getResource("/imagenes/raq_l.png")).getImage();
	}
	
	public int getCoordX(){
		return this.racketX;
	}
	
	public int getCoordY(){
		return this.racketY;
	}
	
	public void setCoordX(int coordX){
		this.racketX = coordX;
	}
	
	public void setCoordY(int coordY){
		this.racketY = coordY;
	}
	
	public void ampliar(){
		RACKET_W += 10;
		if(RACKET_W > 150){
			RACKET_W = 150;
		}
	}
	
	public void reduir(){
		RACKET_W -= 10;
		if(RACKET_W < 12){
			RACKET_W = 12;
		}
	}
	
	public void paint(Graphics gr){
		gr.setColor(Color.blue);
		int tamanyLaterals = Raqueta.RACKET_W - fondo_izq.getWidth(null) - fondo_der.getWidth(null);
		gr.drawImage(fondo_izq, this.getCoordX(), this.getCoordY(), null);
		for(int x = 0; x < tamanyLaterals; x++){
			gr.drawImage(fondo_cen, this.getCoordX()+fondo_izq.getWidth(null)+x, this.getCoordY(), null);
		}
		gr.drawImage(fondo_der, this.getCoordX()+Raqueta.RACKET_W - fondo_der.getWidth(null), this.getCoordY(), null);
	}
}
