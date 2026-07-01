# FakeLag Randomization Implementation

Apply these exact changes to BOTH `yugi-mc-26.1` and `yugi-mc-26.2`.

---

## 1. YugiClient.java

### Add field after `lastFlushTime` (around line 37)

```java
    public static boolean fakelagRandomize = false;
```

### Change timer flush block (currently lines 238-243)

From:
```java
            if (fakeLagEnabled) {
                long now = System.currentTimeMillis();
                if (now - lastFlushTime >= fakelagDelay) {
                    PacketUtil.flushPackets();
                    lastFlushTime = now;
                }
```

To:
```java
            if (fakeLagEnabled) {
                long now = System.currentTimeMillis();
                if (now - lastFlushTime >= fakelagDelay) {
                    PacketUtil.flushPackets();
                    if (fakelagRandomize) {
                        lastFlushTime = now + (long)((Math.random() - 0.5) * fakelagDelay * 0.3);
                    } else {
                        lastFlushTime = now;
                    }
                }
```

### Add to disconnect handler

```java
            fakelagRandomize = false;
```

---

## 2. YugiMCCommand.java

### Add `randomize` subcommand under the `fakelag` block

After the `.executes(ctx -> { ... })` for the delay argument and before the `.executes(ctx -> { ... })` for the info display:

```java
                .then(ClientCommands.literal("randomize").executes(ctx -> {
                    YugiClient.fakelagRandomize = !YugiClient.fakelagRandomize;
                    ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rFakeLag randomization " +
                        (YugiClient.fakelagRandomize ? "§aOn" : "§cOff")));
                    return 1;
                }))
```

### Update the info command to show randomization state

In the `.executes(ctx -> { ... })` with no arguments, change:
```java
                    ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rCurrent FakeLag delay: " + YugiClient.fakelagDelay + "ms"));
                    ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rUsage: /yugimc fakelag <delay(0-5000)>"));
```

To:
```java
                    ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rCurrent FakeLag delay: " + YugiClient.fakelagDelay + "ms (Randomization: " +
                        (YugiClient.fakelagRandomize ? "§aOn" : "§cOff") + "§r)"));
                    ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rUsage: /yugimc fakelag <delay(0-5000)>"));
                    ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rUsage: /yugimc fakelag randomize"));
```

---

## Files to modify

```
yugi-mc-26.1/src/main/java/com/yugimc/YugiClient.java
yugi-mc-26.1/src/main/java/com/yugimc/command/YugiMCCommand.java
yugi-mc-26.2/src/main/java/com/yugimc/YugiClient.java
yugi-mc-26.2/src/main/java/com/yugimc/command/YugiMCCommand.java
```
