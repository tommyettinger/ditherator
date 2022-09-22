package com.github.tommyettinger.headless;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.LittleEndianDataInputStream;
import com.github.tommyettinger.SpotVox;
import com.github.tommyettinger.Tools3D;
import com.github.tommyettinger.VoxIO;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "ditherator", version = "Ditherator 0.0.1",
		description = "Given an image file, write dithered versions to a subfolder.",
		mixinStandardHelpOptions = true)
public class HeadlessLauncher implements Callable<Integer> {

	@CommandLine.Parameters(description = "The absolute or relative path to a .jpg or .png image.", defaultValue = "David.png")
	public String input = "David.png";

	public static void main(String[] args) {
		int exitCode = new picocli.CommandLine(new HeadlessLauncher()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		configuration.updatesPerSecond = -1;
		if(Ditherator.DEBUG)
			input = "../tmp/" + input;
		try {
			int nameStart = Math.max(input.lastIndexOf('/'), input.lastIndexOf('\\')) + 1;
			input = input.substring(nameStart, input.indexOf('.', nameStart));
			new HeadlessApplication(new Ditherator(input), configuration){
				{
					try {
						mainLoopThread.join();
					} catch (InterruptedException e) {
						System.out.println("Interrupted!");
					}
				}
			};

		} catch (FileNotFoundException e) {
			System.out.println("Parameters are not valid. Run with -h to show help.");
			return -1;
		}
		return 0;
	}
}