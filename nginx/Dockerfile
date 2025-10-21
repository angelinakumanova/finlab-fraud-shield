FROM nginx:stable
COPY nginx.conf /etc/nginx/nginx.conf
COPY app/ /usr/share/nginx/html/
COPY self.crt /etc/ssl/certs/
COPY self.key /etc/ssl/private/
EXPOSE 443
