package com.yugimc.util;

import com.yugimc.command.YugiMCCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ModCommandRegister {
    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(YugiMCCommand::register);
    }
}