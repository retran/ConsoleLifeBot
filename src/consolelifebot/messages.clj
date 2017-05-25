(ns consolelifebot.messages)

(def rules-url
  "http://telegra.ph/Pravila-konferencii-Console-Life-20-05-13")

(def vk-url
  "https://vk.com/consolelifevk")

(def telegram-channel-url
  "https://telegram.me/consolenote")

(def youtube-channel-url
  "https://www.youtube.com/channel/UCTkzwcV5g8DndENYe__EeKw")

(def doom-server-url
  "http://telegra.ph/Console-Life-Doom-Coop-Server-01-07")

(def list-url
  "https://docs.google.com/spreadsheets/d/1P_CjFZEKdMUlokf-vQnQRVg5pASLIAQuhhGh4qg6mW0/edit?usp=sharing")

(def welcome
  (format
   "Добро пожаловать в конференцию <b>Console Life</b>!

Прежде чем начать бороздить Вселенную консольных игр в данной группе, настоятельно просим ознакомиться с несколькими <a href=\"%s\">простыми правилами</a>."
   rules-url))

(def rules
  (format
   "Правила конференции можно почитать <a href=\"%s\">здесь</a>."
   rules-url))

(def about
  (format
   "Заходите в наши:
- <a href=\"%s\">группу Вконтакте</a>
- <a href=\"%s\">канал в Telegram</a>
- <a href=\"%s\">канал на YouTube</a>
- <a href=\"%s\">сервер для прохождения Doom</a>"
   vk-url telegram-channel-url youtube-channel-url doom-server-url))

(def console-list
  (format
   "Жми <a href=\"%s\">сюда</a>, чтобы увидеть список консолей и их владельцев."
   list-url))

