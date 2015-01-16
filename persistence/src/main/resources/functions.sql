INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-57b89fd3-ee03-4c21-9026-bab8fcfe86eb', 'blacklist', 'Blacklist filter', '{"name":"blacklist","dsl":"metafacture","reference":"blacklist","description":"Blacklist filter","parameters":{"entry":{"repeat":true,"type":"repeat","parameters":{"name":{"type":"text"},"value":{"type":"text","optional":true}}}}}', 'Function', '["entry"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-7c4e285d-6ea6-49ad-84fa-f58fcc4c3067', 'case', 'Upper/lower-case transformation.', '{"name":"case","dsl":"metafacture","reference":"case","description":"Upper/lower-case transformation.","parameters":{"to":{"type":"radio","choices":["upper","lower"]},"language":{"type":"text","optional":true}}}', 'Function', '["to","language"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-81d714c9-2dc2-41a5-af19-d3b81ccb8a03', 'compose', 'Add pre- or postfix to a string.', '{"name":"compose","dsl":"metafacture","reference":"compose","description":"Add pre- or postfix to a string.","parameters":{"prefix":{"type":"text","optional":true},"postfix":{"type":"text","optional":true}}}', 'Function', '["prefix","postfix"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-8f5a6192-b8c2-4ba9-97b5-cef74912d1a7', 'constant', 'Sets literal value to a constant.', '{"name":"constant","dsl":"metafacture","reference":"constant","description":"Sets literal value to a constant.","parameters":{"value":{"type":"text"}}}', 'Function', '["value"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-984b58bf-81cf-462b-96c5-61fc90c88357', 'count', 'Returns the an increasing count for each received literal.', '{"name":"count","dsl":"metafacture","reference":"count","description":"Returns the an increasing count for each received literal."}', 'Function', null);
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-9ef5d298-d32f-44e7-a99c-b693c61269d2', 'equals', 'Returns the value only if equality holds.', '{"name":"equals","dsl":"metafacture","reference":"equals","description":"Returns the value only if equality holds.","parameters":{"string":{"type":"text"}}}', 'Function', '["string"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-b3f70182-3998-4ad2-8b4d-a0c32e4adcdf', 'htmlanchor', 'Create an HTML anchor.', '{"name":"htmlanchor","dsl":"metafacture","reference":"htmlanchor","description":"Create an HTML anchor.","parameters":{"prefix":{"type":"text"},"postfix":{"type":"text","optional":true},"title":{"type":"text","optional":true}}}', 'Function', '["prefix","postfix","title"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-c92b5cf5-100b-4974-bbb4-7012e2f2feae', 'isbn', 'ISBN conversion and verification.', '{"name":"isbn","dsl":"metafacture","reference":"isbn","description":"ISBN conversion and verification.","parameters":{"to":{"type":"radio","choices":["isbn13","isbn10","clean"]},"verifyCheckDigit":{"type":"checkbox","optional":true}}}', 'Function', '["to","verifyCheckDigit"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-e4c6c5af-45a5-4530-9a5e-61edf253fb1e', 'lookup', 'Performs a table lookup', '{"name":"lookup","dsl":"metafacture","reference":"lookup","description":"Performs a table lookup","parameters":{"in":{"type":"text","optional":true,"description":"Unique name of the lookup table"},"default":{"type":"text","optional":true,"description":"Value used if no corresponding key is found."},"entry":{"repeat":true,"type":"repeat","parameters":{"name":{"type":"text"},"value":{"type":"text","optional":true}}}}}', 'Function', '["in","default","entry"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-f75a733f-31d4-49db-a0ff-e9d76339ba2c', 'normalize-utf8', null, '{"name":"normalize-utf8","dsl":"metafacture","reference":"normalize-utf8"}', 'Function', null);
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-23664f78-a620-4757-8c4d-3c7163926473', 'not-equals', 'Returns value only if equality does not hold.', '{"name":"not-equals","dsl":"metafacture","reference":"not-equals","description":"Returns value only if equality does not hold.","parameters":{"string":{"type":"text"}}}', 'Function', '["string"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-36fc0b67-1daa-41c6-bb36-0ce3d826e5e8', 'occurrence', 'Filter by number of occurrence.', '{"name":"occurrence","dsl":"metafacture","reference":"occurrence","description":"Filter by number of occurrence.","parameters":{"only":{"type":"text","pattern":"(lessThen |moreThen )?\\\\d+"},"sameEntity":{"type":"checkbox","optional":true}}}', 'Function', '["only","sameEntity"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-72730663-9467-4046-9293-73f14ded1346', 'regexp', 'Extract data based on a regular expression. Syntax corresponds to Java Regular Expressions.', '{"name":"regexp","dsl":"metafacture","reference":"regexp","description":"Extract data based on a regular expression. Syntax corresponds to Java Regular Expressions.","parameters":{"match":{"type":"text"},"format":{"type":"text","optional":true}}}', 'Function', '["match","format"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-9cd2510e-88bb-43c9-9b0b-ae3e398a46e7', 'replace', 'String replace based on a regular expression. Pattern syntax corresponds to Java Regular Expressions.', '{"name":"replace","dsl":"metafacture","reference":"replace","description":"String replace based on a regular expression. Pattern syntax corresponds to Java Regular Expressions.","parameters":{"pattern":{"type":"regexp"},"with":{"type":"text","description":"The replacement"}}}', 'Function', '["pattern","with"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-b6adb851-8770-41f8-9971-1dfb16b68f9c', 'setreplace', 'Relace strings based on a replacement table.', '{"name":"setreplace","dsl":"metafacture","reference":"setreplace","description":"Relace strings based on a replacement table.","parameters":{"map":{"type":"text","optional":true,"description":"Unique name of the replacement table."},"entry":{"repeat":true,"type":"repeat","parameters":{"name":{"type":"text"},"value":{"type":"text","optional":true}}}}}', 'Function', '["map","entry"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-bb39c290-9d50-4629-90f0-15ba8ceb7d8e', 'split', 'Split string based on a regular expression. Pattern syntax corresponds to Java Regular Expressions.', '{"name":"split","dsl":"metafacture","reference":"split","description":"Split string based on a regular expression. Pattern syntax corresponds to Java Regular Expressions.","parameters":{"delimiter":{"type":"text","description":"Regular expression, defining the split"}}}', 'Function', '["delimiter"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-be7aa2d2-26a0-4b0a-9bd5-af6ecce9574b', 'substring', 'Returns a substring', '{"name":"substring","dsl":"metafacture","reference":"substring","description":"Returns a substring","parameters":{"start":{"type":"number","optional":true},"end":{"type":"number","optional":true}}}', 'Function', '["start","end"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-e609cd9b-28b9-435d-9bbf-e25ffcec2e58', 'switch-name-value', 'Switches name and value.', '{"name":"switch-name-value","dsl":"metafacture","reference":"switch-name-value","description":"Switches name and value."}', 'Function', null);
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-e8db2881-a577-44e1-8cb9-24a102aa2e6c', 'trim', 'Trimms the value', '{"name":"trim","dsl":"metafacture","reference":"trim","description":"Trimms the value"}', 'Function', null);
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-f04e38f1-730c-4f4f-b483-d874d4fd1578', 'unique', 'Filters out dublicate literals', '{"name":"unique","dsl":"metafacture","reference":"unique","description":"Filters out dublicate literals","parameters":{"in":{"type":"select","choices":["record","entity"],"optional":true,"description":"Scope of ''sameness'' (experimental)"},"part":{"type":"select","choices":["value","name","name-value"],"optional":true,"description":"Part of the literal is tested for equality"}}}', 'Function', '["in","part"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-0eaa9c66-bb6e-4847-870c-e5708a79b203', 'urlencode', 'Escapes value according to URL encoding rules.', '{"name":"urlencode","dsl":"metafacture","reference":"urlencode","description":"Escapes value according to URL encoding rules."}', 'Function', null);
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-2f9c968f-3478-4f23-b64e-2313f86e11de', 'whitelist', 'Whitelist filter.', '{"name":"whitelist","dsl":"metafacture","reference":"whitelist","description":"Whitelist filter.","parameters":{"map":{"type":"text","optional":true,"description":"Unique name of the replacement table."},"entry":{"repeat":true,"type":"repeat","parameters":{"name":{"type":"text"},"value":{"type":"text","optional":true}}}}}', 'Function', '["map","entry"]');
INSERT INTO FUNCTION (UUID, NAME, DESCRIPTION, FUNCTION_DESCRIPTION, FUNCTION_TYPE, PARAMETERS) VALUES ('Function-45511b07-9126-4796-8a0d-86394711004f', 'concat', 'Collects all received values and concatenates them on record end.', '{"name":"concat","dsl":"metafacture","reference":"concat","description":"Collects all received values and concatenates them on record end.","parameters":{"delimiter":{"type":"text"},"prefix":{"type":"text","optional":true},"postfix":{"type":"text","optional":true}}}', 'Function', '["delimiter","prefix","postfix"]');
