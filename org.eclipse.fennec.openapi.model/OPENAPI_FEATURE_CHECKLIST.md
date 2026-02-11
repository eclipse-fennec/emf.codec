# OpenAPI 3.0.3 Feature Completeness Checklist

Dieses Dokument listet alle fehlenden Features auf, die in `openapi.ecore` implementiert werden müssen, um die OpenAPI 3.0.3 Spezifikation vollständig abzubilden.

---

## 1. Bestehende Klassen erweitern

### 1.1 Contact (Zeile 28-30)
- [ ] `name : EString` - Name der Kontaktperson/Organisation
- [ ] `url : EString` - URL für Kontaktinformationen

### 1.2 Server (Zeile 45-48)
- [ ] `variables : StringToServerVariableMap[*]` - URL-Template-Variablen

### 1.3 OpenApi (Zeile 4-17)
- [ ] `security : Security[*]` - Globale Security Requirements

### 1.4 Path / PathItem (Zeile 49-55)
- [ ] `options : Options` - OPTIONS HTTP-Methode
- [ ] `head : Head` - HEAD HTTP-Methode
- [ ] `patch : Patch` - PATCH HTTP-Methode
- [ ] `trace : Trace` - TRACE HTTP-Methode
- [ ] `ref : EString` (@JsonProperty "$ref") - Externe PathItem-Referenz
- [ ] `summary : EString` - Zusammenfassung für alle Operationen
- [ ] `description : EString` - Beschreibung für alle Operationen
- [ ] `servers : Server[*]` - Alternative Server
- [ ] `parameters : Parameter[*]` - Parameter für alle Operationen

### 1.5 Method / Operation (Zeile 85-96)
- [ ] `externalDocs : Docs` - Erweiterte Dokumentation
- [ ] `callbacks : StringToCallbackMap[*]` - Out-of-band Callbacks
- [ ] `deprecated : EBoolean` - Deprecation-Flag
- [ ] `servers : Server[*]` - Alternative Server

### 1.6 Parameter (Zeile 56-64)
- [ ] `deprecated : EBoolean` - Deprecation-Flag
- [ ] `allowEmptyValue : EBoolean` - Leere Werte erlauben (nur query)
- [ ] `style : EString` - Serialisierungsstil (matrix, label, form, simple, spaceDelimited, pipeDelimited, deepObject)
- [ ] `explode : EBoolean` - Separate Parameter für Arrays/Objekte
- [ ] `allowReserved : EBoolean` - RFC 3986 reservierte Zeichen erlauben
- [ ] `example : EObject` - Beispielwert
- [ ] `examples : StringToExampleMap[*]` - Mehrere Beispiele
- [ ] `content : MimeTypeMap[*]` - Alternative Serialisierung

### 1.7 Response (Zeile 162-166)
- [ ] `headers : StringToHeaderMap[*]` - Response-Header
- [ ] `links : StringToLinkMap[*]` - Operation-Links

### 1.8 MimeType / MediaType (Zeile 167-170)
- [ ] `example : EObject` - Einzelnes Beispiel
- [ ] `examples : StringToExampleMap[*]` - Mehrere Beispiele
- [ ] `encoding : StringToEncodingMap[*]` - Multipart/Form Encoding-Regeln

### 1.9 Property / Schema (Zeile 176-193)
- [ ] `description : EString` - Beschreibung
- [ ] `required : EString[*]` - Liste der Pflichtfelder
- [ ] `nullable : EBoolean` - Null-Werte erlauben
- [ ] `readOnly : EBoolean` - Nur lesbar
- [ ] `writeOnly : EBoolean` - Nur schreibbar
- [ ] `example : EObject` - Beispielwert
- [ ] `deprecated : EBoolean` - Deprecation-Flag
- [ ] `allOf : Property[*]` - Schema-Komposition (AND)
- [ ] `oneOf : Property[*]` - Schema-Komposition (exklusives OR)
- [ ] `anyOf : Property[*]` - Schema-Komposition (OR)
- [ ] `not : Property` - Schema-Negation
- [ ] `discriminator : Discriminator` - Polymorphismus-Unterstützung
- [ ] `xml : XML` - XML-Serialisierungsoptionen
- [ ] `externalDocs : Docs` - Externe Dokumentation
- [ ] `minimum : EBigDecimal` - Minimalwert
- [ ] `maximum : EBigDecimal` - Maximalwert
- [ ] `exclusiveMinimum : EBoolean` - Exklusives Minimum
- [ ] `exclusiveMaximum : EBoolean` - Exklusives Maximum
- [ ] `minLength : EInt` - Minimale String-Länge
- [ ] `maxLength : EInt` - Maximale String-Länge
- [ ] `pattern : EString` - Regex-Pattern
- [ ] `minItems : EInt` - Minimale Array-Länge
- [ ] `maxItems : EInt` - Maximale Array-Länge
- [ ] `minProperties : EInt` - Minimale Anzahl Properties
- [ ] `maxProperties : EInt` - Maximale Anzahl Properties
- [ ] `multipleOf : EBigDecimal` - Vielfaches von

### 1.10 Components (Zeile 122-132)
- [ ] `responses : StringToResponseMap[*]` - Wiederverwendbare Responses
- [ ] `parameters : StringToParameterMap[*]` - Wiederverwendbare Parameter
- [ ] `examples : StringToExampleMap[*]` - Wiederverwendbare Beispiele
- [ ] `requestBodies : StringToRequestBodyMap[*]` - Wiederverwendbare RequestBodies
- [ ] `headers : StringToHeaderMap[*]` - Wiederverwendbare Header
- [ ] `links : StringToLinkMap[*]` - Wiederverwendbare Links
- [ ] `callbacks : StringToCallbackMap[*]` - Wiederverwendbare Callbacks

### 1.11 SecuritySchemes (Zeile 133-152)
- [ ] Refactoring: Map-basiert statt hardcodierte Felder
- [ ] `schemes : StringToSecuritySchemeMap[*]` - Dynamische Security Schemes

---

## 2. Neue Klassen erstellen

### 2.1 ServerVariable (NEU)
```
EClass ServerVariable {
  enum : EString[*]        // Erlaubte Werte
  default : EString        // REQUIRED - Standardwert
  description : EString    // Beschreibung
}
```

### 2.2 Neue HTTP-Methoden (NEU)
```
EClass Options extends Method {}
EClass Head extends Method {}
EClass Patch extends Method {
  requestBody : RequestBody
}
EClass Trace extends Method {}
```

### 2.3 Callback (NEU)
```
EClass Callback {
  // Map von Expression-String zu PathItem
  paths : StringToPathMap[*]
}
```

### 2.4 Example (NEU)
```
EClass Example {
  summary : EString           // Zusammenfassung
  description : EString       // Beschreibung
  value : EObject             // Eingebetteter Beispielwert
  externalValue : EString     // URL zu externem Beispiel
}
```

### 2.5 Link (NEU)
```
EClass Link {
  operationRef : EString      // Referenz zur Operation
  operationId : EString       // Operation-ID
  parameters : StringToAnyMap[*]  // Parameter-Mapping
  requestBody : EObject       // Request Body Expression
  description : EString       // Beschreibung
  server : Server             // Server-Override
}
```

### 2.6 Header (NEU)
```
EClass Header {
  // Wie Parameter, aber ohne 'name' und 'in'
  description : EString
  required : EBoolean
  deprecated : EBoolean
  allowEmptyValue : EBoolean
  style : EString
  explode : EBoolean
  allowReserved : EBoolean
  schema : Property
  example : EObject
  examples : StringToExampleMap[*]
  content : MimeTypeMap[*]
}
```

### 2.7 Encoding (NEU)
```
EClass Encoding {
  contentType : EString       // Content-Type für Property
  headers : StringToHeaderMap[*]  // Zusätzliche Header
  style : EString             // Serialisierungsstil
  explode : EBoolean          // Array/Object separieren
  allowReserved : EBoolean    // Reservierte Zeichen erlauben
}
```

### 2.8 Discriminator (NEU)
```
EClass Discriminator {
  propertyName : EString      // REQUIRED - Diskriminator-Property
  mapping : StringToStringMap[*]  // Schema-Mapping
}
```

### 2.9 XML (NEU)
```
EClass XML {
  name : EString              // XML-Elementname
  namespace : EString         // XML-Namespace URI
  prefix : EString            // Namespace-Präfix
  attribute : EBoolean        // Als Attribut rendern
  wrapped : EBoolean          // Array-Items wrappen
}
```

### 2.10 SecurityScheme (NEU - Refactoring)
```
EClass SecurityScheme {
  type : EString              // REQUIRED: apiKey, http, oauth2, openIdConnect
  description : EString       // Beschreibung
  name : EString              // Für apiKey: Name
  in : EString                // Für apiKey: header, query, cookie
  scheme : EString            // Für http: basic, bearer, etc.
  bearerFormat : EString      // Für http/bearer: Format
  flows : OAuthFlows          // Für oauth2: Flow-Konfiguration
  openIdConnectUrl : EString  // Für openIdConnect: Discovery-URL
}
```

### 2.11 OAuthFlows (NEU)
```
EClass OAuthFlows {
  implicit : OAuthFlow        // Implicit Flow
  password : OAuthFlow        // Password Flow
  clientCredentials : OAuthFlow  // Client Credentials Flow
  authorizationCode : OAuthFlow  // Authorization Code Flow
}
```

### 2.12 OAuthFlow (NEU)
```
EClass OAuthFlow {
  authorizationUrl : EString  // Authorization Endpoint
  tokenUrl : EString          // Token Endpoint
  refreshUrl : EString        // Refresh Endpoint
  scopes : StringToStringMap[*]  // Verfügbare Scopes
}
```

---

## 3. Neue Map-Klassen erstellen

- [ ] `StringToServerVariableMap` (key: EString, value: ServerVariable)
- [ ] `StringToCallbackMap` (key: EString, value: Callback)
- [ ] `StringToExampleMap` (key: EString, value: Example)
- [ ] `StringToLinkMap` (key: EString, value: Link)
- [ ] `StringToHeaderMap` (key: EString, value: Header)
- [ ] `StringToEncodingMap` (key: EString, value: Encoding)
- [ ] `StringToSecuritySchemeMap` (key: EString, value: SecurityScheme)
- [ ] `StringToParameterMap` (key: EString, value: Parameter)
- [ ] `StringToRequestBodyMap` (key: EString, value: RequestBody)
- [ ] `StringToStringMap` (key: EString, value: EString)
- [ ] `StringToAnyMap` (key: EString, value: EObject)

---

## 4. Enumerationen hinzufügen (Optional aber empfohlen)

### 4.1 ParameterStyle
```
EEnum ParameterStyle {
  MATRIX = 0
  LABEL = 1
  FORM = 2
  SIMPLE = 3
  SPACE_DELIMITED = 4
  PIPE_DELIMITED = 5
  DEEP_OBJECT = 6
}
```

### 4.2 ParameterLocation
```
EEnum ParameterLocation {
  QUERY = 0
  HEADER = 1
  PATH = 2
  COOKIE = 3
}
```

### 4.3 SecuritySchemeType
```
EEnum SecuritySchemeType {
  API_KEY = 0
  HTTP = 1
  OAUTH2 = 2
  OPEN_ID_CONNECT = 3
}
```

### 4.4 SchemaType
```
EEnum SchemaType {
  STRING = 0
  NUMBER = 1
  INTEGER = 2
  BOOLEAN = 3
  ARRAY = 4
  OBJECT = 5
}
```

---

## 5. Priorisierung

### Priorität 1 (Häufig verwendet)
1. Schema-Komposition (`allOf`, `oneOf`, `anyOf`)
2. Fehlende HTTP-Methoden (`patch`, `options`, `head`)
3. `Header` Objekt
4. Schema-Validierung (min/max, pattern, etc.)
5. `nullable` Flag

### Priorität 2 (Wichtig für komplexe APIs)
1. `Discriminator` für Polymorphismus
2. `Example` Objekt
3. OAuth2 Flows
4. `Link` Objekt
5. Components-Erweiterungen

### Priorität 3 (Vollständigkeit)
1. `Callback` Objekt
2. `Encoding` Objekt
3. `XML` Objekt
4. `ServerVariable`
5. Alle Enumerationen

---

## Geschätzte Vollständigkeit nach Implementierung

| Phase | Vollständigkeit |
|-------|-----------------|
| Aktuell | ~35-40% |
| Nach Priorität 1 | ~65-70% |
| Nach Priorität 2 | ~85-90% |
| Nach Priorität 3 | ~98-100% |
