package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.Dithered;
import com.github.tommyettinger.anim8.PNG8;
import com.github.tommyettinger.anim8.PaletteReducer;

import java.io.IOException;

public class Ditherator extends ApplicationAdapter {
    public static final boolean DEBUG = false;
    public String name;
    private PNG8 png;

    public Ditherator() {
    }
    public Ditherator(String name) {
        this.name = name;
    }

    @Override
    public void create() {
        long startTime = TimeUtils.millis();
        png = new PNG8();
        Pixmap pixmap;
        if(Gdx.files.absolute(this.name).exists())
            pixmap = new Pixmap(Gdx.files.absolute(this.name));
        else
            pixmap = new Pixmap(Gdx.files.local(this.name));
//		png.write(Gdx.files.local((DEBUG ? "out/" + name : name) + "/size" + exp + (smoothing ? "smooth/" : "blocky/") + name + "_angle" + i + ".png"), pixmap);
        System.out.println("Rendered to files in " + (DEBUG ? "out/" + name : name));
        System.out.println("Finished in " + TimeUtils.timeSinceMillis(startTime) * 0.001 + " seconds.");
        Gdx.app.exit();
        System.exit(0);
    }
}