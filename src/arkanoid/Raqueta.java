package arkanoid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * Clase encargada de la raqueta
 * @author Grupo5DIU
 */
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
	
	/** Imagenes de la raqueta */
        private Image fondo_der = null;
	private Image fondo_cen = null;
	private Image fondo_izq = null;
	
        /**
         * Posicionamiento y asignacion de imagenes de la raqueta
         * @param coordX
         * @param coordY
         */
        public Raqueta(int coordX, int coordY){
		this.racketX = coordX;
		this.racketY = coordY;
		this.fondo_der = new ImageIcon(this.getClass().getResource("/imagenes/raq_r.png")).getImage();
		this.fondo_cen = new ImageIcon(this.getClass().getResource("/imagenes/raq_c.png")).getImage();
		this.fondo_izq = new ImageIcon(this.getClass().getResource("/imagenes/raq_l.png")).getImage();
	}
	
        /**
         * Metodo de devolucion del posicionamiento en la coordenada x
         * @return
         */
        public int getCoordX(){
		return this.racketX;
	}
	
        /**
         * Metodo de devolucion del posicionamiento en la coordenada y
         * @return
         */
        public int getCoordY(){
		return this.racketY;
	}
	
        /**
         * Metodo asignador del posicionamiento en la coordenada x
         * @param coordX
         */
        public void setCoordX(int coordX){
		this.racketX = coordX;
	}
	
        /**
         * Metodo asignador del posicionamiento en la coordenada y
         * @param coordY
         */
        public void setCoordY(int coordY){
		this.racketY = coordY;
	}
	
        /**
         * Metodo encargado del efecto de ampliado
         */
        public void ampliar(){
		RACKET_W += 10;
		if(RACKET_W > 150){
			RACKET_W = 150;
		}
	}
	
        /**
         * Metodo encargado del efecto de reduccion
         */
        public void reduir(){
		RACKET_W -= 10;
		if(RACKET_W < 12){
			RACKET_W = 12;
		}
	}
	
        /**
         * Metodo encargado del pintado de la raqueta
         * @param gr
         */
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
