# Example: Hazelcast cluster with Payara Micro

1. mvn install
2. start one node: `java -jar /my/payara-micro.jar --deploy target/PayaraHazelcastExample-1.0-SNAPSHOT.war`
3. start more nodes (up to 5 in total)
4. start a node with health checking enabled: `java -jar /my/payara-micro.jar --postbootcommandfile ./conf/postboot.txt --deploy target/PayaraHazelcastExample-1.0-SNAPSHOT.war`