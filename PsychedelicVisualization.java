import ddf.minim.AudioBuffer;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PImage;

public class PsychedelicVisualization extends PApplet {

    Minim minim;
    AudioPlayer player;
    AudioBuffer buffer;
    float lerpedHue = 0;
    float lerpedAvg = 0;
    float rotationAngle = 0;
    float noiseScale = 0.02f;
    float noiseStrength = 200;
    PImage texture;

    @Override
    public void settings() {
        size(800, 600, P3D);
    }

    @Override
    public void setup() {
        minim = new Minim(this);
        player = minim.loadFile("respect.mp3", 1024);
        player.play();
        buffer = player.mix;

        texture = createTexture();
    }

    @Override
    public void draw() {
        background(0);
        lights();
        colorMode(HSB);
        calculateAmplitude();
        drawPsychedelicShape();
    }

    void calculateAmplitude() {
        float totalAmplitude = 0;
        for (int i = 0; i < buffer.size(); i++) {
            totalAmplitude += abs(buffer.get(i));
        }
        lerpedAvg = lerp(lerpedAvg, totalAmplitude / buffer.size(), 0.1f);
        lerpedHue = map(lerpedAvg, 0, 1, 0, 255);
    }

    void drawPsychedelicShape() {
        float noiseVal = noise(rotationAngle * noiseScale) * noiseStrength;
        rotateX(rotationAngle);
        rotateY(rotationAngle);
        translate(width / 2, height / 2, 0);

        for (float i = 0; i < TWO_PI; i += 0.1) {
            float x = cos(i) * noiseVal;
            float y = sin(i) * noiseVal;
            fill(lerpedHue + x, 255, 255);
            ellipse(x, y, 20, 20);
        }
        rotationAngle += lerpedAvg * 0.01;
    }

    PImage createTexture() {
        PImage img = createImage(100, 100, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            float x = map(i % img.width, 0, img.width, -1, 1);
            float y = map(i / img.height, 0, img.height, -1, 1);
            float n = noise(x * 2, y * 2);
            img.pixels[i] = color(n * 255);
        }
        img.updatePixels();
        return img;
    }

    public static void main(String[] args) {
        PApplet.main("PsychedelicVisualization");
    }
}
