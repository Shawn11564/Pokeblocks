import os
import json
from pathlib import Path

def update_json_files(directory_path, regular_scale, gigantic_scale):
    """
    Update the GUI scale values in pokedoll JSON files within the specified directory.
    
    Args:
        directory_path (str): Path to the directory containing JSON files
        regular_scale (list): List containing [x, y, z] scale values for regular pokedolls
        gigantic_scale (list): List containing [x, y, z] scale values for gigantic pokedolls
    """
    # Convert the directory path to a Path object
    path = Path(directory_path)
    
    # Ensure the directory exists
    if not path.exists():
        print(f"Error: Directory '{directory_path}' does not exist.")
        return
    
    # Counters for modified files
    regular_count = 0
    gigantic_count = 0
    skipped_count = 0
    
    # Iterate through all JSON files in the directory
    for json_file in path.glob('*.json'):
        filename = json_file.name.lower()
        
        # Skip files that don't contain "pokedoll"
        if 'pokedoll' not in filename:
            skipped_count += 1
            continue
            
        try:
            # Read the JSON file
            with open(json_file, 'r') as f:
                data = json.load(f)
            
            # Check if the file has the required structure
            if 'display' in data and 'gui' in data['display'] and 'scale' in data['display']['gui']:
                # Determine which scale to use based on filename
                if 'gigantic' in filename:
                    data['display']['gui']['scale'] = gigantic_scale
                    gigantic_count += 1
                    print(f"Updated (Gigantic): {json_file.name}")
                else:
                    data['display']['gui']['scale'] = regular_scale
                    regular_count += 1
                    print(f"Updated (Regular): {json_file.name}")
                
                # Write the updated JSON back to the file
                with open(json_file, 'w') as f:
                    json.dump(data, f, indent=2)
        
        except json.JSONDecodeError:
            print(f"Error: Invalid JSON in file {json_file.name}")
        except Exception as e:
            print(f"Error processing {json_file.name}: {str(e)}")
    
    print(f"\nProcess completed:")
    print(f"- Modified {regular_count} regular pokedoll files")
    print(f"- Modified {gigantic_count} gigantic pokedoll files")
    print(f"- Skipped {skipped_count} non-pokedoll files")

def get_scale_values(doll_type):
    """
    Prompt user for scale values.
    
    Args:
        doll_type (str): Type of pokedoll (for display purposes)
    
    Returns:
        list: [x, y, z] scale values
    """
    print(f"\nEnter {doll_type} pokedoll scale values:")
    try:
        x_scale = float(input(f"{doll_type} X scale: "))
        y_scale = float(input(f"{doll_type} Y scale: "))
        z_scale = float(input(f"{doll_type} Z scale: "))
        return [x_scale, y_scale, z_scale]
    except ValueError:
        print("Error: Please enter valid numbers for scale values.")
        return None

def main():
    # Directory path
    directory = "src/main/resources/assets/pokeblocks/models/item"
    
    # Get scale values for both types
    regular_scale = get_scale_values("Regular")
    if regular_scale is None:
        return
        
    gigantic_scale = get_scale_values("Gigantic")
    if gigantic_scale is None:
        return
    
    # Show summary and confirm
    print("\nScale values to be applied:")
    print(f"Regular pokedolls:  {regular_scale}")
    print(f"Gigantic pokedolls: {gigantic_scale}")
    
    confirm = input("\nProceed with update? (y/n): ").lower()
    
    if confirm == 'y':
        update_json_files(directory, regular_scale, gigantic_scale)
    else:
        print("Operation cancelled.")

if __name__ == "__main__":
    main()