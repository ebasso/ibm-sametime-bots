# ibm-sametime-bots
Bots using IBM Sametime

## Bots

| Application                 |   Description                                                                 | 
|-----------------------------|-------------------------------------------------------------------------------|
| SimpleBot                   | Simple Bot example                                                            |
| SimpleRestBot               | This Bot queries a site for information and return result (Json) to partner   |
| WatsonConversationBot       | This Bot use Watson Conversation service to do a dialog with partner          |
| WatsonLanguageTranslatorBot | This Bot use IBM Watson Language Translator service to do text translation    |

## How to run

Change the SimpleBot.properties property file.

And run

```
java -cp ibm-sametime-bots.jar SimpleBot -inifile=/home/ebasso/SimpleBot.properties
```

