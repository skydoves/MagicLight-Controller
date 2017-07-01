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
