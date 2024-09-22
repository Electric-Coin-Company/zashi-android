## Design System

The design system contains dimensions, colors and color modes in Figma. Because the amount of these variables makes it 
hard to reflect the changes manually it is a better use of time to automate it.

## AI Automation

The idea is to output the variables into a json and then have AI (i.e. chatgpt) generate kotlin code from it.

## Figma variables into json

Use [Variables Exporter For Dev Mode](https://www.figma.com/community/plugin/1306814436222162088) figma plugin. It 
outputs all variables into a simple structured json.

## Json variables into kotlin

# AI Prompt for color palette

- the more descriptive and exhaustive the prompt is the better

Example Prompt:

```
System:
You are an expert Android Developer and you are able to understand kotlin & json.

Task:
Your task is to convert given json into kotlin code. In the next message you will be given a json file and 
then you will execute your task.
 
Details:
Convert colors represented in json to jetpack compose colors. Create a data class for each nested object, except the 
lowest level one with $type and $value - truncate this lowest level object into $value only. Do not populate them 
by actual colors, just create an exhaustive list of data classes

Notes:
- always output variables in kotlin compatible way
- examples: 'button-background'='buttonBackground' 
```

# AI Prompt for light theme

```
Populate this data structure by the colors from given json. Output a variable 'val LightZashiColorModes = 
ZashiColorModes(...)'.
```

# AI Prompt for dark theme

```
Populate this data structure by the colors from given json. Output a variable 'val DarkZashiColorModes = 
ZashiColorModes(...)'.
```
