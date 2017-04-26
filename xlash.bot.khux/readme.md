# KHUx Info Bot

This is a bot I created to make it easier to retrieve medal info, along with auto posting updates from Twitter if you so choose. This was made using the libraries [Javacord](https://github.com/BtoBastian/Javacord/) by BtoBastian and [JSoup](https://jsoup.org/).

## How to use

There are a few commands you can use to help to prepare yourself. They are as listed:
<br>
To get the details of any medal currently in the NA version of the game, use the !medal command:
```cmd
*!medal [medal name/nickname]*
```
Note that you can either use the medal's full name or nicknames based on the following conventions (capitals are completely ignored):
<br>
+ "Person & Person" will be shorted to their initals separated by &. Ex: Xehanort & Vanitas = X&V.
+ "Illustrated" will be replaced by 'i'. Ex: Illustrated Ansem = iAnsem, Illustrated Belle & Beast = iB&B.
+ "Halloween" will be replaced with "HT". Ex: Halloween Sora = HT Sora.
+ "Atlantica" will be replaced with "AT". Ex: Atlantica Sora = AT Sora.
+ "KH II" will be replaced with "KH2". Ex: KH II Yuffie = KH2 Yuffie.
+ "Timeless River" will be replaced with "TR". Ex: Timeless River Pete = TR Pete.
+ "Key Art" will be replaced with "KA". Ex: Key Art 2 = KA2.
+ "WORLD OF FF" will be replaced with "WOFF". Ex: WORLD OF FF Sephiroth = WOFF Sephiroth.
+ Any medal with more than 3 persons mention will be reduced to each person's initial. Ex: Sora & Donald & Goofy A = SDG A.
+ "The" is dropped. Ex: The King & Donald & Goofy = KDG.
<br>
Some commonly used names, such as Tieri and Pooglet, are also valid. If a common nickname is not present, let me know.
<br>
To automatically grab the latest updates from Twitter, go to the channel of your choice and do:
```cmd
*!tweet [on/off]*
```
*!tweet get* can also be used to grab the latest Tweet.

To automatically remind party members of double lux times during raid events, go to your desired channel and do the following:
```cmd
!lux [on/off]
```
This will send both a reminder that double lux has started and one for when it has ended. It will work regardless of time zones, set at 3-4am/pm and 9-10am/pm GMT/UTC.

## How to install

Download the [latest build](https://github.com/xlash123/KHUx-Discord-Bot/releases/) or build from the source (using Maven). Then either double click on the file (only for Windows) or run the following in terminal/cmd.
```cmd
java -jar "[filepath]" run
```
A good explaination of how to get the Discord App ID is explained [here](https://github.com/BtoBastian/Javacord/blob/master/README.md) in the Javacord README.
<br>
If you're still lost, here's a helpful [Reddit thread](https://www.reddit.com/r/KingdomHearts/comments/65puab/khux_easy_guide_to_run_xlash123s_khux_bot_in_your/), thanks to /u/MaxialstarOA, although it is now slightly outdated.

## Questions?

Any bugs, suggestions, or questions can be posted as an issue. I'll respond to anything I get.