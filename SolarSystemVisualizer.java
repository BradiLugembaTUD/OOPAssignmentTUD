import ddf.minim.AudioBuffer;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PVector;

public class SolarSystemVisualizer extends PApplet {

    Minim minim;
    AudioPlayer player;
    AudioBuffer buffer;
    float lerpedAvg = 0;

    Planet[] planets;
    int numPlanets = 5;

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

        planets = new Planet[numPlanets];
        for (int i = 0; i < numPlanets; i++) {
            float radius = random(100, 300); // Orbit radius
            float speed = random(0.01, 0.05); // Orbit speed
            float size = random(10, 30); // Planet size
            color col = color(random(50, 255), random(50, 255), random(50, 255)); // Planet color
            float angle = random(TWO_PI); // Initial angle
            planets[i] = new Planet(radius, speed, size, col, angle);
        }
    }

    @Override
    public void draw() {
        background(0);

        lerpedAvg = lerp(lerpedAvg, getAverageAmplitude(), 0.1f);

        for (Planet p : planets) {
            p.update();
            p.display();
        }
    }

    float getAverageAmplitude() {
        float totalAmplitude = 0;
        for (int i = 0; i < buffer.size(); i++) {
            totalAmplitude += abs(buffer.get(i));
        }
        return totalAmplitude / buffer.size();
    }

    class Planet {
        float radius;
        float angle;
        float speed;
        float size;
        color col;

        Planet(float r, float s, float sz, color c, float a) {
            radius = r;
            speed = s;
            size = sz;
            col = c;
            angle = a;
        }

        void update() {
            angle += speed * lerpedAvg; // Adjust speed based on music intensity
        }

        void display() {
            float x = width / 2 + cos(angle) * radius;
            float y = height / 2 + sin(angle) * radius;
            fill(col, 150 + lerpedAvg * 100); // Adjust transparency based on music intensity
            ellipse(x, y, size, size);
        }
    }

    public static void main(String[] args) {
        PApplet.main("SolarSystemVisualizer");
    }
}
