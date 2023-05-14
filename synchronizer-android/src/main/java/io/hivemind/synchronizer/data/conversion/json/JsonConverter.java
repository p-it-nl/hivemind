/**
 * Copyright (c) p-it
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.hivemind.synchronizer.data.conversion.json;

import io.hivemind.synchronizer.data.conversion.ResourceConverter;
import io.hivemind.synchronizer.HiveResource;
import io.hivemind.synchronizer.exception.ConversionException;
import io.hivemind.synchronizer.exception.HiveCeption;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts to and from json
 *
 * @author Patrick-4488
 */
public class JsonConverter extends ResourceConverter {

    private final StringBuilder sb;
    /**
     * @Override public byte[] toBytes(final Object... objects) { try (
     * ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
     * baos.write(B_OPEN); for (Object obj : objects) { Class<?> clazz =
     * obj.getClass(); Map<String, Map.Entry<Field, Integer>> mappedFields =
     * mapFields(clazz); Map<String, Method> mappedMethods = mapMethods(baos,
     * mappedFields, clazz.getMethods(), obj); readFields(mappedMethods, baos,
     * mappedFields, obj); } baos.write(B_CLOSE); } catch (IOException ex) {
     * LOGGER.error("Error occurred while writing object to json bytes", ex); }
     *
     * return new byte[0]; }
     */
    private static final String GET = "get";
    private static final byte[] B_OPEN = "{".getBytes();
    private static final byte[] B_CLOSE = "}".getBytes();
    private static final byte[] COMMA = ",".getBytes();
    private static final byte[] D_QUOTE = "\"".getBytes();

    private static final String PACKAGE_JAVA_LANG = "java.lang";
    private static final String DEFAULT_FIELD = "this";
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);

    public JsonConverter() {
        sb = new StringBuilder();
    }

    /**
     * Converts object to json as byte array.WHEN DONE, COMPARE SPEED WITH
     * JACKSON<br>
     * If slower, refactor and test again, if faster, celebrate
     *
     * @param objects the objects to convert
     * @return the byte array filled with json or empty
     * @throws io.hivemind.synchronizer.exception.ConversionException when
     * conversion runs in a exception, to ignore exceptions call
     * .continueOnException()
     */
    @Override
    public byte[] toBytes(final Object... objects) throws ConversionException {
        if (objects[0] != null) {
            try ( ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                int last = objects.length - 1;
                for (int i = 0; i < objects.length; i++) {
                    Object obj = objects[i];
                    Class<?> clazz = obj.getClass();
                    if (!isPartOfJavaLang(clazz)) {
                        Map<String, Map.Entry<Field, Integer>> mappedFields = mapFields(clazz);
                        if (!mappedFields.isEmpty()) {
                            String[] fieldResults = new String[mappedFields.size()];

                            Map<String, Method> mappedMethods = mapMethods(fieldResults, mappedFields, clazz.getMethods(), obj);
                            readFields(mappedMethods, fieldResults, mappedFields, obj);

                            writeResult(fieldResults, (i == last), baos);
                        } else {
                            LOGGER.debug("Object contains no fields, nothing to convert");
                        }
                    } else {
                        LOGGER.debug("Object is part of java.lang and probably primitive "
                                + "or otherwise not eligible for conversion as object, will convert as value");
                        baos.write(obj.toString().getBytes());
                    }
                }

                return baos.toByteArray();
            } catch (IOException ex) {
                LOGGER.error("Error occurred while writing object to json bytes", ex);
            }
        }

        return new byte[0];
    }

    @Override
    public List<HiveResource> fromBytes(final byte[] data) {

        return new ArrayList<>();
    }

    private Map<String, Map.Entry<Field, Integer>> mapFields(final Class<?> clazz) {
        Map<String, Map.Entry<Field, Integer>> result = new HashMap<>();

        Field[][] allFields = {clazz.getDeclaredFields(), clazz.getSuperclass().getDeclaredFields()};
        int fieldNo = 0;
        for (int i = 0; i < allFields.length; i++) {
            Field[] fields = allFields[i];
            for (Field field : fields) {
                String name = field.getName();
                if (!name.startsWith(DEFAULT_FIELD)) {
                    result.put(name, new SimpleEntry<>(field, fieldNo));
                    fieldNo++;
                } else {
                    LOGGER.debug("Field is default Java field e.g. this$0");
                }
            }
        }

        return result;
    }

    private Map<String, Method> mapMethods(
            final String[] results,
            final Map<String, Map.Entry<Field, Integer>> mappedFields,
            final Method[] methods,
            final Object obj) {
        Map<String, Method> mappedMethods = new HashMap<>();
        for (Method method : methods) {
            try {
                if (method.isAnnotationPresent(RepresentsGetterForField.class)) {
                    RepresentsGetterForField represented = method.getAnnotation(RepresentsGetterForField.class);
                    String field = represented.field();
                    readValue(mappedFields.get(field), method, results, obj);
                    mappedFields.remove(field);
                    // throw exception if this goes wrong?
                } else {
                    mappedMethods.put(method.getName(), method);
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                if (!continueOnException) {
                    System.out.println("should throw");
                    System.out.println(ex);
                } else {
                    LOGGER.warn("Exception while converting", ex);
                }
            }
        }

        return mappedMethods;
    }

    private String getDefaultGetter(final String fieldName) {
        sb.setLength(0);
        sb.append(GET);
        sb.append(fieldName.substring(0, 1).toUpperCase());
        sb.append(fieldName.substring(1));

        return sb.toString();
    }

    private void readValue(
            final Map.Entry<Field, Integer> fieldEntry,
            final Method method,
            final String[] results,
            final Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object value = null;
        if (method != null) {
            value = method.invoke(obj, (Object[]) null);
            // if  value has fields, go deeper
            if (value instanceof Map) {
                System.out.println("is map");
            }

            if (value instanceof List) {
                System.out.println("is list");
            }

            if (value instanceof Map.Entry) {
                System.out.println("is entry");
            }
            if (value != null) {
                System.out.println(value.getClass().getPackage());
                System.out.println(value.getClass().getSimpleName());
            }
        }

        concatanate(fieldEntry.getKey().getName(), value);
        results[fieldEntry.getValue()] = "\"" + fieldEntry.getKey().getName() + "\":" + (value != null ? "\"" + value + "\"" : value) + "";
    }

    private String concatanate(final String field, final Object value) {
        sb.setLength(0);
        sb.append(D_QUOTE);
        sb.append(field);
        sb.append(D_QUOTE);
        if (value == null) {
            sb.append(value);
        } else {
            sb.append(D_QUOTE);
            sb.append(value);
            sb.append(D_QUOTE);
        }

        return sb.toString();
    }

    private void writeResult(final String[] results, final boolean lastObject, final ByteArrayOutputStream baos) throws IOException {
        baos.write(B_OPEN);
        for (int i = 0; i < results.length; i++) {
            if (results[i] != null) {
                baos.write(results[i].getBytes());
                if (i < (results.length - 1)) {
                    baos.write(COMMA);
                }
            } else {
                LOGGER.debug("Result was expected, but not set {}", (continueOnException
                        ? "this happend due to an exception being ignored"
                        : "this should not have happened, something went wrong with writing results"));
            }
        }
        baos.write(B_CLOSE);
        if (!lastObject) {
            baos.write(COMMA);
        }
    }

    private void readFields(
            final Map<String, Method> mappedMethods,
            final String[] results,
            final Map<String, Map.Entry<Field, Integer>> mappedFields,
            final Object obj) throws ConversionException {
        for (Map.Entry<String, Map.Entry<Field, Integer>> fieldEntry : mappedFields.entrySet()) {
            try {
                String defaultGetter = getDefaultGetter(fieldEntry.getKey());
                if (mappedMethods.containsKey(defaultGetter)) {
                    Method method = mappedMethods.get(defaultGetter);
                    method.canAccess(obj);

                    readValue(fieldEntry.getValue(), method, results, obj);
                } else if (!continueOnException) {
                    String name = fieldEntry.getKey();
                    throw new ConversionException(HiveCeption.JSON_CONVERSION_ERROR_CANNOT_ACCESS_FIELD, name, getDefaultGetter(name), name);
                } else {
                    readValue(fieldEntry.getValue(), null, results, obj);
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                if (!continueOnException) {
                    System.out.println("should throw");
                    System.out.println(ex);
                } else {
                    LOGGER.warn("Exception while converting", ex);
                }
            }
        }
    }

    private boolean isPartOfJavaLang(final Class<?> clazz) {
        return PACKAGE_JAVA_LANG.equals(clazz.getPackageName());
    }
}
