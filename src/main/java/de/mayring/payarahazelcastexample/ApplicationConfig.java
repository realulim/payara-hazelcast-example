package de.mayring.payarahazelcastexample;

import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import fish.payara.micro.PayaraMicro;
import fish.payara.micro.PayaraMicroRuntime;

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

    @javax.annotation.PostConstruct
    public void configurePayara() {
        final PayaraMicroRuntime pmRuntime = PayaraMicro.getInstance().getRuntime();
        pmRuntime.run("healthcheck-configure", "--enabled=true", "--dynamic=true");
        pmRuntime.run("healthcheck-configure-service", "--serviceName=healthcheck-cpu", "--enabled=true",
            "--time=5", "--unit=SECONDS", "--dynamic=true");
        pmRuntime.run("healthcheck-configure-service-threshold", "--serviceName=healthcheck-cpu",
            "--thresholdCritical=90", "--thresholdWarning=50", "--thresholdGood=0", "--dynamic=true");
        pmRuntime.run("healthcheck-configure-service", "--serviceName=healthcheck-machinemem",
            "--enabled=true", "--dynamic=true", "--time=5","--unit=SECONDS");
        pmRuntime.run("healthcheck-configure-service-threshold", "--serviceName=healthcheck-machinemem",
            "--thresholdCritical=90", "--thresholdWarning=50", "--thresholdGood=0", "--dynamic=true");
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
