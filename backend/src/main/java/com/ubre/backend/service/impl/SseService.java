package com.ubre.backend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class SseService {

    private static final long TIMEOUT_MS = 0L; // ili npr 30 * 60 * 1000L
    private static final long PING_INITIAL_DELAY_SEC = 5;
    private static final long PING_PERIOD_SEC = 15;

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            remove(userId, emitter);
        });
        emitter.onError(e -> {
            try { emitter.completeWithError(e); } catch (Exception ignored) {}
            remove(userId, emitter);
        });

        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // PING drži konekciju živom (proxy/load balancer često ubije idle konekciju)
        ScheduledFuture<?> pingTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("PING")
                        .data("ok"));
            } catch (IOException ex) {
                remove(userId, emitter);
                emitter.complete();
            } catch (IllegalStateException ex) {
                remove(userId, emitter);
            }
        }, PING_INITIAL_DELAY_SEC, PING_PERIOD_SEC, TimeUnit.SECONDS);

        Runnable stopPing = () -> pingTask.cancel(true);
        emitter.onCompletion(stopPing);
        emitter.onTimeout(stopPing);
        emitter.onError(e -> stopPing.run());

        try {
            emitter.send(SseEmitter.event().name("CONNECTED").data("ok"));
        } catch (IOException ex) {
            stopPing.run();
            remove(userId, emitter);
            emitter.complete();
        }

        return emitter;
    }

    public void send(Long userId, String eventName, Object data) {
        List<SseEmitter> list = emitters.get(userId);
        if (list == null || list.isEmpty()) return;

        for (SseEmitter em : list) {
            try {
                em.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException | IllegalStateException ex) {
                remove(userId, em);
                try { em.complete(); } catch (Exception ignored) {}
            }
        }
    }

    private void remove(Long userId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = emitters.get(userId);
        if (list == null) return;
        list.remove(emitter);
        if (list.isEmpty()) emitters.remove(userId);
    }
}
