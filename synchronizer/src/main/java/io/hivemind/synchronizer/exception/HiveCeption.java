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
package io.hivemind.synchronizer.exception;

/**
 * Custom exceptions including some nice messages that are user friendly
 *
 * @author Patrick-4488
 */
public enum HiveCeption {

    INVALID_ESSENCE("""
        The received hive essence is not valid. Expected is a list of bytes containing: 
        {id},{version};{id},{version}.... """
    ),
    CONSISTENCY_MODEL_NOT_SUPPORTED("""
        The requested consistency model is not (yet) supported"""
    ),
    HIVE_RESOURCE_ID_OR_VERSION_NOT_NUMBER("""
        While attemting to generate the essence, a value for either id or version 
        of the hive resources was given which is not a number. This is not (currently) 
        supported since the value will become part of a number sequence that 
        represents the hive essence. Please use a number"""
    );

    private final String message;

    HiveCeption(String message) {
        this.message = message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
