package reproductor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * Clase encargada de la reproduccion de sonidos
 * @author Grupo5DIU
 */
public class jlap {

    //--------------- Atributos ---------------

    /** Nombre del archivo */
    private String filename = "";

    /** Estado de repeticion del audio */
    @SuppressWarnings("FieldMayBeFinal")
    private boolean repetir = false;

    /** Hilo de ejecucion */
    private static Thread hilo; 

    /** Iniciar la reproduccion del audio */
    public static boolean iniciar = false;

    public AdvancedPlayer player = null;

    /**
     * Asignacion del nombre del archivo
     * @param file
     * @throws JavaLayerException
     * @throws IOException
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public jlap(String file) throws JavaLayerException, IOException{
        this.filename = file;
        play(false);
    }

    /**
     * Metodo encargado de la inicializacion del reproductor
     * @param inicial
     * @throws JavaLayerException
     * @throws IOException
     */
    public void play(boolean inicial) throws JavaLayerException, IOException {
        jlap.iniciar = false;
        InfoListener lst = new InfoListener();
        player = playMp3(new File(filename), lst);
    }

    /**
     * Metodo encargado de la inicializacion del reproductor
     * @throws JavaLayerException
     * @throws IOException
     */
    public void play() throws JavaLayerException, IOException {
        jlap.iniciar = true;
        InfoListener lst = new InfoListener();
        player = playMp3(new File(filename), lst);
    }

    /**
     * Metodo encargado de devolver valores avanzados para el reproductor
     * @param mp3
     * @param listener
     * @return
     * @throws IOException
     * @throws JavaLayerException
     */
    public static AdvancedPlayer playMp3(File mp3, PlaybackListener listener) throws IOException, JavaLayerException {
        return playMp3(mp3, 0, Integer.MAX_VALUE, listener);
    }

    /**
     * Metodo encargado de devolver valores avanzados para el reproductor
     * @param mp3
     * @param start
     * @param end
     * @param listener
     * @return
     * @throws IOException
     * @throws JavaLayerException
     */
    public static AdvancedPlayer playMp3(File mp3, int start, int end, PlaybackListener listener) throws IOException, JavaLayerException {
        return playMp3(new BufferedInputStream(new FileInputStream(mp3)), start, end, listener);
    }

    /**
     * Metodo encargado de la asignacion de hilos para el reproductor
     * @param is
     * @param start
     * @param end
     * @param listener
     * @return
     * @throws JavaLayerException
     */
    public static AdvancedPlayer playMp3(final InputStream is, final int start, final int end, PlaybackListener listener) throws JavaLayerException {
        final AdvancedPlayer player = new AdvancedPlayer(is);
        player.setPlayBackListener(listener);
        // run in new thread
        if(jlap.iniciar){
            hilo = new Thread() {
                @Override
                public void run() {
                    try {
                      player.play(start, end);
                    } catch (JavaLayerException e) {
                      throw new RuntimeException(e.getMessage());
                    }
                }
            };
            hilo.start();
        }
        return player;
    }

    /**
     * Clase encargada de la reproduccion repetitiva de sonidos
     * @author Grupo5DIU
     */
    public class InfoListener extends PlaybackListener {

        /**
         * No se usa en este programa
         * @param evt
         */
        @Override
        public void playbackStarted(PlaybackEvent evt) {

        }

        /**
         * Metodo encargado de la reproduccion del audio al finalizar la
         * anterior reproduccion
         * @param evt
         */
        @Override
        public void playbackFinished(PlaybackEvent evt) {
            if(repetir){
                hilo.start();
            }
        }
    }
}