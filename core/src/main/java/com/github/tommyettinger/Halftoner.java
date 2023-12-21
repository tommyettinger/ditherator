package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.PNG8;
import com.github.tommyettinger.anim8.PaletteReducer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Halftoner extends ApplicationAdapter {
    public static final boolean DEBUG = false;
    public String name;

    public Halftoner() {
    }
    public Halftoner(String name) {
        this.name = name;
    }

    public static final double HPI = Math.PI * 0.125;
    @Override
    public void create() {
        long startTime = TimeUtils.millis();
        PNG8 png = new PNG8();
        FileHandle fh;
        if(Gdx.files.absolute(name).exists())
            fh = Gdx.files.absolute(name);
        else
            fh = Gdx.files.local(name);
        String baseName = fh.nameWithoutExtension();
        Pixmap basis = new Pixmap(fh), noise = new Pixmap(Gdx.files.local("BlueNoise.png"));
//        Pixmap basis = new Pixmap(fh), noise = new Pixmap(Gdx.files.internal("blue1024_0.png"));
        final int h = basis.getHeight(), w = basis.getWidth();
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        ByteBuffer encoded = basis.getPixels();
        ByteBuffer o = pixmap.getPixels();
        IntBuffer out = o.asIntBuffer();
        byte[] bn = new byte[noise.getWidth() * noise.getHeight()];
        noise.getPixels().get(bn);
//        byte[] bn = PaletteReducer.TRI_BLUE_NOISE; // Small 64x64 tiles, with noticeable tiling. Triangular.

        FileHandle dir = fh.sibling(baseName);
        dir.mkdirs();
        PaletteReducer bw = new PaletteReducer(new int[]{0, -1, 255});
        png.setPalette(bw);
        png.setFlipY(false);
        final int MASK = bn.length - 1;
        double[] strengths = {0.25, 0.5, 0.75, 1.0, 1.25, 1.5};
        for(double strength : strengths) {
            encoded.position(0);
            out.position(0);
            o.position(0);
            for (int y = 0, idx = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int red = encoded.get() & 255, green = encoded.get() & 255, blue = encoded.get() & 255;
                    if(basis.getFormat() == Pixmap.Format.RGBA8888)
                        encoded.get(); // alpha is ignored for now
                    if(red * 0.2126 + green * 0.7152 + blue * 0.0722 <= // https://en.wikipedia.org/wiki/Rec._709 lightness
                            // gets an unsigned byte, moves it to the -127.5 to 127.5 range, multiplies by strength, moves back up
                            ((bn[idx++ & MASK] & 255) - 127.5) * strength + 127.5 + (Math.cos(x * HPI) * Math.sin(y * HPI)) * 48)
//                    if(red * 0.2126 + green * 0.7152 + blue * 0.0722 <= // https://en.wikipedia.org/wiki/Rec._709 lightness
//                            Math.sin(x * HPI) * Math.cos(y * HPI) * 150 * strength + 127.5) //((bn[idx++ & MASK] & 255) - 127.5) * 0.1
                        out.put(255);
                    else
                        out.put(-1);
                }
            }
            pixmap.setPixels(o);
//            png.writePrecisely(dir.child(baseName + "-BW-" + Math.round(strength * 100) + "-halftone.png"), pixmap, new int[]{0, -1, 255}, false, 100);
            png.writePrecisely(dir.child(baseName + "-BW-" + Math.round(strength * 100) + "-bn-halftone.png"), pixmap, new int[]{0, -1, 255}, false, 100);
        }
        System.out.println("Rendered to files in " + dir.path());
        System.out.println("Finished in " + TimeUtils.timeSinceMillis(startTime) * 0.001 + " seconds.");
        basis.dispose();
        pixmap.dispose();
        png.dispose();
        Gdx.app.exit();
        System.exit(0);
    }
}