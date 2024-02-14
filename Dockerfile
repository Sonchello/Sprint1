FROM alpine:latest

RUN apk add --no-cache bash

WORKDIR /protei1sprint

CMD ./gradlew run
