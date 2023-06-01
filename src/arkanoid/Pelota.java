package arkanoid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.swing.ImageIcon;

import javazoom.jl.decoder.JavaLayerException;

import reproductor.jlap;

/**
 * Clase encargada de las pelotas
 * @author Grupo5DIU
 */
public class Pelota {
	
	//--------------- Valores constantes ---------------
	
	/** La anchura de la pelota */
	public static final int BW=15;

	/** La altura de la pelota */
	public static final int BH=15;
	
	//--------------- Atributos ---------------
	
	/** Coordenada X de la pelota */
	private double bx;
	
	/** Coordenada Y de la pelota */
	private double by;
	
	/** Incremento en X de la pelota, tras cada cambio de posici�n */
	private double bdx;
	
	/** Incremento en Y de la pelota, tras cada cambio de posici�n */
	private double bdy;
	
	/** Estado de la pelota */
        private boolean baixant = false;
	
	/** Imagen de la pelota */
        private Image fondo = null;
	
	/** Audio del rebote de la pelota */
        private jlap mediaRebote = null;
	
	/** Velocidad de la pelota */
        private double defaultBdx = 2;
	
        /**
         * Posicionamiento y asignacion de imagenes de las pelotas
         * @param posX
         * @param posY
         */
        public Pelota(double posX, double posY){
		this.bx = posX;
		this.by = posY;
		this.bdx = 0;
		this.bdy = 0;
		this.fondo = new ImageIcon(this.getClass().getResource("/imagenes/bola_trans.png")).getImage();
		try {
			mediaRebote = new jlap("\\UDP\\Arkanoid\\sonidos\\click.mp3");
		} catch (JavaLayerException | IOException e) {
		}
	}
	
        /**
         * Metodo de devolucion del posicionamiento en la coordenada x
         * @return
         */
        public double getCoordX(){
		return this.bx;
	}
	
        /**
         * Metodo asignador del posicionamiento en la coordenada x
         * @param posX
         */
        public void setCoordX(double posX){
		this.bx = posX;
	}
	
        /**
         * Metodo de devolucion del posicionamiento en la coordenada y
         * @return
         */
        public double getCoordY(){
		return this.by;
	}
	
        /**
         * Metodo de devolucion del movimiento en la coordenada x
         * @return
         */
        public double getMovX(){
		return this.bdx;
	}
	
        /**
         * Metodo de devolucion del movimiento en la coordenada y
         * @return
         */
        public double getMovY(){
		return this.bdy;
	}
	
        /**
         * Metodo asignador del movimiento en la coordenada x
         * @param movX
         */
        public void setMovX(double movX){
		this.bdx = movX;
	}
	
        /**
         * Metodo asignador del movimiento en la coordenada y
         * @param movY
         */
        public void setMovY(double movY){
		this.bdy = movY;
	}
	
        /**
         * Metodo de devolucion del estado de la pelota
         * @return
         */
        public boolean isDown(){
		return this.baixant;
	}
	
        /**
         * Metodo asignador del estado de la pelota
         * @param down
         */
        public void setDown(boolean down){
		this.baixant = down;
	}
	
        /**
         * Metodo encargado del efecto de aceleracion
         */
        public void acelera(){
		this.setMovX(this.getMovX()*1.5);
		this.setMovY(this.getMovY()*1.5);
		this.defaultBdx = defaultBdx * 1.5;
	}
	
        /**
         * Metodo encargado del efecto de frenado
         */
        public void frena(){
		if(this.getMovY() > 1){
			this.setMovX(this.getMovX()/1.5);
			this.setMovY(this.getMovY()/1.5);
			this.defaultBdx = defaultBdx / 1.5;
		}
	}
	
        /**
         * Metodo encargado del movimiento de la pelota
         * @param width
         * @param height
         * @throws JavaLayerException
         * @throws IOException
         */
        public void move(int width, int height) throws JavaLayerException, IOException{
		this.bx += this.bdx;
		this.by += this.bdy;
		if ((this.bx+BW>width && this.bdx > 0)||(this.bx<0 && this.bdx < 0)){
			this.bdx=-this.bdx;
			mediaRebote.play();
		}
		if (this.by+BH>=height || this.by<=30){
			this.bdy=-this.bdy;
			mediaRebote.play();
		}
		if (this.by<=30){
			this.setDown(true);
			mediaRebote.play();
		}
	}
	
        /**
         * Metodo encargado del pintado de las pelotas
         * @param gr
         */
        public void paint(Graphics gr){
		gr.setColor(Color.red);
		gr.fillOval((int)this.getCoordX(),(int)this.getCoordY(),BW-1,BH-1);
		gr.drawImage(fondo,(int)this.getCoordX(),(int)this.getCoordY(),null);
	}
	
        /**
         * Metodo encargado del rebote de las pelotas
         * @param posX
         * @param posY
         * @param width
         * @param height
         * @param ladrillo
         * @return
         * @throws JavaLayerException
         * @throws IOException
         */
        public boolean rebota(int posX, int posY , int width, int height, Ladrillo ladrillo) throws JavaLayerException, IOException{
		if(ladrillo == null){
			if((this.getCoordX() + Pelota.BW/2 >= posX + 2*width/5)&&(this.getCoordX() + Pelota.BW/2 <= posX + 3*width/5)){
				if(this.getMovX() > 0){
					this.setMovX(defaultBdx/4);
				}else{
					this.setMovX(-defaultBdx/4);
				}
			}else if((this.getCoordX() + Pelota.BW/2 >= posX + width/5)&&(this.getCoordX() + Pelota.BW/2 < posX + 2*width/5)){
				this.setMovX(-defaultBdx);
			}else if((this.getCoordX() + Pelota.BW/2 > posX + 3*width/5)&&(this.getCoordX() + Pelota.BW/2 <= posX + 4*width/5)){
				this.setMovX(defaultBdx);
			}else if((this.getCoordX() + Pelota.BW/2 >= posX)&&(this.getCoordX() + Pelota.BW/2 < posX + width/5)){
				this.setMovX(-defaultBdx*1.5);
			}else if((this.getCoordX() + Pelota.BW/2 > posX + 4*width/5)&&(this.getCoordX() + Pelota.BW/2 <= posX + width)){
				this.setMovX(defaultBdx*1.5);
			}else if(this.getCoordX() + Pelota.BW/2 < posX){
				this.setMovX(-defaultBdx*2);
			}else{
				this.setMovX(defaultBdx*2);
			}
			this.setMovY(-this.getMovY());
			mediaRebote.play();
			return true;
		}else{
			
                        boolean rebote = false;
			if((this.getCoordX()+Pelota.BW > posX-3)&&(this.getCoordX()+Pelota.BW <= posX+3)&&(this.getMovX()>0)){
				rebote = true;
			}else if((this.getCoordX() >= posX+Ladrillo.BlqWidth-3)&&(this.getCoordX() < posX+Ladrillo.BlqWidth+3)&&(this.getMovX()<0)){
				rebote = true;
			}
			if((this.getCoordY() >= posY-3*Pelota.BH/4)&&(this.getCoordY() <= posY+Ladrillo.BlqHeight-Pelota.BH/4)){
				if(rebote){
					this.setMovX(-this.getMovX());
					if(ladrillo.getLifes() > 0){
						ladrillo.setLifes(ladrillo.getLifes()-1);
					}
					if(ladrillo.getLifes() == 0){
						Arkanoid.puntuacion += ladrillo.getPoints();
					}
					return true;
				}
			}else if(((this.getCoordX()+Pelota.BW) >= posX)&&(this.getCoordX() <= posX+Ladrillo.BlqWidth)){
				if(this.isDown()){
					if (((this.getCoordY()+Pelota.BH) >= posY)&&(this.getCoordY() < (posY+Ladrillo.BlqHeight))){
						this.setDown(false);
						this.setMovY(-this.getMovY());
						if(ladrillo.getLifes() > 0){
							ladrillo.setLifes(ladrillo.getLifes()-1);
						}
						if(ladrillo.getLifes() == 0){
							Arkanoid.puntuacion += ladrillo.getPoints();
						}
						return true;
					}
				}else{
					if ((this.getCoordY() <= (posY+Ladrillo.BlqHeight))&&((this.getCoordY()+Pelota.BH) > posY)){
						this.setDown(true);
						this.setMovY(-this.getMovY());
						if(ladrillo.getLifes() > 0){
							ladrillo.setLifes(ladrillo.getLifes()-1);
						}
						if(ladrillo.getLifes() == 0){
							Arkanoid.puntuacion += ladrillo.getPoints();
						}
						return true;
					}
				}
			}
			return false;
		}
	}
}
