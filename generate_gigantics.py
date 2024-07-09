import os
import shutil
import json

src_dir = "src/main/java/dev/mrshawn/pokeblocks"
resources_dir = "src/main/resources"
block_entity_dir = os.path.join(src_dir, "block/entity")

for folder_name in os.listdir(block_entity_dir):
    folder_path = os.path.join(block_entity_dir, folder_name)
    if os.path.isdir(folder_path) and not any(ignore_word in folder_name.lower() for ignore_word in ["figurine", "fishbowl", "trophy", "applin_basket"]):
        pokemon_name = folder_name
        capitalized_pokemon_name = pokemon_name.capitalize()

        folder_path = os.path.join(block_entity_dir, folder_name)
        if "WashingMachine" in folder_name:
            continue

        if not any(file.startswith(f"PokedollGigantic{capitalized_pokemon_name}BlockEntity") for file in os.listdir(folder_path)):
            # 1. Copy and modify block entity file
            os.makedirs(folder_path, exist_ok=True)
            shutil.copy(f"template/block/entity/template_block_entity.java",
                        f"{folder_path}/PokedollGigantic{capitalized_pokemon_name}BlockEntity.java")

            # Open the file and replace <Pokemon name> with the actual pokemon's name
            with open(f"{folder_path}/PokedollGigantic{capitalized_pokemon_name}BlockEntity.java", "r") as file:
                content = file.read()

            content = content.replace("<Pokemon name>", f"Gigantic{capitalized_pokemon_name}")
            content = content.replace("<pokemon name>", pokemon_name)

            with open(f"{folder_path}/PokedollGigantic{capitalized_pokemon_name}BlockEntity.java", "w") as file:
                file.write(content)

            # Create shiny variant
            shiny_capitalized_pokemon_name = "Shiny" + capitalized_pokemon_name
            shutil.copy(f"template/block/entity/template_block_entity.java",
                        f"{folder_path}/PokedollGigantic{shiny_capitalized_pokemon_name}BlockEntity.java")

            with open(f"{folder_path}/PokedollGigantic{shiny_capitalized_pokemon_name}BlockEntity.java", "r") as file:
                shiny_content = file.read()

            shiny_content = shiny_content.replace("<Pokemon name>", f"GiganticShiny{capitalized_pokemon_name}")
            shiny_content = shiny_content.replace("<pokemon name>", pokemon_name)

            with open(f"{folder_path}/PokedollGigantic{shiny_capitalized_pokemon_name}BlockEntity.java", "w") as file:
                file.write(shiny_content)

            # 2. Modify ModBlocks.java
            # Read the file
            with open(f"{src_dir}/block/ModBlocks.java", "r") as file:
                lines = file.readlines()

            # Find the last line that matches the pattern of the existing entries
            last_entry_line = -1
            for i, line in enumerate(lines):
                if "public static final Block POKEDOLL_" in line:
                    last_entry_line = i + 1

            # If found, insert the new entries after that line
            if last_entry_line != -1:
                new_lines = [
                    f"\npublic static final Block GIGANTIC_POKEDOLL_{pokemon_name.upper()} = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_{pokemon_name.upper()}),",
                    f"\nnew PokedollBlock<>(() -> PokedollGigantic{capitalized_pokemon_name}BlockEntity.class));",
                    f"\npublic static final Block GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()} = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()}),",
                    f"\nnew PokedollBlock<>(() -> PokedollGiganticShiny{capitalized_pokemon_name}BlockEntity.class));",
                    f"\n"
                ]
                for new_line in reversed(new_lines):
                    lines.insert(last_entry_line + 1, new_line)

            # Write the file
            with open(f"{src_dir}/block/ModBlocks.java", "w") as file:
                file.writelines(lines)

            # 3. Modify ModBlockEntities.java
            # Read the file
            with open(f"{src_dir}/block/entity/ModBlockEntities.java", "r") as file:
                lines = file.readlines()

            # Find the last line that matches the pattern of the existing entries
            last_entry_line = -1
            for i, line in enumerate(lines):
                if "public static BlockEntityType<Pokedoll" in line:
                    last_entry_line = i

            # If found, insert the new entries after that line
            if last_entry_line != -1:
                new_lines = [
                    f"\npublic static BlockEntityType<PokedollGigantic{capitalized_pokemon_name}BlockEntity> GIGANTIC_POKEDOLL_{pokemon_name.upper()}_BLOCK_ENTITY;",
                    f"\npublic static BlockEntityType<PokedollGiganticShiny{capitalized_pokemon_name}BlockEntity> GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()}_BLOCK_ENTITY;",
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
                    f"\nGIGANTIC_POKEDOLL_{pokemon_name.upper()}_BLOCK_ENTITY = registerBlockEntity(",
                    f"\nnew Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_{pokemon_name.upper()}),",
                    f"\nFabricBlockEntityTypeBuilder.create(PokedollGigantic{capitalized_pokemon_name}BlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_{pokemon_name.upper()}),",
                    f"\nPokedollGigantic{capitalized_pokemon_name}BlockEntity.class",
                    f"\n);",
                    f"\nGIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()}_BLOCK_ENTITY = registerBlockEntity(",
                    f"\nnew Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()}),",
                    f"\nFabricBlockEntityTypeBuilder.create(PokedollGiganticShiny{capitalized_pokemon_name}BlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()}),",
                    f"\nPokedollGiganticShiny{capitalized_pokemon_name}BlockEntity.class",
                    f"\n);",
                    f"\n"
                ]
                for new_line in reversed(new_lines):
                    lines.insert(last_entry_line + 1, new_line)

            # Write the file
            with open(f"{src_dir}/block/entity/ModBlockEntities.java", "w") as file:
                file.writelines(lines)

            # 4. Modify PokeIDs.java
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
                    f"public static final String GIGANTIC_POKEDOLL_{pokemon_name.upper()} = \"gigantic_pokedoll_{pokemon_name}\";\n",
                    f"public static final String GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()} = \"gigantic_pokedoll_shiny_{pokemon_name}\";\n"
                ]
                for new_line in reversed(new_lines):
                    lines.insert(last_curly_bracket_line, new_line)

            # Write the file
            with open(f"{src_dir}/constants/PokeIDs.java", "w") as file:
                file.writelines(lines)

            # 5. Modify PokeblocksClient.java
            # Read the file
            with open(f"{src_dir}/PokeblocksClient.java", "r") as file:
                lines = file.readlines()

            # Find the method and insert the new lines
            method_name = "onInitializeClient"
            new_lines = [
                f"    registerScaledBlockEntityRenderer(\n" \
                f"        ModBlockEntities.GIGANTIC_POKEDOLL_{pokemon_name.upper()}_BLOCK_ENTITY,\n" \
                f"        ResourceConstants.POKEDOLL_{pokemon_name.upper()}_MODEL,\n" \
                f"        ResourceConstants.POKEDOLL_{pokemon_name.upper()}_TEXTURE,\n" \
                f"        SCALE\n" \
                f"    );\n\n",
                f"    registerScaledBlockEntityRenderer(\n" \
                f"        ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()}_BLOCK_ENTITY,\n" \
                f"        ResourceConstants.POKEDOLL_{pokemon_name.upper()}_MODEL,\n" \
                f"        ResourceConstants.POKEDOLL_SHINY_{pokemon_name.upper()}_TEXTURE,\n" \
                f"        SCALE\n" \
                f"    );\n\n"
            ]

            for i, line in enumerate(lines):
                if method_name in line:
                    for new_line in reversed(new_lines):
                        lines.insert(i + 1, new_line)
                    break

            # Write the file
            with open(f"{src_dir}/PokeblocksClient.java", "w") as file:
                file.writelines(lines)

            # 6. Modify ModItems.java
            # Read the file
            with open(f"{src_dir}/item/ModItems.java", "r") as file:
                lines = file.readlines()

            # Find the last line that matches the pattern of the existing entries
            last_entry_line = -1
            for i, line in enumerate(lines):
                if "public static final Item POKEDOLL_" in line:
                    last_entry_line = i + 1

            # If found, insert the new entries after that line
            if last_entry_line != -1:
                new_lines = [
                    f"\npublic static final Item GIGANTIC_POKEDOLL_{pokemon_name.upper()}_BLOCK_ITEM = registerItem(",
                    f"\n    PokeIDs.GIGANTIC_POKEDOLL_{pokemon_name.upper()},",
                    f"\n    ModBlocks.GIGANTIC_POKEDOLL_{pokemon_name.upper()},",
                    f"\n    ResourceConstants.POKEDOLL_{pokemon_name.upper()}_MODEL,",
                    f"\n    ResourceConstants.POKEDOLL_{pokemon_name.upper()}_TEXTURE,",
                    f"\n    DollRarity.GIGANTIC",
                    f"\n);",
                    f"\npublic static final Item GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()}_BLOCK_ITEM = registerItem(",
                    f"\n    PokeIDs.GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()},",
                    f"\n    ModBlocks.GIGANTIC_POKEDOLL_SHINY_{pokemon_name.upper()},",
                    f"\n    ResourceConstants.POKEDOLL_{pokemon_name.upper()}_MODEL,",
                    f"\n    ResourceConstants.POKEDOLL_SHINY_{pokemon_name.upper()}_TEXTURE,",
                    f"\n    DollRarity.SHINY",
                    f"\n);",
                    f"\n"
                ]
                for new_line in reversed(new_lines):
                    lines.insert(last_entry_line + 1, new_line)

            # Write the file
            with open(f"{src_dir}/item/ModItems.java", "w") as file:
                file.writelines(lines)
            
            # 7. Create item model JSON
            os.makedirs(os.path.join(resources_dir, "assets/pokeblocks/models/item"), exist_ok=True)

            # Regular variant
            item_model_json_path = os.path.join(resources_dir, "assets/pokeblocks/models/item", f"gigantic_pokedoll_{pokemon_name}.json")
            with open(item_model_json_path, "w") as file:
                item_model_json = {
                    "parent": f"pokeblocks:block/gigantic_pokedoll_{pokemon_name}",
                    "display": {
                        "thirdperson_righthand": {
                            "rotation": [0, 135, 0],
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "thirdperson_lefthand": {
                            "rotation": [0, 135, 0],
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "firstperson_righthand": {
                            "rotation": [0, 135, 0],
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "firstperson_lefthand": {
                            "rotation": [0, 135, 0],
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "ground": {
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "gui": {
                            "rotation": [0, 135, 0],
                            "scale": [1.0, 1.0, 1.0],
                            "translation": [0, -8, 0]
                        }
                    }
                }
                json.dump(item_model_json, file, indent=2)

            # Shiny variant
            shiny_item_model_json_path = os.path.join(resources_dir, "assets/pokeblocks/models/item", f"gigantic_pokedoll_shiny_{pokemon_name}.json")
            with open(shiny_item_model_json_path, "w") as file:
                shiny_item_model_json = {
                    "parent": f"pokeblocks:block/gigantic_pokedoll_{pokemon_name}",
                    "display": {
                        "thirdperson_righthand": {
                            "rotation": [0, 135, 0],
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "thirdperson_lefthand": {
                            "rotation": [0, 135, 0],
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "firstperson_righthand": {
                            "rotation": [0, 135, 0],
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "firstperson_lefthand": {
                            "rotation": [0, 135, 0],
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "ground": {
                            "scale": [0.75, 0.75, 0.75]
                        },
                        "gui": {
                            "rotation": [0, 135, 0],
                            "scale": [1.0, 1.0, 1.0],
                            "translation": [0, -8, 0]
                        }
                    }
                }
                json.dump(shiny_item_model_json, file, indent=2)

            # 8. Create block model JSON
            os.makedirs(os.path.join(resources_dir, "assets/pokeblocks/models/block"), exist_ok=True)

            # Regular variant
            block_model_json_path = os.path.join(resources_dir, "assets/pokeblocks/models/block", f"gigantic_pokedoll_{pokemon_name}.json")
            with open(block_model_json_path, "w") as file:
                block_model_json = {
                    "credit": "Made with Blockbench",
                    "parent": "builtin/entity",
                    "texture_size": [64, 64],
                    "textures": {
                        "particle": f"pokeblocks:block/pokedoll_{pokemon_name}_texture"
                    }
                }
                json.dump(block_model_json, file, indent=2)

            # Shiny variant
            shiny_block_model_json_path = os.path.join(resources_dir, "assets/pokeblocks/models/block", f"gigantic_pokedoll_shiny_{pokemon_name}.json")
            with open(shiny_block_model_json_path, "w") as file:
                shiny_block_model_json = {
                    "credit": "Made with Blockbench",
                    "parent": "builtin/entity",
                    "texture_size": [64, 64],
                    "textures": {
                        "particle": f"pokeblocks:block/pokedoll_{pokemon_name}_shiny_texture"
                    }
                }
                json.dump(shiny_block_model_json, file, indent=2)
            # 9. Create lang entries in en_us.json
            lang_file_path = os.path.join(resources_dir, "assets/pokeblocks/lang/en_us.json")
            with open(lang_file_path, "r+") as file:
                data = json.load(file)
                data[f"item.pokeblocks.gigantic_pokedoll_{pokemon_name}"] = f"Gigantic {pokemon_name.capitalize()} Pokedoll"
                data[f"block.pokeblocks.gigantic_pokedoll_{pokemon_name}"] = f"Gigantic {pokemon_name.capitalize()} Pokedoll"
                data[f"item.pokeblocks.gigantic_pokedoll_shiny_{pokemon_name}"] = f"Gigantic Shiny {pokemon_name.capitalize()} Pokedoll"
                data[f"block.pokeblocks.gigantic_pokedoll_shiny_{pokemon_name}"] = f"Gigantic Shiny {pokemon_name.capitalize()} Pokedoll"
                file.seek(0)
                json.dump(data, file, indent=4)
                file.truncate()

            # 10. Create blockstate file
            os.makedirs(os.path.join(resources_dir, "assets/pokeblocks/blockstates"), exist_ok=True)

            # Regular variant
            blockstate_file_path = os.path.join(resources_dir, "assets/pokeblocks/blockstates", f"gigantic_pokedoll_{pokemon_name}.json")
            with open(blockstate_file_path, "w") as file:
                blockstate_json = {
                    "variants": {
                        "": {
                            "model": f"pokeblocks:block/gigantic_pokedoll_{pokemon_name}"
                        }
                    }
                }
                json.dump(blockstate_json, file, indent=2)

            # Shiny variant
            shiny_blockstate_file_path = os.path.join(resources_dir, "assets/pokeblocks/blockstates", f"gigantic_pokedoll_shiny_{pokemon_name}.json")
            with open(shiny_blockstate_file_path, "w") as file:
                shiny_blockstate_json = {
                    "variants": {
                        "": {
                            "model": f"pokeblocks:block/gigantic_pokedoll_shiny_{pokemon_name}"
                        }
                    }
                }
                json.dump(shiny_blockstate_json, file, indent=2)