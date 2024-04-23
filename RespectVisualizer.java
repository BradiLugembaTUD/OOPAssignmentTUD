import processing.core.*;
import ddf.minim.*;
import ddf.minim.analysis.*;

public class RespectVisualizer extends PApplet {

    // Configuration variables
    // ------------------------
    int canvasWidth = 1080;
    int canvasHeight = 1080;

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
    }
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
    