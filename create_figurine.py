import os
import shutil
import json

input_dir = "input"
output_dir = "src/main/resources/assets/pokeblocks"
template_dir = "template"
src_dir = "src/main/java/dev/mrshawn/pokeblocks"

for folder_name in os.listdir(input_dir):
    if not "Figurine" in folder_name:
        continue
        
    # Extract the name part before " Figurine"
    base_name = folder_name.replace(" Figurine", "").lower()
    # Convert to snake_case for file names
    file_base_name = base_name.replace(" ", "_")
    
    # Delete .bbmodel from folder
    for file_name in os.listdir(f"{input_dir}/{folder_name}"):
        if file_name.endswith(".bbmodel"):
            os.remove(f"{input_dir}/{folder_name}/{file_name}")

    # 1. Copy texture files
    for file_name in os.listdir(f"{input_dir}/{folder_name}"):
        if file_name.endswith(".png"):
            shutil.copy(f"{input_dir}/{folder_name}/{file_name}", f"{output_dir}/textures/block")

    # 2. Copy geo json file
    for file_name in os.listdir(f"{input_dir}/{folder_name}"):
        if file_name.endswith(".geo.json"):
            shutil.copy(f"{input_dir}/{folder_name}/{file_name}", f"{output_dir}/geo")

    # 3. Copy and modify blockstate file
    # Copy the template file to create the new file
    shutil.copy(f"{template_dir}/blockstates/template_blockstate.json",
                f"{output_dir}/blockstates/figurine_{file_base_name}.json")

    with open(f"{output_dir}/blockstates/figurine_{file_base_name}.json", "r") as file:
        content = file.read()
    content = content.replace("<pokemon name>", file_base_name)
    with open(f"{output_dir}/blockstates/figurine_{file_base_name}.json", "w") as file:
        file.write(content)

    # 4. Copy and modify block model file
    shutil.copy(f"{template_dir}/models/block/template_block_model.json",
                f"{output_dir}/models/block/figurine_{file_base_name}.json")

    with open(f"{output_dir}/models/block/figurine_{file_base_name}.json", "r") as file:
        content = file.read()
    content = content.replace("<pokemon name>", file_base_name)
    with open(f"{output_dir}/models/block/figurine_{file_base_name}.json", "w") as file:
        file.write(content)

    # 5. Copy and modify item model file
    shutil.copy(f"{template_dir}/models/item/template_item_model.json",
                f"{output_dir}/models/item/figurine_{file_base_name}.json")

    with open(f"{output_dir}/models/item/figurine_{file_base_name}.json", "r") as file:
        content = file.read()
    content = content.replace("<pokemon name>", file_base_name)
    with open(f"{output_dir}/models/item/figurine_{file_base_name}.json", "w") as file:
        file.write(content)

    # 6. Modify en_us.json
    with open(f"{output_dir}/lang/en_us.json", "r+") as file:
        data = json.load(file)
        display_name = " ".join(word.capitalize() for word in base_name.split("_"))
        data[f"item.pokeblocks.figurine_{file_base_name}"] = f"{display_name} Figurine"
        data[f"block.pokeblocks.figurine_{file_base_name}"] = f"{display_name} Figurine"
        file.seek(0)
        json.dump(data, file, indent=4)
        file.truncate()

    # 7. Modify PokeIDs.java
    with open(f"{src_dir}/constants/PokeIDs.java", "r") as file:
        lines = file.readlines()

    last_curly_bracket_line = -1
    for i, line in reversed(list(enumerate(lines))):
        if "}" in line:
            last_curly_bracket_line = i
            break

    if last_curly_bracket_line != -1:
        new_lines = [
            f"	public static final String FIGURINE_{file_base_name.upper()} = \"figurine_{file_base_name}\";\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_curly_bracket_line, new_line)

    with open(f"{src_dir}/constants/PokeIDs.java", "w") as file:
        file.writelines(lines)

    # 8. Modify ResourceConstants.java
    with open(f"{src_dir}/constants/ResourceConstants.java", "r") as file:
        lines = file.readlines()

    last_curly_bracket_line = -1
    for i, line in reversed(list(enumerate(lines))):
        if "}" in line:
            last_curly_bracket_line = i
            break

    if last_curly_bracket_line != -1:
        new_lines = [
            f"	public static final String FIGURINE_{file_base_name.upper()}_TEXTURE = \"figurine_{file_base_name}_texture.png\";\n",
            f"	public static final String FIGURINE_{file_base_name.upper()}_MODEL = \"figurine_{file_base_name}.geo.json\";\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_curly_bracket_line, new_line)

    with open(f"{src_dir}/constants/ResourceConstants.java", "w") as file:
        file.writelines(lines)

    # 9. Modify PokeblocksClient.java
    with open("src/main/java/dev/mrshawn/pokeblocks/PokeblocksClient.java", "r") as file:
        lines = file.readlines()

    method_name = "ServerHandler.register();"
    new_lines = [
        f"		registerBlockEntityRenderer(\n",
        f"			ModBlockEntities.FIGURINE_{file_base_name.upper()}_BLOCK_ENTITY,\n",
        f"			ResourceConstants.FIGURINE_{file_base_name.upper()}_MODEL,\n",
        f"			ResourceConstants.FIGURINE_{file_base_name.upper()}_TEXTURE\n",
        f"		);\n",
        f"\n"
    ]

    for i, line in enumerate(lines):
        if method_name in line:
            for new_line in reversed(new_lines):
                lines.insert(i + 1, new_line)
            break

    with open("src/main/java/dev/mrshawn/pokeblocks/PokeblocksClient.java", "w") as file:
        file.writelines(lines)

    # 10. Modify ModBlocks.java
    with open("src/main/java/dev/mrshawn/pokeblocks/block/ModBlocks.java", "r") as file:
        lines = file.readlines()

    last_entry_line = -1
    for i, line in enumerate(lines):
        if "public static final Block FIGURINE_" in line:
            last_entry_line = i + 1

    if last_entry_line == -1:  # If no figurine entries found, look for pokedoll entries
        for i, line in enumerate(lines):
            if "public static final Block POKEDOLL_" in line:
                last_entry_line = i + 1

    if last_entry_line != -1:
        class_name = "".join(word.capitalize() for word in file_base_name.split("_"))
        new_lines = [
            f"\n    public static final Block FIGURINE_{file_base_name.upper()} = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.FIGURINE_{file_base_name.upper()}),",
            f"\n            new FigurineBlock<>(() -> Figurine{class_name}BlockEntity.class));\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    with open("src/main/java/dev/mrshawn/pokeblocks/block/ModBlocks.java", "w") as file:
        file.writelines(lines)

    # 11. Modify ModItems.java
    with open("src/main/java/dev/mrshawn/pokeblocks/item/ModItems.java", "r") as file:
        lines = file.readlines()

    last_entry_line = -1
    for i, line in enumerate(lines):
        if "public static final Item FIGURINE_" in line:
            last_entry_line = i + 6

    if last_entry_line == -1:  # If no figurine entries found, look for pokedoll entries
        for i, line in enumerate(lines):
            if "public static final Item POKEDOLL_" in line:
                last_entry_line = i + 6

    if last_entry_line != -1:
        new_lines = [
            f"\n    public static final Item FIGURINE_{file_base_name.upper()}_BLOCK_ITEM = registerItem(",
            f"\n        PokeIDs.FIGURINE_{file_base_name.upper()},",
            f"\n        ModBlocks.FIGURINE_{file_base_name.upper()},",
            f"\n        ResourceConstants.FIGURINE_{file_base_name.upper()}_MODEL,",
            f"\n        ResourceConstants.FIGURINE_{file_base_name.upper()}_TEXTURE,",
            f"\n        DollRarity.NONE",
            f"\n    );\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    with open("src/main/java/dev/mrshawn/pokeblocks/item/ModItems.java", "w") as file:
        file.writelines(lines)

    # 12. Modify ModItemGroups.java
    with open("src/main/java/dev/mrshawn/pokeblocks/item/ModItemGroups.java", "r") as file:
        lines = file.readlines()

    last_entry_line = -1
    for i, line in enumerate(lines):
        if "    entries.add(ModBlocks.FIGURINE_" in line:
            last_entry_line = i

    if last_entry_line == -1:  # If no figurine entries found, look for pokedoll entries
        for i, line in enumerate(lines):
            if "    entries.add(ModBlocks.POKEDOLL_" in line:
                last_entry_line = i

    if last_entry_line != -1:
        new_lines = [
            f"\n                        entries.add(ModBlocks.FIGURINE_{file_base_name.upper()});\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    with open("src/main/java/dev/mrshawn/pokeblocks/item/ModItemGroups.java", "w") as file:
        file.writelines(lines)

    # 13. Modify ModBlockEntities.java
    with open("src/main/java/dev/mrshawn/pokeblocks/block/entity/ModBlockEntities.java", "r") as file:
        lines = file.readlines()

    last_entry_line = -1
    for i, line in enumerate(lines):
        if "public static BlockEntityType<Figurine" in line:
            last_entry_line = i

    if last_entry_line == -1:  # If no figurine entries found, look for pokedoll entries
        for i, line in enumerate(lines):
            if "public static BlockEntityType<Pokedoll" in line:
                last_entry_line = i

    if last_entry_line != -1:
        class_name = "".join(word.capitalize() for word in file_base_name.split("_"))
        new_lines = [
            f"\n    public static BlockEntityType<Figurine{class_name}BlockEntity> FIGURINE_{file_base_name.upper()}_BLOCK_ENTITY;\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    # Find the registration section and add the new entity
    last_entry_line = -1
    for i, line in enumerate(lines):
        if "FIGURINE_" in line and "registerBlockEntity(" in line:
            last_entry_line = i + 4

    if last_entry_line == -1:
        for i, line in enumerate(lines):
            if "POKEDOLL_" in line and "registerBlockEntity(" in line:
                last_entry_line = i + 4

    if last_entry_line != -1:
        class_name = "".join(word.capitalize() for word in file_base_name.split("_"))
        new_lines = [
            f"\n        FIGURINE_{file_base_name.upper()}_BLOCK_ENTITY = registerBlockEntity(",
            f"\n                new Identifier(Pokeblocks.MOD_ID, PokeIDs.FIGURINE_{file_base_name.upper()}),",
            f"\n                FabricBlockEntityTypeBuilder.create(Figurine{class_name}BlockEntity::new, ModBlocks.FIGURINE_{file_base_name.upper()}),",
            f"\n                Figurine{class_name}BlockEntity.class",
            f"\n        );\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    with open("src/main/java/dev/mrshawn/pokeblocks/block/entity/ModBlockEntities.java", "w") as file:
        file.writelines(lines)

    # 14. Copy and modify block entity file
    os.makedirs(f"{src_dir}/block/entity/{file_base_name}", exist_ok=True)
    shutil.copy(f"{template_dir}/block/entity/template_block_entity.java",
                f"{src_dir}/block/entity/{file_base_name}/Figurine{class_name}BlockEntity.java")

    # Modify the block entity file
    with open(f"{src_dir}/block/entity/{file_base_name}/Figurine{class_name}BlockEntity.java", "r") as file:
        content = file.read()
    
    class_name = "".join(word.capitalize() for word in file_base_name.split("_"))
    content = content.replace("<Pokemon name>", class_name)
    content = content.replace("<pokemon name>", file_base_name)

    with open(f"{src_dir}/block/entity/{file_base_name}/Figurine{class_name}BlockEntity.java", "w") as file:
        file.write(content)