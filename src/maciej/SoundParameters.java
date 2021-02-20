package maciej;

import javax.sound.sampled.SourceDataLine;

public class SoundParameters {

    private SourceDataLine audioOutput;
    private double frequency;
    private double soundRate;
    private WaveShape waveShape;
    private short volume;

    public void setParameters(SourceDataLine audioOutput, int frequency, WaveShape shape, short volume){
        setAudioOutput(audioOutput);
        setFrequency(frequency);
        this.soundRate = 44100;
        setWaveShape(shape);
        setVolume(volume);
    }

    public short getVolume() {
        return volume;
    }

    public void setVolume(short volume) {
        this.volume = (short) ((volume/100.0) * (Short.MAX_VALUE-1));
    }

    public WaveShape getWaveShape() {
        return waveShape;
    }

    public void setWaveShape(WaveShape waveShape) {
        this.waveShape = waveShape;
    }

    public void setFrequency(double frequency){
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }

    public SourceDataLine getAudioOutput() {
        return audioOutput;
    }

    public void setAudioOutput(SourceDataLine audioOutput) {
        this.audioOutput = audioOutput;
    }

    public double getSoundRate() {
        return soundRate;
    }

    public void setSoundRate(double soundRate) {
        this.soundRate = soundRate;
    }
}
