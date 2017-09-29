# Telegram Bot Configurator

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

## Screenshots

### Commands:
A simple command that just sends "Pong" back to the sender

![Pong command](https://user-images.githubusercontent.com/24529735/31031888-5100bee8-a55a-11e7-99c9-3dc1fedf727a.png)

A command that uses predefined arguments with predefined attributes
"Fridge" has the a property "code" with the value "00001" that you can access with the "arguments" array 
Access the given argument with the "userInputs" array.
You may use placeholders like %PHOTO% or %LOCATION% in the arguments part to allow these data types.

![Activate command](https://user-images.githubusercontent.com/24529735/31031889-5104466c-a55a-11e7-95a3-85db600361a8.png)

![Activate command arguments](https://user-images.githubusercontent.com/24529735/31031890-5105328e-a55a-11e7-9367-c0d9d651bced.png)

### Roles:
Configure roles with permissions. In this case I configured "Peasant" and "Admin"
Peasants may use the command "ping" as they wish.
Admin inherits every right of a peasant and has the right to activate everything.
NOTE: When configuring a command TBC automatically create a permission for that command (without arument restrictions)

![Roles](https://user-images.githubusercontent.com/24529735/31031891-5107353e-a55a-11e7-9052-16060208d2ff.png)

![Roles Admin](https://user-images.githubusercontent.com/24529735/31031892-5110cafe-a55a-11e7-8911-e60ab932056e.png)

### Permissions
You may restrict command arguments using permissions.
For example I need a permission that restricts a user to only use the activate command with "Fridge" as the argument.
I then assign that permission to the peasant role.
You may use placeholders like %PHOTO% or %LOCATION% in here too.

![Permissions](https://user-images.githubusercontent.com/24529735/31031887-50ff17e6-a55a-11e7-8901-6897a13738f5.png)

![Permissions Peasant](https://user-images.githubusercontent.com/24529735/31031881-50e5b968-a55a-11e7-96db-3d13acbc0b48.png)

### Users
You can add users in the users tab by giving the chat id and a custom name.
You activate them and deactivate them.
You can assign roles and single permissions to a user.
A user can have own key-value properties that you can access in the command script.
In the screenshot there is Timo who is a peasant (obviously). 
There is the admin Greg and Yo mama who has "special" permissions.

![Timo](https://user-images.githubusercontent.com/24529735/31031884-50ef14f4-a55a-11e7-9a79-7ff03e8174aa.png)

![Greg](https://user-images.githubusercontent.com/24529735/31031882-50ea2b38-a55a-11e7-8516-90b69ddaa2cf.png)

![I am not funny](https://user-images.githubusercontent.com/24529735/31031883-50eb233a-a55a-11e7-9ccd-1108dd7570e7.png)

### Configuration
You must put your [Bot token](https://core.telegram.org/bots/api#authorizing-your-bot) in the text box here.
If you want to add a user when he contacts your bot, you may activate "Benutzer automatisch einpflegen" (add user automatically).
You can assign automatically added users a default group by choosing one in the combobox.
If you want your auto added users to be activated when added or not just check the box accordingly ("Automatisch Benutzer aktiviert")
If you add your users automatically, make sure you checked "Automatisch speichern". 
This option saves the configuration before shutting down and every once in a while when running. It is useful if you run the application in no gui mode.

![Configuration](https://user-images.githubusercontent.com/24529735/31031885-50efccfa-a55a-11e7-911a-423f833bb521.png)

### Running
You can start your bot in the gui by switching to the "Starten" tab and just clicking the button on the top left.
It shows you how long your bot is running and how many commands where executed.
Below is a list of all running commands with a progress bar that you can access in the command (empty in screenshot).
If you want to run it without a GUI you need to pass the jar following arguments: _-n --file "path/to/conf.tbc"_.
If running in no gui mode you have to pass it a file. 

![Run in GUI](https://user-images.githubusercontent.com/24529735/31031886-50fbdd60-a55a-11e7-85b4-5e1349c64e0f.png)