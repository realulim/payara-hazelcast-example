# Example: Hazelcast cluster with Payara Micro

1. mvn install
2. Start one node: `java -jar /my/payara-micro.jar --deploy target/PayaraHazelcastExample-1.0-SNAPSHOT.war`
3. Point your browser to `http://localhost:8080/uuid`. You will get a colored UUID.
4. Start up to 4 more nodes. Each will get their own color for their UUID.
5. You can also start a node with health checking enabled: `java -jar /my/payara-micro.jar --postbootcommandfile ./conf/postboot.txt --deploy target/PayaraHazelcastExample-1.0-SNAPSHOT.war`
6. Put a load balancer (e. g. haproxy) in front of your nodes and watch how the color cycles, if you just hold down ctrl-R (reload) in your browser.
