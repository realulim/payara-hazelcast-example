# Example for setting up a Hazelcast cluster with Payara Micro

1. mvn install
2. start one node:
- java -jar /my/payara-micro.jar --postbootcommandfile ./conf/postboot.txt --deploy target/PayaraHazelcastExample-1.0-SNAPSHOT.war
3. start more nodes