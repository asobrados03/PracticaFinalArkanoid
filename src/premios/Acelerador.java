package premios;

import javax.swing.ImageIcon;

public class Acelerador extends Premio{
	
	public static final int PW = 20;
	
	public static final int PH = 10;
	
	public Acelerador(int posx, int posy){
		super(posx,posy,PW,PH);
		setFondo(new ImageIcon(this.getClass().getResource("/imagenes/acelerar.jpg")).getImage());
	}
}