# Clojure base image
FROM clojure:temurin-21-tools-deps-bookworm-slim

# Install necessary packages including X11 libraries and Python
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    bash \
    openssh-client \
    openssh-server \
    sudo \
    libx11-dev \
    libxi6 \
    libxtst6 \
    libxrender1 \
    libxext6 \
    libxft2 \
    x11-apps \
    python3 \
    python3-pip \
    python3-numpy \
    python3-pandas \
    python3-ipykernel \
    python3-matplotlib \
    python3-seaborn \
    python3-scipy \
    python3-sklearn \
    python3-statsmodels \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install Quarto
RUN wget https://github.com/quarto-dev/quarto-cli/releases/download/v1.3.450/quarto-1.3.450-linux-amd64.deb \
&& dpkg -i quarto-1.3.450-linux-amd64.deb \
&& rm quarto-1.3.450-linux-amd64.deb \
&& apt-get update \
&& apt-get install -f -y \
&& apt-get clean \
&& rm -rf /var/lib/apt/lists/*


# Create a non-root user and set up sudo privileges
RUN useradd -ms /bin/bash vscode \
    && echo "vscode ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

# Set the working directory inside the container
WORKDIR /workspace/ds-in-clojure

# # Copy requirements.txt into the container
# COPY pyquirements.txt /tmp/pyquirements.txt

# # Install Python packages from requirements.txt
# RUN pip install --no-cache-dir -r /tmp/pyquirements.txt

# Install R and required packages
RUN apt-get update && apt-get install -y --no-install-recommends \
    r-base \
    r-base-dev \
    libcurl4-openssl-dev \
    libssl-dev \
    libxml2-dev \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements.txt into the container
COPY rquirements.txt /tmp/rquirements.txt

# Install R packages from requirements.txt
RUN Rscript -e "install.packages(readLines('/tmp/rquirements.txt'), repos='https://cran.rstudio.com/', Ncpus=parallel::detectCores())"

# Switch to the non-root user
USER vscode

# Add Python to PATH
ENV PATH="/usr/local/bin:/usr/bin:/usr/local/bin/python3:${PATH}"

# Add Quarto to PATH
ENV PATH="/usr/local/bin:/usr/bin:${PATH}"

# Add NVIDIA GPU support
# UNCOMMENT IF YOU HAVE A GPU
# ENV NVIDIA_VISIBLE_DEVICES=all
# ENV NVIDIA_DRIVER_CAPABILITIES=compute,utility

# Set DISPLAY environment variable (might be needed for some GUI applications)
ENV DISPLAY=host.docker.internal:0

# Ensure the Java AWT libraries can find the X11 libraries
ENV LD_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu:$LD_LIBRARY_PATH

# Set Java to run in headless mode by default
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"
