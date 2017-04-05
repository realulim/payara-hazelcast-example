package de.mayring.payarahazelcastexample;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@ApplicationPath("/")
public class ApplicationConfig extends Application {

    public static final String COLORS = "colors";

    public ApplicationConfig() throws NamingException {
        List<String> colorsAvailable = initialiseColors();

        javax.naming.Context ctx = new InitialContext();
        HazelcastInstance hazelcast = (HazelcastInstance) ctx.lookup("payara/Hazelcast");
        Cluster cluster = hazelcast.getCluster();

        IMap<String, String> colorsInUse = hazelcast.getMap(COLORS);

        if (cluster.getMembers().size() == 1) {
            // we are the only member of the cluster, so all entries are stale
            colorsInUse.clear();
        }
        else {
            // joining an existing cluster, so colors already taken are removed
            for (String color : colorsInUse.values()) {
                colorsAvailable.remove(color);
            }
        }
        String myColor = colorsAvailable.get(ThreadLocalRandom.current().nextInt(0, colorsAvailable.size()));
        colorsInUse.put(cluster.getLocalMember().getUuid(), myColor);
    }

    private List<String> initialiseColors() {
        List<String> colors = new ArrayList<>();
        colors.add("Fuchsia");
        colors.add("Teal");
        colors.add("Lime");
        colors.add("Blue");
        colors.add("Black");
        return colors;
    }

    @PostConstruct
    public void configurePayara() {
        // not working in Payara 4.1.1.171.1
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(de.mayring.payarahazelcastexample.UuidService.class);
    }

}
