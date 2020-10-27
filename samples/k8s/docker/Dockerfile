FROM datamaintain

# Copy entrypoint for execute datamaintain with a lock and arguments
COPY entrypoint.sh /code/entrypoint.sh

# Add scripts that will be executed
COPY scripts /workspace/scripts

ENTRYPOINT ["/bin/bash", "-c", "/code/entrypoint.sh"]
