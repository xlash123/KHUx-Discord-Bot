# KHUx Info Bot

This is a bot I created to make it easier to retrieve medal info, along with auto posting updates from Twitter if you so choose. This was made using the libraries [Javacord](https://github.com/BtoBastian/Javacord/) by BtoBastian and [JSoup](https://jsoup.org/).

## How to use

Right now, there is only one command, but I plan to add more features in the future, so expect more.
<br>
To get the details of any medal currently in the NA version of the game, use the !medal command:

*!medal [medal name/nickname]*
<br>
Note that you can either use the medal's full name or nicknames based on the following conventions:
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

## How to install

Download the [latest build](https://github.com/xlash123/KHUx-Discord-Bot/releases/) or build from the source (using Maven). Then run the jar with the following command line:
```cmd
java -jar "[filepath]" [Discord App ID] (optional: channel ID for auto Twitter updates )
```
A good explaination of how to get the Discord App ID is explained [here](https://github.com/BtoBastian/Javacord/blob/master/README.md) in the Javacord README.

## Questions?

Any bugs, suggestions, or questions can be posted as an issue. I'll respond to anything I get.