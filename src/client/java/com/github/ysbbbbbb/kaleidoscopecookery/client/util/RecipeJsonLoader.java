package com.github.ysbbbbbb.kaleidoscopecookery.client.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.slf4j.Logger;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class RecipeJsonLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String RECIPES_PATH = "recipe/";
    private static final String JSON_EXT = ".json";


    private RecipeJsonLoader() {
    }

    public static <T extends Recipe<?>> List<RecipeHolder<T>> getRecipes(RecipeType<T> type, RecipeSerializer<T> serializer) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            RegistryAccess.Frozen registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            HolderLookup.Provider provider = loadTagAwareProvider(minecraft.getResourceManager(), registryAccess);
            List<RecipeHolder<T>> fromResources = loadFromResources(minecraft.getResourceManager(), provider, serializer);
            if (!fromResources.isEmpty()) {
                return fromResources;
            }
            return loadFromModContainer(KaleidoscopeCookery.MOD_ID, provider, serializer);
        }
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            return collectFromManager(server.getRecipeManager(), type);
        }
        RecipeAccess access = level.recipeAccess();
        if (access != null) {
            List<RecipeHolder<T>> fromAccess = collectFromAccess(access, type);
            if (!fromAccess.isEmpty()) {
                return fromAccess;
            }
            if (access instanceof RecipeManager recipeManager) {
                List<RecipeHolder<T>> fromManager = collectFromManager(recipeManager, type);
                if (!fromManager.isEmpty()) {
                    return fromManager;
                }
            }
        }
        List<RecipeHolder<T>> fromMod = loadFromModContainer(KaleidoscopeCookery.MOD_ID, level.registryAccess(), serializer);
        if (!fromMod.isEmpty()) {
            return fromMod;
        }
        List<RecipeHolder<T>> fromResources = loadFromResources(minecraft.getResourceManager(), level.registryAccess(), serializer);
        return fromResources;
    }

    public static <T extends Recipe<?>> List<RecipeHolder<T>> getRecipes(RecipeSerializer<T> serializer) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            RegistryAccess.Frozen registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            HolderLookup.Provider provider = loadTagAwareProvider(minecraft.getResourceManager(), registryAccess);
            List<RecipeHolder<T>> fromResources = loadFromResources(minecraft.getResourceManager(), provider, serializer);
            if (!fromResources.isEmpty()) {
                return fromResources;
            }
            return loadFromModContainer(KaleidoscopeCookery.MOD_ID, provider, serializer);
        }
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            return collectBySerializer(server.getRecipeManager(), serializer);
        }
        RecipeAccess access = level.recipeAccess();
        if (access != null) {
            List<RecipeHolder<T>> fromAccess = collectBySerializer(access, serializer);
            if (!fromAccess.isEmpty()) {
                return fromAccess;
            }
            if (access instanceof RecipeManager recipeManager) {
                List<RecipeHolder<T>> fromManager = collectBySerializer(recipeManager, serializer);
                if (!fromManager.isEmpty()) {
                    return fromManager;
                }
            }
        }
        List<RecipeHolder<T>> fromMod = loadFromModContainer(KaleidoscopeCookery.MOD_ID, level.registryAccess(), serializer);
        if (!fromMod.isEmpty()) {
            return fromMod;
        }
        return loadFromResources(minecraft.getResourceManager(), level.registryAccess(), serializer);
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectFromManager(RecipeManager manager, RecipeType<T> type) {
        return collectMatchingRecipes(manager.getRecipes(), type);
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectFromAccess(RecipeAccess access, RecipeType<T> type) {
        return collectMatchingRecipes(access.getSynchronizedRecipes().recipes(), type);
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectBySerializer(RecipeManager manager, RecipeSerializer<T> serializer) {
        return collectMatchingRecipes(manager.getRecipes(), serializer);
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectBySerializer(RecipeAccess access, RecipeSerializer<T> serializer) {
        return collectMatchingRecipes(access.getSynchronizedRecipes().recipes(), serializer);
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectMatchingRecipes(Iterable<RecipeHolder<?>> holders, RecipeType<T> type) {
        List<RecipeHolder<T>> results = new ArrayList<>();
        for (RecipeHolder<?> holder : holders) {
            if (holder.value().getType() == type) {
                results.add(castRecipeHolder(holder));
            }
        }
        return results;
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectMatchingRecipes(
            Iterable<RecipeHolder<?>> holders,
            RecipeSerializer<T> serializer
    ) {
        List<RecipeHolder<T>> results = new ArrayList<>();
        for (RecipeHolder<?> holder : holders) {
            if (holder.value().getSerializer() == serializer) {
                results.add(castRecipeHolder(holder));
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> RecipeHolder<T> castRecipeHolder(RecipeHolder<?> holder) {
        return (RecipeHolder<T>) holder;
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> loadFromModContainer(
            String modId,
            HolderLookup.Provider lookupProvider,
            RecipeSerializer<T> serializer
    ) {
        var container = FabricLoader.getInstance().getModContainer(modId);
        if (container.isEmpty()) {
            return List.of();
        }
        Identifier serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
        if (serializerId == null) {
            return List.of();
        }
        Path root = container.get().findPath("data/" + modId + "/" + RECIPES_PATH).orElse(null);
        if (root == null) {
            return List.of();
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, lookupProvider);
        List<RecipeHolder<T>> results = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(path -> path.toString().endsWith(JSON_EXT)).forEach(path -> {
                String recipePath = root.relativize(path).toString().replace('\\', '/');
                if (!recipePath.endsWith(JSON_EXT)) {
                    return;
                }
                recipePath = recipePath.substring(0, recipePath.length() - JSON_EXT.length());
                Identifier recipeId = Identifier.fromNamespaceAndPath(modId, recipePath);
                JsonObject json = readJson(path, recipeId);
                if (json == null) {
                    return;
                }
                Identifier typeId = getTypeId(json);
                if (!serializerId.equals(typeId)) {
                    return;
                }
                parseRecipe(serializer, ops, recipeId, json).ifPresent(results::add);
            });
        } catch (Exception e) {
            LOGGER.warn("Failed to load recipes from mod container {}", modId, e);
        }
        return results;
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> loadFromResources(
            ResourceManager resourceManager,
            HolderLookup.Provider lookupProvider,
            RecipeSerializer<T> serializer
    ) {
        Identifier serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
        if (serializerId == null) {
            return List.of();
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, lookupProvider);
        List<RecipeHolder<T>> results = new ArrayList<>();
        Map<Identifier, Resource> resources = resourceManager.listResources("recipe", id -> id.getPath().endsWith(JSON_EXT));
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            Identifier recipeId = toRecipeId(resourceId);
            if (recipeId == null) {
                continue;
            }
            JsonObject json = readJson(entry.getValue(), resourceId);
            if (json == null) {
                continue;
            }
            Identifier typeId = getTypeId(json);
            if (!serializerId.equals(typeId)) {
                continue;
            }
            parseRecipe(serializer, ops, recipeId, json).ifPresent(results::add);
        }
        return results;
    }

    private static <T extends Recipe<?>> Optional<RecipeHolder<T>> parseRecipe(
            RecipeSerializer<T> serializer,
            RegistryOps<JsonElement> ops,
            Identifier recipeId,
            JsonObject json
    ) {
        DataResult<T> parsed = serializer.codec().codec().parse(ops, json);
        return parsed.resultOrPartial(message -> LOGGER.warn("Failed to parse recipe {}: {}", recipeId, message))
                .map(recipe -> new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, recipeId), recipe));
    }

    private static HolderLookup.Provider loadTagAwareProvider(ResourceManager resourceManager, RegistryAccess.Frozen registryAccess) {
        try {
            List<Registry.PendingTags<?>> pendingTags = TagLoader.loadTagsForExistingRegistries(resourceManager, registryAccess);
            if (pendingTags.isEmpty()) {
                return registryAccess;
            }
            List<HolderLookup.RegistryLookup<?>> lookups = TagLoader.buildUpdatedLookups(registryAccess, pendingTags);
            return HolderLookup.Provider.create(lookups.stream());
        } catch (Exception e) {
            LOGGER.warn("Failed to prepare tag-aware recipe lookup for JEI fallback", e);
            return registryAccess;
        }
    }

    private static Identifier getTypeId(JsonObject json) {
        if (!json.has("type")) {
            return null;
        }
        String type = GsonHelper.getAsString(json, "type", "");
        return Identifier.tryParse(type);
    }

    private static Identifier toRecipeId(Identifier resourceId) {
        String path = resourceId.getPath();
        if (!path.startsWith(RECIPES_PATH) || !path.endsWith(JSON_EXT)) {
            return null;
        }
        String recipePath = path.substring(RECIPES_PATH.length(), path.length() - JSON_EXT.length());
        return Identifier.fromNamespaceAndPath(resourceId.getNamespace(), recipePath);
    }

    private static JsonObject readJson(Resource resource, Identifier resourceId) {
        try (Reader reader = resource.openAsReader()) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                LOGGER.warn("Recipe {} is not a JSON object", resourceId);
                return null;
            }
            return element.getAsJsonObject();
        } catch (Exception e) {
            LOGGER.warn("Failed to read recipe {}", resourceId, e);
            return null;
        }
    }

    private static JsonObject readJson(Path path, Identifier recipeId) {
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                LOGGER.warn("Recipe {} is not a JSON object", recipeId);
                return null;
            }
            return element.getAsJsonObject();
        } catch (Exception e) {
            LOGGER.warn("Failed to read recipe {}", recipeId, e);
            return null;
        }
    }
}
