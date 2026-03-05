# Feature Enhancement

## Description

I want to be able, given a reference to an abstract EClass to serialize that in a jsonschema with the oneOf attribute. 

This might be already possible specifying an annotation in the model, but I want to be able to overwrite the behaviour from the load/save options.

## Example

I have an EMF EClass SearchResult of type AbstractSearchResult. Then I have two concrete classes that inherits from AbstractSearchResult, let's say SearchResultA and SearchResultB. I want the jsonschema to have a oneOf property with a reference to SearchResultA and SearchResultB and their definitions in the $defs section.