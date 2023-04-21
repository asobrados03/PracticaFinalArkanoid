package premios;

import java.awt.Graphics;
import java.awt.Image;

import arkanoid.Raqueta;

public abstract class Premio {
	private static final double MOVY = 2.0;
	
	private int width;
	
	private int height;
	
	private int posX;
	
	private int posY;
	
	private Image fondo;
	
	public Premio(int posx, int posy, int wd, int hg){
		this.posX = posx;
		this.posY = posy;
		this.width = wd;
		this.height = hg;
	}

	public void pinta(Graphics gr){
		gr.drawImage(fondo, getPosX(), getPosY(), null);
	}
	
	public void setFondo(Image image){
		this.fondo = image;
	}
	
	public int getWidth(){
		return width;
	}

	public void setWidth(int width){
		this.width = width;
	}

	public int getHeight(){
		return height;
	}

	public void setHeight(int height){
		this.height = height;
	}

	public int getPosX(){
		return posX;
	}

	public void setPosX(int posX){
		this.posX = posX;
	}

	public int getPosY(){
		return posY;
	}

	public void setPosY(int posY){
		this.posY = posY;
	}
	
	public void move(){
		setPosY((int)(getPosY() + MOVY));
	}
	
	public boolean recivido(Raqueta raqueta){
		if(this.getPosX() >= raqueta.getCoordX() - this.getWidth() && this.getPosX() + this.getWidth() <= raqueta.getCoordX() + Raqueta.RACKET_W){
			if((this.getPosY() + this.getHeight() >= raqueta.getCoordY())&&(this.getPosY() <= raqueta.getCoordY()+Raqueta.RACKET_H)){
				return true;
			}
		}
		return false;
	}
}
