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

import io.hivemind.synchronizer.data.conversion.json.ConverterTestObject.PrivateFieldNoMethods;
import io.hivemind.synchronizer.exception.ConversionException;
import io.hivemind.synchronizer.exception.HiveException;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for json converter
 *
 * @author Patrick-4488
 */
public class JsonConverterTest {

    private static final byte[] RESULT_EMPTY = new byte[0];
    private static final byte[] RESULT_PRIMITIVE = "3252".getBytes();
    private static final ConverterTestObject TEST = new ConverterTestObject();

    // Jackson: null | this: empty byte array
    @Test
    public void toBytes_withNull() throws ConversionException {
        byte[] expectedResult = RESULT_EMPTY;
        JsonConverter converter = new JsonConverter();
        Object input = null;

        byte[] result = converter.toBytes(input);

        assertTrue(Arrays.compare(expectedResult, result) == 0);
    }

    // Jackson: 3252 | this: 3252
    @Test
    public void toBytes_notAnObjectButPrimitive() throws ConversionException {
        byte[] expectedResult = RESULT_PRIMITIVE;
        JsonConverter converter = new JsonConverter();
        Object input = 3252L;

        byte[] result = converter.toBytes(input);

        assertTrue(Arrays.compare(expectedResult, result) == 0);
    }

    // Jackson: "" | this: empty byte array
    @Test
    public void toBytes_objectIsString() throws ConversionException {
        byte[] expectedResult = RESULT_EMPTY;
        JsonConverter converter = new JsonConverter();
        Object input = "";

        byte[] result = converter.toBytes(input);

        assertTrue(Arrays.compare(expectedResult, result) == 0);
    }

    // Jackson: exception NoFields | this: empty byte array
    @Test
    public void toBytes_noFields() throws ConversionException {
        byte[] expectedResult = RESULT_EMPTY;
        JsonConverter converter = new JsonConverter();
        Object input = TEST.new NoFields();

        byte[] result = converter.toBytes(input);

        assertTrue(Arrays.compare(expectedResult, result) == 0);
    }

    // Jackson: exception PrivateFieldNoMethods | this: exception, JSON_CONVERSION_ERROR_CANNOT_ACCESS_FIELD
    @Test
    public void toBytes_privateFieldNoMethods() {
        Class expected = ConversionException.class;
        JsonConverter converter = new JsonConverter();
        Object input = TEST.new PrivateFieldNoMethods();

        HiveException exception = assertThrows(ConversionException.class, () -> converter.toBytes(input));

        assertEquals(expected, exception.getClass());
    }

    // Requires custom deserializer for Jackson to support continue on exception
    // Jackson: PrivateFieldNoMethods | this: {"someField":null}
    @Test
    public void toBytes_privateFieldNoMethodsContinueOnException() throws ConversionException {
        JsonConverter converter = new JsonConverter();
        converter.continueOnException();
        ConverterTestObject.TestObject input = TEST.new PrivateFieldNoMethods();

        byte[] result = converter.toBytes(input);

        assertTrue(Arrays.compare(input.getExpected().getBytes(), result) == 0);
    }

    // Jackson: {"someField":"value"} | this: {"someField":"value"}
    @Test
    public void toBytes_oneFieldNoSetter() throws ConversionException {
        JsonConverter converter = new JsonConverter();
        ConverterTestObject.TestObject input = TEST.new OneFieldNoSetter();

        byte[] result = converter.toBytes(input);

        assertTrue(Arrays.compare(input.getExpected().getBytes(), result) == 0);
    }

    // Jackson: exception OneFieldNoGetter | this: exception, JSON_CONVERSION_ERROR_CANNOT_ACCESS_FIELD
    @Test
    public void toBytes_oneFieldNoGetter() {
        Class expected = ConversionException.class;
        JsonConverter converter = new JsonConverter();
        Object input = TEST.new OneFieldNoGetter();

        HiveException exception = assertThrows(ConversionException.class, () -> converter.toBytes(input));

        assertEquals(expected, exception.getClass());
    }

    // Jackson: {"someField":null} | this: {"someField":null}
    @Test
    public void toBytes_oneStringFieldButIsNull() throws ConversionException {
        JsonConverter converter = new JsonConverter();
        ConverterTestObject.TestObject input = TEST.new OneStringFieldButIsNull();

        byte[] result = converter.toBytes(input);

        assertTrue(Arrays.compare(input.getExpected().getBytes(), result) == 0);
    }

    // Jackson: {"someField":""} | this: {"someField":""}
    @Test
    public void toBytes_oneStringFieldBeingEmpty() throws ConversionException {
        JsonConverter converter = new JsonConverter();
        ConverterTestObject.TestObject input = TEST.new OneStringFieldBeingEmpty();

        byte[] result = converter.toBytes(input);

        assertTrue(Arrays.compare(input.getExpected().getBytes(), result) == 0);
    }

    // Jackson: InvalidDefinitionException | this: exception, JSON_CONVERSION_ERROR_CANNOT_ACCESS_FIELD
    @Test
    public void toBytes_oneStringFieldButGetterIsPrivate() {
        Class expected = ConversionException.class;
        JsonConverter converter = new JsonConverter();
        Object input = TEST.new OneStringFieldButGetterIsPrivate();

        HiveException exception = assertThrows(ConversionException.class, () -> converter.toBytes(input));

        assertEquals(expected, exception.getClass());
    }

    // Jackson: InvalidDefinitionException | this: exception, JSON_CONVERSION_ERROR_CANNOT_ACCESS_FIELD
    @Test
    public void toBytes_oneStringFieldButGetterIsProtected() {
        Class expected = ConversionException.class;
        JsonConverter converter = new JsonConverter();
        Object input = TEST.new OneStringFieldButGetterIsProtected();

        HiveException exception = assertThrows(ConversionException.class, () -> converter.toBytes(input));

        assertEquals(expected, exception.getClass());
    }

    // Jackson: InvalidDefinitionException | this: exception, JSON_CONVERSION_ERROR_CANNOT_ACCESS_FIELD
    @Test
    public void toBytes_oneStringFieldButGetterIsPackagePrivate() {
        Class expected = ConversionException.class;
        JsonConverter converter = new JsonConverter();
        Object input = TEST.new OneStringFieldButGetterIsPackagePrivate();

        HiveException exception = assertThrows(ConversionException.class, () -> converter.toBytes(input));

        assertEquals(expected, exception.getClass());
    }

    // Jackon: {"someField":"value"} | this: {"someField":"value"}
    @Test
    public void toBytes_oneStringFieldFieldBeingInSuperClass() throws ConversionException {
        JsonConverter converter = new JsonConverter();
        ConverterTestObject.TestObject input = TEST.new OneStringFieldFieldBeingInSuperClass();

        byte[] result = converter.toBytes(input);

        assertTrue(Arrays.compare(input.getExpected().getBytes(), result) == 0);
    }

    @Test
    public void toBytes_oneStringFieldFieldAndGetterBeingInSuperClass() {

    }

    @Test
    public void toBytes_oneStringFieldFieldAndSetterBeingInSuperClass() {

    }

    @Test
    public void toBytes_oneStringFieldBeingSetHavingGetterAnnotatedForUnknownField() {

    }

    @Test
    public void toBytes_oneStringFieldBeingSetHavingGetterAnnotatedForField() {

    }

    @Test
    public void toBytes_oneStringFieldBeingSet() {

    }

    @Test
    public void toBytes_arrayBeingEmtpy() {

    }

    @Test
    public void toBytes_arrayWithOneObjectHavingOneStringFieldBeingSet() {

    }

    @Test
    public void toBytes_arrayWithTwoObjectHavingOneStringFieldBeingSet() {

    }

    @Test
    public void toBytes_twoStringFieldsBeingSet() {

    }

    @Test
    public void toBytes_twoStringFieldsOneEmpty() {

    }

    @Test
    public void toBytes_twoStringFieldsOneNull() {

    }

    @Test
    public void toBytes_oneStringOneIntegerField() {

    }

    @Test
    public void toBytes_oneStringOneIntegerOneDoubleField() {

    }

    @Test
    public void toBytes_afterDoubleAppendingLongField() {

    }

    @Test
    public void toBytes_afterLongAppendingFloatField() {

    }

    @Test
    public void toBytes_afterFloatAppendingBooleanField() {

    }

    @Test
    public void toBytes_afterBooleanAppendingStringArray() {

    }

}
