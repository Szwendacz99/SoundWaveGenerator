package maciej;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.SourceDataLine;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class GeneratorThread extends Thread{

    private final CountDownLatch latch = new CountDownLatch(1);
    static final Logger logger = LogManager.getLogger(GeneratorThread.class);
    private SoundParameters soundParameters;
    private boolean shouldItRun = true;
    private Random random = new Random();

    public GeneratorThread(SoundParameters parameters){
        this.soundParameters = parameters;
    }

    byte[] buf = new byte[2];

    public void run(){
        logger.info("Playing sound...");

        SourceDataLine sdl = soundParameters.getAudioOutput();
        double i = 0;
        long timer = System.currentTimeMillis();
        double counter = 0;

        while (shouldItRun) {
            if ((System.currentTimeMillis() - timer) > 1000)
            {
                timer = System.currentTimeMillis();
                logger.info((i - counter)+ " buffs per second");
                counter = i;
            }
            switch (soundParameters.getWaveShape()) {
                case SIN -> sinWave(i);
                case SQUARE -> squareWave(i);
                case TRIANGULAR -> triangularWave(i);
                case SAWTOOTH -> sawtoothWave(i);
                case WHITE_NOISE -> whiteNoise();
                default -> throw new IllegalStateException("Unexpected value: " + soundParameters.getWaveShape());
            }
            sdl.write(buf, 0, 2);
            i++;

        }
        sdl.flush();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Thread playing sound have stopped");
    }

    private void sinWave(double i){
        double numberOfSamplesToRepresentFullSin =  soundParameters.getSoundRate() / soundParameters.getFrequency();
        double angle = i / (numberOfSamplesToRepresentFullSin/ 2.0) * Math.PI;
        short a = (short) (Math.sin(angle) * soundParameters.getVolume());
        buf[0] = (byte) (a & 0xFF);
        buf[1] = (byte) (a >> 8);
    }
    private void squareWave(double i){
        double numberOfSamplesToRepresentFullSin = soundParameters.getSoundRate() / soundParameters.getFrequency();
        double angle = i / (numberOfSamplesToRepresentFullSin/ 2.0) * Math.PI;
        short a = (short) ((Math.sin(angle) > 0) ? soundParameters.getVolume() : -soundParameters.getVolume());
        buf[0] = (byte) (a & 0xFF);
        buf[1] = (byte) (a >> 8);
    }

    private void triangularWave(double i){
        double numberOfSamplesToRepresentFullSin = soundParameters.getSoundRate() / soundParameters.getFrequency();
        double angle = i / (numberOfSamplesToRepresentFullSin / 2.0) * Math.PI;
        short a = (short) (Math.asin(Math.sin(angle)) * soundParameters.getVolume());

        buf[0] = (byte) (a & 0xFF);
        buf[1] = (byte) (a >> 8);
    }

    private void sawtoothWave(double i){
        double numberOfSamplesToRepresentFullSin = soundParameters.getSoundRate() / soundParameters.getFrequency();
        double angle = i / (numberOfSamplesToRepresentFullSin);
        short a = (short) (angle%1 * soundParameters.getVolume() * 2 - soundParameters.getVolume());
        buf[0] = (byte) (a & 0xFF);
        buf[1] = (byte) (a >> 8);
    }

    private void whiteNoise(){
        short a = (short) (random.nextGaussian() * soundParameters.getVolume());
        buf[0] = (byte) (a & 0xFF);
        buf[1] = (byte) (a >> 8);
    }

    public void stopThread(){
        shouldItRun = false;
        latch.countDown();
    }

}
