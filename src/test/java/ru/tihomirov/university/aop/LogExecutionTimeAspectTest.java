package ru.tihomirov.university.aop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class LogExecutionTimeAspectTest {

    @Test
    void testLogExecutionTimeAspect(CapturedOutput output) {
        DummyService target = new DummyService();
        LogExecutionTimeAspect aspect = new LogExecutionTimeAspect();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        DummyService proxy = factory.getProxy();

        proxy.doSomething();

        assertThat(output.getOut()).contains("Method");
        assertThat(output.getOut()).contains("executed in");
    }

    static class DummyService {
        @LogExecutionTime
        public void doSomething() {
            try {
                Thread.sleep(100); // имитируем работу
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
