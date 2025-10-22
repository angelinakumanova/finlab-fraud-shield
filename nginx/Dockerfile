FROM nginx:alpine

RUN apk add --no-cache openssl

RUN mkdir -p /etc/ssl/private /etc/ssl/certs

RUN openssl req -x509 -newkey rsa:2048 -nodes \
    -keyout /etc/ssl/private/self.key \
    -out /etc/ssl/certs/self.crt \
    -subj "/CN=localhost" -days 365

RUN chmod 600 /etc/ssl/private/self.key

COPY nginx.conf /etc/nginx/nginx.conf
COPY app/ /usr/share/nginx/html/

EXPOSE 443
