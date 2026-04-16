package br.umc.demo.config;

import java.net.Socket;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MongoPortCondition implements Condition {

    @SuppressWarnings("null")
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try (Socket socket = new Socket("127.0.0.1", 27017)) {
            System.out.println("✅ MongoDB detectado na porta 27017. Pulando Embedded...");
            return false;
        } catch (Exception e) {
            System.out.println("❌ MongoDB não detectado. Ativando Embedded...");
            return true;
        }
    }
}