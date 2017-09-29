#Telegram Bot Configurator

This is a JavaFx-Application I made during my apprenticeship as a Software Developer.
You can create simple commands for your [Telegram](https://telegram.org/) [Bot](https://core.telegram.org/bots) 
and configure permissions, roles and users within the application.
It's possible to save your configuration and use it without the GUI using _n --file 'file/path/configuration.tbc'_
arguments.
I used it to configure simple commands on my [Raspberry Pi](https://www.raspberrypi.org/).

It uses [pengrads Telegram bot api](https://github.com/pengrad/java-telegram-bot-api).

I learned a lot during development. I just found it again and wanted to share it with anyone who might find this code/application handy.
I tried to combine JavaFx with dependency injection. It was tricky because I didn't even know how to use spring correctly.
But I somehow made it...
To make JavaFx and Spring like each other I used [Gluon](http://gluonhq.com/) [Ignite](http://gluonhq.com/labs/ignite/).

The code was one rather big project, that I will try to slice into smaller pieces. For example to decouple the model-classes.
I wasn't really familiar with MVC back then, that's why I let my model classes have little babies with JavaFx Properties.
That is making it hard to decouple stuff I guess but it let me just bind my models to the gui :P

I plan to integrate some api that provides some interface so that you can program commands in Java.
It's a bit tricky to script in the plain text area of JFX.

The Telegram Bot Configurator loads all jar files in the "[execution location]/tbc/plugins" directory.
You might use some other libraries if you put the jars in that directory.#

The application is in german (UI Components). But feel free to translate it in your language if you need it (telegram-bot-configurator-application/src/main/resources/fxml/bundles).
If anybody really wants to use this application I can translate it in english.

####Screenshots

comming soon...