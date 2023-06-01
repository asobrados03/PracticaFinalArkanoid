package premios;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;

/**
 * Clase encargada del premio mas pelotas
 * @author Grupo5DIU
 */
public class MasPelotas extends Premio{

        //--------------- Valores constantes ---------------
	
	/** La anchura del efecto */
        public static final int BW = 15;
	
	/** La altura del efecto */
        public static final int BH = 15;
	
        /**
         * Posicionamiento y asignacion de imagenes del efecto
         * @param posx
         * @param posy
         */
        public MasPelotas(int posx, int posy){
		super(posx,posy,BW,BH);
		setFondo(new ImageIcon(this.getClass().getResource("/imagenes/bola_trans.png")).getImage());
	}
	
        /**
         * Metodo encargado del pintado del efecto
         * @param gr
         */
        @Override
	public void pinta(Graphics gr){
		gr.setColor(Color.GREEN);
		gr.fillOval((int)getPosX(),(int)getPosY(),BW-1,BH-1);
		super.pinta(gr);
	}
}
