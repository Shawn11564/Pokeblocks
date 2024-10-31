import os
import json
from pathlib import Path

def update_json_files(directory_path, new_scale):
    """
    Update the GUI scale values in all JSON files within the specified directory.
    
    Args:
        directory_path (str): Path to the directory containing JSON files
        new_scale (list): List containing [x, y, z] scale values
    """
    # Convert the directory path to a Path object
    path = Path(directory_path)
    
    # Ensure the directory exists
    if not path.exists():
        print(f"Error: Directory '{directory_path}' does not exist.")
        return
    
    # Counter for modified files
    modified_count = 0
    
    # Iterate through all JSON files in the directory
    for json_file in path.glob('*.json'):
        try:
            # Read the JSON file
            with open(json_file, 'r') as f:
                data = json.load(f)
            
            # Check if the file has the required structure
            if 'display' in data and 'gui' in data['display'] and 'scale' in data['display']['gui']:
                # Update the scale values
                data['display']['gui']['scale'] = new_scale
                
                # Write the updated JSON back to the file
                with open(json_file, 'w') as f:
                    json.dump(data, f, indent=2)
                
                modified_count += 1
                print(f"Updated: {json_file.name}")
        
        except json.JSONDecodeError:
            print(f"Error: Invalid JSON in file {json_file.name}")
        except Exception as e:
            print(f"Error processing {json_file.name}: {str(e)}")
    
    print(f"\nProcess completed. Modified {modified_count} files.")

def main():
    # Directory path
    directory = "src/main/resources/assets/pokeblocks/models/item"
    
    # Get scale values from user
    try:
        print("Enter new scale values:")
        x_scale = float(input("X scale: "))
        y_scale = float(input("Y scale: "))
        z_scale = float(input("Z scale: "))
        
        new_scale = [x_scale, y_scale, z_scale]
        
        # Confirm with user
        print(f"\nNew scale values will be: {new_scale}")
        confirm = input("Proceed with update? (y/n): ").lower()
        
        if confirm == 'y':
            update_json_files(directory, new_scale)
        else:
            print("Operation cancelled.")
            
    except ValueError:
        print("Error: Please enter valid numbers for scale values.")

if __name__ == "__main__":
    main()