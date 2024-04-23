import ddf.minim.AudioBuffer;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;

public class music extends PApplet {

    Minim minim;
    AudioPlayer player;
    AudioBuffer buffer;
    float lerpedHue = 0;
    float lerpedAvg = 0;
    float rotationAngle = 0;
    float textSizeBase = 50;
    float textScaleFactor = 10;
    float waveAmplitude = 100;
    float waveFrequency = 0.02f;

    @Override
    public void settings() {
        size(800, 600);
    }

    @Override
    public void setup() {
        minim = new Minim(this);
        player = minim.loadFile("data/respect.mp3", 1024);
        player.play();
        buffer = player.mix;
        textSize(textSizeBase);
        textAlign(CENTER, CENTER);
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

        lerpedAvg = lerp(lerpedAvg, avgAmplitude, 0.1f);

        // Updates rotation angle based on the beat
        float rotationSpeed = map(lerpedAvg, 0, 1, 0.01f, 0.1f);
        rotationAngle += rotationSpeed;

        // Changes color and size of the text based on the beat
        float textHue = map(lerpedAvg, 0, 1, 0, 360);
        lerpedHue = lerp(lerpedHue, textHue, 0.1f);
        float textSizeFactor = map(lerpedAvg, 0, 1, 0.5f, 2);
        textSize(textSizeBase * textSizeFactor);
        fill(lerpedHue, 255, 255);
        text("RESPECT", width / 4, height / 7);

        // sine waves that represent sea waves
        for (float y = 0; y < height; y += 10) {
            float waveOffset = y * waveFrequency + millis() * 0.01f;
            float waveColor = (lerpedHue + y / height * 360) % 360;
            stroke(waveColor, 255, 255);
            float waveAmplitudeFactor = lerpedAvg * 20;
            float waveXStart = 0;
            float waveXEnd = width;
            float waveY = y + sin(waveOffset) * waveAmplitude * waveAmplitudeFactor;
            line(waveXStart, y, waveXEnd, waveY);
        }
    }

    public static void main(String[] args) {
        PApplet.main("music");
    }
}



