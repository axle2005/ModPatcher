package me.nallar.modpatcher;

import me.nallar.javatransformer.api.JavaTransformer;

class ModPatcherLoadHook {
	private static final int API_VERSION = 0; //Keep in sync with version in ModPatcher.java
	private static final String VERSION = "@VERSION@".replace("-SNAPSHOT", "");

	static void loadHook(ModPatcher.Version requiredVersion, String modPatcherRelease, int apiVersion) {
		if (ModPatcherLoadHook.class.getClassLoader().getClass().getName().contains("LaunchClassLoader")) {
			throw new Error("ModPatcher should not be loaded under LaunchClassLoader");
		}

		if (API_VERSION != apiVersion) {
			PatcherLog.warn("API version mismatch. Expected " + API_VERSION + ", got " + apiVersion);
			PatcherLog.warn("API was loaded from: " + JavaTransformer.pathFromClass(ModPatcher.class));
		}

		ModPatcher.Version current = ModPatcher.Version.of(VERSION);

		if (current.compareTo(requiredVersion) < 0) {
			String autoUpdate = "\nWill auto-update on next start.";

			if (ModPatcher.neverUpdate())
				autoUpdate = "";
			else
				JavaTransformer.pathFromClass(ModPatcherTransformer.class).toFile().deleteOnExit();

			throw new RuntimeException("ModPatcher outdated. Have version: " + VERSION + ", requested version: " + requiredVersion + autoUpdate);
		}
	}
}
