# Dockerfile

FROM  phusion/baseimage:0.9.17

MAINTAINER  Shindou Jeikobu <shindou@jeikobu.net>

RUN echo "deb http://archive.ubuntu.com/ubuntu trusty main universe" > /etc/apt/sources.list

RUN apt-get -y update

RUN DEBIAN_FRONTEND=noninteractive apt-get install -y -q python-software-properties software-properties-common

# update and install support for https:// sources if not already installed
RUN apt-get update && apt-get install apt-transport-https -y --force-yes --no-install-recommends
# add my key to trusted APT keys
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys A66C5D02
# add the package repo to sources
RUN echo 'deb https://rpardini.github.io/adoptopenjdk-deb-installer stable main' > /etc/apt/sources.list.d/rpardini-aoj.list
# update from sources
RUN apt-get update
# install a JDK, see above instructions for Ubuntu for other variants as well
RUN apt-get install adoptopenjdk-8-installer -y --force-yes --no-install-recommends

RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN mkdir /home/kotomi/

WORKDIR /home/kotomi

COPY build/libs/*.jar ./

RUN mv *-all.jar app.jar

RUN mkdir /home/kotomi/config

CMD [ "java", "-jar", "app.jar"]