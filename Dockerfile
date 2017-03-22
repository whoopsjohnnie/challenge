# Deploy challenge container

# Option one: Use a prebuilt java Docker image
FROM openjdk:8

# RUN adduser localuser
RUN useradd -ms /bin/bash localuser
ADD ./ /home/localuser/challenge
ENV STORAGE_PATH /home/localuser
CMD ["su", "-", "localuser", "-c", "/home/localuser/challenge/bin/challenge-executable.sh"]
