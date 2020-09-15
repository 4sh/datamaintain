FROM ubuntu:xenial

CMD ["bash"]

RUN apt-get update \
    && apt-get install -y --no-install-recommends apt-transport-https ca-certificates curl netbase wget \
    && rm -rf /var/lib/apt/lists/*

# Install java (open jdk-8)
RUN apt-get update \
	&& apt-get install -y openjdk-8-jdk ant \
	&& apt-get clean \
	&& rm -rf /var/lib/apt/lists/* \
	&& rm -rf /var/cache/oracle-jdk8-installer;

# Fix certificate issues : https://bugs.launchpad.net/ubuntu/+source/ca-certificates-java/+bug/983302
RUN apt-get update \
	&& apt-get install -y ca-certificates-java \
	&& apt-get clean \
	&& update-ca-certificates -f \
	&& rm -rf /var/lib/apt/lists/* \
	&& rm -rf /var/cache/oracle-jdk8-installer;

# From official mongo image and https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/#install-mongodb-community-edition-using-deb-packages
ARG MONGO_PACKAGE=mongodb-org
ARG MONGO_REPO=repo.mongodb.org
ENV MONGO_PACKAGE=${MONGO_PACKAGE} MONGO_REPO=${MONGO_REPO}

ARG MONGO_MAJOR
RUN test -n "$MONGO_MAJOR"
ENV MONGO_MAJOR $MONGO_MAJOR

RUN wget -qO - https://www.mongodb.org/static/pgp/server-${MONGO_MAJOR}.asc | apt-key add - \
    && echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/${MONGO_MAJOR} multiverse" | tee /etc/apt/sources.list.d/mongodb-org-${MONGO_MAJOR}.list \
    && apt-get update \
    && apt-get install -y ${MONGO_PACKAGE}-shell \
    && rm -rf /var/lib/apt/lists/*

# Install datamaintain
COPY ./build/distributions/cli-*.tar /code/datamaintain-cli.tar

RUN mkdir /code/datamaintain \
    && tar xvf /code/datamaintain-cli.tar --strip-components=1 -C /code/datamaintain
    && rm /code/datamaintain/bin/cli.bat

ENTRYPOINT ["/bin/bash", "-c", "/code/datamaintain/bin/cli"]
