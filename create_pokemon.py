import os
import shutil
import json

input_dir = "input"
output_dir = "src/main/resources/assets/pokeblocks"
template_dir = "template"
src_dir = "src/main/java/dev/mrshawn/pokeblocks"

for pokemon_name in os.listdir(input_dir):
    # 0. Rename folders to match the naming convention
    # Remove leading numbers + _
    if pokemon_name[0].isdigit():
        newName = pokemon_name.split("_", 1)[1].lower()
        os.rename(f"{input_dir}/{pokemon_name}", f"{input_dir}/{newName}")
        pokemon_name = newName
    
    # Delete .bbmodel from folder
    for file_name in os.listdir(f"{input_dir}/{pokemon_name}"):
        if file_name.endswith(".bbmodel"):
            os.remove(f"{input_dir}/{pokemon_name}/{file_name}")

    # 1. Copy texture files
    for file_name in os.listdir(f"{input_dir}/{pokemon_name}"):
        if file_name.endswith(".png"):
            shutil.copy(f"{input_dir}/{pokemon_name}/{file_name}", f"{output_dir}/textures/block")

    # 2. Copy geo json file
    for file_name in os.listdir(f"{input_dir}/{pokemon_name}"):
        if file_name.endswith(".geo.json"):
            shutil.copy(f"{input_dir}/{pokemon_name}/{file_name}", f"{output_dir}/geo")

    # 3. Copy and modify blockstate file
    for file_name in ["pokedoll_", "pokedoll_shiny_"]:
        # Copy the template file to create the new file
        shutil.copy(f"{template_dir}/blockstates/template_blockstate.json",
                    f"{output_dir}/blockstates/{file_name}{pokemon_name}.json")

        # Now you can open the newly created file and modify it
        with open(f"{output_dir}/blockstates/{file_name}{pokemon_name}.json", "r") as file:
            content = file.read()

        # Check if the file name starts with 'shiny_'
        if "_shiny" in file_name:
            content = content.replace("<pokemon name>", "shiny_" + pokemon_name)
        else:
            content = content.replace("<pokemon name>", pokemon_name)

        with open(f"{output_dir}/blockstates/{file_name}{pokemon_name}.json", "w") as file:
            file.write(content)

    # 4. Copy and modify block model file
    for file_name in ["pokedoll_", "pokedoll_shiny_"]:
        # Copy the template file to create the new file
        shutil.copy(f"{template_dir}/models/block/template_block_model.json",
                    f"{output_dir}/models/block/{file_name}{pokemon_name}.json")

        # Now you can open the newly created file and modify it
        with open(f"{output_dir}/models/block/{file_name}{pokemon_name}.json", "r") as file:
            content = file.read()

        # Check if the file name starts with 'shiny_'
        if "_shiny" in file_name:
            content = content.replace("<pokemon name>", pokemon_name + "_shiny")
        else:
            content = content.replace("<pokemon name>", pokemon_name)

        with open(f"{output_dir}/models/block/{file_name}{pokemon_name}.json", "w") as file:
            file.write(content)

    # 5. Copy and modify item model file
    for file_name in ["pokedoll_", "pokedoll_shiny_"]:
        # Copy the template file to create the new file
        shutil.copy(f"{template_dir}/models/item/template_item_model.json",
                    f"{output_dir}/models/item/{file_name}{pokemon_name}.json")

        # Now you can open the newly created file and modify it
        with open(f"{output_dir}/models/item/{file_name}{pokemon_name}.json", "r") as file:
            content = file.read()

        # Check if the file name starts with 'shiny_'
        if "_shiny" in file_name:
            content = content.replace("<pokemon name>", "shiny_" + pokemon_name)
        else:
            content = content.replace("<pokemon name>", pokemon_name)

        with open(f"{output_dir}/models/item/{file_name}{pokemon_name}.json", "w") as file:
            file.write(content)

    # 6. Modify en_us.json
    with open(f"{output_dir}/lang/en_us.json", "r+") as file:
        data = json.load(file)
        data["item.pokeblocks.pokedoll_" + pokemon_name] = f"{pokemon_name.capitalize()} Pokedoll"
        data["item.pokeblocks.pokedoll_shiny_" + pokemon_name] = f"Shiny {pokemon_name.capitalize()} Pokedoll"
        data["block.pokeblocks.pokedoll_" + pokemon_name] = f"{pokemon_name.capitalize()} Pokedoll"
        data["block.pokeblocks.pokedoll_shiny_" + pokemon_name] = f"Shiny {pokemon_name.capitalize()} Pokedoll"
        file.seek(0)
        json.dump(data, file, indent=4)
        file.truncate()

    # 7. Modify PokeIDs.java
    with open(f"{src_dir}/constants/PokeIDs.java", "r") as file:
        lines = file.readlines()

    # Find the last line that contains a closing curly bracket
    last_curly_bracket_line = -1
    for i, line in reversed(list(enumerate(lines))):
        if "}" in line:
            last_curly_bracket_line = i
            break

    # If found, insert the new entries before that line
    if last_curly_bracket_line != -1:
        new_lines = [
            f"public static final String POKEDOLL_{pokemon_name.upper()} = \"pokedoll_{pokemon_name}\";\n",
            f"public static final String POKEDOLL_SHINY_{pokemon_name.upper()} = \"pokedoll_shiny_{pokemon_name}\";\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_curly_bracket_line, new_line)

    # Write the file
    with open(f"{src_dir}/constants/PokeIDs.java", "w") as file:
        file.writelines(lines)

    # 8. Modify ResourceConstants.java
    with open(f"{src_dir}/constants/ResourceConstants.java", "r") as file:
        lines = file.readlines()

    # Find the last line that contains a closing curly bracket
    last_curly_bracket_line = -1
    for i, line in reversed(list(enumerate(lines))):
        if "}" in line:
            last_curly_bracket_line = i
            break

    # If found, insert the new entries before that line
    if last_curly_bracket_line != -1:
        new_lines = [
            f"public static final String POKEDOLL_{pokemon_name.upper()}_TEXTURE = \"pokedoll_{pokemon_name}_texture.png\";\n",
            f"public static final String POKEDOLL_SHINY_{pokemon_name.upper()}_TEXTURE = \"pokedoll_{pokemon_name}_shiny_texture.png\";\n",
            f"public static final String POKEDOLL_{pokemon_name.upper()}_MODEL = \"pokedoll_{pokemon_name}.geo.json\";\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_curly_bracket_line, new_line)

    # Write the file
    with open(f"{src_dir}/constants/ResourceConstants.java", "w") as file:
        file.writelines(lines)

    # 9. Modify PokeblocksClient.java
    # Read the file
    with open("src/main/java/dev/mrshawn/pokeblocks/PokeblocksClient.java", "r") as file:
        lines = file.readlines()

    # Find the method and insert the new lines
    method_name = "onInitializeClient"
    new_lines = [
        f"  registerBlockEntityRenderer(\n",
        f"    ModBlockEntities.POKEDOLL_{pokemon_name.upper()}_BLOCK_ENTITY,\n",
        f"    ResourceConstants.POKEDOLL_{pokemon_name.upper()}_MODEL,\n",
        f"    ResourceConstants.POKEDOLL_{pokemon_name.upper()}_TEXTURE\n",
        f"  );\n",
        f"  registerBlockEntityRenderer(\n",
        f"    ModBlockEntities.POKEDOLL_SHINY_{pokemon_name.upper()}_BLOCK_ENTITY,\n",
        f"    ResourceConstants.POKEDOLL_{pokemon_name.upper()}_MODEL,\n",
        f"    ResourceConstants.POKEDOLL_SHINY_{pokemon_name.upper()}_TEXTURE\n",
        f"  );\n",
        f"\n"
    ]

    for i, line in enumerate(lines):
        if method_name in line:
            for new_line in reversed(new_lines):
                lines.insert(i + 1, new_line)
            break

    # Write the file
    with open("src/main/java/dev/mrshawn/pokeblocks/PokeblocksClient.java", "w") as file:
        file.writelines(lines)

    # 10. Modify ModBlocks.java
    # Read the file
    with open("src/main/java/dev/mrshawn/pokeblocks/block/ModBlocks.java", "r") as file:
        lines = file.readlines()

    # Find the last line that matches the pattern of the existing entries
    last_entry_line = -1
    for i, line in enumerate(lines):
        if "public static final Block POKEDOLL_" in line:
            last_entry_line = i + 1

    # If found, insert the new entries after that line
    if last_entry_line != -1:
        new_lines = [
            f"\npublic static final Block POKEDOLL_{pokemon_name.upper()} = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_{pokemon_name.upper()}),",
            f"\nnew PokedollBlock<>(() -> Pokedoll{pokemon_name.capitalize()}BlockEntity.class));",
            f"\npublic static final Block POKEDOLL_SHINY_{pokemon_name.upper()} = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_{pokemon_name.upper()}),",
            f"\nnew PokedollBlock<>(() -> PokedollShiny{pokemon_name.capitalize()}BlockEntity.class));",
            f"\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    # Write the file
    with open("src/main/java/dev/mrshawn/pokeblocks/block/ModBlocks.java", "w") as file:
        file.writelines(lines)

    # 11. Modify ModItems.java
    # Read the file
    with open("src/main/java/dev/mrshawn/pokeblocks/item/ModItems.java", "r") as file:
        lines = file.readlines()

    # Find the last line that matches the pattern of the existing entries
    last_entry_line = -1
    for i, line in enumerate(lines):
        if "public static final Item POKEDOLL_" in line:
            last_entry_line = i + 5

    # If found, insert the new entries after that line
    if last_entry_line != -1:
        new_lines = [
            f"\npublic static final Item POKEDOLL_{pokemon_name.upper()}_BLOCK_ITEM = registerItem(",
            f"\nPokeIDs.POKEDOLL_{pokemon_name.upper()},",
            f"\nModBlocks.POKEDOLL_{pokemon_name.upper()},",
            f"\nResourceConstants.POKEDOLL_{pokemon_name.upper()}_MODEL,",
            f"\nResourceConstants.POKEDOLL_{pokemon_name.upper()}_TEXTURE",
            f"\n);",
            f"\npublic static final Item POKEDOLL_SHINY_{pokemon_name.upper()}_BLOCK_ITEM = registerItem(",
            f"\nPokeIDs.POKEDOLL_SHINY_{pokemon_name.upper()},",
            f"\nModBlocks.POKEDOLL_SHINY_{pokemon_name.upper()},",
            f"\nResourceConstants.POKEDOLL_{pokemon_name.upper()}_MODEL,",
            f"\nResourceConstants.POKEDOLL_SHINY_{pokemon_name.upper()}_TEXTURE",
            f"\n);",
            f"\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    # Write the file
    with open("src/main/java/dev/mrshawn/pokeblocks/item/ModItems.java", "w") as file:
        file.writelines(lines)

    # 12. Modify ModItemGroups.java
    # Read the file
    with open("src/main/java/dev/mrshawn/pokeblocks/item/ModItemGroups.java", "r") as file:
        lines = file.readlines()

    # Find the last line that matches the pattern of the existing entries
    last_entry_line = -1
    for i, line in enumerate(lines):
        if "entries.add(ModBlocks.POKEDOLL_" in line:
            last_entry_line = i

    # If found, insert the new entries after that line
    if last_entry_line != -1:
        new_lines = [
            f"\nentries.add(ModBlocks.POKEDOLL_{pokemon_name.upper()});",
            f"\nentries.add(ModBlocks.POKEDOLL_SHINY_{pokemon_name.upper()});",
            f"\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    # Write the file
    with open("src/main/java/dev/mrshawn/pokeblocks/item/ModItemGroups.java", "w") as file:
        file.writelines(lines)

    # 13. Modify ModBlockEntities.java
    # Read the file
    with open("src/main/java/dev/mrshawn/pokeblocks/block/entity/ModBlockEntities.java", "r") as file:
        lines = file.readlines()

    # Find the last line that matches the pattern of the existing entries
    last_entry_line = -1
    for i, line in enumerate(lines):
        if "public static BlockEntityType<Pokedoll" in line:
            last_entry_line = i

    # If found, insert the new entries after that line
    if last_entry_line != -1:
        new_lines = [
            f"\npublic static BlockEntityType<Pokedoll{pokemon_name.capitalize()}BlockEntity> POKEDOLL_{pokemon_name.upper()}_BLOCK_ENTITY;",
            f"\npublic static BlockEntityType<PokedollShiny{pokemon_name.capitalize()}BlockEntity> POKEDOLL_SHINY_{pokemon_name.upper()}_BLOCK_ENTITY;",
            f"\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    # Find the last line that matches the pattern of the existing entries in the method registerAllBlockEntities
    last_entry_line = -1
    for i, line in enumerate(lines):
        if "POKEDOLL_" in line and "registerBlockEntity(" in line:
            last_entry_line = i + 4

    # If found, insert the new entries after that line
    if last_entry_line != -1:
        new_lines = [
            f"\nPOKEDOLL_{pokemon_name.upper()}_BLOCK_ENTITY = registerBlockEntity(",
            f"\nnew Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_{pokemon_name.upper()}),",
            f"\nFabricBlockEntityTypeBuilder.create(Pokedoll{pokemon_name.capitalize()}BlockEntity::new, ModBlocks.POKEDOLL_{pokemon_name.upper()}),",
            f"\nPokedoll{pokemon_name.capitalize()}BlockEntity.class",
            f"\n);",
            f"\nPOKEDOLL_SHINY_{pokemon_name.upper()}_BLOCK_ENTITY = registerBlockEntity(",
            f"\nnew Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_{pokemon_name.upper()}),",
            f"\nFabricBlockEntityTypeBuilder.create(PokedollShiny{pokemon_name.capitalize()}BlockEntity::new, ModBlocks.POKEDOLL_SHINY_{pokemon_name.upper()}),",
            f"\nPokedollShiny{pokemon_name.capitalize()}BlockEntity.class",
            f"\n);",
            f"\n"
        ]
        for new_line in reversed(new_lines):
            lines.insert(last_entry_line + 1, new_line)

    # Write the file
    with open("src/main/java/dev/mrshawn/pokeblocks/block/entity/ModBlockEntities.java", "w") as file:
        file.writelines(lines)

    # 14. Copy and modify block entity file
    os.makedirs(f"{src_dir}/block/entity/{pokemon_name}", exist_ok=True)
    shutil.copy(f"{template_dir}/block/entity/template_block_entity.java",
                f"{src_dir}/block/entity/{pokemon_name}/Pokedoll{pokemon_name.capitalize()}BlockEntity.java")
    shutil.copy(f"{template_dir}/block/entity/template_block_entity.java",
                f"{src_dir}/block/entity/{pokemon_name}/PokedollShiny{pokemon_name.capitalize()}BlockEntity.java")

    # Open the files and replace <Pokemon name> with the actual pokemon's name
    for file_name in ["Pokedoll", "PokedollShiny"]:
        with open(f"{src_dir}/block/entity/{pokemon_name}/{file_name}{pokemon_name.capitalize()}BlockEntity.java",
                  "r") as file:
            content = file.read()

        # Check if the file name contains 'Shiny'
        if "Shiny" in file_name:
            content = content.replace("<Pokemon name>", "Shiny" + pokemon_name.capitalize())
        else:
            content = content.replace("<Pokemon name>", pokemon_name.capitalize())

        content = content.replace("<pokemon name>", pokemon_name)

        with open(f"{src_dir}/block/entity/{pokemon_name}/{file_name}{pokemon_name.capitalize()}BlockEntity.java",
                  "w") as file:
            file.write(content)