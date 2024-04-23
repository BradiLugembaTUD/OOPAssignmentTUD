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
    PVector[] fishPositions;
    PVector[] fishVelocities;

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

        fishPositions = new PVector[20]; // Adjust the number of fish
        fishVelocities = new PVector[20];

        for (int i = 0; i < fishPositions.length; i++) {
            fishPositions[i] = new PVector(random(width), random(height));
            fishVelocities[i] = PVector.random2D().mult(random(1, 3));
        }

        noCursor();
    }

    @Override
    public void draw() {
        background(50, 100, 200); // Muted blue background

        float totalAmplitude = 0;
        for (int i = 0; i < buffer.size(); i++) {
            totalAmplitude += abs(buffer.get(i));
        }
        float avgAmplitude = totalAmplitude / buffer.size();
        lerpedAvg = lerp(lerpedAvg, avgAmplitude, 0.1f);
        lerpedHue = lerp(lerpedHue, map(avgAmplitude, 0, 1, 0, 255), 0.1f);

        for (int i = 0; i < fishPositions.length; i++) {
            updateFish(i);
            drawFish(fishPositions[i], fishVelocities[i]);
        }

        drawDecorations();
    }

    void updateFish(int index) {
        fishVelocities[index].add(PVector.random2D().mult(lerpedAvg * 0.1f));
        fishVelocities[index].limit(3);
        fishPositions[index].add(fishVelocities[index]);

        if (fishPositions[index].x > width) {
            fishPositions[index].x = 0;
        } else if (fishPositions[index].x < 0) {
            fishPositions[index].x = width;
        }
        if (fishPositions[index].y > height) {
            fishPositions[index].y = 0;
        } else if (fishPositions[index].y < 0) {
            fishPositions[index].y = height;
        }
    }

    void drawFish(PVector pos, PVector vel) {
        pushMatrix();
        translate(pos.x, pos.y);
        rotate(vel.heading() + PI / 2);
        fill(lerpedHue + 30, 255, 255, 127); // Warmer tone for fish color
        beginShape();
        vertex(0, -20);
        bezierVertex(-10, -30, -10, -10, 0, -5);
        bezierVertex(10, -10, 10, -30, 0, -20);
        endShape(CLOSE);
        popMatrix();
    }

    void drawDecorations() {
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
