package com.yugimc;

import com.yugimc.util.ModCommandRegister;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YugiMC implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("yugi-mc");

	public static boolean initialized;

	@Override
	public void onInitialize() {


		if (initialized) {
            throw new RuntimeException("YugiMC.onInitialize() ran twice!");
        }
		YugiClient.cliente.initialize();
		ModCommandRegister.registerCommands();
		initialized = true;
        System.out.println("Yugi+ is now Running!");

		LOGGER.info("Hello Fabric world!");
	}
}