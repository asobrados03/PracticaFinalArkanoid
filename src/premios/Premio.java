package premios;

import java.awt.Graphics;
import java.awt.Image;

import arkanoid.Raqueta;

/**
 * Clase encargada de la definicion de las clases de los premios
 * @author Grupo5DIU
 */
public abstract class Premio {
	
        //--------------- Valores constantes ---------------
	
	/** Movimiento sobre la coordenada y del premio */
        private static final double MOVY = 2.0;
	
	//--------------- Atributos ---------------
        
        /** Anchura del premio */
        private int width;
	
	/** Anchura de la altura */
        private int height;
	
	/** Posicionamiento en la coordenada x */
        private int posX;
	
	/** Posicionamiento en la coordenada y */
        private int posY;
	
	/** Imagen del premio */
        private Image fondo;
	
        /**
         * Posicionamiento del efecto
         * @param posx
         * @param posy
         * @param wd
         * @param hg
         */
        public Premio(int posx, int posy, int wd, int hg){
		this.posX = posx;
		this.posY = posy;
		this.width = wd;
		this.height = hg;
	}

        /**
         * Metodo encargado del pintado del efecto
         * @param gr
         */
        public void pinta(Graphics gr){
		gr.drawImage(fondo, getPosX(), getPosY(), null);
	}
	
        /**
         * Metodo asignador de imagenes del efecto
         * @param image
         */
        public void setFondo(Image image){
		this.fondo = image;
	}
	
        /**
         * Metodo de devolucion del ancho
         * @return
         */
        public int getWidth(){
		return width;
	}

        /**
         * Metodo asignador del ancho
         * @param width
         */
        public void setWidth(int width){
		this.width = width;
	}

        /**
         * Metodo de devolucion del alto
         * @return
         */
        public int getHeight(){
		return height;
	}

        /**
         * Metodo asignador del alto
         * @param height
         */
        public void setHeight(int height){
		this.height = height;
	}

        /**
         * Metodo de devolucion del posicionamiento en la coordenada x
         * @return
         */
        public int getPosX(){
		return posX;
	}

        /**
         * Metodo asignador del posicionamiento en la coordenada x
         * @param posX
         */
        public void setPosX(int posX){
		this.posX = posX;
	}

        /**
         * Metodo de devolucion del posicionamiento en la coordenada y
         * @return
         */
        public int getPosY(){
		return posY;
	}

        /**
         * Metodo asignador del posicionamiento en la coordenada y
         * @param posY
         */
        public void setPosY(int posY){
		this.posY = posY;
	}
	
        /**
         * Metodo encargado del movimiento del premio sobre la coordenada y
         */
        public void move(){
		setPosY((int)(getPosY() + MOVY));
	}
	
        /**
         * Metodo encargado de la recepcion del premio por la raqueta
         * @param raqueta
         * @return
         */
        public boolean recivido(Raqueta raqueta){
		if(this.getPosX() >= raqueta.getCoordX() - this.getWidth() && this.getPosX() + this.getWidth() <= raqueta.getCoordX() + Raqueta.RACKET_W){
			if((this.getPosY() + this.getHeight() >= raqueta.getCoordY())&&(this.getPosY() <= raqueta.getCoordY()+Raqueta.RACKET_H)){
				return true;
			}
		}
		return false;
	}
}
