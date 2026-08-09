# Serves the Helix static frontend (plain HTML/CSS/JS, hash-based
# routing - no build step needed) via nginx.
FROM nginx:1.27-alpine

# Remove the default nginx welcome page and static assets.
RUN rm -rf /usr/share/nginx/html/*

# Copy the frontend as-is - index.html, css/, js/, views/.
COPY . /usr/share/nginx/html/

EXPOSE 80
