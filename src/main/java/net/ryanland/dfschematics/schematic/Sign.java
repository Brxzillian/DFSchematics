package net.ryanland.dfschematics.schematic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.ryanland.dfschematics.df.value.Item;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockPos;

import java.util.Arrays;

public record Sign(SchematicBlockPos pos, String[] frontLines, String[] backLines) implements Item {

    // example sign
    // {Count:1b,DF_NBT:3578,id:\"minecraft:oak_sign\",tag:{display:{Lore:['{\"extra\":[{\"text\":\"1front\"}]}','{\"extra\":[{\"text\":\"2front\"}]}','{\"extra\":[{\"text\":\"3front\"}]}','{\"extra\":[{\"text\":\"4front\"}]}','{\"extra\":[{\"text\":\"1back\"}]}','{\"extra\":[{\"text\":\"2back\"}]}','{\"extra\":[{\"text\":\"3back\"}]}','{\"extra\":[{\"text\":\"4back\"}]}']}}}

    @Override
    public String getItemNBT() {
        JsonObject item = new JsonObject();
        item.addProperty("Count", 1);
        item.addProperty("id", "minecraft:oak_sign");
        JsonObject tag = new JsonObject();
        JsonObject display = new JsonObject();
        JsonArray lore = new JsonArray(8);
        for (String frontLine : frontLines) lore.add(frontLine);
        for (String backLine : backLines) lore.add(backLine);
        display.add("Lore", lore);
        display.addProperty("Name", extra("%s,%s,%s".formatted(pos.x, pos.y, pos.z)));
        tag.add("display", display);
        item.add("tag", tag);
        return item.toString();
        // result: Oak sign with lores; name is xyz, 1-4 front lines, 5-8 back lines
    }

    private String extra(String input) {
        return "{\"extra\":[{\"text\":\"%s\"}],\"text\":\"\"}".formatted(input);
    }

    public boolean isEmpty() {
        return Arrays.stream(frontLines).allMatch(this::isComponentEmpty) &&
            Arrays.stream(backLines).allMatch(this::isComponentEmpty);
    }

    private boolean isComponentEmpty(String json) {
        Component component = GsonComponentSerializer.gson().deserialize(json);
        return LegacyComponentSerializer.legacySection().serialize(component).isEmpty();
    }
}
