FROM docker.pkg.github.com/4sh/datamaintain/datamaintain:1.2.0-mongo-4.2

# Copy entrypoint for execute datamaintain with a lock and arguments
COPY entrypoint.sh /code/entrypoint.sh

# Add scripts that will be executed
COPY scripts /workspace/scripts

ENTRYPOINT ["/bin/bash", "-c", "/code/entrypoint.sh"]
