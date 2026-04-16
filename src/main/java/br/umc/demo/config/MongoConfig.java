package br.umc.demo.config;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.ImmutableStart;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean(destroyMethod = "close")
    @Conditional(MongoPortCondition.class)
    public TransitionWalker.ReachedState<RunningMongodProcess> runningMongo() {

        TransitionWalker.ReachedState<RunningMongodProcess> running = Mongod.instance()
                .withNet(ImmutableStart.to(Net.class)
                        .initializedWith(Net.builder()
                                .port(27017)
                                .isIpv6(false)
                                .bindIp("127.0.0.1")
                                .build()))
                .start(Version.Main.V6_0);

        System.out.println("🚀 Embedded MongoDB is now running on port 27017");
        return running;
    }
}