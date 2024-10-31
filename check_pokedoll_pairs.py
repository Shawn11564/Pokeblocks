import os
from pathlib import Path
from collections import defaultdict

def find_unmatched_pokedolls(directory_path):
    """
    Find pokedoll files that don't have matching gigantic versions.
    
    Args:
        directory_path (str): Path to the directory containing JSON files
    """
    # Convert the directory path to a Path object
    path = Path(directory_path)
    
    # Ensure the directory exists
    if not path.exists():
        print(f"Error: Directory '{directory_path}' does not exist.")
        return
    
    # Dictionary to store regular and gigantic pokedoll files
    pokedoll_files = defaultdict(lambda: {'regular': None, 'gigantic': None})
    
    # Process all JSON files
    for json_file in path.glob('*.json'):
        filename = json_file.name.lower()
        
        # Skip files that don't contain "pokedoll"
        if 'pokedoll' not in filename:
            continue
            
        # Extract the pokemon name from the filename
        if 'gigantic' in filename:
            # Remove 'gigantic_pokedoll_' prefix and '.json' suffix
            pokemon_name = filename.replace('gigantic_pokedoll_', '').replace('.json', '')
            pokedoll_files[pokemon_name]['gigantic'] = json_file.name
        else:
            # Remove 'pokedoll_' prefix and '.json' suffix
            pokemon_name = filename.replace('pokedoll_', '').replace('.json', '')
            pokedoll_files[pokemon_name]['regular'] = json_file.name
    
    # Print results
    print("\nAnalysis Results:")
    print("=" * 50)
    
    # Find missing matches
    missing_gigantic = []
    missing_regular = []
    complete_pairs = []
    
    for pokemon, files in pokedoll_files.items():
        if files['regular'] and not files['gigantic']:
            missing_gigantic.append((pokemon, files['regular']))
        elif files['gigantic'] and not files['regular']:
            missing_regular.append((pokemon, files['gigantic']))
        else:
            complete_pairs.append((pokemon, files['regular'], files['gigantic']))
    
    # Print complete pairs
    print("\nComplete Pairs:")
    print("-" * 50)
    for pokemon, regular, gigantic in sorted(complete_pairs):
        print(f"Pokemon: {pokemon}")
        print(f"  Regular:  {regular}")
        print(f"  Gigantic: {gigantic}")
        print()
    
    # Print missing gigantic versions
    print("\nMissing Gigantic Versions:")
    print("-" * 50)
    if missing_gigantic:
        for pokemon, regular_file in sorted(missing_gigantic):
            print(f"Pokemon: {pokemon}")
            print(f"  Regular file: {regular_file}")
            print(f"  Expected gigantic file: gigantic_pokedoll_{pokemon}.json")
            print()
    else:
        print("None - All regular pokedolls have matching gigantic versions")
    
    # Print missing regular versions
    print("\nMissing Regular Versions:")
    print("-" * 50)
    if missing_regular:
        for pokemon, gigantic_file in sorted(missing_regular):
            print(f"Pokemon: {pokemon}")
            print(f"  Gigantic file: {gigantic_file}")
            print(f"  Expected regular file: pokedoll_{pokemon}.json")
            print()
    else:
        print("None - All gigantic pokedolls have matching regular versions")
    
    # Print summary
    print("\nSummary:")
    print("-" * 50)
    print(f"Total Pokemon: {len(pokedoll_files)}")
    print(f"Complete Pairs: {len(complete_pairs)}")
    print(f"Missing Gigantic Versions: {len(missing_gigantic)}")
    print(f"Missing Regular Versions: {len(missing_regular)}")

def main():
    # Directory path
    directory = "src/main/resources/assets/pokeblocks/models/item"
    
    find_unmatched_pokedolls(directory)

if __name__ == "__main__":
    main()