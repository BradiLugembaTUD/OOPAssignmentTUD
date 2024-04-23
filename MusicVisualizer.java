import ddf.minim.AudioBuffer;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PVector;

public class MusicVisualizer extends PApplet {

    Minim minim;
    AudioPlayer player;
    AudioBuffer buffer;
    float lerpedHue = 0;
    float lerpedAvg = 0;
    PVector fishPos, fishVelocity, fishAcceleration;

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
        textSize(16);
        textAlign(CENTER, CENTER);
        fishPos = new PVector(width / 2, height / 2);
        fishVelocity = new PVector(random(-1, 1), random(-1, 1));
        fishAcceleration = new PVector(0, 0);
        noCursor();
    }

    @Override
    public void draw() {
        background(0, 100, 200); // Darker blue for depth
        colorMode(HSB);
        float totalAmplitude = 0;
        for (int i = 0; i < buffer.size(); i++) {
            totalAmplitude += abs(buffer.get(i));
        }
        float avgAmplitude = totalAmplitude / buffer.size();
        lerpedAvg = lerp(lerpedAvg, avgAmplitude, 0.1f);
        lerpedHue = lerp(lerpedHue, map(avgAmplitude, 0, 1, 0, 255), 0.1f);

        updateFish();
        drawDetailedFish();
        drawSpeakers();
    }

    void updateFish() {
        fishAcceleration = PVector.random2D();
        fishAcceleration.mult(lerpedAvg * 5);
        fishVelocity.add(fishAcceleration);
        fishVelocity.limit(3);
        fishPos.add(fishVelocity);
        fishPos.x = (fishPos.x + width) % width;
        fishPos.y = (fishPos.y + height) % height;
    }

    void drawDetailedFish() {
        pushMatrix();
        translate(fishPos.x, fishPos.y);
        rotate(fishVelocity.heading() + PI / 2);
        noStroke();
        fill(lerpedHue, 255, 255, 127); // Corrected alpha value
        // Drawing a more detailed fish
        beginShape();
        vertex(-15, -10);
        bezierVertex(-30, -20, -30, 20, -15, 10);
        bezierVertex(-5, 5, -5, -5, -15, -10);
        endShape(CLOSE);
        popMatrix();
    }

    void drawSpeakers() {
        for (int i = 0; i < width; i += 60) {
            float x = i;
            fill(lerpedHue, 100, 150);
            noStroke();
            rect(x, height - 100, 10, 90);
        }
    }

    public static void main(String[] args) {
        PApplet.main("MusicVisualizer");
    }
}
