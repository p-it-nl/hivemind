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
package io.hivemind.configuration;

import io.hivemind.constant.ConsistencyModel;
import io.hivemind.constant.ServerType;
import io.hivemind.exception.HiveCeption;
import io.hivemind.exception.HiveException;
import io.hivemind.exception.UnstartableException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.WARNING;

/**
 * Configuration for the application
 *
 * TODO: - Use @snippet instead of misusing @code when the IDE's support it
 *
 * @author Patrick-4488
 */
public class HiveConfig {

    private ServerType serverType;
    private ConsistencyModel consistencyModel;
    private int port;
    private int maxThreads;
    private int maxQueuedTasks;

    private static HiveConfig instance;

    private static final String APP_PROPERTIES = "/app.properties";

    private static final String KEY_SERVERTYPE = "server.type";
    private static final String KEY_CONSISTENCY_MODEL = "consistency.model";
    private static final String KEY_PORT = "port";
    private static final String KEY_MAX_THREADS = "max.threads";
    private static final String KEY_MAX_QUEUED_TASKS = "max.queued.tasks";

    private static final Logger LOGGER = System.getLogger(HiveConfig.class.getName());

    private HiveConfig() throws UnstartableException {
        loadConfiguration();
    }

    /**
     * returns instance application configuration
     * <p>
     * Will load from properties file only once, on construction</p>
     *
     * @return the application configuration
     * @throws io.hivemind.exception.HiveException exception when determining
     * properties
     */
    public static synchronized HiveConfig getInstance() throws HiveException {
        if (instance == null) {
            instance = new HiveConfig();
        }

        return instance;
    }

    /**
     * @return the server type
     */
    public ServerType getServerType() {
        return serverType;
    }

    /**
     * @return the consistency model
     */
    public ConsistencyModel getConsistencyModel() {
        return consistencyModel;
    }

    /**
     * @return the server type
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the maximum amount of threads
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * @return the maximum amount of queued tasks
     */
    public int getMaxQueuedTasks() {
        return maxQueuedTasks;
    }

    /**
     * Get a thread pool executor for the hive mind
     * <p>
     * having 1 to {config.maxthreads} where each thread is killed after 60
     * seconds idle time having a fixed queue of maximum
     * {config.maxqueuedtasks}, where excess tasks are rejected by
     * CallerRunsPolicy @see
     * java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy</p>
     *
     * @return the thread pool executor
     */
    public ThreadPoolExecutor getPoolExecutor() {
        return new ThreadPoolExecutor(1,
                maxThreads,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxQueuedTasks),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private void loadConfiguration() throws UnstartableException {
        InputStream configurationFile = getClass().getResourceAsStream(APP_PROPERTIES);
        if (configurationFile != null) {
            try ( InputStream is = new BufferedInputStream(configurationFile)) {
                Properties properties = new Properties();
                properties.load(is);

                setProperties(properties);
            } catch (IOException ex) {
                LOGGER.log(ERROR, """
                    Not able to read app.properties file in the classpath, 
                    resolve this issue in order to use the application""", ex);
                throw new UnstartableException(HiveCeption.INVALID_APP_PROPERTIES);
            }
        } else {
            throw new UnstartableException(HiveCeption.MISSING_APP_PROPERTIES);
        }
    }

    /**
     * TODO: when this method gets to big, switch to using reflections and
     * assign each variable a default value {@code :
     * FOR EACH property key
     *  IF this.{propertykey} exists
     *      INVOKE setter for this.{propertykey}
     *  ELSE
     *      IGNORE}
     */
    private void setProperties(final Properties properties) {
        setServerType(properties);
        setConsistencyModel(properties);
        setPort(properties);
        setMaxThreads(properties);
        setMaxQueuedTasks(properties);
    }

    private void setServerType(final Properties properties) {
        try {
            serverType = (properties.containsKey(KEY_SERVERTYPE)
                    ? ServerType.valueOf(properties.getProperty(KEY_SERVERTYPE).toUpperCase())
                    : ServerType.DEFAULT);
        } catch (IllegalArgumentException ex) {
            serverType = ServerType.DEFAULT;
            LOGGER.log(WARNING, "Failed to determine server type, will be default", ex);
        }
    }

    private void setConsistencyModel(final Properties properties) {
        try {
            consistencyModel = (properties.containsKey(KEY_CONSISTENCY_MODEL)
                    ? ConsistencyModel.valueOf(properties.getProperty(KEY_CONSISTENCY_MODEL).toUpperCase())
                    : ConsistencyModel.EVENTUAL_CONSISTENCY);
        } catch (IllegalArgumentException ex) {
            consistencyModel = ConsistencyModel.EVENTUAL_CONSISTENCY;
            LOGGER.log(WARNING, "Failed to determine consistency model, will be default", ex);
        }
    }

    private void setPort(final Properties properties) {
        try {
            port = Integer.valueOf(properties.getProperty(KEY_PORT));
        } catch (NumberFormatException ex) {
            port = 8000;
            LOGGER.log(WARNING, "Failed to determine port, will use the default port 8000", ex);
        }
    }

    private void setMaxThreads(final Properties properties) {
        try {
            maxThreads = Integer.valueOf(properties.getProperty(KEY_MAX_THREADS));
        } catch (NumberFormatException ex) {
            maxThreads = 1;
            LOGGER.log(WARNING, "Failed to determine maximum amount of threads, will default to 1", ex);
        }
    }

    private void setMaxQueuedTasks(final Properties properties) {
        try {
            maxQueuedTasks = Integer.valueOf(properties.getProperty(KEY_MAX_QUEUED_TASKS));
        } catch (NumberFormatException ex) {
            maxQueuedTasks = 100;
            LOGGER.log(WARNING, "Failed to determine maximum queued tasks, will default to 100", ex);
        }
    }
}
