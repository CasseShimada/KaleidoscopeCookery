package com.github.ysbbbbbb.kaleidoscopecookery.item.quality;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class QualityEvaluator {
    private static final int MAX_CAPACITY = 9;

    private QualityEvaluator() {
    }

    public static Quality evaluate(List<ItemStack> inputs, List<Ingredient> ingredients, Identifier recipeId, long worldSeed) {
        List<Ingredient> nonEmpty = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (!ingredient.isEmpty()) {
                nonEmpty.add(ingredient);
            }
        }

        List<ItemStack> nonEmptyInputs = inputs.stream()
                .filter(stack -> !stack.isEmpty())
                .toList();
        if (nonEmptyInputs.isEmpty() || nonEmpty.isEmpty()) {
            return Quality.POOR;
        }

        if (nonEmpty.size() == 1) {
            return oneInputQuality(nonEmptyInputs, recipeId, worldSeed);
        }

        var recipeVector = randomVector(nonEmpty, recipeId, worldSeed);
        return evalQuality(nonEmptyInputs, recipeVector);
    }

    private static @NotNull Quality oneInputQuality(List<ItemStack> inputs, Identifier recipeId, long worldSeed) {
        long recipeSeed = worldSeed * 31 + recipeId.hashCode();
        Random random = new Random(recipeSeed);

        int count = inputs.size();
        if (count <= 1) {
            return Quality.POOR;
        }

        int standard = 1 + random.nextInt(2);
        if (count <= standard) {
            return Quality.STANDARD;
        }

        int excellent = 2 + random.nextInt(2);
        if (count <= excellent) {
            return Quality.EXCELLENT;
        }

        return Quality.SUPERB;
    }

    private static List<Pair<Ingredient, Integer>> randomVector(List<Ingredient> ingredients, Identifier id, long seed) {
        long recipeSeed = seed * 31 + id.hashCode();
        Random random = new Random(recipeSeed);

        int size = ingredients.size();
        IntList countPool = new IntArrayList(size);

        switch (size) {
            case 2:
                countPool.add(2 + random.nextInt(3));
                countPool.add(1 + random.nextInt(2));
                break;
            case 3:
                countPool.add(2 + random.nextInt(3));
                countPool.add(1 + random.nextInt(3));
                countPool.add(1 + random.nextInt(2));
                break;
            default:
                for (int i = 0; i < size; i++) {
                    countPool.add(1 + random.nextInt(2));
                }
                break;
        }

        Collections.shuffle(countPool, random);

        List<Pair<Ingredient, Integer>> recipeVector = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            recipeVector.add(Pair.of(ingredients.get(i), countPool.getInt(i)));
        }

        return recipeVector;
    }

    private static Quality evalQuality(List<ItemStack> inputs, List<Pair<Ingredient, Integer>> recipes) {
        int size = recipes.size();
        int[] inputsVec = new int[size];
        int[] recipesVec = new int[size];

        for (int i = 0; i < size; i++) {
            var pair = recipes.get(i);
            for (ItemStack stack : inputs) {
                if (pair.left().test(stack)) {
                    inputsVec[i]++;
                }
            }
            recipesVec[i] = pair.right();
        }

        double finalScore = getFinalScore(size, inputs.size(), inputsVec, recipesVec);
        for (Quality quality : Quality.values()) {
            if (finalScore >= quality.getScore()) {
                return quality;
            }
        }

        return Quality.STANDARD;
    }

    private static double getFinalScore(int size, int inputSize, int[] inputsVec, int[] recipesVec) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < size; i++) {
            double a = inputsVec[i];
            double b = recipesVec[i];
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        double cosine = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        double similarityScore = Math.pow(cosine, 4);
        double quantityFactor = 0.8 + 0.2 * (inputSize / (double) MAX_CAPACITY);

        return Mth.clamp(similarityScore * quantityFactor, 0.0, 1.0);
    }
}
