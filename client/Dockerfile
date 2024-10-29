From node:23-alpine

WORKDIR /App

COPY package*.json ./
RUN npm i
RUN npm i axios
RUN npm i crypto

COPY . .

CMD ["npm","run","dev"]


