import os
import json
from collections import defaultdict
import jinja2

# Function to create a defaultdict that returns an empty dictionary for missing keys
def make_defaultdict(default_factory):
    return defaultdict(lambda: defaultdict(default_factory))

# Load the translation dictionary from the JSON file
script_dir = os.path.dirname(os.path.abspath(__file__))
json_path = os.path.join(script_dir, 'translation_zashi.json')

with open(json_path, 'r', encoding='utf-8') as file:
    data = json.load(file)
    translation_dict = data.get('translation_dict', {})

# Convert the translation dictionary to a defaultdict
translation_dict = make_defaultdict(lambda: "") | translation_dict

# Project root directory
root_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))

# Function to process each .jinja2 file for a specific language
def process_file(filepath, lang, output_dir):
    with open(filepath, 'r', encoding='utf-8') as file:
        content = file.read()

    template = jinja2.Template(content)
    rendered_content = template.render(translation_dict={k: v.get(lang, '') for k, v in translation_dict.items()})

    os.makedirs(output_dir, exist_ok=True)
    output_filepath = os.path.join(output_dir, 'strings.xml')
    with open(output_filepath, 'w', encoding='utf-8') as file:
        file.write(rendered_content)

# Traverse the directory and process each .jinja2 file for each language
for root, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith('.jinja2'):
            filepath = os.path.join(root, file)
            # Process for the default language (English)
            process_file(filepath, 'en', os.path.dirname(filepath))

            # Process for all other languages
            for lang in translation_dict[next(iter(translation_dict))].keys():
                if lang != 'en':
                    output_dir = os.path.join(os.path.dirname(os.path.dirname(filepath)), f'values-{lang}')
                    process_file(filepath, lang, output_dir)

print("All files have been processed.")
