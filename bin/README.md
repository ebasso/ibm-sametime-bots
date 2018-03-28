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


Do the same for the others, change the property file and run

```
java -cp ibm-sametime-bots.jar SimpleRestBot -inifile=<directory>/SimpleRestBot.properties

java -cp ibm-sametime-bots.jar WatsonConversationBot -inifile=<directory>/WatsonConversationBot.properties

java -cp ibm-sametime-bots.jar WatsonLanguageTranslatorBot -inifile=<directory>/WatsonLanguageTranslatorBot.properties
```
