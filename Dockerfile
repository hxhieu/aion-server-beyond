FROM maven:eclipse-temurin AS builder
WORKDIR /aion-src

ARG REPO=https://github.com/hxhieu/aion-server-beyond.git
ARG TAG=hxh-4.8.1

# clone and build
RUN git clone $REPO
RUN cd aion-server-beyond && git checkout $TAG
RUN cd aion-server-beyond && mvn package

# Runner
FROM eclipse-temurin:24 AS runner
RUN apt-get update && apt-get install -y unzip
WORKDIR /aion-server
RUN chown -R 1000:1000 /aion-server

USER 1000

# copy the built files from the builder stage
COPY --from=builder \
    /aion-src/aion-server-beyond/chat-server/target/chat-server.zip \
    /aion-src/aion-server-beyond/login-server/target/login-server.zip \
    /aion-src/aion-server-beyond/game-server/target/game-server.zip \
    ./

# extract the zip files
RUN unzip chat-server.zip && unzip login-server.zip && unzip game-server.zip && rm *.zip
RUN chmod +x /aion-server/**/*.sh

CMD [ "tail", "-f", "/dev/null" ]