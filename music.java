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
    float rotationAngle = 0;

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

        float totalAmplitude = 0;
        for (int i = 0; i < buffer.size(); i++) {
            totalAmplitude += abs(buffer.get(i));
        }
        float avgAmplitude = totalAmplitude / buffer.size();

        lerpedAvg = lerp(lerpedAvg, avgAmplitude, 0.1f);

        float rotationSpeed = map(lerpedAvg, 0, 1, 0.01f, 0.1f);
        rotationAngle += rotationSpeed;

        float hue = map(lerpedAvg, 0, 1, 0, 360);
        lerpedHue = lerp(lerpedHue, hue, 0.1f);
        fill(lerpedHue, 255, 255);
        stroke(lerpedHue, 255, 255);

        float shapeSize = lerpedAvg * 300;
        translate(width / 2, height / 2);
        rotate(rotationAngle);
        beginShape();

        for (int i = 0; i < 6; i++) {
            float angle = TWO_PI / 6 * i;
            float x = cos(angle) * shapeSize;
            float y = sin(angle) * shapeSize;
            vertex(x, y);
        }
        endShape(CLOSE);

    }

    public static void main(String[] args) {
        PApplet.main("music");
    }

}
