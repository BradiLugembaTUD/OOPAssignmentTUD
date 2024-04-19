import ddf.minim.AudioBuffer;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;

public class music extends PApplet {

    Minim minim;
    AudioPlayer player;
    AudioBuffer buffer;
    float lerpedHue = 0;
    float lerpedAvg = 0;
    float waveScale = 5; // Scaling factor for wave size

    @Override
    public void settings() {
        size(800, 600);
    }

    @Override
    public void setup() {
        minim = new Minim(this);
        player = minim.loadFile("respect.mp3", 1024);
        player.play();
        buffer = player.mix;
    }

    @Override
    public void draw() {
        background(0);
        colorMode(HSB);
        strokeWeight(2);

        // Calculate the average amplitude of the audio buffer
        float totalAmplitude = 0;
        for (int i = 0; i < buffer.size(); i++) {
            totalAmplitude += abs(buffer.get(i));
        }
        float avgAmplitude = totalAmplitude / buffer.size();

        // Smoothly interpolate the average amplitude
        lerpedAvg = lerp(lerpedAvg, avgAmplitude, 0.1f);

        // Visualize the average amplitude with a sine wave
        float h = height / 2;
        float waveHeight = lerpedAvg * 200; // Adjust multiplier for wave amplitude
        float waveFrequency = 0.02f; // Adjust frequency for wider or narrower wave

        lerpedHue += 0.1; // Hue increment for color change

        for (float x = 0; x < width; x += 2) {
            float y = h + sin(x * waveFrequency) * waveHeight * waveScale; // Apply scaling factor
            float hue = (lerpedHue + x) % 360;
            stroke(hue, 255, 255);
            point(x, y);
        }

    }

    public static void main(String[] args){
        PApplet.main("music");
    }
}
