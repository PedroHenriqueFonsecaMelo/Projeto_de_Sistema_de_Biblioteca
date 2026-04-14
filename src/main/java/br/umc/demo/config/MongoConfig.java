package br.umc.demo.config;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.ImmutableStart;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    private TransitionWalker.ReachedState<RunningMongodProcess> running;

    @PostConstruct
    public void startMongo() {
        this.running = Mongod.instance()
                .withNet(ImmutableStart.to(Net.class)
                        .initializedWith(Net.builder()
                                .port(27017)
                                .isIpv6(false)
                                .bindIp("127.0.0.1")
                                .build()))
                .start(Version.Main.V6_0);

        System.out.println("🚀 Embedded MongoDB is now running on port 27017");
    }

    @PreDestroy
    public void stopMongo() {
        if (this.running != null) {
            this.running.close();
            System.out.println("🛑 Embedded MongoDB stopped");
        }
    }
}