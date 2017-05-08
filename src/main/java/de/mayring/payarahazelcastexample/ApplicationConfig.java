package de.mayring.payarahazelcastexample;

import java.util.HashSet;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.hazelcast.core.HazelcastInstance;

@ApplicationPath("/")
public class ApplicationConfig extends Application {

    public static String HAZELCAST = "payara/Hazelcast";
    public static String COLORS = "colors";
    public static String STORED = "StoredData";
    private static HazelcastInstance hazelcast = null;

    public ApplicationConfig() {
    }

    public static HazelcastInstance getHazelcast() {
        if (hazelcast != null && hazelcast.getLifecycleService().isRunning()) {
            return hazelcast;
        } 
        else {
            try {
                Context ctx = new InitialContext();
                hazelcast = (HazelcastInstance) ctx.lookup(HAZELCAST);
                return hazelcast;
            }
            catch (NamingException ex) {
                return null;
            }
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(de.mayring.payarahazelcastexample.UuidService.class);
        return resources;
    }

}
