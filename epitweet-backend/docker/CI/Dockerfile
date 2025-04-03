FROM ubuntu:20.04

# Disable interactive prompts during package installation
ENV DEBIAN_FRONTEND=noninteractive

# Install required packages including JDK 17 and Maven
RUN apt-get update && apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    openjdk-17-jdk \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Install Docker Engine
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add - && \
    add-apt-repository \
       "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
       $(lsb_release -cs) \
       stable" && \
    apt-get update && apt-get install -y docker-ce docker-ce-cli containerd.io && \
    rm -rf /var/lib/apt/lists/*

# Install Docker Compose (v2.21.0 in this example)
RUN curl -L "https://github.com/docker/compose/releases/download/v2.21.0/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# Copy the Docker-in-Docker entrypoint script into the image
COPY dind-entrypoint.sh /usr/local/bin/dind-entrypoint.sh
RUN chmod +x /usr/local/bin/dind-entrypoint.sh

# Expose Docker daemon port (optional)
EXPOSE 2375

# Set the custom entrypoint to start Docker daemon
ENTRYPOINT ["/usr/local/bin/dind-entrypoint.sh"]

# Default command (keeps the container running)
CMD ["tail", "-f", "/dev/null"]

