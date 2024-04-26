import processing.core.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import java.util.ArrayList;
import java.util.Iterator;

public class RespectVisualizer extends PApplet {

    // Configuration variables
    // ------------------------
    int canvasWidth = 1080;
    int canvasHeight = 1000;

    String audioFileName = "data/respect.mp3"; // Audio file in data folder

    float fps = 30;
    float smoothingFactor = 0.25f; // FFT audio analysis smoothing factor
    // ------------------------

    // Global variables
    AudioPlayer track;
    FFT fft;
    Minim minim;

    // General
    int bands = 256; // must be multiple of two
    float[] spectrum = new float[bands];
    float[] sum = new float[bands];

    // Graphics
    float unit;
    int groundLineY;
    PVector center;

    // Particle system
    ParticleSystem particleSystem;
    boolean dynamicVisualization = true;
    boolean showParticles = true;
    boolean staticMode = false; // Flag for static visualization mode
    int colorModeToggle = 0; // Toggle for different color modes

    public void settings() {
        size(canvasWidth, canvasHeight);
        smooth(8);
    }

    public void setup() {
        frameRate(fps);

        // Graphics related variable setting
        unit = height / 100; // Everything else can be based around unit to make it change depending on size
        strokeWeight(unit / 10.24f);
        groundLineY = height * 3 / 4;
        center = new PVector(width / 2, height * 3 / 4);

        minim = new Minim(this);
        track = minim.loadFile(audioFileName, 2048);

        track.loop();

        fft = new FFT(track.bufferSize(), track.sampleRate());

        
        fft.linAverages(bands);

        // Initialize particle system
        particleSystem = new ParticleSystem();
    }

    int sphereRadius;
    float spherePrevX;
    float spherePrevY;
    int yOffset;
    boolean initialStatic = true;
    float[] extendingSphereLinesRadius;

    public void drawStatic() {
        if (initialStatic) {
            extendingSphereLinesRadius = new float[241];
            for (int angle = 0; angle <= 240; angle += 4) {
                extendingSphereLinesRadius[angle] = map(random(1), 0, 1, sphereRadius, sphereRadius * 7);
            }
            initialStatic = false;
        }

        // More extending lines
        for (int angle = 0; angle <= 240; angle += 4) {
            float x = round(cos(radians(angle + 150)) * sphereRadius + center.x);
            float y = round(sin(radians(angle + 150)) * sphereRadius + groundLineY - yOffset);
            float xDestination = x;
            float yDestination = y;

            // Draw lines in small increments to make it easier to work with
            for (int i = sphereRadius; i <= extendingSphereLinesRadius[angle]; i++) {
                float x2 = cos(radians(angle + 150)) * i + center.x;
                float y2 = sin(radians(angle + 150)) * i + groundLineY - yOffset;

                if (y2 <= getGroundY(x2)) { // Make sure it doesn't go into ground
                    xDestination = x2;
                    yDestination = y2;
                }
            }
            stroke(255,255,0);
            if (y <= getGroundY(x)) {
                line(x, y, xDestination, yDestination);
            }
        }
    }

    public void drawAll(float[] sum) {
        // Initialize extendingSphereLinesRadius
        float[] extendingSphereLinesRadius = new float[241];

        // Center sphere
        sphereRadius = 15 * round(unit);
        spherePrevX = 0;
        spherePrevY = 0;
        yOffset = round(sin(radians(150)) * sphereRadius);

        drawStatic();

        // Lines surrounding
        float x = 0;
        float y = 0;
        int surrCount = 1;
        boolean direction = false;

        while (x < width * 1.5 && x > 0 - width / 2) {
            float surroundingRadius;
            float surrRadMin = sphereRadius + sphereRadius * 1 / 2 * surrCount;
            float surrRadMax = surrRadMin + surrRadMin * 1 / 8;
            float surrYOffset;
            float addon = frameCount * 1.5f;

            if (direction) {
                addon = addon * 1.5f;
            }

            for (float angle = 0; angle <= 240; angle += 1.5) {
                surroundingRadius = map(sin(radians(angle * 7 + addon)), -1, 1, surrRadMin, surrRadMax);
                surrYOffset = sin(radians(150)) * surroundingRadius;

                x = round(cos(radians(angle + 150)) * surroundingRadius + center.x);
                y = round(sin(radians(angle + 150)) * surroundingRadius + getGroundY(x) - surrYOffset);

                stroke(100,50,80);
                //fill(map(surroundingRadius, surrRadMin, surrRadMax, 100, 255));
                fill(255,0,0);
                circle(x, y, 3 * unit / 10.24f);
                noFill();
            }

            direction = !direction;
            surrCount += 1;
        }

        // Lines extending from sphere
        float extendingLinesMin = sphereRadius * 1.3f;
        float extendingLinesMax = sphereRadius * 3.5f;

        float xDestination;
        float yDestination;

        for (int angle = 0; angle <= 240; angle++) {
            float extendingSpheresLinesRadius = map(noise(angle * 0.3f), 0, 1, extendingLinesMin, extendingLinesMax);

            if (sum[0] != 0) {
                if (angle >= 0 && angle <= 30) {
                    extendingSpheresLinesRadius = map(sum[240 - round(map((angle), 0, 30, 0, 80))], 0, 0.8f,
                            extendingSpheresLinesRadius - extendingSpheresLinesRadius / 8, extendingLinesMax * 1.5f);
                } else if (angle > 30 && angle <= 90) {
                    extendingSpheresLinesRadius = map(sum[160 - round(map((angle - 30), 0, 60, 0, 80))], 0, 3,
                            extendingSpheresLinesRadius - extendingSpheresLinesRadius / 8, extendingLinesMax * 1.5f);
                } else if (angle > 90 && angle <= 120) {
                    extendingSpheresLinesRadius = map(sum[80 - round(map((angle - 90), 0, 30, 65, 80))], 0, 40,
                            extendingSpheresLinesRadius - extendingSpheresLinesRadius / 8, extendingLinesMax * 1.5f);
                } else if (angle > 120 && angle <= 150) {
                    extendingSpheresLinesRadius = map(sum[0 + round(map((angle - 120), 0, 30, 0, 15))], 0, 40,
                            extendingSpheresLinesRadius - extendingSpheresLinesRadius / 8, extendingLinesMax * 1.5f);
                } else if (angle > 150 && angle <= 210) {
                    extendingSpheresLinesRadius = map(sum[80 + round(map((angle - 150), 0, 60, 0, 80))], 0, 3,
                            extendingSpheresLinesRadius - extendingSpheresLinesRadius / 8, extendingLinesMax * 1.5f);
                } else if (angle > 210) {
                    extendingSpheresLinesRadius = map(sum[160 + round(map((angle - 210), 0, 30, 0, 80))], 0, 0.8f,
                            extendingSpheresLinesRadius - extendingSpheresLinesRadius / 8, extendingLinesMax * 1.5f);
                }
            }

            x = round(cos(radians(angle + 150)) * sphereRadius + center.x);
            y = round(sin(radians(angle + 150)) * sphereRadius + groundLineY - yOffset);

            xDestination = x;
            yDestination = y;

            for (int i = sphereRadius; i <= extendingSpheresLinesRadius; i++) {
                int x2 = round(cos(radians(angle + 150)) * i + center.x);
                int y2 = round(sin(radians(angle + 150)) * i + groundLineY - yOffset);

                if (y2 <= getGroundY(x2)) { // Make sure it doesn't go into ground
                    xDestination = x2;
                    yDestination = y2;
                }
            }
            //stroke(map(extendingSpheresLinesRadius, extendingLinesMin, extendingLinesMax, 200, 255));

            stroke(255,255,255);

            if (y <= getGroundY(x)) {
                line(x, y, xDestination, yDestination);
            }
        }

        // Ground line
        for (int groundX = 0; groundX <= width; groundX++) {
            float groundY = getGroundY(groundX);
            noStroke();
            //fill(255);
            fill(255,220,0);
            circle(groundX, groundY, 1.8f * unit / 10.24f);
            noFill();
        }

        // Display particle system
        if (showParticles) {
            particleSystem.update();
            particleSystem.display();
        }
    }

    // Get the Y position at position X of ground sine wave
    public float getGroundY(float groundX) {
        float angle = 1.1f * groundX / unit * 10.24f;
        float groundY = sin(radians(angle + frameCount * 2)) * unit * 1.25f + groundLineY - unit * 1.25f;
        return groundY;
    }

    int lastColorChangeTime = 0; // Variable to store the last time color was changed
int colorChangeInterval = 2100; // Color change interval in milliseconds (3 seconds)

public void draw() {
    fft.forward(track.mix);
    spectrum = new float[bands];

    for (int i = 0; i < fft.avgSize(); i++) {
        spectrum[i] = fft.getAvg(i) / 2;
        // Smooth the FFT spectrum data by smoothing factor
        sum[i] += (abs(spectrum[i]) - sum[i]) * smoothingFactor;
    }

    // Check if it's time to change the canvas color
    if (millis() - lastColorChangeTime >= colorChangeInterval) {
        background(random(255), random(255), random(255)); // Change canvas color to random RGB
        lastColorChangeTime = millis(); // Update the last color change time
    }

    if (!staticMode) {
        drawAll(sum);
    } else {
        drawStatic();
    }
}

    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == UP) {
                fps += 100; // Increase frame rate
                frameRate(fps);
            } else if (keyCode == DOWN) {
                fps -= 100; // Decrease frame rate
                frameRate(fps);
            } else if (keyCode == LEFT) {
                particleSystem.changeDirection(-1); // Change particle system direction
            } else if (keyCode == RIGHT) {
                particleSystem.changeDirection(1); // Change particle system direction
            }
        } else if (key == ' ') {
            dynamicVisualization = !dynamicVisualization; // Toggle dynamic visualization
            showParticles = !showParticles; // Toggle particle visibility
            if (!dynamicVisualization) {
                noLoop(); // Stop animation
            } else {
                loop(); // Resume animation
            }
        } else if (key == 'S' || key == 's') {
            staticMode = true; // Switch to static visualization mode
        } else if (key == 'D' || key == 'd') {
            staticMode = false; // Switch to dynamic visualization mode
        } else if (key == 'C' || key == 'c') {
            // Toggle color modes
            colorModeToggle = (colorModeToggle + 1) % 3;
        }
    }

    public static void main(String[] args) {
        PApplet.main("RespectVisualizer");
    }

    class Particle {
        PVector position;
        PVector velocity;
        PVector acceleration;
        float lifespan;
        int particleColor;

        Particle(float x, float y, float vx, float vy) {
            position = new PVector(x, y);
            velocity = new PVector(vx, vy);
            acceleration = new PVector(0, 0.05f);
            lifespan = 255;
            particleColor = color(random(255), random(255), random(255));
        }

        void update() {
            velocity.add(acceleration);
            position.add(velocity);
            lifespan -= 2;
        }

        void display() {
            stroke(particleColor, lifespan);
            fill(particleColor, lifespan);
            ellipse(position.x, position.y, 8, 8);
        }

        boolean isDead() {
            return lifespan < 0;
        }
    }

    class ParticleSystem {
        ArrayList<Particle> particles;
        int direction;

        ParticleSystem() {
            particles = new ArrayList<>();
            direction = 1; // Default direction: right
        }

        void addParticle(float x, float y) {
            float vx = random(-1, 1) * direction;
            float vy = random(-2, -1);
            particles.add(new Particle(x, y, vx, vy));
        }

        void update() {
            Iterator<Particle> iterator = particles.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                p.update();
                if (p.isDead()) {
                    iterator.remove();
                }
            }
        }

        void display() {
            for (Particle p : particles) {
                p.display();
            }
        }

        void changeDirection(int dir) {
            direction = dir;
        }
    }
}
