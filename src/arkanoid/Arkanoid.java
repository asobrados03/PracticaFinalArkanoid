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
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Arkanoid extends JPanel implements KeyListener, MouseInputListener {

    private static final long serialVersionUID = 1L;

    //--------------- Valores constantes ---------------
    /**
     * La anchura de la ventana principal
     */
    public static final int FRAME_W = 277;

    /**
     * La altura de la ventana principal
     */
    public static final int FRAME_H = 450;

    /**
     * El número de imágenes por segundo. Puede aumentarse o disminuirse para
     * acelerar o ralentizar, respectivamente, la velocidad del juego.
     */
    public static final int FPS = 50;

    /**
     * El tiempo que debe esperarse tras cada cambio de posici�n de la bola
     */
    public static final int WAIT_TIME = 1000 / FPS;

    //--------------- Atributos ---------------
    /**
     * Anchura de la zona interior de la ventana en la que se mueve la pelota
     */
    private int panelW;

    /**
     * Altura de la zona interior de la ventana en la que se mueve la pelota
     */
    private int panelH;

    /**
     * ArrayList contenedor de las pelotas
     */
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

    private int level = 0;

    private final int numLevels = 7;

    private int lifes = 3;
    
    private int lifesreset = 0;

    private int timeNextLevel = 0;

    public static int puntuacion = 0;
    
    public static int puntuacionreset = 0;

    private Image image = null;

    private Image fondoVidas = null;

    private Random random;

    private Random randomPremios;

    private static final int MOVEMENT_SPEED = 5; // velocidad de movimiento con las teclas

    private static boolean moverIzquierda = false;
    private static boolean moverDerecha = false;
    private static boolean inicializar = false;
    private static boolean reset = false;
    private static boolean sound = false;
    private static boolean mouseExited = false;
    
    private String[] english;
    private String[] spanish;

    public Arkanoid() {
        this.english = new String[]{"Press any key", "para iniciar el juego", "Fin del Juego!", "Desarrollado Por:", "Siete alumnos de DIU", "Has Ganado", "Puntaje: ", "Puntuación: ", "Nivel: ", "Completado!", "Próximo nivel"};
        this.spanish = new String[]{"Presiona cualquier tecla", "for start the game", "Game Over!", "Developed For:", "Seven DIU students", "You Win", "Score: ", "Score: ", "Level: ", "Filled!", "Next level"};
        this.fondoVidas = new ImageIcon(this.getClass().getResource("/imagenes/red-mc.png")).getImage().getScaledInstance(15, 15, Image.SCALE_DEFAULT);
    }

    public void setImage(String img) {
        if(this.level >= 0){
            this.image = new ImageIcon(this.getClass().getResource(img)).getImage();
        }
    }

    /**
     * * Programa principal.Se encarga de inicializar el juego, crear la
     * ventana principal y mostrarla.Finalmente invoca a playGame, que se
 encarga de mover la pelota.
     *
     * @param args
     * @throws javazoom.jl.decoder.JavaLayerException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws JavaLayerException, IOException {
        Arkanoid panel = null;
        JFrame frame;

        // Inicialización de la ventana principal
        panel = new Arkanoid();
        frame = new JFrame("Arkanoid");
        try {
            panel.crearFicheros();
        } catch (IOException e1) {
        }
        hideMouse(frame);
        frame.setResizable(false);
        frame.setContentPane(panel);
        frame.setSize(FRAME_W, FRAME_H);
        Dimension screenRes = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenRes.width - FRAME_W) / 2, (screenRes.height - FRAME_H) / 2);
        // El código siguiente se encarga de terminar el
        // programa cuando el usuario cierra la ventana
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
        // Inicialización de los atributos del juego
        panel.raqueta = new Raqueta(FRAME_W / 2 - Raqueta.RACKET_W / 2, 0);
        // Se registra para capturar eventos de ratón
        panel.addMouseMotionListener(panel);
        panel.addMouseListener(panel);
        panel.setFocusable(true);
        panel.addKeyListener(panel);

        frame.setVisible(true);
        // Muestra la ventana principal
        
        if(panel != null){
            panel.inicio();
            while(inicializar = true){
                try {
                    panel.playGame();
                } catch (JavaLayerException | IOException e) {
                }
            }
        }
    }

    public void sonidoInicio() throws JavaLayerException, IOException {
        while(!sound){
            jlPlayer = new jlap("\\UDP\\Arkanoid\\sonidos\\fondo0.mp3");
            jlPlayer.play();
            this.sound = true;
        }
    }
    
    public void sonidoMuerte() throws JavaLayerException, IOException {
        jlPlayer.player.close();
        while(!sound){
            jlPlayer = new jlap("\\UDP\\Arkanoid\\sonidos\\gameover.mp3");
            jlPlayer.play();
            this.sound = true;
            System.out.println("Código ejecutado una vez.1");
        }
    }
    public void sonidoVictoria() throws JavaLayerException, IOException {
        while(!sound){
            jlPlayer = new jlap("\\UDP\\Arkanoid\\sonidos\\victory.mp3");
            jlPlayer.play();
            this.sound = true;
            System.out.println("Código ejecutado una vez.2");
        }
    }
    
    private void crearFicheros() throws IOException {
        String[] directorios = {"sonidos"};
        String[][] archivos = new String[][]{
            {"weak_ball.mp3", "click.mp3", "fondo1.mp3", "fondo2.mp3", "fondo3.mp3", "fondo4.mp3", "fondo5.mp3", "fondo6.mp3", "fondo7.mp3", "fondo0.mp3", "gameover.mp3", "victory.mp3"}
        };
        for (int x = 0; x < directorios.length; x++) {
            File directorio = new File("\\UDP\\Arkanoid" + "\\" + directorios[x]);
            directorio.mkdirs();
            directorio.setWritable(true);
            for (String archivo1 : archivos[x]) {
                String archivo = directorio.getCanonicalPath() + "\\" + archivo1;
                File temp = new File(archivo);
                InputStream is = (InputStream) this.getClass().getResourceAsStream("/" + directorios[x] + "/" + archivo1);
                FileOutputStream archivoDestino = new FileOutputStream(temp);
                byte[] buffer = new byte[512 * 1024];
                int nbLectura;
                while ((nbLectura = is.read(buffer)) != -1) {
                    archivoDestino.write(buffer, 0, nbLectura);
                }
            }
        }
    }
    
    private void inicio() {
        while(!inicializar){
            repaint();

            long nextTime, currTime;
            int fpsOverflow;

            fpsOverflow = 0;
            nextTime = System.currentTimeMillis();

            currTime = System.currentTimeMillis();
            if (currTime < nextTime)
                                try {
                Thread.sleep(nextTime - currTime);
            } catch (InterruptedException e) {
            } else {
                fpsOverflow++;
            }
            nextTime += WAIT_TIME;
        }
        if(inicializar){
            this.sound = false;
        }
    }
    
    public void paintInicio(Graphics gr) throws JavaLayerException, IOException{
        sonidoInicio();
        this.setImage("/imagenes/arkanoid_logo.png");
        gr.drawImage(image, -10, 0, null);
        String iniciar="Presiona cualquier tecla";

        gr.setColor(Color.LIGHT_GRAY);

        Font inicio = new Font("Serif", Font.BOLD + Font.ITALIC, 24);
        gr.setFont(inicio);
        gr.drawString(iniciar, 10, (panelH - 58));

        iniciar="para iniciar el juego";
        gr.setFont(inicio);
        gr.drawString(iniciar, 30, (panelH - 30));
    }
    
    public void paintPause(Graphics gr) throws JavaLayerException, IOException{
        Font alerta = new Font("Sans Serif", Font.BOLD, 30);
        gr.setFont(alerta);
        gr.setColor(Color.RED);
        String completado = "PAUSE";
        gr.drawString(completado, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(completado) / 2, 100);
        String iniciar="Presiona cualquier tecla";
        Font inicio = new Font("Sans Serif", Font.BOLD, 22);
        gr.setFont(inicio);
        gr.drawString(iniciar, 10, (panelH - 58));
        iniciar="para iniciar el juego";
        gr.setFont(inicio);
        gr.drawString(iniciar, 30, (panelH - 30));
    }
    
    public void paintMuerte(Graphics gr) throws JavaLayerException, IOException{
        sonidoMuerte();
        this.setImage("/imagenes/fondofinal.png");
        gr.drawImage(image, 0, 0, null);
        this.setImage("/imagenes/red-bh.png");
        gr.drawImage(image, (panelW - image.getWidth(null)) / 2, 20, null);
        Font alerta = new Font("Sans Serif", Font.BOLD, 30);
        int heightFinal = 20 + image.getHeight(null) + 20;
        gr.setFont(alerta);
        gr.setColor(Color.RED);
        String frases = "Fin del Juego!";
        heightFinal += gr.getFontMetrics().getHeight();
        gr.drawString(frases, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(frases) / 2, heightFinal);
        heightFinal = panelH - 100;
        alerta = new Font("Sans Serif", Font.BOLD, 20);
        gr.setFont(alerta);
        frases = "Desarrollado Por:";
        gr.drawString(frases, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(frases) / 2, heightFinal);
        frases = "Siete alumnos de DIU";
        heightFinal += gr.getFontMetrics().getHeight();
        gr.drawString(frases, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(frases) / 2, heightFinal);
    }
    
    public void paintVictoria(Graphics gr) throws JavaLayerException, IOException{
        sonidoVictoria();
        this.setImage("/imagenes/fondofinal.png");
        gr.drawImage(image, 0, 0, null);
        this.setImage("/imagenes/logo.png");
        gr.drawImage(image, (panelW - image.getWidth(null)) / 2, 50, null);
        Font alerta = new Font("Sans Serif", Font.BOLD, 30);
        int heightFinal = 50 + image.getHeight(null);
        gr.setFont(alerta);
        gr.setColor(Color.GREEN);
        String frases = "Has Ganado";
        heightFinal += gr.getFontMetrics().getHeight();
        gr.drawString(frases, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(frases) / 2, heightFinal);
        heightFinal = panelH - 150;
        alerta = new Font("Sans Serif", Font.BOLD, 25);
        gr.setFont(alerta);
        gr.setColor(Color.GREEN);
        gr.drawString("Puntaje: " + puntuacion, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(frases) / 2, heightFinal);
        heightFinal = panelH - 100;
        alerta = new Font("Sans Serif", Font.BOLD, 20);
        gr.setFont(alerta);
        frases = "Desarrollado Por:";
        gr.drawString(frases, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(frases) / 2, heightFinal);
        frases = "Siete alumnos de DIU";
        heightFinal += gr.getFontMetrics().getHeight();
        gr.drawString(frases, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(frases) / 2, heightFinal);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void paint(Graphics gr){
        // Borramos el interior de la ventana.
        Dimension d = getSize();
        panelW = d.width;
        panelH = d.height;
        if ((this.lifes != -1)&&(this.level > this.numLevels)&&(inicializar == true)) {
            try {
                paintVictoria(gr);
            } catch (JavaLayerException | IOException ex) {
                Logger.getLogger(Arkanoid.class.getName()).log(Level.SEVERE, null, ex);
            }
        }if ((this.lifes == -1)&&(this.level <= this.numLevels)&&(inicializar == true)) {
            try {
                paintMuerte(gr);
            } catch (JavaLayerException | IOException ex) {
                Logger.getLogger(Arkanoid.class.getName()).log(Level.SEVERE, null, ex);
            }
        }if ((this.lifes != -1)&&(this.level <= this.numLevels)&&(this.level > 0)&&(inicializar == true)){
            raqueta.setCoordY(panelH - Raqueta.RACKET_H * 5);
            if (pelotas.isEmpty()) {
                pelotas.add(new Pelota(FRAME_W / 2 - Pelota.BW / 2, raqueta.getCoordY() - Raqueta.RACKET_H));
            }
            gr.setColor(Color.white);
            gr.fillRect(0, 0, panelW, panelH);
            gr.drawImage(image, 0, 0, null);
            // Pintamos la pelota
            for (Pelota pel : pelotas) {
                pel.paint(gr);
            }
            // Pintamos la raqueta
            raqueta.paint(gr);
            // Pintamos los Bloques
            if (this.ladrillos != null) {
                for (Ladrillo bloq : this.ladrillos) {
                    bloq.paint(gr);
                }
            }
            // Pintamos los Premios
            if (this.premios != null) {
                for (Premio prem : this.premios) {
                    prem.pinta(gr);
                }
            }
            // Pintamos fondos y textos
            gr.setColor(Color.black);
            gr.fillRect(0, raqueta.getCoordY() + Raqueta.RACKET_H, panelW, raqueta.getCoordY() + Raqueta.RACKET_H);
            gr.fillRect(0, 0, panelW, 30);
            for (int x = 0; x < lifes; x++) {
                gr.fillOval(10 + (x * (Pelota.BW + 2)), 15 - (Pelota.BH / 2), Pelota.BW - 1, Pelota.BH - 1);
                gr.drawImage(fondoVidas, 10 + (x * (Pelota.BW + 2)), 15 - (Pelota.BH / 2), null);
            }
            gr.setColor(Color.white);
            Font contador = new Font("Sans Serif", Font.BOLD, 13);
            gr.setFont(contador);
            gr.drawString("Puntuación: " + puntuacion, (panelW - (gr.getFontMetrics().stringWidth("Puntuación: " + puntuacion)) - 10), 30 - (gr.getFontMetrics().getHeight() / 2));
            gr.drawString("Nivel: " + level, 10, (panelH - 10));
            if (this.ladrillos != null) {
                if (this.ladrillos.size() - Ladrillo.Immortales > 0) {
                    if (this.startTime > 0) {
                        long playTime = System.currentTimeMillis() - this.startTime;
                        Time crono = new Time(playTime);
                        String tiempo = crono.getMinutes() + ":" + crono.getSeconds();
                        gr.drawString(tiempo, (panelW - (gr.getFontMetrics().stringWidth(tiempo)) - 10), (panelH - 10));
                    }
                } else {
                    if (this.startTime > 0) {
                        if (this.finalTime == 0) {
                            this.finalTime = System.currentTimeMillis() - this.startTime;
                        }
                        Time crono = new Time(this.finalTime);
                        String tiempo = crono.getMinutes() + ":" + crono.getSeconds();
                        gr.drawString(tiempo, (panelW - (gr.getFontMetrics().stringWidth(tiempo)) - 10), (panelH - 10));
                    }
                    Font alerta = new Font("Sans Serif", Font.BOLD, 30);
                    gr.setFont(alerta);
                    gr.setColor(Color.RED);
                    String completado = "Completado!";
                    gr.drawString(completado, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(completado) / 2, 100);
                    int nextLevelY = 110 + gr.getFontMetrics().getHeight();
                    completado = "Próximo nivel";
                    gr.drawString(completado, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(completado) / 2, nextLevelY);
                    nextLevelY += 10 + gr.getFontMetrics().getHeight();
                    completado = "" + timeNextLevel;
                    gr.drawString(completado, this.getWidth() / 2 - gr.getFontMetrics().stringWidth(completado) / 2, nextLevelY);
                }
            }if(mouseExited && this.lifes!=-1 && this.level <= this.numLevels){
                try {
                    paintPause(gr);
                } catch (JavaLayerException | IOException ex) {
                    Logger.getLogger(Arkanoid.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }if ((this.lifes != -1)&&(this.level == 0)) {
            try {
                paintInicio(gr);
            } catch (JavaLayerException | IOException ex) {
                Logger.getLogger(Arkanoid.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void playGame() throws JavaLayerException, IOException {
        if(this.level != 0){
            jlPlayer.player.close();
            jlap.iniciar = false;
            this.lifesreset = this.lifes;
            this.puntuacionreset = this.puntuacion;
            setImage("/imagenes/fondo" + this.level + ".png");
            if(this.lifes != -1){
                jlPlayer = new jlap("\\UDP\\Arkanoid\\sonidos\\fondo" + level + ".mp3");
                jlPlayer.play();
            }
            this.generarBloques();
            long nextTime, currTime;
            int fpsOverflow;

            fpsOverflow = 0;
            nextTime = System.currentTimeMillis();
            while (true) {
                update();
                
                if(reset){
                    jlPlayer.player.close();
                    jlap.iniciar = false;
                    premios.clear();
                    this.pelotas.clear();
                    this.ladrillos = null;
                    this.reset = false;
                    this.lifes = this.lifesreset;
                    this.puntuacion = this.puntuacionreset - 1000;
                    repaint();
                    this.playGame();
                }
                
                // Espera de un tiempo fijo
                currTime = System.currentTimeMillis();
                if (currTime < nextTime)
                                    try {
                    Thread.sleep(nextTime - currTime);
                } catch (InterruptedException e) {
                } else {
                    fpsOverflow++;
                }
                nextTime += WAIT_TIME;
                // Actualización de las coordenadas e incrementos de la pelota
                int cont = 0;
                for (Pelota pel : pelotas) {
                    pel.move(panelW, panelH);
                    if (pel.isDown()) {
                        if (pel.getCoordY() > (raqueta.getCoordY() + Raqueta.RACKET_H)) {
                            pelotas.remove(cont);
                            if (pelotas.isEmpty()) {
                                this.lifes--;
                            }
                            if (lifes >= 0 && pelotas.isEmpty()) {
                                pelotas.add(new Pelota(raqueta.getCoordX() + Raqueta.RACKET_W / 2 - Pelota.BW / 2, raqueta.getCoordY() - Raqueta.RACKET_H));
                            }
                            break;
                        }
                        if (((pel.getCoordX() + Pelota.BW) >= raqueta.getCoordX()) && (pel.getCoordX() <= raqueta.getCoordX() + Raqueta.RACKET_W) && ((pel.getCoordY() + Pelota.BH) >= raqueta.getCoordY()) && (pel.getCoordY() < (raqueta.getCoordY() + Raqueta.RACKET_H))) {
                            pel.setDown(false);
                            pel.rebota(raqueta.getCoordX(), raqueta.getCoordY(), Raqueta.RACKET_W, Raqueta.RACKET_H, null);
                        }
                    }
                    int contBloq = 0;
                    for (Ladrillo bloq : ladrillos) {
                        if (bloq.destruido(pel)) {
                            if (bloq.getLifes() == 0) {
                                switch (bloq.getPremio()) {
                                    case 1 ->
                                        premios.add(new Expansor(bloq.getCoordX() + Ladrillo.BlqWidth / 2 - Expansor.PW / 2, bloq.getCoordY()));
                                    case 2 ->
                                        premios.add(new MasPelotas(bloq.getCoordX() + Ladrillo.BlqWidth / 2 - MasPelotas.BW / 2, bloq.getCoordY()));
                                    case 3 ->
                                        premios.add(new Reductor(bloq.getCoordX() + Ladrillo.BlqWidth / 2 - MasPelotas.BW / 2, bloq.getCoordY()));
                                    case 4 ->
                                        premios.add(new Acelerador(bloq.getCoordX() + Ladrillo.BlqWidth / 2 - MasPelotas.BW / 2, bloq.getCoordY()));
                                    case 5 ->
                                        premios.add(new Freno(bloq.getCoordX() + Ladrillo.BlqWidth / 2 - MasPelotas.BW / 2, bloq.getCoordY()));
                                    default -> {
                                    }
                                }
                                ladrillos.remove(contBloq);
                            }
                            break;
                        }
                        contBloq++;
                    }
                    cont++;
                }
                cont = 0;
                if (!ladrillos.isEmpty()) {
                    for (Premio prem : premios) {
                        prem.move();
                        if (prem.recivido(raqueta)) {
                            if (prem instanceof Expansor) {
                                raqueta.ampliar();
                            } else if (prem instanceof MasPelotas) {
                                this.pelotas.add(new Pelota(raqueta.getCoordX() + Raqueta.RACKET_W / 2 - Pelota.BW / 2, raqueta.getCoordY() - Raqueta.RACKET_H));
                            } else if (prem instanceof Reductor) {
                                raqueta.reduir();
                            } else if (prem instanceof Acelerador) {
                                for (Pelota pelota : pelotas) {
                                    pelota.acelera();
                                }
                            } else if (prem instanceof Freno) {
                                for (Pelota pelota : pelotas) {
                                    pelota.frena();
                                }
                            }
                            premios.remove(cont);
                            break;
                        } else if (prem.getPosY() + prem.getWidth() >= raqueta.getCoordY() + Raqueta.RACKET_H) {
                            premios.remove(cont);
                            break;
                        }
                        cont++;
                    }
                }
                // Repintado de la ventana para actualizar su contenido
                repaint();
                if (ladrillos.size() - Ladrillo.Immortales == 0 || this.lifes < 0) {
                    timeLevels.add(new Time(System.currentTimeMillis() - this.startTime));
                    this.startTime = 0;
                    break;
                }
            }
            premios.clear();
            Raqueta.RACKET_W = 50;
            raqueta = new Raqueta(FRAME_W / 2 - Raqueta.RACKET_W / 2, 0);
            jlPlayer.player.close();
            if (this.level < this.numLevels && this.lifes >= 0) {
                jlap.iniciar = false;
                for (int y = 0; y < 5; y++) {
                    timeNextLevel = 5 - y;
                    repaint();
                    update();
                    for (int x = 0; x < FPS; x++) {
                        currTime = System.currentTimeMillis();
                        if (currTime < nextTime)
                                            try {
                            Thread.sleep(nextTime - currTime);
                        } catch (InterruptedException e) {
                        } else {
                            fpsOverflow++;
                        }
                        nextTime += WAIT_TIME;
                        repaint();
                        update();
                    }
                }
                this.pelotas.clear();
                this.level += 1;
                this.ladrillos = null;
                this.playGame();
            } else if (this.level == this.numLevels) {
                this.level++;
                repaint();
            }
        }
        
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        if(!mouseExited && this.lifes!=-1 && this.level <= this.numLevels){
            raqueta.setCoordX(evt.getX() - Raqueta.RACKET_W / 2);
            if (raqueta.getCoordX() < 0) {
                raqueta.setCoordX(0);
            }
            if (raqueta.getCoordX() + Raqueta.RACKET_W >= panelW) {
                raqueta.setCoordX(panelW - Raqueta.RACKET_W);
            }
            int cont = 0;
            for (Pelota pel : pelotas) {
                if (pel.getMovX() == 0 && pel.getMovY() == 0) {
                    pel.setCoordX(evt.getX() - Pelota.BW / 2);
                    if (raqueta.getCoordX() <= 0) {
                        pel.setCoordX(Raqueta.RACKET_W / 2 - Pelota.BW / 2);
                    }
                    if (raqueta.getCoordX() + Raqueta.RACKET_W >= panelW) {
                        pel.setCoordX(panelW - Raqueta.RACKET_W / 2 - Pelota.BW / 2);
                    }
                } else if (pel.isDown()) {
                    if (pel.getCoordY() > (raqueta.getCoordY() + Raqueta.RACKET_H)) {
                        pelotas.remove(cont);
                        if (pelotas.isEmpty()) {
                            this.lifes--;
                        }
                        if (lifes >= 0 && pelotas.isEmpty()) {
                            pelotas.add(new Pelota(raqueta.getCoordX() + Raqueta.RACKET_W / 2 - Pelota.BW / 2, raqueta.getCoordY() - Raqueta.RACKET_H));
                        }
                        break;
                    }
                    if (((pel.getCoordX() + Pelota.BW) >= raqueta.getCoordX()) && (pel.getCoordX() <= raqueta.getCoordX() + Raqueta.RACKET_W) && ((pel.getCoordY() + Pelota.BH) >= raqueta.getCoordY()) && (pel.getCoordY() < (raqueta.getCoordY() + Raqueta.RACKET_H))) {
                        pel.setDown(false);
                        try {
                            pel.rebota(raqueta.getCoordX(), raqueta.getCoordY(), Raqueta.RACKET_W, Raqueta.RACKET_H, null);
                        } catch (JavaLayerException | IOException e) {
                        }
                    }
                }
                cont++;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (this.level == 0) {
            this.level++;
            jlPlayer.player.close();
            jlap.iniciar = false;
            inicializar = true;
        }
        if(inicializar && !mouseExited && this.lifes!=-1 && this.level <= this.numLevels){
            for (Pelota pel : pelotas) {
                if (pel.getMovX() == 0 && pel.getMovY() == 0) {
                    pel.setMovX(0.0);
                    pel.setMovY(-3.0);
                    if (this.startTime == 0) {
                        this.startTime = System.currentTimeMillis() + startTime;
                    }
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        if(mouseExited && this.lifes!=-1 && this.level <= this.numLevels){
            this.mouseExited = false;
            if (!pauseX.isEmpty()) {
                int cont = 0;
                for (@SuppressWarnings("unused") Object pausX : pauseX) {
                    pelotas.get(cont).setMovX(Float.parseFloat(pauseX.get(cont).toString()));
                    pelotas.get(cont).setMovY(Float.parseFloat(pauseY.get(cont).toString()));
                    cont++;
                }
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        if(!mouseExited && this.lifes!=-1 && this.level <= this.numLevels){
            this.mouseExited = true;
            pauseX.removeAll(pauseX);
            pauseY.removeAll(pauseY);
            for (Pelota pelota : pelotas) {
                pauseX.add(pelota.getMovX());
                pauseY.add(pelota.getMovY());
                pelota.setMovX(0);
                pelota.setMovY(0);
            }
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

    public void moverIzquierda() {
        raqueta.setCoordX(raqueta.getCoordX() - MOVEMENT_SPEED);
        if (raqueta.getCoordX() < 0) {
            raqueta.setCoordX(0);
        }
    }

    public void moverDerecha() {
        raqueta.setCoordX(raqueta.getCoordX() + MOVEMENT_SPEED);
        if (raqueta.getCoordX() + Raqueta.RACKET_W >= panelW) {
            raqueta.setCoordX(panelW - Raqueta.RACKET_W);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (this.level == 0) {
            this.level++;
            jlPlayer.player.close();
            jlap.iniciar = false;
            inicializar = true;
        }
        if(inicializar){
            if(mouseExited && this.lifes!=-1 && this.level <= this.numLevels){
                this.mouseExited = false;
                if (!pauseX.isEmpty()) {
                    int cont = 0;
                    for (@SuppressWarnings("unused") Object pausX : pauseX) {
                        pelotas.get(cont).setMovX(Float.parseFloat(pauseX.get(cont).toString()));
                        pelotas.get(cont).setMovY(Float.parseFloat(pauseY.get(cont).toString()));
                        cont++;
                    }
                }
            }
            if (key == KeyEvent.VK_ESCAPE && !mouseExited && this.lifes!=-1 && this.level <= this.numLevels) {
                this.mouseExited = true;
                pauseX.removeAll(pauseX);
                pauseY.removeAll(pauseY);
                for (Pelota pelota : pelotas) {
                    pauseX.add(pelota.getMovX());
                    pauseY.add(pelota.getMovY());
                    pelota.setMovX(0);
                    pelota.setMovY(0);
                }
            }
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT && !mouseExited && this.lifes!=-1 && this.level <= this.numLevels) {
                moverIzquierda = true;
                moverDerecha = false;
            }
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT && !mouseExited && this.lifes!=-1 && this.level <= this.numLevels) {
                moverDerecha = true;
                moverIzquierda = false;
            }
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP && !mouseExited && this.lifes!=-1 && this.level <= this.numLevels) {
                for (Pelota pel : pelotas) {
                    if (pel.getMovX() == 0 && pel.getMovY() == 0) {
                        pel.setMovX(0.0);
                        pel.setMovY(-3.0);
                        if (this.startTime == 0) {
                            this.startTime = System.currentTimeMillis() + startTime;
                        }
                    }
                }
            }
            if (key == KeyEvent.VK_R && !mouseExited && this.lifes!=-1 && this.level <= this.numLevels) {
                if(this.puntuacionreset >= 1000){
                    this.reset = true;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
            moverIzquierda = false;
        }
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
            moverDerecha = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void update() {
        if (moverIzquierda) {
            moverIzquierda();
        }
        if (moverDerecha) {
            moverDerecha();
        }
        for (Pelota pel : pelotas) {
            if (pel.getMovX() == 0 && pel.getMovY() == 0) {
                pel.setCoordX(raqueta.getCoordX() + Raqueta.RACKET_W / 2 - Pelota.BW / 2);
                if (raqueta.getCoordX() <= 0) {
                    pel.setCoordX(Raqueta.RACKET_W / 2 - Pelota.BW / 2);
                }
                if (raqueta.getCoordX() + Raqueta.RACKET_W >= panelW) {
                    pel.setCoordX(panelW - Raqueta.RACKET_W / 2 - Pelota.BW / 2);
                }
            }
        }
    }

    private void generarBloques() {
        int constBloques[][] = null;
        switch (level) {
            case 1 ->
                constBloques = new int[][]{
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0}
                };
            case 2 ->
                constBloques = new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 1, 1, 1, -1, 1, 1, 1, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0}
                };
            case 3 ->
                constBloques = new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 2, 0, 0, 0, 0},
                    {0, 0, 0, 1, 2, 1, 0, 0, 0},
                    {0, 0, 1, 1, 2, 1, 1, 0, 0},
                    {0, 3, 1, 1, -1, 1, 1, 3, 0},
                    {0, 0, 1, 1, 2, 1, 1, 0, 0},
                    {0, 0, 0, 1, 2, 1, 0, 0, 0},
                    {0, 0, 0, 0, 2, 0, 0, 0, 0}
                };
            case 4 ->
                constBloques = new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 2, 3, 4, -1, 4, 3, 2, 1},
                    {0, 1, 2, 3, 4, 3, 2, 1, 0},
                    {0, 0, 1, 2, 3, 2, 1, 0, 0},
                    {0, 0, 0, 1, 2, 1, 0, 0, 0},
                    {0, 0, 0, 0, -1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 2, 1, 0, 0, 0},
                    {0, 0, 1, 2, 3, 2, 1, 0, 0},
                    {0, 1, 2, 3, 4, 3, 2, 1, 0},
                    {1, 2, 3, 4, -1, 4, 3, 2, 1}
                };
            case 5 ->
                constBloques = new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 5, 5, 5, 0, 0, 0},
                    {0, 0, 5, 1, 1, 1, 5, 0, 0},
                    {0, 0, 5, 2, 2, 2, 5, 0, 0},
                    {0, 0, 5, 2, 2, 2, 5, 0, 0},
                    {0, 0, 5, 3, 3, 3, 5, 0, 0},
                    {0, 0, 5, 3, 3, 3, 5, 0, 0},
                    {0, 0, 5, 4, 4, 4, 5, 0, 0},
                    {0, 0, 5, -1, 5, -1, 5, 0, 0},
                    {0, 5, 4, 0, 4, 0, 4, 5, 0},
                    {0, 5, 4, 0, 4, 0, 4, 5, 0},
                    {0, 5, 4, 3, 4, 3, 4, 5, 0},
                    {0, 0, 5, 4, 4, 4, 5, 0, 0},
                    {0, 0, 0, -1, -1, -1, 0, 0, 0},
                    {0, 0, 0, 5, -1, 5, 0, 0, 0},
                    {0, 0, 0, 5, 5, 5, 0, 0, 0}
                };
            case 6 ->
                constBloques = new int[][]{
                    {0, 0, 0, 6, 6, 6, 0, 0, 0},
                    {0, 0, 6, 1, 1, 1, 6, 0, 0},
                    {0, 0, 6, 2, 2, 2, 6, 0, 0},
                    {0, 0, 6, 2, 2, 2, 6, 0, 0},
                    {0, 0, 6, 3, 3, 3, 6, 0, 0},
                    {0, 0, 6, 3, 3, 3, 6, 0, 0},
                    {0, 0, 6, 4, 4, 4, 6, 0, 0},
                    {0, 0, 6, 5, 5, 5, 6, 0, 0},
                    {0, 0, 6, 5, -1, 5, 6, 0, 0},
                    {0, 6, 5, 0, 5, 0, 5, 6, 0},
                    {0, 6, 5, 0, 5, 0, 5, 6, 0},
                    {0, 6, 5, 4, 5, 4, 5, 6, 0},
                    {0, 0, 6, 5, 5, 5, 6, 0, 0},
                    {0, 0, 0, -1, -1, -1, 0, 0, 0},
                    {0, 0, 0, 6, -1, 6, 0, 0, 0},
                    {0, 0, 0, 6, 6, 6, 0, 0, 0},
                    {0, 0, 0, 6, 6, 6, 0, 0, 0},
                    {0, 0, 0, 6, 6, 6, 0, 0, 0}
                };
            case 7 ->
                constBloques = new int[][]{
                    {0, 0, 0, 7, 7, 7, 0, 0, 0},
                    {0, 0, 6, 1, 1, 1, 6, 0, 0},
                    {0, 6, 1, 2, 2, 2, 1, 6, 0},
                    {0, 6, 2, 3, 3, 3, 2, 6, 0},
                    {0, 6, 3, 4, -1, 4, 3, 6, 0},
                    {0, 6, 4, 5, 5, 5, 4, 6, 0},
                    {0, 6, 5, 6, 6, 6, 5, 6, 0},
                    {0, 6, 7, 7, 7, 7, 7, 6, 0},
                    {0, 7, 5, -1, 5, -1, 5, 7, 0},
                    {0, 7, 5, -1, 5, -1, 5, 7, 0},
                    {0, 7, 5, -1, 5, -1, 5, 7, 0},
                    {0, 7, 5, 3, 5, 3, 5, 7, 0},
                    {0, 7, 6, 5, 5, 5, 6, 7, 0},
                    {0, 0, 7, 6, 5, 6, 7, 0, 0},
                    {-1, 0, 0, 7, 6, 7, 0, 0, -1},
                    {6, 6, 6, 6, 7, 6, 6, 6, 6},
                    {0, 0, 0, 7, 6, 7, 0, 0, 0},
                    {0, 0, 7, 0, 6, 0, 7, 0, 0},};
            default -> {
            }
        }
        if (constBloques != null) {
            this.ladrillos = new ArrayList<>();
            Ladrillo.Immortales = 0;
            for (int x = 0; x < constBloques.length; x++) {
                for (int y = 0; y < constBloques[x].length; y++) {
                    if (constBloques[x][y] != 0) {
                        this.ladrillos.add(new Ladrillo((Ladrillo.BlqWidth * y), (Ladrillo.BlqHeight * x) + Ladrillo.posYinicial, constBloques[x][y]));
                    }
                    if (constBloques[x][y] < 0) {
                        Ladrillo.Immortales++;
                    }
                }
            }
            random = new Random();
            randomPremios = new Random();
            int number = 0;
            int premio = 0;
            for (int x = 0; x < ladrillos.size() / 3; x++) {
                premio = Math.abs(randomPremios.nextInt() % 5) + 1;
                number = Math.abs(random.nextInt() % ladrillos.size());
                ladrillos.get(number).setPremio(premio);
            }
        }
    }

}