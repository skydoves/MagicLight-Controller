# MagicLight Controller
I hacked MagicLight bluetooth bulb's communication protocals through reverse engineering.<br>
so anyone can control MagicLight bluetooth bulbs using this simple demo app or your own app.

![screenshot1972702450](https://user-images.githubusercontent.com/24237865/27760414-a01409b0-5e81-11e7-9685-64a41c6629cb.png) 
![screenshot_2017-07-01-17-23-02](https://user-images.githubusercontent.com/24237865/27760413-a0062642-5e81-11e7-9003-e6fbc3022757.png)

## Simulation on Youtube
<a href="https://youtu.be/1pZjqVqJMqI" target="_blank">![alt tag](https://user-images.githubusercontent.com/24237865/27760540-1aaedb16-5e85-11e7-99e9-9584c8907b91.png)</a>

## Protocols analysis
**LED service UUID**<br>
_0000ffe5-0000-1000-8000-00805f9b34fb_

**LED characteristic UUID**<br>
_0000ffe9-0000-1000-8000-00805f9b34fb_

**Color control Attribute**<br>
_56 RR GG BB 00 f0 aa_

**Brightness control Attribute**<br>
_56 00 00 00 LL 0f aa_


## What you can do through this app
This simple demo app supplys some examples about basic controls through smart bulb's protocols.</br>
And you can customize your own apps using following protocols.

### Connecting with bluetooth devices
![kakaotalk_20170702_211939839](https://user-images.githubusercontent.com/24237865/27769791-e15cb476-5f6c-11e7-8dca-9b53650b7efa.jpg)

### Selecting colors from your gallery images
![kakaotalk_20170702_211940454](https://user-images.githubusercontent.com/24237865/27769790-e14c392a-5f6c-11e7-8bfb-255abec87220.jpg)
![kakaotalk_20170702_214838377](https://user-images.githubusercontent.com/24237865/27769921-47e75d06-5f70-11e7-9265-060b4a56541b.jpg)

### Music-Reactive bulbs color changing

## Customizing
You can control smart bulbs through your customized apps. <br>
following youtube simulation video shows smart bulbs are controlling by my jarvis system and reacting at music. <br><br>
<a href="https://youtu.be/GurM3x1nq6Q" target="_blank">![alt tag](https://user-images.githubusercontent.com/24237865/27769974-8f777c9a-5f71-11e7-8ecd-de6dd54aec37.png)</a>

# License
```xml
Copyright 2017 skydoves

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
