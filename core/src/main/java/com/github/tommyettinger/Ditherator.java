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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Ditherator extends ApplicationAdapter {
    public static final boolean DEBUG = false;
    public String name;

    public Ditherator() {
    }
    public Ditherator(String name) {
        this.name = name;
    }

    @Override
    public void create() {
        long startTime = TimeUtils.millis();
        PNG8 png = new PNG8();
        Pixmap pixmap;
        FileHandle fh;
        if(Gdx.files.absolute(name).exists())
            fh = Gdx.files.absolute(name);
        else
            fh = Gdx.files.local(name);
        String baseName = fh.nameWithoutExtension();
        pixmap = new Pixmap(fh);
        Pixmap basis = new Pixmap(fh);
        ByteBuffer encoded = basis.getPixels();
        FileHandle dir = fh.sibling(baseName);
        dir.mkdirs();
        LinkedHashMap<String, PaletteReducer> palettes = new LinkedHashMap<>(16);
        palettes.put("BW", new PaletteReducer(new int[]{0, 255, -1}));
        palettes.put("GrayPink", new PaletteReducer(new int[]{0, 0xFF10D0FF, 0x182432FF}));
        palettes.put("OrangeBrown", new PaletteReducer(new int[]{0, 0x402838FF, 0xffb830FF}));
        palettes.put("BoldOrangeBrown", new PaletteReducer(new int[]{0, 0x402838FF, 0xFFA238FF}));
        palettes.put("PeachBrown", new PaletteReducer(new int[]{0, 0x402838FF, 0xECD297FF}));
        palettes.put("GrayGold", new PaletteReducer(new int[]{0, 0x3c3438FF, 0xf4c830FF}));
        palettes.put("BrownApricot", new PaletteReducer(new int[]{0, 0x402838FF, 0xf8f098FF}));
        for(Map.Entry<String, PaletteReducer> pal : palettes.entrySet()) {
            png.setPalette(pal.getValue());
            png.setFlipY(false);
            png.setDitherAlgorithm(Dithered.DitherAlgorithm.WREN);
            float[] strengths = {0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f};
            for (float strength : strengths) {
                png.setDitherStrength(strength);
                pixmap.setPixels(encoded);
                png.write(dir.child("out/" + baseName + "-" + pal.getKey() + "-" + png.getDitherAlgorithm().legibleName +
                        "-" + Math.round(strength * 100) + ".png"), pixmap, false, true, 100);
            }
        }
//		png.write(Gdx.files.local((DEBUG ? "out/" + name : name) + "/size" + exp + (smoothing ? "smooth/" : "blocky/") + name + "_angle" + i + ".png"), pixmap);
        System.out.println("Rendered to files in " + dir.path());
        System.out.println("Finished in " + TimeUtils.timeSinceMillis(startTime) * 0.001 + " seconds.");
        basis.dispose();
        pixmap.dispose();
        png.dispose();
        Gdx.app.exit();
        System.exit(0);
    }
}