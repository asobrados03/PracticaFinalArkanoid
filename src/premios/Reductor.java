package premios;

import javax.swing.ImageIcon;

/**
 * Clase encargada del premio reductor
 * @author Grupo5DIU
 */
public class Reductor extends Premio{
	
	//--------------- Valores constantes ---------------
	
	/** La anchura del efecto */
        public static final int PW = 20;
	
	/** La altura del efecto */
        public static final int PH = 10;
	
        /**
         * Posicionamiento y asignacion de imagenes del efecto
         * @param posx
         * @param posy
         */
        public Reductor(int posx, int posy){
		super(posx,posy,PW,PH);
		setFondo(new ImageIcon(this.getClass().getResource("/imagenes/reducir.jpg")).getImage());
	}
}