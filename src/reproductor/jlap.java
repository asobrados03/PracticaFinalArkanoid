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

public class jlap
{
	private String filename = "";
        @SuppressWarnings("FieldMayBeFinal")
	private boolean repetir = false;
	private static Thread hilo; 
	public static boolean iniciar = false;
	public AdvancedPlayer player = null;

        @SuppressWarnings("OverridableMethodCallInConstructor")
	public jlap(String file) throws JavaLayerException, IOException{
		this.filename = file;
		play(false);
	}

	public void play(boolean inicial) throws JavaLayerException, IOException
	{
		jlap.iniciar = false;
		InfoListener lst = new InfoListener();
		player = playMp3(new File(filename), lst);
	}
	
	public void play() throws JavaLayerException, IOException
	{
		jlap.iniciar = true;
		InfoListener lst = new InfoListener();
		player = playMp3(new File(filename), lst);
	}

  public static AdvancedPlayer playMp3(File mp3, PlaybackListener listener) throws IOException, JavaLayerException
  {
    return playMp3(mp3, 0, Integer.MAX_VALUE, listener);
  }

  public static AdvancedPlayer playMp3(File mp3, int start, int end, PlaybackListener listener) throws IOException, JavaLayerException
  {
    return playMp3(new BufferedInputStream(new FileInputStream(mp3)), start, end, listener);
  }

  public static AdvancedPlayer playMp3(final InputStream is, final int start, final int end, PlaybackListener listener) throws JavaLayerException
  {
    final AdvancedPlayer player = new AdvancedPlayer(is);
    player.setPlayBackListener(listener);
    // run in new thread
    if(jlap.iniciar){
	    hilo = new Thread()
	    {
              @Override
	      public void run()
	      {
	        try
	        {
	          player.play(start, end);
	        }
	        catch (JavaLayerException e)
	        {
	          throw new RuntimeException(e.getMessage());
	        }
	      }
	    };
	    hilo.start();
    }
    return player;
  }

  public class InfoListener extends PlaybackListener
  {

            /**
             *
             * @param evt
             */
            @Override
    public void playbackStarted(PlaybackEvent evt)
    {
    }

            /**
             *
             * @param evt
             */
            @Override
    public void playbackFinished(PlaybackEvent evt)
    {
    	if(repetir){
    		hilo.start();
    	}
    }
  }
}