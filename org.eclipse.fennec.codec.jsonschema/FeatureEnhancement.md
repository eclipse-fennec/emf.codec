# Feature Enhancement

## Description

As a user of the codec library, I want to be able to pass some format specific options via load/save or via model annotations. These options, since they are format specific, do not have a constant in the PropertyConfig and should not have one. However, they should be named with a `codec.` prefix so that they can be easily spotted.

They should, ideally, when creating the `EffectiveCodecConfig` end up in a `customProperty` map so they are then accessible via the `EffectiveCodecConfig` and in the `CodecWriterContext`. 

So, all the properties with a `codec.` prefix that do not have a match in any other `EffectiveCodecConfig` end property, should end up in the `customProperty` map.

Example:

User defined a format specific property for jsonschema: 

```
public final static String OPTION_ALL_FIELDS_REQUIRED = "codec.ALL_FIELDS_REQUIRED"
```

Then he saves a resource with options `Map.of(OPTION_ALL_FIELDS_REQUIRED, true)`. 

This should end up in the `EffectiveCodecConfig#customPropertyMap`.

Then, in the `EClassValueWriter` in the jsonschema I can access them via  `CodecWriterContext`:

```
ctx.getConfig().getCustomProperty()
```