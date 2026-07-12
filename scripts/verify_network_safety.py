#!/usr/bin/env python3
"""Verify simple serverbound networking stays server-authoritative."""

from __future__ import annotations

import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery"
CLIENT_JAVA_ROOT = ROOT / "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery"
MESSAGE_DIR = JAVA_ROOT / "network/message"
NETWORK_HANDLER = JAVA_ROOT / "network/NetworkHandler.java"
CLIENT_NETWORK_HANDLER = CLIENT_JAVA_ROOT / "client/network/ClientNetworkHandler.java"


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def collect_messages() -> dict[str, str]:
    messages: dict[str, str] = {}
    for path in sorted(MESSAGE_DIR.glob("*.java")):
        text = read(path)
        match = re.search(r"public\s+record\s+(\w+)\s*\(([^)]*)\)\s+implements\s+CustomPacketPayload", text)
        if match:
            messages[match.group(1)] = match.group(2).strip()
    return messages


def main() -> int:
    errors: list[str] = []
    messages = collect_messages()
    network_handler = read(NETWORK_HANDLER)
    client_network_handler = read(CLIENT_NETWORK_HANDLER)

    if not messages:
        errors.append("No CustomPacketPayload records found under network/message.")

    for message, components in messages.items():
        text = read(MESSAGE_DIR / f"{message}.java")
        if components:
            errors.append(f"{message} carries client-provided fields: {components}")
        if "StreamCodec.unit(INSTANCE)" not in text:
            errors.append(f"{message} does not use unit stream codec.")
        if f"PayloadTypeRegistry.serverboundPlay().register({message}.TYPE, {message}.STREAM_CODEC)" not in network_handler:
            errors.append(f"{message} is not registered as a serverbound play payload.")
        receiver_pattern = rf"ServerPlayNetworking\.registerGlobalReceiver\(\s*{message}\.TYPE\s*,"
        if not re.search(receiver_pattern, network_handler):
            errors.append(f"{message} has no server play receiver.")
        if f"sendIfAvailable({message}.TYPE, {message}.INSTANCE)" not in client_network_handler:
            errors.append(f"{message} is not sent through ClientNetworkHandler.sendIfAvailable.")

    for path in sorted(JAVA_ROOT.rglob("*.java")):
        text = read(path)
        if "ClientPlayNetworking.send(" in text and path != CLIENT_NETWORK_HANDLER:
            errors.append(f"Direct ClientPlayNetworking.send outside ClientNetworkHandler: {path.relative_to(ROOT)}")
        if "ServerPlayNetworking.registerGlobalReceiver" in text and path != NETWORK_HANDLER:
            errors.append(f"Server networking receiver registered outside NetworkHandler: {path.relative_to(ROOT)}")

    if "ClientPlayNetworking.canSend(type)" not in client_network_handler:
        errors.append("ClientNetworkHandler does not guard sends with ClientPlayNetworking.canSend(type).")

    context_handlers = re.findall(
        r"private\s+static\s+void\s+(handle\w+)\s*\([^)]*ServerPlayNetworking\.Context\s+context\)\s*\{(?P<body>.*?)\n\s*\}",
        network_handler,
        flags=re.DOTALL,
    )
    if len(context_handlers) != len(messages):
        errors.append(f"Expected {len(messages)} context handlers, found {len(context_handlers)}.")
    for name, body in context_handlers:
        if "context.server().execute" in body:
            errors.append(f"{name} redundantly reschedules a Fabric server-thread receiver.")
        if "context.player()" not in body:
            errors.append(f"{name} does not get the authoritative server player from context.")

    if "private static boolean isValidPlayer(ServerPlayer player)" not in network_handler:
        errors.append("NetworkHandler is missing a shared server player validity check.")
    if "!player.hasDisconnected()" not in network_handler or "!player.isRemoved()" not in network_handler:
        errors.append("NetworkHandler player validity check does not cover disconnected/removed players.")
    if "player.hasEffect(ModEffects.FLATULENCE)" not in network_handler:
        errors.append("Flatulence handling does not verify the server-side effect.")
    if "stack.is(ModItems.BAOZI)" not in network_handler:
        errors.append("Baozi throwing does not verify the server-side held item.")
    if "player.getCooldowns().isOnCooldown(stack)" not in network_handler:
        errors.append("Baozi throwing does not check server-side cooldown.")
    if "player.getCooldowns().addCooldown(stack" not in network_handler:
        errors.append("Baozi throwing does not add server-side cooldown.")
    if "stack.shrink(THROWN_BAOZI_COUNT)" not in network_handler:
        errors.append("Baozi throwing does not consume the server-side stack.")

    if errors:
        print("Network safety verification failed:")
        print("\n".join(errors))
        return 1

    print("Network safety verification passed.")
    print(f"  serverbound payloads: {len(messages)}")
    for message in sorted(messages):
        print(f"  - {message}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
