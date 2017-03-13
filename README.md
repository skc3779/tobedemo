### 1. SuperTypeToken

Spring 3.2 이상에서 제공하는 ParameterizedTypeReference에 대한 소스 코딩

1. TypeToken 만들기
2. SuperTypeToken 만들기
3. ParameterizedTypeReference 활용하기

```$xslt
package kr.kangchun.demo07.generic.type 패키지 밑에 소스 참고
```

강의 : https://goo.gl/q4jByi


###2. reactive streams

https://github.com/reactive-streams/reactive-streams-jvm/tree/v1.0.0

The API consists of the following components

```tex
Publisher
Subscriber
Subscription
Processor
```
In response to a call to Publisher.subscribe(Subscriber) the possible invocation sequences for methods on the Subscriber are given by the following protocol:

```
onSubscribe onNext* (onError | onComplete) ?
```

### 3. reactive core

https://github.com/reactor/reactor-core

url : http://localhost:8080/hello?name=Reactive
