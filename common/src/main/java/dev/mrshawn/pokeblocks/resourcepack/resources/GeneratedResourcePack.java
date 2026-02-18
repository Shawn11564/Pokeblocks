package dev.mrshawn.pokeblocks.resourcepack.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.mrshawn.pokeblocks.registry.DecorativeRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

public class GeneratedResourcePack {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Returns a map of resource path -> JSON string for all generated resources.
     */
    public static Map<String, String> generateAll() {
        Map<String, String> resources = new LinkedHashMap<>();

        String blockModel = GSON.toJson(blockModelJson());
        String itemModel = GSON.toJson(itemModelJson());

        // Pokedoll
        addBlock(resources, "pokedoll", blockModel, itemModel);

        // Figurine
        addBlock(resources, "figurine", blockModel, itemModel);

        // All decorative blocks
        for (DecorativeRegistry.DecorativeEntry entry : DecorativeRegistry.ALL_ENTRIES) {
            addBlock(resources, entry.definition().id(), blockModel, itemModel);
        }

        return resources;
    }

    private static void addBlock(Map<String, String> resources, String id, String blockModel, String itemModel) {
        // Blockstate
        Map<String, Object> blockstate = new LinkedHashMap<>();
        Map<String, Object> variants = new LinkedHashMap<>();
        Map<String, Object> variant = new LinkedHashMap<>();
        variant.put("model", "pokeblocks:block/" + id);
        variants.put("", variant);
        blockstate.put("variants", variants);
        resources.put("assets/pokeblocks/blockstates/" + id + ".json", GSON.toJson(blockstate));

        // Block model
        resources.put("assets/pokeblocks/models/block/" + id + ".json", blockModel);

        // Item model
        resources.put("assets/pokeblocks/models/item/" + id + ".json", itemModel);
    }

    private static Map<String, Object> blockModelJson() {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("parent", "minecraft:builtin/entity");
        return json;
    }

    private static Map<String, Object> itemModelJson() {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("parent", "builtin/entity");

        Map<String, Object> display = new LinkedHashMap<>();

        Map<String, Object> thirdPerson = new LinkedHashMap<>();
        thirdPerson.put("rotation", new int[]{0, 135, 0});
        thirdPerson.put("scale", new double[]{0.5, 0.5, 0.5});

        Map<String, Object> firstPerson = new LinkedHashMap<>();
        firstPerson.put("rotation", new int[]{0, 135, 0});
        firstPerson.put("scale", new double[]{0.5, 0.5, 0.5});

        Map<String, Object> ground = new LinkedHashMap<>();
        ground.put("scale", new double[]{0.5, 0.5, 0.5});

        Map<String, Object> gui = new LinkedHashMap<>();
        gui.put("rotation", new int[]{0, 135, 0});
        gui.put("scale", new double[]{0.75, 0.75, 0.75});
        gui.put("translation", new int[]{0, -8, 0});

        display.put("thirdperson_righthand", thirdPerson);
        display.put("thirdperson_lefthand", thirdPerson);
        display.put("firstperson_righthand", firstPerson);
        display.put("firstperson_lefthand", firstPerson);
        display.put("ground", ground);
        display.put("gui", gui);

        json.put("display", display);
        return json;
    }
}