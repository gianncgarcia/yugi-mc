package com.yugimc.util;

import com.yugimc.command.outlinecom;
import com.yugimc.command.outlineoff;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ModCommandRegister {
    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(outlinecom::register); // turning on for all entity outline
        ClientCommandRegistrationCallback.EVENT.register(outlineoff::register); // turning off for all entity outline
    }
}