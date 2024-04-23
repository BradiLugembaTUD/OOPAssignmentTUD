import processing.core.*;
import ddf.minim.*;
import ddf.minim.analysis.*;

public class RespectVisualizer extends PApplet 
{

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

            
            stroke(255);

            if (y <= getGroundY(x)) {
                line(x, y, xDestination, yDestination);
            }
        }
    } 
        
            
        
        public void drawAll(float[] sum) 
        {
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
    
                    surroundingRadius = map(sin(radians(angle * 7 + addon)), -1, 1, surrRadMin, surrRadMax); // Faster
                                                                                                            // rotation
                                                                                                            // through
                                                                                                            // angles,
                                                                                                            // radius
                                                                                                            // oscillates
    
                    surrYOffset = sin(radians(150)) * surroundingRadius;
    
                    x = round(cos(radians(angle + 150)) * surroundingRadius + center.x);
                    y = round(sin(radians(angle + 150)) * surroundingRadius + getGroundY(x) - surrYOffset);
    
                    noStroke();
                    fill(map(surroundingRadius, surrRadMin, surrRadMax, 100, 255));
                    circle(x, y, 3 * unit / 10.24f);
                    noFill();
                }
    
                direction = !direction;
    
                surrCount += 1;
            }
        }
}


    
        