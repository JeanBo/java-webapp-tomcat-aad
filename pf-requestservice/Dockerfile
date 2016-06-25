FROM node
MAINTAINER chvugrin@microsoft.com
COPY Gruntfile.js /opt/
COPY app /opt/app
COPY app.js /opt/
COPY bower.json /opt/
COPY config /opt/config
COPY node_modules /opt/node_modules
COPY package.json /opt/
COPY public /opt/public
RUN npm -g install sendgrid
CMD ["while true;do; echo hello; sleep 10;done"]
