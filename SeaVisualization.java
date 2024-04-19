import ddf.minim.AudioBuffer;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PVector;

public class SeaVisualization extends PApplet {

    Minim minim;
    AudioPlayer player;
    AudioBuffer buffer;
    float lerpedHue = 0;
    float lerpedAvg = 0;
    float rotationAngle = 0;
    PVector fishPos;
    PVector fishVelocity;
    PVector fishAcceleration;
    boolean wavesVisible = true;
    float seaDepth = -500;

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

        fishPos = new PVector(width / 2, height / 2, seaDepth);
        fishVelocity = PVector.random3D();
        fishVelocity.mult(2);
        fishAcceleration = new PVector(0, 0, 0);
    }

    @Override
    public void draw() {
        background(0, 128, 255); // Sea background
        lights();
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

        // Update fish movement
        fishAcceleration = PVector.random3D();
        fishAcceleration.mult(lerpedAvg * 0.1f); // Fish speed based on amplitude
        fishVelocity.add(fishAcceleration);
        fishVelocity.limit(4); // Limit fish speed
        fishPos.add(fishVelocity);
        checkEdges(); // Keep fish within the screen boundaries

        // Change color of the fish based on the song
        float fishHue = map(lerpedAvg, 0, 1, 0, 360);
        lerpedHue = lerp(lerpedHue, fishHue, 0.1f);
        fill(lerpedHue, 255, 255);
        drawFish();

        // Draw sine waves representing sea waves
        if (wavesVisible) {
            drawSeaWaves();
        }

        // Draw speakers represented as sine waves
        drawSpeakers();
    }

    // Draw the fish
    void drawFish() {
        pushMatrix();
        translate(fishPos.x, fishPos.y, fishPos.z);
        rotateY(fishVelocity.heading() + PI / 2);
        rotateZ(fishVelocity.heading() + PI / 2);
        fill(lerpedHue, 255, 255);
        beginShape();
        vertex(-50, -25, 0);
        vertex(50, -25, 0);
        vertex(50, 25, 0);
        vertex(-50, 25, 0);
        endShape(CLOSE);
        popMatrix();
    }

    // Draw sine waves representing sea waves
    void drawSeaWaves() {
        float waveAmplitude = lerpedAvg * 100; // Amplitude of sine waves based on song amplitude
        float waveFrequency = 0.01f; // Frequency of sine waves
        float waveColorOffset = millis() * 0.01f; // Color offset for vibrant effect

        for (float y = 0; y < height; y += 20) {
            float waveOffset = y * waveFrequency;
            float waveColor = (lerpedHue + y / (float) height * 360 + waveColorOffset) % 360;
            stroke(waveColor, 255, 255);
            float waveXStart = 0;
            float waveXEnd = width;
            float waveY = y + sin(waveOffset + rotationAngle) * waveAmplitude;
            line(waveXStart, y, seaDepth, waveXEnd, waveY, seaDepth);
        }
    }

    // Draw speakers represented as sine waves
    void drawSpeakers() {
        float speakerAmplitude = lerpedAvg * 100; // Amplitude of speakers based on song amplitude
        float speakerFrequency = 0.01f; // Frequency of speakers
        float speakerColorOffset = millis() * 0.01f; // Color offset for vibrant effect

        for (float x = 0; x < width; x += 50) {
            float speakerOffset = x * speakerFrequency;
            float speakerColor = (lerpedHue + x / (float) width * 360 + speakerColorOffset) % 360;
            stroke(speakerColor, 255, 255);
            float speakerYStart = height;
            float speakerYEnd = height - speakerAmplitude;
            line(x, speakerYStart, seaDepth, x, speakerYEnd, seaDepth);
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
        if (fishPos.z > 0) {
            fishPos.z = seaDepth;
        } else if (fishPos.z < seaDepth) {
            fishPos.z = 0;
        }
    }

    public static void main(String[] args) {
        PApplet.main("SeaVisualization");
    }
}
