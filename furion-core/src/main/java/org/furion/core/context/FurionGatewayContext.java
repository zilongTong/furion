package org.furion.core.context;

import org.furion.core.filter.FurionFilter;
import org.furion.core.filter.FurionFilterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class FurionGatewayContext {

    private static final Logger LOG = LoggerFactory.getLogger(FurionGatewayContext.class);

    private FurionProperties FurionProperties;

    private static FurionFilterRegistry registry;

    private static final Properties properties = new Properties();

    public static FurionFilterRegistry getRegistry() {
        return registry;
    }

    public static void setRegistry(FurionFilterRegistry registry) {
        FurionGatewayContext.registry = registry;
    }

    public FurionGatewayContext() {
        lodePropsFromLocalFile(FurionGatewayContext.class.getResource("").getPath());
        refresh();
    }

    public static void main(String[] args) {
        System.out.println(FurionGatewayContext.class.getResource("/META-INF").getPath());
    }

    public void refresh() {
        //
    }

    void lodePropsFromFurionAdmin() {
        //TODO
        lodePropsFromLocalFile("");
    }


    public static void registryFilter() {
        //TODO
    }

    void lodePropsFromLocalFile(String path) {
        final File propsFile = new File(path);
        if (propsFile.isFile()) {
            try (InputStream is = new FileInputStream(propsFile)) {
                properties.load(is);
            } catch (final IOException e) {
                LOG.warn("Could not load props file?", e);
            }
        }
    }


}
