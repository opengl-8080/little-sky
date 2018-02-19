# LittleSky
This is a clock application in Java.  

The original idea is made by [zk-phi](https://github.com/zk-phi).

- [zk-phi/sky-color-clock: [Emacs] A clock widget for modelines with real-time sky color and moonphase/weather icon](https://github.com/zk-phi/sky-color-clock)
- [Emacs で時の流れを感じる - Qiita](https://qiita.com/zk_phi/items/11a419911db861b9211e)

![img](https://camo.qiitausercontent.com/a04f1b32466cf0813c018820a44d38ef9b62d662/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e616d617a6f6e6177732e636f6d2f302f32383330322f34646433316363322d316332612d323731632d373265302d6136393161626131303265662e676966)

# Installation
You can get a jar file from [release pages](https://github.com/opengl-8080/little-sky/releases).

# Environment
## OS
- Windows
    - I confirmed to run in Windows 10.
- Mac
    - I can't confirm in Mac because I don't have it.
    - If this application can't run in Mac, please pull request.

## JRE
Java 8u40 or later.

# Starting
```bash
$ java -jar littlesky.jar
```

This application save configurations to a file (`littkesky.xml`).  
The file is saved at the current directory.

If you move the jar file, you must move the `littlesky.xml` too.

# Setup at the first time
![setup](https://camo.qiitausercontent.com/3d710bd50a9845b942ec98c8b6772affd5750fa1/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e616d617a6f6e6177732e636f6d2f302f32383330322f38653065623037342d356130612d363337612d346538302d3064346436656338363934302e6a706567)

At the first time, you must set your location.

The location setting is used at calculating sunset/surise times and requesting weather informations.

# How to use
![main-window](https://camo.qiitausercontent.com/91ec451eb6b3b1a04de738f3cfbb2764d50fd983/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e616d617a6f6e6177732e636f6d2f302f32383330322f65663265666335642d646237312d666563372d613431662d6262303730343534623163632e6a706567)

The main window has three elements.

1. Sky Icon
    - This element displays the moon phase if weather is sunny.
    - Or else, rainy or snowy icon is shown.
1. Time
    - Current time.
1. Temperature
    - Current temperature.
    - Temperature is displayed as color.
    - blue (colder), white (normal), red (hotter)

## Context menu
![context-menu](https://camo.qiitausercontent.com/d28b82f2dcb44d00434a4ee080d176f4ec081dc8/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e616d617a6f6e6177732e636f6d2f302f32383330322f35363534346264642d633737662d383637382d616166622d6339633364343461666561312e6a706567)

The context menu is shown if you click the time label on mouse right button.

## Weather service
This application uses the [OpenWeatherMap](https://openweathermap.org/) to get weather informations.

### Get API Key
Please get API Key of the OpenWeatherMap.

You need to sign up the OpenWeatherMap to get API Key.

### Set API Key
![api-key](https://camo.qiitausercontent.com/7a3e78ab3f26a77fcc28b84404e330596b344f6c/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e616d617a6f6e6177732e636f6d2f302f32383330322f65343233643633302d323866382d313263372d313836662d3936616135346665323231662e6a706567)

Select `Options` at the context menu, setting dialog is shown.

Please set the API Key, and save it.

### Enable weather service
![weather-service](https://camo.qiitausercontent.com/93588d22ef6b91966c4a021464373e6c36f9eee4/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e616d617a6f6e6177732e636f6d2f302f32383330322f31626331326533662d633539362d643333322d343738642d3564646663366666323030622e6a706567)

Select [Weather service] > [Start] at the context menu.  
Weather service is started.

At next start time, weather service is started automatically, if API Key is set.

If you want to stop weather service, select [Weather service] > [Stop] on the context menu.

## HTTP Proxy
![proxy](https://camo.qiitausercontent.com/fd2231f46857d2470c34bca914e5a2eaa0f1618c/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e616d617a6f6e6177732e636f6d2f302f32383330322f31306636633938312d393937642d313334352d386135632d6135386435346330313535352e6a706567)

You can set settings of http proxy at Options dialog.

If you don't set `port`, `80` is used at default.

If the http proxy requires authentications, you must set `User` and `Password`

Note: Password is saved in plain text at the configuration file(`littlesky.xml`).

## View
![view](https://camo.qiitausercontent.com/0e360060eca3b43dc08102d8e935b145cd1e8be7/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e616d617a6f6e6177732e636f6d2f302f32383330322f37386266363966382d616236642d333932632d316666642d6631336562326339653864622e6a706567)

Select [View] at the context menu.

### Always on top
If you check this menu, window is always shown on most top of the desktop.

Default is false.

### Show seconds
You can show or hide the seconds at time label.

Default is true.

### Show temperature
You can show or hide the temperature element.

Default is true.

### Show sky status icon
You can show or hide the sky status icon.

Default is true.

# OSS
This application uses some OSS.

## [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
- [mikereedell/sunrisesunsetlib-java: Library for computing the sunrise/sunset from GPS coordinates and a date, in Java.](https://github.com/mikereedell/sunrisesunsetlib-java)
- [FasterXML/jackson-databind: General data-binding package for Jackson (2.x): works on streaming API (core) implementation(s)](https://github.com/FasterXML/jackson-databind)
