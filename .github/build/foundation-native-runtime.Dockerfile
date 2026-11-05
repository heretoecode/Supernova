# Test-only Debian tools; no signing environment, keys or app data enter this image.
FROM debian@sha256:7c7b2c966bc9ee8cedfeef67e0e279108992c77681fa595db4a9d65c06ccc587
RUN --mount=type=secret,id=proxy_ca,required=true \
    apt-get -o Acquire::https::CaInfo=/run/secrets/proxy_ca update -qq && \
    apt-get -o Acquire::https::CaInfo=/run/secrets/proxy_ca install -y -qq e2fsprogs unzip qemu-user-static && \
    rm -rf /var/lib/apt/lists/*
CMD ["tail", "-f", "/dev/null"]
