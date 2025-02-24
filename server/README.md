## Run Service from Docker

### Push latest image to docker hub

- Replace NEW_VERSION with the appropriate version (ex. 1.0.2)
- Run `docker build -t r8ravind/project-vars:NEW_VERSION .`
- Run `docker push r8ravind/project-vars:NEW_VERSION`
- Run `docker tag r8ravind/project-vars:NEW_VERSION r8ravind/project-vars:latest`
- Run `docker push r8ravind/project-vars:latest`

### Pull latest image from docker hub

- Run `docker pull r8ravind/project-vars:latest`
- Run `docker run -p 3000:3000 r8ravind/project-vars:latest`
