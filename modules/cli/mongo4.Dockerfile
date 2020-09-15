FROM openjdk:8

# From official mongo image and https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/#install-mongodb-community-edition-using-deb-packages
ARG MONGO_PACKAGE=mongodb-org
ARG MONGO_REPO=repo.mongodb.org
ENV MONGO_PACKAGE=${MONGO_PACKAGE} MONGO_REPO=${MONGO_REPO}

ARG MONGO_MAJOR
RUN test -n "$MONGO_MAJOR"
ENV MONGO_MAJOR $MONGO_MAJOR

RUN wget -qO - https://www.mongodb.org/static/pgp/server-${MONGO_MAJOR}.asc | apt-key add - \
    && echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu bionic/mongodb-org/${MONGO_MAJOR} multiverse" | tee /etc/apt/sources.list.d/mongodb-org-${MONGO_MAJOR}.list \
    && apt-get update \
    && apt-get install -y ${MONGO_PACKAGE}-shell \
    && rm -rf /var/lib/apt/lists/*

# Install datamaintain
COPY ./build/distributions/cli-*.tar /code/datamaintain-cli.tar

RUN mkdir /code/datamaintain \
    && tar xvf /code/datamaintain-cli.tar --strip-components=1 -C /code/datamaintain

ENTRYPOINT ["/code/datamaintain/bin/cli"]
