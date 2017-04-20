package de.mayring.payarahazelcastexample;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.nurkiewicz.asyncretry.AsyncRetryExecutor;
import com.nurkiewicz.asyncretry.RetryExecutor;

@ApplicationPath("/")
public class ApplicationConfig extends Application {

    public static String COLORS = "colors";
    public static String STORED = "StoredData";
    private static HazelcastInstance hazelcast = null;

    public ApplicationConfig() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        RetryExecutor executor = new AsyncRetryExecutor(scheduler).
                retryOn(NamingException.class).
                withExponentialBackoff(500, 2). // start with a delay of 500ms and double delay after each retry
                withMaxDelay(10000). // maximum delay should be 10 seconds
                withUniformJitter(); // add between +/- 100 ms randomly

        final CompletableFuture<HazelcastInstance> future = executor.getWithRetry(() -> {
            javax.naming.Context ctx = new InitialContext();
            return (HazelcastInstance) ctx.lookup("payara/Hazelcast");
        });

        future.thenAccept((HazelcastInstance hz) -> {
            Logger.getAnonymousLogger().info("Connected to the Cluster!");
            IMap<String, String> colorsInUse = hz.getMap(COLORS);

            ClusterMembershipListener listener = new ClusterMembershipListener(colorsInUse);
            hz.getCluster().addMembershipListener(listener);

            ApplicationConfig.hazelcast = hz;
        });
    }

    public static HazelcastInstance getHazelcast() {
        return hazelcast;
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
