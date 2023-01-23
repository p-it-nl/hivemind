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
package io.hivemind.exception;

/**
 * Custom exceptions including some nice messages that are user friendly
 *
 * @author Patrick-4488
 */
public enum HiveCeption {

    MISSING_APP_PROPERTIES("Not able to read app.properties file in the classpath, resolve this issue in order to use the application"),
    INVALID_APP_PROPERTIES("""
        Not able to read app.properties file in the classpath, the file might be 
        in incorrect format or the application has no permission to read it"""
    ),
    NETTY_FAILED_TO_BOOT("The application was unable to boot Netty server, see exception for more details"),
    HTTP_SERVER_FAILED_TO_BOOT("The application was unable to boot httpserver, see exception for more details"),
    UNABLE_TO_SETUP_SERVER("The application was not able to set up a server to boot the hive, see exception for more details"),
    INVALID_ESSENCE("""
        The received hive essence is not valid. Expected is a list of bytes containing: 
        {id},{version};{id},{version}.... """
    ),
    UNEXPECTED_VALUE_FOR_CLEAR_RECEIVED_BY_MANAGER("""
        The manage request received contained an unexpected value for clear, expecting 
        either: ALL or INERT.""");

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
