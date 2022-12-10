package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.Dithered;
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
        Pixmap basis = new Pixmap(fh), noise = new Pixmap(Gdx.files.internal("blue1024_0.png"));
        System.out.println(noise.getFormat());
        final int h = basis.getHeight(), w = basis.getWidth();
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        ByteBuffer encoded = basis.getPixels();
        ByteBuffer o = pixmap.getPixels();
        IntBuffer out = o.asIntBuffer();
        byte[] bn = new byte[1 << 20];
        noise.getPixels().get(bn);
        FileHandle dir = fh.sibling(baseName);
        dir.mkdirs();
        PaletteReducer bw = new PaletteReducer(new int[]{0, -1, 255});
        png.setPalette(bw);
        png.setFlipY(false);
//        byte[] bn = PaletteReducer.TRI_BLUE_NOISE;
        final int MASK = bn.length - 1;
        for (int y = 0, idx = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int red = encoded.get() & 255, green = encoded.get() & 255, blue = encoded.get() & 255;
                if(basis.getFormat() == Pixmap.Format.RGBA8888)
                    encoded.get(); // alpha
                if(red * 0.2126 + green * 0.7152 + blue * 0.0722 < (bn[idx++ & MASK] + 128))
//                if(red * 0.2126 + green * 0.7152 + blue * 0.0722 < (bn[idx++ & MASK] + 128) / 255.0)
                    out.put(255);
                else
                    out.put(-1);
            }
        }
        pixmap.setPixels(o);
//        float[] strengths = {0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f};
//        for(float strength : strengths) {
//            png.setDitherStrength(strength);
//            pixmap.setPixels(encoded);
//            png.write(dir.child(baseName + "-BW-" + Math.round(strength * 100) + ".png"), pixmap, false, true, 100);
//        }
        png.writePrecisely(dir.child(baseName + "-BW-halftone.png"), pixmap, new int[]{0, -1, 255}, false, 100);
        System.out.println("Rendered to files in " + dir.path());
        System.out.println("Finished in " + TimeUtils.timeSinceMillis(startTime) * 0.001 + " seconds.");
        basis.dispose();
        pixmap.dispose();
        png.dispose();
        Gdx.app.exit();
        System.exit(0);
    }
}