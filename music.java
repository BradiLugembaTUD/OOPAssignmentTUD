import ddf.minim.AudioBuffer;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PVector;

public class music extends PApplet {

    Minim minim;
    AudioPlayer player;
    AudioBuffer buffer;
    float lerpedHue = 0;
    float lerpedAvg = 0;
    float rotationAngle = 0;
    float textSizeBase = 50; // Base size of the text
    float textScaleFactor = 10; // Scale factor for text size
    float fishSpeed = 1; // Speed of the fish
    PVector fishPos; // Position of the fish
    PVector fishVelocity; // Velocity of the fish
    PVector fishAcceleration; // Acceleration of the fish
    boolean speakersVisible = true;

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

        fishPos = new PVector(width / 2, height / 2);
        fishVelocity = new PVector(random(-1, 1), random(-1, 1));
        fishAcceleration = new PVector(0, 0);
    }

    @Override
    public void draw() {
        background(0, 128, 255); // Blue background representing the bottom of the sea
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

        // Update rotation angle based on the beat
        float rotationSpeed = map(lerpedAvg, 0, 1, 0.01f, 0.1f);
        rotationAngle += rotationSpeed;

        // Change color and size of the fish based on the beat
        float fishHue = map(lerpedAvg, 0, 1, 0, 360);
        lerpedHue = lerp(lerpedHue, fishHue, 0.1f);
        fill(lerpedHue, 255, 255);
        drawFish();

        // Draw speakers underwater
        if (speakersVisible) {
            drawSpeakers();
        }

        // Update fish movement
        fishAcceleration = PVector.random2D();
        fishAcceleration.mult(lerpedAvg * 0.01f); // Fish speed based on amplitude
        fishVelocity.add(fishAcceleration);
        fishVelocity.limit(2); // Limit fish speed
        fishPos.add(fishVelocity);
        checkEdges(); // Keep fish within the screen boundaries
    }

    // Draw the fish
    void drawFish() {
        pushMatrix();
        translate(fishPos.x, fishPos.y);
        rotate(fishVelocity.heading() + PI / 2);
        // Fish body
        fill(lerpedHue, 255, 255);
        ellipse(0, 0, 40, 60);
        // Fish tail
        fill(lerpedHue, 255, 255);
        triangle(-20, 30, 0, 50, 20, 30);
        popMatrix();
    }

    // Draw speakers underwater
    void drawSpeakers() {
        float speakerHeight = height * 0.8f;
        float speakerWidth = 20;
        float speakerGap = 80;
        for (float x = speakerGap / 2; x < width; x += speakerGap) {
            float speakerX = x;
            float speakerY = height - speakerHeight / 2;
            fill(lerpedHue, 255, 255);
            rect(speakerX, speakerY, speakerWidth, speakerHeight);
        }
    }

    // Check if the fish is out of screen boundaries
    void checkEdges() {
        if (fishPos.x > width) {
            fishPos.x = 0;
        } else if (fishPos.x < 0) {
            fishPos.x = width;
        }
        if (fishPos.y > height) {
            fishPos.y = 0;
        } else if (fishPos.y < 0) {
            fishPos.y = height;
        }
    }

    public static void main(String[] args) {
        PApplet.main("music");
    }
}
