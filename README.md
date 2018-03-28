# ibm-sametime-bots
Bots using IBM Sametime

## Bots

| Application                 |   Description                                                                 | 
|-----------------------------|-------------------------------------------------------------------------------|
| SimpleBot                   | Simple Bot example                                                            |
| SimpleRestBot               | This Bot queries a site for information and return result (Json) to partner   |
| WatsonConversationBot       | This Bot use Watson Conversation service to demonstrates the Conversation service in a Sametime chat interface simulating a cognitive car dashboard.         |
| WatsonLanguageTranslatorBot | This Bot use IBM Watson Language Translator service to do text translation    |


## Binary version and how to run

Change the SimpleBot.properties property file.

And run

```
java -cp ibm-sametime-bots.jar SimpleBot -inifile=<directory>/SimpleBot.properties
```

Do the same for the others, change the property file and run

```
java -cp ibm-sametime-bots.jar SimpleRestBot -inifile=<directory>/SimpleRestBot.properties

java -cp ibm-sametime-bots.jar WatsonConversationBot -inifile=<directory>/WatsonConversationBot.properties

java -cp ibm-sametime-bots.jar WatsonLanguageTranslatorBot -inifile=<directory>/WatsonLanguageTranslatorBot.properties
```

# WatsonConversationBot - Conversation Sample Application using IBM Sametime

This Bot application demonstrates the Conversation service in a Sametime chat interface simulating a cognitive car dashboard.

## Before you begin

* Create a Bluemix account
    * [Sign up][sign_up] in Bluemix, or use an existing account. Your account must have available space for at least 1 app and 1 service.
* Make sure that you have the following prerequisites installed:
    * The [Java 8](https://java.com/download) runtime
    * The [Cloud Foundry][cloud_foundry] command-line client



## Importing the Conversation workspace

1. In your browser, navigate to [your Bluemix console] (https://console.ng.bluemix.net/dashboard/services).

1. On Bluemix catalog, created Conversation service.

1. From the **All Items** tab, click the newly created Conversation service in the **Services** list.

    ![Screen capture of Services list](readme_images/conversation_service.png)

1. On the Service Details page, click **Launch tool**.

1. Click the **Import workspace** icon in the Conversation service tool. Specify the location of the workspace JSON file in your local copy of the app project:

    `<project_root>/training/car_workspace.json`

1. Select **Everything (Intents, Entities, and Dialog)** and then click **Import**. The car dashboard workspace is created.


## Configure WatsonLanguageTranslatorBot.properties

On file **WatsonLanguageTranslatorBot.properties** update 

```
conversation.username=<conversation username>
conversation.password=<password>
conversation.workspaceid=<workspace id>
```

## Testing

Run WatsonLanguageTranslatorBot inside your Java IDE or by command like:

```
java -cp ibm-sametime-bots.jar WatsonLanguageTranslatorBot -inifile=<directory>/WatsonLanguageTranslatorBot.properties
```