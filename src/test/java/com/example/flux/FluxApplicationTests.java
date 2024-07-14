package com.example.flux;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
class FluxApplicationTests {

  List<Integer> elements;

  @BeforeEach
  void setup() {
    elements = new ArrayList<>();
  }

  @Test
  void contextLoads() {
  }

  @Test
  void test1() {
    Flux<Integer> flux = Flux.just(1, 2, 3, 4);
    Mono<Integer> mono = Mono.just(1);
    Publisher<String> publisher = Mono.just("foo");

    flux.just(1, 2, 3, 4)
        .log()
        .subscribe(elements::add);

    assertThat(elements).containsExactly(1, 2, 3, 4);

  }


  @Test
  void flowOfElements() {

    Flux.just(1, 2, 3, 4)
        .log()
        .subscribe(new Subscriber<Integer>() {
          @Override
          public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
            System.out.println("onSubscribe");
          }

          @Override
          public void onNext(Integer integer) {
            elements.add(integer);
            System.out.println("add: " + integer);
          }

          @Override
          public void onError(Throwable t) {
          }

          @Override
          public void onComplete() {
            System.out.println("completed");
          }
        });
  }

  @Test
  void backPressure() {
    Flux.just(1, 2, 3, 4)
        .log()
        .subscribe(new Subscriber<Integer>() {
          private Subscription s;
          int onNextAmount;

          @Override
          public void onSubscribe(Subscription s) {
            this.s = s;
            s.request(2);
          }

          @Override
          public void onNext(Integer integer) {
            elements.add(integer);
            onNextAmount++;
            if (onNextAmount % 2 == 0) {
              s.request(2);
            }
          }

          @Override
          public void onError(Throwable t) {
          }

          @Override
          public void onComplete() {
          }
        });
  }

  @Test
  void mappingDataInStreat() {
    Flux.just(1, 2, 3, 4)
        .log()
        .map(i -> {
//          LOGGER.debug("{}:{}", i, Thread.currentThread());
          System.out.println("i=" + i + ", currentThread: " + Thread.currentThread());
          return i * 2;
        })
        .subscribe(elements::add);
  }

  @Test
  void hotStreams() {
    ConnectableFlux<Object> published = Flux.create(fluxSink -> {
          while (true) {
            fluxSink.next(System.currentTimeMillis());
          }
        })
        .publish();
//    Disposable connect = published.connect();
    published.subscribe(System.out::println);
  }

}