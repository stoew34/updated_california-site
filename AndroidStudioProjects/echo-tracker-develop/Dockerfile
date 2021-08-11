# This Dockerfile creates a static build image for CI

FROM openjdk:8-jdk

MAINTAINER Build Engineer "DIAEchoLocateApp@T-Mobile.com"

ENV ANDROID_COMPILE_SDK "29"

ENV ANDROID_BUILD_TOOLS "30.0.2"

ENV ANDROID_SDK_ROOT /opt/android-sdk-linux

# install Android OS packages
RUN apt-get --quiet update --yes
RUN apt-get --quiet install --yes curl
RUN apt-get --quiet install --yes wget apt-utils tar unzip lib32stdc++6 lib32z1 build-essential ruby ruby-dev
# We use this for xxd hex->binary
RUN apt-get --quiet install --yes vim-common

# ------------------------------------------------------
# --- Download Android Command line Tools into $ANDROID_SDK_ROOT

RUN cd /opt \
    && wget -q https://dl.google.com/android/repository/commandlinetools-linux-6609375_latest.zip -O android-commandline-tools.zip \
    && mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools \
    && unzip -q android-commandline-tools.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools \
    && rm android-commandline-tools.zip

ENV PATH ${PATH}:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/tools/bin

RUN yes | sdkmanager --licenses

RUN touch /root/.android/repositories.cfg

RUN yes | sdkmanager --update --channel=3

RUN yes | sdkmanager \
    "platforms;android-${ANDROID_COMPILE_SDK}" \
    "build-tools;${ANDROID_BUILD_TOOLS}" \
    "system-images;android-${ANDROID_COMPILE_SDK};google_apis;x86" \
    "extras;android;m2repository" \
    "extras;google;m2repository" \
    "extras;google;google_play_services" \
    "extras;m2repository;com;android;support;constraint;constraint-layout;1.0.2" \
    "add-ons;addon-google_apis-google-23" \
