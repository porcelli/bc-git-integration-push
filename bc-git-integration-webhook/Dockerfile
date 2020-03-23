FROM fabric8/java-centos-openjdk8-jre
ENV PORT=9090

USER root
RUN yum install -y openssh-server
RUN mkdir -p /root/.ssh
RUN ssh-keygen -t rsa -N "" -f /root/.ssh/id_rsa
RUN echo -e "Host * \nStrictHostKeyChecking no" > /root/.ssh/config
RUN cat /root/.ssh/id_rsa.pub

COPY target/bc-git-integration-webhook-2.2.0-SNAPSHOT.jar .

CMD java -jar -Dport=$PORT bc-git-integration-webhook-2.2.0-SNAPSHOT.jar
