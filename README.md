# KHUx Info Bot

This is a bot I created to make it easier to retrieve medal info, remind your server about lux or Union Cross times, and posting updates from Twitter for either the NA or JP version of KHUx. This was made using the libraries [Javacord](https://github.com/BtoBastian/Javacord/) by BtoBastian, [JSoup](https://jsoup.org/), [Emoji-Java](https://github.com/vdurmont/emoji-java) by vdurmont, and [Gson](https://github.com/google/gson) by Google, with extreme thanks to Rosie for her amazing website [khuxtracker.com](http://khuxtracker.com/) which I use for the searching algorithm and database.

## My Host
[Click here](https://discordapp.com/oauth2/authorize?client_id=300117950457708560&scope=bot&permissions=0) to have my hosted instance of the bot join your server (You must be a server admin to do such). Note that any channels that the bot will post in will need **permission both to post plain text and embedded message**.

## How to use

There are a few commands you can use to help to prepare yourself. They are as listed:
### !medal
To get the details of any medal in your default game, use the !medal command:

`!medal [medal name/nickname]`

If you don't know a medal's full name, type a portion of its name and a list of suggestions will appear like such:

![alt text](https://i.imgur.com/LRt9Dg1.png)

From here, just click or tap on the number that corresponds with the medal you want, and it will display its stats as such:

![alt text](https://i.imgur.com/7W6Y8ur.png)

### !default
To change the default game that !medal pulls from, use:

`!default [NA/JP]`

You can also use !medalna or !medaljp if you don't want to change your default game. Note that !default will also change the lux times for the !lux command.

### !tweet
To automatically grab the latest updates from Twitter, go to the channel of your choice and do:

`!tweet [on/off/get/status]`

*!tweet get* can also be used to grab the latest Tweet.

### !lux
To automatically remind party members of double lux times during raid events, go to your desired channel and do the following:

`!lux [on/off/status/check] (na/jp)`

This will send both a notice that double lux has started and one for when it has ended. It will work regardless of time zones, set at 3-4am/pm and 9-10am/pm GMT/UTC. The reminder message is editable in the config. Running check will tell you how long until the next bonus time.

You can also have the bot remind you up to 30 minutes before double lux has started. To do that, use

`!lux remind [minutes]`

### !uc
Similar to `!lux`, this command will allow you to remind the server of when the Union Cross bonus times are active.

`!ux [on/off/status/check] (na/jp)`

Running check will tell you how long until the next bonus time.

You can also use this to set a reminder up to 30 minutes before:

`!ux remind [minutes]`

### !admin
Some commands are administrative, so use this command to set admins for your server to keep everything under control:

`!admin @[user] @[another user] ...`

Note: If no one has yet been registered as an admin, any server administrator will be able to register admins with this command. This only grants admins to using the bot, and does not affect server administration.

### !unadmin
Use this to remove admin privilages from the troublmakers:

`!unadmin @[user] @[another user] ...`

### !salt
Let's face it. This game is pretty salty. Simply run `!salt` to let all servers know that you're mad! Running it will simply increase a global counter by one.

### !config
To reload the config data, use !config. This is helpful for loading the lux reminder message when edited.

### !keyblade
Use this to view the stats of any Keyblade at any level! It'll display all the slots available for that Keyblade, including its attribute, type, and each multiplier.

`!keyblade [keyblade name] (level)`

Omiting level will simply display the max level stats.

### !help
You can also view all of the commands and their usage by running !help.

## How to install
If you choose not to let [my host](https://discordapp.com/oauth2/authorize?client_id=300117950457708560&scope=bot&permissions=0) join your server, you can run your own instance by doing the following:

Download the [latest build](https://github.com/xlash123/KHUx-Discord-Bot/releases/) or build from the source (using Maven). Then either double click on the file (only for Windows) or run the following in terminal/cmd.
`java -jar "[filepath]" run`
A good explaination of how to get the Discord App ID is explained [here](https://github.com/BtoBastian/Javacord/blob/master/README.md) in the Javacord README.
<br>
If you're still lost, here's a helpful [Reddit thread](https://www.reddit.com/r/KingdomHearts/comments/65puab/khux_easy_guide_to_run_xlash123s_khux_bot_in_your/), thanks to /u/MaxialstarOA, although it is now slightly outdated.

## Questions?

Any bugs, suggestions, or questions can be posted as an issue. I'll respond to anything I get.
