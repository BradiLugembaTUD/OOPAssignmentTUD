import ddf.minim.AudioBuffer;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;

public class music extends PApplet {

    Minim minim;
    AudioPlayer player;
    AudioBuffer buffer;

    float lerpedAvg = 0;

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

        // Calculate the average amplitude of the audio buffer
        float totalAmplitude = 0;
        for (int i = 0; i < buffer.size(); i++) {
            totalAmplitude += abs(buffer.get(i));
        }
        float avgAmplitude = totalAmplitude / buffer.size();

        // Smoothly interpolate the average amplitude
        lerpedAvg = lerp(lerpedAvg, avgAmplitude, 0.1f);

        // Visualize the average amplitude
        float circleSize = lerpedAvg * 500; // Adjust multiplier for scaling
        float circleX = width / 2;
        float circleY = height / 2;
        noStroke();
        fill(255, 0, 0); // Colout
        ellipse(circleX, circleY, circleSize, circleSize);



    }

}
