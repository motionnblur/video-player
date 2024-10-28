From node:23-alpine

WORKDIR /App

COPY package*.json ./
RUN npm i
RUN npm i axios

COPY . .

CMD ["npm","run","dev"]


