package arkanoid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.swing.ImageIcon;
import reproductor.jlap;
import javazoom.jl.decoder.JavaLayerException;

/**
 * Clase encargada de los ladrillos
 * @author Grupo5DIU
 */
public class Ladrillo {
	
        //--------------- Valores constantes ---------------
	
	/** La anchura del ladrillo */
        public static int BlqWidth = 30;
	
        /** La altura del ladrillo */
	public static int BlqHeight = 15;
	
	/** La posicion inicial del ladrillo */
        public static int posYinicial = 50;
	
	//--------------- Atributos ---------------
        
        /** Numero de ladrillos inmortales */
        public static int Immortales = 0;
	
	/** Coordenada X de los ladrillos */
        private int posX;
	
	/** Coordenada X de los ladrillos */
        private int posY;
	
	/** Vidas de los ladrillos */
        private int lifes;
	
	/** Puntaje de los ladrillos */
        private int points;
	
	/** Imagen de los ladrillos */
        private Image fondo = null;
	
	/** Audio del rebote con los ladrillos */
        private jlap mediaRebote = null;
	
	/** Premios asignados a los ladrillos */
        private int premio = 0;
		
        /**
         * Posicionamiento, asignacion de vidas e imagenes de los ladrillos
         * @param posX
         * @param posY
         * @param life
         */
        @SuppressWarnings("OverridableMethodCallInConstructor")
	public Ladrillo(int posX, int posY, int life){
		this.setCoordX(posX);
		this.setCoordY(posY);
		this.setLifes(life);
		this.fondo = new ImageIcon(this.getClass().getResource("/imagenes/ladrillo.png")).getImage();
		this.setPoints(life * 100);
		try {
			mediaRebote = new jlap("\\UDP\\Arkanoid\\sonidos\\weak_ball.mp3");
		} catch (JavaLayerException | IOException e) {
		}
	}
	
        /**
         * Metodo asignador de las vidas de los ladrillos
         * @param life
         */
        public void setLifes(int life){
		this.lifes = life;
	}
	
        /**
         * Metodo de devolución de las vidas de los ladrillos
         * @return
         */
        public int getLifes(){
		return this.lifes;
	}
	
        /**
         * Metodo asignador del posicionamiento en la coordenada x
         * @param pos
         */
        public void setCoordX(int pos){
		this.posX = pos;
	}
	
        /**
         * Metodo de devolucion del posicionamiento en la coordenada x
         * @return
         */
        public int getCoordX(){
		return this.posX;
	}
	
        /**
         * Metodo asignador del posicionamiento en la coordenada y
         * @param pos
         */
        public void setCoordY(int pos){
		this.posY = pos;
	}
	
        /**
         * Metodo de devolucion del posicionamiento en la coordenada y
         * @return
         */
        public int getCoordY(){
		return this.posY;
	}
	
        /**
         * Metodo asignador del puntaje
         * @param puntos
         */
        public void setPoints(int puntos){
		this.points = puntos;
	}
	
        /**
         * Metodo de devolucion del puntaje
         * @return
         */
        public int getPoints(){
		return this.points;
	}
	
        /**
         * Metodo asignador de los premios
         * @param prem
         */
        public void setPremio(int prem){
		this.premio = prem;
	}

        /**
         * Metodo de devolucion de los premios
         * @return
         */
        public int getPremio() {
		return this.premio;
	}
	
        /**
         * Metodo encargado del pintado de los ladrillos
         * @param gr
         */
        public void paint(Graphics gr){
		if(this.getLifes()!=0){
			switch (this.getLifes()) {
				case -1 -> gr.setColor(Color.BLACK);
				case 1 -> gr.setColor(Color.GREEN);
				case 2 -> gr.setColor(Color.YELLOW);
				case 3 -> gr.setColor(Color.CYAN);
				case 4 -> gr.setColor(Color.ORANGE);
				case 5 -> gr.setColor(Color.MAGENTA);
                                case 6 -> gr.setColor(Color.BLUE);
                                case 7 -> gr.setColor(Color.RED);
				default -> {
                        }
			}
			gr.fillRect(this.getCoordX(),this.getCoordY(),BlqWidth,BlqHeight);
			gr.setColor(Color.GRAY);
			gr.drawRect(this.getCoordX(),this.getCoordY(),BlqWidth,BlqHeight);
			gr.drawImage(fondo, this.getCoordX(), this.getCoordY(), null);
		}
	}
	
        /**
         * Metodo encargado del rebote de las pelotas y del estado de
         * destruccion del ladrillo
         * @param pelota
         * @return
         * @throws JavaLayerException
         * @throws IOException
         */
        public boolean destruido(Pelota pelota) throws JavaLayerException, IOException{
		if(pelota.rebota(this.getCoordX(), this.getCoordY(), Ladrillo.BlqWidth, Ladrillo.BlqHeight, this)){
			mediaRebote.play();
			return true;
		}else{
			return false;
		}
	}
	
}
