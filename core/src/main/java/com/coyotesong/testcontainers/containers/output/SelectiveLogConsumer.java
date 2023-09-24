package com.coyotesong.testcontainers.containers.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.function.Consumer;

/**
 * Selective TestContainer log consumer.
 *
 * The ALL and NONE logging levels are self-explanatory.
 *
 * The SOME logging level attempts to skip log messages until
 * the docker container is successfully started.
 */
public class SelectiveLogConsumer implements Consumer<OutputFrame> {

    private static final Logger LOG = LoggerFactory.getLogger(SelectiveLogConsumer.class);

    /** Logging levels */
    public enum LoggingLevel {
        /** Log everything */
        ALL,
        /** Only log messages after container is successfully started */
        SOME,
        /** Log nothing */
        NONE,
    }

    private final String name;

    private LoggingLevel level = LoggingLevel.NONE;

    /**
     * Contructor
     *
     * @param name container name
     * @param level logging level
     */
    public SelectiveLogConsumer(String name, LoggingLevel level) {
        this.name = name;
        this.level = level;
    }

    /**
     * Convenience constructor
     *
     * @param name container name
     */
    public SelectiveLogConsumer(String name) {
        this(name, LoggingLevel.SOME);
    }

    /**
     * Convenience constructor
     *
     * @param name docker image name
     * @param level logging level
     */
    public SelectiveLogConsumer(DockerImageName name, LoggingLevel level) {
        this(name.asCanonicalNameString(), level);
    }

    /**
     * Get logging level
     *
     * @return LoggingLevel enum
     */
    public LoggingLevel getLoggingLevel() {
        return level;
    }

    /**
     * Set logging level
     *
     * @param level LoggingLevel enum
     */
    public void setLoggingLevel(LoggingLevel level) {
        this.level = level;
    }

    @Override
    public void accept(OutputFrame frame) {
        // should message prepend image name? Or rely on user and MDC?
        final String s = frame.getUtf8StringWithoutLineEnding();

        switch (level) {
            case ALL:
                switch (frame.getType()) {
                    case STDOUT:
                        LOG.info(s);
                        break;
                    case STDERR:
                        LOG.warn(s);
                }
                break;
            case SOME:
                // strip out embedded log statements
                // we could be more intelligent and use a regex that considers timestamps, etc.
                final boolean ignore =
                    s.contains(": DEBUG:  ") |
                    s.contains(": NOTICE:  ") |
                    s.contains(": INFO:  ") |
                    s.contains(" LOG: ");
                switch (frame.getType()) {
                    case STDOUT:
                        LOG.info(frame.getUtf8StringWithoutLineEnding());
                        break;
                    case STDERR:
                        if (ignore) {
                            LOG.debug(s);
                        } else {
                            LOG.warn(s);
                        }
                }
                break;
            case NONE:
            // do nothing
        }
    }
}
