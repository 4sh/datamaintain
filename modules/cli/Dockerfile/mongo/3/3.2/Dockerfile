FROM ubuntu:xenial

# Install JRE 8
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl netbase \
    && apt-get install -y openjdk-8-jre-headless \
	&& apt-get autoremove --purge -y curl netbase \
	&& rm -rf /var/lib/apt/lists/*

# From official mongo image and https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/#install-mongodb-community-edition-using-deb-packages
ARG MONGO_PACKAGE=mongodb-org
ARG MONGO_REPO=repo.mongodb.org
ENV MONGO_PACKAGE=${MONGO_PACKAGE} MONGO_REPO=${MONGO_REPO}

ARG MONGO_MAJOR
RUN test -n "$MONGO_MAJOR"
ENV MONGO_MAJOR $MONGO_MAJOR

RUN apt-get update \
    && apt-get install -y wget apt-transport-https \
    && wget -qO - https://www.mongodb.org/static/pgp/server-${MONGO_MAJOR}.asc | apt-key add - \
    && echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/${MONGO_MAJOR} multiverse" | tee /etc/apt/sources.list.d/mongodb-org-${MONGO_MAJOR}.list \
    && apt-get update \
    && apt-get install -y ${MONGO_PACKAGE}-shell \
	&& apt-get autoremove --purge -y wget apt-transport-https \
    && rm -rf /var/lib/apt/lists/*

# Install datamaintain
COPY cli-*.tar /code/datamaintain-cli.tar

RUN mkdir /code/datamaintain \
    && tar xvf /code/datamaintain-cli.tar --strip-components=1 -C /code/datamaintain \
    && rm /code/datamaintain-cli.tar \
    && rm /code/datamaintain/bin/cli.bat \
    && rm /code/datamaintain/zsh-autocomplete.sh

# Setup autocompletion
RUN ln -s /code/datamaintain/bin/cli /usr/local/bin/datamaintain
RUN echo "source /code/datamaintain/bash-autocomplete.sh" >> ~/.bashrc

ENTRYPOINT ["datamaintain"]
