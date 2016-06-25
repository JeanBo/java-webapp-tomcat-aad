FROM nginx
MAINTAINER chvugrin@microsoft.com

RUN mkdir /usr/share/nginx/html/css
COPY  nn-provision.html /usr/share/nginx/html/index.html
COPY  css/nn.css /usr/share/nginx/html/css/
COPY  js/jquery.relevant-dropdown.js /usr/share/nginx/html/js/
