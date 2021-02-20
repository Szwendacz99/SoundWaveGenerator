package maciej;

import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SoundOutputsManager {

    private final Map<Mixer.Info, SourceDataLine> outputs = new HashMap<>(AudioSystem.getMixerInfo().length);
    public SoundOutputsManager(){
        for(Mixer.Info info: AudioSystem.getMixerInfo()){
            outputs.put(info, null);

        }
    }

    public Set<Mixer.Info> getAllOutputsInfos(){
        return  outputs.keySet();
    }

    public SourceDataLine getOutputLine(Mixer.Info output) throws LineUnavailableException {
        SourceDataLine value = outputs.get(output);
        if (value == null){
            AudioFormat af = new AudioFormat((float) 44100, 16, 1, true, false);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af, output);
            sdl.open();
            sdl.start();

            outputs.put(output, sdl);
            return sdl;
        }else

        return value;
    }
}
