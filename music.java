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

        float shapeSize = lerpedAvg * 300;
        float shapeCount = lerpedAvg * 20;
        for (int i = 0; i < shapeCount; i++) {
            float angle = map(i, 0, shapeCount, 0, TWO_PI);
            float x = width / 2 + cos(angle + rotationAngle) * shapeSize;
            float y = height / 2 + sin(angle + rotationAngle) * shapeSize;
            float shapeHue = (lerpedHue + i * 10) % 360;
            fill(shapeHue, 255, 255);
            stroke(shapeHue, 255, 255);
            float shapeType = i % 4;
            switch ((int) shapeType) {
                case 0:
                    ellipse(x, y, shapeSize, shapeSize);
                    break;
                case 1:
                    rectMode(CENTER);
                    rect(x, y, shapeSize, shapeSize);
                    break;
                case 2:
                    triangle(x - shapeSize / 2, y + shapeSize / 2, x + shapeSize / 2, y + shapeSize / 2, x,
                            y - shapeSize / 2);
                    break;
                case 3:
                    float pentagonRadius = shapeSize / 2 / cos(PI / 5);
                    beginShape();
                    for (int j = 0; j < 5; j++) {
                        float theta = TWO_PI / 5 * j;
                        float px = x + cos(theta) * pentagonRadius;
                        float py = y + sin(theta) * pentagonRadius;
                        vertex(px, py);
                    }
                    endShape(CLOSE);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("music");
    }
}
