package premios;

import javax.swing.ImageIcon;

public class Expansor extends Premio{
	
	public static final int PW = 20;
	
	public static final int PH = 10;
	
	public Expansor(int posx, int posy){
		super(posx,posy,PW,PH);
		setFondo(new ImageIcon(this.getClass().getResource("/imagenes/expandir.jpg")).getImage());
	}
}