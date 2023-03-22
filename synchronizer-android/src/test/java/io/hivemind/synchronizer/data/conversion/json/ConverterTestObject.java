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

/**
 * Objects to test conversion with
 *
 * TODO add option to ignore a field
 *
 * @author Patrick-4488
 */
public class ConverterTestObject {

    public static final String STRING_VALUE = "value";
    public static final int INTEGER_VALUE = 8;
    public static final double DOUBLE_VALUE = 8.8;
    public static final long LONG_VALUE = 88l;
    public static final float FLOAT_VALUE = 8.8f;
    public static final boolean BOOLEAN_VALUE = true;

    public class NoFields implements TestObject {

        public void someSetter(final String something) {
            String anotherSomething = something;
        }

        public String someGetter() {
            return "";
        }

        @Override
        public String getExpected() {
            return null; // Expecting exception, unless exceptions are ignored
        }
    }

    public class PrivateFieldNoMethods implements TestObject {

        private String someField;

        @Override
        public String getExpected() {
            return "{\"someField\":" + someField + "}";
        }
    }

    public class OneFieldNoSetter implements TestObject {

        private final String someField = STRING_VALUE;

        public String getSomeField() {
            return someField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\"}";
        }
    }

    public class OneFieldNoGetter implements TestObject {

        private String someField = STRING_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        @Override
        public String getExpected() {
            return null; // Expecting exception, unless exceptions are ignored
        }
    }

    public class OneStringFieldButIsNull implements TestObject {

        private String someField;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":" + someField + "}";
        }
    }

    public class OneStringFieldBeingEmpty implements TestObject {

        private String someField = "";

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"\"}";
        }
    }

    public class OneStringFieldButGetterIsPrivate implements TestObject {

        private String someField = STRING_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        private String getSomeField() {
            return someField;
        }

        @Override
        public String getExpected() {
            return null; // Expecting exception, unless exceptions are ignored
        }
    }

    public class OneStringFieldButGetterIsProtected implements TestObject {

        private String someField = STRING_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        protected String getSomeField() {
            return someField;
        }

        @Override
        public String getExpected() {
            return null; // Expecting exception, unless exceptions are ignored
        }
    }

    public class OneStringFieldButGetterIsPackagePrivate implements TestObject {

        private String someField = STRING_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        String getSomeField() {
            return someField;
        }

        @Override
        public String getExpected() {
            return null; // Expecting exception, unless exceptions are ignored
        }
    }

    public class OneStringFieldFieldBeingInSuperClass extends FieldClass implements TestObject {

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\"}";
        }
    }

    public abstract class FieldClass {

        protected String someField = STRING_VALUE;

    }

    public abstract class GetterFieldClass {

        protected String someField = STRING_VALUE;

        public String getSomeField() {
            return this.someField;
        }
    }

    public abstract class SetterFieldClass {

        protected String someField = STRING_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }
    }

    public class OneStringFieldFieldAndGetterBeingInSuperClass extends GetterFieldClass implements TestObject {

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\"}";
        }
    }

    public class OneStringFieldFieldAndSetterBeingInSuperClass extends SetterFieldClass implements TestObject {

        public String getSomeField() {
            return this.someField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\"}";
        }
    }

    public class OneStringFieldBeingSet implements TestObject {

        private String someField = STRING_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\"}";
        }
    }

    public class OneStringFieldBeingSetHavingGetterAnnotatedForUnknownField implements TestObject {

        private String someField = STRING_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        @RepresentsGetterForField(field = "unknown")
        public String randomGetter() {
            return someField;
        }

        @Override
        public String getExpected() {
            return null; // Expecting exception, unless exceptions are ignored
        }
    }

    public class OneStringFieldBeingSetHavingGetterAnnotatedForField implements TestObject {

        private String someField = STRING_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        @RepresentsGetterForField(field = "someField")
        public String randomGetter() {
            return someField;
        }

        @Override
        public String getExpected() {
            return null; // Expecting exception, unless exceptions are ignored
        }
    }

    public class TwoStringFieldsBeingSet implements TestObject {

        private String someField = STRING_VALUE;
        private String anotherField = STRING_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setAnotherField(final String anotherField) {
            this.anotherField = anotherField;
        }

        public String getAnotherField() {
            return anotherField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\",\"anotherField\":\"" + anotherField + "\"}";
        }
    }

    public class TwoStringFieldsOneEmpty implements TestObject {

        private String someField = STRING_VALUE;
        private String anotherField = "";

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setAnotherField(final String anotherField) {
            this.anotherField = anotherField;
        }

        public String getAnotherField() {
            return anotherField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\",\"anotherField\":\"" + anotherField + "\"}";
        }
    }

    public class TwoStringFieldsOneNull implements TestObject {

        private String someField = STRING_VALUE;
        private String anotherField = null;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setAnotherField(final String anotherField) {
            this.anotherField = anotherField;
        }

        public String getAnotherField() {
            return anotherField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\"}";
        }
    }

    public class OneStringOneIntegerField implements TestObject {

        private String someField = STRING_VALUE;
        private int anotherField = INTEGER_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setAnotherField(final int anotherField) {
            this.anotherField = anotherField;
        }

        public int getAnotherField() {
            return anotherField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\",\"anotherField\":\"" + anotherField + "\"}";
        }
    }

    public class OneStringOneIntegerOneDoubleField implements TestObject {

        private String someField = STRING_VALUE;
        private int anotherField = INTEGER_VALUE;
        private double thirdField = DOUBLE_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setAnotherField(final int anotherField) {
            this.anotherField = anotherField;
        }

        public int getAnotherField() {
            return anotherField;
        }

        public void setThirdField(final double thirdField) {
            this.thirdField = thirdField;
        }

        public double getThirdField() {
            return thirdField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\",\"anotherField\":\"" + anotherField + "\",\"thirdField\":\"" + thirdField + "\"}";
        }
    }

    public class AfterDoubleAppendingLongField implements TestObject {

        private String someField = STRING_VALUE;
        private int anotherField = INTEGER_VALUE;
        private double thirdField = DOUBLE_VALUE;
        private long longField = LONG_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setAnotherField(final int anotherField) {
            this.anotherField = anotherField;
        }

        public int getAnotherField() {
            return anotherField;
        }

        public void setThirdField(final double thirdField) {
            this.thirdField = thirdField;
        }

        public double getThirdField() {
            return thirdField;
        }

        public void setLongField(final long longField) {
            this.longField = longField;
        }

        public long getLongField() {
            return longField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\",\"anotherField\":\"" + anotherField + "\",\"thirdField\":\"" + thirdField + "\",\"longField\":\"" + longField + "\"}";
        }
    }

    public class AfterLongAppendingFloatField implements TestObject {

        private String someField = STRING_VALUE;
        private int anotherField = INTEGER_VALUE;
        private double thirdField = DOUBLE_VALUE;
        private long longField = LONG_VALUE;
        private float floatField = FLOAT_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setAnotherField(final int anotherField) {
            this.anotherField = anotherField;
        }

        public int getAnotherField() {
            return anotherField;
        }

        public void setThirdField(final double thirdField) {
            this.thirdField = thirdField;
        }

        public double getThirdField() {
            return thirdField;
        }

        public void setLongField(final long longField) {
            this.longField = longField;
        }

        public long getLongField() {
            return longField;
        }

        public void setFloatField(final float floatField) {
            this.floatField = floatField;
        }

        public float getFloatField() {
            return floatField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\",\"anotherField\":\"" + anotherField + "\",\"thirdField\":\"" + thirdField + "\",\"longField\":\"" + longField + "\",\"floatField\":\"" + floatField + "\"}";
        }
    }

    public class AfterFloatAppendingBooleanField implements TestObject {

        private String someField = STRING_VALUE;
        private int anotherField = INTEGER_VALUE;
        private double thirdField = DOUBLE_VALUE;
        private long longField = LONG_VALUE;
        private float floatField = FLOAT_VALUE;
        private boolean booleanField = BOOLEAN_VALUE;

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setAnotherField(final int anotherField) {
            this.anotherField = anotherField;
        }

        public int getAnotherField() {
            return anotherField;
        }

        public void setThirdField(final double thirdField) {
            this.thirdField = thirdField;
        }

        public double getThirdField() {
            return thirdField;
        }

        public void setLongField(final long longField) {
            this.longField = longField;
        }

        public long getLongField() {
            return longField;
        }

        public void setFloatField(final float floatField) {
            this.floatField = floatField;
        }

        public float getFloatField() {
            return floatField;
        }

        public void setBooleanField(final boolean booleanField) {
            this.booleanField = booleanField;
        }

        public boolean getBooleanField() {
            return booleanField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\",\"anotherField\":\"" + anotherField + "\",\"thirdField\":\"" + thirdField + "\",\"longField\":\"" + longField + "\",\"floatField\":\"" + floatField + "\",\"booleanField\":\"" + booleanField + "\"}";
        }
    }

    public class AfterBooleanAppendingStringArray implements TestObject {

        private String someField = STRING_VALUE;
        private int anotherField = INTEGER_VALUE;
        private double thirdField = DOUBLE_VALUE;
        private long longField = LONG_VALUE;
        private float floatField = FLOAT_VALUE;
        private boolean booleanField = BOOLEAN_VALUE;
        private String[] stringArrayField = new String[]{STRING_VALUE, STRING_VALUE};

        public void setSomeField(final String someField) {
            this.someField = someField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setAnotherField(final int anotherField) {
            this.anotherField = anotherField;
        }

        public int getAnotherField() {
            return anotherField;
        }

        public void setThirdField(final double thirdField) {
            this.thirdField = thirdField;
        }

        public double getThirdField() {
            return thirdField;
        }

        public void setLongField(final long longField) {
            this.longField = longField;
        }

        public long getLongField() {
            return longField;
        }

        public void setFloatField(final float floatField) {
            this.floatField = floatField;
        }

        public float getFloatField() {
            return floatField;
        }

        public void setBooleanField(final boolean booleanField) {
            this.booleanField = booleanField;
        }

        public boolean getBooleanField() {
            return booleanField;
        }

        public void setStringArrayField(final String[] stringArrayField) {
            this.stringArrayField = stringArrayField;
        }

        public String[] getStringArrayField() {
            return stringArrayField;
        }

        @Override
        public String getExpected() {
            return "{\"someField\":\"" + someField + "\",\"anotherField\":\"" + anotherField
                    + "\",\"thirdField\":\"" + thirdField + "\",\"longField\":\"" + longField
                    + "\",\"floatField\":\"" + floatField + "\",\"booleanField\":\"" + booleanField
                    + "\",\"stringArrayField\":[\"" + stringArrayField[0] + "\",\"" + stringArrayField[1] + "]}";
        }
    }

    public interface TestObject {

        public String getExpected();
    }
}
