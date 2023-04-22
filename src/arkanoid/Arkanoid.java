package arkanoid;

import reproductor.jlap;
import premios.Freno;
import premios.Premio;
import premios.Acelerador;
import premios.MasPelotas;
import premios.Reductor;
import premios.Expansor;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javazoom.jl.decoder.JavaLayerException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

public class Arkanoid extends JPanel implements MouseInputListener {

	
	private static final long serialVersionUID = 1L;

	//--------------- Valores constantes ---------------
	
	/** La anchura de la ventana principal */
	public static final int FRAME_W=277;

	/** La altura de la ventana principal */
	public static final int FRAME_H=450;
	
	/** El número de imágenes por segundo. Puede aumentarse
	 o disminuirse para acelerar o ralentizar, respectivamente,
	 la velocidad del juego. */
	public static final int FPS=50;

	/** El tiempo que debe esperarse tras cada cambio de posici�n
	 de la bola */
	public static final int WAIT_TIME=1000/FPS;
	
	
	//--------------- Atributos ---------------

	/** Anchura de la zona interior de la ventana en la que se mueve la pelota */
	private int panelW;
	
	/** Altura de la zona interior de la ventana en la que se mueve la pelota */
	private int panelH;
	
	/** ArrayList contenedor de las pelotas */
	private ArrayList<Pelota> pelotas = new ArrayList<Pelota>();

	private ArrayList<Premio> premios = new ArrayList<Premio>();
	
	private Raqueta raqueta;
	
	private ArrayList<Object> pauseX = new ArrayList<Object>();
	
	private ArrayList<Object> pauseY = new ArrayList<Object>();
	
	private ArrayList<Ladrillo> ladrillos = null;
	
	private ArrayList<Time> timeLevels = new ArrayList<Time>();
	
	private static jlap jlPlayer;
	
	private long startTime = 0;
	
	private long finalTime = 0;
	
	private int level = 1;
	
	private int numLevels = 5 ;
	
	private int lifes = 3;
	
	private int timeNextLevel = 0;
	
	public static int puntuacion = 0;
	
	private Image image = null;
	
	private Image fondoVidas = null;

	private Random random;
	
	private Random randomPremios;

	public Arkanoid() {
		this.fondoVidas = new ImageIcon(this.getClass().getResource("/imagenes/bola_trans.png")).getImage();
	}

	public void setImage(String img) {
	    this.image = new ImageIcon(this.getClass().getResource(img)).getImage();
	}
	
	/** Programa principal. Se encarga de inicializar el juego,
	 crear la ventana principal y mostrarla. Finalmente invoca a
	 playGame, que se encarga de mover la pelota.
	 */
	public static void main(String[] args) {
		Arkanoid panel = null;
		JFrame frame;

		// Inicialización de la ventana principal
		panel=new Arkanoid();
		frame=new JFrame("Arkanoid");
		try {
			panel.crearFicheros();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		hideMouse(frame);
		frame.setResizable(false);
		frame.setContentPane(panel);
		frame.setSize(FRAME_W,FRAME_H);
		Dimension screenRes = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenRes.width-FRAME_W)/2, (screenRes.height-FRAME_H)/2);
		// El código siguiente se encarga de terminar el
		// programa cuando el usuario cierra la ventana
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});
		// Inicialización de los atributos del juego
		panel.raqueta = new Raqueta(FRAME_W/2 - Raqueta.RACKET_W/2,0);
		// Se registra para capturar eventos de ratón
		panel.addMouseMotionListener(panel);
		panel.addMouseListener(panel);
		frame.setVisible(true);	// Muestra la ventana principal
		try {
			panel.playGame();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void crearFicheros() throws IOException {
		String[] directorios = {"sonidos"};
		String[][] archivos = new String[][]{
			{"weak_ball.mp3","click.mp3","fondo1.mp3","fondo2.mp3","fondo3.mp3","fondo4.mp3","fondo5.mp3"}
		};
		for(int x = 0; x < directorios.length; x++){
			File directorio = new File("\\UDP\\Arkanoid" + "\\"+directorios[x]);
			directorio.mkdirs();
			directorio.setWritable(true);
			for(int y = 0; y < archivos[x].length; y++){
				String archivo = directorio.getCanonicalPath() + "\\"+archivos[x][y];
		
				File temp = new File(archivo);
				InputStream is = (InputStream) this.getClass().getResourceAsStream("/"+directorios[x]+"/"+archivos[x][y]);
				FileOutputStream archivoDestino = new FileOutputStream(temp);
				byte[] buffer = new byte[512*1024];
				int nbLectura;
		
				while ((nbLectura = is.read(buffer)) != -1)
				archivoDestino.write(buffer, 0, nbLectura);
			}
		}		
	}

	@SuppressWarnings("deprecation")
	public void paint(Graphics gr) {
		// Borramos el interior de la ventana.
		Dimension d = getSize();
		panelW = d.width; panelH = d.height;
		if(this.level > this.numLevels){
			gr.setColor(Color.blue);
			gr.fillRect(0,0,panelW,panelH);
			this.setImage("/imagenes/logo.png");
			gr.drawImage(image, (panelW - image.getWidth(null))/2, 20, null);
			Font alerta = new Font("Sans Serif",Font.BOLD,30);
			int heightFinal = 20 + image.getHeight(null) + 20;
			gr.setFont(alerta);
			gr.setColor(Color.WHITE);
			String frases = "Fin del Juego!";
			heightFinal += gr.getFontMetrics().getHeight();
			gr.drawString(frases, this.getWidth()/2-gr.getFontMetrics().stringWidth(frases)/2, heightFinal);
			heightFinal = panelH - 100;
			alerta = new Font("Sans Serif",Font.BOLD,20);
			gr.setFont(alerta);
			frases = "Desarrollado Por:";
			gr.drawString(frases, this.getWidth()/2-gr.getFontMetrics().stringWidth(frases)/2, heightFinal);
			frases = "Cristian Castillejo";
			heightFinal += gr.getFontMetrics().getHeight();
			gr.drawString(frases, this.getWidth()/2-gr.getFontMetrics().stringWidth(frases)/2, heightFinal);
			frases = "ccastillejo@uniondeprogramadores.com";
			heightFinal += gr.getFontMetrics().getHeight();
			alerta = new Font("Sans Serif",Font.BOLD,10);
			gr.setFont(alerta);
			gr.drawString(frases, this.getWidth()/2-gr.getFontMetrics().stringWidth(frases)/2, heightFinal);
		}else{
			raqueta.setCoordY(panelH - Raqueta.RACKET_H * 5);
			if(pelotas.isEmpty()){
				pelotas.add(new Pelota(FRAME_W/2 - Pelota.BW/2,raqueta.getCoordY() - Raqueta.RACKET_H));
			}
			gr.setColor(Color.white);
			gr.fillRect(0,0,panelW,panelH);
			gr.drawImage(image, 0, 0, null);
			// Pintamos la pelota
			for(Pelota pel : pelotas){
				pel.paint(gr);
			}
			// Pintamos la raqueta
			raqueta.paint(gr);
			// Pintamos los Bloques
			if(this.ladrillos != null){
				for(Ladrillo bloq : this.ladrillos){
					bloq.paint(gr);
				}
			}
			// Pintamos los Premios
			if(this.premios != null){
				for(Premio prem : this.premios){
					prem.pinta(gr);
				}
			}
			// Pintamos fondos y textos
			gr.setColor(Color.black);
			gr.fillRect(0, raqueta.getCoordY()+Raqueta.RACKET_H, panelW, raqueta.getCoordY()+Raqueta.RACKET_H);
			gr.fillRect(0, 0, panelW, 30);
			for(int x = 0; x < lifes; x++){
				gr.setColor(Color.red);
				gr.fillOval(10 + (x * (Pelota.BW + 2)),15 - (Pelota.BH / 2),Pelota.BW-1,Pelota.BH-1);
				gr.drawImage(fondoVidas,10 + (x * (Pelota.BW + 2)),15 - (Pelota.BH / 2),null);
			}
			gr.setColor(Color.white);
			Font contador = new Font("Sans Serif",Font.BOLD,13);
			gr.setFont(contador);
			gr.drawString("Puntuación: "+puntuacion,(panelW - (gr.getFontMetrics().stringWidth("Puntuación: "+puntuacion))-10),30-(gr.getFontMetrics().getHeight()/2));
			gr.drawString("Nivel: "+level,10,(panelH -10));
			if(this.ladrillos != null){
				if(this.ladrillos.size()-Ladrillo.Immortales > 0){
					if(this.startTime >0){
						long playTime = System.currentTimeMillis() - this.startTime;
						Time crono = new Time(playTime);
						String tiempo = crono.getMinutes()+":"+crono.getSeconds();
						gr.drawString(tiempo, (panelW - (gr.getFontMetrics().stringWidth(tiempo))-10), (panelH -10));
					}
				}else{
					if(this.startTime > 0){
						if(this.finalTime == 0){
							this.finalTime = System.currentTimeMillis() - this.startTime;
						}
						Time crono = new Time(this.finalTime);
						String tiempo = crono.getMinutes()+":"+crono.getSeconds();
						gr.drawString(tiempo, (panelW - (gr.getFontMetrics().stringWidth(tiempo))-10), (panelH -10));
					}
					Font alerta = new Font("Sans Serif",Font.BOLD,30);
					gr.setFont(alerta);
					gr.setColor(Color.RED);
					String completado = "Completado!";
					gr.drawString(completado, this.getWidth()/2-gr.getFontMetrics().stringWidth(completado)/2, 100);
					int nextLevelY = 110 + gr.getFontMetrics().getHeight();
					completado = "Próximo nivel";
					gr.drawString(completado, this.getWidth()/2-gr.getFontMetrics().stringWidth(completado)/2, nextLevelY);
					nextLevelY += 10 + gr.getFontMetrics().getHeight();
					completado = ""+timeNextLevel;
					gr.drawString(completado, this.getWidth()/2-gr.getFontMetrics().stringWidth(completado)/2, nextLevelY);
				}
			}
		}
	}

	private void playGame() throws JavaLayerException, IOException {
		setImage("/imagenes/fondo"+this.level+".png");
		jlPlayer = new jlap("\\UDP\\Arkanoid\\sonidos\\fondo"+level+".mp3");
		jlPlayer.play();
		this.generarBloques();
		long nextTime,currTime;
		int fpsOverflow;
		
		fpsOverflow=0;
		nextTime=System.currentTimeMillis();
		while (true) {
			// Espera de un tiempo fijo
			currTime = System.currentTimeMillis();
			if (currTime<nextTime)
				try { Thread.sleep(nextTime-currTime); }
				catch (Exception e) {}
			else fpsOverflow++;
			nextTime+=WAIT_TIME;
			// Actualizaci�n de las coordenadas e incrementos de la pelota
			int cont = 0;
			for (Pelota pel : pelotas){
				pel.move(panelW, panelH);
				if(pel.isDown()){
					if(pel.getCoordY() > (raqueta.getCoordY()+Raqueta.RACKET_H)){
						pelotas.remove(cont);
						if(pelotas.isEmpty()){
							this.lifes --;
						}
						if(lifes >= 0 && pelotas.isEmpty()){
							pelotas.add(new Pelota(raqueta.getCoordX() + Raqueta.RACKET_W/2 - Pelota.BW/2,raqueta.getCoordY() - Raqueta.RACKET_H));
						}
						break;
					}
					if (((pel.getCoordX()+Pelota.BW) >= raqueta.getCoordX())&&(pel.getCoordX() <= raqueta.getCoordX()+Raqueta.RACKET_W)&&((pel.getCoordY()+Pelota.BH) >= raqueta.getCoordY())&&(pel.getCoordY() < (raqueta.getCoordY()+Raqueta.RACKET_H))){
						pel.setDown(false);
						pel.rebota(raqueta.getCoordX(), raqueta.getCoordY(), Raqueta.RACKET_W, Raqueta.RACKET_H, null);
					}
				}
				int contBloq = 0;
				for (Ladrillo bloq : ladrillos){
					if(bloq.destruido(pel)){
						if(bloq.getLifes() == 0){
							switch (bloq.getPremio()) {
								case 1:
									premios.add(new Expansor(bloq.getCoordX() + Ladrillo.BlqWidth/2 - Expansor.PW/2, bloq.getCoordY()));
									break;
								case 2:
									premios.add(new MasPelotas(bloq.getCoordX() + Ladrillo.BlqWidth/2 - MasPelotas.BW/2, bloq.getCoordY()));
									break;
								case 3:
									premios.add(new Reductor(bloq.getCoordX() + Ladrillo.BlqWidth/2 - MasPelotas.BW/2, bloq.getCoordY()));
									break;
								case 4:
									premios.add(new Acelerador(bloq.getCoordX() + Ladrillo.BlqWidth/2 - MasPelotas.BW/2, bloq.getCoordY()));
									break;
								case 5:
									premios.add(new Freno(bloq.getCoordX() + Ladrillo.BlqWidth/2 - MasPelotas.BW/2, bloq.getCoordY()));
									break;
								default:
									break;
							}
							ladrillos.remove(contBloq);
						}
						break;
					}
					contBloq ++;
				}
				cont ++;
			}
			cont = 0;
			if(!ladrillos.isEmpty()){
				for(Premio prem : premios){
					prem.move();
					if(prem.recivido(raqueta)){
						if (prem instanceof Expansor) {
							raqueta.ampliar();
						}else if(prem instanceof MasPelotas){
							this.pelotas.add(new Pelota(raqueta.getCoordX() + Raqueta.RACKET_W/2 - Pelota.BW/2,raqueta.getCoordY() - Raqueta.RACKET_H));
						}else if(prem instanceof Reductor){
							raqueta.reduir();
						}else if(prem instanceof Acelerador){
							for(Pelota pelota : pelotas){
								pelota.acelera();
							}
						}else if(prem instanceof Freno){
							for(Pelota pelota : pelotas){
								pelota.frena();
							}
						}
						premios.remove(cont);
						break;
					}else if(prem.getPosY() + prem.getWidth() >= raqueta.getCoordY() + Raqueta.RACKET_H){
						premios.remove(cont);
						break;
					}
					cont++;
				}
			}
			// Repintado de la ventana para actualizar su contenido
			repaint();
			if(ladrillos.size()-Ladrillo.Immortales == 0 || this.lifes < 0){
				timeLevels.add(new Time(System.currentTimeMillis() - this.startTime));
				this.startTime = 0;
				break;
			}
		}
		premios.clear();
		Raqueta.RACKET_W = 50;
		raqueta = new Raqueta(FRAME_W/2 - Raqueta.RACKET_W/2,0);
		jlPlayer.player.close();
		if(this.level < this.numLevels && this.lifes >= 0){
			jlap.iniciar = false;
			for(int y = 0; y < 5; y++){
				timeNextLevel = 5-y;
				repaint();
				try { Thread.sleep(1000); }
				catch (Exception e) {}
			}
			this.pelotas.clear();
			this.pelotas.add(new Pelota(raqueta.getCoordX() + Raqueta.RACKET_W/2 - Pelota.BW/2,raqueta.getCoordY() - Raqueta.RACKET_H));
			this.level += 1;
			this.ladrillos = null;
			this.playGame();
		}else if(this.level == this.numLevels){
			this.level++;
			repaint();
		}
	}

        public void mouseDragged(MouseEvent evt){
	}

	public void mouseMoved(MouseEvent evt){
		raqueta.setCoordX(evt.getX() - Raqueta.RACKET_W/2);
		if (raqueta.getCoordX()<0) raqueta.setCoordX(0);
		if (raqueta.getCoordX()+Raqueta.RACKET_W>=panelW) raqueta.setCoordX(panelW-Raqueta.RACKET_W);
		int cont = 0;
		for (Pelota pel : pelotas){
			if(pel.getMovX() == 0 && pel.getMovY() == 0){
				pel.setCoordX(evt.getX() - Pelota.BW/2);
				if (raqueta.getCoordX()<=0) pel.setCoordX(Raqueta.RACKET_W/2 - Pelota.BW/2);
				if (raqueta.getCoordX()+Raqueta.RACKET_W>=panelW) pel.setCoordX(panelW - Raqueta.RACKET_W/2 - Pelota.BW/2);
			}
			else if(pel.isDown()){
				if(pel.getCoordY() > (raqueta.getCoordY()+Raqueta.RACKET_H)){
					pelotas.remove(cont);
					if(pelotas.isEmpty()){
						this.lifes --;
					}
					if(lifes >= 0 && pelotas.isEmpty()){
						pelotas.add(new Pelota(raqueta.getCoordX() + Raqueta.RACKET_W/2 - Pelota.BW/2,raqueta.getCoordY() - Raqueta.RACKET_H));
					}
					break;
				}
				if (((pel.getCoordX()+Pelota.BW) >= raqueta.getCoordX())&&(pel.getCoordX() <= raqueta.getCoordX()+Raqueta.RACKET_W)&&((pel.getCoordY()+Pelota.BH) >= raqueta.getCoordY())&&(pel.getCoordY() < (raqueta.getCoordY()+Raqueta.RACKET_H))){
					pel.setDown(false);
					try {
						pel.rebota(raqueta.getCoordX(), raqueta.getCoordY(), Raqueta.RACKET_W, Raqueta.RACKET_H, null);
					} catch (JavaLayerException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			cont ++;
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		for (Pelota pel : pelotas){
			if(pel.getMovX() == 0 && pel.getMovY() == 0){
				pel.setMovX(0.0);
				pel.setMovY(-3.0);
			}
		}
		if(this.startTime == 0){
			this.startTime = System.currentTimeMillis();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (!pauseX.isEmpty()){
			int cont = 0;
			for (@SuppressWarnings("unused") Object pausX : pauseX){
				pelotas.get(cont).setMovX(Float.parseFloat(pauseX.get(cont).toString()));
				pelotas.get(cont).setMovY(Float.parseFloat(pauseY.get(cont).toString()));
				cont ++;
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		pauseX.removeAll(pauseX);
		pauseY.removeAll(pauseY);
		for (Pelota pelota : pelotas){
			pauseX.add(pelota.getMovX());
			pauseY.add(pelota.getMovY());
			pelota.setMovX(0);
			pelota.setMovY(0);
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	
	private static void hideMouse(JFrame frame) {
		BufferedImage emptyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		emptyImg.setRGB(0, 0, 0xFFFFFF);
		Cursor myCursor = Toolkit.getDefaultToolkit().
		createCustomCursor(emptyImg, new Point(0, 0), "invisible");
		frame.setCursor(myCursor);
	}
	
	private void generarBloques(){
		int constBloques[][] = null;
		switch (level) {
		case 1:
			constBloques = new int[][]{
				{0,1,1,1,1,1,1,1,0},
				{0,1,1,1,1,1,1,1,0},
				{0,1,1,1,1,1,1,1,0}
			};
			break;
		case 2:
			constBloques = new int[][]{
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,1,0,0,0,0},
				{0,0,0,1,1,1,0,0,0},
				{0,0,1,1,1,1,1,0,0},
				{0,1,1,1,-1,1,1,1,0},
				{0,0,1,1,1,1,1,0,0},
				{0,0,0,1,1,1,0,0,0},
				{0,0,0,0,1,0,0,0,0}
			};
			break;
		case 3:
			constBloques = new int[][]{
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,2,0,0,0,0},
				{0,0,0,1,2,1,0,0,0},
				{0,0,1,1,2,1,1,0,0},
				{0,3,1,1,-1,1,1,3,0},
				{0,0,1,1,2,1,1,0,0},
				{0,0,0,1,2,1,0,0,0},
				{0,0,0,0,2,0,0,0,0}
			};
			break;
		case 4:
			constBloques = new int[][]{
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{1,2,3,4,-1,4,3,2,1},
				{0,1,2,3,4,3,2,1,0},
				{0,0,1,2,3,2,1,0,0},
				{0,0,0,1,2,1,0,0,0},
				{0,0,0,0,-1,0,0,0,0},
				{0,0,0,1,2,1,0,0,0},
				{0,0,1,2,3,2,1,0,0},
				{0,1,2,3,4,3,2,1,0},
				{1,2,3,4,-1,4,3,2,1}
			};
			break;
		case 5:
			constBloques = new int[][]{
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,5,5,5,0,0,0},
				{0,0,5,1,1,1,5,0,0},
				{0,0,5,2,2,2,5,0,0},
				{0,0,5,2,2,2,5,0,0},
				{0,0,5,3,3,3,5,0,0},
				{0,0,5,3,3,3,5,0,0},
				{0,0,5,4,4,4,5,0,0},
				{0,0,5,-1,5,-1,5,0,0},
				{0,5,4,0,4,0,4,5,0},
				{0,5,4,0,4,0,4,5,0},
				{0,5,4,3,4,3,4,5,0},
				{0,0,5,4,4,4,5,0,0},
				{0,0,0,-1,-1,-1,0,0,0},
				{0,0,0,5,-1,5,0,0,0},
				{0,0,0,5,5,5,0,0,0}
			};
			break;
		default:
			break;
		}
		if(constBloques != null){
			this.ladrillos = new ArrayList<Ladrillo>();
			Ladrillo.Immortales = 0;
			for(int x = 0; x < constBloques.length; x++){
				for(int y = 0; y < constBloques[x].length; y++){
					if(constBloques[x][y] != 0){
						this.ladrillos.add(new Ladrillo((Ladrillo.BlqWidth * y),(Ladrillo.BlqHeight * x)+Ladrillo.posYinicial,constBloques[x][y]));
					}
					if(constBloques[x][y] < 0){
						Ladrillo.Immortales ++;
					}
				}
			}
			random = new Random();
			randomPremios = new Random();
			int number = 0;
			int premio = 0;
			for(int x = 0; x < ladrillos.size()/3; x++){
				premio = Math.abs( randomPremios.nextInt() % 5) + 1;
				number = Math.abs( random.nextInt() % ladrillos.size());
				ladrillos.get(number).setPremio(premio);
			}
		}
	}
}
