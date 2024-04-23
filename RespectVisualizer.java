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
}