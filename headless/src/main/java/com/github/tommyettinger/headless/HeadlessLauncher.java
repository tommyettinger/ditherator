package com.github.tommyettinger.headless;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.Ditherator;
import com.github.tommyettinger.Halftoner;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "ditherator", version = "Ditherator 0.0.1",
		description = "Given an image file, write dithered versions to a subfolder.",
		mixinStandardHelpOptions = true)
public class HeadlessLauncher implements Callable<Integer> {

	@CommandLine.Parameters(description = "The absolute or relative path to a .jpg or .png image.", defaultValue = "Roar.jpg", index = "0")
	public String input = "Roar.jpg";

	public static void main(String[] args) {
		int exitCode = new picocli.CommandLine(new HeadlessLauncher()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		configuration.updatesPerSecond = -1;
		if(Ditherator.DEBUG)
			input = "temp/" + input;
		new HeadlessApplication(new Ditherator(input), configuration){
			{
				try {
					mainLoopThread.join();
				} catch (InterruptedException e) {
					System.out.println("Interrupted!");
				}
			}
		};

		return 0;
	}
}