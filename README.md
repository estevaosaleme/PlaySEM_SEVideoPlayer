# Intro
**SE Video Player** is a multi-platform tool for playing back videos annotated with sensory effects metadata (MPEG-V format). If the video does not have an associated SEM or [PlaySEM SE Renderer](https://github.com/estevaosaleme/PlaySEM_SERenderer) is not available, SE Video Player behaves like a traditional video player. Together with [PlaySEM SE Renderer](https://github.com/estevaosaleme/PlaySEM_SERenderer), it is capable of simulating and rendering sensory effects.

**Related paper**: 
* SALEME, E. B.; SANTOS, C. A. S., [PlaySEM: a Platform for Rendering MulSeMedia Compatible with MPEG-V](http://dx.doi.org/10.1145/2820426.2820450), WebMedia '15.

**PlaySEM SE Video Player**
![PlaySEM SE Video Player 1.0](https://github.com/estevaosaleme/PlaySEM_SEVideoPlayer/blob/master/docs/PlaySEM_SEVideoPlayer.png)<br />
[Click here](https://github.com/estevaosaleme/PlaySEM_SEVideoPlayer/tree/master/docs) to see other screenshots.

# Pre-requisites:
* Java 1.7 or greater (http://www.java.com/en/download)
* VLC Media Player 2.1.3 or greater (http://www.videolan.org/vlc/)

# Running: (Pay attention to the config.properties file)
* [Download](https://github.com/estevaosaleme/PlaySEM_SEVideoPlayer/releases) the lastest release
* Uncompress the file PlaySEM_SEVideoPlayer_< version >.zip
* Run the command `java -jar PlaySEM_SEVideoPlayer.jar` (Please, check if you need to update the name of the jar file)

# Configuration (config.properties):<br />
`### PlaySEM SE Video Player - Settings`<br />
`# VLC directory.`<br />
`vlc_path=C\:\\Program Files\\VideoLAN\\VLC`<br />
`# LINUX`<br />
`# vlc_path=/usr/bin/vlc`<br />
`# Interface theme (optional NimROD Look & Feel).`<br />
`theme_path=my.theme`<br />
***
Please, report any application crash (or bug) via [issue tracker](https://github.com/estevaosaleme/PlaySEM_SEVideoPlayer/issues).<br />
Visit the **PlaySEM SE Renderer** repository at https://github.com/estevaosaleme/PlaySEM_SERenderer
