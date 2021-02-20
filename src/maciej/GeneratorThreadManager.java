package maciej;

public class GeneratorThreadManager {

    private GeneratorThread thread = null;

    public void startNewThread(SoundParameters parameters){
        if (thread == null){
            thread = new GeneratorThread(parameters);
            thread.start();
        }
    }

    public void stopAllThreads(){
        if (thread !=null){
            thread.stopThread();
            thread = null;
        }

    }
}
