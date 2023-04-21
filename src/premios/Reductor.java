package premios;

import javax.swing.ImageIcon;

public class Reductor extends Premio{
	
	public static final int PW = 20;
	
	public static final int PH = 10;
	
	public Reductor(int posx, int posy){
		super(posx,posy,PW,PH);
		setFondo(new ImageIcon(this.getClass().getResource("/imagenes/reducir.jpg")).getImage());
	}
}