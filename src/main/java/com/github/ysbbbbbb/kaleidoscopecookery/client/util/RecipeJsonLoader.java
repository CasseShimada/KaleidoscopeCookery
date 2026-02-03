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
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
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
import java.util.stream.Stream;

public final class RecipeJsonLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String RECIPES_PATH = "recipes/";
    private static final String JSON_EXT = ".json";


    private RecipeJsonLoader() {
    }

    public static <T extends Recipe<?>> List<RecipeHolder<T>> getRecipes(RecipeType<T> type, RecipeSerializer<T> serializer) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return List.of();
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
        List<RecipeHolder<T>> fromMod = loadFromModContainer(KaleidoscopeCookery.MOD_ID, level.registryAccess(), serializer, type);
        if (!fromMod.isEmpty()) {
            return fromMod;
        }
        List<RecipeHolder<T>> fromResources = loadFromResources(minecraft.getResourceManager(), level.registryAccess(), serializer, type);
        return fromResources;
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectFromManager(RecipeManager manager, RecipeType<T> type) {
        List<RecipeHolder<T>> results = new ArrayList<>();
        for (RecipeHolder<?> holder : manager.getRecipes()) {
            if (holder.value().getType() == type) {
                results.add((RecipeHolder<T>) holder);
            }
        }
        return results;
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectFromAccess(RecipeAccess access, RecipeType<T> type) {
        List<RecipeHolder<T>> results = new ArrayList<>();
        for (RecipeHolder<?> holder : access.getSynchronizedRecipes().recipes()) {
            if (holder.value().getType() == type) {
                results.add((RecipeHolder<T>) holder);
            }
        }
        return results;
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> loadFromModContainer(
            String modId,
            RegistryAccess registryAccess,
            RecipeSerializer<T> serializer,
            RecipeType<T> type
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
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
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
                DataResult<T> parsed = serializer.codec().codec().parse(ops, json);
                parsed.resultOrPartial(message -> LOGGER.warn("Failed to parse recipe {}: {}", recipeId, message))
                        .ifPresent(recipe -> results.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, recipeId), recipe)));
            });
        } catch (Exception e) {
            LOGGER.warn("Failed to load recipes from mod container {}", modId, e);
        }
        return results;
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> loadFromResources(
            ResourceManager resourceManager,
            RegistryAccess registryAccess,
            RecipeSerializer<T> serializer,
            RecipeType<T> type
    ) {
        Identifier serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
        if (serializerId == null) {
            return List.of();
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
        List<RecipeHolder<T>> results = new ArrayList<>();
        Map<Identifier, Resource> resources = resourceManager.listResources("recipes", id -> id.getPath().endsWith(JSON_EXT));
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
            DataResult<T> parsed = serializer.codec().codec().parse(ops, json);
            parsed.resultOrPartial(message -> LOGGER.warn("Failed to parse recipe {}: {}", recipeId, message))
                    .ifPresent(recipe -> results.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, recipeId), recipe)));
        }
        return results;
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
