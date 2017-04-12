# Example: Hazelcast cluster with Payara Micro

1. mvn install
2. Start one node: `java -jar /my/payara-micro.jar --deploy target/PayaraHazelcastExample-1.0-SNAPSHOT.war`
3. Point your browser to `http://localhost:8080/uuid`. You will get a colored UUID. Reload for new UUID.
4. Start up to 4 more nodes and add the `--autoBindHttp` command line switch to avoid port clashes. The new node will run on the next available port upwards from 8080.
5. Each node from 1-5 will get their own color.
6. Each node beyond the 5th will be assigned the default color.
7. You can also start a node with health checking enabled: `java -jar /my/payara-micro.jar --postbootcommandfile ./conf/postboot.txt --deploy target/PayaraHazelcastExample-1.0-SNAPSHOT.war`
8. Put a load balancer (e. g. haproxy) in front of your nodes and cycle through the colors, if you just hold down ctrl-R (reload) in your browser.
