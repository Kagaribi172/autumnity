package com.minecraftabnormals.autumnity.core;

import com.minecraftabnormals.abnormals_core.core.util.DataUtil;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import com.minecraftabnormals.autumnity.core.other.AutumnityClient;
import com.minecraftabnormals.autumnity.core.other.AutumnityCompat;
import com.minecraftabnormals.autumnity.core.registry.AutumnityBanners;
import com.minecraftabnormals.autumnity.core.registry.AutumnityBiomes;
import com.minecraftabnormals.autumnity.core.registry.AutumnityEffects;
import com.minecraftabnormals.autumnity.core.registry.AutumnityEntities;
import com.minecraftabnormals.autumnity.core.registry.AutumnityFeatures;
import com.minecraftabnormals.autumnity.core.registry.AutumnityPaintings;
import com.minecraftabnormals.autumnity.core.registry.AutumnityParticles;
import com.minecraftabnormals.autumnity.core.registry.AutumnityPotions;
import com.minecraftabnormals.autumnity.core.registry.AutumnityStructures;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Autumnity.MOD_ID)
public class Autumnity {
	public static final String MOD_ID = "autumnity";
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

	public Autumnity() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		REGISTRY_HELPER.register(modEventBus);
		AutumnityPaintings.PAINTINGS.register(modEventBus);
		AutumnityEffects.EFFECTS.register(modEventBus);
		AutumnityPotions.POTIONS.register(modEventBus);
		AutumnityFeatures.FEATURES.register(modEventBus);
		AutumnityStructures.STRUCTURES.register(modEventBus);
		AutumnityParticles.PARTICLES.register(modEventBus);

		MinecraftForge.EVENT_BUS.register(this);

		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modEventBus.addListener(this::clientSetup);
		});

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AutumnityConfig.COMMON_SPEC);
		DataUtil.registerConfigCondition(MOD_ID, AutumnityConfig.COMMON);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			AutumnityCompat.registerCompostables();
			AutumnityCompat.registerFlammables();
			AutumnityCompat.registerDispenserBehaviors();

			AutumnityBanners.registerBanners();
			AutumnityPotions.registerBrewingRecipes();
			AutumnityBiomes.addBiomeTypes();
			AutumnityBiomes.addBiomesToGeneration();
			AutumnityFeatures.Configured.registerConfiguredFeatures();
			AutumnityStructures.Configured.registerConfiguredStructureFeatures();
			AutumnityStructures.registerNoiseSettings();;
			AutumnityEntities.registerSpawns();
			AutumnityEntities.registerAttributes();
		});
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		AutumnityEntities.setupEntitiesClient();
		event.enqueueWork(() -> {
			AutumnityClient.setRenderLayers();
			AutumnityClient.registerBlockColors();
		});
	}
}