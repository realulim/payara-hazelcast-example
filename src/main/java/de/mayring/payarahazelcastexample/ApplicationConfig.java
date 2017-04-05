package de.mayring.payarahazelcastexample;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;

@ApplicationPath("/")
public class ApplicationConfig extends Application {

    private final IList<String> colors;

    public ApplicationConfig() throws NamingException {
        javax.naming.Context ctx = new InitialContext();
        HazelcastInstance hazelcast = (HazelcastInstance) ctx.lookup("payara/Hazelcast");
        colors = hazelcast.getList("colors");
        colors.add("Fuchsia");
        colors.add("Teal");
        colors.add("Lime");
        colors.add("Blue");
        colors.add("Black");
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
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(de.mayring.payarahazelcastexample.UuidService.class);
    }
    
}
